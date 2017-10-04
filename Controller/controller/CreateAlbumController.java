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
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import application.Album;
import application.Photo;
import application.User;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class CreateAlbumController implements javafx.fxml.Initializable {
	
	@FXML
	Button createAlbumBackBtn;
	@FXML
	Button importPhotosBtn;
	@FXML
	Button createAlbumBtn;
	@FXML
	TextField albumNameTxtField;
	@FXML
	Label number;
	
	private User user;
	private ArrayList<Photo> photos;
	private int num;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		this.photos = new ArrayList<Photo>();
		this.num = 0;
	}
	
	/**
	 * Gets indices of all selected photos from the listview and adds them to an album object
	 * @param album - album to add photos to
	 * @return - album object with photos added
	 */
	private Album addPhotosToAlbum(Album album) {
		Date earliestDate = null;
		Date latestDate = null;
		if (this.photos == null || this.photos.size() == 0) {
			return null;
		}
		for (Photo p : this.photos) {
			if (earliestDate == null) {
				earliestDate = p.getDate();
				latestDate = p.getDate();
			} else if (p.getDate().before(earliestDate)) {
				earliestDate = p.getDate();
			} else if (p.getDate().after(latestDate)) {
				latestDate = p.getDate();
			}
			album.addPhoto(p);
		}
		album.setEarlyPhotoDate(earliestDate);
		album.setLatePhotoDate(latestDate);
		return album;
	}
	
	/**
	 * Performs action necessary to create and save the album
	 * @param event
	 */
	@FXML
	private void createAlbum(ActionEvent event) {
		String albumName = albumNameTxtField.getText();
		if (albumName == null || albumName.equals("") || albumName.length() > 18) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Could not apply changes.");
			alert.setContentText("You must enter a valid album name (1-18 characters long)");
			alert.showAndWait();
		} else if (!albumExists(albumName)) {
			//Album doesn't exist
			Album album = new Album(albumName);
			album = addPhotosToAlbum(album);
			if (album == null) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error");
				alert.setHeaderText("Could not apply changes.");
				alert.setContentText("Must select at least one image!");
				alert.showAndWait();
				return;
			}
			this.user.addAlbum(album);
			saveFile();
			goToHome();
		} else {
			//Album name already being used
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Could not apply changes.");
			alert.setContentText("Album with name " + albumName + " already exists!");
			alert.showAndWait();
		}
	}
	
	/**
	 * Checks to see if album name is already being used 
	 * @param albumName
	 * @return
	 */
	private boolean albumExists(String albumName) {
		for (Album album : this.user.getAlbums()) {
			if (album.getAlbumName().toLowerCase().equals(albumName.toLowerCase())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Allows user to import photos from their PC
	 * @param event
	 */
	@FXML
	private void importPhotos(ActionEvent event) {
		boolean exists = false;
		Stage window = (Stage) importPhotosBtn.getScene().getWindow();
		
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
		
		num++;
		this.number.setText(Integer.toString(num));
		
		List<File> files = fileChooser.showOpenMultipleDialog(window);
		if (files == null || files.size() == 0) {
			return;
		}
		for (File f : files) {
			Photo photo = new Photo(f.getAbsolutePath());
			this.photos.add(photo);
		}
	}
	
	/**
	 * Returns to the home page
	 */
	@FXML
	private void goBack(ActionEvent event) {
		Stage window = (Stage) createAlbumBackBtn.getScene().getWindow();
    	AnchorPane root = null;
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/application/HomeScreen.fxml"));
			root = (AnchorPane) loader.load();
			HomeScreenController controller = loader.<HomeScreenController>getController();
			controller.setUser(this.user);
			controller.load();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		Scene scene = new Scene(root);
		window.setScene(scene);
		window.sizeToScene();
		window.show();
	}
	
	/**
	 * Returns to home page
	 */
	private void goToHome() {
		Stage window = (Stage) createAlbumBtn.getScene().getWindow();
    	AnchorPane root = null;
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/application/HomeScreen.fxml"));
			root = (AnchorPane) loader.load();
			HomeScreenController controller = loader.<HomeScreenController>getController();
			controller.setUser(this.user);
			controller.load();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		Scene scene = new Scene(root);
		window.setScene(scene);
		window.sizeToScene();
		window.show();
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
	 * Returns list of photos associated with this user
	 * @return
	 */
	private ArrayList<Photo> loadPhotos() {
		ArrayList<Photo> userPhotos = new ArrayList<>();
		for (Album a : this.user.getAlbums()) {
			for (Photo p : a.getPhotos()) {
				userPhotos.add(p);
			}
		}
		return userPhotos;
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
	
	
	/**
	 * Sets the user that is currently logged in
	 * @param user
	 */
	public void setUser(User user) {
		this.user = user;
	}
	
}
