package catgpt;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controls the main CatGPT chat window defined in FXML.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    private CatGPT chatbot;

    /**
     * Keeps the newest conversation entry visible.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Supplies the application logic used to process user commands.
     *
     * @param chatbot CatGPT instance backing this window.
     */
    public void setChatbot(CatGPT chatbot) {
        this.chatbot = chatbot;
        dialogContainer.getChildren().add(DialogBox.getCatDialog(chatbot.getWelcomeMessage()));
    }

    /**
     * Sends the current input to CatGPT and appends both sides of the exchange.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }

        String response = chatbot.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                DialogBox.getCatDialog(response));
        userInput.clear();
    }
}
