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
import java.util.Optional;
import java.util.ResourceBundle;

import application.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import application.Album;
import application.Photo;

public class SearchResultsController implements javafx.fxml.Initializable {
	
	@FXML
	ListView resultsList;
	@FXML
	ImageView displayImage;
	@FXML
	Button backBtn;
	@FXML
	Button createAlbumBtn;
	
	private User user;
	private ArrayList<Photo> results;
	private ArrayList<Image> imageList;
	private ObservableList<String> images;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		resultsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	}
	
	
	/**
	 * Loads images into listview
	 */
	@SuppressWarnings("unchecked")
	private void loadResults() {
		resultsList.setItems(this.images);
		resultsList.setCellFactory(param -> new ListCell<String>() {
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
	 * Loads photos into array list of image objects
	 */
	public void loadImages() {
		images = FXCollections.observableArrayList();
		for (Photo photo : results) {
			this.images.add(photo.getFilePath());
		}
		loadResults();
	}
	
	/**
	 * Returns an image object for the specified photo
	 * @param photo
	 * @return
	 */
	private Image getImage(String path) {
		File file = new File(path);
		Image image;
		try {
			image = new Image(new FileInputStream(file), 64, 64, true,true);
			return image;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("File Not Found");
			alert.setHeaderText("File Not Found Erro");
			alert.setContentText("There was an error in locating some of the image files");

			alert.showAndWait();
			goHome();
		}
		return null;
	}
	
	/**
	 * Previews image that user clicked
	 * @param event
	 */
	@FXML
	private void previewImage(MouseEvent event) {
		String path = (String) resultsList.getSelectionModel().getSelectedItem();
		try {
			Image image = new Image(new FileInputStream(path), 256, 256, true,true);
			displayImage.setImage(image);
            //setGraphic(displayImage);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Get list of photos that were selected and the user wants to create a new album with
	 * @return
	 */
	private ArrayList<Photo> getSelection() {
		ArrayList<Photo> selection = new ArrayList<Photo>();
		ObservableList<String> list = resultsList.getSelectionModel().getSelectedItems();
		for (String path : list) {
			Photo photo = new Photo(path);
			selection.add(photo);
		}
		return selection;
	}
	
	/**
	 * Returns to the previous screen
	 * @param event
	 */
	@FXML
	private void goBack(ActionEvent event) {
		Stage window = (Stage) backBtn.getScene().getWindow();
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
	 * Returns to the home screen
	 */
	private void goHome() {
		Stage window = (Stage) backBtn.getScene().getWindow();
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
		window.setWidth(802);
		window.setHeight(525);
		window.show();    
	}
	
	/**
	 * Takes user to the create album page
	 * @param event
	 */
	@FXML
	private void openCreateAlbumPage(ActionEvent event) {
		ArrayList<Photo> selection = getSelection();
		if (selection.size() == 0) {
			return;
		}
		TextInputDialog dialog = new TextInputDialog("New Album");
		dialog.setTitle("Album Name");
		dialog.setHeaderText("Album Name");
		dialog.setContentText("Please enter an album name:");
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			String name = result.get();
			//TODO check if name is already in use
			if (!nameInUse(name)) {
				Album album = createAlbum(selection, name);
				this.user.addAlbum(album);
				saveFile();
				goHome();
			} else {
				//Display error
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error");
				alert.setHeaderText("Could not apply changes.");
				alert.setContentText("Album with name " + name + " already exists!");
				alert.showAndWait();
			}
		}
	}
	
	/**
	 * Creates an album with the specified photos and name
	 * @param selections - photos to add to the album
	 * @param name - name of the album 
	 * @return - album object
	 */
	private Album createAlbum(ArrayList<Photo> selections, String name) {
		Date earliestDate = null;
		Date latestDate = null;
		Album album = new Album(name);
		for (Photo p : selections) {
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
	 * Checks to see if there already exists an album with the specified name
	 * @param name - new name of the album user wants to create
	 * @return
	 */
	private boolean nameInUse(String name) {
		for (Album album : this.user.getAlbums()) {
			if (album.getAlbumName().equals(name)) {
				return true;
			}
		}
		return false;
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
	 * Sets the user for this controller
	 * @param user
	 */
	public void setUser(User user) {
		this.user = user;
	}
	
	/**
	 * Sets results array list 
	 * @param results
	 */
	public void setResults(ArrayList<Photo> results) {
		this.results = results;
	}

}
