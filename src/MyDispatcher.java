import java.util.List;

public class MyDispatcher extends Dispatcher {
    private int nextHost = 0;
    public MyDispatcher(SchedulingAlgorithm algorithm, List<Host> hosts) {
        super(algorithm, hosts);
    }

    /**
     * Adds a task to the host with the least amount of work left.
     * @param task the task to be added
     */
    @Override
    public void addTask(Task task) {
        switch (algorithm) {
            case ROUND_ROBIN -> {
                hosts.get(nextHost).addTask(task);
                nextHost = (nextHost + 1) % hosts.size();
            }
            case SHORTEST_QUEUE -> {
                int shortestQueue = 0;
                for (int i = 1; i < hosts.size(); i++) {
                    if (hosts.get(i).getQueueSize() < hosts.get(shortestQueue).getQueueSize()) {
                        shortestQueue = i;
                    }
                }
                hosts.get(shortestQueue).addTask(task);
            }
            case SIZE_INTERVAL_TASK_ASSIGNMENT -> {
                switch (task.getType()) {
                    case SHORT -> hosts.get(TaskType.SHORT.ordinal()).addTask(task);
                    case MEDIUM -> hosts.get(TaskType.MEDIUM.ordinal()).addTask(task);
                    case LONG -> hosts.get(TaskType.LONG.ordinal()).addTask(task);
                    default -> throw new IllegalStateException("Unexpected value: " + task.getType());
                }
            }
            case LEAST_WORK_LEFT -> {
                int leastWorkLeft = 0;
                for (int i = 1; i < hosts.size(); i++) {
                    if (hosts.get(i).getWorkLeft() < hosts.get(leastWorkLeft).getWorkLeft()) {
                        leastWorkLeft = i;
                    }
                }
                hosts.get(leastWorkLeft).addTask(task);
            }
            default -> throw new IllegalStateException("Unexpected value: " + algorithm);
        }
    }
}
