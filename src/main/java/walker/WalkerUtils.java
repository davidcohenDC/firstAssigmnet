package walker;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class WalkerUtils {
    public static int countLines(Path file) throws IOException {
        try (Stream<String> fileStream = Files.lines(file)) {
            return (int) fileStream.count();
        } catch (UncheckedIOException e) {
            if (e.getCause() instanceof MalformedInputException) {
                System.out.println("MalformedInputException"); //TODO
            }
            return 0;
        }
    }
}
