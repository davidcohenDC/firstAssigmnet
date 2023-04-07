package walker;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

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

    /**
     * The parameters for configuring the directory walking behavior.
     */
    protected final DirectoryWalkerParams params;
    protected final AtomicBoolean isRunning = new AtomicBoolean(false);

    public AbstractDirectoryWalker(DirectoryWalkerParams params) {
        this.params = params;
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
        this.isRunning.set(true);
        try {
            //TODO DirectoryWalkerAgent
            Thread thread = new Thread(() -> {
                try {
                    walkRec(this.params.getDirectory());
                    // TODO check PERFORMANCE
                    System.out.println("------ Name of current thread " + Thread.currentThread().getName());
                    System.out.println("------ Number of threads " + Thread.activeCount());
                    System.out.println("------ Name of group thread " + Thread.currentThread().getThreadGroup().getName());
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            thread.start();

            this.beforeWalk();
            thread.join();
            this.afterWalk();

            this.isRunning.set(false);
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

    @Override
    public void stop() {
        this.isRunning.set(false);
        this.stopBehaviour();
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


    protected abstract void stopBehaviour();

    protected abstract void beforeWalk();

    protected abstract void afterWalk();
}
