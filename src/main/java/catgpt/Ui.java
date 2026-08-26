package catgpt;

import java.util.Scanner;

/**
 * Handles all console input and output for CatGPT.
 */
public class Ui {
    private static final String BANNER = "  ____      _    ____ ____ _____ \n"
            + " / ___|__ _| |_ / ___|  _ \\_   _|\n"
            + "| |   / _` | __| |  _| |_) || |  \n"
            + "| |__| (_| | |_| |_| |  __/ | |  \n"
            + " \\____\\__,_|\\__|\\____|_|    |_|  \n";

    private final Scanner scanner;

    /** Creates a console UI that reads from standard input. */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /** Displays CatGPT's banner and greeting. */
    public void showWelcome() {
        System.out.println(BANNER);
        System.out.println("Hello! I'm CatGPT.");
        System.out.println("What can I do for you?");
    }

    /**
     * Reads the next command, or returns {@code null} when input ends.
     *
     * @return next command, or {@code null} at end of input
     */
    public String readCommand() {
        return scanner.hasNextLine() ? scanner.nextLine() : null;
    }

    /** Displays CatGPT's farewell message. */
    public void showGoodbye() {
        System.out.println("Bye. Hope to see you again soon!");
    }

    /**
     * Displays a user-friendly error message.
     *
     * @param message explanation of the error
     */
    public void showError(String message) {
        System.out.println("OOPS!!! " + message);
    }

    /**
     * Displays all tasks with one-based numbering.
     *
     * @param tasks tasks to display
     */
    public void showTaskList(TaskList tasks) {
        System.out.println("Here are the tasks in your list:");
        int taskNumber = 1;
        for (Task task : tasks) {
            System.out.println(taskNumber + "." + task);
            taskNumber++;
        }
    }

    /**
     * Displays tasks that match a find command with one-based numbering.
     *
     * @param tasks Matching tasks to display.
     */
    public void showMatchingTasks(TaskList tasks) {
        System.out.println("Here are the matching tasks in your list:");
        int taskNumber = 1;
        for (Task task : tasks) {
            System.out.println(taskNumber + "." + task);
            taskNumber++;
        }
    }

    /**
     * Displays confirmation that a task was added.
     *
     * @param task added task
     * @param taskCount resulting number of tasks
     */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println("Got it. I've added this task:");
        System.out.println(task);
        showTaskCount(taskCount);
    }

    /**
     * Displays confirmation that a task was deleted.
     *
     * @param task deleted task
     * @param taskCount resulting number of tasks
     */
    public void showTaskDeleted(Task task, int taskCount) {
        System.out.println("Noted. I've removed this task:");
        System.out.println(task);
        showTaskCount(taskCount);
    }

    /**
     * Displays confirmation of a task status change.
     *
     * @param task updated task
     * @param isDone whether the task was marked completed
     */
    public void showTaskStatusChanged(Task task, boolean isDone) {
        String message = isDone
                ? "Nice! I've marked this task as done:"
                : "OK! I've marked this task as not done yet:";
        System.out.println(message);
        System.out.println(task);
    }

    private void showTaskCount(int taskCount) {
        String taskWord = taskCount == 1 ? "task" : "tasks";
        System.out.println("Now you have " + taskCount + " " + taskWord + " in the list.");
    }
}
