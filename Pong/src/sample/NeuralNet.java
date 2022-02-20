package sample;

import java.io.*;
import java.util.function.UnaryOperator;

public class NeuralNet implements Serializable {

    private Layer[] layers;
    private final UnaryOperator<Double> activation;
    private final UnaryOperator<Double> derivative;

    public NeuralNet (UnaryOperator<Double> activation, UnaryOperator<Double> derivative, int... sizes) {
        this.activation = activation;
        this.derivative = derivative;
        layers = new Layer[sizes.length];

        for (int i = 0;i < sizes.length;i++) {
            int nextSize = 0;
            if (i < sizes.length - 1) {
                nextSize = sizes[i + 1];
            }
            layers[i] = new Layer(sizes[i], nextSize);
            for (int j = 0;j < sizes[i];j++) {
                layers[i].biases[j] = Math.random() * 2.0 - 1.0;
                for (int k = 0;k < nextSize;k++) {
                    layers[i].weights[j][k] = Math.random() * 2.0 - 1.0;
                }
            }
        }
    }

    public NeuralNet(NeuralNet parent1, NeuralNet parent2) {
        this.activation = parent1.activation;
        this.derivative = parent1.derivative;
        layers = new Layer[parent1.layers.length];

        int[] sizes = new int[layers.length];
        for (int i = 0;i < layers.length;i++) {
            sizes[i] = parent1.layers[i].size;
        }

        for (int i = 0;i < sizes.length;i++) {
            int nextSize = 0;
            if (i < sizes.length - 1) {
                nextSize = sizes[i + 1];
            }
            layers[i] = new Layer(sizes[i], nextSize);
            for (int j = 0;j < sizes[i];j++) {
                layers[i].biases[j] = getRandomOf(parent1.layers[i].biases[j], parent2.layers[i].biases[j]);
                for (int k = 0;k < nextSize;k++) {
                    layers[i].weights[j][k] = getRandomOf(parent1.layers[i].weights[j][k], parent2.layers[i].weights[j][k]);
                }
            }
        }
    }

    public NeuralNet (UnaryOperator<Double> activation, UnaryOperator<Double> derivative) {
        this.activation = activation;
        this.derivative = derivative;

        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream("neurons_data.txt"))) {
            this.layers = (Layer[]) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public double[] feedForward (double[] inputs) {
        System.arraycopy(inputs, 0, layers[0].neurons, 0, inputs.length);
        for (int i = 1;i < layers.length;i++) {
            Layer prefLayer = layers[i - 1];
            Layer currLayer = layers[i];
            for (int j = 0;j < currLayer.size;j++) {
                currLayer.neurons[j] = 0;
                for (int k = 0;k < prefLayer.size;k++) {
                    currLayer.neurons[j] += prefLayer.neurons[k] * prefLayer.weights[k][j];
                }
                currLayer.neurons[j] += currLayer.biases[j];
                currLayer.neurons[j] = activation.apply(currLayer.neurons[j]);
            }
        }
        return layers[layers.length - 1].neurons;
    }

    public void serialize () throws IOException {
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("neurons_data.txt"))) {
            oos.writeObject((Layer[])this.layers);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public double getRandomOf (double value1, double value2) {
        if (Math.random() < 0.001) {
            return Math.random() * 2.0 - 1.0;
        }
        if (Math.random() < 0.5) {
            return value1;
        } else {
            return value2;
        }
    }
}
