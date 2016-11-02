package seedu.forgetmenot.ui;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import seedu.forgetmenot.model.task.ReadOnlyTask;
//@@author A0139211R
/**
 * 
 * Card to be used in Floating panel
 *
 */
public class FloatCard extends UiPart{

    private static final String FXML = "FloatingCard.fxml";

    @FXML
    private HBox cardPane;
    @FXML
    private Label floatname;

    private ReadOnlyTask task;


    public FloatCard(){

    }

    public static FloatCard load(ReadOnlyTask task){
        FloatCard card = new FloatCard();
        card.task = task;
        return UiPartLoader.loadUiPart(card);
    }
    
    // Displays the name of the task on every floating card cell in the list.
    @FXML
    public void initialize() {
		floatname.setText(task.getName().fullName);
    }

    public HBox getLayout() {
        return cardPane;
    }

    @Override
    public void setNode(Node node) {
        cardPane = (HBox)node;
    }

    @Override
    public String getFxmlPath() {
        return FXML;
    }
}
