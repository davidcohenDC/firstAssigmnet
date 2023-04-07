package walker;

import boundedbuffer.Distribution;
import chrono.Chrono;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

public class TestWalker {

     public static void main(String[] args) {
         if (args.length != Arguments.ARGUMENTS_SIZE.ordinal()) {
             System.out.println("Usage: <max number of files> <directory> <number of intervals> <max number of lines>");
             System.exit(1);
         }

         String directory = args[Arguments.DIRECTORY.ordinal()]; //"C:\\Users\\HP\\Desktop\\UNIBO\\LaureaMagistrale";
         int maxFiles = Integer.parseInt(args[Arguments.N_FILES.ordinal()]);
         int numIntervals = Integer.parseInt(args[Arguments.NUMBER_OF_INTERVALS.ordinal()]);
         int maxLength = Integer.parseInt(args[Arguments.MAX_LINES.ordinal()]);

         if (numIntervals <= 0 || maxLength <= 0) {
             System.out.println("The number of intervals and the max length of interval must be greater than 0");
             System.exit(1);
         }

         File dir = new File(directory);
         if (!dir.exists() || !dir.isDirectory()) {
             System.out.println("The directory " + directory + " does not exist");
             System.exit(1);
         }
         Distribution<Integer, Path> distribution = new Distribution<>();
         DirectoryWalkerParams params = DirectoryWalkerParams.builder()
                                             .directory(dir.toPath())
                                             .maxFiles(maxFiles)
                                             .numIntervals(numIntervals)
                                             .maxLines(maxLength)
                                             .distribution(distribution)
                                             .build();
         Walker walker = new DirectoryWalkerMaster(params);
         Chrono cron = new Chrono();
         cron.start();
         try {
             walker.walk();
             cron.stop();
             System.out.println("Time elapsed: " + cron.getTime() + " milliseconds"); //TODO check PERFORMANCE
         } catch (IOException e) {
             throw new RuntimeException(e);
         }
     }
}
