package Exceptions;

import com.fileutils.specs2.models.UserSystemException;

public class UserExistsException extends UserSystemException {
    public UserExistsException(String userName) {
        super("User " + userName + " exists");
    }
}
