package controller;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import application.Photo;
import application.Album;
import application.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class SearchPageController implements javafx.fxml.Initializable {

	@FXML
	Button backBtn;
	@FXML
	Button searchBtn;
	@FXML
	DatePicker startDate;
	@FXML
	DatePicker endDate;
	@FXML
	TextField locationTxt;
	@FXML
	TextField peopleTxt;
	
	private User user;
	private ArrayList<Photo> photos;
	private Date start;
	private Date end;
	private String[] locations;
	private String[] people;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		this.photos = new ArrayList<>();
	}
	
	/**
	 * Searches for photos matching criteria and goes to search results page
	 * @param event
	 */
	@FXML
	private void search(ActionEvent event) {
		//TODO perform a search on photos in the user's profile that matches the criteria
		if (endDate.getValue() == null && startDate.getValue() != null) {
			displayAlert("Must specify a valid end date!");
		} else if (startDate.getValue() == null && endDate.getValue() != null) {
			displayAlert("Must specify a valid start date!");
		} else if (startDate.getValue() == null && endDate.getValue() == null){
			//this.start = Date.from(startDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
			//this.end = Date.from(endDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
			if (!locationTxt.getText().equals("")) {
				this.locations = locationTxt.getText().split(",");
			} else {
				this.locations = null;
			}
			if (!peopleTxt.getText().equals("")) {
				this.people = peopleTxt.getText().split(",");
			} else {
				this.people = null;
			}
			if (this.people != null || this.locations != null) {
				loadFile();
				goToSearchResults();
			} else {
				displayAlert("Must specify search criteria.");
			}
		} else {
			this.start = Date.from(startDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
			this.end = Date.from(endDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
			loadFile();
			goToSearchResults();
		}
	}
	
	/**
	 * Determines whether the given photo meets our search criteria
	 * @param photo
	 * @return
	 */
	private boolean matchesCriteria(Photo photo) {
		//TODO look for photos between specified dates, and containing same tags
		if (this.start != null && this.end != null && betweenDates(photo)) {
			//Look between dates with tags for people and location specified
			if ((this.people != null && this.people.length != 0) && (this.locations != null && this.locations.length != 0)) {
				return hasLocation(photo) && hasPeople(photo);
			} else {
				return true;
			}
		} else if ((this.people != null && this.people.length != 0) && (this.locations != null && this.locations.length != 0)) {
			//Only look for people and locations tags
			return hasLocation(photo) && hasPeople(photo);
		} else if (this.people != null && this.people.length != 0) {
			//Only look for people tags
			return hasPeople(photo);
		} else if (this.locations != null && this.locations.length != 0) {
			//Only look for locations tags
			return hasLocation(photo);
		}
		return false;
	}
	
	/**
	 * Checks to see if the passed photo's date falls between our specified start and end date search criteria
	 * @param photo
	 * @return
	 */
	private boolean betweenDates(Photo photo) {
		if (photo.getDate().equals(start) || photo.getDate().equals(end)) {
			return true;
		}
		if (photo.getDate().before(this.end)) {
			if (photo.getDate().after(this.start)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks to see if photo contains tags match the search criteria
	 * @param photo
	 * @return
	 */
	private boolean hasLocation(Photo photo) {
		boolean match = false;
		HashMap<String, List<String>> photoTags = photo.getTags();
		for (String location : this.locations) {
			location = location.trim();
			if (photoTags.get("location") != null && photoTags.get("location").contains(location)) {
				match = true;
			}
		}	
		return match;
	}
	
	/**
	 * Checks to see if photo has people tags matching the search criteria
	 * @param photo
	 * @return
	 */
	private boolean hasPeople(Photo photo) {
		boolean match = false;
		HashMap<String, List<String>> photoTags = photo.getTags();
		for (String person : this.people) {
			person = person.trim();
			if (photoTags.get("person") != null && photoTags.get("person").contains(person)) {
				match = true;
			}
		}
		return match;
	}
	
	/**
	 * Displays error message with the given context string
	 * @param context
	 */
	private void displayAlert(String context) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText("Invalid dates.");
		alert.setContentText(context);
		alert.showAndWait();
	}
	
	/**
	 * Goes to search results page
	 */
	private void goToSearchResults() {
		Stage window = (Stage) backBtn.getScene().getWindow();
    	AnchorPane root = null;
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/application/SearchResultsPage.fxml"));
			root = (AnchorPane) loader.load(); 
			SearchResultsController controller = loader.<SearchResultsController>getController();
			controller.setUser(this.user);
			controller.setResults(this.photos);
			controller.loadImages();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		Scene scene = new Scene(root);
		window.setScene(scene);
		window.sizeToScene();
		window.show();
	}
	
	/**
	 * Returns back to the home screen on button click
	 * @param event
	 */
	@FXML
	private void goBack(ActionEvent event) {
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
		window.show();      
	}
	
	/**
	 * Sets the user that is logged in
	 * @param user
	 */
	public void setUser(User user) {
		this.user = user;
	}
	
	/**
	 * Loads up all of the user's photo into an array list which we will scan to see if any photos match the search criteria
	 * @return
	 */
	private void loadFile() {
		for (Album album : this.user.getAlbums()) {
			for (Photo photo : album.getPhotos()) {
				if (matchesCriteria(photo)) {
					this.photos.add(photo);
				}
			}
		}
	}

}
