package life.haq.it.android.instalikeprofile.data;

public class FeedItem {
	private int id;
	private String name, status, image, profilePic, timeStamp, new_slides, url, key;

	public FeedItem() {}

	public FeedItem(int id, String name, String image, String status,
			String profilePic, String profilePic2, String profilePic3, String timeStamp, String new_slides, String url, String key) {
		super();
		this.id = id;
		this.new_slides = new_slides;
		this.name = name;
		this.image = image;
		this.status = status;
		this.profilePic = profilePic;
		this.profilePic = profilePic2;
		this.profilePic = profilePic3;
		this.timeStamp = timeStamp;
		this.url = url;
		this.key = key;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImge() {
		return image;
	}

	public void setImge(String image) {
		this.image = image;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getProfilePic() {
		return profilePic;
	}

	public void setProfilePic(String profilePic) {
		this.profilePic = profilePic;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getNewSlides() {
		return new_slides;
	}

	public void setNewSlides(String new_slides) {
		this.new_slides = new_slides;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
