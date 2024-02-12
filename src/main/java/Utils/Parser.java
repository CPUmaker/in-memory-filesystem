package Utils;

import Exceptions.GroupInvalidException;
import Exceptions.UserInvalidException;
import Files.DirectoryEntry;

import java.util.*;
import java.util.regex.Pattern;

public class Parser {
    static Pattern namePattern = Pattern.compile("[a-zA-Z._][a-z0-9A-Z._]{0,256}");
    static Pattern contentPattern = Pattern.compile("[a-zA-Z0-9_ ,.!?'()@/\\\\]");
    static Pattern userNamePattern = Pattern.compile("[a-zA-Z._]+");
    static Pattern groupNamePattern = Pattern.compile("[a-zA-Z._]+");

    public static boolean checkNameFormat(String name) {
        return name.length() <= 256 && namePattern.matcher(name).matches();
    }

    public static void checkUserFormat(String name) throws UserInvalidException {
        if (name.isEmpty() || name.length() > 128 || !userNamePattern.matcher(name).matches()) {
            throw new UserInvalidException(name);
        }
    }

    public static void checkGroupFormat(String name) throws GroupInvalidException {
        if (name.isEmpty() || name.length() > 128 || !groupNamePattern.matcher(name).matches()) {
            throw new GroupInvalidException(name);
        }
    }

    public static boolean isAbsPath(String path) {
        return path.startsWith("/");
    }

    public static ArrayList<String> parsePath(String path) {
        if (path.length() > 4096 || path.trim().length() == 0) {
            return null;
        }
        ArrayList<String> nameList = new ArrayList<>(Arrays.asList(path.split("/+")));
        if (isAbsPath(path) && nameList.size() > 0) {
            nameList.remove(0);
        }
        for (String name : nameList) {
            if (!checkNameFormat(name)) {
                return null;
            }
        }
        return nameList;
    }

    public static String generateListInfo(Set<String> files) {
        StringBuilder ans = new StringBuilder();
        for (String name : files) {
            if (!(name.equals(".") || name.equals(".."))) {
                ans.append(name).append(" ");
            }
        }
        return ans.toString().trim();
    }

    public static String getLastDirName(String s) {
        ArrayList<String> nameList = new ArrayList<>(Arrays.asList(s.split("/+")));
        if (nameList.size() > 0) {
            return nameList.get(nameList.size() - 1);
        }
        return "/";
    }

    public static String getLastRegularFileName(String s) {
        String name = s.substring(s.lastIndexOf('/') + 1);
        if (name.equals("")) {
            return null;
        }
        return name;
    }

    public static String generateAbsPathForFile(String absPath, String fileName) {
        return absPath + (absPath.endsWith("/") ? "" : "/") + fileName;
    }

    public static boolean isFather(DirectoryEntry father, DirectoryEntry son) {
        if (father == father.getParentEntry()) {
            return true;
        }
        while (son != son.getParentEntry()) {
            if(son == father){
                return true;
            }
            son = son.getParentEntry();
        }
        return false;
    }
}
