package walker;

import boundedbuffer.Distribution;
import chrono.Chrono;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class TestWalker {

     public static void main(String[] args) {
         if (args.length != WalkerArguments.ARGUMENTS_SIZE.ordinal()) {
             System.out.println("Usage: <max number of files> <directory> <number of intervals> <max number of lines>");
             System.exit(1);
         }

         String directory = args[WalkerArguments.DIRECTORY.ordinal()]; //"C:\\Users\\HP\\Desktop\\UNIBO\\LaureaMagistrale";
         int maxFiles = Integer.parseInt(args[WalkerArguments.N_FILES.ordinal()]);
         int numIntervals = Integer.parseInt(args[WalkerArguments.NUMBER_OF_INTERVALS.ordinal()]);
         int maxLength = Integer.parseInt(args[WalkerArguments.MAX_LINES.ordinal()]);

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
         int maxThread = new PerformanceUtils().getDefaultNumThread();
         /*Walker walker = new DirectoryWalkerMaster(params, maxThread);
         Chrono cron = new Chrono();
         cron.start();
         try {
             walker.walk();
             cron.stop();
             System.out.println("Time elapsed: " + cron.getTime() + " milliseconds"); //TODO check PERFORMANCE
             System.out.println("Number of available processors: " + Runtime.getRuntime().availableProcessors());
             System.out.println("Number of thread: " + maxThread);
         } catch (IOException e) {
             throw new RuntimeException(e);
         }*/


         for (int i=0; i<maxThread; i++) {
             //creazione directoryWalkerParams con thread i
             Walker walker = new DirectoryWalkerMaster(params, maxThread);
             Chrono crono = new Chrono();
             crono.start();
             try {
                 walker.walk();
                 crono.stop();
                 System.out.println("Time elapsed: " + crono.getTime() + " milliseconds"); //TODO check PERFORMANCE
             } catch (IOException e) {
                 throw new RuntimeException(e);
             }
         }
     }
}
