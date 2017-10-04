package controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import application.User;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


public class LoginScreenController implements javafx.fxml.Initializable {

	@FXML
	private Button enter;
	
	@FXML
	private TextField username;
	
	private ArrayList<User> usernames;
	
	
	/**
	 * returns ArrayList of valid usernames
	 * @return
	 */
	public ArrayList<User> getUsernames() {
		return usernames;
	}
	
	/**
	 * initializes program, opens up login screen
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		loadFile();
		//Add listener to handle user song additions
				enter.setOnAction(new EventHandler<ActionEvent>() {
					@Override
				    public void handle(ActionEvent e) {
				    	Stage window = (Stage) enter.getScene().getWindow();
				    	AnchorPane root = null;
				    	if(checkAdmin(username.getText())){
				    		openAdminScreen();
				    	}else if(checkUsername(username.getText())){
						try {
							FXMLLoader loader = new FXMLLoader();
							loader.setLocation(getClass().getResource("/application/HomeScreen.fxml"));
							root = (AnchorPane) loader.load(); 	
							HomeScreenController controller = loader.<HomeScreenController>getController();
							User user = getUser(username.getText());
							controller.setUser(user);
							controller.load();
						} catch(Exception ex) {
							ex.printStackTrace();
						}
						
						Scene scene = new Scene(root);
						window.setScene(scene);
						window.sizeToScene();
						window.show();
				    }else{
				    	Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("Error");
						alert.setHeaderText("Username does not exist.");
						alert.setContentText("You must enter a valid username!");
						alert.showAndWait();
				   }
				}
			});
		}
	
	/**
	 * checks if "admin" is typed into TextField
	 * @param user
	 * @return
	 */
	public boolean checkAdmin(String user){
		if(user.equals("admin")){
			return true;
		}return false;
	}
	
	/**
	 * checks if what is typed into TextField is a valid username
	 * @param user
	 * @return
	 */
	public boolean checkUsername(String user){
		if(usernames == null || usernames.size() == 0){
			return false;
		}else{
			for(int i = 0; i < usernames.size(); i++){
				if(usernames.get(i).getName().equals(user)) {
					return true;
				}
			}
			return false;
		}
	}
	
	/**
	 * Returns user profile matching the username
	 * @param username
	 * @return
	 */
	public User getUser(String username) {
		for (User user : usernames) {
			if (user.getName().equals(username)) {
				return user;
			}
		}
		return null;
	}
	
	/**
	 * closes login screen, opens admin screen
	 */
	public void openAdminScreen() {
	    Stage window = (Stage) enter.getScene().getWindow();
	    AnchorPane root = null;
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/application/AdminScreen.fxml"));
			root = loader.load(); 	
			AdminController controller = loader.<AdminController>getController();
			controller.setUsernames(usernames);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
			
		Scene scene = new Scene(root);
		window.setScene(scene);
		window.sizeToScene();
		window.show();
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