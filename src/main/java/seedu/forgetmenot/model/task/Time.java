package seedu.forgetmenot.model.task;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;

import seedu.forgetmenot.commons.exceptions.IllegalValueException;
import seedu.forgetmenot.logic.parser.DatePreParse;

//@@author A0147619W
/**
 * Represents a Task's time in the task manager.
 * Guarantees: immutable; is valid as declared in {@link #isValidStart(String)}
 */
public class Time {
    
    public static final String MESSAGE_TIME_CONSTRAINTS = "Invalid Time!";
    public static final String MESSAGE_INCORRECT_DATE_FORMAT = "The date provided is invalid.";
    private static final String DEFAULT_DATE = "Thu Jan 01 07:30:00 SGT 1970";
    
    public Calendar time;

    /**
     * Validates given start.
     *
     * @throws IllegalValueException if given start timing string is invalid.
     */
    public Time(String input) throws IllegalValueException {
        input = input.trim();

        time = Calendar.getInstance();
        
        if(input.contains("/")) {
        	if(!isValidDate(input)) {
        		throw new IllegalValueException(MESSAGE_INCORRECT_DATE_FORMAT);
        	}
        }
        
    	String taskTime = DatePreParse.preparse(input);
    	
    	if(!taskTime.isEmpty() && !taskTime.equals(new Date(0).toString())){
    		List<DateGroup> dates = new Parser().parse(taskTime); // Using the Natty Parser()
    		if(dates.isEmpty()){
    			throw new IllegalValueException(MESSAGE_TIME_CONSTRAINTS);
    		}
    		else if(dates.get(0).getDates().isEmpty()){
    			throw new IllegalValueException(MESSAGE_TIME_CONSTRAINTS);
    		}
    		else{
    			time.setTime(dates.get(0).getDates().get(0));
    		}
    	}
    	
    	else{
    		time.setTime(new Date(0));
    	}
    }
    
    /**
     * @return true if the time parameter is missing
     */
    public boolean isMissing() {
        
        Calendar t = Calendar.getInstance();
        t.setTime(new Date(0));
        if (time.equals(t))
            return true;
        
		return time.getTime().toString().equalsIgnoreCase(DEFAULT_DATE);
	}
    
    /**
     * Formats the time to human readable form
     * @return the formatted time
     */
    public String appearOnUIFormat() {
    	if(time.getTime().equals(new Date(0))) {
    		return "-";
    	}
    	else {
    		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy h:mm a");
    		return dateFormat.format(time.getTime());
    	}
    }
    //@@author
    //@@author A0139671X
    /**
     * @return a String for a date and time thats easy to read. 
     * Example, "Thu, Feb 17, 2016, 10:11 AM"
     */
    public String easyReadDateFormatForUI() {
        if (time.getTime().equals(new Date(0))) {
            return "-";
        }
        
        else {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("E, MMM d, yyyy, hh:mm a");
            return dateFormatter.format(time.getTime());
        }
    }
    //@@author
    
    //@@author A0139198N
    public String appearOnUIFormatForDate() {
    	if(time.getTime().equals(new Date(0))) {
    		return "-";
    	}
    	else {
    		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
    		return dateFormat.format(time.getTime());
    	}
    }
   
    public boolean isToday(String date) {
    	Calendar cal = Calendar.getInstance();
    	DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
    	if (date.equals(dateFormat.format(cal.getTime()))){
    		return true;
    	}
    	return false;
    }
    
    public boolean isTomorrow(String date) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 1);
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
    	
    	if(date.equals(dateFormat.format(cal.getTime()))) {
    		return true;
    	}
    	return false;
    }
    
    public boolean isUpcoming(String date) {
    	Calendar cal = Calendar.getInstance();
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
		
		String d0 = dateFormat.format(cal.getTime());
		
		cal.add(Calendar.DATE, 1);
		String d1 = dateFormat.format(cal.getTime());
		
		cal.add(Calendar.DATE, 1);
		String d2 = dateFormat.format(cal.getTime());
		
		cal.add(Calendar.DATE, 1);
		String d3 = dateFormat.format(cal.getTime());
		
		cal.add(Calendar.DATE, 1);
		String d4 = dateFormat.format(cal.getTime());
		
		cal.add(Calendar.DATE, 1);
		String d5 = dateFormat.format(cal.getTime());
		
		cal.add(Calendar.DATE, 1);
		String d6 = dateFormat.format(cal.getTime());
		
		if (date.equals(d0) || date.equals(d1) || date.equals(d2) || date.equals(d3) || date.equals(d4) ||
				date.equals(d5) || date.equals(d6)) {
			return true;
		}
		
		return false;
    }
    //@@author
    
    //@@author A0147619W
    /**
     * 
     * @param token
     * @return true if the given date is a valid date
     */
	public static boolean isValidDate(String token) {
		
		String[] date = token.split(" ");
		Pattern dateType = Pattern.compile("(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/(\\d\\d)");
		
		for(String input: date) {
			if(input.contains("/")) {
				token = input;
				break;
			}
		}
		
		Matcher matcher = dateType.matcher(token);
		
		if(!matcher.matches()){
			return false;
		} else {
			int day = Integer.parseInt(matcher.group(1));
			int month = Integer.parseInt(matcher.group(2));
			int year = Integer.parseInt(matcher.group(3));
			
			switch (month) {
		        case 1: case 3: case 5: case 7: case 8: case 10: case 12: 
		        	return day < 32;
		        case 4: case 6: case 9: case 11: 
		        	return day < 31;
		        case 2: 
		            if (year % 4 == 0)
		                return day < 30; //its a leap year
		            else
		                return day < 29;
		        default:
		            break;
	        }
			return false;
		}
	}
	
	 /**
     * Checks whether time1 < time2 or not
     * 
     * @return true only if time1 < time2
     * @throws IllegalValueException
     */
    public static boolean checkOrderOfDates(String time1, String time2) throws IllegalValueException {  
    	
    	Time start = new Time(time1);
    	Time end = new Time(time2);
    	
    	return end.isMissing() || start.time.compareTo(end.time) <= 0;
	}

    public static boolean checkOrderOfTime(Time firstTime, Time secondTime) {
        return firstTime.time.compareTo(secondTime.time) <= 0;
    }
  
    
    /**
     * 
     * @param start
     * @param end
     * @return true if both start and end are after the current time
     * @throws IllegalValueException 
     */
    public static boolean taskTimeisAfterCurrentTime(String checkTime) throws IllegalValueException { 
    	
    	Time now = new Time("today");
    	Time time = new Time(checkTime);
    	
    	return time.isMissing() || time.time.compareTo(now.time) >= 0;
	}
  
    @Override
    public String toString() {
    	SimpleDateFormat dateFormatter = new SimpleDateFormat("E, MMM d, yyyy, hh:mm a");
        if(time.getTime().equals(new Date(0))) {
        	return dateFormatter.format(new Date(0));
        }
        else
            return dateFormatter.format(time.getTime());
    }
    

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (this.time != null && ((Time)other).time != null) && (other instanceof Time // instanceOf handles nulls
                && this.time.equals(((Time) other).time)); // state check
    }

    @Override
    public int hashCode() {
        return time.hashCode();
    }
}
