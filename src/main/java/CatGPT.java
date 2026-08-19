import java.util.Scanner;

/**
 * Runs the CatGPT task-management chatbot.
 */
public class CatGPT {
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
        String[] tasks = new String[100];
        String[] taskTypes = new String[100];
        String[] taskSuffixes = new String[100];
        boolean[] isDone = new boolean[100];
        int taskCount = 0;

        while (true) {
            String input = scanner.nextLine();

            if (input.equals("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                break;
            } else if (input.equals("list")) {
                System.out.println("Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    System.out.println((i + 1) + "." + formatTask(
                            taskTypes[i], isDone[i], tasks[i], taskSuffixes[i]));
                }
            } else if (input.startsWith("mark ")) {
                int taskNumber = Integer.parseInt(input.substring(5));
                int taskIndex = taskNumber - 1;
                isDone[taskIndex] = true;
                System.out.println("Nice! I've marked this task as done:");
                System.out.println(formatTask(
                        taskTypes[taskIndex], isDone[taskIndex], tasks[taskIndex], taskSuffixes[taskIndex]));
            } else if (input.startsWith("unmark ")) {
                int taskNumber = Integer.parseInt(input.substring(7));
                int taskIndex = taskNumber - 1;
                isDone[taskIndex] = false;
                System.out.println("OK! I've marked this task as not done yet:");
                System.out.println(formatTask(
                        taskTypes[taskIndex], isDone[taskIndex], tasks[taskIndex], taskSuffixes[taskIndex]));
            } else {
                String taskType;
                String description;
                String suffix = "";

                if (input.startsWith("todo ")) {
                    taskType = "T";
                    description = input.substring(5);
                } else if (input.startsWith("deadline ")) {
                    int byIndex = input.indexOf(" /by ");
                    taskType = "D";
                    description = input.substring(9, byIndex);
                    suffix = " (by: " + input.substring(byIndex + 5) + ")";
                } else if (input.startsWith("event ")) {
                    int fromIndex = input.indexOf(" /from ");
                    int toIndex = input.indexOf(" /to ", fromIndex + 7);
                    taskType = "E";
                    description = input.substring(6, fromIndex);
                    String from = input.substring(fromIndex + 7, toIndex);
                    String to = input.substring(toIndex + 5);
                    suffix = " (from: " + from + " to: " + to + ")";
                } else {
                    taskType = "T";
                    description = input;
                }

                tasks[taskCount] = description;
                taskTypes[taskCount] = taskType;
                taskSuffixes[taskCount] = suffix;
                taskCount++;
                System.out.println("Got it. I've added this task:");
                System.out.println(formatTask(taskType, false, description, suffix));
                String taskWord = taskCount == 1 ? "task" : "tasks";
                System.out.println("Now you have " + taskCount + " " + taskWord + " in the list.");
            }
        }
    }
}
