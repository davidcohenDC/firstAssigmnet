package walker;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * A walker is an object that walks a directory and prints the longest line in each file.
 */
public class SimpleWalker implements Walker {
    private final File directory;

    private final List<File> files = new ArrayList<>();

    public SimpleWalker(File directory) {
        this.directory = directory;
    }

    /**
     * Walks the directory and prints the longest line in each file.
     * If there are more than one longest line in the same file, print all of them.
     * If there are more than one longest line in different files, print all of them.
     * If there are no sources files in the directory D, print nothing.
     */
    @Override
    public void walk() {
        walkRec(this.directory);
    }

    /**
     * Recursive method to walk the directory
     * @param directory the directory to walk
     */
    private void walkRec(final File directory) {
        if(directory == null) {
            return;
        }
        File[] listOfFile = directory.listFiles();
        if(listOfFile == null) {
            return;
        }
        for(File file: listOfFile) {
            if(file.isFile()) {
                int numberOfLine = 0;
                try(Stream<String> fileStream = Files.lines(file.toPath())) {
                   numberOfLine = (int) fileStream.count();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                this.files.add(file);
                System.out.println(file.getAbsolutePath() + " has " + numberOfLine + " lines");
            } else if(file.isDirectory() && !file.isHidden()) {
                walkRec(file);
            }
        }
    }

    public List<File> getFiles() {
        return this.files;
    }
}