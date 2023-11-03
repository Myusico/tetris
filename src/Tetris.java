import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.Animation;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.collections.ObservableList;
import javafx.util.Duration;

import java.util.*;

public class Tetris extends Application {
    private StackPane game = new StackPane();
    private Pane tetrominos = new Pane();
    private Pane panel = new Pane();
    private Text pauseText = new Text("PAUSED");
    private Text gameOverText = new Text("GAMEOVER");
    private Text scoreText = new Text("000");
    private Button play = new Button("Play");
    private boolean gameOver = true;
    private double side = 40;
    private Timeline freeFall = new Timeline();
    private double period = 0.2;
    private int rowsCleared = 0;
    private Tetromino tetromino;
    private Tetromino nextTetro;
    private List<Integer> order = new ArrayList<>();
    private int nextIndex;
    private LinkedList<Rectangle[]> rectangles = new LinkedList<>();

    @Override
    public void start(Stage primaryStage) {
        for (int i = 0; i < 14; i++) {
            order.add(i % 7);
        }
        Rectangle gameBackground = new Rectangle(side * 10, side * 20);
        pauseText.setFont(Font.font("Impact", 80));
        pauseText.setStyle("-fx-fill:white;-fx-stroke:red;-fx-stroke-width: 2");
        gameOverText.setFont(Font.font("Impact", 80));
        gameOverText.setStyle("-fx-fill:white;-fx-stroke:red;-fx-stroke-width: 2");
        game.getChildren().addAll(gameBackground, tetrominos);

        Rectangle panelBackground = new Rectangle(side * 7, side * 20);
        panelBackground.setStyle("-fx-fill: grey");
        panel.getChildren().add(panelBackground);
        for (int i = 1; i < 7; i++) {
            panel.getChildren().add(new Line(side * i, 0, side * i, side * 20));
        }
        for (int i = 1; i < 20; i++) {
            panel.getChildren().add(new Line(0, side * i, side * 7, side * i));
        }
        Rectangle nextBackground = new Rectangle(side, side * 3, side * 5, side * 4);

        Rectangle nextGrey = new Rectangle(side * 5, side * 2);
        nextGrey.setStyle("-fx-fill: darkgrey");
        Text nextText = new Text("NEXT");
        nextText.setFont(Font.font("Bauhaus 93", 72));
        StackPane next = new StackPane(nextGrey, nextText);
        next.setLayoutX(side);
        next.setLayoutY(side);

        Rectangle linesGrey = new Rectangle(side * 5, side);
        linesGrey.setStyle("-fx-fill:darkgrey");
        Text linesText = new Text("Score");
        linesText.setFont(Font.font("Bauhaus 93", 36));
        StackPane linesCleared = new StackPane(linesGrey, linesText);
        linesCleared.setLayoutX(side);
        linesCleared.setLayoutY(side * 8);

        Rectangle scoreBackground = new Rectangle(side * 5, side * 2);
        scoreText.setFont(Font.font("Broadway", 72));
        scoreText.setStyle("-fx-fill:white");
        StackPane score = new StackPane(scoreBackground, scoreText);
        score.setLayoutX(side);
        score.setLayoutY(side * 9);

        Rectangle difficultyGrey = new Rectangle(side * 5, side);
        difficultyGrey.setStyle("-fx-fill:darkgrey");
        Text difficultyText = new Text("Difficulty");
        difficultyText.setFont(Font.font("Bauhaus 93", 36));
        StackPane difficultyPane = new StackPane(difficultyGrey, difficultyText);
        difficultyPane.setLayoutX(side);
        difficultyPane.setLayoutY(side * 12);

        ObservableList<String> difficulties = FXCollections.observableArrayList("Easy", "Normal", "Hard", "Extreme");
        ChoiceBox<String> difficulty = new ChoiceBox<>(difficulties);
        ChangeListener<String> changeListener = (observable, oldValue, newValue) -> {
            switch (newValue) {
                case "Easy":
                    period = 0.6;
                    break;
                case "Normal":
                    period = 0.2;
                    break;
                case "Hard":
                    period = 0.12;
                    break;
                case "Extreme":
                    period = 0.08;
            }
            if (gameOver || freeFall.getStatus() == Animation.Status.PAUSED) {
                createTimeline(period);
                freeFall.pause();
            } else {
                createTimeline(period);
            }
        };
        difficulty.getSelectionModel().selectedItemProperty().addListener(changeListener);

        difficulty.setMinSize(side * 5, side);
        difficulty.setLayoutX(side);
        difficulty.setLayoutY(side * 13);
        difficulty.setFocusTraversable(false);
        difficulty.setValue("Normal");
        Button tutorial = new Button("Tutorial");
        tutorial.setLayoutX(side);
        tutorial.setLayoutY(side * 15);
        tutorial.setMinSize(side * 5, side);
        tutorial.setFont(Font.font("Bauhaus 93", 22));
        tutorial.setFocusTraversable(false);
        play.setLayoutX(side);
        play.setLayoutY(side * 17);
        play.setMinSize(side * 5, side * 2);
        play.setMaxSize(side * 5, side * 2);
        play.setFont(Font.font("Bauhaus 93", 42));
        play.setFocusTraversable(false);
        panel.getChildren().addAll(next, nextBackground, linesCleared, score, difficultyPane, difficulty, tutorial, play);

        HBox hBox = new HBox();
        hBox.getChildren().addAll(game, panel);
        Scene scene = new Scene(hBox);

        play.setOnAction(e -> {
            if (gameOver) {
                tetrominos.getChildren().clear();
                game.getChildren().remove(gameOverText);
                rectangles.clear();
                for (int i = 0; i < 20; i++) {
                    rectangles.add(new Rectangle[10]);
                }
                if (nextTetro != null) {
                    nextTetro.removeFrom(panel);
                }
                rowsCleared = 0;
                scoreText.setText("000");
                Collections.shuffle(order);
                tetromino = new Tetromino(order.get(0), side);
                tetromino.addTo(tetrominos);
                nextTetro = new Tetromino(order.get(1), side);
                nextTetro.move(-1, 4);
                if (nextTetro.getType() == 0) {
                    nextTetro.move(-0.5, 0);
                } else if (nextTetro.getType() == 6) {
                    nextTetro.move(-0.5, 0.5);
                }
                nextTetro.addTo(panel);
                nextIndex = 2;
                createTimeline(period);
                play.setText("Pause");
                play.setFont(Font.font("Bauhaus 93", 48));
                gameOver = false;
                return;
            }
            if (freeFall.getStatus() == Animation.Status.PAUSED) {
                freeFall.play();
                game.getChildren().remove(pauseText);
                play.setText("Pause");
                play.setFont(Font.font("Bauhaus 93", 48));
            } else {
                freeFall.pause();
                game.getChildren().add(pauseText);
                play.setText("Continue");
                play.setFont(Font.font("Bauhaus 93", 36));
            }
        });

        tutorial.setOnAction(e -> showTutorial());

        scene.setOnKeyPressed(e -> {
            if ((gameOver || freeFall.getStatus() == Animation.Status.PAUSED) && e.getCode() != KeyCode.ENTER) {
                return;
            }
            switch (e.getCode()) {
                case UP:
                    tetromino.rotate(rectangles);
                    break;
                case RIGHT:
                    tetromino.move(1, 0);
                    if (!tetromino.isValid(rectangles)) {
                        tetromino.move(-1, 0);
                    }
                    break;
                case DOWN:
                    tetromino.move(0, 1);
                    if (!tetromino.isValid(rectangles)) {
                        tetromino.move(0, -1);
                    }
                    break;
                case LEFT:
                    tetromino.move(-1, 0);
                    if (!tetromino.isValid(rectangles)) {
                        tetromino.move(1, 0);
                    }
                    break;
                case SPACE:
                    while (tetromino.fall(rectangles)) {
                    }
                    rowsCleared += (clearRows(checkRows(tetromino.getRows()), tetrominos));
                    scoreText.setText(String.format("%03d", rowsCleared));
                    dropNextTetro();
                    break;
                case ENTER:
                    play.fire();
            }
        });

        primaryStage.setScene(scene);
        primaryStage.setTitle("Tetris by Muyou");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void createTimeline(double sec) {
        freeFall.stop();
        freeFall = new Timeline(new KeyFrame(Duration.seconds(sec), e -> {
            if (gameOver) {
                freeFall.stop();
                return;
            }
            if (!tetromino.fall(rectangles)) {
                rowsCleared += (clearRows(checkRows(tetromino.getRows()), tetrominos));
                scoreText.setText(String.format("%03d", rowsCleared));
                dropNextTetro();
                if (!tetromino.isValid(rectangles)) {
                    endGame();
                }
            }
        }));
        freeFall.setCycleCount(Timeline.INDEFINITE);
        freeFall.play();
    }

    private void endGame() {
        while (!tetromino.isValid(rectangles)) {
            tetromino.move(0, -1);
        }
        freeFall.pause();
        game.getChildren().add(gameOverText);
        play.setText("Play");
        play.setFont(Font.font("Bauhaus 93", 42));
        gameOver = true;
    }

    private void dropNextTetro() {
        nextTetro.removeFrom(panel);
        nextTetro.move(1, -4);
        if (nextTetro.getType() == 0) {
            nextTetro.move(0.5, 0);
        } else if (nextTetro.getType() == 6) {
            nextTetro.move(0.5, -0.5);
        }
        tetromino = nextTetro;
        tetromino.addTo(tetrominos);
        nextTetro = new Tetromino(order.get(nextIndex), side);
        nextTetro.move(-1, 4);
        if (nextTetro.getType() == 0) {
            nextTetro.move(-0.5, 0);
        } else if (nextTetro.getType() == 6) {
            nextTetro.move(-0.5, 0.5);
        }
        nextTetro.addTo(panel);
        if (++nextIndex > 13) {
            nextIndex = 0;
            Collections.shuffle(order);
        }
    }

    private int clearRows(ArrayList<Integer> rows, Pane tetrominos) {
        int rowCount = rows.size();
        for (int i = 0; i < 20; i++) {
            if (rowCount == 0) {
                break;
            }
            if (rows.contains(i)) {
                tetrominos.getChildren().removeAll(rectangles.get(i));
                rowCount--;
                continue;
            }
            Rectangle[] row = rectangles.get(i);
            for (int j = 0; j < 10; j++) {
                if (row[j] != null) {
                    row[j].setY(row[j].getY() + rowCount * side);
                }
            }
        }
        for (Integer i : rows) {
            rectangles.remove(i - rowCount);
            rowCount++;
        }
        for (int i = 0; i < rowCount; i++) {
            rectangles.addFirst(new Rectangle[10]);
        }
        return rowCount;
    }

    private ArrayList<Integer> checkRows(Set<Integer> rows) {
        ArrayList<Integer> rowsToClear = new ArrayList<>();
        for (Integer row : rows) {
            if (checkRow(row)) {
                rowsToClear.add(row);
            }
        }
        return rowsToClear;
    }

    private boolean checkRow(int row) {
        Rectangle[] r = rectangles.get(row);
        for (int i = 0; i < 10; i++) {
            if (r[i] == null) {
                return false;
            }
        }
        return true;
    }

    public void showTutorial() {
        Text line0 = new Text("Each Tetris block is called a tetromino");
        Text line1 = new Text("ENTER: start or pause game");
        Text line2 = new Text("↑: rotate tetromino clockwise");
        Text line3 = new Text("←/↓/→: move tetromino");
        Text line4 = new Text("SPACE: drop tetromino to bottom");
        Text line5 = new Text("Enjoy!");
        Button closeTutorial = new Button("Got it!");
        VBox vBox = new VBox(line0, line1, line2, line3, line4, line5, closeTutorial);
        vBox.setAlignment(Pos.CENTER);
        Scene tutorialScene = new Scene(vBox);
        tutorialScene.getStylesheets().add("tutorial.css");
        Stage tutorialStage = new Stage();
        tutorialStage.setScene(tutorialScene);
        tutorialStage.setTitle("Tetris Tutorial");
        tutorialStage.show();

        closeTutorial.setOnAction(e -> tutorialStage.close());
    }

    public static void main(String[] args) {
        launch(args);
    }
}