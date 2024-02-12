package Exceptions;

import com.fileutils.specs2.models.FileSystemException;

public class PathInvalidException extends FileSystemException {

    public PathInvalidException(String path) {
        super("Path " + path + " is invalid");
    }
}
