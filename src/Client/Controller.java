package Client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import javax.swing.*;


public class Controller {
    @FXML
    private TextField textInput;
    @FXML
    private TextArea textArea;
    @FXML
    private Button send;

    private String

    @FXML
    private void sendText(ActionEvent e){
        System.out.println(textInput.getText());
    }

    public String getTextInput(){
        return textInput.getText();
    }
}
