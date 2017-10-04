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
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class AlbumScreenController implements javafx.fxml.Initializable {

	@FXML
	private Button addPhoto;
	@FXML
	private Button deletePhoto;
	@FXML
	private Button movePhoto;
	@FXML 
	private Button copyPhoto;
	@FXML
	private Button startSlsh;
	@FXML
	private Button back;
	@FXML
	private Button editPhoto;
	
	@FXML
	private ListView lv;
	@FXML
	private ImageView preview;
	@FXML
	private Label name;
	@FXML
	private Label caption;
	@FXML
	private Label date;
	@FXML
	private Label peopleTags;
	@FXML
	private Label locationTags;
	
	

	private User user;
	private Album album;
	private ArrayList<Photo> results;
	private ArrayList<Image> imageList;
	private ObservableList<String> images;
	
	
	public Album getAlbum() {
		return album;
	}

	public void setAlbum(Album album) {
		this.album = album;
	}


	public User getUser() {
		return user;
	}


	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub		
		}
		
	public void setAlbumName(){
		name.setText(this.album.getAlbumName());
	}
	
	
	public void setPhotoCaption(){
		if(lv.getSelectionModel().getSelectedItem() == null){
			caption.setText("");
		}else{
		int index = lv.getSelectionModel().getSelectedIndex();
		String cap = this.album.getPhotos().get(index).getCaption();
		caption.setText(cap);
		}
	}
	
	public void setPhotoDate(){
		if(lv.getSelectionModel().getSelectedItem() == null){
			date.setText("");
		}else{
		int index = lv.getSelectionModel().getSelectedIndex();
		String d = this.album.getPhotos().get(index).getDate().toString();
		date.setText(d);
		}
	}
	
	/**
	 * Sets photo tags
	 */
	public void setPhotoTags(){
		if(lv.getSelectionModel().getSelectedItem() == null){
			peopleTags.setText("");
			locationTags.setText("");
		}else{
			setPeopleTags();
			setLocationTags();			
		}
	}
	
	/**
	 * Sets text for the people tags
	 */
	private void setPeopleTags() {
		int index = lv.getSelectionModel().getSelectedIndex();
		List<String> people = this.album.getPhotos().get(index).getTags().get("person");
		StringBuilder sb = new StringBuilder();
		if (people != null) {
			for (String person : people) {
				if (people.size() == 0) {
					this.peopleTags.setText("");
					return;
				}
				if (people.size() == 1) {
					this.peopleTags.setText(person);
					return;
				} else {
					sb.append(person + ", ");
				}
			}
			sb.replace(sb.length() - 2, sb.length(), "");
			peopleTags.setText(sb.toString());
		} else {
			this.peopleTags.setText("");
		}
	}
	
	/**
	 * Sets the text for the location tags
	 */
	private void setLocationTags() {
		int index = lv.getSelectionModel().getSelectedIndex();
		List<String> locations = this.album.getPhotos().get(index).getTags().get("location");
		StringBuilder sb = new StringBuilder();
		if (locations != null) {
			for (String location : locations) {
				if (locations.size() == 0) {
					this.locationTags.setText("");
					return;
				} else if (locations.size() == 1) {
					this.locationTags.setText(location);
					return;
				} else {
					sb.append(location + ", ");
				}
			}
			sb.replace(sb.length() - 2, sb.length(), "");
			locationTags.setText(sb.toString());
		} else {
			this.locationTags.setText("");
		}
	}
	
	/**
	 * Loads images into listview
	 */
	@SuppressWarnings("unchecked")
	private void loadResults() {
		lv.setItems(this.images);
		lv.setCellFactory(param -> new ListCell<String>() {
            @SuppressWarnings("unused")
			private ImageView imageView = new ImageView();
            @Override
            public void updateItem(String name, boolean empty) {
                super.updateItem(name, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                	Image image = getImage(name);
                    imageView.setImage(image);
                    setGraphic(imageView);
                }
            }
        });
	}
	
	/**
	 * Gets an image object from the specified file path
	 * @param path location of image file
	 * @return image object
	 */
	private Image getImage(String path) {
		File file = new File(path);
		Image image;
		try {
			image = new Image(new FileInputStream(file), 64, 64, true,true);
			return image;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Loads photos into array list of image objects
	 */
	public void loadImages() {
		this.imageList = new ArrayList<>();
		images = FXCollections.observableArrayList();
		for (Photo photo : this.album.getPhotos()) {
			File file = new File(photo.getFilePath());
			Image image;
			try {
				image = new Image(new FileInputStream(file), 64, 64, true,true);
				this.imageList.add(image);
				this.images.add(photo.getFilePath());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		loadResults();
	}
	
	/**
	 * Adds photo to the album
	 * @param e
	 */
	@FXML
	private void addPhoto(ActionEvent e){
		Stage window = (Stage) back.getScene().getWindow();
    	AnchorPane root = null;
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/application/AddPhotoScreen.fxml"));
			root = (AnchorPane) loader.load(); 
			AddPhotoController controller = loader.<AddPhotoController>getController();
			controller.setUser(this.user);
			controller.setAlbum(this.album);
			controller.setOGPhotos(this.album.getPhotos().size());
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		Scene scene = new Scene(root);
		window.setScene(scene);
		window.sizeToScene();
		window.show();    
	}
	
	/**
	 * Deletes the selected photo from the album
	 * @param e
	 */
	@FXML
	private void deletePhoto(ActionEvent e){
		if (this.album.getPhotos().size() == 0) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Nothing to delete");
			alert.setContentText("There are no photos to delete.");
			alert.showAndWait();
			return;
		}
		if (this.album.getPhotos().size() == 1) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Cannot delete photo");
			alert.setContentText("There must be at least 1 photo in an album.");
			alert.showAndWait();
			return;
		}
		if(lv.getSelectionModel().getSelectedItem() == null){
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Cannot delete photo");
			alert.setContentText("Please select a photo before pressing the ''Delete Photo'' button.");
			alert.showAndWait();
			return;
		}
		int index = lv.getSelectionModel().getSelectedIndex();
		images.remove(index);
		imageList.remove(index);
		int alb = this.user.getAlbums().indexOf(album);
		this.user.getAlbums().get(alb).getPhotos().remove(index);
		preview.setImage(null);
		saveFile();
	}
	
	/**
	 * Moves photo from one album to another
	 * @param e
	 */
	@FXML
	private void movePhoto(ActionEvent e){
		if(lv.getSelectionModel().getSelectedItem() == null){
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Cannot move photo");
			alert.setContentText("Please select a photo before pressing the ''Move Photo'' button.");
			alert.showAndWait();
			return;
		}
		if(user.getAlbums().size() == 1){
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Cannot move photo");
			alert.setContentText("Only one album exists in user's library!");
			alert.showAndWait();
			return;
		}
		if(album.getPhotos().size() == 1){
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Cannot move photo");
			alert.setContentText("Only one photo exists in user's library!");
			alert.showAndWait();
			return;
		}
		TextInputDialog dialog = new TextInputDialog(album.getAlbumName());
		dialog.setTitle("Move Photo");
		dialog.setHeaderText("Add Photo to Different Album");
		dialog.setContentText("Please enter what album you want to move selected photo to:");
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			String caption = result.get();
			for(int i = 0; i < user.getAlbums().size();i++){
				if(user.getAlbums().get(i).getAlbumName().equals(caption)){
					int index = lv.getSelectionModel().getSelectedIndex();
					Photo p = album.getPhotos().get(index);
					user.getAlbums().get(i).addPhoto(p);
					album.getPhotos().remove(p);
					imageList.remove(index);
					images.remove(index);
					
					lv.getSelectionModel().selectFirst();
					String path = (String) lv.getSelectionModel().getSelectedItem();
					setPhotoCaption();
					setPhotoDate();
					setPhotoTags();
					try {
						Image image = new Image(new FileInputStream(path), 256, 256, true,true);
						preview.setImage(image);
			            //setGraphic(displayImage);
					} catch (FileNotFoundException a) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Success");
					alert.setHeaderText("Photo moved successfully!");
					alert.setContentText("The selected photo is now in album ''" + user.getAlbums().get(i).getAlbumName() + "''.");
					alert.showAndWait();
					saveFile();
					return;
				}
			}
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Cannot move photo");
			alert.setContentText("Album does not exist!");
			alert.showAndWait();
			return;
		}
	}
	
	/**
	 * Copies photo from one album to another
	 * @param e
	 */
	@FXML
	private void copyPhoto(ActionEvent e){
		if(lv.getSelectionModel().getSelectedItem() == null){
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Cannot move photo");
			alert.setContentText("Please select a photo before pressing the ''Copy Photo'' button.");
			alert.showAndWait();
			return;
		}
		if(user.getAlbums().size() == 1){
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Cannot move photo");
			alert.setContentText("Only one album exists in user's library!");
			alert.showAndWait();
			return;
		}
		TextInputDialog dialog = new TextInputDialog(album.getAlbumName());
		dialog.setTitle("Copy Photo");
		dialog.setHeaderText("Add Photo to Different Album");
		dialog.setContentText("Please enter what album you want to add selected photo to:");
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			String caption = result.get();
			for(int i = 0; i < user.getAlbums().size();i++){
				if(user.getAlbums().get(i).getAlbumName().equals(caption)){
					int index = lv.getSelectionModel().getSelectedIndex();
					Photo p = album.getPhotos().get(index);
					user.getAlbums().get(i).addPhoto(p);
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Success");
					alert.setHeaderText("Photo copied successfully!");
					alert.setContentText("The selected photo is now in album ''" + user.getAlbums().get(i).getAlbumName() + "''.");
					alert.showAndWait();
					saveFile();
					return;
				}
			}
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Cannot move photo");
			alert.setContentText("Album does not exist!");
			alert.showAndWait();
			return;
		}	
	}
	
	/**
	 * Opens up a slideshow window which the user can use to view all photos in an album
	 * @param e
	 */
	@FXML
	private void slideshow(ActionEvent e){
		Stage window = (Stage) back.getScene().getWindow();
    	AnchorPane root = null;
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/application/Slideshow.fxml"));
			root = (AnchorPane) loader.load(); 
			SlideshowController controller = loader.<SlideshowController>getController();
			controller.setPhotos(this.album.getPhotos());
			controller.setUser(this.user);
			controller.setAlbum(this.album);
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
	 * Opens edit photo screen
	 * @param e
	 */
	@FXML
	private void editPhoto(ActionEvent e){
		if(lv.getSelectionModel().getSelectedItem() == null){
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Cannot edit photo");
			alert.setContentText("Please select a photo before pressing the ''Edit Photo'' button.");
			alert.showAndWait();
			return;
		}
		Stage window = (Stage) back.getScene().getWindow();
    	AnchorPane root = null;
		try {
			
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/application/EditPhotoScreen.fxml"));
			root = (AnchorPane) loader.load(); 
			EditPhotoController controller = loader.<EditPhotoController>getController();
			int index = lv.getSelectionModel().getSelectedIndex();
			Photo photo = album.getPhotos().get(index);
			controller.setUser(user);
			controller.setAlbum(album);
			controller.setPhoto(photo);
			controller.fillImage();
			controller.fillCaption();
			controller.fillPTags();
			controller.fillLTags();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		Scene scene = new Scene(root);
		window.setScene(scene);
		window.sizeToScene();
		window.show();    
	}
	
	/**
	 * Returns to the previous screen
	 * @param event
	 */
	@FXML
	private void backHome(ActionEvent event){
		saveFile();
		Stage window = (Stage) back.getScene().getWindow();
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
	 * Displays a preview of the selected image
	 * @param event
	 */
	@FXML
	private void previewImage(MouseEvent event) {
		String path = (String) lv.getSelectionModel().getSelectedItem();
		if (lv.getSelectionModel().getSelectedIndex() >= 0) {
			setPhotoCaption();
			setPhotoDate();
			setPhotoTags();
			try {
				Image image = new Image(new FileInputStream(path), 256, 256, true,true);
				preview.setImage(image);
	            //setGraphic(displayImage);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
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