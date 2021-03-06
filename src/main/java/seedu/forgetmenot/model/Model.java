package seedu.forgetmenot.model;

import java.util.Set;

import seedu.forgetmenot.commons.core.UnmodifiableObservableList;
import seedu.forgetmenot.commons.exceptions.IllegalValueException;
import seedu.forgetmenot.model.task.ReadOnlyTask;
import seedu.forgetmenot.model.task.Task;
import seedu.forgetmenot.model.task.UniqueTaskList;
import seedu.forgetmenot.model.task.UniqueTaskList.TaskNotFoundException;

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

    /** Adds the given task  */
    void addTask(Task task);
 
    /** Adds the given recurring task again with the correct date and time
     * @throws IllegalValueException */
    void addRecurringTask(ReadOnlyTask taskToMark) throws IllegalValueException;
    
    /** Edits the given task */
    void editTask(ReadOnlyTask task, String newName, String newInfo, String newEnd) throws TaskNotFoundException, IllegalValueException;
    
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

    /** Clears all the done tasks in the list
     * @throws TaskNotFoundException */
    void clearDone() throws TaskNotFoundException;

    /**  Sorts all the tasks in the list according to date with floating tasks below */
    void sortTasks();
    
    /** Returns the filtered task list as an {@code UnmodifiableObservableList<ReadOnlyTask>} */
    UnmodifiableObservableList<ReadOnlyTask> getFilteredTaskList();
    
    /** Updates the filter of the filtered task list to show all tasks */
    void updateFilteredListToShowAll();
    
    /** Updates the filter of the filtered task list to filter by the given keywords*/
    void updateFilteredTaskList(Set<String> keywords);
    
    /** Updates the filter of the filtered task list to show done tasks */
	void updateFilteredTaskListToShowDone();

	/** Updates the filter of the filtered task list to show task by date */
	void updateFilteredTaskListToShowDate(String date);
	
	/** Updates the filter of the filtered task list to show undone tasks */
	void updateFilteredTaskListToShowNotDone();
	
	/** Updates the filter of the filtered task list to show overdue tasks */
	void updateFilteredTaskListToShowOverdue();
	
	/** Updates the filter of the filtered task list to show floating tasks */
	void updateFilteredTaskListToShowFloating();
	
	/** Checks if the current task clashes with other tasks in ForgetMeNot or not */
	boolean isClashing(Task toAdd);

	/** Returns the filtered task list for floating task as an {@code UnmodifiableObservableList<ReadOnlyTask>} */
	UnmodifiableObservableList<ReadOnlyTask> getFilteredTaskListForFloating();



}
