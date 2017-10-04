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
import java.util.Collections;
import java.util.Optional;
import java.util.ResourceBundle;

import application.User;
import controller.AdminController;
import controller.HomeScreenController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class AdminController implements javafx.fxml.Initializable {

	@FXML
	private Button addUser;
	
	@FXML
	private Button deleteUser;
	
	@FXML
	private Button adminLogout;
	
	@FXML
	private ListView listView;
	
	private ArrayList<User> usernames;
	
	private ObservableList<String> userList;
	
	
	/**
	 * initializes data used in AdminScreen
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		loadFile();
		loadListData();
	}
	
	/**
	 * adds a new username to list of valid usernames. 
	 * New username can now be used to log into program
	 * @param event
	 */
	@FXML
	public void addUser(ActionEvent event){
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Add User");
		dialog.setHeaderText("Name User");
		dialog.setContentText("Enter a username:");
		Optional<String> result = dialog.showAndWait();
		User username = new User(result.get());
		
		//check if username already used
		for(int i = 0; i < usernames.size(); i++){
			if(username.getName().equals(usernames.get(i).getName())){
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error");
				alert.setHeaderText("Username Already Being Used");
				alert.setContentText("Please choose a different username.");
				alert.showAndWait();
				return;
			}
		}
		if(username.getName().length() > 18 || username.getName().equals("")){
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Cannot Create Username");
			alert.setContentText("Please choose a valid username (1-18 characters long).");
			alert.showAndWait();
			return;
		}
		usernames.add(username);
		userList.add(username.getName());
		loadListData();
		saveFile();
	}
	
	/**
	 * deletes a username from the valid username list.
	 * username can no longer be used to log into program
	 * @param event
	 */
	@FXML
	public void deleteUser(ActionEvent event){
		//no users
		if(listView.getItems().isEmpty()){
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Cannot Delete User");
			alert.setContentText("There are no users to delete!");
			alert.showAndWait();
			return;
		}
		
		String name = (String) listView.getSelectionModel().getSelectedItem();
		//nothing selected
		if(name == null){
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Cannot Delete User");
			alert.setContentText("Please select a user before pressing the ''Delete User'' button.");
			alert.showAndWait();
			return;
		}
		
		else{
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Delete User");
			alert.setHeaderText("Remove User: ''" + name +"''" );
			Optional<ButtonType> result = alert.showAndWait();
			if(result.get() == ButtonType.OK){
				userList.remove(name);
				for(int i = 0; i<usernames.size();i++){
					if(usernames.get(i).getName().equals(name)){
						usernames.remove(i);
					}
				}
				loadListData();
				saveFile();
				return;
			}else{
			return;
			}
		}
	}
	
	/**
	 * Closes admin window, opens login window
	 * @param event
	 */
	@FXML
	private void logout(ActionEvent event){
		saveFile();
		Stage window = (Stage) adminLogout.getScene().getWindow();
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
	 * returns list of valid usernames
	 * @return
	 */
	public ArrayList<User> getUsernames() {
		return usernames;
	}
	
	/**
	 * set list of valid usernames
	 * @param usernames
	 */
	public void setUsernames(ArrayList<User> usernames) {
		this.usernames = usernames;
	}
	
	/**
	 * Updates then loads usernames into listView
	 */
	private void loadListData() {
		userList = FXCollections.observableArrayList();
		listView.getItems().clear();
		ArrayList<String> list = new ArrayList<String>();
		for (User user : usernames) {
			String s = user.getName();
			list.add(s);
		}
		Collections.sort(list);
		userList.addAll(list);
		listView.getItems().addAll(userList);
	}
	
	/**
	 * Saves current list of usernames into file
	 */
	private void saveFile() {
		File fout = new File("Data/users.ser");
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(fout);
			ObjectOutputStream osw = new ObjectOutputStream(fos);
			osw.writeObject(usernames);
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
	 * loads file of valid usernames into ArrayList
	 */
	private void loadFile() {
		File f = new File("Data/users.ser");
		usernames = new ArrayList<User>();
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
	}	

}

