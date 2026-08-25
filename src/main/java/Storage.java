import java.io.IOException;
import java.time.DateTimeException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Loads and saves CatGPT tasks using a file relative to the project directory.
 */
public class Storage {
    private static final String FIELD_SEPARATOR = " | ";
    private static final String FIELD_SEPARATOR_REGEX = " \\| ";
    private static final int FIELD_COUNT = 4;

    private final Path filePath;

    /**
     * Creates storage backed by the specified file path.
     *
     * @param filePath path to the task data file
     */
    public Storage(String filePath) {
        this.filePath = Path.of(filePath);
    }

    /**
     * Loads saved tasks into the supplied task array.
     *
     * @param tasks task array to populate
     * @return the number of tasks loaded
     * @throws CatGPTException if the file cannot be read or contains invalid data
     */
    public int load(Task[] tasks) throws CatGPTException {
        if (!Files.exists(filePath)) {
            return 0;
        }

        try {
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            if (lines.size() > tasks.length) {
                throw new CatGPTException("The saved task list contains too many tasks.");
            }

            for (int i = 0; i < lines.size(); i++) {
                tasks[i] = loadTask(lines.get(i), i);
            }
            return lines.size();
        } catch (IOException error) {
            throw new CatGPTException("I couldn't load tasks from " + filePath + ".");
        }
    }

    /**
     * Saves all current tasks, creating the data directory and file when necessary.
     *
     * @param tasks tasks to save
     * @param taskCount number of tasks to save
     * @throws CatGPTException if the task data cannot be written
     */
    public void save(Task[] tasks, int taskCount) throws CatGPTException {
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < taskCount; i++) {
            lines.add(tasks[i].getType().name()
                    + FIELD_SEPARATOR + (tasks[i].isDone() ? "1" : "0")
                    + FIELD_SEPARATOR + encode(tasks[i].getDescription())
                    + FIELD_SEPARATOR + encode(tasks[i].getStorageDetails()));
        }

        try {
            Path parentDirectory = filePath.getParent();
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }
            Files.write(filePath, lines, StandardCharsets.UTF_8);
        } catch (IOException error) {
            throw new CatGPTException("I couldn't save tasks to " + filePath + ".");
        }
    }

    /**
     * Parses one saved task.
     *
     * @param line serialized task data
     * @param index zero-based source line number
     * @return the reconstructed task
     * @throws CatGPTException if the serialized task is invalid
     */
    private Task loadTask(String line, int index) throws CatGPTException {
        String[] fields = line.split(FIELD_SEPARATOR_REGEX, -1);
        if (fields.length != FIELD_COUNT) {
            throw corruptedDataException(index);
        }

        try {
            TaskType taskType = TaskType.valueOf(fields[0]);
            if (!fields[1].equals("0") && !fields[1].equals("1")) {
                throw corruptedDataException(index);
            }
            boolean isDone = fields[1].equals("1");
            String description = decode(fields[2]);
            String details = decode(fields[3]);
            return Task.fromStorage(taskType, isDone, description, details);
        } catch (IllegalArgumentException | DateTimeException error) {
            throw corruptedDataException(index);
        }
    }

    /**
     * Encodes task text so separators inside user input cannot corrupt the data format.
     *
     * @param value text to encode
     * @return Base64-encoded text
     */
    private String encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Decodes text previously written by {@link #encode(String)}.
     *
     * @param value Base64-encoded text
     * @return decoded text
     */
    private String decode(String value) {
        byte[] decodedBytes = Base64.getDecoder().decode(value);
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }

    /**
     * Creates a user-friendly error identifying a corrupted data-file line.
     *
     * @param index zero-based line index
     * @return the corruption error
     */
    private CatGPTException corruptedDataException(int index) {
        return new CatGPTException("The saved task data is corrupted at line " + (index + 1) + ".");
    }
}
