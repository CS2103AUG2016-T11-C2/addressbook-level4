package seedu.address.logic.commands;

import java.util.EmptyStackException;

public class UndoCommand extends Command {

    public static final String COMMAND_WORD = "undo";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Undoes the most recent command in ForgetMeNot. "
            + "Example: " + COMMAND_WORD;

    public static final String MESSAGE_SUCCESS = "Command undone!";
    public static final String MESSAGE_UNDO_INVALID = "There were no recent commands to be undone.";
    
    @Override
    public CommandResult execute() {
        assert model != null;
        try {
            model.loadFromHistory();
            return new CommandResult(MESSAGE_SUCCESS);
        } catch (EmptyStackException e) {
            return new CommandResult(MESSAGE_UNDO_INVALID);
        }
    }   

}
