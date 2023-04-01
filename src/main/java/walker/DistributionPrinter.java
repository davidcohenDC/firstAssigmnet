package walker;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DistributionPrinter implements Runnable {
    private final DirectoryWalkerParams params;
    private final AtomicBoolean isPrinting = new AtomicBoolean(false);
    private final int intervalInSeconds;
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private List<Pair<Integer, Integer>> intervalRanges;
    private boolean isIntervalRangesDirty = true;

    public DistributionPrinter(DirectoryWalkerParams params, int intervalInSeconds) {
        this.params = params;
        this.intervalInSeconds = intervalInSeconds;
    }

    private void updateIntervalRangesIfNeeded() {
        if (isIntervalRangesDirty) {
            this.intervalRanges = calculateIntervalRanges();
            isIntervalRangesDirty = false;
        }
    }

    private List<Pair<Integer, Integer>> calculateIntervalRanges() {
        int numIntervals = this.params.getNumIntervals();
        int intervalLength = this.params.getIntervalLength();
        int maxLines = this.params.getMaxLines();

        List<Pair<Integer, Integer>> intervalRanges = IntStream.range(0, numIntervals)
                .mapToObj(i -> Pair.of(i * intervalLength, (i + 1) * intervalLength - 1))
                .collect(Collectors.toList());

        intervalRanges.add(Pair.of(numIntervals * intervalLength, maxLines));

        return intervalRanges;
    }

    public void startPrinting() {
        if (isPrinting.get()) {
            // Already printing
            return;
        }

        isPrinting.set(true);
        executorService.scheduleAtFixedRate(this, 0, intervalInSeconds, TimeUnit.SECONDS);
    }

    public void stopPrinting() {
        isPrinting.set(false);
        executorService.shutdown();
    }

    @Override
    public void run() {
        System.out.println(this.getDistributionString());
    }

    public String getDistributionString() {
        synchronized (this.params.getDistribution()) {
            updateIntervalRangesIfNeeded();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < intervalRanges.size(); i++) {
                int start = intervalRanges.get(i).getLeft();
                int end = intervalRanges.get(i).getRight();
                List<Path> list = this.params.getDistribution().readDistribution().getOrDefault(i, Collections.emptyList());
                if (start == this.params.getMaxLines()) {
                    sb.append("[").append(start).append(",+∞]: ").append(list.size()).append("\n");
                } else {
                    sb.append("[").append(start).append(",").append(end).append("]: ").append(list.size()).append("\n");
                }
            }
            return sb.toString();
        }
    }

    public String getMaxFilesString() {
        synchronized (this.params.getDistribution()) {
            StringBuilder sb = new StringBuilder();
            this.params.getDistribution().readDistribution()
                    .values()
                    .stream()
                    .flatMap(List::stream)
                    .limit(this.params.getMaxFiles())
                    .map(Path::toString) // Convert Path objects to String
                    .forEach(fileName -> sb.append(fileName).append("\n"));
            return sb.toString();
        }
    }
}
