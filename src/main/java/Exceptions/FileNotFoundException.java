package Exceptions;

import com.fileutils.specs2.models.FileSystemException;

public class FileNotFoundException extends FileSystemException {
    public FileNotFoundException(String path) {
        super("Path " + path + " is invalid");
    }
}
