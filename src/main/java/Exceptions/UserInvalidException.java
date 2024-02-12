package Exceptions;

import com.fileutils.specs2.models.UserSystemException;

public class UserInvalidException extends UserSystemException {
    public UserInvalidException(String userName) {
        super("User " + userName + " is invalid");
    }
}
