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

public class DirectoryWalker implements Walker {
    private final Path directory;
    final Distribution<Integer, Path> distribution;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final DistributionMapUpdater updater;
    private final DistributionPrinter printer;
    private final DirectoryWalkerParams params;
    private final Semaphore threadSemaphore;

    public DirectoryWalker(Path dir, int maxFiles, int numIntervals, int maxLength, Distribution<Integer, Path> distribution, int maxThreads) {
        this.directory = dir;
        this.distribution = distribution;
        this.params = DirectoryWalkerParams.builder()
                .directory(dir)
                .maxFiles(maxFiles)
                .numIntervals(numIntervals)
                .maxLines(maxLength)
                .distribution(distribution)
                .build();
        this.printer = new DistributionPrinter(this.params, (int) TimeUnit.SECONDS.toSeconds(1));
        this.updater = new DistributionMapUpdater(this.distribution);
        this.threadSemaphore = new Semaphore(maxThreads);
    }

    @Override
    public boolean walk() {
        Thread thread = new Thread(() -> {
            try {
                walkRec(this.directory);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            this.isRunning.set(true);
        });
        thread.start();

        this.printer.startPrinting();

        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        this.printer.stopPrinting();

        System.out.println("\nThe " + this.params.getMaxFiles() + " files with the highest number of lines are: \n" + this.printer.getMaxFilesString());
        System.out.println("\nThe distribution of files is:\n" + this.printer.getDistributionString());

        return true;
    }

    @Override
    public void stop() {
        this.isRunning.set(false);
        this.printer.stopPrinting();
    }

    /**
     * It will walk recursively through the directory and add the files to the distribution map
     * If the file has more lines than the max lines, it will be in a new interval that goes from the max lines to infinity
     * @param directory the directory to walk
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the thread is interrupted
     */
    private void walkRec(Path directory) throws IOException, InterruptedException {
        System.out.println("Walking " + directory);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            for (Path path : stream) {
                try {
                    if (Files.isRegularFile(path) && path.getFileName().toString().endsWith(".java")) {
                        threadSemaphore.acquire();
                        new Thread(() -> {
                            try {
                                try {
                                    updater.processFile(params.getInterval(WalkerUtils.countLines(path)), path);
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


    /**
     * Returns a defensive copy of the `DirectoryWalkerParams` object.
     */
    public DirectoryWalkerParams getParams() {
            return DirectoryWalkerParams.builder()
                    .directory(this.params.getDirectory())
                    .maxFiles(this.params.getMaxFiles())
                    .numIntervals(this.params.getNumIntervals())
                    .maxLines(this.params.getMaxLines())
                    .distribution(this.params.getDistribution())
                    .build();
        }
}