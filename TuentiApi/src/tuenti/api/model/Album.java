/**
 * 
 */
package tuenti.api.model;

import java.util.Date;
import java.util.Map;

/**
 * Album class model
 * 
 * @author Manuel Rodríguez
 */
public class Album {
	private String id;
	private String name;
	private int size;
	private Date last_change_time;
	private Map<Integer,String> thumbnail;
	
	public Album() {

	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public Date getLast_change_time() {
		return last_change_time;
	}
	public void setLast_change_time(Date last_change_time) {
		this.last_change_time = last_change_time;
	}
	public Map<Integer, String> getThumbnail() {
		return thumbnail;
	}
	public void setThumbnail(Map<Integer, String> thumbnail) {
		this.thumbnail = thumbnail;
	}
	
	@Override
	public String toString() {
		return "Album [id=" + id + ", name=" + name + ", size=" + size
				+ ", last_change_time=" + last_change_time + ", thumbnail="
				+ thumbnail + "]";
	}
}
