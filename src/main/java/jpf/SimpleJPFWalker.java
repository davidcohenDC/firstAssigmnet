package jpf;

import walker.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

public class SimpleJPFWalker {

    private final int maxLines;

    private final int numIntervals;

    private final Semaphore semaphore = new Semaphore(2);

    private final Map<Integer, List<String>> simpleDistribution = new ConcurrentHashMap<>();

    private final List<Pair<Integer,String>> filesWithLines = new ArrayList<>();

    private Thread walkerAgent;

    private Thread printerAgent;

    private boolean walkerFinished = false;

    public SimpleJPFWalker(int numIntervals, int maxLines) {
        this.numIntervals = numIntervals;
        this.maxLines = maxLines;
        this.populateFiles();
        this.createWorkers();
    }

    private void createWorkers() {
        this.walkerAgent = new Thread(() -> {
            for (Pair<Integer,String> fileWithLines : this.filesWithLines) {
                int interval;
                if (fileWithLines.getX() > this.maxLines) {
                    interval = this.numIntervals;
                } else {
                    interval = fileWithLines.getX() / (this.maxLines / this.numIntervals);
                }

                int finalInterval = interval;
                try {
                    this.semaphore.acquire();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Thread processingThread = new Thread(() -> {
                    synchronized (this.simpleDistribution) {
                        if(fileWithLines.getY().endsWith(".java")) {
                            List<String> files = this.simpleDistribution.computeIfAbsent(finalInterval, k -> new ArrayList<>());
                            files.add(fileWithLines.getY());
                        }
                    }
                });
                processingThread.start();
                this.semaphore.release();
            }
        });


        this.printerAgent = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(10);
                    synchronized (this.simpleDistribution) {
                        System.out.println(this.simpleDistribution);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    public void walk() {
        //printer worker
        this.printerAgent.start();
        //walker walker
        this.walkerAgent.start();
            try {
                printerAgent.join();
                walkerAgent.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        synchronized (simpleDistribution) {
            System.out.println(this.simpleDistribution);
        }

    }

    private void populateFiles() {
        this.filesWithLines.add(new Pair<>(30,"PCD/src/"));
        this.filesWithLines.add(new Pair<>(50,"PCD/src/main.java"));
        this.filesWithLines.add(new Pair<>(150,"PCD/test/test.java"));
    }
}
