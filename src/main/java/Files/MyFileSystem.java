package Files;

import Exceptions.*;
import Files.*;
import Users.MyUserSystem;
import Utils.*;
import com.fileutils.specs2.models.FileSystem;
import com.fileutils.specs2.models.FileSystemException;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;

import static Utils.LookUpService.*;

public class MyFileSystem implements FileSystem {
    public static int instruction_id;
    public DirectoryEntry currentPosition;
    public static DirectoryEntry root;
    public static MyFileSystem instance;

    static {
        instruction_id = 0;
        root = new DirectoryEntry();
    }

    public MyFileSystem() {
        root.setParentEntry(root);
        this.currentPosition = this.root;
        instance = this;
    }

    @Override
    public String changeDirectory(String s) throws FileSystemException {
        Counter();
        currentPosition = lookUpDir(s, false);
        return currentPosition.getAbsPath();
    }

    @Override
    public void touchFile(String s) throws FileSystemException {
        Counter();
        DirectoryEntry entry = lookUpDir(s, true);
        String fileName = Parser.getLastRegularFileName(s);
        if (fileName == null) {
            throw new FileNotFoundException(s);
        }
        File touchFile = entry.getFileFromList(fileName);
        if (touchFile instanceof SoftLinkFile) {
            Pair<File, String> p = lookUpFile(((SoftLinkFile) touchFile).getTargetPath());
            touchFile = p.getKey();
        }
        if (touchFile == null) {
            entry.makeNewRegularFile(fileName, "");
        } else if (touchFile instanceof RegularFile) {
            touchFile.setModifyTime(instruction_id);
        } else {
            throw new FileNotFoundException(s);
        }
    }

    @Override
    public String catFile(String s) throws FileSystemException {
        Counter();
        File file = lookUpRegularFile(s);
        if (file instanceof SoftLinkFile) {
            file = lookUpRegularFile(((SoftLinkFile) file).getTargetPath());
        }
        return ((RegularFile) file).getContent();
    }

    @Override
    public String list(String s) throws FileSystemException {
        Counter();
        DirectoryEntry entry = lookUpDir(s, false);
        return Parser.generateListInfo(entry.getAllFileNames());
    }

    @Override
    public void fileWrite(String filePath, String content) throws FileSystemException {
        Counter();
        DirectoryEntry entry = lookUpDir(filePath, true);
        String filename = Parser.getLastRegularFileName(filePath);
        if (filename == null) {
            throw new FileNotFoundException(filePath);
        }
        File writeFile = entry.getFileFromList(filename);
        if (writeFile instanceof SoftLinkFile) {
            Pair<File, String> p = lookUpFile(((SoftLinkFile) writeFile).getTargetPath());
            writeFile = p.getKey();
        }
        if (writeFile == null) {
            entry.makeNewRegularFile(filename, content);
        } else if (writeFile instanceof RegularFile) {
            ((RegularFile) writeFile).setContent(content);
        } else {
            throw new FileNotFoundException(filePath);
        }
    }

    @Override
    public void fileAppend(String filePath, String content) throws FileSystemException {
        Counter();
        DirectoryEntry entry = lookUpDir(filePath, true);
        String filename = Parser.getLastRegularFileName(filePath);
        if (filename == null) {
            throw new FileNotFoundException(filePath);
        }
        File appendFile = entry.getFileFromList(filename);
        if (appendFile instanceof SoftLinkFile) {
            Pair<File, String> p = lookUpFile(((SoftLinkFile) appendFile).getTargetPath());
            appendFile = p.getKey();
        }
        if (appendFile == null) {
            RegularFile newFile = entry.makeNewRegularFile(filename, content);
        } else if (appendFile instanceof RegularFile) {
            ((RegularFile) appendFile).appendContent(content);
        } else {
            throw new FileNotFoundException(filePath);
        }
    }

    @Override
    public String makeDirectory(String s) throws FileSystemException {
        Counter();
        DirectoryEntry target = lookUpDir(s, true);
        String fileName = Parser.getLastDirName(s);
        File file = target.getFileFromList(fileName);
        if (file instanceof Directory || fileName.equals("/")) {
            throw new PathExistsException(s);
        }
        if (file != null) {
            throw new PathInvalidException(s);
        }
        DirectoryEntry newDir = new DirectoryEntry(fileName, target);
        target.addNewDirToList(newDir);
        return newDir.getAbsPath();
    }

    @Override
    public String makeDirectoryRecursively(String s) throws FileSystemException {
        Counter();
        ArrayList<String> names = Parser.parsePath(s);
        ArrayList<DirectoryEntry> stack = new ArrayList<>();
        if (names == null) {
            throw new PathInvalidException(s);
        }
        DirectoryEntry entry = Parser.isAbsPath(s) ? root : currentPosition;
        File file = null;
        // check
        for (String name : names) {
            file = entry.getFileFromList(name);
            if (file instanceof SoftLinkFile) {
                Pair<File, String> p = lookUpFile(((SoftLinkFile) file).getTargetPath());
                file = p.getKey();
            }
            if (file != null && !(file instanceof Directory)) {
                // 将栈中新建的节点删掉
                for (DirectoryEntry abandoned : stack) {
                    abandoned.getParentEntry().removeFileFromList(abandoned.getName());
                }
                throw new PathInvalidException(s);        // 不是目录
            }
            if (file != null) {
                entry = ((Directory) file).getEntry();      //已有目录
            } else {
                DirectoryEntry newEntry = new DirectoryEntry(name, entry);
                entry.addNewDirToList(newEntry);
                // 如果父节点是老目录，说明当前是新建目录的根，加入栈中。否则说明父亲已经在栈中，无需加入
                if (entry.getDirectory().getCreateTime() != instruction_id) {
                    stack.add(newEntry);
                }
                entry = newEntry;
            }
        }
        return entry.getAbsPath();
    }

    @Override
    public String removeFile(String s) throws FileSystemException {
        Counter();
        DirectoryEntry entry = lookUpDir(s, true);
        String fileName = Parser.getLastRegularFileName(s);
        if (fileName == null) {
            throw new FileNotFoundException(s);
        }
        File file = entry.getFileFromList(fileName);
        if (file == null || file instanceof Directory) {
            throw new PathInvalidException(s);
        }
        entry.removeFileFromList(fileName);
        return Parser.generateAbsPathForFile(entry.getAbsPath(), fileName);
    }

    @Override
    public String removeRecursively(String s) throws FileSystemException {
        Counter();
        DirectoryEntry entry = lookUpDir(s, true);
        Pair<File, String> pair = lookUpFileWithEntry(entry, s);
        File file = pair.getKey();
        String fileName = pair.getValue();
        if(!(file instanceof Directory)){
            throw new PathInvalidException(s);
        }
        if(Parser.isFather(((Directory) file).getEntry(), currentPosition)){
            throw new PathInvalidException(s);
        }
        entry.removeFileFromList(fileName);
        return Parser.generateAbsPathForFile(entry.getAbsPath(), fileName);
    }

    @Override
    public String information(String s) throws FileSystemException {
        Counter();
        boolean isRoot = s.matches("/+");           // 是否查找的是根目录
        DirectoryEntry entry = isRoot ? root : lookUpDir(s, true);
        File infoFile;
        String fileName = "";
        if (isRoot) {
            infoFile = entry.getDirectory();
        } else {
            if (s.charAt(s.length() - 1) == '/') {                      // 下一级只能是在查目录，否则不合法
                fileName = Parser.getLastDirName(s);
                infoFile = entry.getFileFromList(fileName);
                if (!(infoFile instanceof Directory)) {
                    throw new PathInvalidException(s);
                }
            } else {                                                     // 可能是目录或者普通文件
                fileName = Parser.getLastRegularFileName(s);
                infoFile = entry.getFileFromList(fileName);
                if (infoFile == null) {                                  //不存在此文件
                    throw new PathInvalidException(s);
                }
            }
        }
        String absPath = null;
        if (infoFile instanceof Directory) {
            absPath = ((Directory) infoFile).getEntry().getAbsPath();
        } else {
            absPath = entry.getAbsPath();
            absPath += (absPath.endsWith("/") ? "" : "/") + fileName;
        }

        return String.format("%s %s %d %d %d %d %s",
                infoFile.getOwner().getName(),
                infoFile.getOwnerGroup().getName(),
                infoFile.getCreateTime(),
                infoFile.getModifyTime(),
                infoFile.getSize(),
                infoFile.getCount(),
                absPath);
    }

    @Override
    public String linkSoft(String src, String dst) throws FileSystemException {
        // TODO: 最终指向同一路径判断
        Counter();
        DirectoryEntry srcEntry = lookUpDir(src, true);
        Pair<File, String> ret = lookUpFileWithEntry(srcEntry, src);
        File srcFile = ret.getKey();
        String srcName = ret.getValue();
        DirectoryEntry dstEntry = lookUpDir(dst, true);
        // dst是src的子目录
        if (srcFile instanceof Directory && Parser.isFather(((Directory) srcFile).getEntry(), dstEntry)) {
            throw new PathInvalidException(dst);
        }
        try {
            ret = lookUpFileWithEntry(dstEntry, dst);
        } catch (FileNotFoundException e) {
            ret = null;
        }
        // 不存在目标文件， 则创建一个
        if (ret == null) {
            if (dst.endsWith("/")) {
                throw new PathInvalidException(src);
            }
            String lastName = Parser.getLastRegularFileName(dst);
            //最后是文件，且文件不存在，则要创建新文件，并进行链接
            SoftLinkFile linkFile = new SoftLinkFile(
                    Parser.generateAbsPathForFile(srcEntry.getAbsPath(), srcName), srcFile);
            dstEntry.addNewFileToList(lastName, linkFile);
            return Parser.generateAbsPathForFile(dstEntry.getAbsPath(), lastName);
        } else {
            File dstFile = ret.getKey();
            String dstName = ret.getValue();
            // 最终指向路径一样
            if (srcFile == dstFile) {
                throw new PathInvalidException(dst);
            }
            // 是个文件 且已经存在
            if (!(dstFile instanceof Directory)) {
                throw new PathExistsException(dst);
            }
            // 是个目录 已经有名为srcName的文件
            if (((Directory) dstFile).getEntry().containsFile(srcName)) {
                throw new PathExistsException(dst + "/" + srcName);
            }
            // 没有 则创建
            SoftLinkFile linkFile = new SoftLinkFile(
                    Parser.generateAbsPathForFile(srcEntry.getAbsPath(), srcName), srcFile);
            ((Directory) dstFile).getEntry().addNewFileToList(srcName, linkFile);
            return Parser.generateAbsPathForFile(((Directory) dstFile).getEntry().getAbsPath(), srcName);
        }
    }

    @Override
    public String readLink(String s) throws FileSystemException {
        File softFile = lookUpLinkFile(s);
        if (!(softFile instanceof SoftLinkFile)) {
            throw new PathInvalidException(s);
        }
        return ((SoftLinkFile) softFile).getTargetPath();
    }

    @Override
    public String linkHard(String src, String dst) throws FileSystemException {
        Counter();
        DirectoryEntry srcEntry = lookUpDir(src, true);
        Pair<File, String> ret = lookUpFileWithEntry(srcEntry, src);
        // 不存在源文件, 或源文件路径不符合规范，或目标是一个目录
        if (ret.getKey() instanceof Directory) {
            throw new PathInvalidException(src);
        }
        File srcFile = ret.getKey();
        String srcName = ret.getValue();
        DirectoryEntry dstEntry = lookUpDir(dst, true);
        try {
            ret = lookUpFileWithEntry(dstEntry, dst);
        } catch (FileNotFoundException e) {
            ret = null;
        }
        if (ret == null) {
            if (dst.endsWith("/")) {
                // dst为目录（结尾/），且最后的目录不存在
                // TODO：按照ubuntu，这时候不能创建。目录是否可以创建链接到目录？
                throw new PathInvalidException(src);
            }
            String lastName = Parser.getLastRegularFileName(dst);
            dstEntry.addNewFileToList(lastName, srcFile);
            return Parser.generateAbsPathForFile(dstEntry.getAbsPath(), lastName);
        } else {
            File dstFile = ret.getKey();
            String dstName = ret.getValue();
            // 最终指向路径一样
            if (srcFile == dstFile) {
                throw new PathInvalidException(dst);
            }
            // 是个文件 且已经存在
            if (!(dstFile instanceof Directory)) {
                throw new PathExistsException(dst);
            }
            // 是个目录 已经有名为srcName的文件
            if (((Directory) dstFile).getEntry().containsFile(srcName)) {
                throw new PathExistsException(dst + "/" + srcName);
            }
            ((Directory) dstFile).getEntry().addNewFileToList(srcName, srcFile);
            return Parser.generateAbsPathForFile(((Directory) dstFile).getEntry().getAbsPath(), srcName);
        }
    }

    @Override
    public void move(String src, String dst) throws FileSystemException {
        Counter();
        DirectoryEntry srcEntry = lookUpDir(src, true);
        Pair<File, String> ret = lookUpFileWithEntry(srcEntry, src);
        File srcFile = ret.getKey();
        String srcName = ret.getValue();
        // src是当前工作目录或的上层目录
        if (srcFile instanceof Directory && Parser.isFather(((Directory) srcFile).getEntry(), currentPosition)) {
            throw new PathInvalidException(src);
        }
        DirectoryEntry dstEntry = lookUpDir(dst, true);
        // dst是src的子目录
        if (srcFile instanceof Directory && Parser.isFather(((Directory) srcFile).getEntry(), dstEntry)) {
            throw new PathInvalidException(dst);
        }
        try {
            ret = lookUpFileWithEntry(dstEntry, dst);
        } catch (FileNotFoundException e) {
            ret = null;
        }
        // 不存在目标文件
        if (ret == null) {
            srcEntry.removeFileFromList(srcName);
            String lastName = Parser.getLastDirName(dst);
            if (srcFile instanceof Directory) {         // 改名
                ((Directory) srcFile).getEntry().setName(lastName);
                LinkedList<File> ll = new LinkedList<>();
                ll.addLast(srcFile);
                while (!ll.isEmpty()) {
                    File tmp = ll.removeFirst();
                    if (tmp instanceof Directory) {
                        ll.addAll(((Directory) tmp).getEntry().getAllFilesExceptSpecial());
                    }
                    tmp.setModifyTime(instruction_id);
                }
            }
            dstEntry.addNewFileToList(lastName, srcFile);
            srcFile.setModifyTime(instruction_id);
            return;
        }
        File dstFile = ret.getKey();
        String dstName = ret.getValue();
        // 最终指向路径一样
        if (srcFile == dstFile) {
            throw new PathInvalidException(dst);
        }
        // 源文件是文件，目标是目录
        if (!(srcFile instanceof Directory) && dstFile instanceof Directory) {
            File origin = ((Directory) dstFile).getEntry().getFileFromList(srcName);
            // 不存在重名
            if (origin == null) {
                srcEntry.removeFileFromList(srcName);
                ((Directory) dstFile).getEntry().addNewFileToList(srcName, srcFile);
                srcFile.setModifyTime(instruction_id);
            } else if (origin instanceof Directory) {
                throw new PathExistsException(dst + "/" + srcName);
            } else {
                srcEntry.removeFileFromList(srcName);
                ((Directory) dstFile).getEntry().addNewFileToList(srcName, srcFile);
                srcFile.setModifyTime(instruction_id);
            }
        }
        // 源文件是文件，目标是文件
        else if (!(srcFile instanceof Directory) && !(dstFile instanceof Directory)) {
            srcEntry.removeFileFromList(srcName);
            dstEntry.addNewFileToList(dstName, srcFile);
            srcFile.setModifyTime(instruction_id);
        }
        // 源文件是目录，目标是目录
        else if (srcFile instanceof Directory && dstFile instanceof Directory) {
            File origin = ((Directory) dstFile).getEntry().getFileFromList(srcName);
            if (origin == null) {
                srcEntry.removeFileFromList(srcName);
                ((Directory) dstFile).getEntry().addNewFileToList(srcName, srcFile);
                // srcFile.setModifyTime(instruction_id);
                LinkedList<File> ll = new LinkedList<>();
                ll.addLast(srcFile);
                while (!ll.isEmpty()) {
                    File tmp = ll.removeFirst();
                    if (tmp instanceof Directory) {
                        ll.addAll(((Directory) tmp).getEntry().getAllFilesExceptSpecial());
                    }
                    tmp.setModifyTime(instruction_id);
                }
            } else if (origin instanceof Directory && ((Directory) origin).getEntry().isEmptyDir()) {
                srcEntry.removeFileFromList(srcName);
                ((Directory) dstFile).getEntry().addNewFileToList(srcName, srcFile);
                srcFile.setModifyTime(instruction_id);
                srcFile.setCreateTime(origin.getCreateTime());
                srcFile.setOwner(origin.getOwner());
                srcFile.setOwnerGroup(origin.getOwnerGroup());
                LinkedList<File> ll = new LinkedList<>(((Directory) srcFile).getEntry().getAllFilesExceptSpecial());
                while (!ll.isEmpty()) {
                    File tmp = ll.removeFirst();
                    if (tmp instanceof Directory) {
                        ll.addAll(((Directory) tmp).getEntry().getAllFilesExceptSpecial());
                    }
                    tmp.setModifyTime(instruction_id);
                }
            } else {
                throw new PathExistsException(dst + "/" + srcName);
            }
        } else if (srcFile instanceof Directory && !(dstFile instanceof Directory)) {
            throw new PathExistsException(dst);
        }
    }

    @Override
    public void copy(String src, String dst) throws FileSystemException {
        Counter();
        DirectoryEntry srcEntry = lookUpDir(src, true);
        Pair<File, String> ret = lookUpFileWithEntry(srcEntry, src);
        File srcFile = ret.getKey();
        String srcName = ret.getValue();
        DirectoryEntry dstEntry = lookUpDir(dst, true);
        // dst是src的子目录
        if (srcFile instanceof Directory && Parser.isFather(((Directory) srcFile).getEntry(), dstEntry)) {
            throw new PathInvalidException(dst);
        }
        try {
            ret = lookUpFileWithEntry(dstEntry, dst);
        } catch (FileNotFoundException e) {
            ret = null;
        }
        // 目标文件或目录不存在
        if (ret == null) {
            File newFile = srcFile.cloneNewFile(dstEntry);
            String lastName = Parser.getLastDirName(dst);
            if (newFile instanceof Directory) {     // 如果是目录文件 要给entry改名
                ((Directory) newFile).getEntry().setName(lastName);
            }
            dstEntry.addNewFileToList(lastName, newFile);
            return;
        }
        File dstFile = ret.getKey();
        String dstName = ret.getValue();
        // 最终指向路径一样
        if (srcFile == dstFile) {
            throw new PathInvalidException(dst);
        }
        // 源文件是文件，目标是目录
        if (!(srcFile instanceof Directory) && dstFile instanceof Directory) {
            File origin = ((Directory) dstFile).getEntry().getFileFromList(srcName);
            if (origin == null) {
                File newFile = srcFile.cloneNewFile(((Directory) dstFile).getEntry());
                ((Directory) dstFile).getEntry().addNewFileToList(srcName, newFile);
            } else if (origin instanceof Directory/* && ((Directory) origin).getEntry().isEmptyDir()*/) {
                throw new PathExistsException(dst + "/" + srcName);
            } else {
                File newFile = srcFile.cloneNewFile(((Directory) dstFile).getEntry());
                ((Directory) dstFile).getEntry().addNewFileToList(srcName, newFile);
                newFile.setCreateTime(origin.getCreateTime());
                newFile.setOwner(origin.getOwner());
                newFile.setOwnerGroup(origin.getOwnerGroup());
            }
        } else if (!(srcFile instanceof Directory) && !(dstFile instanceof Directory)) {
            File newFile = srcFile.cloneNewFile(dstEntry);
            dstEntry.addNewFileToList(dstName, newFile);
            newFile.setCreateTime(dstFile.getCreateTime());
            newFile.setOwner(dstFile.getOwner());
            newFile.setOwnerGroup(dstFile.getOwnerGroup());
        } else if (srcFile instanceof Directory && dstFile instanceof Directory) {
            File origin = ((Directory) dstFile).getEntry().getFileFromList(srcName);
            if (origin == null) {
                File newFile = srcFile.cloneNewFile(((Directory) dstFile).getEntry());
                ((Directory) dstFile).getEntry().addNewFileToList(srcName, newFile);
            } else if (origin instanceof Directory && ((Directory) origin).getEntry().isEmptyDir()) {
                File newFile = srcFile.cloneNewFile(((Directory) dstFile).getEntry());
                ((Directory) dstFile).getEntry().addNewFileToList(srcName, newFile);
                newFile.setCreateTime(origin.getCreateTime());
                newFile.setOwner(origin.getOwner());
                newFile.setOwnerGroup(origin.getOwnerGroup());
            } else {
                throw new PathExistsException(dst + "/" + srcName);
            }
        } else if (srcFile instanceof Directory && !(dstFile instanceof Directory)) {
            throw new PathExistsException(dst);
        }
    }

    public static void Counter() {
        instruction_id++;
    }
}