package walker;

import java.nio.file.Path;

import boundedbuffer.Distribution;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.With;
@Getter
@Builder
@AllArgsConstructor
public class DirectoryWalkerParams {
    private final Path directory;
    @Builder.Default private final int maxFiles = 100;
    @Builder.Default private final int numIntervals = 10;
    @Builder.Default private final int maxLines = 1000;
    @With private final Distribution<Integer, Path> distribution;
    public int getIntervalLength() {
        return this.maxLines /this.numIntervals;
    }

    public int getInterval(int numberOfLines) {
        if (numberOfLines > this.maxLines) {
            return this.numIntervals;
        }
        return numberOfLines / this.getIntervalLength();
    }

}


