package com.br.firesa.baleiaBotDmo;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.br.firesa.baleiaBotDmo.BaleiaBotController;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

@SpringBootApplication
public class initApp extends Application {
	
	@FXML
	private WebView WebViewBB;
	
	private WebEngine engine;
	
	@Override
	public void start(Stage stage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("BaleiaBotView.fxml"));
			//System.out.println(getClass().getResource("baleiaBotDmo/BaleiaBotView.fxml"));
			Parent root = loader.load();
			BaleiaBotController controller = loader.getController();
			Scene scene = new Scene(root);		
			//stage.getIcons().add(new Image("icon.png"));
			//stage.setTitle("Bro web browser");
			stage.setScene(scene);
			stage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
