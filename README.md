# Homework 2 APD - Task planning in a datacenter

<div align="center"><img src="https://media.tenor.com/Zf45U-rHMgkAAAAC/friday-good-morning.gif" width="300px"></div>

## Overview

* This Java code implements a task dispatcher and host system to manage and execute tasks across multiple hosts. The system includes a MyDispatcher class responsible for task allocation and a MyHost class representing the individual hosts capable of executing tasks. The task dispatcher utilizes different scheduling algorithms to distribute tasks among hosts.

## `MyDispatcher` Class

The `MyDispatcher` class extends the abstract `Dispatcher` class and provides task allocation functionalities based on different scheduling algorithms:

1. Round Robin: Tasks are assigned to hosts in a cyclic manner.
2. Shortest Queue: Tasks are assigned to the host with the shortest task queue.
3. Size Interval Task Assignment: Tasks are assigned to hosts based on their size intervals (short, medium, long).
4. Least Work Left: Tasks are assigned to the host with the least amount of work left.

``` java
    case ROUND_ROBIN 
```

* the task is added to the host pointed to by nextHost, and then nextHost is updated to point to the next host in a cyclic manner. This ensures that tasks are distributed in a round-robin fashion among all hosts.

``` java
    case SHORTEST_QUEUE 
```

* the task is assigned to the host with the shortest task queue. The algorithm iterates through all hosts, compares their queue sizes, and selects the one with the minimum size.

``` java
    case SIZE_INTERVAL_TASK_ASSIGNMENT 
```

* tasks are assigned to hosts based on their size intervals (short, medium, long). It uses the task type to determine which category the task falls into and assigns it accordingly.

``` java
    case LEAST_WORK_LEFT
```

* tasks are assigned to the host with the least amount of work left. The algorithm synchronizes on a lock object to ensure thread safety while determining the host with the minimum workload. (in the last test there was a problem in which a task was received too quickly and the algorithm failed, that's why I used synchronization)

## `MyHost` Class

The `MyHost` class represents an individual host capable of executing tasks. Each host runs on its own thread and maintains a priority queue (PriorityBlockingQueue) to manage tasks based on priority and start time.

### Features

* Task Preemption: In certain situations, a task in execution can be interrupted in order to run another task with a higher priority. This is called preemption and can only be done if the running task is preemptible. Thus, if a node runs a preemptible task and the dispatcher assigns it another task with a higher priority, the first task will be preempted and the second one will start to be executed.
* Task Execution: Hosts continuously execute tasks until they are shut down.

``` java
    public void run()
```

* It represents the main execution loop of the host. As long as the isRunning flag is true, the host continuously performs tasks by calling the performTask method.

``` java
    private void performTask()
```

* Checks if there is a task currently assigned to the host. If not, it retrieves a task from the queue using `poll()`. If a task is present, it calls the executeTask method.

``` java
    private void executeTask()
```

* Executes the task until it is finished or until it is preempted by a task with a higher priority in the queue. It continuously updates the remaining execution time of the task.

``` java
    private void preemptTaskIfNecessary()
```

* Checks if the current task is preemptible, the queue is not empty, and there is a task in the queue with higher priority. If all conditions are met, the current task is added back to the queue, and the task with higher priority is retrieved.

``` java
    private void finishTask()
```

* Finishes the current task by calling its finish method (assuming there is a task). It sets the task reference to null after finishing.

``` java
    public void addTask(Task task)
```

* Adds a task to the host's task queue using the add method of the priority queue.

``` java
    public int getQueueSize()
```

* Returns the size of the task queue, including the currently executing task (if any).

``` java
    public long getWorkLeft()
```

* Returns the total amount of work left in the task queue, including the remaining time of the currently executing task (if any).

``` java
    public void shutdown()
```

* Shuts down the host by setting the isRunning flag to false. This signals the termination of the host's main execution loop.

## Project Structure

* checker/
  * checker.sh
* src/
  * in/ - input files
  * Dispatcher.java - abstract class for a task dispatcher
  * Enums.java
    * SchedulingAlgorithm - enums for the scheduling algorithm implemented by the dispatcher
    * TaskType - enum for the type of tasks (only used by the SITA scheduling algorithm)
  * Exitter.java - used for forcefully exiting the program after a given time
  * Host.java - abstract class for a host
  * Main.java
  * MyDispatcher.java
  * MyHost.java
  * Task.java - class for a task to be scheduled
  * TaskGenerator.java - Class that simulates a task generator. Reads a list of tasks and their start times from a file, and sends those tasks to the dispatcher based on their starting point
  * Test.java - Class for a test, defined by the number of hosts, the scheduling algorithm, the wait time until the threads are joined, the folder with the input files, and the amount of points obtained from this test
  * Timer.java - class for measuring the time since a specified initialization moment

## Dependencies

Java 8 or higher.

## Note

* The code includes mechanisms for task preemption, continuous task execution, and shutting down hosts gracefully.
* Different scheduling algorithms can be chosen when instantiating the MyDispatcher class.
* The code utilizes a priority queue for efficient task management in the host.
