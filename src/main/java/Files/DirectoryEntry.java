package Files;

import Utils.*;

import java.util.*;

public class DirectoryEntry {
    private String name;
    private final Directory directory;
    private final TreeMap<String, File> fileList;
    private DirectoryEntry parentEntry;

    public DirectoryEntry(String name, DirectoryEntry parentEntry) {
        this.name = name;
        directory = new Directory(this);
        this.parentEntry = parentEntry;
        fileList = new TreeMap<>();
        fileList.put(".", this.getDirectory());
        fileList.put("..", parentEntry.getDirectory());
    }

    public DirectoryEntry(DirectoryEntry originEntry, DirectoryEntry newParent) {
        this.name = originEntry.name;
        directory = new Directory(originEntry.getDirectory().getSize(), this);
        this.parentEntry = newParent;
        fileList = new TreeMap<>();
        fileList.put(".", this.getDirectory());
        fileList.put("..", parentEntry.getDirectory());
        for (String originName : originEntry.fileList.keySet()) {
            if (originName.equals(".") || originName.equals("..")) {
                continue;
            }
            File originFile = originEntry.getFileFromList(originName);
            this.fileList.put(originName, originFile.cloneNewFile(this));
        }
    }

    public DirectoryEntry() {
        this.name = "/";
        directory = new Directory(this);
        this.parentEntry = this;
        fileList = new TreeMap<>();
        fileList.put(".", this.getDirectory());
        fileList.put("..", parentEntry.getDirectory());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Directory getDirectory() {
        return directory;
    }

    public File getFileFromList(String fileName) {
        return fileList.get(fileName);
    }

    public Set<String> getAllFileNames() {
        return fileList.keySet();
    }

    public Collection<File> getAllFilesExceptSpecial() {
        ArrayList<File> files = new ArrayList<>();
        for (String name : fileList.keySet()) {
            if (name.equals(".") || name.equals("..")) {
                continue;
            }
            files.add(fileList.get(name));
        }
        return files;
    }

    public boolean containsFile(String filename) {
        return fileList.containsKey(filename);
    }

    public DirectoryEntry getParentEntry() {
        return parentEntry;
    }

    public void setParentEntry(DirectoryEntry parentEntry) {
        this.parentEntry = parentEntry;
    }

    public String getAbsPath() {
        return getAbsPathStr().toString();
    }


    public StringBuilder getAbsPathStr() {
        if (this == this.parentEntry) {
            return new StringBuilder("/");
        }
        StringBuilder sb = new StringBuilder("/");
        Stack<DirectoryEntry> entrys = new Stack<>();
        DirectoryEntry entry = this.getParentEntry();
        while (entry != entry.getParentEntry()) {
            entrys.push(entry);
            entry = entry.getParentEntry();
        }
        while (!entrys.isEmpty()) {
            sb.append(entrys.pop().getName());
            if (sb.charAt(sb.length() - 1) != '/') {
                sb.append("/");
            }
        }
        sb.append(this.name);
        return sb;
    }

    /*
     * 创建新文件
     * 1. 如果命名不规范，则返回null
     * 2. 正常创建，则返回该文件
     */
    public RegularFile makeNewRegularFile(String name, String content) {
        RegularFile newFile = new RegularFile(content);
        addNewFileToList(name, newFile);
        return newFile;
    }

    public void addSize(int newSize) {
        if (newSize == 0) {
            return;
        }
        DirectoryEntry entry = this;
        entry.getDirectory().setSize(entry.getDirectory().getSize() + newSize);
        while (entry != entry.parentEntry) {
            entry = entry.getParentEntry();
            entry.getDirectory().setSize(entry.getDirectory().getSize() + newSize);
        }
    }

    public void addNewDirToList(DirectoryEntry entry) {
        fileList.put(entry.getName(), entry.getDirectory());
        directory.setModifyTime(MyFileSystem.instruction_id);
    }

    public void removeFileFromList(String name) {
        File file = fileList.get(name);
        if (file != null) {
            fileList.remove(name);
            addSize(-file.getSize());
        }
        directory.setModifyTime(MyFileSystem.instruction_id);
        if (file instanceof RegularFile) {
            ((RegularFile) file).removeFromDirEntries(this);
        }
    }

    public void addNewFileToList(String name, File file) {
        File origin = fileList.get(name);
        fileList.put(name, file);
        directory.setModifyTime(MyFileSystem.instruction_id);
        int originSize = origin == null ? 0 : origin.getSize();
        addSize(file.getSize() - originSize);
        if (file instanceof Directory) {
            ((Directory) file).getEntry().setParentEntry(this);
        } else if (file instanceof RegularFile) {
            ((RegularFile) file).addToDirEntries(this);
        }
    }

    public boolean isEmptyDir() {
        return fileList.size() <= 2;
    }
}
