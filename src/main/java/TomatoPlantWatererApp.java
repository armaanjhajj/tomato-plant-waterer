import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.*;
import java.time.LocalDateTime;

public class TomatoPlantWatererApp extends Application {

    private Timeline loop;
    private Spinner<Integer> minutesSpinner;
    private Button startBtn;
    private Label status;
    private AudioClip chime;

    @Override public void start(Stage stage) {
        chime = new AudioClip(getClass().getResource("/sounds/chime.wav").toExternalForm());
        FontLoader.register("/fonts/PressStart2P.ttf");

        ImageView plant = new ImageView(
                new Image(getClass().getResource("/images/plant.png").toExternalForm()));
        plant.setFitWidth(64); plant.setPreserveRatio(true);

        minutesSpinner = new Spinner<>(1, 240, 30); minutesSpinner.setEditable(true);
        startBtn = new Button("START");
        startBtn.setOnAction(e -> toggle());

        status = new Label("idle");

        VBox root = new VBox(15, plant,
                new Label("remind every (min):"), minutesSpinner, startBtn, status);
        root.setPadding(new Insets(25)); root.getStyleClass().add("root");

        Scene scene = new Scene(root, 300, 320);
        scene.getStylesheets().add(getClass().getResource("/styles/app.css").toExternalForm());

        stage.setTitle("Tomato Plant Waterer 🍅");
        stage.setScene(scene); stage.setResizable(false); stage.show();
    }

    private void toggle() {
        if (loop == null) {                    // start
            int m = minutesSpinner.getValue();
            loop = new Timeline(new KeyFrame(Duration.minutes(m), e -> remind()));
            loop.setCycleCount(Animation.INDEFINITE); loop.play();
            startBtn.setText("STOP"); status.setText("running every " + m + " min");
        } else {                               // stop
            loop.stop(); loop = null;
            startBtn.setText("START"); status.setText("idle");
        }
    }

    private void remind() {
        new Alert(Alert.AlertType.INFORMATION, "YO! WATER THE TOMATO!!! 🍅").show();
        chime.play();
        try (FileWriter fw = new FileWriter("reminder_log.txt", true)) {
            fw.write("Reminder: " + LocalDateTime.now() + System.lineSeparator());
        } catch (IOException ignored) {}
    }

    public static void main(String[] a) { launch(); }
}

class FontLoader { static void register(String p) {
    javafx.scene.text.Font.loadFont(FontLoader.class.getResourceAsStream(p), 12);
}}
