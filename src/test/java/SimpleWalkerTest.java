import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import walker.Constants;
import walker.SimpleWalker;
import java.io.File;
import java.io.IOException;

public class SimpleWalkerTest {

    private final static String PATH = "C:\\Users\\Dach-\\Documents\\GitHub\\Universita\\PPS\\course-pps22-23-aula";
    private SimpleWalker walker;

    @Test
    void testBasicBehaviour() throws IOException {
        walker.walk();
    }

}
