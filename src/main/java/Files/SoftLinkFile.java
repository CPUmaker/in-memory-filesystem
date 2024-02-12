package Files;


public class SoftLinkFile extends File {
    private final String targetPath;
    private final File srcFile;

    public SoftLinkFile(String targetPath, File srcFile) {
        super(MyFileSystem.instruction_id, MyFileSystem.instruction_id, 0);
        if (srcFile instanceof SoftLinkFile) {
            this.targetPath = ((SoftLinkFile) srcFile).getTargetPath();
        }else{
            this.targetPath = targetPath;
        }
        this.srcFile = srcFile;

    }

    public String getTargetPath() {
        return targetPath;
    }

    @Override
    public File cloneNewFile(DirectoryEntry newParent) {
        return new SoftLinkFile(targetPath, srcFile);
    }

    @Override
    public int getCount() {
        return 1;
    }
}
