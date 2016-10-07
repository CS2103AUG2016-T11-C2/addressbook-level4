package seedu.address.model.task;
//package seedu.address.model.task;
//
//import seedu.address.commons.exceptions.IllegalValueException;
//
///**
// * Represents a Task's date number in the address book.
// * Guarantees: immutable; is valid as declared in {@link #isValidDate(String)}
// */
//public class Date {
//
//    public static final String MESSAGE_PHONE_CONSTRAINTS = "Task date numbers should only contain numbers";
//    public static final String PHONE_VALIDATION_REGEX = "\\d+";
//
//    public final String value;
//
//    /**
//     * Validates given date number.
//     *
//     * @throws IllegalValueException if given date string is invalid.
//     */
//    public Date(String date) throws IllegalValueException {
//        assert date != null;
//        date = date.trim();
//        if (!isValidDate(date)) {
//            throw new IllegalValueException(MESSAGE_PHONE_CONSTRAINTS);
//        }
//        this.value = date;
//    }
//
//    /**
//     * Returns true if a given string is a valid task date number.
//     */
//    public static boolean isValidDate(String test) {
//        return test.matches(PHONE_VALIDATION_REGEX);
//    }
//
//    @Override
//    public String toString() {
//        return value;
//    }
//
//    @Override
//    public boolean equals(Object other) {
//        return other == this // short circuit if same object
//                || (other instanceof Date // instanceof handles nulls
//                && this.value.equals(((Date) other).value)); // state check
//    }
//
//    @Override
//    public int hashCode() {
//        return value.hashCode();
//    }
//
//}
