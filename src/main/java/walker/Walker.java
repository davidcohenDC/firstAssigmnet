package walker;

import java.io.IOException;

public interface Walker {

    /**
     * Walk the directory and print the longest line in each file
     *
     * @throws IOException if an I/O error occurs
     */
    boolean walk() throws IOException;

    /**
     * get the string representation of the max lines
     */
    String getMaxFilesString();

    /**
     * get the string representation of the distribution map
     */
    String getDistributionString();

    void stop();

    void resume();

}


