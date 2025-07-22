package org.example.pfeback.exceptions;

public class ManagerAlreadyExistsException extends RuntimeException {
    public ManagerAlreadyExistsException(String message) {
        super(message);
    }
}
