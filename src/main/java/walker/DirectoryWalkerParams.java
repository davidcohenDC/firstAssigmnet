package walker;

import boundedbuffer.Distribution;

import java.nio.file.Path;

public class DirectoryWalkerParams {
    private final Path directory;
    private int maxFiles = 100;
    private int numIntervals = 10;
    private int maxLines = 1000;
    private final Distribution<Integer, Path> distribution;

    public DirectoryWalkerParams(Path directory, int maxFiles, int numIntervals, int maxLines, Distribution<Integer, Path> distribution) {
        this.directory = directory;
        this.maxFiles = maxFiles;
        this.numIntervals = numIntervals;
        this.maxLines = maxLines;
        this.distribution = distribution;
    }

    private static int $default$maxFiles() {
        return 100;
    }

    private static int $default$numIntervals() {
        return 10;
    }

    private static int $default$maxLines() {
        return 1000;
    }

    public static DirectoryWalkerParamsBuilder builder() {
        return new DirectoryWalkerParamsBuilder();
    }

    public int getIntervalLength() {
        return this.maxLines / this.numIntervals;
    }

    public int getInterval(int numberOfLines) {
        if (numberOfLines > this.maxLines) {
            return this.numIntervals;
        }
        return numberOfLines / this.getIntervalLength();
    }

    public Path getDirectory() {
        return this.directory;
    }

    public int getMaxFiles() {
        return this.maxFiles;
    }

    public int getNumIntervals() {
        return this.numIntervals;
    }

    public int getMaxLines() {
        return this.maxLines;
    }

    public Distribution<Integer, Path> getDistribution() {
        return this.distribution;
    }


    public static class DirectoryWalkerParamsBuilder {
        private Path directory;
        private int maxFiles$value;
        private boolean maxFiles$set;
        private int numIntervals$value;
        private boolean numIntervals$set;
        private int maxLines$value;
        private boolean maxLines$set;
        private Distribution<Integer, Path> distribution;

        DirectoryWalkerParamsBuilder() {
        }

        public DirectoryWalkerParamsBuilder directory(Path directory) {
            this.directory = directory;
            return this;
        }

        public DirectoryWalkerParamsBuilder maxFiles(int maxFiles) {
            this.maxFiles$value = maxFiles;
            this.maxFiles$set = true;
            return this;
        }

        public DirectoryWalkerParamsBuilder numIntervals(int numIntervals) {
            this.numIntervals$value = numIntervals;
            this.numIntervals$set = true;
            return this;
        }

        public DirectoryWalkerParamsBuilder maxLines(int maxLines) {
            this.maxLines$value = maxLines;
            this.maxLines$set = true;
            return this;
        }

        public DirectoryWalkerParamsBuilder distribution(Distribution<Integer, Path> distribution) {
            this.distribution = distribution;
            return this;
        }

        public DirectoryWalkerParams build() {
            int maxFiles$value = this.maxFiles$value;
            if (!this.maxFiles$set) {
                maxFiles$value = DirectoryWalkerParams.$default$maxFiles();
            }
            int numIntervals$value = this.numIntervals$value;
            if (!this.numIntervals$set) {
                numIntervals$value = DirectoryWalkerParams.$default$numIntervals();
            }
            int maxLines$value = this.maxLines$value;
            if (!this.maxLines$set) {
                maxLines$value = DirectoryWalkerParams.$default$maxLines();
            }
            return new DirectoryWalkerParams(this.directory, maxFiles$value, numIntervals$value, maxLines$value, this.distribution);
        }

        public String toString() {
            return "DirectoryWalkerParams.DirectoryWalkerParamsBuilder(directory=" + this.directory + ", maxFiles$value=" + this.maxFiles$value + ", numIntervals$value=" + this.numIntervals$value + ", maxLines$value=" + this.maxLines$value + ", distribution=" + this.distribution + ")";
        }
    }
}


