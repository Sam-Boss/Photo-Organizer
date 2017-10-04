package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Album;
import application.Photo;
import application.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class EditPhotoController implements javafx.fxml.Initializable {

	@FXML
	private Label cap;
	@FXML
	private Label peopleTags;
	@FXML
	private Label locationTags;
	@FXML
	private Button editCap;
	@FXML
	private Button editPeopleBtn;
	@FXML
	private Button editLocationBtn;
	@FXML
	private Button back;
	@FXML
	private ImageView im;
	
	private Photo photo;
	private User user;
	private Album album;
	
	/**
	 * Returns the user for this window
	 * @return
	 */
	public User getUser() {
		return user;
	}

	/**
	 * Sets user for this window
	 * @param user
	 */
	public void setUser(User user) {
		this.user = user;
	}
	
	/**
	 * Returns the photo assigned to this window
	 * @return
	 */
	public Photo getPhoto() {
		return photo;
	}

	/**
	 * Sets the photo for this window
	 * @param photo
	 */
	public void setPhoto(Photo photo) {
		this.photo = photo;
	}
	
	/**
	 * Returns the album assigned to this window
	 * @return
	 */
	public Album getAlbum() {
		return album;
	}

	/**
	 * Sets the album for this window
	 * @param album
	 */
	public void setAlbum(Album album) {
		this.album = album;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}
	
	@FXML
	private void editCap(ActionEvent e){
		TextInputDialog dialog = new TextInputDialog(album.getAlbumName());
		dialog.setTitle("Apply Changes");
		dialog.setHeaderText("Change Caption");
		dialog.setContentText("Please enter a new caption:");
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			String caption = result.get();
			photo.setCaption(caption);
			fillCaption();
		}	
	}
	
	/**
	 * Adds location tags to the photo
	 * @param e
	 */
	@FXML
	private void editLocationTags(ActionEvent e) {
		TextInputDialog dialog = new TextInputDialog(album.getAlbumName());
		dialog.setTitle("Edit Location");
		dialog.setHeaderText("Add a Location Tag");
		dialog.setContentText("Enter a location. Multiple locations must be comma separated.");
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			String locs = result.get();
			String[] arr = locs.split(",");
			if (photo.getTags().containsKey("location")) {
				List<String> locations = photo.getTags().get("location");
				for (String l : arr) {
					l = l.trim();
					locations.add(l);
				}
				photo.getTags().put("location", locations);
			} else {
				List<String> locations = new ArrayList<String>();
				for (String l : arr) {
					l = l.trim();
					locations.add(l);
				}
				photo.getTags().put("location", locations);
			}
			fillLTags();
			savePhoto();
			saveAlbum();
			saveFile();
		}	
	}
	
	/**
	 * Adds people to the tags
	 * @param e
	 */
	@FXML
	private void editPeopleTags(ActionEvent e){
		TextInputDialog dialog = new TextInputDialog(album.getAlbumName());
		dialog.setTitle("Edit People");
		dialog.setHeaderText("Add a Person Tag");
		dialog.setContentText("Enter a person's name. Multiple names must be comma separated.");
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			String peeps = result.get();
			String[] arr = peeps.split(",");
			if (photo.getTags().containsKey("person")) {
				List<String> people = photo.getTags().get("person");
				for (String p : arr) {
					p = p.trim();
					people.add(p);
				}
				photo.getTags().put("person", people);
			} else {
				List<String> people = new ArrayList<String>();
				for (String p : arr) {
					p = p.trim();
					people.add(p);
				}
				photo.getTags().put("person", people);
			}
			fillPTags();
			savePhoto();
			saveAlbum();
			saveFile();
		}	
	}
	
	/**
	 * Goes back to previous screen
	 * @param e
	 */
	@FXML
	private void goBack(ActionEvent e){
		saveFile();
		Stage window = (Stage) back.getScene().getWindow();
    	AnchorPane root = null;
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/application/AlbumScreen.fxml"));
			root = (AnchorPane) loader.load(); 
			AlbumScreenController controller = loader.<AlbumScreenController>getController();
			controller.setUser(this.user);
			controller.setAlbum(album);
			controller.loadImages();
			controller.setAlbumName();	
			controller.setPhotoCaption();
			controller.setPhotoDate();
			controller.setPhotoTags();
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		Scene scene = new Scene(root);
		window.setScene(scene);
		window.sizeToScene();
		window.show();    
	}
	
	public void fillImage(){
		String path = photo.getFilePath();
		try {
			Image image = new Image(new FileInputStream(path), 512, 512, true,true);
			im.setImage(image);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void fillCaption(){
		if(photo.getCaption() == null ||photo.getCaption().equals("")){
			this.cap.setText("N/A");
		}else{
			this.cap.setText(photo.getCaption());
		}
	}
	
	/**
	 * Sets the label for the people tags
	 */
	public void fillPTags(){
		if(photo.getTags().get("person") == null){
			this.peopleTags.setText("N/A");
		}else{
			//change later
			List<String> people = this.photo.getTags().get("person");
			StringBuilder sb = new StringBuilder();
			for (String person : people) {
				if (people.size() == 0) {
					this.peopleTags.setText("N/A");
					return;
				}
				if (people.size() == 1) {
					this.peopleTags.setText(person);
					return;
				} else {
					sb.append(person + ", ");
				}
			}
			this.peopleTags.setText(sb.toString());
		}
	}
	
	/**
	 * Sets the label for the location tags
	 */
	public void fillLTags(){
		if(photo.getTags().get("location") == null){
			this.locationTags.setText("N/A");
		}else{
			//change later
			List<String> locations = this.photo.getTags().get("location");
			StringBuilder sb = new StringBuilder();
			for (String place : locations) {
				if (locations.size() == 0) {
					this.locationTags.setText("N/A");
					return;
				}
				if (locations.size() == 1) {
					this.locationTags.setText(place);
					return;
				} else {
					sb.append(place + ", ");
				}
			}
			this.locationTags.setText(sb.toString());
		}
	}
	
	/**
	 * Saves changes made to the photo
	 */
	private void savePhoto() {
		int index = 0;
		for (Photo p : this.album.getPhotos()) {
			if (p.getFilePath().equals(this.photo.getFilePath())) {
				ArrayList<Photo> photos = this.album.getPhotos();
				photos.set(index, this.photo);
				this.album.setPhotos(photos);
				return;
			}
			index++;
		}
	}
	
	/**
	 * Save album changes
	 */
	private void saveAlbum() {
		int index = 0;
		for (Album a : this.user.getAlbums()) {
			if (a.getAlbumName().equals(this.album.getAlbumName())) {
				ArrayList<Album> albums = this.user.getAlbums();
				albums.set(index, this.album);
				this.user.setAlbums(albums);
				return;
			}
			index++;
		}
	}
	
	/**
	 * Saves changes to the file
	 */
	private void saveFile() {
		ArrayList<User> users = loadFile();
		File fout = new File("Data/users.ser");
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(fout);
			ObjectOutputStream osw = new ObjectOutputStream(fos);
			users = overwriteUser(users);
			osw.writeObject(users);
			osw.flush();
			osw.close();
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Loads data from the file
	 */
	@SuppressWarnings("unchecked")
	private ArrayList<User> loadFile() {
		File f = new File("Data/users.ser");
		ArrayList<User> usernames = new ArrayList<User>();
		if (f.exists() && !f.isDirectory()) {
			try {
				FileInputStream fis = new FileInputStream(f);
				ObjectInputStream ois = new ObjectInputStream(fis);
				usernames = (ArrayList<User>) ois.readObject();
				fis.close();
				ois.close();
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
		return usernames;
	}
	
	/**
	 * Overwrites user data
	 * @return
	 */
	private ArrayList<User> overwriteUser(ArrayList<User> users) {
		int index = 0;
		for (User u : users) {
			if (this.user.getName().equals(u.getName())) {
				users.set(index, this.user);
			}
			index++;
		}
		return users;
	}
}
