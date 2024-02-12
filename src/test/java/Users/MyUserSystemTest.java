package Users;

import Files.MyFileSystem;
import com.fileutils.specs2.models.FileSystemException;
import com.fileutils.specs2.models.UserSystemException;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class MyUserSystemTest {
    private MyUserSystem myUserSystem;
    private MyFileSystem myFileSystem;

    @Before
    public void setUp() throws Exception {
        myUserSystem = new MyUserSystem();
        MyFileSystem.instruction_id = 0;
        myFileSystem = new MyFileSystem();
    }

    @Test
    public void addUser() {
        try {
            assertEquals(myUserSystem.getAllUsers(), "root");
            assertEquals(myUserSystem.getAllGroups(), "root");
            ArrayList<String> userList = new ArrayList<>(Arrays.asList("Tom", ".Merry", "_.John", "lcy"));
            for (String name : userList) {
                myUserSystem.addUser(name);
            }
            userList.add("root");
            userList.sort(Comparator.naturalOrder());
            assertEquals(myUserSystem.getAllUsers(), String.join(" ", userList));
            assertEquals(myUserSystem.getAllGroups(), String.join(" ", userList));
        } catch (UserSystemException e) {
            fail();
        }

        try {
            myUserSystem.addGroup("ABC");
            myUserSystem.addUser("ABC");
        } catch (UserSystemException e) {
            fail();
        }

        try {
            myUserSystem.changeUser("Tom");
            myUserSystem.addUser("JACK");
            fail();
        } catch (UserSystemException e) {
            assertEquals("Operation is not permitted", e.getMessage());
            assertFalse(myUserSystem.getAllUsers().contains("JACK"));
            try {
                myUserSystem.exitUser();
            } catch (UserSystemException e1) {}
        }

        try {
            myUserSystem.addUser("root");
            fail();
        } catch (UserSystemException e) {
            assertEquals("Operation is not permitted", e.getMessage());
        }

        try {
            myUserSystem.addUser("lcy");
            fail();
        } catch (UserSystemException e) {
            assertEquals("User lcy exists", e.getMessage());
        }

        try {
            myUserSystem.addUser("aa cc");
            fail();
        } catch (UserSystemException e) {
            assertEquals("User aa cc is invalid", e.getMessage());
        }

        try {
            myUserSystem.addUser("");
            fail();
        } catch (UserSystemException e) {
            assertEquals("User  is invalid", e.getMessage());
        }

        String bigName = String.join("", Collections.nCopies(129, "z"));
        try {
            myUserSystem.addUser(bigName);
            fail();
        } catch (UserSystemException e) {
            assertEquals("User " + bigName + " is invalid", e.getMessage());
        }
    }

    @Test
    public void deleteUser() {
        try {
            ArrayList<String> userList = new ArrayList<>(Arrays.asList("Tom", ".Merry", "_.John", "lcy"));
            for (String name : userList) {
                myUserSystem.addUser(name);
            }
            for (String name : userList) {
                myUserSystem.deleteUser(name);
            }
            assertEquals(myUserSystem.getAllUsers(), "root");
            assertEquals(myUserSystem.getAllGroups(), "root");
        } catch (UserSystemException e) {
            fail();
        }

        try {
            myUserSystem.addUser("Tom");
            myUserSystem.addUser("lcy");
            myUserSystem.addUserToGroup("Tom", "lcy");
            myUserSystem.deleteUser("Tom");
            myUserSystem.deleteUser("lcy");
            assertEquals(myUserSystem.getAllUsers(), "root");
            assertEquals(myUserSystem.getAllGroups(), "Tom root");
        } catch (UserSystemException e) {
            fail();
        }

        try {
            myUserSystem.addUser("Tom");
            myUserSystem.addUser("Jane");
            myUserSystem.addUser("Dante");
            myUserSystem.addUserToGroup("Tom", "Jane");
            myUserSystem.addUserToGroup("Tom", "Dante");
            myUserSystem.addUserToGroup("Jane", "Dante");
            myUserSystem.deleteUser("Dante");
            myUserSystem.deleteUser("Jane");
            myUserSystem.deleteUser("Tom");
            assertEquals(myUserSystem.getAllUsers(), "root");
            assertEquals(myUserSystem.getAllGroups(), "root");
        } catch (UserSystemException e) {
            fail();
        }

        try {
            myUserSystem.addUser("Jane");
            myUserSystem.changeUser("Jane");
            myUserSystem.deleteUser("Jane");
            fail();
        } catch (UserSystemException e) {
            assertEquals("Operation is not permitted", e.getMessage());
            assertTrue(myUserSystem.getAllUsers().contains("Jane"));
            try {
                myUserSystem.exitUser();
            } catch (UserSystemException e1) {}
        }

        try {
            myUserSystem.deleteUser("root");
            fail();
        } catch (UserSystemException e) {
            assertEquals("Operation is not permitted", e.getMessage());
        }

        try {
            myUserSystem.deleteUser("KKK");
            fail();
        } catch (UserSystemException e) {
            assertEquals("User KKK is invalid", e.getMessage());
        }
    }

    @Test
    public void addGroup() {
        try {
            assertEquals(myUserSystem.getAllUsers(), "root");
            assertEquals(myUserSystem.getAllGroups(), "root");
            ArrayList<String> userList = new ArrayList<>(Arrays.asList("Tom", ".Merry", "_.John", "lcy"));
            for (String name : userList) {
                myUserSystem.addUser(name);
            }
            userList.add("root");
            userList.sort(Comparator.naturalOrder());
            assertEquals(myUserSystem.getAllUsers(), String.join(" ", userList));
            assertEquals(myUserSystem.getAllGroups(), String.join(" ", userList));
        } catch (UserSystemException e) {
            fail();
        }

        try {
            myUserSystem.addUser("TEST");
            myUserSystem.changeUser("TEST");
            myUserSystem.addGroup("TEST_TEST");
            fail();
        } catch (UserSystemException e) {
            assertEquals("Operation is not permitted", e.getMessage());
            assertFalse(myUserSystem.getAllGroups().contains("TEST_TEST"));
            try {
                myUserSystem.exitUser();
            } catch (UserSystemException e1) {}
        }

        try {
            myUserSystem.addGroup("root");
            fail();
        } catch (UserSystemException e) {
            assertEquals("Operation is not permitted", e.getMessage());
        }

        try {
            myUserSystem.addGroup("KKK");
            myUserSystem.addGroup("KKK");
            fail();
        } catch (UserSystemException e) {
            assertEquals("Group KKK exists", e.getMessage());
        }

        try {
            myUserSystem.addGroup("aa cc");
            fail();
        } catch (UserSystemException e) {
            assertEquals("Group aa cc is invalid", e.getMessage());
        }

        try {
            myUserSystem.addGroup("");
            fail();
        } catch (UserSystemException e) {
            assertEquals("Group  is invalid", e.getMessage());
        }

        String bigName = String.join("", Collections.nCopies(129, "z"));
        try {
            myUserSystem.addGroup(bigName);
            fail();
        } catch (UserSystemException e) {
            assertEquals("Group " + bigName + " is invalid", e.getMessage());
        }
    }

    @Test
    public void deleteGroup() {
        try {
            ArrayList<String> groupList = new ArrayList<>(Arrays.asList("Tom", ".Merry", "_.John", "lcy"));
            for (String name : groupList) {
                myUserSystem.addGroup(name);
            }
            for (String name : groupList) {
                myUserSystem.deleteGroup(name);
            }
            assertEquals(myUserSystem.getAllUsers(), "root");
            assertEquals(myUserSystem.getAllGroups(), "root");
        } catch (UserSystemException e) {
            fail();
        }

        try {
            myUserSystem.addUser("Tom");
            myUserSystem.addGroup("lcy");
            myUserSystem.addUserToGroup("lcy", "Tom");
            myUserSystem.deleteGroup("lcy");
            myUserSystem.deleteUser("Tom");
            // TODO: 增加判断Tom中自己所在的Group没有lcy
            assertEquals(myUserSystem.getAllUsers(), "root");
            assertEquals(myUserSystem.getAllGroups(), "root");
        } catch (UserSystemException e) {
            fail();
        }

        try {
            myUserSystem.addUser("Tom");
            myUserSystem.addGroup("lcy");
            myUserSystem.addUserToGroup("lcy", "Tom");
            myUserSystem.deleteGroup("Tom");
            fail();
        } catch (UserSystemException e) {
            assertEquals("Group Tom is invalid", e.getMessage());
        }

        try {
            myUserSystem.addGroup("TEST_DD");
            myUserSystem.addUser("TEST");
            myUserSystem.changeUser("TEST");
            myUserSystem.deleteGroup("TEST_DD");
            fail();
        } catch (UserSystemException e) {
            assertEquals("Operation is not permitted", e.getMessage());
            assertTrue(myUserSystem.getAllGroups().contains("TEST_DD"));
            try {
                myUserSystem.exitUser();
            } catch (UserSystemException e1) {}
        }

        try {
            myUserSystem.deleteGroup("root");
            fail();
        } catch (UserSystemException e) {
            assertEquals("Operation is not permitted", e.getMessage());
        }

        try {
            myUserSystem.deleteGroup("KKK");
            fail();
        } catch (UserSystemException e) {
            assertEquals("Group KKK is invalid", e.getMessage());
        }
    }

    @Test
    public void addUserToGroup() {
        try {
            myUserSystem.addGroup("TEST_DD");
            myUserSystem.addUser("TEST");
            myUserSystem.changeUser("TEST");
            myUserSystem.addUserToGroup("TEST_DD", "TEST");
            fail();
        } catch (UserSystemException e) {
            assertEquals("Operation is not permitted", e.getMessage());
            try {
                myUserSystem.exitUser();
            } catch (UserSystemException e1) {}
        }

        try {
            myUserSystem.addUserToGroup("TEST_DD", "root");
            fail();
        } catch (UserSystemException e) {
            assertEquals("Operation is not permitted", e.getMessage());
        }

        try {
            myUserSystem.addUserToGroup("root", "TEST");
            fail();
        } catch (UserSystemException e) {
            assertEquals("Operation is not permitted", e.getMessage());
        }

        try {
            myUserSystem.addUserToGroup("TEST_DD", "www");
            fail();
        } catch (UserSystemException e) {
            assertEquals("User www is invalid", e.getMessage());
        }

        try {
            myUserSystem.addUserToGroup("www", "TEST");
            fail();
        } catch (UserSystemException e) {
            assertEquals("Group www is invalid", e.getMessage());
        }

        try {
            myUserSystem.addUserToGroup("TEST_DD", "TEST");
        } catch (UserSystemException e) {
            fail();
        }

        try {
            myUserSystem.addUserToGroup("TEST_DD", "TEST");
            fail();
        } catch (UserSystemException e) {
            assertEquals("User TEST is invalid", e.getMessage());
        }
    }

    @Test
    public void changeUser() {
        try {
            myFileSystem.makeDirectoryRecursively("/vvv/ddd/sss");
            myFileSystem.changeDirectory("/vvv/ddd/sss");
            myUserSystem.addUser("pop");
            myUserSystem.changeUser("pop");
            assertEquals("/vvv/ddd/sss", myFileSystem.currentPosition.getAbsPath());
            myUserSystem.exitUser();
        } catch (UserSystemException | FileSystemException e) {
            fail();
        }

        try {
            myUserSystem.changeUser("SkSk");
            fail();
        } catch (UserSystemException e) {
            assertEquals("User SkSk is invalid", e.getMessage());
        }
    }

    @Test
    public void exitUser() {
        try {
            myUserSystem.exitUser();
            fail();
        } catch (UserSystemException e) {
            assertEquals("Operation is not permitted", e.getMessage());
        }

        try {
            myFileSystem.makeDirectoryRecursively("/vvv/ddd/sss");
            myFileSystem.changeDirectory("/vvv/ddd/sss");
            myUserSystem.addUser("pop");
            myUserSystem.changeUser("pop");
            myFileSystem.changeDirectory("/");
            myUserSystem.exitUser();
            assertEquals("/vvv/ddd/sss", myFileSystem.currentPosition.getAbsPath());
        } catch (UserSystemException | FileSystemException e) {
            fail();
        }

        try {
            myFileSystem.makeDirectoryRecursively("/vvv/ddd/sss");
            myFileSystem.changeDirectory("/vvv/ddd/sss");
            myUserSystem.addUser("MaKaBaKa");
            myUserSystem.changeUser("MaKaBaKa");
            myFileSystem.changeDirectory("/");
            myFileSystem.removeRecursively("/vvv");
            myUserSystem.exitUser();
            assertEquals("/", myFileSystem.currentPosition.getAbsPath());
        } catch (UserSystemException | FileSystemException e) {
            fail();
        }

        try {
            myFileSystem.makeDirectoryRecursively("/vvv/ddd/sss");
            myFileSystem.changeDirectory("/vvv/ddd/sss");
            myUserSystem.changeUser("MaKaBaKa");
            myFileSystem.changeDirectory("/");
            myFileSystem.removeRecursively("/vvv/ddd/sss");
            myFileSystem.makeDirectoryRecursively("/vvv/kkk/ppp");
            myFileSystem.linkSoft("/vvv/kkk/ppp", "/vvv/ddd/sss");
            myUserSystem.exitUser();
            assertEquals("/", myFileSystem.currentPosition.getAbsPath());
        } catch (UserSystemException | FileSystemException e) {
            fail();
        }
    }

    @Test
    public void queryUser() {
        try {
            assertEquals("root", myUserSystem.queryUser());
            myUserSystem.addUser("Li_Hua");
            myUserSystem.addUser("Zhang_Hua");
            myUserSystem.changeUser("Li_Hua");
            assertEquals("Li_Hua", myUserSystem.queryUser());
            myUserSystem.exitUser();
            assertEquals("root", myUserSystem.queryUser());
            myUserSystem.changeUser("Zhang_Hua");
            assertEquals("Zhang_Hua", myUserSystem.queryUser());
            myUserSystem.exitUser();
            assertEquals("root", myUserSystem.queryUser());
        } catch (UserSystemException e) {
            fail();
        }
    }
}