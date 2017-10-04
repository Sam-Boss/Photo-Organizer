package application;

import java.io.File;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Photo implements Serializable {

	private String caption;
	private String filePath;
	private Date date;
	private HashMap<String, List<String>> tags;
	
	public Photo(String filePath) {
		tags = new HashMap<String, List<String>>();
		this.filePath = filePath;
		setDate();
	}
	
	/**
	 * Returns the caption associated with this photo
	 * @return
	 */
	public String getCaption() {
		return caption;
	}
	
	/**
	 * Sets the caption for this photo
	 * @param caption
	 */
	public void setCaption(String caption) {
		this.caption = caption;
	}
	
	/**
	 * Returns file path that contains the photo location
	 * @return
	 */
	public String getFilePath() {
		return filePath;
	}
	
	/**
	 * Sets the file path that contains the photo location
	 * @param filePath
	 */
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	/**
	 * Returns photo's last modified date
	 * @return
	 */
	public Date getDate() {
		return date;
	}
	
	/**
	 * Sets the date for this photo
	 * @param date
	 */
	public void setDate() {
		//this.date = date;
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy");
		File file = new File(this.filePath);
		String time = sdf.format(file.lastModified());
		try {
			this.date = sdf.parse(time);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns the hashmap of tags
	 * @return tag map
	 */
	public HashMap<String, List<String>> getTags() {
		return tags;
	}
	
	/**
	 * Assigns the tags map to this photo
	 * @param tags
	 */
	public void setTags(HashMap<String, List<String>> tags) {
		this.tags = tags;
	}
		
	/**
	 * Adds the key value pair for the tag to this photo
	 * @param key - identifying tag
	 * @param value - value of the tag
	 */
	public void addTag(String key, String value) {
		List<String> values = this.tags.get(key);
		values.add(value);
		this.tags.put(key, values);
	}
	
}
