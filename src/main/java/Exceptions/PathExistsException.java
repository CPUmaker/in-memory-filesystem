package Exceptions;

import com.fileutils.specs2.models.FileSystemException;

public class PathExistsException extends FileSystemException {

    public PathExistsException(String path) {
        super("Path " + path + " exists");
    }
}
