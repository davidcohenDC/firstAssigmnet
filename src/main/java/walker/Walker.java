package walker;

import boundedbuffer.Distribution;

import java.io.IOException;
import java.nio.file.Path;

public interface Walker {

    /**
     * Walk the directory and print the longest line in each file
     *
     * @throws IOException if an I/O error occurs
     */
    boolean walk() throws IOException;

    void stop();

    void resume();

    Distribution<Integer, Path> getDistribution();

}


