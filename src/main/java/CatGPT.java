import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Runs the CatGPT task-management chatbot.
 */
public class CatGPT {
    private static final int MAX_TASKS = 100;
    private static final String DATA_FILE_PATH = "./data/catgpt.txt";

    private CatGPT() {
    }

    /**
     * Parses and validates the task number supplied to a task-selection command.
     *
     * @param input the full user command
     * @param command the command word to remove before parsing the number
     * @param taskCount the number of tasks currently stored
     * @return the zero-based index of the selected task
     * @throws CatGPTException if the task number is missing, invalid, or outside the task list
     */
    private static int parseTaskIndex(String input, String command, int taskCount) throws CatGPTException {
        String argument = input.substring(command.length()).trim();
        if (argument.isEmpty()) {
            throw new CatGPTException("Please provide a task number after '" + command + "'.");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(argument);
        } catch (NumberFormatException error) {
            throw new CatGPTException("The task number must be a whole number.");
        }

        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new CatGPTException("That task number is not in your list.");
        }
        return taskNumber - 1;
    }

    /**
     * Starts CatGPT and processes commands until the user exits.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        String banner = "  ____      _    ____ ____ _____ \n"
                + " / ___|__ _| |_ / ___|  _ \\_   _|\n"
                + "| |   / _` | __| |  _| |_) || |  \n"
                + "| |__| (_| | |_| |_| |  __/ | |  \n"
                + " \\____\\__,_|\\__|\\____|_|    |_|  \n";
        System.out.println(banner);
        System.out.println("Hello! I'm CatGPT.");
        System.out.println("What can I do for you?");
        Scanner scanner = new Scanner(System.in);
        Task[] tasks = new Task[MAX_TASKS];
        int taskCount = 0;
        Storage storage = new Storage(DATA_FILE_PATH);
        try {
            taskCount = storage.load(tasks);
        } catch (CatGPTException error) {
            System.out.println("OOPS!!! " + error.getMessage());
        }

        while (true) {
            if (!scanner.hasNextLine()) {
                System.out.println("Bye. Hope to see you again soon!");
                break;
            }
            String input = scanner.nextLine().trim();

            try {
                if (input.equals("bye")) {
                    System.out.println("Bye. Hope to see you again soon!");
                    break;
                } else if (input.equals("list")) {
                    System.out.println("Here are the tasks in your list:");
                    for (int i = 0; i < taskCount; i++) {
                        System.out.println((i + 1) + "." + tasks[i]);
                    }
                } else if (input.equals("mark") || input.startsWith("mark ")) {
                    int taskIndex = parseTaskIndex(input, "mark", taskCount);
                    tasks[taskIndex].markAsDone();
                    storage.save(tasks, taskCount);
                    System.out.println("Nice! I've marked this task as done:");
                    System.out.println(tasks[taskIndex]);
                } else if (input.equals("unmark") || input.startsWith("unmark ")) {
                    int taskIndex = parseTaskIndex(input, "unmark", taskCount);
                    tasks[taskIndex].markAsNotDone();
                    storage.save(tasks, taskCount);
                    System.out.println("OK! I've marked this task as not done yet:");
                    System.out.println(tasks[taskIndex]);
                } else if (input.equals("delete") || input.startsWith("delete ")) {
                    int taskIndex = parseTaskIndex(input, "delete", taskCount);
                    Task deletedTask = tasks[taskIndex];
                    for (int i = taskIndex; i < taskCount - 1; i++) {
                        tasks[i] = tasks[i + 1];
                    }
                    taskCount--;
                    tasks[taskCount] = null;
                    storage.save(tasks, taskCount);
                    System.out.println("Noted. I've removed this task:");
                    System.out.println(deletedTask);
                    String taskWord = taskCount == 1 ? "task" : "tasks";
                    System.out.println("Now you have " + taskCount + " " + taskWord + " in the list.");
                } else if (input.equals("todo") || input.startsWith("todo ")) {
                    String description = input.substring(4).trim();
                    if (description.isEmpty()) {
                        throw new CatGPTException("The description of a todo cannot be empty.");
                    }
                    if (taskCount >= MAX_TASKS) {
                        throw new CatGPTException("Your task list is full.");
                    }
                    Task task = Task.createTodo(description);
                    tasks[taskCount] = task;
                    taskCount++;
                    storage.save(tasks, taskCount);
                    System.out.println("Got it. I've added this task:");
                    System.out.println(task);
                    String taskWord = taskCount == 1 ? "task" : "tasks";
                    System.out.println("Now you have " + taskCount + " " + taskWord + " in the list.");
                } else if (input.equals("deadline") || input.startsWith("deadline ")) {
                    String body = input.substring(8).trim();
                    int byIndex = body.indexOf(" /by ");
                    if (byIndex < 0) {
                        throw new CatGPTException("Use: deadline DESCRIPTION /by yyyy-MM-dd");
                    }
                    String description = body.substring(0, byIndex).trim();
                    String dateText = body.substring(byIndex + 5).trim();
                    if (description.isEmpty()) {
                        throw new CatGPTException("The description of a deadline cannot be empty.");
                    }
                    if (dateText.isEmpty()) {
                        throw new CatGPTException("The deadline date cannot be empty.");
                    }
                    if (taskCount >= MAX_TASKS) {
                        throw new CatGPTException("Your task list is full.");
                    }
                    LocalDate deadlineDate;
                    try {
                        deadlineDate = LocalDate.parse(dateText);
                    } catch (DateTimeParseException error) {
                        throw new CatGPTException(
                                "The deadline date must use yyyy-MM-dd and be a valid date.");
                    }
                    Task task = Task.createDeadline(description, deadlineDate);
                    tasks[taskCount] = task;
                    taskCount++;
                    storage.save(tasks, taskCount);
                    System.out.println("Got it. I've added this task:");
                    System.out.println(task);
                    String taskWord = taskCount == 1 ? "task" : "tasks";
                    System.out.println("Now you have " + taskCount + " " + taskWord + " in the list.");
                } else if (input.equals("event") || input.startsWith("event ")) {
                    String body = input.substring(5).trim();
                    int fromIndex = body.indexOf(" /from ");
                    int toIndex = fromIndex < 0 ? -1 : body.indexOf(" /to ", fromIndex + 7);
                    if (fromIndex < 0 || toIndex < 0) {
                        throw new CatGPTException("Use: event DESCRIPTION /from START /to END");
                    }
                    String description = body.substring(0, fromIndex).trim();
                    String from = body.substring(fromIndex + 7, toIndex).trim();
                    String to = body.substring(toIndex + 5).trim();
                    if (description.isEmpty()) {
                        throw new CatGPTException("The description of an event cannot be empty.");
                    }
                    if (from.isEmpty() || to.isEmpty()) {
                        throw new CatGPTException("The event start and end times cannot be empty.");
                    }
                    if (taskCount >= MAX_TASKS) {
                        throw new CatGPTException("Your task list is full.");
                    }
                    Task task = Task.createEvent(description, from, to);
                    tasks[taskCount] = task;
                    taskCount++;
                    storage.save(tasks, taskCount);
                    System.out.println("Got it. I've added this task:");
                    System.out.println(task);
                    String taskWord = taskCount == 1 ? "task" : "tasks";
                    System.out.println("Now you have " + taskCount + " " + taskWord + " in the list.");
                } else if (input.isEmpty()) {
                    throw new CatGPTException("Please enter a command.");
                } else {
                    throw new CatGPTException("I don't know what that means.");
                }
            } catch (CatGPTException error) {
                System.out.println("OOPS!!! " + error.getMessage());
            }
        }
    }
}
