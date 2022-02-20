package sample;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.UnaryOperator;

public class Simulation {

    final static int AMOUNT_OF_GAME_SIMULATION = 7;

    private static class Pair implements Comparable<Object>{
        long score;
        NeuralNet neuralNet;

        public Pair(long score, NeuralNet neuralNet) {
            this.score = score;
            this.neuralNet = neuralNet;
        }

        @Override
        public int compareTo(Object pair) {
            return (int) (((Pair)pair).score - this.score);
        }
    }

    public static void study () {
        UnaryOperator<Double> sigmoid = x -> 1 / (1 + Math.exp(-x));
        UnaryOperator<Double> dsigmoid = y -> y * (1 - y);

        int populationSize = 5000;
        int amountLoops = 15;

        List<NeuralNet> neuralNets = new ArrayList<>();
        for (int i = 0;i < populationSize - 1;i++) {
            neuralNets.add(new NeuralNet(sigmoid, dsigmoid, 6, 3));
        }
        neuralNets.add(new NeuralNet(sigmoid, dsigmoid));

        while (amountLoops-- > 0) {

            // Simulate games
            amountWins = 0;
            List<Pair> bestNets = new ArrayList<>();
            for (NeuralNet nn : neuralNets) {
                /// I just simulate the game 10 times
                int sumSCore = 0;
                int minScore = Integer.MAX_VALUE - 1;
                for (int i = 0;i < AMOUNT_OF_GAME_SIMULATION;i++) {
                    int currScore = simulateGame(nn);
                    minScore = Math.min(minScore, currScore);
                    sumSCore += currScore;
                }
                bestNets.add(new Pair((minScore + sumSCore / 3) / AMOUNT_OF_GAME_SIMULATION, nn));
            }
            Collections.sort(bestNets);

            // Print best result
            System.out.println("Res: " + amountWins + "      " + "Will amount: " + amountLoops);
            System.out.println("Best: " + (int)(bestNets.get(0).score * 0.017));

            // Serialize best
            try {
                bestNets.get(0).neuralNet.serialize();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Make children
            neuralNets.clear();
            for (int i = 0;i < populationSize - 1;i++) {
                neuralNets.add(new NeuralNet(
                        getRandomBest(bestNets, populationSize),
                        getRandomBest(bestNets, populationSize)
                ));
            }
            neuralNets.add(bestNets.get(0).neuralNet);
        }
    }

    private static int amountWins = 0;

    private static int simulateGame (NeuralNet neuralNet) {
        Game game = new Game();
        while (true) {
            if (game.steps > Integer.MAX_VALUE / 10_000) {
                return Integer.MAX_VALUE;
            }
            String res = game.checkGameOver();
            if (res.equals("right")) {
                amountWins++;
                return Integer.MAX_VALUE - 1;
            } else if (res.equals("left")) {
                return game.steps;
            }

            double[] inputs = new double[]{
                    game.ballHeightPos, game.ballWidthPos, game.leftRacketHeight,
                    game.rightRacketHeight, game.speedY, game.speedX};

            double[] outputs = neuralNet.feedForward(inputs);
            double maxOutput = Math.max(outputs[0], Math.max(outputs[1], outputs[2]));
            if (outputs[0] == maxOutput) {
                game.moveRacket(false, true);
            } else if (outputs[2] == maxOutput) {
                game.moveRacket(false, false);
            }

            game.step();
        }
    }

    private static NeuralNet getRandomBest(List<Pair> list, int populationSize) {
        List<Pair> selectedList = new ArrayList<>();
        for (int i = 0;i < populationSize / 20;i++) {
            int index = (int) (Math.random() * populationSize);
            selectedList.add(list.get(index));
        }
        Collections.sort(selectedList);
        return selectedList.get(0).neuralNet;
    }



}
