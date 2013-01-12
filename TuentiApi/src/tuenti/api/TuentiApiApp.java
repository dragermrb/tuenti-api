/**
 * 
 */
package tuenti.api;

import java.io.FileInputStream;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Scanner;

import tuenti.api.model.Album;
import tuenti.api.model.Photo;

/**
 * TuentiApi test app
 * @author Manuel Rodríguez
 *
 */
public class TuentiApiApp {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		
		//Try to read application_key from config file. application_key is not public, try google to find one.
		Properties c = new Properties();
		FileInputStream is;
		try {
			is = new FileInputStream("config/config.properties");
			c.load(is);
			is.close();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		String app_id=c.getProperty("application_key");
		String email=c.getProperty("email");
		String pass=c.getProperty("pass");
		
		if (app_id==null){
			System.out.println("You need an application_key to access tuenti api. Application_key is not public, try google to find one.");
			System.out.println("Application_key: ");
			app_id = in.nextLine();
		}
		
		if (email==null){
			System.out.println("Email: ");
			email = in.nextLine();
		}
		
		if (pass==null){
			System.out.println("Pass: ");
			pass = in.nextLine();
		}
		in.close();
		
		TuentiApi tuenti=new TuentiApi();
		tuenti.setApplication_key(app_id);
		
		System.out.println("### Trying to login...");
		if (tuenti.login(email, pass)){
			System.out.println("### Logged in...");
		}
		else{
			System.out.println("### Login failed");
			return;
		}
		
		try {
			System.out.println("### Getting user albums...");
			Map<String,Album> userAlbums=tuenti.getUserAlbums();
			
			//Foreach albums...
			Iterator<Entry<String, Album>> it_uas = userAlbums.entrySet().iterator();
		    while (it_uas.hasNext()) {
		    	Map.Entry<String, Album> entryAlbum = it_uas.next();
		        Album userAlbum = (Album)entryAlbum.getValue();
		        
		        System.out.println("- AlbumID: "+userAlbum.getId());
		        
		        System.out.println("### Getting album photos...");
		        List<Photo> albumPhotos=tuenti.getAlbumPhotos(userAlbum.getId());
		        
		        //Foreach photo...
		        ListIterator<Photo> it_aps = albumPhotos.listIterator();
		        while (it_aps.hasNext()) {
		          Photo photo = it_aps.next();
		          
		          System.out.println("  - Photo "+it_aps.previousIndex());
		          System.out.println(photo);
		        }
		    }
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
