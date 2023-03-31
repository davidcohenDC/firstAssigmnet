package walker;

import boundedbuffer.Distribution;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

//TODO SRP for all todo
//TODO Walking through a directory recursively
//TODO Reading the contents of files and counting the number of lines
//TODO Maintaining a distribution of files based on their line counts
//TODO Printing the distribution to the console
//TODO Starting and stopping a print thread
public class DirectoryWalker implements Walker {
    private final Path directory;
    protected final int maxFiles;
    final int numIntervals;
    final int maxLines;
    final int intervalLength;
    final Distribution<Integer, Path> distribution;
    private volatile boolean isRunning = true;
    private final DistributionPrinter printer;
    private final boolean debug;

    public DirectoryWalker(Path dir, int maxFiles, int numIntervals, int maxLength, Distribution<Integer, Path> distribution, boolean debug) {
        this.directory = dir;
        this.maxFiles = maxFiles;
        this.numIntervals = numIntervals;
        this.maxLines = maxLength;
        this.intervalLength = maxLength / numIntervals;
        this.distribution = distribution;
        this.debug = debug;
        this.printer = new DistributionPrinter(this, (int) TimeUnit.SECONDS.toSeconds(1));
    }

    @Override
    public boolean walk() {
        Thread thread = new Thread(() -> {
            try {
                walkRec(this.directory);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            isRunning = false;
        });
        thread.start();

        printer.startPrinting();

        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        printer.stopPrinting();
        if(debug) {
            System.out.println("\nThe " + this.maxFiles + " files with the highest number of lines are: \n" + this.printer.getMaxFilesString());
            System.out.println("\nThe distribution of files is:\n" + this.printer.getDistributionString());
        }
        return true;
    }

    @Override
    public void stop() {
        isRunning = false;
        this.printer.stopPrinting();
    }

    @Override
    public void resume() {
        isRunning = true;
        this.printer.startPrinting();
    }

    @Override
    public Distribution<Integer, Path> getDistribution() {
        return this.distribution;
    }

    /**
     * Core of the walker
     * It will walk recursively through the directory and add the files to the distribution map
     * If the file has more lines than the max lines, it will be in a new interval that goes from the max lines to infinity
     * @param directory the directory to walk
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the thread is interrupted
     */
    private void walkRec(Path directory) throws IOException, InterruptedException {
        if(debug) {
            System.out.println("Walking " + directory);
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            for (Path path : stream) {
                int numberOfLines;
                if (Files.isRegularFile(path) && path.getFileName().toString().endsWith(".java")) {
                    numberOfLines = WalkerUtils.countLines(path);
                    synchronized (distribution) {
                        int interval = getInterval(numberOfLines);
                        this.distribution.writeInterval(interval,path);
                    }
                } else if (Files.isDirectory(path) && !Files.isHidden(path)) {
                    if (isRunning) {
                        walkRec(path);
                    }
                }
            }
        }
    }

    /**
     * Get the interval of the file
     * If the file has more lines than the max lines, it will be in a new interval that goes from the max lines to infinity
     * @param numberOfLines the max length of the file
     * @return the interval
     */
    Integer getInterval(int numberOfLines) {
        if(numberOfLines > this.maxLines) {
            return this.numIntervals;
        }
        return numberOfLines / (this.maxLines / this.numIntervals);
    }

    public int getNumIntervals() {
        return this.numIntervals;
    }

    public int getIntervalLength() {
        return this.intervalLength;
    }

    public int getMaxLines() {
        return this.maxLines;
    }

    public int getMaxFiles() {
        return this.maxFiles;
    }
}