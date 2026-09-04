package catgpt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests task collection operations in {@link TaskList}.
 */
class TaskListTest {
    @Test
    void addNullTaskViolatesInternalInvariant() {
        TaskList tasks = new TaskList();

        assertThrows(AssertionError.class, () -> tasks.add(null));
    }

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

    @Test
    void findMatchingKeywordReturnsOnlyMatchingTasks() {
        Task firstMatch = new Todo("read book");
        Task nonMatch = new Todo("write notes");
        Task secondMatch = new Todo("return book");
        TaskList tasks = new TaskList(List.of(firstMatch, nonMatch, secondMatch));

        TaskList matchingTasks = tasks.find("book");

        assertEquals(2, matchingTasks.size());
        assertSame(firstMatch, matchingTasks.get(0));
        assertSame(secondMatch, matchingTasks.get(1));
    }
}
