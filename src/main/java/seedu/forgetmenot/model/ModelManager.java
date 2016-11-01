package seedu.forgetmenot.model;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.transformation.FilteredList;
import seedu.forgetmenot.commons.core.ComponentManager;
import seedu.forgetmenot.commons.core.LogsCenter;
import seedu.forgetmenot.commons.core.UnmodifiableObservableList;
import seedu.forgetmenot.commons.events.model.TaskManagerChangedEvent;
import seedu.forgetmenot.commons.exceptions.IllegalValueException;
import seedu.forgetmenot.commons.util.StringUtil;
import seedu.forgetmenot.model.task.Done;
import seedu.forgetmenot.model.task.ReadOnlyTask;
import seedu.forgetmenot.model.task.Recurrence;
import seedu.forgetmenot.model.task.Task;
import seedu.forgetmenot.model.task.Time;
import seedu.forgetmenot.model.task.UniqueTaskList;
import seedu.forgetmenot.model.task.UniqueTaskList.DuplicateTaskException;
import seedu.forgetmenot.model.task.UniqueTaskList.TaskNotFoundException;

/**
 * Represents the in-memory model of the task manager data. All changes to any
 * model should be synchronized.
 */
public class ModelManager extends ComponentManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final TaskManager taskManager;
    private final FilteredList<Task> filteredTasks;
    private Deque<TaskManager> taskManagerHistory = new ArrayDeque<TaskManager>();
    private Deque<TaskManager> undoHistory = new ArrayDeque<TaskManager>();

    /**
     * Initializes a ModelManager with the given TaskManager TaskManager and its
     * variables should not be null
     */
    public ModelManager(TaskManager src, UserPrefs userPrefs) {
        super();
        assert src != null;
        assert userPrefs != null;

        logger.fine("Initializing with task manager: " + src + " and user prefs " + userPrefs);

        taskManager = new TaskManager(src);
        filteredTasks = new FilteredList<>(taskManager.getTasks());
    }

    public ModelManager() {
        this(new TaskManager(), new UserPrefs());
    }

    public ModelManager(ReadOnlyTaskManager initialData, UserPrefs userPrefs) {
        taskManager = new TaskManager(initialData);
        filteredTasks = new FilteredList<>(taskManager.getTasks());
    }

    @Override
    public void resetData(ReadOnlyTaskManager newData) {
        taskManager.resetData(newData);
        indicateTaskManagerChanged();
    }

    //@@author A0139198N
    @Override
    public void clearDone() throws TaskNotFoundException {
        taskManager.clearDone();
        indicateTaskManagerChanged();
    }

    //@@author A0139671X
    public void clearHistory() {
        taskManagerHistory.clear();
        undoHistory.clear();
    }

    @Override
    public ReadOnlyTaskManager getTaskManager() {
        return taskManager;
    }

    //@@author A0139671X
    @Override
    public void saveToHistory() {
        taskManagerHistory.push(new TaskManager(taskManager));
        undoHistory.clear();
    }

    //@@author A0139671X
    @Override
    public void loadFromHistory() throws NoSuchElementException {
        TaskManager oldManager = taskManagerHistory.pop();
        undoHistory.push(new TaskManager(taskManager));
        taskManager.setTasks(oldManager.getTasks());
        taskManager.counter();
        indicateTaskManagerChanged();
    }

    //@@author A0139671X
    @Override
    public void loadFromUndoHistory() throws NoSuchElementException {
        TaskManager oldManager = undoHistory.pop();
        taskManagerHistory.push(new TaskManager(taskManager));
        taskManager.setTasks(oldManager.getTasks());
        taskManager.counter();
        indicateTaskManagerChanged();
    }

    /** Raises an event to indicate the model has changed */
    private void indicateTaskManagerChanged() {
        raise(new TaskManagerChangedEvent(taskManager));
    }

    @Override
    public synchronized void deleteTask(ReadOnlyTask target) throws TaskNotFoundException {
        taskManager.removeTask(target);
        indicateTaskManagerChanged();
    }

    // @@author A0147619W
    @Override
    public synchronized void sortTasks() {
        taskManager.sortTasksList();
    }

    //@@author A0139198N
    @Override
    public synchronized void doneTask(ReadOnlyTask target) throws TaskNotFoundException {
        taskManager.doneTask(target);
        updateFilteredTaskListToShowNotDone();
        indicateTaskManagerChanged();

    }

    //@@author A0139198N
    @Override
    public synchronized void undoneTask(ReadOnlyTask target) throws TaskNotFoundException {
        taskManager.undoneTask(target);
        updateFilteredTaskListToShowDone();
        indicateTaskManagerChanged();

    }

    //@@author A0147619W
    @Override
    public synchronized void addTask(Task task) {
        taskManager.addTask(task);
        updateFilteredListToShowAll();
        indicateTaskManagerChanged();
    }

    //@@author A0139671X
    @Override
    public synchronized void addRecurringTask(ReadOnlyTask task) throws IllegalValueException {

        String freq = task.getRecurrence().getRecurFreq();
        int occur = task.getRecurrence().getOccurence();

        // Recurring task with only start time.
       if (task.isStartTask()) {
            StringBuilder recurStartTime = new StringBuilder(task.getStartTime().appearOnUIFormat());
            for (int i = 0; i < occur - 1; i++) {
                recurStartTime.insert(0, freq + " after ");
                addTask(new Task(task.getName(), new Done(false), new Time(recurStartTime.toString()), new Time(""),
                        new Recurrence(task.getRecurrence().getRecurFreq())));
            }
        }

       // Recurring task with only end time.
       else if (task.isDeadlineTask()) {
           StringBuilder recurEndTime = new StringBuilder(task.getEndTime().appearOnUIFormat());
           for (int i = 0; i < occur - 1; i++) {
               recurEndTime.insert(0, freq + " after ");
               addTask(new Task(task.getName(), new Done(false), new Time(""), new Time(recurEndTime.toString()),
                       new Recurrence(task.getRecurrence().getRecurFreq())));
           }
       }
       
        // Recurring task with both start and end times
        else if (task.isEventTask()) {
            StringBuilder recurStartTime = new StringBuilder(task.getStartTime().appearOnUIFormat());
            StringBuilder recurEndTime = new StringBuilder(task.getEndTime().appearOnUIFormat());
            for (int i = 0; i < occur - 1; i++) {
                recurStartTime.insert(0, freq + " after ");
                recurEndTime.insert(0, freq + " after ");
                addTask(new Task(task.getName(), new Done(false), new Time(recurStartTime.toString()),
                        new Time(recurEndTime.toString()), new Recurrence(task.getRecurrence().getRecurFreq())));
            }
        }

        updateFilteredTaskListToShowNotDone();
        indicateTaskManagerChanged();
    }

    //@@author A0139671X
    @Override
    public synchronized void editTask(ReadOnlyTask task, String newName, String newStart, String newEnd,
            String newRecur) throws TaskNotFoundException, IllegalValueException {
        if (newName != null)
            taskManager.editTaskName(task, newName);

        if (newStart != null)
            taskManager.editTaskStartTime(task, newStart);

        if (newEnd != null)
            taskManager.editTaskEndTime(task, newEnd);

        if (newRecur != null)
            taskManager.editTaskRecurFreq(task, newRecur);

        updateFilteredListToShowAll();
        indicateTaskManagerChanged();
    }
    
    @Override
    public synchronized boolean isClashing(Task toAdd) {
    	Time start = toAdd.getStartTime();
    	Time end = toAdd.getEndTime();
    	
    	if(start.isMissing() && end.isMissing())
    		return false;
    	
    	if(!start.isMissing() && !end.isMissing()) {
    		for(Task task: taskManager.getTasks().filtered(isNotDone())) {
    			if(!task.getStartTime().isMissing() && 
    					task.getStartTime().time.compareTo(start.time) >= 0 && 
    					task.getStartTime().time.compareTo(end.time) < 0)
    				return true;
    			
    			if(!task.getEndTime().isMissing() && 
    					task.getEndTime().time.compareTo(start.time) > 0 && 
    					task.getEndTime().time.compareTo(end.time) <= 0)
    				return true;	
    		}
    	}
    	
    	if(!start.isMissing()) {
    		for(Task task: taskManager.getTasks().filtered(isNotDone())) {
    			if(!task.getStartTime().isMissing() && task.getStartTime().time.compareTo(start.time) == 0) {
    				System.out.println("a");
    				return true;
    			}
    			if(!task.getStartTime().isMissing() && !task.getEndTime().isMissing() && 
    					task.getStartTime().time.compareTo(start.time) <= 0 && 
    					task.getEndTime().time.compareTo(start.time) > 0) {
    				System.out.println("b");
    				return true;
    			}
    		}
    	}
    	
    	if(!end.isMissing()) {
    		for(Task task: taskManager.getTasks().filtered(isNotDone())) {
    			if(!task.getEndTime().isMissing() && task.getEndTime().time.compareTo(end.time) == 0) {
    				System.out.println("c");
    				return true;
    			}
    			if(!task.getStartTime().isMissing() && !task.getEndTime().isMissing() && 
    					task.getStartTime().time.compareTo(end.time) <= 0 && 
    					task.getEndTime().time.compareTo(end.time) > 0) {
    				System.out.println("d");
    				return true;
    			}
    		}
    	}
    	
    	return false;
    }

    // =========== Filtered Task List Accessors
    // ===============================================================

    @Override
    public UnmodifiableObservableList<ReadOnlyTask> getFilteredTaskList() {
        return new UnmodifiableObservableList<>(filteredTasks);
    }

    //@@author A0147619W
    @Override
    public void updateFilteredListToShowAll() {
        sortTasks();
        filteredTasks.setPredicate(null);
    }

    @Override
    public void updateFilteredTaskList(Set<String> keywords) {
        sortTasks();
        updateFilteredTaskList(new PredicateExpression(new NameQualifier(keywords)));
    }

    private void updateFilteredTaskList(Expression expression) {
        sortTasks();
        filteredTasks.setPredicate(expression::satisfies);
    }

    //@@author A0139198N
    @Override
    public void updateFilteredTaskListToShowDone() {
        sortTasks();
        filteredTasks.setPredicate(isDone());
        taskManager.counter();
    }

    //@@author A0139198N
    @Override
    public void updateFilteredTaskListToShowNotDone() {
        sortTasks();
        filteredTasks.setPredicate(isNotDone());
        taskManager.counter();
    }

    //@@author A0139198N
    @Override
    public void updateFilteredTaskListToShowDate(String date) {
        sortTasks();
        filteredTasks.setPredicate(filterByDate(date));
        taskManager.counter();
    }

    //@@author A0139198N
    @Override
    public void updateFilteredTaskListToShowOverdue() {
    	sortTasks();
        filteredTasks.setPredicate(isOverdue());
        taskManager.counter();
    }

    // ========== Inner classes/interfaces used for filtering
    // ==================================================

    interface Expression {
        boolean satisfies(ReadOnlyTask task);

        String toString();
    }

    private class PredicateExpression implements Expression {

        private final Qualifier qualifier;

        PredicateExpression(Qualifier qualifier) {
            this.qualifier = qualifier;
        }

        @Override
        public boolean satisfies(ReadOnlyTask task) {
            return qualifier.run(task);
        }

        @Override
        public String toString() {
            return qualifier.toString();
        }
    }

    interface Qualifier {
        boolean run(ReadOnlyTask task);

        String toString();
    }

    private class NameQualifier implements Qualifier {
        private Set<String> nameKeyWords;

        NameQualifier(Set<String> nameKeyWords) {
            this.nameKeyWords = nameKeyWords;
        }

        @Override
        public boolean run(ReadOnlyTask task) {
            return nameKeyWords.stream()
                    .filter(keyword -> StringUtil.containsIgnoreCase(task.getName().fullName, keyword)).findAny()
                    .isPresent();
        }

        @Override
        public String toString() {
            return "name=" + String.join(", ", nameKeyWords);
        }
    }

    //@@author A0139198N
    public static Predicate<Task> isDone() {
        return t -> t.getDone().value == true;
    }

    //@@author A0139198N
    public static Predicate<Task> filterByDate(String date) {
        return t -> (t.getStartTime().appearOnUIFormatForDate().equals(date)
                || t.getEndTime().appearOnUIFormatForDate().equals(date));
    }

    //@@author A0139198N
    public static Predicate<Task> isNotDone() {
        return t -> t.getDone().value == false;
    }

    //@@author A0139198N
    public static Predicate<Task> isOverdue() {
        return t -> t.checkOverdue() == true && t.getDone().value == false;
    }
}
