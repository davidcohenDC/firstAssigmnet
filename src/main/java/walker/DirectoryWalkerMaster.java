package walker;

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
public class DirectoryWalkerMaster extends AbstractDirectoryWalker {

    private static final int NUMBER_CPU = Runtime.getRuntime().availableProcessors();
    private static final int UTILIZATION_CPU = 1;
    private static final int CONSTANT = 1;
    private static final int WAIT_TIME = 1;
    private static final int COMPUTE_TIME = 1;

    private final DistributionPrinter printer;
    private final Semaphore semaphore;

    public DirectoryWalkerMaster(DirectoryWalkerParams params) {
        super(params);
        this.printer = new DistributionPrinter(this.params, (int) TimeUnit.SECONDS.toSeconds(1));
        int maxThread = this.numberThread(NUMBER_CPU, UTILIZATION_CPU, WAIT_TIME, COMPUTE_TIME);
        this.semaphore = new Semaphore(maxThread);
        //TODO check PERFORMANCE
        System.out.println("Number of available processors: " + Runtime.getRuntime().availableProcessors());
        System.out.println("Number of thread: " + maxThread);
    }

    private int numberThread(int numberCPU, int utilizationCPU, int waitTime, int computeTime) {
        return numberCPU * utilizationCPU * (CONSTANT + waitTime/computeTime);
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
        //System.out.println("Walking " + directory); //TODO
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

    private void createNewProcessingThread(Path path) throws InterruptedException, IOException {
        this.semaphore.acquire();
        try {
            new ProcessingFileAgent(this.params, path).start();
        } finally {
            this.semaphore.release();
        }
    }

}