package walker;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Active component, the Agent that walks in directories.
 */
public class DirectoryWalkerAgent extends Thread {
    private final Walker directoryWalker;
    private final Path directory;

    public DirectoryWalkerAgent(Walker walker, Path directory) {
        this.directoryWalker = walker;
        this.directory = directory;
    }

    @Override
    public void run() {
        try {
            ((AbstractDirectoryWalker) this.directoryWalker).walkRec(directory);
            // TODO check PERFORMANCE
            System.out.println("------ Name of current thread " + Thread.currentThread().getName());
            System.out.println("------ Number of threads " + Thread.activeCount());
            System.out.println("------ Name of group thread " + Thread.currentThread().getThreadGroup().getName());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
