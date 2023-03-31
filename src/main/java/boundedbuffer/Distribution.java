package boundedbuffer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * This class implements a bounded buffer that associates each key with a list of values.
 * The buffer is thread-safe and supports concurrent read and write operations.
 *
 * @param <X> the type of keys in the buffer
 * @param <Y> the type of values in the buffer
 */
public class Distribution<X,Y> implements BoundedBuffer<X,Y>{
    private final Map<X, List<Y>> buffer;
    private final ReadWriteLock lock;

    public Distribution() {
        this.buffer = new ConcurrentHashMap<>();
        this.lock = new ReentrantReadWriteLock();
    }

    @Override
    public void writeInterval(X key, Y item) throws InterruptedException {
        lock.writeLock().lockInterruptibly();
        try {
            this.buffer.merge(key, new ArrayList<>(List.of(item)), (list1, list2) -> {
                list1.addAll(list2);
                return list1;
            });
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public List<Y> readInterval(X key) throws InterruptedException {
        lock.readLock().lockInterruptibly();
        try {
            return List.copyOf(buffer.get(key));
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Map<X, List<Y>> readDistribution() {
        lock.readLock().lock();
        try {
            return Map.copyOf(buffer);
        } finally {
            lock.readLock().unlock();
        }
    }
}
