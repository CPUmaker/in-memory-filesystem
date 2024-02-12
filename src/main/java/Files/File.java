package Files;

import Users.Group;
import Users.MyUserSystem;
import Users.User;

public abstract class File {
    private int createTime;
    private int modifyTime;
    private int size;
    private int count;
    private User owner;
    private Group ownerGroup;

    public File(int createTime, int modifyTime, int size) {
        this.createTime = createTime;
        this.modifyTime = modifyTime;
        this.size = size;
        this.owner = MyUserSystem.getCurrentUser();
        this.ownerGroup = this.owner.getMainGroup();
    }

    public void setCreateTime(int createTime) {
        this.createTime = createTime;
    }

    public Integer getCreateTime() {
        return createTime;
    }

    public Integer getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Integer modifyTime) {
        this.modifyTime = modifyTime;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public abstract int getCount();

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Group getOwnerGroup() {
        return ownerGroup;
    }

    public void setOwnerGroup(Group ownerGroup) {
        this.ownerGroup = ownerGroup;
    }

    public abstract File cloneNewFile(DirectoryEntry newParent);
}