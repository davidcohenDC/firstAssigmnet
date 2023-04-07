package walker;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Active component, the Agent that process a file.
 */
public class ProcessingFileAgent extends Thread {
    private final DistributionMapUpdater updater;
    private final int interval;
    private final Path path;

    public ProcessingFileAgent(DirectoryWalkerParams params, Path path) throws IOException {
        this.updater = new DistributionMapUpdater(params.getDistribution());
        this.interval = params.getInterval(WalkerUtils.countLines(path), path);
        this.path = path;
    }

    @Override
    public void run() {
        this.updater.processFile(this.interval, this.path);
    }
}
