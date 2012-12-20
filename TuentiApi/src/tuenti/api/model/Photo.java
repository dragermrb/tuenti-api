/**
 * 
 */
package tuenti.api.model;

/**
 * Photo class model
 * 
 * @author Manuel Rodríguez
 */
public class Photo {
	
	private String id;
	private String title;
	private String photo_url_100;
	private String photo_url_200;
	private String photo_url_600;
	private boolean can_edit_title;
	private boolean can_tag;
	private boolean can_see_profile;
	private boolean can_see_wall;
	private boolean can_dowload;
	
	public Photo() {
		
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPhoto_url_100() {
		return photo_url_100;
	}
	public void setPhoto_url_100(String photo_url_100) {
		this.photo_url_100 = photo_url_100;
	}
	public String getPhoto_url_200() {
		return photo_url_200;
	}
	public void setPhoto_url_200(String photo_url_200) {
		this.photo_url_200 = photo_url_200;
	}
	public String getPhoto_url_600() {
		return photo_url_600;
	}
	public void setPhoto_url_600(String photo_url_600) {
		this.photo_url_600 = photo_url_600;
	}
	public boolean isCan_edit_title() {
		return can_edit_title;
	}
	public void setCan_edit_title(boolean can_edit_title) {
		this.can_edit_title = can_edit_title;
	}
	public boolean isCan_tag() {
		return can_tag;
	}
	public void setCan_tag(boolean can_tag) {
		this.can_tag = can_tag;
	}
	public boolean isCan_see_profile() {
		return can_see_profile;
	}
	public void setCan_see_profile(boolean can_see_profile) {
		this.can_see_profile = can_see_profile;
	}
	public boolean isCan_see_wall() {
		return can_see_wall;
	}
	public void setCan_see_wall(boolean can_see_wall) {
		this.can_see_wall = can_see_wall;
	}
	public boolean isCan_dowload() {
		return can_dowload;
	}
	public void setCan_dowload(boolean can_dowload) {
		this.can_dowload = can_dowload;
	}

	@Override
	public String toString() {
		return "Photo [id=" + id + ", title=" + title + ", photo_url_100="
				+ photo_url_100 + ", photo_url_200=" + photo_url_200
				+ ", photo_url_600=" + photo_url_600 + ", can_edit_title="
				+ can_edit_title + ", can_tag=" + can_tag
				+ ", can_see_profile=" + can_see_profile + ", can_see_wall="
				+ can_see_wall + ", can_dowload=" + can_dowload + "]";
	}
}
