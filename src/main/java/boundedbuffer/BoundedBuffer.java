package boundedbuffer;

import java.util.List;
import java.util.Map;

public interface BoundedBuffer<X,Y> {

    /**
     * Writes a value to the buffer at the specified key.
     *
     * @param key the key to write to
     * @param item the value to write
     * @throws InterruptedException if the write operation is interrupted
     */
    void writeInterval(X key,Y item) throws InterruptedException;

    /**
     * Reads the values from the buffer at the specified key.
     *
     * @param key the key to read from
     * @return an unmodifiable list of the values at the specified key
     * @throws InterruptedException if the read operation is interrupted
     */
    List<Y> readInterval(X key) throws InterruptedException;

    /**
     * Returns a copy of the buffer.
     *
     * @return an unmodifiable map of the buffer
     */
    Map<X, List<Y>> readDistribution();

}
