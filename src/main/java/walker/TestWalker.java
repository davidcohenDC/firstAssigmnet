package walker;

import java.io.File;

public class TestWalker {

    /**
     * Given three arguments D (directory), NI (number of intervals) and MAXL(max length of interval),
     * print foreach number of interval i (0 <= i < NI) the longest line file in the sources files in the directory D.
     * The longest line is divided in NI intervals of length MAXL.
     * The longest line is printed in the standard output.
     * Foreach interval use one thread to find the longest line in the sources files.
     * Each line is printed in the format:
     * <file name>:<line number>
     *  where <file name> is the name of the file containing the longest line,
     *  and <line number> is the number of the line in the file.
     *  If there are more than one longest line in the same file, print all of them.
     *  If there are more than one longest line in different files, print all of them.
     *  If there are no sources files in the directory D, print nothing.
     *  If there are no longest lines in the sources files, print nothing.
     *  Example: NI = 5, MAXL = 1000 means there are 5 intervals of length [0,199], [200,399], [400,599], [600,799], [800,999], [1000, until the end of the directory] and each interval is processed by a thread.
    */
     public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: <directory> <number of intervals> <max length of interval>");
            System.exit(1);
        }

        String directory = args[0];
        int numIntervals = Integer.parseInt(args[1]);
        int maxLength = Integer.parseInt(args[2]);

        if (numIntervals <= 0 || maxLength <= 0) {
            System.out.println("The number of intervals and the max length of interval must be greater than 0");
            System.exit(1);
        }

        File dir = new File(directory);
        SimpleWalker walker = new SimpleWalker(dir);
        walker.walk();


    }
}
