package catgpt;

/**
 * Coordinates CatGPT's user interface, parser, task list, and storage.
 */
public class CatGPT {
    private static final String DATA_FILE_PATH = "./data/catgpt.txt";

    private final Storage storage;
    private final Ui ui;
    private final Parser parser;
    private TaskList tasks;

    /**
     * Creates CatGPT and loads tasks from the specified data file.
     *
     * @param filePath path of the data file to load and save
     */
    public CatGPT(String filePath) {
        storage = new Storage(filePath);
        ui = new Ui();
        parser = new Parser();
        try {
            tasks = new TaskList(storage.load());
        } catch (CatGPTException error) {
            ui.showError(error.getMessage());
            tasks = new TaskList();
        }
    }

    /**
     * Processes user commands until the user exits or input ends.
     */
    public void run() {
        ui.showWelcome();
        while (true) {
            String input = ui.readCommand();
            if (input == null) {
                ui.showGoodbye();
                break;
            }

            try {
                Parser.ParsedCommand command = parser.parse(input);
                if (execute(command)) {
                    break;
                }
            } catch (CatGPTException error) {
                ui.showError(error.getMessage());
            }
        }
    }

    /**
     * Executes one parsed command.
     *
     * @param command parsed command to execute
     * @return {@code true} if CatGPT should exit
     * @throws CatGPTException if command execution or storage fails
     */
    private boolean execute(Parser.ParsedCommand command) throws CatGPTException {
        switch (command.getType()) {
        case BYE:
            ui.showGoodbye();
            return true;
        case LIST:
            ui.showTaskList(tasks);
            break;
        case MARK:
            changeTaskStatus(command, true);
            break;
        case UNMARK:
            changeTaskStatus(command, false);
            break;
        case DELETE:
            deleteTask(command);
            break;
        case FIND:
            findTasks(command);
            break;
        case TODO:
        case DEADLINE:
        case EVENT:
            addTask(command);
            break;
        default:
            throw new IllegalStateException("Unsupported command type");
        }
        return false;
    }

    private void addTask(Parser.ParsedCommand command) throws CatGPTException {
        Task task = parser.parseTask(command);
        tasks.add(task);
        storage.save(tasks);
        ui.showTaskAdded(task, tasks.size());
    }

    private void changeTaskStatus(Parser.ParsedCommand command, boolean isDone)
            throws CatGPTException {
        int taskIndex = parser.parseTaskIndex(command, tasks.size());
        Task task = tasks.get(taskIndex);
        if (isDone) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
        storage.save(tasks);
        ui.showTaskStatusChanged(task, isDone);
    }

    private void deleteTask(Parser.ParsedCommand command) throws CatGPTException {
        int taskIndex = parser.parseTaskIndex(command, tasks.size());
        Task deletedTask = tasks.delete(taskIndex);
        storage.save(tasks);
        ui.showTaskDeleted(deletedTask, tasks.size());
    }

    private void findTasks(Parser.ParsedCommand command) throws CatGPTException {
        String keyword = parser.parseKeyword(command);
        TaskList matchingTasks = tasks.find(keyword);
        ui.showMatchingTasks(matchingTasks);
    }

    /**
     * Starts CatGPT using its default data-file location.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        new CatGPT(DATA_FILE_PATH).run();
    }
}
