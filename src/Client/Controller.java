package Client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;

import javax.swing.*;
import java.io.IOException;


public class Controller {
    @FXML
    private TextField textInput;
    @FXML
    private TextArea textArea;
    @FXML
    private Button send;
    @FXML
    private TabPane tabs;
    @FXML
    private void sendText(ActionEvent e){
        System.out.println(textInput.getText());
        int numTabs = tabs.getTabs().size();
        Tab newTab = new Tab("Tab" + (numTabs + 1));
        tabs.getTabs().add(newTab);
        try {
            tabs.getTabs().addAll((Tab) FXMLLoader.load(this.getClass().getResource("tab.fxml")));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

}
