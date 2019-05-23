package com.experiment.lmax;

import com.lmax.disruptor.*;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Dispatcher {
    private final WorkerPool<Stock> workerPool;
    private final MessageWorkerHandler[] schedulers;
    private final EventFactory<Stock> EVENT_FACTORY = new MessageFactory();
    private RingBuffer<Stock> ringBuffer;
    private long dispatched = 0;

    public Dispatcher(int ringBufferSize, int workerSize) {

        ringBuffer = RingBuffer.createSingleProducer(EVENT_FACTORY,
                ringBufferSize,
                new YieldingWaitStrategy());

        schedulers = new MessageWorkerHandler[workerSize];
        for (int i = 0; i < workerSize; i++) {
            schedulers[i] = new MessageWorkerHandler(i);
        }

        workerPool = new WorkerPool<Stock>(ringBuffer,
                ringBuffer.newBarrier(),
                new FatalExceptionHandler(),
                schedulers);

        ringBuffer.addGatingSequences(workerPool.getWorkerSequences());
        Executor executor = Executors.newFixedThreadPool(workerSize);
        workerPool.start(executor);
    }

    public boolean publish(Stock stock) {
        while (true) {
            try {
                final long sequence = ringBuffer.tryNext();
                Stock stk = ringBuffer.get(sequence);
                stk.setStockId(stock.getStockId());
                if (stock.getStockId() % 2 == 0)
                    stk.setStockName("TCS");
                else
                    stk.setStockName("OTHER");
                ringBuffer.publish(sequence);
                dispatched++;
                return true;
            } catch (InsufficientCapacityException e) {
                waitForSlot();
                continue;
            }
        }
    }

    private void waitForSlot() {
        try {
            Thread.sleep(50);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
    }

    public void clear() {
        workerPool.drainAndHalt();
    }

    public class MessageWorkerHandler implements WorkHandler<Stock> {
        private int id = 0;
        private long consumed = 0;

        public MessageWorkerHandler(int id) {
            this.id = id;
        }

        public void onEvent(Stock event) throws Exception {
            if (event.getStockName().equalsIgnoreCase("TCS")) {
                consumed++;
                System.out.println("Worker[" + id + "] consumed [" + consumed + "] Stock " +
                        "where Stock[id: " + event.getStockId() + ", instance: " + event + "].");
                Thread.sleep(500);
            }
            // DO WORK HERE...
        }

    }

}
