 package uz.consortgroup.forum_service.exception;

public class ForumValidationException extends RuntimeException {
    public ForumValidationException(String message) {
        super(message);
    }
    
    public ForumValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}