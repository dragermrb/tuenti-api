
package tuenti.api;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import tuenti.api.model.Album;
import tuenti.api.model.Photo;


/**
 * TuentiApi class
 * 
 * Set the application_key hash before use it.
 * 
 * @author Manuel Rodríguez
 */
public class TuentiApi {
	private String hostname = "api.tuenti.com";
	private String pathname = "/api/";
	private String application_key;

	private String email = null;
	private String password = null;
	
	private String session_id = null;
	private String user_id = null;

	public TuentiApi() {

	}
	
	public String getApplication_key() {
		return application_key;
	}

	public void setApplication_key(String application_key) {
		this.application_key = application_key;
	}
	
	
	/**
	 * Login to tuenti
	 * 
	 * Login to tuenti using email and password a saves session_id for use in later request.
	 * 
	 * @param email User email
	 * @param password User plain password
	 * @return True if logged in, false instead.
	 */
	public boolean login(String email, String password){
		this.email = email;
		this.password = password;
		
		String address = "http://" + this.hostname + this.pathname;
		
		// getChallenge
		String urlParameters = "{ " +
				"\"requests\": [ [ \"getChallenge\", {\"type\":\"login\"} ] ], " +
				"\"version\": \"0.4\" " +
				"}";
		String response_challenge = httpRequest(address, urlParameters);
		
		JSONArray challenge_arr = (JSONArray) JSONValue
				.parse(response_challenge);
		JSONObject challenge_obj = (JSONObject) challenge_arr.get(0);

		if (challenge_obj.get("challenge").equals("")) {
			//Challenge not found 
			return false;
		}

		// getSession
		String session_data;
		try {
			session_data = "{ \"requests\": [ [ \"getSession\", {"
					+ "\"passcode\": \""
					+ md5(challenge_obj.get("challenge") + md5(this.password))
					+ "\"," + "\"application_key\": \"" + this.application_key
					+ "\"," + "\"timestamp\": \"" + challenge_obj.get("timestamp")
					+ "\"," + "\"seed\": \"" + challenge_obj.get("seed") + "\","
					+ "\"email\": \"" + this.email + "\""
					+ "} ] ], \"version\": \"0.4\" }";
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		String response_session = httpRequest(address, session_data);

		JSONArray session_arr = (JSONArray) JSONValue.parse(response_session);
		JSONObject session_obj = (JSONObject) session_arr.get(0);

		if (session_obj.containsKey("error")){
			//error returned
			System.out.println("Error: "+session_obj.get("error")+" => "+session_obj.get("message"));
			return false;
		}
		else if(session_obj.get("session_id").equals("") || session_obj.get("user_id").equals("")){
			//session_id or user_id not found
			return false;
		}
		else{
			this.session_id = (String) session_obj.get("session_id");
			this.user_id = Long.toString((Long)session_obj.get("user_id"));
			
			return true;
		}
	}

	/**
	 * Perform a request to tuenti
	 * 
	 * Perform a request to tuenti, parse the JSON response and return a JSONArray 
	 * 
	 * @param method Method to request
	 * @param data JSON encoded data if necessary
	 * @return Parsed JSON response
	 */
	private JSONArray request(String method, String data){
		if (this.session_id==null){
			JSONArray a=new JSONArray();
			JSONObject o=new JSONObject();
			o.put("error","Not logged in");
			a.add(o);
			return a;
		}
	    
	    try {
	    	String address = "http://" + this.hostname + this.pathname;
			String urlParameters = "{ " 
					+ "\"requests\": [[ \"" + method + "\"" + (data.equals("") ? "" : ", " + data) + " ] ], "
					+ "\"version\": \"0.4\", " + "\"session_id\":\""
					+ this.session_id + "\"" 
				+ "}";
	    	
	    	String response=httpRequest(address, urlParameters);

			JSONArray arr=(JSONArray)JSONValue.parse(response);
			
			return arr;

	    } catch (Exception e) {
			e.printStackTrace();
		}
		
		return new JSONArray();
	}

	/**
	 * Get the logged user albums
	 * 
	 * @return Map of user albums indexed by album_id
	 * @throws Exception
	 */
	public Map<String,Album> getUserAlbums() throws Exception{
		if (this.session_id==null){
			throw new Exception("Not logged in");
		}
		
		String method="getUserAlbums";
		String data="";
		
		JSONArray a=request(method,data);
		JSONObject userAlbums=(JSONObject)a.get(0);
		
		Map<String,Album> map=new HashMap<String,Album>();
		Iterator it = userAlbums.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry userAlbum = (Map.Entry)it.next();
	        
	        JSONObject album_fields=(JSONObject)userAlbum.getValue();
	        JSONObject thumbnail_fields=(JSONObject)album_fields.get("thumbnail");
	        
	        Album album=new Album();
	        album.setId((String)userAlbum.getKey());
			album.setName((String)album_fields.get("name"));
			album.setSize(Integer.parseInt((String)album_fields.get("size")));
			album.setLast_chage_time(Integer.parseInt((String)album_fields.get("last_chage_time")));

			Map<Integer,String> thumbnail=new HashMap<Integer,String>();
			thumbnail.put(30,(String)thumbnail_fields.get("30"));
			thumbnail.put(100,(String)thumbnail_fields.get("100"));
			thumbnail.put(200,(String)thumbnail_fields.get("200"));
			album.setThumbnail(thumbnail);
			
			map.put(album.getId(), album);
	    }
		
		return map;
	}
	
	/**
	 * Get photos from album
	 * 
	 * @param album_id Id of desired album
	 * @return List of photos
	 * @throws Exception
	 */
	public List<Photo> getAlbumPhotos(String album_id) throws Exception{
		if (this.session_id==null){
			throw new Exception("Not logged in");
		}
		
		String method="getAlbumPhotos";
		String data="{\"album_id\": \""+album_id+"\"}";
		
		JSONArray a=request(method,data);
		JSONObject o=(JSONObject)a.get(0);
		JSONArray albumPhoto_list=(JSONArray)o.get("album");
		
		ArrayList<Photo> list=new ArrayList<Photo>();

        ListIterator it = albumPhoto_list.listIterator();
        while (it.hasNext()) {
          JSONObject p = (JSONObject)it.next();
          
          Photo photo=new Photo();
          photo.setId((String)p.get("id"));
          photo.setTitle((String)p.get("title"));
          photo.setPhoto_url_100((String)p.get("photo_url_100"));
          photo.setPhoto_url_200((String)p.get("photo_url_200"));
          photo.setPhoto_url_600((String)p.get("photo_url_600"));
          photo.setCan_edit_title(Boolean.parseBoolean((String) p.get("can_edit_title")));
          photo.setCan_tag(Boolean.parseBoolean((String) p.get("can_tag")));
          photo.setCan_see_profile(Boolean.parseBoolean((String) p.get("can_see_profile")));
          photo.setCan_see_wall(Boolean.parseBoolean((String) p.get("can_see_wall")));
          photo.setCan_dowload(Boolean.parseBoolean((String) p.get("can_dowload")));
          
          list.add(photo);
        }
        
        return list;
	}

	
	/**
	 * Calculates the MD5 hash of a string
	 * @param stringToHash String to calculate hash
	 * @return MD5 hash of string
	 * @throws Exception
	 */
 	private String md5(String stringToHash) throws Exception {
		char[] HEXADECIMAL = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
				'9', 'a', 'b', 'c', 'd', 'e', 'f' };
		StringBuilder sb = null;

		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] bytes = md.digest(stringToHash.getBytes());
			sb = new StringBuilder(2 * bytes.length);
			for (int i = 0; i < bytes.length; i++) {
				int low = (int) (bytes[i] & 0x0f);
				int high = (int) ((bytes[i] & 0xf0) >> 4);
				sb.append(HEXADECIMAL[high]);
				sb.append(HEXADECIMAL[low]);
			}

		} catch (NoSuchAlgorithmException e) {
			throw new Exception("Error calculating MD5. " + e.getMessage());
		}

		return sb.toString();
	}
	
	/**
	 * Perform an http request and return response as string
	 * 
	 * Perform an http request with POST data and return response as string
	 * 
	 * @param address Address to request
	 * @param urlParameters Parameters
	 * @return
	 */
	private String httpRequest(String address, String urlParameters){
		URL url;
	    HttpURLConnection connection = null;
	    
	    try {
	    	url = new URL(address);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");

			connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));

			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);

			// Send request
			DataOutputStream wr = new DataOutputStream(
					connection.getOutputStream());
			wr.writeBytes (urlParameters);
			wr.flush();
			wr.close();
			connection.disconnect();

			// Get Response
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer response = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
			
			System.out.println("Request: "+urlParameters); //TODO: debug
			System.out.println("Response: "+response); //TODO: debug
			
			return response.toString();

	    } catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
