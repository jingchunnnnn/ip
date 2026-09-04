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
     * Creates CatGPT using its default data-file location.
     */
    public CatGPT() {
        this(DATA_FILE_PATH);
    }

    /**
     * Creates CatGPT and loads tasks from the specified data file.
     *
     * @param filePath Path of the data file to load and save.
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
                ui.showResponse(execute(command));
                if (command.getType() == Parser.CommandType.BYE) {
                    break;
                }
            } catch (CatGPTException error) {
                ui.showError(error.getMessage());
            }
        }
    }

    /**
     * Processes one user command and returns the response for a graphical UI.
     *
     * @param input User command to process.
     * @return User-facing response to the command.
     */
    public String getResponse(String input) {
        try {
            return execute(parser.parse(input));
        } catch (CatGPTException error) {
            return ui.formatError(error.getMessage());
        }
    }

    /**
     * Returns CatGPT's greeting for a graphical UI.
     *
     * @return Greeting shown when the application starts.
     */
    public String getWelcomeMessage() {
        return ui.getWelcomeMessage();
    }

    /**
     * Executes one parsed command.
     *
     * @param command Parsed command to execute.
     * @return User-facing response to the command.
     * @throws CatGPTException If command execution or storage fails.
     */
    private String execute(Parser.ParsedCommand command) throws CatGPTException {
        assert command != null : "Command must be parsed before execution";
        return switch (command.getType()) {
            case BYE -> ui.getGoodbyeMessage();
            case LIST -> ui.formatTaskList(tasks);
            case MARK -> changeTaskStatus(command, true);
            case UNMARK -> changeTaskStatus(command, false);
            case DELETE -> deleteTask(command);
            case FIND -> findTasks(command);
            case TODO, DEADLINE, EVENT -> addTask(command);
        };
    }

    private String addTask(Parser.ParsedCommand command) throws CatGPTException {
        Task task = parser.parseTask(command);
        tasks.add(task);
        storage.save(tasks);
        return ui.formatTaskAdded(task, tasks.size());
    }

    private String changeTaskStatus(Parser.ParsedCommand command, boolean isDone)
            throws CatGPTException {
        int taskIndex = parser.parseTaskIndex(command, tasks.size());
        Task task = tasks.get(taskIndex);
        if (isDone) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
        storage.save(tasks);
        return ui.formatTaskStatusChanged(task, isDone);
    }

    private String deleteTask(Parser.ParsedCommand command) throws CatGPTException {
        int taskIndex = parser.parseTaskIndex(command, tasks.size());
        Task deletedTask = tasks.delete(taskIndex);
        storage.save(tasks);
        return ui.formatTaskDeleted(deletedTask, tasks.size());
    }

    private String findTasks(Parser.ParsedCommand command) throws CatGPTException {
        String keyword = parser.parseKeyword(command);
        TaskList matchingTasks = tasks.find(keyword);
        return ui.formatMatchingTasks(matchingTasks);
    }

    /**
     * Starts CatGPT using its default data-file location.
     *
     * @param args Command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        new CatGPT(DATA_FILE_PATH).run();
    }
}
