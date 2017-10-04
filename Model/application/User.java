package application;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {

	private boolean admin;
	private String name;
	private ArrayList<Album> albums;
	
	public User(String name) {
		this.name = name;
		this.albums = new ArrayList<>();
	}
	
	public boolean isAdmin() {
		return admin;
	}
	public void setAdmin(boolean admin) {
		this.admin = admin;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<Album> getAlbums() {
		return albums;
	}
	public void setAlbums(ArrayList<Album> albums) {
		this.albums = albums;
	}
	
	/**
	 * Adds album to user's list of albums
	 * @param album
	 */
	public void addAlbum(Album album) {
		this.albums.add(album);
	}
	
	/**
	 * Replace the album at the specified index
	 * @param index - index of the album to be replaced
	 * @param album - album object that is replacing the current one
	 */
	public void replaceAlbum(String albumName, Album album) {
		//this.albums.add(index, album);
		int index = 0;
		for (Album a : this.getAlbums()) {
			if (a.getAlbumName().equals(album.getAlbumName())) {
				this.getAlbums().set(index, album);
				return;
			}
			index++;
		}
	}
	
	/**
	 * REmoves the album at the specified index
	 * @param index - index at which the album is that we want to remove
	 */
	public void removeAlbum(String albumName) {
		int index = 0;
		for (Album a : this.getAlbums()) {
			if (a.getAlbumName().equals(albumName)) {
				this.getAlbums().remove(index);
				return;
			}
			index++;
		}
	}
	
}
