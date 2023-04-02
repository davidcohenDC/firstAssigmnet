package walker;

import boundedbuffer.Distribution;

import java.io.IOException;
import java.nio.file.Path;

/**
 * An abstract class that provides a skeleton implementation of the directory walking functionality
 * for a given directory. Concrete subclasses can extend this class and implement the `walkRec`,
 * `beforeWalk`, and `afterWalk` methods to perform the actual directory walking and file processing,
 * and to perform any necessary setup and cleanup tasks before and after the directory walking.
 * The `AbstractDirectoryWalker` class takes in a `Path` object that represents the directory to be walked,
 * a `Distribution` object that represents the distribution of files based on some criterion, and some other
 * parameters for configuring the directory walking behavior.
 */
public abstract class AbstractDirectoryWalker implements Walker {

    // The directory to be walked
    protected final Path directory;

    // The distribution of files based on some criterion
    protected final Distribution<Integer,Path> distribution;

    // The parameters for configuring the directory walking behavior
    protected final DirectoryWalkerParams params;

    /**
     * Creates a new instance of the `AbstractDirectoryWalker` class with the given directory,
     * distribution, and configuration parameters.
     *
     * @param directory the directory to be walked
     * @param maxFiles the maximum number of files to be tracked in the distribution
     * @param numIntervals the number of intervals to divide the distribution into
     * @param maxLength the maximum number of lines allowed in a file
     * @param distributionMap the distribution of files based on some criterion
     */
    public AbstractDirectoryWalker(Path directory, int maxFiles, int numIntervals,
                                   int maxLength, Distribution<Integer,Path> distributionMap) {
        // Initialize the fields
        this.directory = directory;
        this.distribution = distributionMap;
        this.params = DirectoryWalkerParams.builder()
                .directory(directory)
                .maxFiles(maxFiles)
                .numIntervals(numIntervals)
                .maxLines(maxLength)
                .distribution(distributionMap)
                .build();
    }

    /**
     * Starts the directory walking process. This method creates a new thread to perform the
     * directory walking, waits for the thread to complete, and then calls the `beforeWalk` and
     * `afterWalk` methods to perform any necessary setup and cleanup tasks.
     *
     * @return true if the directory walking is successful, false otherwise
     */
    @Override
    public boolean walk() {
        try {
            Thread thread = new Thread(() -> {
                try {
                    walkRec(directory);
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            thread.start();

            this.beforeWalk();
            thread.join();
            this.afterWalk();

            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Walk was interrupted.");
            return false;
        } catch (Exception e) {
            System.err.println("An error occurred during the walk: " + e.getMessage());
            return false;
        }
    }

    /**
     * Returns a defensive copy of the `DirectoryWalkerParams` object used to configure
     * the directory walking behavior.
     *
     * @return a defensive copy of the `DirectoryWalkerParams` object
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

    /**
     * Performs the actual directory walking and file processing for the given directory.
     * This method is abstract and must be implemented by the concrete subclasses to provide
     * the specific behavior for the directory walking.
     *
     * @param directory the directory to be walked
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the thread is interrupted
     */
    protected abstract void walkRec(Path directory) throws IOException, InterruptedException;

    protected abstract void beforeWalk();

    protected abstract void afterWalk();
}
