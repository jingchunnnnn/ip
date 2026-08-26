package catgpt;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Owns CatGPT's task collection and provides operations that modify it.
 */
public class TaskList implements Iterable<Task> {
    /** Maximum number of tasks that CatGPT can store. */
    public static final int MAX_TASKS = 100;

    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing tasks loaded from storage.
     *
     * @param tasks Tasks with which to initialize the list.
     */
    public TaskList(List<Task> tasks) {
        if (tasks.size() > MAX_TASKS) {
            throw new IllegalArgumentException("Too many tasks");
        }
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task Task to add.
     * @throws CatGPTException If the task list is full.
     */
    public void add(Task task) throws CatGPTException {
        if (tasks.size() >= MAX_TASKS) {
            throw new CatGPTException("Your task list is full.");
        }
        tasks.add(task);
    }

    /**
     * Returns the task at a zero-based index.
     *
     * @param index Zero-based task index.
     * @return The selected task.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Removes and returns the task at a zero-based index.
     *
     * @param index Zero-based task index.
     * @return The removed task.
     */
    public Task delete(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return Task count.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns an iterator over the tasks in list order.
     *
     * @return Task iterator.
     */
    @Override
    public Iterator<Task> iterator() {
        return tasks.iterator();
    }
}
