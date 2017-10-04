package application;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Album implements Serializable {

	private String albumName;
	private Date earlyPhotoDate;
	private Date latePhotoDate;
	private ArrayList<Photo> photos;
	
	/**
	 * Default constructor
	 * @param name
	 */
	public Album(String name) {
		photos = new ArrayList<Photo>();
		setAlbumName(name);
	}
	
	/**
	 * Returns album anme
	 * @return
	 */
	public String getAlbumName() {
		return albumName;
	}
	
	/**
	 * Sets album name
	 * @param albumName
	 */
	public void setAlbumName(String albumName) {
		this.albumName = albumName;
	}
	
	/**
	 * Returns list of photos for this album
	 * @return
	 */
	public ArrayList<Photo> getPhotos() {
		return photos;
	}
	
	/**
	 * Sets photo list for this album
	 * @param photos
	 */
	public void setPhotos(ArrayList<Photo> photos) {
		this.photos = photos;
	}
	
	/**
	 * Adds the passed photo to the album
	 * @param photo - photo object to add
	 */
	public void addPhoto(Photo photo) {
		this.photos.add(photo);
	}
	
	/**
	 * Removes index 
	 * @param index
	 */
	public void removePhoto(int index) {
		this.photos.remove(index);
	}
	
	/**
	 * Returns the earliest date of all photos in the album
	 * @return
	 */
	public Date getEarlyPhotoDate() {
		return earlyPhotoDate;
	}
	
	/**
	 * Sets the earliest photo date of all photos in the album
	 * @param earlyPhotoDate
	 */
	public void setEarlyPhotoDate(Date earlyPhotoDate) {
		this.earlyPhotoDate = earlyPhotoDate;
	}
	
	/**
	 * Returns the latest date of all photos in the album
	 * @return
	 */
	public Date getLatePhotoDate() {
		return latePhotoDate;
	}
	
	/**
	 * Sets the latest photo date of all photos in the album
	 * @param latePhotoDate
	 */
	public void setLatePhotoDate(Date latePhotoDate) {
		this.latePhotoDate = latePhotoDate;
	}
	
	
}
