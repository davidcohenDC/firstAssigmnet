package walker;

import boundedbuffer.Distribution;
import chrono.Chrono;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

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
         } catch (IOException e) {
             throw new RuntimeException(e);
         }
     }
}
