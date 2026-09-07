package catgpt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests CatGPT's response API used by the graphical interface.
 */
public class CatGPTTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void getResponseValidCommandsReturnsUpdatedTaskList() {
        CatGPT chatbot = new CatGPT(temporaryDirectory.resolve("tasks.txt").toString());

        String addResponse = chatbot.getResponse("todo read book");
        String listResponse = chatbot.getResponse("list");

        assertTrue(addResponse.contains("[T][ ] read book"));
        assertTrue(listResponse.contains("1.[T][ ] read book"));
    }

    @Test
    public void getResponseInvalidCommandReturnsUserFriendlyError() {
        CatGPT chatbot = new CatGPT(temporaryDirectory.resolve("tasks.txt").toString());

        assertEquals("OOPS!!! I don't know what that means.", chatbot.getResponse("meow"));
    }

    @Test
    public void getResponseSortCommandSortsAndPersistsTasks() {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        CatGPT chatbot = new CatGPT(dataFile.toString());
        chatbot.getResponse("todo write report");
        chatbot.getResponse("todo attend meeting");

        String sortResponse = chatbot.getResponse("sort");
        CatGPT reloadedChatbot = new CatGPT(dataFile.toString());

        assertTrue(sortResponse.contains("1.[T][ ] attend meeting"));
        assertTrue(sortResponse.contains("2.[T][ ] write report"));
        assertTrue(reloadedChatbot.getResponse("list").contains(
                "1.[T][ ] attend meeting" + System.lineSeparator()
                        + "2.[T][ ] write report"));
    }
}
