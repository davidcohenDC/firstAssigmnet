package walker;

import boundedbuffer.Distribution;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class DirectoryWalker extends AbstractDirectoryWalker {
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final DistributionMapUpdater updater;
    private final DistributionPrinter printer;
    private final Semaphore threadSemaphore;

    public DirectoryWalker(Path dir, int maxFiles, int numIntervals, int maxLength, Distribution<Integer, Path> distribution, int maxThreads) {
        super(dir, maxFiles, numIntervals, maxLength, distribution);
        this.printer = new DistributionPrinter(this.params, (int) TimeUnit.SECONDS.toSeconds(1));
        this.updater = new DistributionMapUpdater(this.distribution);
        this.threadSemaphore = new Semaphore(maxThreads);
    }

    @Override
    public void stop() {
        this.isRunning.set(false);
        this.printer.stopPrinting();
    }

    @Override
    protected void beforeWalk() {
        this.printer.startPrinting();
    }

    @Override
    protected void afterWalk() {
        this.printer.stopPrinting();
        System.out.println("\nThe " + this.params.getMaxFiles() + " files with the highest number of lines are: \n" + this.printer.getMaxFilesString());
        System.out.println("\nThe distribution of files is:\n" + this.printer.getDistributionString());
    }

    @Override
    protected void walkRec(Path directory) throws IOException, InterruptedException {
        System.out.println("Walking " + directory);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            for (Path path : stream) {
                try {
                    if (Files.isRegularFile(path) && path.getFileName().toString().endsWith(".java")) {
                        threadSemaphore.acquire();
                        new Thread(() -> {
                            try {
                                try {
                                    this.updater.processFile(params.getInterval(WalkerUtils.countLines(path)), path);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            } finally {
                                threadSemaphore.release();
                            }
                        }).start();
                    } else if (Files.isDirectory(path) && !Files.isHidden(path)) {
                        walkRec(path);
                    }
                } catch (AccessDeniedException e) {
                    System.out.println("Access denied to " + path);
                }
            }
        }
    }

}