package seedu.address.model;

import javafx.collections.ObservableList;
import seedu.address.model.task.Task;
import seedu.address.model.task.Time;
import seedu.address.model.task.Name;
import seedu.address.model.task.ReadOnlyTask;
import seedu.address.model.task.UniqueTaskList;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.tag.Tag;
import seedu.address.model.tag.UniqueTagList;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Wraps all data at the task-manager level
 * Duplicates are not allowed (by .equals comparison)
 */
public class TaskManager implements ReadOnlyTaskManager {

    private final UniqueTaskList tasks;
    private final UniqueTagList tags;
    
    private static int floatingCounter;
    private static int todayCounter;
    private static int tomorrowCounter;
    private static int upcomingCounter;
    private static int overdueCounter;
    
    {
        tasks = new UniqueTaskList();
        tags = new UniqueTagList();
    }

    public TaskManager() {}

    /**
     * Tasks and Tags are copied into this taskmanager
     */
    public TaskManager(ReadOnlyTaskManager toBeCopied) {
        this(toBeCopied.getUniqueTaskList(), toBeCopied.getUniqueTagList());
    }

    /**
     * Tasks and Tags are copied into this taskmanager
     */
    public TaskManager(UniqueTaskList tasks, UniqueTagList tags) {
        resetData(tasks.getInternalList(), tags.getInternalList());
    }

    public static ReadOnlyTaskManager getEmptyTaskManager() {
        return new TaskManager();
    }

//// list overwrite operations

    public ObservableList<Task> getTasks() {
        return tasks.getInternalList();
    }

    public void setTasks(List<Task> tasks) {
        this.tasks.getInternalList().setAll(tasks);
    }

    public void setTags(Collection<Tag> tags) {
        this.tags.getInternalList().setAll(tags);
    }

    public void resetData(Collection<? extends ReadOnlyTask> newTasks, Collection<Tag> newTags) {
        setTasks(newTasks.stream().map(Task::new).collect(Collectors.toList()));
        setTags(newTags);
    }

    public void resetData(ReadOnlyTaskManager newData) {
        resetData(newData.getTaskList(), newData.getTagList());
    }

//// task-level operations

    /**
     * Adds a task to the task manager.
     * Also checks the new task's tags and updates {@link #tags} with any new tags found,
     * and updates the Tag objects in the task to point to those in {@link #tags}.
     *
     * @throws UniqueTaskList.DuplicateTaskException if an equivalent task already exists.
     */
    public void addTask(Task p) throws UniqueTaskList.DuplicateTaskException {
        syncTagsWithMasterList(p);
        counter(p);
        tasks.add(p);
    }

    /**
     * Ensures that every tag in this task:
     *  - exists in the master list {@link #tags}
     *  - points to a Tag object in the master list
     */
    private void syncTagsWithMasterList(Task task) {
        final UniqueTagList taskTags = task.getTags();
        tags.mergeFrom(taskTags);

        // Create map with values = tag object references in the master list
        final Map<Tag, Tag> masterTagObjects = new HashMap<>();
        for (Tag tag : tags) {
            masterTagObjects.put(tag, tag);
        }

        // Rebuild the list of task tags using references from the master list
        final Set<Tag> commonTagReferences = new HashSet<>();
        for (Tag tag : taskTags) {
            commonTagReferences.add(masterTagObjects.get(tag));
        }
        task.setTags(new UniqueTagList(commonTagReferences));
    }

    public boolean removeTask(ReadOnlyTask key) throws UniqueTaskList.TaskNotFoundException {
        if (tasks.remove(key)) {
            return true;
        } else {
            throw new UniqueTaskList.TaskNotFoundException();
        }
    }
    
    public void doneTask(ReadOnlyTask task) throws UniqueTaskList.TaskNotFoundException {
    	tasks.done(task);
    }
	
    public void clearDone() throws UniqueTaskList.TaskNotFoundException{
	   	for (int i = 0; i < tasks.getInternalList().size(); i++ ) {
			if (tasks.getInternalList().get(i).getDone().getDoneValue() == true) {
				tasks.remove(tasks.getInternalList().get(i));
				i--;
			}
		}
	}
    
    public void undoneTask(ReadOnlyTask task) throws UniqueTaskList.TaskNotFoundException {
    	tasks.undone(task);
    }

    public void editTaskName(ReadOnlyTask task, String newInfo) throws UniqueTaskList.TaskNotFoundException, IllegalValueException {
        tasks.setTaskName(task, new Name(newInfo));
    }
    
    public void editTaskStartTime(ReadOnlyTask task, String newInfo) throws UniqueTaskList.TaskNotFoundException, IllegalValueException {
        	tasks.setStartTime(task, new Time(newInfo));
    }
    
    public void editTaskEndTime(ReadOnlyTask task, String newInfo) throws UniqueTaskList.TaskNotFoundException, IllegalValueException {
        	tasks.setEndTime(task, new Time(newInfo));
    }
    
//// tag-level operations

    public void addTag(Tag t) throws UniqueTagList.DuplicateTagException {
        tags.add(t);
    }

//// util methods

    @Override
    public String toString() {
        return tasks.getInternalList().size() + " tasks, " + tags.getInternalList().size() +  " tags";
        // TODO: refine later
    }

    @Override
    public List<ReadOnlyTask> getTaskList() {
        return Collections.unmodifiableList(tasks.getInternalList());
    }

    @Override
    public List<Tag> getTagList() {
        return Collections.unmodifiableList(tags.getInternalList());
    }

    @Override
    public UniqueTaskList getUniqueTaskList() {
        return this.tasks;
    }

    @Override
    public UniqueTagList getUniqueTagList() {
        return this.tags;
    }


    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof TaskManager // instanceof handles nulls
                && this.tasks.equals(((TaskManager) other).tasks)
                && this.tags.equals(((TaskManager) other).tags));
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(tasks, tags);
    }


	private void counter(Task p) {
		 for (int i = 0; i < tasks.getInternalList().size(); i++) {
			 if (p.getStartTime().isMissing() && p.getEndTime().isMissing()) {
				 floatingCounter++;
				 System.out.println(floatingCounter);
			 }
			 if (p.getStartTime().isToday(p.getStartTime().appearOnUIFormatForDate())
					 || p.getEndTime().isToday(p.getEndTime().appearOnUIFormatForDate())) {
				 todayCounter++;
				 System.out.println(todayCounter);
			 }
			 if (p.getStartTime().isTomorrow(p.getStartTime().appearOnUIFormatForDate())
					 || p.getEndTime().isTomorrow(p.getEndTime().appearOnUIFormatForDate())) {
				 tomorrowCounter++;
				 System.out.println(tomorrowCounter);
			 }
		 }
		
	}
	
}
