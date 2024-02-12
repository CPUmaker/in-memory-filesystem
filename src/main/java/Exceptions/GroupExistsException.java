package Exceptions;

import com.fileutils.specs2.models.UserSystemException;

public class GroupExistsException extends UserSystemException {
    public GroupExistsException(String groupName) {
        super("Group " + groupName + " exists");
    }
}
