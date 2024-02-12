package Files;

public class Directory extends File {

    private final DirectoryEntry entry;

    public Directory(DirectoryEntry entry) {
        super(MyFileSystem.instruction_id, MyFileSystem.instruction_id, 0);
        this.entry = entry;
    }

    public Directory(int size, DirectoryEntry entry) {
        super(MyFileSystem.instruction_id, MyFileSystem.instruction_id, size);
        this.entry = entry;
    }

    public DirectoryEntry getEntry() {
        return entry;
    }

    public File cloneNewFile(DirectoryEntry newParent){
        DirectoryEntry newEntry = new DirectoryEntry(entry, newParent);
        return newEntry.getDirectory();
    }

    @Override
    public int getCount() {
        return entry.getAllFileNames().size() - 2;
    }
}
