package seedu.forgetmenot.model;

import seedu.forgetmenot.commons.core.UnmodifiableObservableList;
import seedu.forgetmenot.commons.exceptions.IllegalValueException;
import seedu.forgetmenot.model.task.ReadOnlyTask;
import seedu.forgetmenot.model.task.Task;
import seedu.forgetmenot.model.task.UniqueTaskList;
import seedu.forgetmenot.model.task.UniqueTaskList.DuplicateTaskException;
import seedu.forgetmenot.model.task.UniqueTaskList.TaskNotFoundException;

import java.util.Set;
import java.util.function.Predicate;

/**
 * The API of the Model component.
 */
public interface Model {
    /** Clears existing backing model and replaces with the provided new data. */
    void resetData(ReadOnlyTaskManager newData);

    /** Returns the TaskManager */
    ReadOnlyTaskManager getTaskManager();

    /** Deletes the given task. */
    void deleteTask(ReadOnlyTask target) throws UniqueTaskList.TaskNotFoundException;

    /** Adds the given task 
     * @@author A0147619W
     * */
    void addTask(Task task) throws UniqueTaskList.DuplicateTaskException;
 
    /** Adds the given recurring task again with the correct date and time*/
    void addRecurringTask(ReadOnlyTask taskToMark, String recurringDays) throws DuplicateTaskException, IllegalValueException;
    
    /** Edits the given task */
    void editTask(ReadOnlyTask task, String newName, String newInfo, String newEnd, String newRecur) throws TaskNotFoundException, IllegalValueException;
    
    /** Marks the given task as done */
    void doneTask(ReadOnlyTask target) throws UniqueTaskList.TaskNotFoundException;
    
    /** Marks the given task as undone */
    void undoneTask (ReadOnlyTask target) throws UniqueTaskList.TaskNotFoundException;
    
    /** Saves the current task manager*/
    void saveToHistory();
    
    /** Loads the previous task manager from the stored stack of task managers*/
    void loadFromHistory();
    
    /** Loads the previous task manager from the stored stack of undone task managers*/
    void loadFromUndoHistory();    
    
    /** Clears the contents of the undo and redo collections from the task manager*/
    void clearHistory();

    /** Returns the filtered task list as an {@code UnmodifiableObservableList<ReadOnlyTask>} */
    UnmodifiableObservableList<ReadOnlyTask> getFilteredTaskList();

    /** Updates the filter of the filtered task list to show all tasks */
    void updateFilteredListToShowAll();

    /** Updates the filter of the filtered task list to filter by the given keywords*/
    void updateFilteredTaskList(Set<String> keywords);
    
    void updateFilteredTaskListToShow(Predicate<Task> predicate);
    
    /** Clears all the done tasks in the list
     * @throws TaskNotFoundException */
    void clearDone() throws TaskNotFoundException;
    
    //@@author A0147619W
    void sortTasks();

}