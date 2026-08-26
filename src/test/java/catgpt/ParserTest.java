package catgpt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Tests command recognition and argument validation in {@link Parser}.
 */
class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void parseTodoCommandReturnsTypeAndArguments() throws CatGPTException {
        Parser.ParsedCommand command = parser.parse("todo read book");

        assertEquals(Parser.CommandType.TODO, command.getType());
        assertEquals("read book", command.getArguments());
    }

    @Test
    void parseTaskValidDeadlineReturnsDeadline() throws CatGPTException {
        Parser.ParsedCommand command = parser.parse("deadline return book /by 2028-02-29");

        Task task = parser.parseTask(command);

        assertInstanceOf(Deadline.class, task);
        assertEquals("[D][ ] return book (by: Feb 29 2028)", task.toString());
    }

    @Test
    void parseTaskInvalidDeadlineThrowsException() throws CatGPTException {
        Parser.ParsedCommand command = parser.parse("deadline return book /by 2027-02-29");

        CatGPTException exception = assertThrows(
                CatGPTException.class, () -> parser.parseTask(command));
        assertEquals(
                "The deadline date must use yyyy-MM-dd and be a valid date.",
                exception.getMessage());
    }

    @Test
    void parseTaskIndexOutOfRangeThrowsException() throws CatGPTException {
        Parser.ParsedCommand command = parser.parse("mark 3");

        CatGPTException exception = assertThrows(
                CatGPTException.class, () -> parser.parseTaskIndex(command, 2));
        assertEquals("That task number is not in your list.", exception.getMessage());
    }

    @Test
    void parseKeywordMissingKeywordThrowsException() throws CatGPTException {
        Parser.ParsedCommand command = parser.parse("find");

        CatGPTException exception = assertThrows(
                CatGPTException.class, () -> parser.parseKeyword(command));
        assertEquals("Please provide a keyword after 'find'.", exception.getMessage());
    }
}
