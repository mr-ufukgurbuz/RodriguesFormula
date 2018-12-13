import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class deneme extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Group group = new Group();

        Rectangle rect = new Rectangle(20,20,200,200);

        rect.setFill(new Color(0.2, 0.2,0.3, 0.1 ));

        rect.setStroke(Color.TRANSPARENT);
        group.getChildren().add(rect);

        Scene scene = new Scene(group, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}