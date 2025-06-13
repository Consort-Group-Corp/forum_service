package uz.consortgroup.forum_service.exception;

public class ForumTopicNotFoundException extends RuntimeException {
    public ForumTopicNotFoundException(String message) {
        super(message);
    }
}
