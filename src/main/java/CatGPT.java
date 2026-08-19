import java.util.Scanner;

public class CatGPT {
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
        int taskCount = 0;

        while (true) {
            String input = scanner.nextLine();

            if (input.equals("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                break;
            } else if (input.equals("list")) {
                System.out.println("Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    System.out.println((i + 1) + ". " + tasks[i]);
                }
            } else {
                tasks[taskCount] = input;
                taskCount++;
                System.out.println("Added: " + input);
            }
        }
    }
}
