package walker;

import boundedbuffer.Distribution;

import java.io.IOException;
import java.nio.file.Path;

public abstract class AbstractDirectoryWalker implements Walker {

    protected final Path directory;
    protected final Distribution<Integer,Path> distribution;
    protected final DirectoryWalkerParams params;

    public AbstractDirectoryWalker(Path directory, int maxFiles, int numIntervals, int maxLength, Distribution<Integer,Path> distributionMap) {
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

    @Override
    public boolean walk() {
        Thread thread = new Thread(() -> {
            try {
                walkRec(directory);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        thread.start();

        this.beforeWalk();
        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        this.afterWalk();

        return true;
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

    /**
     * It will walk recursively through the directory and add the files to the distribution map
     * If the file has more lines than the max lines, it will be in a new interval that goes from the max lines to infinity
     * @param directory the directory to walk
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the thread is interrupted
     */
    protected abstract void walkRec(Path directory) throws IOException, InterruptedException;

    protected abstract void beforeWalk();

    protected abstract void afterWalk();
}
