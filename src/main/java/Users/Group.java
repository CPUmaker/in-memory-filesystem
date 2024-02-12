package Users;

import java.util.HashMap;

public class Group {
    private final String name;
    private final HashMap<String, User> usersMap;

    public Group(String name) {
        this.name = name;
        usersMap = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public HashMap<String, User> getUsersMap() {
        return usersMap;
    }

    public void deleteFromUsers() {
        for (User user : usersMap.values()) {
            user.getGroupsMap().remove(name);
        }
    }
}
