package catgpt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests task collection operations in {@link TaskList}.
 */
class TaskListTest {
    @Test
    void addTaskStoresTaskAtEnd() throws CatGPTException {
        TaskList tasks = new TaskList();
        Task task = new Todo("read book");

        tasks.add(task);

        assertEquals(1, tasks.size());
        assertSame(task, tasks.get(0));
    }

    @Test
    void deleteTaskRemovesAndReturnsSelectedTask() {
        Task firstTask = new Todo("read book");
        Task secondTask = new Todo("write notes");
        TaskList tasks = new TaskList(List.of(firstTask, secondTask));

        Task deletedTask = tasks.delete(0);

        assertSame(firstTask, deletedTask);
        assertEquals(1, tasks.size());
        assertSame(secondTask, tasks.get(0));
    }

    @Test
    void markTaskUpdatesDisplayedStatus() throws CatGPTException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        tasks.get(0).markAsDone();

        assertEquals("[T][X] read book", tasks.get(0).toString());
    }
}
