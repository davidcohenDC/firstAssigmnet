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
    private final DirectoryWalker walker;
    private final AtomicBoolean isPrinting = new AtomicBoolean(false);
    private final int intervalInSeconds;
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    public DistributionPrinter(DirectoryWalker walker, int intervalInSeconds) {
        this.walker = walker;
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
        synchronized (this.walker.getDistribution()) {
            StringBuilder sb = new StringBuilder();
            IntStream.range(0, this.walker.getNumIntervals() + 1)
                    .map(i -> i * this.walker.getIntervalLength())
                    .forEach(start -> {
                        int end = (start + this.walker.getIntervalLength() - 1);
                        int interval = this.walker.getInterval(end);
                        List<Path> list = this.walker.getDistribution().readDistribution().getOrDefault(interval, Collections.emptyList());
                        if (start == this.walker.getMaxLines()) {
                            sb.append("[").append(start).append(",+∞]: ").append(list.size()).append("\n");
                        } else {
                            sb.append("[").append(start).append(",").append(end).append("]: ").append(list.size()).append("\n");
                        }
                    });
            return sb.toString();
        }
    }

    public String getMaxFilesString() {
        synchronized (this.walker.getDistribution()) {
            List<String> fileNames = this.walker.getDistribution().readDistribution()
                    .values()
                    .stream()
                    .flatMap(List::stream)
                    .limit(this.walker.getMaxFiles())
                    .map(Path::toString) // Convert Path objects to String
                    .collect(Collectors.toList());
            return String.join("\n", fileNames);
        }
    }
}
