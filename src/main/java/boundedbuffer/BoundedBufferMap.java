package boundedbuffer;

import java.util.HashMap;
import java.util.Map;

public class BoundedBufferMap<X,Y> implements BoundedBuffer<X,Y>{
    private final Map<X, Y> buffer;

    public BoundedBufferMap() {
        this.buffer = new HashMap<>();
    }

    @Override
    public synchronized void put(X key, Y item){
        this.buffer.put(key, item);
    }

    @Override
    public synchronized Y get(X key){
        return this.buffer.get(key);
    }

    @Override
    public boolean containsKey(X key) {
        return this.buffer.containsKey(key);
    }

    @Override
    public synchronized Map<X, Y> getMap() {
        return this.buffer;
    }


}
