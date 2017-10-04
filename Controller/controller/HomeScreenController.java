package controller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Album;
import application.Photo;
import application.User;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

public class HomeScreenController implements javafx.fxml.Initializable {
	
	@FXML
	private Button homeCreateAlbumBtn;
	@FXML
	private Button homeDeleteAlbumBtn;
	@FXML
	private Button homeRenameAlbumBtn;
	@FXML
	private Button homeOpenAlbumBtn;
	@FXML
	private Button homeScreenLogout;
	@FXML
	private Button homeScreenSearch;
	
	@FXML
	private Label albumNameLabel;
	@FXML
	private Label numberOfPhotosLabel;
	@FXML
	private Label earlyPhotoDateLabel;
	@FXML
	private Label latePhotoDateLabel;
	
	@FXML
	private ListView homeScreenAlbumList;
	
	private User user;
	private ObservableList<String> albums;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Loads user data to their home screen
	 */
	public void load() {
		if (this.user.getAlbums() != null && this.user.getAlbums().size() != 0) {
			//Load albums
			loadAlbums();
		}
	}
	
	/**
	 * Loads album data into list view
	 */
	private void loadAlbums() {
		albums = FXCollections.observableArrayList();
		ArrayList<String> albumList = new ArrayList<>();
		for (Album a : this.user.getAlbums()) {
			String name = a.getAlbumName();
			albumList.add(name);
		
			Collections.sort(albumList);
			this.albums.add(name);
			homeScreenAlbumList.getItems().add(name);
		}
	}
	
	/**
	 * Display the album details
	 * @param event
	 */
	@FXML
	private void displayItemDetails(MouseEvent event) {
		String name = (String) homeScreenAlbumList.getSelectionModel().getSelectedItem();
		Album album = getAlbum(name);
		if (album != null) {
			albumNameLabel.setText(album.getAlbumName());
			numberOfPhotosLabel.setText(Integer.toString(album.getPhotos().size()));
			earlyPhotoDateLabel.setText(formatDate(album.getEarlyPhotoDate()));
			latePhotoDateLabel.setText(formatDate(album.getLatePhotoDate()));
		}
	}
	
	/**
	 * Gets the album object with the specified name
	 * @param name - name of the album we want
	 * @return album object with the specified name
	 */
	private Album getAlbum(String name) {
		for (Album a : this.user.getAlbums()) {
			if (a.getAlbumName().equals(name)) {
				return a;
			}
		}
		return null;
	}
	
	/**
	 * Formats the date object into a more user friendly format
	 * @param date
	 * @return
	 */
	private String formatDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
		String time = sdf.format(date);
		return time;
	}
	
	/**
	 * Takes user to the search page
	 * @param event
	 */
	@FXML
	private void openSearchPage(ActionEvent event) {
		Stage window = (Stage) homeScreenLogout.getScene().getWindow();
    	AnchorPane root = null;
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/application/SearchPage.fxml"));
			root = (AnchorPane) loader.load(); 
			SearchPageController controller = loader.<SearchPageController>getController();
			controller.setUser(this.user);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		Scene scene = new Scene(root);
		window.setScene(scene);
		window.sizeToScene();
		window.show();  
	}
	
	/**
	 * Creates a pop-up to allow user to enter new album name
	 * @param event
	 */
	@FXML
	private void renameAlbum(ActionEvent event) {
		if (this.user.getAlbums().size() == 0) {
			return;
		}
		int albumIndex = homeScreenAlbumList.getSelectionModel().getSelectedIndex();
		String albumName = (String) homeScreenAlbumList.getSelectionModel().getSelectedItem();
		Album album = getAlbum(albumName);
		TextInputDialog dialog = new TextInputDialog(album.getAlbumName());
		dialog.setTitle("Rename");
		dialog.setHeaderText("Rename Album");
		dialog.setContentText("Please enter an album name:");
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			String name = result.get();
			if (!albumExists(name)) {
				album.setAlbumName(name);
				this.user.replaceAlbum(albumName, album);
				homeScreenAlbumList.getItems().set(albumIndex, album.getAlbumName());
				saveFile();
			} else {
				displayError("Album with name " + name + " already exists!");
			}
		}
		
	}
	
	/**
	 * Displays error with specified context
	 * @param context
	 */
	private void displayError(String context) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText("Album Name Exists");
		alert.setContentText(context);
		alert.showAndWait();
	}
	
	/**
	 * Checks to see if album with the given name already exists
	 * @param name
	 * @return
	 */
	private boolean albumExists(String name) {
		for (Album a : this.user.getAlbums()) {
			if (a.getAlbumName().equals(name)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Deletes the album that is selected
	 * @param event
	 */
	@FXML
	private void deleteAlbum(ActionEvent event) {
		if (this.user.getAlbums().size() == 0) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Nothing to delete");
			alert.setContentText("There are no albums to delete.");
			alert.showAndWait();
			return;
		}
		if (confirmDelete()) {
			int albumIndex = homeScreenAlbumList.getSelectionModel().getSelectedIndex();
			String albumName = (String) homeScreenAlbumList.getSelectionModel().getSelectedItem();
			homeScreenAlbumList.getItems().remove(albumIndex);
			resetUI();
			this.user.removeAlbum(albumName);
			saveFile();
		} else {
			return;
		}
	}
	
	/**
	 * Resets UI elements
	 */
	private void resetUI() {
		albumNameLabel.setText("");
		numberOfPhotosLabel.setText("");
		earlyPhotoDateLabel.setText("");
		latePhotoDateLabel.setText("");
	}
	
	/**
	 * Opens the selected album
	 * @param event
	 */
	@FXML
	private void openAlbum(ActionEvent event) {
		if (this.user.getAlbums().size() == 0) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Nothing to open");
			alert.setContentText("There are no albums to open.");
			alert.showAndWait();
			return;
		}
		
		String name = (String) homeScreenAlbumList.getSelectionModel().getSelectedItem();
		//nothing selected
		if(name == null){
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Cannot Open Album");
			alert.setContentText("Please select an album before pressing the ''Open Album'' button.");
			alert.showAndWait();
			return;
		}
		int index = this.homeScreenAlbumList.getSelectionModel().getSelectedIndex();
		Stage window = (Stage) homeScreenLogout.getScene().getWindow();
    	AnchorPane root = null;
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/application/AlbumScreen.fxml"));
			root = (AnchorPane) loader.load(); 
			name = (String) homeScreenAlbumList.getSelectionModel().getSelectedItem();
			AlbumScreenController controller = loader.<AlbumScreenController>getController();
			controller.setUser(this.user);
			controller.setAlbum(getAlbum(name));
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
	
	/**
	 * Takes user to the create album page
	 * @param event
	 */
	@FXML
	private void openCreateAlbumPage(ActionEvent event) {
		Stage window = (Stage) homeCreateAlbumBtn.getScene().getWindow();
    	AnchorPane root = null;
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/application/CreateAlbum.fxml"));
			root = (AnchorPane) loader.load(); 
			CreateAlbumController controller = loader.<CreateAlbumController>getController();
			controller.setUser(this.user);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		Scene scene = new Scene(root);
		window.setScene(scene);
		window.sizeToScene();
		window.show();  
	}
	
	/**
	 * Creates pop up message to confirm user deletion
	 * @return
	 */
	private boolean confirmDelete() {
		String context = "Are you sure you want to delete this album?";
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirm Changes");
		alert.setHeaderText("Are you sure you want to apply these changes?");
		alert.setContentText(context);
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
		    // ok was pressed.
			return true;
		} else {
		    // cancel might have been pressed.
			return false;
		}
	}
	
	/**
	 * Logs user out and returns to login page.
	 */
	@FXML
	private void logout(ActionEvent event){
		saveFile();
		Stage window = (Stage) homeScreenLogout.getScene().getWindow();
    	AnchorPane root = null;
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/application/LoginScreen.fxml"));
			root = (AnchorPane) loader.load(); 
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		Scene scene = new Scene(root);
		window.setScene(scene);
		window.sizeToScene();
		window.show();          
	}
	
	/**
	 * Assigns the user for this application and controller
	 * @param user - the user that is logged in
	 */
	public void setUser(User user) {
		this.user = user;
	}
	
	/**
	 * Returns the user profile that is logged in
	 * @return
	 */
	public User getUser() {
		return this.user;
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
