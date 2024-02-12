package Exceptions;

import com.fileutils.specs2.models.UserSystemException;

public class GroupInvalidException extends UserSystemException {
    public GroupInvalidException(String groupName) {
        super("Group " + groupName + " is invalid");
    }
}
