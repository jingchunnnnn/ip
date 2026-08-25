import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task and the information needed to display and save it.
 */
public class Task {
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    private final TaskType type;
    private final String description;
    private final String suffix;
    private final LocalDate deadlineDate;
    private boolean isDone;

    /**
     * Creates a task with all its stored state.
     *
     * @param type category of the task
     * @param description task description
     * @param suffix display details for non-deadline tasks
     * @param deadlineDate deadline date, or {@code null} for other task types
     * @param isDone whether the task has been completed
     */
    private Task(TaskType type, String description, String suffix,
                 LocalDate deadlineDate, boolean isDone) {
        this.type = type;
        this.description = description;
        this.suffix = suffix;
        this.deadlineDate = deadlineDate;
        this.isDone = isDone;
    }

    /**
     * Creates an incomplete todo task.
     *
     * @param description task description
     * @return the new todo task
     */
    public static Task createTodo(String description) {
        return new Task(TaskType.TODO, description, "", null, false);
    }

    /**
     * Creates an incomplete deadline task with a typed date.
     *
     * @param description task description
     * @param deadlineDate date by which the task should be completed
     * @return the new deadline task
     */
    public static Task createDeadline(String description, LocalDate deadlineDate) {
        return new Task(TaskType.DEADLINE, description, "", deadlineDate, false);
    }

    /**
     * Creates an incomplete event task.
     *
     * @param description task description
     * @param from event start details
     * @param to event end details
     * @return the new event task
     */
    public static Task createEvent(String description, String from, String to) {
        String suffix = " (from: " + from + " to: " + to + ")";
        return new Task(TaskType.EVENT, description, suffix, null, false);
    }

    /**
     * Reconstructs a task from fields stored in the data file.
     *
     * @param type stored task type
     * @param isDone stored completion state
     * @param description stored task description
     * @param details stored deadline date or display suffix
     * @return the reconstructed task
     */
    static Task fromStorage(TaskType type, boolean isDone, String description, String details) {
        return switch (type) {
        case TODO -> new Task(type, description, "", null, isDone);
        case DEADLINE -> new Task(type, description, "", LocalDate.parse(details), isDone);
        case EVENT -> new Task(type, description, details, null, isDone);
        };
    }

    /**
     * Marks this task as completed.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as incomplete.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns this task's category.
     *
     * @return the task type
     */
    public TaskType getType() {
        return type;
    }

    /**
     * Returns whether this task is completed.
     *
     * @return {@code true} if the task is completed
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns this task's description.
     *
     * @return the task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns task-type-specific details in their storage representation.
     *
     * @return an ISO deadline date or a display suffix
     */
    String getStorageDetails() {
        return type == TaskType.DEADLINE ? deadlineDate.toString() : suffix;
    }

    /**
     * Formats this task for display to the user.
     *
     * @return the formatted task
     */
    @Override
    public String toString() {
        String status = isDone ? "X" : " ";
        String displaySuffix = type == TaskType.DEADLINE
                ? " (by: " + deadlineDate.format(DISPLAY_DATE_FORMAT) + ")"
                : suffix;
        return "[" + type.getMarker() + "][" + status + "] " + description + displaySuffix;
    }
}
