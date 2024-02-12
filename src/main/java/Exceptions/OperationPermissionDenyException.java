package Exceptions;

import com.fileutils.specs2.models.UserSystemException;

public class OperationPermissionDenyException extends UserSystemException {
    public OperationPermissionDenyException() {
        super("Operation is not permitted");
    }
}
