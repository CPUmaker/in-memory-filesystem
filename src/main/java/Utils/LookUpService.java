package Utils;

import Exceptions.FileNotFoundException;
import Files.*;
import Exceptions.PathInvalidException;
import com.fileutils.specs2.models.FileSystemException;
import javafx.util.Pair;

import java.util.ArrayList;

public class LookUpService {
    public static DirectoryEntry lookUpDir(String path, boolean ignoreEnd) throws PathInvalidException {
    /* 根据路径查找目录节点:
     * 1. 要求路径中全是目录或软链接且全都存在，否则抛出异常
     * 2. 特别的，对于'/'，无论ignoreEnd取何值都将返回root
     */
        ArrayList<String> names = Parser.parsePath(path);
        if (names == null) {
            throw new PathInvalidException(path);
        }
        if (ignoreEnd && names.size() > 0) {
            names.remove(names.size() - 1);
        }
        DirectoryEntry entry = Parser.isAbsPath(path) ? MyFileSystem.instance.root : MyFileSystem.instance.currentPosition;
        for (String name : names) {
            File file = entry.getFileFromList(name);
            if (file == null) { // 不存在文件
                throw new PathInvalidException(path);
            }
            if (file instanceof SoftLinkFile) { // 是软链接
                entry = lookUpDir(((SoftLinkFile) file).getTargetPath(), false);
                continue;
            }
            if (!(file instanceof Directory)) { // 是目录以外的文件
                throw new PathInvalidException(path);
            }
            entry = ((Directory) file).getEntry();
        }
        return entry;
    }

    /* 根据路径查找普通文件:
     * 1. 要求路径中全是目录或软链接且全都存在，最后必须是一个普通文件或软链接，否则返回null
     * 2. 对于以'/'结尾都将返回null
     */
    public static File lookUpRegularFile(String s) throws PathInvalidException {
        DirectoryEntry entry = lookUpDir(s, true);
        String fileName = Parser.getLastRegularFileName(s);
        if (fileName == null) {
            throw new PathInvalidException(s);
        }
        File file = entry.getFileFromList(fileName);
        if (file == null || file instanceof Directory) {
            throw new PathInvalidException(s);
        }
        return file;
    }

    /* 根据路径查找文件和其名字:
     * 1. 如果找到的结果不是目录且路径以'/'结尾，抛出Path异常
     * 2. 特别的，对于'/'，将直接返回节点目录和'/'
     */
    public static Pair<File, String> lookUpFile(String s) throws FileSystemException {
        DirectoryEntry entry = lookUpDir(s, true);
        return lookUpFileWithEntry(entry, s);
    }

    /* 根据路径和目录节点查找文件和其名字:
     * 1. 要求目录节点有效，否则返回null
     * 2. 如果找到的结果不是目录且路径以'/'结尾，抛出Path异常
     * 3. 特别的，对于'/'，将直接返回节点目录和'/'
     */
    public static Pair<File, String> lookUpFileWithEntry(DirectoryEntry entry, String s) throws FileSystemException {
        if (entry == null) {
            throw new PathInvalidException(s);
        }
        boolean isRoot = s.matches("/+");
        if (isRoot) {
            return new Pair<>(entry.getDirectory(), "/");
        }
        String fileName = Parser.getLastDirName(s);
        File file = entry.getFileFromList(fileName);
        if (file == null) {
            throw new FileNotFoundException(s);
        }
        if (!(file instanceof Directory) && s.endsWith("/")) {
            throw new PathInvalidException(s);
        }
        /*
        if (file instanceof SoftLinkFile) {
            Pair<File, String> nextFile = lookUpFile(((SoftLinkFile) file).getTargetPath());
            return nextFile;
        }
         */
        return new Pair<>(file, fileName);
    }

    /* 根据路径查找软链接:
     * 1. 要求路径中全是目录或软链接且全都存在，最后必须是软链接，否则返回null
     * 2. 对于路径'/'，直接返回节点目录
     * 3. 以'/'结尾将抛出Path异常
     */
    public static File lookUpLinkFile(String s) throws PathInvalidException {
        DirectoryEntry entry = lookUpDir(s, true);
        boolean isRoot = s.matches("/+");
        if (isRoot) {
            return entry.getDirectory();
        }
        String fileName = Parser.getLastRegularFileName(s);
        if (fileName == null) {
            throw new PathInvalidException(s);
        }
        File file = entry.getFileFromList(fileName);
        if (!(file instanceof SoftLinkFile)) {
            throw new PathInvalidException(s);
        }
        return file;
    }
}
