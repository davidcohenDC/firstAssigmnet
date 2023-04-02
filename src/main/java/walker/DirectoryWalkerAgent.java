package walker;

import boundedbuffer.Distribution;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * the DirectoryWalkerAgent class is a directory walker that recursively traverses a specified directory and processes
 * all Java files found within it. The class uses a distribution object to keep track of the number of lines in each
 * Java file, and provides methods for printing out various statistics and information about the files processed.
 */
public class DirectoryWalkerAgent extends AbstractDirectoryWalker {

    private final DistributionMapUpdater updater;
    private final DistributionPrinter printer;
    private final Semaphore threadSemaphore;

    public DirectoryWalkerAgent(Path dir, int maxFiles, int numIntervals, int maxLength, Distribution<Integer, Path> distribution, int maxThreads) {
        super(dir, maxFiles, numIntervals, maxLength, distribution);
        this.printer = new DistributionPrinter(this.params, (int) TimeUnit.SECONDS.toSeconds(1));
        this.updater = new DistributionMapUpdater(this.distribution);
        this.threadSemaphore = new Semaphore(maxThreads);
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
        if (!this.isRunning.get()) return;
        System.out.println("Walking " + directory);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            for (Path path : stream) {
                try {
                    if (this.isRegularJavaFile(path)) {
                        this.createNewProcessingThread(path);
                    } else if (this.isDirectoryNotHidden(path)) {
                        this.walkRec(path);
                    }
                } catch (AccessDeniedException e) {
                    System.out.println("Access denied to " + path);
                }
            }
        }
    }

    @Override
    protected void stopBehaviour() {
        this.printer.stopPrinting();
    }

    private boolean isRegularJavaFile(Path path) {
        return Files.isRegularFile(path) && path.getFileName().toString().endsWith(".java");
    }

    private boolean isDirectoryNotHidden(Path path) throws IOException {
        return Files.isDirectory(path) && !Files.isHidden(path);
    }

    private void createNewProcessingThread(Path path) throws InterruptedException {
        this.threadSemaphore.acquire();
        new Thread(() -> {
            try {
                try {
                    this.updater.processFile(this.params.getInterval(WalkerUtils.countLines(path)), path);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } finally {
                this.threadSemaphore.release();
            }
        }).start();
    }

}