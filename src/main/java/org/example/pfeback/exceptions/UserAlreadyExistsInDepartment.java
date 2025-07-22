package org.example.pfeback.exceptions;

public class UserAlreadyExistsInDepartment extends RuntimeException {
    public UserAlreadyExistsInDepartment(String message) {
      super(message);
    }
}
