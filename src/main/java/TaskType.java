/**
 * Identifies a task category and its marker in CatGPT's text output.
 */
public enum TaskType {
    TODO("T"),
    DEADLINE("D"),
    EVENT("E");

    private final String marker;

    /**
     * Creates a task type with the marker used when displaying a task.
     *
     * @param marker the single-letter marker for this task type
     */
    TaskType(String marker) {
        this.marker = marker;
    }

    /**
     * Returns the marker used when displaying this task type.
     *
     * @return the task type marker
     */
    public String getMarker() {
        return marker;
    }
}
