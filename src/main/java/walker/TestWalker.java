package walker;

import boundedbuffer.Distribution;
import chrono.Chrono;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TestWalker {

     public static void main(String[] args) {
         if (args.length != WalkerArguments.ARGUMENTS_SIZE.getValue()) {
             System.out.println("Usage: <max number of files> <directory> <number of intervals> <max number of lines>");
             System.exit(1);
         }

         String directory = args[WalkerArguments.DIRECTORY.getValue()];
         int maxFiles = Integer.parseInt(args[WalkerArguments.N_FILES.getValue()]);
         int numIntervals = Integer.parseInt(args[WalkerArguments.NUMBER_OF_INTERVALS.getValue()]);
         int maxLength = Integer.parseInt(args[WalkerArguments.MAX_LINES.getValue()]);

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
         int maxThread = PerformanceUtils.getDefaultNumThread(); //PerformanceUtils.getNumberThread(PerformanceUtils.getNumberCpu(), 1, 0.2);
         Walker walker = new DirectoryWalkerMaster(params, maxThread);
         Chrono cron = new Chrono();
         cron.start();
         try {
             walker.walk();
             cron.stop();
             System.out.println("PERFORMANCE - Time elapsed: " + cron.getTime() + " milliseconds");
             System.out.println("PERFORMANCE - Number of available processors: " + Runtime.getRuntime().availableProcessors());
             System.out.println("PERFORMANCE - Number of thread: " + maxThread);
         } catch (IOException e) {
             throw new RuntimeException(e);
         }

         // PERFORMANCE - test
         /*Distribution<Integer, Path> distribution = new Distribution<>();
         DirectoryWalkerParams params = DirectoryWalkerParams.builder()
                 .directory(dir.toPath())
                 .maxFiles(maxFiles)
                 .numIntervals(numIntervals)
                 .maxLines(maxLength)
                 .distribution(distribution)
                 .build();

         //int maxThread = PerformanceUtils.getDefaultNumThread();
         List<Double> stepsRatioWaitComputeTime = new ArrayList<>(List.of(0.0, 0.2, 0.4, 0.6, 0.8, 1.0));
         for (Double step : stepsRatioWaitComputeTime) {
             int nThread = PerformanceUtils.getNumberThread(PerformanceUtils.getNumberCpu(), 1, step);
             Walker walker = new DirectoryWalkerMaster(params, nThread);
             Chrono crono = new Chrono();
             crono.start();
             try {
                 walker.walk();
                 crono.stop();
                 System.out.println("PERFORMANCE - Time elapsed: " + crono.getTime() + " milliseconds");
                 System.out.println("PERFORMANCE - nThread: " + nThread);
                 System.out.println("PERFORMANCE - W/C: " + step);
             } catch (IOException e) {
                 throw new RuntimeException(e);
             }
         }*/
     }
}
