package Files;

import java.util.LinkedList;

public class RegularFile extends File {
    private StringBuilder content;
    private final LinkedList<DirectoryEntry> linkingDirEntries;

    public RegularFile(String content) {
        super(MyFileSystem.instruction_id, MyFileSystem.instruction_id, 0);
        this.content = replaceAll(new StringBuilder(content), "@n", "\n");
        this.linkingDirEntries = new LinkedList<>();
        setSize(this.content.length());
    }

    public String getContent() {
        return content.toString();
    }

    public void setContent(String content) {
        int oldSize = getSize();
        // this.content = content.replaceAll("@n","\n");
        this.content = replaceAll(new StringBuilder(content), "@n","\n");
        setSize(this.content.length());
        int newSize = getSize();
        setModifyTime(MyFileSystem.instruction_id);
        for (DirectoryEntry entry : linkingDirEntries) {
            entry.addSize(newSize - oldSize);
        }
    }

    public void appendContent(String content) {
        int oldSize = getSize();
        StringBuilder newContent = replaceAll(new StringBuilder(content),"@n","\n");
        if (this.content.length() > 0 && newContent.length() > 0 &&
                this.content.charAt(this.content.length() - 1) == '@' &&
                newContent.charAt(0) == 'n') {
            this.content.deleteCharAt(this.content.length() - 1)
                    .append("\n")
                    .append(newContent.substring(1));
        } else {
            this.content.append(newContent);
        }
        setSize(this.content.length());
        int newSize = getSize();
        setModifyTime(MyFileSystem.instruction_id);
        for (DirectoryEntry entry : linkingDirEntries) {
            entry.addSize(newSize - oldSize);
        }
    }

    public void addToDirEntries(DirectoryEntry entry) {
        linkingDirEntries.addLast(entry);
    }

    public void removeFromDirEntries(DirectoryEntry entry) {
        linkingDirEntries.remove(entry);
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public File cloneNewFile(DirectoryEntry newParent) {
        return new RegularFile(content.toString());
    }

    public static StringBuilder replaceAll(StringBuilder stb, String oldStr, String newStr) {
        if (stb == null || oldStr == null || newStr == null || stb.length() == 0 || oldStr.length() == 0)
            return stb;
        int index = stb.indexOf(oldStr);
        if (index > -1 && !oldStr.equals(newStr)) {
            while (index > -1) {
                stb.replace(index, index+oldStr.length(), newStr);
                index = stb.indexOf(oldStr);
            }
        }
        return stb;
    }
}
