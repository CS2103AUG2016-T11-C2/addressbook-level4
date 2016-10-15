package seedu.address.model.task;

import seedu.address.commons.util.CollectionUtil;
import seedu.address.model.tag.UniqueTagList;

import java.util.Objects;

/**
 * Represents a Task in the address book.
 * Guarantees: details are present and not null, field values are validated.
 */
public class Task implements ReadOnlyTask {

    private Name name;
    private Date date;
    private StartTime start;
    private EndTime end;
    private boolean done = false;
    
    private UniqueTagList tags;

    /**
     * Every field must be present and not null.
     */
    public Task(Name name, Date date, StartTime start, EndTime address, UniqueTagList tags) {
        assert !CollectionUtil.isAnyNull(name, date, start, address, tags);
        this.name = name;
        this.date = date;
        this.start = start;
        this.end = address;
        this.tags = new UniqueTagList(tags); // protect internal tags from changes in the arg list
    }

    /**
     * Copy constructor.
     */
    public Task(ReadOnlyTask source) {
        this(source.getName(), source.getDate(), source.getStartTime(), source.getEndTime(), source.getTags());
    }

    @Override
    public Name getName() {
        return name;
    }

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public StartTime getStartTime() {
        return start;
    }

    @Override
    public EndTime getEndTime() {
        return end;
    }

    @Override
    public UniqueTagList getTags() {
        return new UniqueTagList(tags);
    }
    
    @Override
    public boolean getDone() {
    	return done;
    }

    /**
     * Replaces this task's tags with the tags in the argument tag list.
     */
    public void setTags(UniqueTagList replacement) {
        tags.setTags(replacement);
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof ReadOnlyTask // instanceof handles nulls
                && this.isSameStateAs((ReadOnlyTask) other));
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, date, start, end, tags);
    }

    @Override
    public String toString() {
        return getAsText();
    }
    
    public void setDone() {
    	System.out.println("done");
    	this.done = true;
    }
    
    public void setUndone() {
    	System.out.println("undone");
    	this.done = false;
    }

}
