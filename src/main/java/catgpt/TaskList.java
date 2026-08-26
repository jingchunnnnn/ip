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

    /** Creates an empty task list. */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing tasks loaded from storage.
     *
     * @param tasks tasks with which to initialize the list
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
     * @param task task to add
     * @throws CatGPTException if the task list is full
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
     * @param index zero-based task index
     * @return the selected task
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Removes and returns the task at a zero-based index.
     *
     * @param index zero-based task index
     * @return the removed task
     */
    public Task delete(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return task count
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns tasks whose descriptions contain the specified keyword.
     *
     * @param keyword Keyword to search for.
     * @return Matching tasks in their original order.
     */
    public TaskList find(String keyword) {
        List<Task> matchingTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getDescription().contains(keyword)) {
                matchingTasks.add(task);
            }
        }
        return new TaskList(matchingTasks);
    }

    /**
     * Returns an iterator over the tasks in list order.
     *
     * @return task iterator
     */
    @Override
    public Iterator<Task> iterator() {
        return tasks.iterator();
    }
}
