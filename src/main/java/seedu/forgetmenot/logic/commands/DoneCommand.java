package seedu.forgetmenot.logic.commands;

import seedu.forgetmenot.commons.core.Messages;
import seedu.forgetmenot.commons.core.UnmodifiableObservableList;
import seedu.forgetmenot.model.task.ReadOnlyTask;
import seedu.forgetmenot.model.task.UniqueTaskList.TaskNotFoundException;

//@@author A0139198N
/**
 * Mark a task as done identified using it's last displayed index from the task manager.
 *
 */
public class DoneCommand extends Command {

    public static final String COMMAND_WORD = "done";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Mark a task as done identified by the index number used in the last task listing.\n"
            + "\tParameters: INDEX (must be a positive integer)\n"
            + "\tExample: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_DONE_TASK_SUCCESS = "Task marked as done: %1$s";

    public final int targetIndex;

    public DoneCommand(int targetIndex) {
        this.targetIndex = targetIndex;
    }


    @Override
    public CommandResult execute() {

        UnmodifiableObservableList<ReadOnlyTask> lastShownList = model.getFilteredTaskList();

        if (lastShownList.size() < targetIndex) {
            indicateAttemptToExecuteIncorrectCommand();
            return new CommandResult(Messages.MESSAGE_INVALID_TASK_DISPLAYED_INDEX);
        }

        ReadOnlyTask taskToMark = lastShownList.get(targetIndex - 1);

        try {
            model.saveToHistory();
            model.doneTask(taskToMark);
            model.updateFilteredTaskListToShowNotDone();
        } catch (TaskNotFoundException pnfe) {
            assert false : "The target task cannot be missing";
        }

        return new CommandResult(String.format(MESSAGE_DONE_TASK_SUCCESS, taskToMark));
    }

}
