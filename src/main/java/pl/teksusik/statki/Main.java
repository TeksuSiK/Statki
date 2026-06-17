package pl.teksusik.statki;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        Label title = new Label("Battleships");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        Button hostButton = new Button("Host");
        Button joinButton = new Button("Join");

        hostButton.setPrefWidth(200);
        joinButton.setPrefWidth(200);

        VBox root = new VBox(20, title, hostButton, joinButton);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));

        stage.setTitle("Battleships");
        stage.setScene(new Scene(root, 400, 300));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
