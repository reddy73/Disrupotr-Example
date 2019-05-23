package com.experiment.lmax;

import com.lmax.disruptor.EventFactory;

public class MessageFactory implements EventFactory<Stock> {
    public MessageFactory() {
    }

    public Stock newInstance() {
        return new Stock();
    }
}