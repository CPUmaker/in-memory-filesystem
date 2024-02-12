package Users;

import java.util.HashMap;
import java.util.Map;

public class User {
    private final String name;
    private final Group mainGroup;
    private final HashMap<String, Group> groupsMap;

    public User(String name, Group mainGroup) {
        this.name = name;
        this.mainGroup = mainGroup;
        this.groupsMap = new HashMap<>();
        this.groupsMap.put(mainGroup.getName(), mainGroup);
    }

    public String getName() {
        return name;
    }

    public Group getMainGroup() {
        return mainGroup;
    }

    public HashMap<String, Group> getGroupsMap() {
        return groupsMap;
    }

    public void deleteFromGroups() {
        for (Group group : groupsMap.values()) {
            group.getUsersMap().remove(name);
        }
    }
}
