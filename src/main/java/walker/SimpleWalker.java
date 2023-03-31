package walker;

import boundedbuffer.BoundedBufferMap;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SimpleWalker implements Walker {
    private final Path directory;
    private final int maxFiles;
    private final int numIntervals;
    private final int maxLines;
    private final int intervalLength;
    private final BoundedBufferMap<Integer, List<Path>> distribution;
    private volatile boolean isRunning = true;
    private volatile boolean isPrinting = false;
    private Thread printThread;
    private final boolean debug;

    public SimpleWalker(Path dir, int maxFiles, int numIntervals, int maxLength, BoundedBufferMap<Integer, List<Path>> distribution, boolean debug) {
        this.directory = dir;
        this.maxFiles = maxFiles;
        this.numIntervals = numIntervals;
        this.maxLines = maxLength;
        this.intervalLength = maxLength / numIntervals;
        this.distribution = distribution;
        this.debug = debug;
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

        startPrintThread();

        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        stopPrintThread();
        if(debug) {
            System.out.println("\nThe " + this.maxFiles + " files with the highest number of lines are: \n" + this.getMaxFilesString());
            System.out.println("\nThe distribution of files is:\n" + this.getDistributionString());
        }
        return true;
    }

    public String getMaxFilesString() {
        List<String> fileNames = this.distribution.getMap()
                .values()
                .stream()
                .flatMap(List::stream)
                .limit(this.maxFiles).map(Path::toString)
                .collect(Collectors.toList());
        return String.join("\n", fileNames);
    }

    public String getDistributionString() {
        StringBuilder sb = new StringBuilder();
        IntStream.range(0, this.numIntervals+1)
                .map(i -> i * this.intervalLength)
                .forEach(start -> {
                    int end = (start + this.intervalLength - 1);
                    int interval = getInterval(end);
                    List<Path> list = this.distribution.getMap().getOrDefault(interval, Collections.emptyList());
                    if(start == this.maxLines) {
                        sb.append("[").append(start).append(",+∞]: ").append(list.size()).append("\n");
                    } else {
                        sb.append("[").append(start).append(",").append(end).append("]: ").append(list.size()).append("\n");
                    }
                });
        return sb.toString();
    }

    @Override
    public void stop() {
        isRunning = false;
    }

    @Override
    public void resume() {
        isRunning = true;
        startPrintThread();
    }


    /**
     * Start the print thread
     * It will print the distribution every second
     * It will be interrupted when the walker is done
     */
    private void startPrintThread() {
        if (isPrinting) {
            // Already printing
            return;
        }

        isPrinting = true;
        printThread = new Thread(() -> {
            while (isPrinting) {
                if (debug) {
                    System.out.println(getDistributionString());
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        });
        printThread.start();
    }

    /**
     * Stop the print thread
     * It will wait for the thread to finish
     * If the thread is interrupted, it will print the stack trace
     * It will be interrupted when the walker is done
     */
    private void stopPrintThread() {
        isPrinting = false;
        printThread.interrupt();
        try {
            printThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
                    try (Stream<String> fileStream = Files.lines(path)) {
                        numberOfLines = (int) fileStream.count();
                    }

                    synchronized (distribution) {
                        int interval = getInterval(numberOfLines);
                        if (distribution.containsKey(interval)) {
                            distribution.get(interval).add(path);
                        } else {
                            List<Path> list = new ArrayList<>();
                            list.add(path);
                            distribution.put(interval, list);
                        }
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
    private Integer getInterval(int numberOfLines) {
        if(numberOfLines > this.maxLines) {
            return this.numIntervals;
        }
        return numberOfLines / (this.maxLines / this.numIntervals);
    }


}