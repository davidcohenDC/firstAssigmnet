package boundedbuffer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedBufferMap<X,Y> implements BoundedBuffer<X,Y>{
    private final Map<X, Y> buffer;
    private final Lock mutex;

    private final Condition canDoSomething;

    private boolean isOpen = true;

    public BoundedBufferMap() {
        this.buffer = new HashMap<>();
        this.mutex = new ReentrantLock();
        this.canDoSomething = mutex.newCondition();
    }

    @Override
    public synchronized void put(X key, Y item) throws InterruptedException {
        try {
            mutex.lock();
            if(!this.isOpen) {
                System.out.println("Waiting for buffer to be open");
                this.canDoSomething.await();
            }
            buffer.put(key, item);
        } finally {
            mutex.unlock();
        }
    }

    @Override
    public synchronized Y get(X key) throws InterruptedException {
        try {
            mutex.lock();
            if(!this.isOpen) {
                System.out.println("Waiting for buffer to be open");
                this.canDoSomething.await();
            }
            return buffer.get(key);
        } finally {
            mutex.unlock();
        }
    }

    @Override
    public boolean containsKey(X key) {
        try {
            mutex.lock();
            if(!this.isOpen) {
                try {
                    System.out.println("Waiting for buffer to be open");
                    this.canDoSomething.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            return buffer.containsKey(key);
        } finally {
            mutex.unlock();
        }
    }

    @Override
    public synchronized Map<X, Y> getMap() {
        try {
            mutex.lock();
            if(!this.isOpen) {
                System.out.println("Waiting for buffer to be open");
                this.canDoSomething.await();
            }
            return buffer;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            mutex.unlock();
        }
    }

    public void close() {
        try {
            mutex.lock();
            this.isOpen = false;
        } finally {
            mutex.unlock();
        }
    }

    public void open() {
        try {
            mutex.lock();
            this.isOpen = true;
            this.canDoSomething.signalAll();
        } finally {
            mutex.unlock();
        }
    }

}
