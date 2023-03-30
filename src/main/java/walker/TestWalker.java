package walker;

import java.io.File;
import java.io.IOException;

public class TestWalker {

    /**
     Create a concurrent program which, given a directory D present on the local file system containing a
     set of sources in Java (also considering any subdirectories, recursively), provide to determine
     and display on standard output:
     ○ the N sources with the highest number of lines of code;
     ○ The overall distribution related to how many sources have a number of lines of code that falls
     in a certain interval, considering a certain number of intervals NI and a maximum number of code lines MAXL
     to delimit the left end of the last range.
     N, D, NI and MAXL are assumed to be program parameters, passed from the command line.
     ■ Example: if NI = 5 and MAXL is 1000, then the first range is [0.249], the second is [250.499], the third is
     [500,749], the fourth is [750,999], the last is [1000,infinity]. The distribution determines how many sources there are
     for each range  I have a Java framework of the sources I have (all sources considered).
     */
     public static void main(String[] args) {
        if (args.length != Constants.Arguments.ARGUMENTS_SIZE) {
            System.out.println("Usage: <directory> <number of intervals> <max length of interval>");
            System.exit(1);
        }

        String directory = args[Constants.Arguments.DIRECTORY];
        int maxFiles = Integer.parseInt(args[Constants.Arguments.N_FILES]);
        int numIntervals = Integer.parseInt(args[Constants.Arguments.NUMBER_OF_INTERVALS]);
        int maxLength = Integer.parseInt(args[Constants.Arguments.MAX_LINES]);

        if (numIntervals <= 0 || maxLength <= 0) {
            System.out.println("The number of intervals and the max length of interval must be greater than 0");
            System.exit(1);
        }

        File dir = new File(directory);
        if (!dir.exists() || !dir.isDirectory()) {
            System.out.println("The directory " + directory + " does not exist");
            System.exit(1);
        }

        Walker walker = new SimpleWalker(dir.toPath(), maxFiles, numIntervals, maxLength);
         try {
                walker.walk();
         } catch (IOException e) {
             throw new RuntimeException(e);
         }


     }
}
