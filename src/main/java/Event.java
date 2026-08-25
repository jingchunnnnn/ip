/**
 * Represents a task that occurs between specified start and end details.
 */
public class Event extends Task {
    private final String timingDetails;

    /**
     * Creates an incomplete event task.
     *
     * @param description task description
     * @param from event start details
     * @param to event end details
     */
    public Event(String description, String from, String to) {
        this(description, " (from: " + from + " to: " + to + ")", false);
    }

    /**
     * Reconstructs an event task from its stored timing details.
     *
     * @param description task description
     * @param timingDetails formatted event timing details
     * @param isDone whether the task has been completed
     */
    Event(String description, String timingDetails, boolean isDone) {
        super(description, isDone);
        this.timingDetails = timingDetails;
    }

    @Override
    public TaskType getType() {
        return TaskType.EVENT;
    }

    @Override
    public String getStorageDetails() {
        return timingDetails;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + timingDetails;
    }
}
