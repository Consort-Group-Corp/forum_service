package uz.consortgroup.forum_service.exception;

public class ForumAlreadyExistsException extends RuntimeException {
    public ForumAlreadyExistsException(String message) {
        super(message);
    }
}
