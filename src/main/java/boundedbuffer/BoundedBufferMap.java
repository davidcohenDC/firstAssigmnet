package boundedbuffer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedBufferMap<X,Y> implements BoundedBuffer<X,Y>{
    private final Map<X, Y> buffer;
    private final Lock mutex;

    public BoundedBufferMap() {
        this.buffer = new HashMap<>();
        mutex = new ReentrantLock();
    }

    @Override
    public synchronized void put(X key, Y item){
        try {
            mutex.lock();
            buffer.put(key, item);
        } finally {
            mutex.unlock();
        }
    }

    @Override
    public synchronized Y get(X key){
        try {
            mutex.lock();
            return buffer.get(key);
        } finally {
            mutex.unlock();
        }
    }

    @Override
    public boolean containsKey(X key) {
        try {
            mutex.lock();
            return buffer.containsKey(key);
        } finally {
            mutex.unlock();
        }
    }

    @Override
    public synchronized Map<X, Y> getMap() {
        try {
            mutex.lock();
            return buffer;
        } finally {
            mutex.unlock();
        }
    }


}
