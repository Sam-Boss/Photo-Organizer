package controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import application.Album;
import application.Photo;
import application.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class SlideshowController implements javafx.fxml.Initializable {
	
	@FXML
	Button moveForward;
	@FXML
	Button moveBack;
	@FXML
	Button backBtn;
	@FXML
	ImageView imageDisplay;
	
	private User user;
	private Album album;
	private ArrayList<Photo> photos;
	private int index = 0;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		index = 0;
	}
	
	/**
	 * Loads the first image of the album into the imageview
	 */
	public void load() {
		String path = photos.get(index).getFilePath();
		try {
			Image image = new Image(new FileInputStream(path), 512, 512, true,true);
			imageDisplay.setImage(image);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Moves to the next image in the list
	 * @param event
	 */
	@FXML
	private void moveForward(ActionEvent event) {
		index++;
		if (index >= this.photos.size()) {
			index = this.photos.size() - 1;
		} else {
			load();
		}
	}
	
	/**
	 * Moves to the previous image in the list
	 * @param event
	 */
	@FXML
	private void moveBack(ActionEvent event) {
		index--;
		if (index >= 0) {
			load();
		} else {
			index = 0;
		}
	}
	
	/**
	 * Goes back to album page
	 * @param event
	 */
	@FXML
	private void goBack(ActionEvent event) {
		Stage window = (Stage) backBtn.getScene().getWindow();
    	AnchorPane root = null;
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/application/AlbumScreen.fxml"));
			root = (AnchorPane) loader.load(); 
			AlbumScreenController controller = loader.<AlbumScreenController>getController();
			controller.setUser(this.user);
			controller.setAlbum(this.album);
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
	 * Sets the photo object for this image viewer
	 * @param photo
	 */
	public void setPhotos(ArrayList<Photo> photos) {
		this.photos = photos;
	}
	
	/**
	 * Assigns user 
	 * @param user
	 */
	public void setUser(User user) {
		this.user = user;
	}
	
	/**
	 * Assings parent album
	 * @param album
	 */
	public void setAlbum(Album album) {
		this.album = album;
	}

}
