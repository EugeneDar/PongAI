package sample;

public class AppThread extends Thread{
    Controller controller;

    public AppThread(Controller controller) {
        this.controller = controller;
        start();
    }

    public void run () {
        while (!controller.game.gameOver) {
            try {
                // best = 17 millis
                Thread.sleep(17);
                controller.game.step();
                controller.draw();
            } catch (Exception ignore) {}
        }
    }
}
