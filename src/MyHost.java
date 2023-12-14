import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class MyHost extends Host {
    private final PriorityBlockingQueue<Task> queue;
    private volatile Task task;
    private final Object lock;
    private final AtomicBoolean isRunning;

    /**
     * Constructs a new host with a priority queue and a lock.
     */
    public MyHost() {
        this.queue = new PriorityBlockingQueue<>(1, Comparator
                .comparing(Task::getPriority)
                .reversed()
                .thenComparing(Task::getStart));
        this.task = null;
        this.lock = new Object();
        this.isRunning = new AtomicBoolean(true);
    }

    /**
     * Starts the host and performs tasks until it is shut down.
     */
    @Override
    public void run() {
        while (isRunning.get()) {
            performTask();
        }
    }

    /**
     * Performs the task if there is one and if it is not finished yet.
     */
    private void performTask() {
        synchronized (lock) {
            if (task == null) {
                task = queue.poll();
            } else {
                executeTask();
            }
        }
    }

    /**
     * Executes the task until it is finished or until it is preempted by a task with a higher priority in the queue.
     */
    private void executeTask() {
        long initialWorkTime = System.currentTimeMillis();

        while (task.getLeft() > 0) {
            preemptTaskIfNecessary();

            long timeWhenWorkDone = System.currentTimeMillis();
            task.setLeft(task.getLeft() - (timeWhenWorkDone - initialWorkTime));
            initialWorkTime = timeWhenWorkDone;
        }

        finishTask();
    }

    /**
     * Preempts the task if there is one and if there is a task with a higher priority in the queue.
     */
    private void preemptTaskIfNecessary() {
        if (task.isPreemptible()
                && !queue.isEmpty()
                && task.getPriority() < Objects.requireNonNull(queue.peek()).getPriority()) {
            queue.add(task);
            task = queue.poll();
        }
    }

    /**
     * Finishes the task.
     */
    private void finishTask() {
        if (task != null) {
            task.finish();
            task = null;
        }
    }

    /**
     * Adds a task to the queue.
     *
     * @param task the task to add
     */
    @Override
    public void addTask(Task task) {
        queue.add(task);
    }

    /**
     * @return the size of the queue
     */
    @Override
    public int getQueueSize() {
        return queue.size() + (task == null ? 0 : 1);
    }

    /**
     * @return the amount of work left to do
     */
    @Override
    public long getWorkLeft() {
        return queue.stream().mapToLong(Task::getLeft).sum() + (task != null ? task.getLeft() : 0);
    }

    /**
     * Shuts down the host.
     */
    @Override
    public void shutdown() {
        isRunning.set(false);
    }
}
