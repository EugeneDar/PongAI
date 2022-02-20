package sample;

import java.util.function.UnaryOperator;

public class Game {

    final static int MAX_HEIGHT = 600;
    final static int MAX_WIDTH = 800;
    final static int RACKET_WIDTH = 15;
    final static int RACKET_HEIGHT = 150;
    final static int BALL_RADIUS = 15;
    final static int RACKET_SPEED = 6;

    final static double SPEED_BOOST = 1.075;

    int leftRacketHeight;
    int rightRacketHeight;
    double speedX, speedY;
    double ballHeightPos, ballWidthPos;

    boolean gameOver = false;
    int steps = 0;

    boolean isPlayer = false;
    NeuralNet nn;

    public Game() {
        ballHeightPos = (int)(MAX_HEIGHT / 2);
        ballWidthPos = (int)(MAX_WIDTH / 2);
        speedX = 6.0;
        speedY = Math.random() * 10.0 - 5.0;
        if (Math.abs(speedY) < 1.0) {
            speedY *= 5.0;
        }
        leftRacketHeight = MAX_HEIGHT / 3 * 2;
        rightRacketHeight = MAX_HEIGHT / 3 * 2;

        UnaryOperator<Double> sigmoid = x -> 1 / (1 + Math.exp(-x));
        UnaryOperator<Double> dsigmoid = y -> y * (1 - y);
        //nn = new NeuralNet(sigmoid, dsigmoid, 6, 3);
        nn = new NeuralNet(sigmoid, dsigmoid);
    }

    public Game(boolean isPlayer) {
        ballHeightPos = (int)(MAX_HEIGHT / 2);
        ballWidthPos = (int)(MAX_WIDTH / 2);
        speedX = 6.0;
        speedY = Math.random() * 10.0 - 5.0;
        if (Math.abs(speedY) < 1.0) {
            speedY *= 5.0;
        }
        leftRacketHeight = MAX_HEIGHT / 3 * 2;
        rightRacketHeight = MAX_HEIGHT / 3 * 2;

        UnaryOperator<Double> sigmoid = x -> 1 / (1 + Math.exp(-x));
        UnaryOperator<Double> dsigmoid = y -> y * (1 - y);
//        nn = new NeuralNet(sigmoid, dsigmoid, 6, 5, 3);
        nn = new NeuralNet(sigmoid, dsigmoid);

        this.isPlayer = isPlayer;
    }

    public void step () {

        // steps++
        steps++;

        // AI move
        if (isPlayer) {
            double[] inputs = new double[]{
                    ballHeightPos, ballWidthPos, leftRacketHeight,
                    rightRacketHeight, speedY, speedX};
            double[] outputs = nn.feedForward(inputs);
            double maxOutput = Math.max(outputs[0], Math.max(outputs[1], outputs[2]));
            if (outputs[0] == maxOutput) {
                moveRacket(!isPlayer, true);
            } else if (outputs[2] == maxOutput) {
                moveRacket(!isPlayer, false);
            }
        }

        // check walls touching
        if (ballHeightPos + Game.BALL_RADIUS >= Game.MAX_HEIGHT - 1 || ballHeightPos - Game.BALL_RADIUS <= 0) {
            speedY = -speedY;
        }

        // Right wall for AI
        if (!isPlayer && ballWidthPos - Game.BALL_RADIUS * 2 - Math.abs(speedX) <= 0) {
            speedX = -speedX;
            speedX *= SPEED_BOOST;
            ballWidthPos += speedX;
        }

        // check racket touching
        if ((ballHeightPos >= leftRacketHeight - BALL_RADIUS && ballHeightPos <= leftRacketHeight + RACKET_HEIGHT + BALL_RADIUS) &&
                (ballWidthPos - BALL_RADIUS >= 0 && ballWidthPos - BALL_RADIUS <= RACKET_WIDTH + Math.abs(speedX))) {
            speedX *= SPEED_BOOST;
            speedY *= SPEED_BOOST;
            speedX = -speedX;
            //changeAngle(1.25 - Math.abs( (leftRacketHeight + RACKET_HEIGHT / 2.0) - ballHeightPos) / RACKET_HEIGHT);
        }
        if ((ballHeightPos >= rightRacketHeight - BALL_RADIUS && ballHeightPos <= rightRacketHeight + RACKET_HEIGHT + BALL_RADIUS) &&
                (ballWidthPos + BALL_RADIUS <= MAX_WIDTH && MAX_WIDTH - (ballWidthPos + BALL_RADIUS) <= RACKET_WIDTH + Math.abs(speedX))) {
            speedX *= SPEED_BOOST;
            speedY *= SPEED_BOOST;
            speedX = -speedX;
            //changeAngle(1.25 - Math.abs( (rightRacketHeight + RACKET_HEIGHT / 2.0) - ballHeightPos) / RACKET_HEIGHT);
        }

        // move ball
        ballWidthPos += speedX;
        ballHeightPos += speedY;

        // Check game over
        String res = checkGameOver();
        if (res.equals("left")) {
            gameOver = true;
        } else if (res.equals("right")) {
            gameOver = true;
        }
    }

    public void moveRacket (boolean left, boolean toUp) {
        if (left && toUp) {
            leftRacketHeight = Math.max(0, leftRacketHeight - Game.RACKET_SPEED);
        } else if (left && !toUp) {
            leftRacketHeight = Math.min(Game.MAX_HEIGHT - Game.RACKET_HEIGHT- 1, leftRacketHeight + Game.RACKET_SPEED);
        } else if (!left && toUp) {
            rightRacketHeight = Math.max(0, rightRacketHeight - Game.RACKET_SPEED);
        } else if (!left && !toUp) {
            rightRacketHeight = Math.min(Game.MAX_HEIGHT - Game.RACKET_HEIGHT- 1, rightRacketHeight + Game.RACKET_SPEED);
        }
    }

    public String checkGameOver () {
        if (ballWidthPos - Game.BALL_RADIUS <= 0) {
            return "right";
        } else if (ballWidthPos + Game.BALL_RADIUS >= Game.MAX_WIDTH) {
            return "left";
        } else {
            return "nope";
        }
    }

    private void changeAngle (double parameter) {
        //System.out.println(parameter);
        double pastY = speedY;
        double res = Math.sqrt(speedX * speedX + speedY * speedY);
        speedX *= parameter;
        speedY = Math.sqrt(Math.abs(res * res - speedX * speedX));
        if (pastY < 0) {
            speedY = -speedY;
        }
        //System.out.println(speedX + " " + speedY);
    }
}
