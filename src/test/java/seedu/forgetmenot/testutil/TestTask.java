package seedu.forgetmenot.testutil;

import java.util.Calendar;

import seedu.forgetmenot.model.task.Done;
import seedu.forgetmenot.model.task.Name;
import seedu.forgetmenot.model.task.ReadOnlyTask;
import seedu.forgetmenot.model.task.Recurrence;
import seedu.forgetmenot.model.task.Time;

/**
 * A mutable task object. For testing only.
 */
public class TestTask implements ReadOnlyTask {

    private Name name;
    private Time end;
    private Time start;
    private Done done;
    private Recurrence recurrence;

    public void setName(Name name) {
        this.name = name;
    }

    public void setEndTime(Time end) {
        this.end = end;
    }

    public void setStartTime(Time start) {
        this.start = start;
    }

    public void setDone(Done done) {
        this.done = done;
    }
    
    public void setRecurrence(Recurrence recurrence) {
		this.recurrence = recurrence;
	}

    @Override
    public Name getName() {
        return name;
    }

    @Override
    public Done getDone() {
        return done;
    }

    @Override
    public Time getStartTime() {
        return start;
    }

    @Override
    public Time getEndTime() {
        return end;
    }
    
    @Override
    public Recurrence getRecurrence() {
    	return recurrence;
    }

    @Override
    public String toString() {
        return getAsText();
    }

    public String getAddCommand() {
        StringBuilder sb = new StringBuilder();
        sb.append("add " + this.getName().fullName + " ");
//        sb.append("d/" + this.getDate().value + " ");
        sb.append("from " + this.getStartTime().appearOnUIFormat() + " ");
        sb.append("to " + this.getEndTime().appearOnUIFormat() + " ");
        return sb.toString();
    }

    //@@author A0139671X
	@Override
	public boolean checkOverdue() {
		if (start.isMissing() && !end.isMissing()) {
            return end.time.compareTo(Calendar.getInstance()) < 0;
		}
		
        if (!start.isMissing()) {
            return start.time.compareTo(Calendar.getInstance()) < 0;
        }
        return false;
	}
	
    @Override
    public boolean isStartTask() {
        return !start.isMissing() && end.isMissing();
    }

    @Override
    public boolean isDeadlineTask() {
        return start.isMissing() && !end.isMissing();
    }

    @Override
    public boolean isEventTask() {
        return !start.isMissing() && !end.isMissing();
    }
    
    @Override
    public boolean isFloatingTask() {
        return start.isMissing() && end.isMissing();
    }
    //@@author
    
	@Override
	public boolean isDone() {
		return done.getDoneValue();
	}

}
