package sample;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;

public class Controller {

    @FXML
    public Canvas canvas;

    public Affine affine;

    public Game game;
    public boolean pause;

    public Controller() {

        canvas = new Canvas(600, 800);

        affine = new Affine();
        affine.appendScale(1, 1);

        Simulation.study();
    }

    private void initKeyboardListener () {
        canvas.getScene().setOnKeyTyped(ke -> {
            char sigh = ke.getCharacter().charAt(0);
            switch (sigh) {
                case 'w' -> game.moveRacket(true, true);
                case 's' -> game.moveRacket(true, false);
                case 'u' -> game.moveRacket(false, true);
                case 'j' -> game.moveRacket(false, false);
            }
            switch (sigh) {
                case 'w' -> game.moveRacket(true, true);
                case 's' -> game.moveRacket(true, false);
                case 'u' -> game.moveRacket(false, true);
                case 'j' -> game.moveRacket(false, false);
            }
        });
    }

    public void init () {
        initKeyboardListener();
    }

    public void start () {
        game = new Game(true);
        draw();
        pause = false;
        new AppThread(this);
    }

    public void draw () {
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.setTransform(affine);

        // fill all Rectangle
        g.setFill(Color.BLACK);
        g.fillRect(0, 0, 800, 600);

        // draw ball
        g.setFill(Color.WHITE);
        g.fillRect(game.ballWidthPos - Game.BALL_RADIUS, game.ballHeightPos - Game.BALL_RADIUS, Game.BALL_RADIUS * 2, Game.BALL_RADIUS * 2);

        // draw rackets
        g.fillRect(0, game.leftRacketHeight, Game.RACKET_WIDTH, Game.RACKET_HEIGHT);
        g.fillRect(Game.MAX_WIDTH - Game.RACKET_WIDTH, game.rightRacketHeight, Game.RACKET_WIDTH, Game.RACKET_HEIGHT);

    }

}
