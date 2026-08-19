import java.util.Scanner;

/**
 * Runs the CatGPT task-management chatbot.
 */
public class CatGPT {
    private static final int MAX_TASKS = 100;

    /**
     * Formats a task with its type, completion status, description, and optional timing details.
     *
     * @param taskType the task type marker, such as T, D, or E
     * @param isDone whether the task has been completed
     * @param description the task description
     * @param suffix optional timing details to append to the description
     * @return the formatted task
     */
    private static String formatTask(String taskType, boolean isDone, String description, String suffix) {
        String status = isDone ? "X" : " ";
        return "[" + taskType + "][" + status + "] " + description + suffix;
    }

    /**
     * Parses and validates the task number supplied to a mark or unmark command.
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
        String[] tasks = new String[MAX_TASKS];
        String[] taskTypes = new String[MAX_TASKS];
        String[] taskSuffixes = new String[MAX_TASKS];
        boolean[] isDone = new boolean[MAX_TASKS];
        int taskCount = 0;

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
                        System.out.println((i + 1) + "." + formatTask(
                                taskTypes[i], isDone[i], tasks[i], taskSuffixes[i]));
                    }
                } else if (input.equals("mark") || input.startsWith("mark ")) {
                    int taskIndex = parseTaskIndex(input, "mark", taskCount);
                    isDone[taskIndex] = true;
                    System.out.println("Nice! I've marked this task as done:");
                    System.out.println(formatTask(
                            taskTypes[taskIndex], isDone[taskIndex], tasks[taskIndex], taskSuffixes[taskIndex]));
                } else if (input.equals("unmark") || input.startsWith("unmark ")) {
                    int taskIndex = parseTaskIndex(input, "unmark", taskCount);
                    isDone[taskIndex] = false;
                    System.out.println("OK! I've marked this task as not done yet:");
                    System.out.println(formatTask(
                            taskTypes[taskIndex], isDone[taskIndex], tasks[taskIndex], taskSuffixes[taskIndex]));
                } else if (input.equals("todo") || input.startsWith("todo ")) {
                    String description = input.substring(4).trim();
                    if (description.isEmpty()) {
                        throw new CatGPTException("The description of a todo cannot be empty.");
                    }
                    if (taskCount >= MAX_TASKS) {
                        throw new CatGPTException("Your task list is full.");
                    }
                    tasks[taskCount] = description;
                    taskTypes[taskCount] = "T";
                    taskSuffixes[taskCount] = "";
                    taskCount++;
                    System.out.println("Got it. I've added this task:");
                    System.out.println(formatTask("T", false, description, ""));
                    String taskWord = taskCount == 1 ? "task" : "tasks";
                    System.out.println("Now you have " + taskCount + " " + taskWord + " in the list.");
                } else if (input.equals("deadline") || input.startsWith("deadline ")) {
                    String body = input.substring(8).trim();
                    int byIndex = body.indexOf(" /by ");
                    if (byIndex < 0) {
                        throw new CatGPTException("Use: deadline DESCRIPTION /by TIME");
                    }
                    String description = body.substring(0, byIndex).trim();
                    String by = body.substring(byIndex + 5).trim();
                    if (description.isEmpty()) {
                        throw new CatGPTException("The description of a deadline cannot be empty.");
                    }
                    if (by.isEmpty()) {
                        throw new CatGPTException("The deadline time cannot be empty.");
                    }
                    if (taskCount >= MAX_TASKS) {
                        throw new CatGPTException("Your task list is full.");
                    }
                    String suffix = " (by: " + by + ")";
                    tasks[taskCount] = description;
                    taskTypes[taskCount] = "D";
                    taskSuffixes[taskCount] = suffix;
                    taskCount++;
                    System.out.println("Got it. I've added this task:");
                    System.out.println(formatTask("D", false, description, suffix));
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
                    String suffix = " (from: " + from + " to: " + to + ")";
                    tasks[taskCount] = description;
                    taskTypes[taskCount] = "E";
                    taskSuffixes[taskCount] = suffix;
                    taskCount++;
                    System.out.println("Got it. I've added this task:");
                    System.out.println(formatTask("E", false, description, suffix));
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
