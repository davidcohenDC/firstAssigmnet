import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import walker.SimpleWalker;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class SimpleWalkerTest {

    private final static String PATH = "src\\main\\jpf";

    private final static Integer NUMBER_OF_FILES = 3;

    private SimpleWalker walker;

    @BeforeEach
    void setUp() {
        this.walker = new SimpleWalker(new File(PATH));
    }

    @Test
    void testBasicBehaviour() {
        walker.walk();
        assertEquals(NUMBER_OF_FILES, walker.getFiles().size());
    }

}
