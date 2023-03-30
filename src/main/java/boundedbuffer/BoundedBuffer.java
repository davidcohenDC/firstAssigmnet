package boundedbuffer;

import java.util.Map;

public interface BoundedBuffer<X,Y> {
    void put(X key,Y item) throws InterruptedException;
    Y get(X key) throws InterruptedException;
    boolean containsKey(X key);
    Map<X,Y> getMap();

    void open();

    void close();

}
