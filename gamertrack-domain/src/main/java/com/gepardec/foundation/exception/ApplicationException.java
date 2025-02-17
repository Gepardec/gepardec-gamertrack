package com.gepardec.foundation.exception;

public class ApplicationException extends RuntimeException {
    ErrorMessage errorMessage;

    public ApplicationException(ErrorMessage errorMessage) {
        super(errorMessage.message());
        this.errorMessage = errorMessage;
    }

    public static class GameDoesNotExistException extends ApplicationException {
        public GameDoesNotExistException(ErrorMessage errorMessage) {
            super(errorMessage);
        }
    }

    public static class GameAlreadyExistsException extends ApplicationException {
        public GameAlreadyExistsException(ErrorMessage errorMessage) {
            super(errorMessage);
        }
    }

    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }
}
