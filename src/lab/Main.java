package lab;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("main_form.fxml"));
        primaryStage.setTitle("MaADM_Lab_3_4");
        primaryStage.setScene(new Scene(root, 845, 600));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
