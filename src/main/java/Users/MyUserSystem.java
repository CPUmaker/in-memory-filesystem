package Users;

import Exceptions.*;
import Files.DirectoryEntry;
import Files.MyFileSystem;
import Utils.LookUpService;
import Utils.Parser;
import com.fileutils.specs2.models.UserSystem;
import com.fileutils.specs2.models.UserSystemException;

import java.util.*;

public class MyUserSystem implements UserSystem {
    private static MyUserSystem userSystemInstance = null;
    private final static User rootUser;

    private final HashMap<String, Group> allGroups;
    private final HashMap<String, User> allUsers;
    private String historyWorkDirectoryAbsPath;
    private User currentUser;

    static {
        Group rootGroup = new Group("root");
        User root = new User("root", rootGroup);
        rootGroup.getUsersMap().put("root", root);
        rootUser = root;
    }

    public MyUserSystem() {
        userSystemInstance = this;

        allGroups = new HashMap<>();
        allUsers = new HashMap<>();
        currentUser = rootUser;
        historyWorkDirectoryAbsPath = "/";

        allGroups.put("root", rootUser.getMainGroup());
        allUsers.put("root", rootUser);
    }

    public static User getCurrentUser() {
        return userSystemInstance == null ? rootUser : userSystemInstance.currentUser;
    }

    @Override
    public void addUser(String userName) throws UserSystemException {
        MyFileSystem.Counter();
        rootAuthentication();
        parameterCheck(userName);
        Parser.checkUserFormat(userName);
        if (allUsers.containsKey(userName)) {
            throw new UserExistsException(userName);
        }
        Group newGroup = allGroups.get(userName);
        if (newGroup == null) {
            newGroup = new Group(userName);
            allGroups.put(userName, newGroup);
        }
        User newUser = new User(userName, newGroup);
        allUsers.put(userName, newUser);
        newGroup.getUsersMap().put(userName, newUser);
    }

    @Override
    public void deleteUser(String userName) throws UserSystemException {
        MyFileSystem.Counter();
        rootAuthentication();
        parameterCheck(userName);
        User userDeleting = allUsers.remove(userName);
        if (userDeleting == null) {
            throw new UserInvalidException(userName);
        }
        userDeleting.deleteFromGroups();
        if (userDeleting.getMainGroup().getUsersMap().isEmpty()) {
            allGroups.remove(userDeleting.getMainGroup().getName());
        }
    }

    @Override
    public void addGroup(String groupName) throws UserSystemException {
        MyFileSystem.Counter();
        rootAuthentication();
        parameterCheck(groupName);
        Parser.checkGroupFormat(groupName);
        if (allGroups.containsKey(groupName)) {
            throw new GroupExistsException(groupName);
        }
        Group newGroup = new Group(groupName);
        allGroups.put(groupName, newGroup);
    }

    @Override
    public void deleteGroup(String groupName) throws UserSystemException {
        MyFileSystem.Counter();
        rootAuthentication();
        parameterCheck(groupName);
        if (allUsers.containsKey(groupName)) {  // 如果存在同名用户，那么一定是他的主组
            throw new GroupInvalidException(groupName);
        }
        Group groupDeleting = allGroups.remove(groupName);
        if (groupDeleting == null) {
            throw new GroupInvalidException(groupName);
        }
        groupDeleting.deleteFromUsers();
    }

    @Override
    public void addUserToGroup(String groupName, String userName) throws UserSystemException {
        MyFileSystem.Counter();
        rootAuthentication();
        parameterCheck(groupName);
        parameterCheck(userName);
        Group group = allGroups.get(groupName);
        if (group == null) {
            throw new GroupInvalidException(groupName);
        }
        User user = allUsers.get(userName);
        if (user == null || user.getGroupsMap().containsKey(groupName)) {
            throw new UserInvalidException(userName);
        }
        user.getGroupsMap().put(groupName, group);
        group.getUsersMap().put(userName, user);
    }

    @Override
    public String changeUser(String userName) throws UserSystemException {
        MyFileSystem.Counter();
        rootAuthentication();
        parameterCheck(userName);
        User userChanging = allUsers.get(userName);
        if (userChanging == null) {
            throw new UserInvalidException(userName);
        }
        currentUser = userChanging;
        historyWorkDirectoryAbsPath = MyFileSystem.instance.currentPosition.getAbsPath();
        return null;
    }

    @Override
    public String exitUser() throws UserSystemException {
        MyFileSystem.Counter();
        if (currentUser == rootUser) {
            throw new OperationPermissionDenyException();
        }
        currentUser = rootUser;
        try {
            DirectoryEntry entry = LookUpService.lookUpDir(historyWorkDirectoryAbsPath, false);
            if (!entry.getAbsPath().equals(historyWorkDirectoryAbsPath)) {
                MyFileSystem.instance.currentPosition = MyFileSystem.root;
            } else {
                MyFileSystem.instance.currentPosition = entry;
            }
        } catch (PathInvalidException e) {
            MyFileSystem.instance.currentPosition = MyFileSystem.root;
        }
        return null;
    }

    @Override
    public String queryUser() throws UserSystemException {
        MyFileSystem.Counter();
        return currentUser.getName();
    }

    private void rootAuthentication() throws OperationPermissionDenyException {
        if (currentUser != rootUser) {
            throw new OperationPermissionDenyException();
        }
    }

    private void parameterCheck(String param) throws OperationPermissionDenyException {
        if (param.equals("root")) {
            throw new OperationPermissionDenyException();
        }
    }

    public String getAllGroups() {
        String[] array = allGroups.keySet().toArray(new String[0]);
        Arrays.sort(array);
        return String.join(" ", array);
    }

    public String getAllUsers() {
        String[] array = allUsers.keySet().toArray(new String[0]);
        Arrays.sort(array);
        return String.join(" ", array);
    }

}
