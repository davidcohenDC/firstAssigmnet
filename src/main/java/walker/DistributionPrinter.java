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

    public DistributionPrinter(DirectoryWalkerParams params, int intervalInSeconds) {
        this.params = params;
        this.intervalInSeconds = intervalInSeconds;
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
            StringBuilder sb = new StringBuilder();
            IntStream.range(0, this.params.getNumIntervals() + 1)
                    .map(i -> i * this.params.getIntervalLength())
                    .forEach(start -> {
                        int end = (start + this.params.getIntervalLength() - 1);
                        int interval = start / this.params.getIntervalLength();
                        List<Path> list = this.params.getDistribution().readDistribution().getOrDefault(interval, Collections.emptyList());
                        if (start == this.params.getMaxLines()) {
                            sb.append("[").append(start).append(",+∞]: ").append(list.size()).append("\n");
                        } else {
                            sb.append("[").append(start).append(",").append(end).append("]: ").append(list.size()).append("\n");
                        }
                    });
            return sb.toString();
        }
    }

    public String getMaxFilesString() {
        synchronized (this.params.getDistribution()) {
            List<String> fileNames = this.params.getDistribution().readDistribution()
                    .values()
                    .stream()
                    .flatMap(List::stream)
                    .limit(this.params.getMaxFiles())
                    .map(Path::toString) // Convert Path objects to String
                    .collect(Collectors.toList());
            return String.join("\n", fileNames);
        }
    }
}
