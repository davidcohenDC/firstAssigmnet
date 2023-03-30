package walker;

import java.io.IOException;

public interface Walker {

    /**
     * Walk the directory and print the longest line in each file
     * @throws IOException if an I/O error occurs
     */
    void walk() throws IOException;
}
