package com.experiment.lmax;

public class Experiment {

    public static void main(String[] args) throws InterruptedException {
        int workers = 2;
        int ringBufferSize = 2;
        int iterations = 800;
        Dispatcher dispatcher = new Dispatcher(ringBufferSize, workers);
        for (int i = 0; i < iterations; i++) {
            Stock s = new Stock(i + 1);
            dispatcher.publish(s);
        }
        Thread.sleep(500);
    }

}
