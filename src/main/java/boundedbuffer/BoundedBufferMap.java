package boundedbuffer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class BoundedBufferMap<X,Y> implements BoundedBuffer<X,Y>{
    private final Map<X, Y> buffer;
    private final ReadWriteLock lock;
    private boolean isOpen = true;

    public BoundedBufferMap() {
        this.buffer = new HashMap<>();
        this.lock = new ReentrantReadWriteLock();
    }

    @Override
    public void put(X key, Y item) throws InterruptedException {
        lock.writeLock().lockInterruptibly();
        try {
            if(!this.isOpen) {
                throw new InterruptedException("Buffer is closed");
            }
            buffer.put(key, item);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Y get(X key) throws InterruptedException {
        lock.readLock().lockInterruptibly();
        try {
            if(!this.isOpen) {
                throw new InterruptedException("Buffer is closed");
            }
            return buffer.get(key);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean containsKey(X key) {
        lock.readLock().lock();
        try {
            return buffer.containsKey(key);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Map<X, Y> getMap() {
        lock.readLock().lock();
        try {
            return new HashMap<>(buffer);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void close() {
        lock.writeLock().lock();
        try {
            this.isOpen = false;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void open() {
        lock.writeLock().lock();
        try {
            this.isOpen = true;
        } finally {
            lock.writeLock().unlock();
        }
    }

}
