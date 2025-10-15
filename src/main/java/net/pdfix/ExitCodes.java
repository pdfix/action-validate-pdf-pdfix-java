/**
 * Defines all system exit codes for the application.
 */
public class ExitCodes {
    /** Biggest code for successful execution. Exit code 0-100 represents number of duplicate MCIDs. */
    public static final int MAX_SUCCESS = 100;

    /** Uncaught General error. Out of 0-100 range. */
    public static final int GENERAL_ERROR = 101;

    /** General error message for console. */
    public static final String GENERAL_MESSAGE = "Unforseen error happened. More info in logs.";
}