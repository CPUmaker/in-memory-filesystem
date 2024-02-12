import Exceptions.PathExistsException;
import Exceptions.PathInvalidException;
import Files.DirectoryEntry;
import Files.MyFileSystem;
import Users.MyUserSystem;
import Utils.LookUpService;
import Utils.Parser;
import com.fileutils.specs2.models.FileSystemException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.After;

import java.util.Collections;

import static org.junit.Assert.*;


public class MyFileSystemTest {
    protected MyFileSystem myFileSystem;
    protected MyUserSystem myUserSystem;

    @Before
    public void before() throws Exception {
        MyFileSystem.instruction_id = 0;
        MyFileSystem.root = new DirectoryEntry();
        myFileSystem = new MyFileSystem();
        myUserSystem = new MyUserSystem();
        myUserSystem.addUser("lcy");
        MyFileSystem.instruction_id = 0;
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: changeDirectory(String s)
     */
    @Test
    public void testChangeDirectory() throws Exception {
//TODO: Test goes here...
        String cdString;
        try {
            cdString = myFileSystem.changeDirectory("/");
            Assert.assertEquals("/", cdString);

            cdString = myFileSystem.changeDirectory("./");
            Assert.assertEquals("/", cdString);

            cdString = myFileSystem.changeDirectory("/./../.");
            Assert.assertEquals("/", cdString);

            cdString = myFileSystem.changeDirectory("./.././..");
            Assert.assertEquals("/", cdString);
        } catch (FileSystemException e) {
            fail();
        }

        try {
            myFileSystem.makeDirectory("/abc");
            myFileSystem.makeDirectory("/abc/def");
            myFileSystem.makeDirectory("/abc/def/ghi");
            cdString = myFileSystem.changeDirectory("/abc");
            Assert.assertEquals("/abc", cdString);
            cdString = myFileSystem.changeDirectory("./def");
            Assert.assertEquals("/abc/def", cdString);
            cdString = myFileSystem.changeDirectory("ghi");
            Assert.assertEquals("/abc/def/ghi", cdString);

            cdString = myFileSystem.changeDirectory("/");
            Assert.assertEquals("/", cdString);
            cdString = myFileSystem.changeDirectory("/abc/def/ghi");
            Assert.assertEquals("/abc/def/ghi", cdString);

            cdString = myFileSystem.changeDirectory("/");
            Assert.assertEquals("/", cdString);
            cdString = myFileSystem.changeDirectory("/abc/../abc/./def/ghi/.");
            Assert.assertEquals("/abc/def/ghi", cdString);
        } catch (FileSystemException e) {
            e.printStackTrace();
            fail();
        }

        try {
            myFileSystem.changeDirectory("/abc/ghi");
        } catch (FileSystemException e) {
            Assert.assertTrue(true);
        }

        try {
            myFileSystem.changeDirectory("/1abc");
        } catch (FileSystemException e) {
            Assert.assertTrue(true);
        }

        try {
            myFileSystem.changeDirectory("/abc*-@#!/def");
        } catch (FileSystemException e) {
            Assert.assertTrue(true);
        }

        try {
            myFileSystem.touchFile("./aaa");
            myFileSystem.changeDirectory("/aaa/def");
        } catch (FileSystemException e) {
            Assert.assertTrue(true);
        }
    }

    /**
     * Method: touchFile(String s)
     */
    @Test
    public void testTouchFile() throws Exception {
//TODO: Test goes here...
        try {
            myFileSystem.touchFile("a.txt");
        } catch (FileSystemException e) {
            fail();
        }

        try {
            myFileSystem.touchFile("bb.txt");
            assertEquals("root root 2 2 0 1 /bb.txt", myFileSystem.information("bb.txt"));
            myFileSystem.touchFile("bb.txt");
            assertEquals("root root 2 4 0 1 /bb.txt", myFileSystem.information("bb.txt"));
        } catch (FileSystemException e) {
            fail();
        }

        try {
            myFileSystem.touchFile("/");
            fail();
        } catch (FileSystemException e) {
            assertTrue(true);
        }

        try {
            myFileSystem.touchFile("./a/b/c/d/a.txt");
            fail();
        } catch (FileSystemException e) {
            assertTrue(true);
        }

        try {
            myFileSystem.touchFile("./newfile/");
            fail();
        } catch (FileSystemException e) {
            assertTrue(true);
        }
    }

    /**
     * Method: catFile(String s)
     */
    @Test
    public void testCatFile() throws Exception {
//TODO: Test goes here...

        try {
            myFileSystem.touchFile("/a.txt");
            myFileSystem.fileWrite("/a.txt", "I'm LCY.");
            Assert.assertEquals("I'm LCY.", myFileSystem.catFile("a.txt"));
            myFileSystem.catFile("/");
            fail();
        } catch (FileSystemException e) {
            assertTrue(true);
        }

        try {
            myFileSystem.makeDirectory("/test");
            myFileSystem.catFile("/test/a");
            fail();
        } catch (FileSystemException e) {
            assertTrue(true);
        }

        try {
            myFileSystem.makeDirectory("/test/a");
            myFileSystem.catFile("/test/a");
            fail();
        } catch (FileSystemException e) {
            assertTrue(true);
        }

        try {
            myFileSystem.touchFile("/in.txt");
            myFileSystem.catFile("in.txt/");
            fail();
        } catch (FileSystemException e) {
            assertTrue(true);
        }
    }

    /**
     * Method: list(String s)
     */
    @Test
    public void testList() throws Exception {
//TODO: Test goes here...

        try {
            Assert.assertEquals("", myFileSystem.list("/"));

            myFileSystem.makeDirectory("dir2");
            Assert.assertEquals("dir2", myFileSystem.list("/"));

            myFileSystem.makeDirectory("dir1");
            Assert.assertEquals("dir1 dir2", myFileSystem.list("/"));
        } catch (FileSystemException e) {
            fail();
        }

        try {
            myFileSystem.list("xyz");
            fail();
        } catch (FileSystemException e) {
            assertTrue(true);
        }

    }

    /**
     * Method: fileWrite(String filePath, String content)
     */
    @Test
    public void testFileWrite() throws Exception {
//TODO: Test goes here...
        try {
            myFileSystem.touchFile("a.txt");
            myFileSystem.fileWrite("a.txt", "I'm LCY.");
            assertEquals("I'm LCY.", myFileSystem.catFile("a.txt"));
        } catch (FileSystemException e) {
            fail();
        }

        try {
            myFileSystem.makeDirectoryRecursively("x/y/z");
            myFileSystem.fileWrite("x/y/z/a.txt", "I'm LCY.");
            assertEquals("I'm LCY.", myFileSystem.catFile("x/y/z/a.txt"));
        } catch (FileSystemException e) {
            fail();
        }

        try {
            myFileSystem.fileWrite("/dd/a.txt", "I'm LCY.");
            fail();
        } catch (FileSystemException e) {
            assertTrue(true);
        }

        try {
            myFileSystem.makeDirectory("dir");
            myFileSystem.fileWrite("dir", "I'm LCY.");
            fail();
        } catch (FileSystemException e) {
            assertTrue(true);
        }

        try {
            myFileSystem.fileWrite("/", "123");
            fail();
        } catch (FileSystemException e) {
            assertTrue(true);
        }

        try {
            myFileSystem.fileWrite("/txt/", "123");
            fail();
        } catch (FileSystemException e) {
            assertTrue(true);
        }
    }

    /**
     * Method: fileAppend(String filePath, String content)
     */
    @Test
    public void testFileAppend() throws Exception {
//TODO: Test goes here...
        try {
            myFileSystem.touchFile("./aac.txt");
            myFileSystem.fileAppend("aac.txt", "And you?");
            Assert.assertEquals("And you?", myFileSystem.catFile("aac.txt"));
        } catch (FileSystemException e) {
            fail();
        }

        try {
            myFileSystem.touchFile("./aac.txt");
            myFileSystem.fileAppend("aac.txt", "@nAnd you?");
            Assert.assertEquals("And you?\nAnd you?", myFileSystem.catFile("aac.txt"));
        } catch (FileSystemException e) {
            fail();
        }

        try {
            myFileSystem.fileAppend("bbb/aac.txt", "@nAnd you?");
            fail();
        } catch (FileSystemException e) {
            assertTrue(true);
        }

        try {
            myFileSystem.fileAppend("/next.txt", "@nAnd you?");
            assertTrue(myFileSystem.information("next.txt").indexOf("next.txt") != -1);
        } catch (FileSystemException e) {
            fail();
        }

        try {
            myFileSystem.fileAppend("/x/", "@nAnd you?");
            fail();
        } catch (FileSystemException e) {
            assertTrue(true);
        }

        try {
            myFileSystem.fileAppend("/", "@nAnd you?");
            fail();
        } catch (FileSystemException e) {
            assertTrue(true);
        }
        try {
            myFileSystem.makeDirectory("testDir");
            myFileSystem.fileAppend("testDir", "hhhh");
            fail();
        } catch (FileSystemException e) {
            assertTrue(true);
        }
    }

    /**
     * Method: makeDirectory(String s)
     */
    @Test
    public void testMakeDirectory() throws Exception {
//TODO: Test goes here...
        String mkString;
        try {
            mkString = myFileSystem.makeDirectory("./a");
            assertEquals("/a", mkString);

            mkString = myFileSystem.makeDirectory("/b");
            assertEquals("/b", mkString);

            mkString = myFileSystem.makeDirectory("./b/../a/../b/./c");
            assertEquals("/b/c", mkString);
        } catch (FileSystemException e) {
            fail();
        }

        try {
            myFileSystem.makeDirectory("/");
            fail();
        } catch (FileSystemException e) {
            assertTrue(true);
        }

        try {
            myFileSystem.makeDirectory("/abc/def/vdf");
            fail();
        } catch (FileSystemException e) {
            assertTrue(true);
        }

        try {
            myFileSystem.makeDirectory("/a%a");
            fail();
        } catch (FileSystemException e) {
            assertTrue(true);
        }

        try {
            myFileSystem.makeDirectory(String.join("", Collections.nCopies(257, "z")));
            fail();
        } catch (FileSystemException e) {
            assertTrue(true);
        }

        try {
            myFileSystem.makeDirectory("testDir");
            myFileSystem.makeDirectory("testDir");
            fail();
        } catch (FileSystemException e) {
            assertTrue(true);
        }
    }

    /**
     * Method: makeDirectoryRecursively(String s)
     */
    @Test
    public void testMakeDirectoryRecursively() throws Exception {
//TODO: Test goes here...
        String mkString;
        try {
            mkString = myFileSystem.makeDirectoryRecursively("/");
            assertEquals("/", mkString);

            mkString = myFileSystem.makeDirectoryRecursively("./a/b/c/d");
            assertEquals("/a/b/c/d", mkString);
            assertisExists("/a/b/c/d");

            mkString = myFileSystem.makeDirectoryRecursively("/b/../c/../d");
            assertEquals("/d", mkString);
            assertisExists("/b");
            assertisExists("/c");
            assertisExists("/d");

            mkString = myFileSystem.makeDirectoryRecursively("/a/b/e/f");
            assertEquals("/a/b/e/f", mkString);
            assertisExists("/a/b/e/f");
        } catch (FileSystemException e) {
            fail();
        }

        try {
            mkString = myFileSystem.makeDirectoryRecursively("/xx/yy");
            assertEquals("/xx/yy", mkString);
            myFileSystem.touchFile("/xx/yy/file.txt");
            mkString = myFileSystem.makeDirectoryRecursively("/xx/yy/zzz/sss/../../nmn/mnm/mmm/../../dd/../../file.txt/ccc/ddd");
            fail();
        } catch (FileSystemException e) {
            assertTrue(true);
        }

        try {
            myFileSystem.makeDirectoryRecursively("./ff/dd/123/d/f");
            fail();
        } catch (FileSystemException e) {
            // 查看文件存在性
            assertNotExists("/xx/yy/zzz/sss");
            assertNotExists("/xx/yy/zzz");
            assertNotExists("/xx/yy/nmn/mnm");
            assertNotExists("/xx/yy/nmn/dd");
            assertNotExists("/xx/yy/nmn");
            assertTrue(true);
        }


        try {
            myFileSystem.makeDirectory("test");
            myFileSystem.touchFile("test/test");
            myFileSystem.makeDirectoryRecursively("./test/test/test/test/test");
            fail();
        } catch (FileSystemException e) {
            assertTrue(true);
        }
        try {
            String s = "";
            for (int i = 0; i < 5000; i++) {
                s = s + "a/";
            }
            myFileSystem.makeDirectoryRecursively(s);
            fail();
        } catch (FileSystemException e) {
            assertTrue(true);
        }
    }

    /**
     * Method: removeFile(String s)
     */
    @Test
    public void testRemoveFile() throws Exception {
//TODO: Test goes here...
        try {
            myFileSystem.touchFile("a.txt");
            Assert.assertEquals("/a.txt", myFileSystem.removeFile("a.txt"));
        } catch (FileSystemException e) {
            e.printStackTrace();
        }

        try {
            myFileSystem.removeFile("/dir/xxx");
            fail();
        } catch (FileSystemException e) {
            assertTrue(true);
        }

        try {
            myFileSystem.makeDirectory("/dir");
            myFileSystem.removeFile("/dir");
            fail();
        } catch (FileSystemException e) {
            assertTrue(true);
        }

        try {
            myFileSystem.touchFile("/dir/txt");
            myFileSystem.removeFile("/dir/txt/abc");
            fail();
        } catch (FileSystemException e) {
            assertTrue(true);
        }

        try {
            myFileSystem.makeDirectoryRecursively("/testDir");
            myFileSystem.removeFile("/testDir/");
            fail();
        } catch (FileSystemException e) {
            assertTrue(true);
        }
    }

    /**
     * Method: removeRecursively(String s)
     */
    @Test
    public void testRemoveRecursively() throws Exception {
//TODO: Test goes here...
        try {
            myFileSystem.makeDirectoryRecursively("t1/t2/t3");
            myFileSystem.touchFile("t1/t2/t3/a.txt");
            Assert.assertEquals("/t1", myFileSystem.removeRecursively("t1"));
        } catch (FileSystemException e) {
            fail();
        }

        try {
            myFileSystem.touchFile("a.txt");
            myFileSystem.removeRecursively("a.txt");
            fail();
        } catch (FileSystemException e) {
            assertTrue(true);
        }

        try {
            myFileSystem.removeRecursively("/123gg/xxx");
            fail();
        } catch (FileSystemException e) {
            assertTrue(true);
        }

        try {
            myFileSystem.removeRecursively("/");
            fail();
        } catch (FileSystemException e) {
            assertTrue(true);
        }

        try {
            myFileSystem.makeDirectoryRecursively("/a/b/c/d");
            myFileSystem.changeDirectory("/a/b/c/d");
            myFileSystem.removeRecursively("/a");
            fail();
        } catch (FileSystemException e) {
            assertTrue(true);
        }
    }

    /**
     * Method: information(String s)
     */
    @Test
    public void testInformation() throws Exception {
//TODO: Test goes here...
        String infoString;
        try {
            myFileSystem.makeDirectoryRecursively("/lcy");
            infoString = myFileSystem.information("/lcy");
            assertEquals("root root 1 1 0 0 /lcy", infoString);

            infoString = myFileSystem.information("/lcy/");
            assertEquals("root root 1 1 0 0 /lcy", infoString);

            myFileSystem.fileWrite("/lcy/aaa", "as");
            infoString = myFileSystem.information("/lcy");
            assertEquals("root root 1 4 2 1 /lcy", infoString);

            assertEquals("root root 0 1 2 1 /", myFileSystem.information("."));
            assertEquals("root root 0 1 2 1 /", myFileSystem.information(".."));
            assertEquals("root root 1 4 2 1 /lcy", myFileSystem.information("../lcy/."));

        } catch (FileSystemException e) {
            fail();
        }

        try {
            myFileSystem.information("/home");
            fail("Expected an FileSystemException to be thrown");
        } catch (FileSystemException e) {
            assertTrue(true);
        }

        try {
            myFileSystem.information("/1gg/text");
            fail("Expected an FileSystemException to be thrown");
        } catch (FileSystemException e) {
            assertTrue(true);
        }
        try {
            myFileSystem.information("/");
        } catch (FileSystemException e) {
            fail();
        }
        try {
            myFileSystem.makeDirectory("/file");
            myFileSystem.touchFile("file");
            fail();
        } catch (FileSystemException e) {
            assertTrue(true);
        }

        try {
            myFileSystem.touchFile("/myFile.txt");
            myFileSystem.information("/myFile.txt/");
            fail();
        } catch (FileSystemException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testParser() {
        assertEquals("/", Parser.getLastDirName("/"));
    }

    @Test
    public void testLookUpService() {
        try {
            LookUpService.lookUpFileWithEntry(null, "/");
            fail();
        } catch (FileSystemException e) {
            assertTrue(e instanceof PathInvalidException);
        }
    }

    /**
     * Method: lookUpDir(String path, boolean ignoreEnd)
     */
    @Test
    public void testLookUpDir() throws Exception {
//TODO: Test goes here...
    }


    /**
     * Method: lookUpRegularFile(String s)
     */
    @Test
    public void testlookUpRegularFile() throws Exception {

    }

    private void assertNotExists(String path) {
        try {
            myFileSystem.information(path);
            fail();
        } catch (FileSystemException e1) {
            assertTrue(true);
        }
    }

    private void assertisExists(String path) {
        try {
            myFileSystem.information(path);
        } catch (FileSystemException e1) {
            fail();
        }
    }


    /**
     * Method: linkSoft(String src, String dst)
     */
    @Test
    public void testLinkSoft() throws Exception {
//TODO: Test goes here...
        try {
            myFileSystem.makeDirectory("a");
            myFileSystem.linkSoft("a", "a");
            fail();
        } catch (FileSystemException e) {
            assertTrue(e instanceof PathInvalidException);
        }
        try {
            myFileSystem.touchFile("/a/test.txt");
            myFileSystem.linkSoft("test.txt", "a");
            fail();
        } catch (FileSystemException e) {
            assertEquals("Path test.txt is invalid", e.getMessage());
        }
        try {
            myFileSystem.linkSoft("/a/test.txt", "/a");
            fail();
        } catch (FileSystemException e) {
            assertTrue(e instanceof PathExistsException);
        }
        try {
            myFileSystem.linkSoft("/a/test.txt", "/a/");
            fail();
        } catch (FileSystemException e) {
            assertTrue(e instanceof PathExistsException);
        }
        try {
            myFileSystem.linkSoft("/a/test.txt", "/b/");
            fail();
        } catch (FileSystemException e) {
            assertTrue(e instanceof PathInvalidException);
        }
        try {
            myFileSystem.makeDirectory("/b");
            myFileSystem.linkSoft("/a/test.txt", "/b/");
            myFileSystem.fileWrite("/a/test.txt", "123");
            assertTrue(myFileSystem.catFile("/b/test.txt").equals("123"));
            myFileSystem.fileAppend("/b/test.txt", "456");
            assertTrue(myFileSystem.catFile("/a/test.txt").equals("123456"));
            myFileSystem.linkSoft("/a/test.txt", "/b/test2.txt");
            assertTrue(myFileSystem.list("/b").indexOf("test2.txt") != -1);
            myFileSystem.linkSoft("/b/test.txt", "/b/test3.txt");
            assertTrue(myFileSystem.list("/b").indexOf("test3.txt") != -1);
            myFileSystem.fileWrite("/b/test3.txt", "new");
            assertTrue(myFileSystem.catFile("/a/test.txt").equals("new"));
            assertTrue(myFileSystem.catFile("/b/test.txt").equals("new"));
            assertTrue(myFileSystem.catFile("/b/test2.txt").equals("new"));
        } catch (FileSystemException e) {
            fail();
        }

        try {
            myFileSystem.makeDirectoryRecursively("/a/x/x");
            myFileSystem.linkSoft("/a", "/a/x/x");
            fail();
        } catch (FileSystemException e) {
            assertTrue(e instanceof PathInvalidException);
        }

        try {
            myFileSystem.makeDirectoryRecursively("/z");
            myFileSystem.touchFile("/z/file");
            myFileSystem.linkSoft("/a", "/z/file");
            fail();
        } catch (FileSystemException e) {
            assertTrue(e instanceof PathExistsException);
        }

        try {
            myFileSystem.makeDirectory("/z/a");
            myFileSystem.linkSoft("/a", "/z/");
            fail();
        } catch (FileSystemException e) {
            assertTrue(e instanceof PathExistsException);
        }

        try {
            myFileSystem.linkSoft("/a", "/c");
            assertTrue(myFileSystem.list(".").contains("c"));
            assertEquals("new", myFileSystem.catFile("/c/test.txt"));
            assertTrue(myFileSystem.list("/c").contains("test.txt"));
        } catch (FileSystemException e) {
            fail();
            assertTrue(e instanceof PathInvalidException);
        }

        try {
            assertTrue(myFileSystem.removeFile("/b/test.txt").contains("/b/test.txt"));
            myFileSystem.fileWrite("/b/test3.txt", "again");
            assertEquals("again", myFileSystem.catFile("/a/test.txt"));
        } catch (FileSystemException e) {
            fail();
        }
        try {
            assertTrue(myFileSystem.removeFile("/a/test.txt").contains("/a/test.txt"));
            myFileSystem.fileWrite("/b/test3.txt", "final");
            fail();
        } catch (FileSystemException e) {
            assertEquals("Path /a/test.txt is invalid", e.getMessage());
        }
        try {
            myFileSystem.touchFile("/a/test.txt");
            myFileSystem.fileWrite("/b/test3.txt", "final");
            assertTrue(myFileSystem.catFile("/a/test.txt").equals("final"));
            assertTrue(myFileSystem.catFile("/b/test2.txt").equals("final"));
        } catch (FileSystemException e) {
            fail();
        }

        try {
            myFileSystem.makeDirectoryRecursively("/a/b/c/d");
            myFileSystem.linkSoft("/a/b/c", "link");
            myFileSystem.removeRecursively("/a/b/c");
            myFileSystem.catFile("/link/d");
        } catch (FileSystemException e) {
            assertTrue(e.getMessage().indexOf("/a/b/c") != -1);
        }
        try {
            myFileSystem.changeDirectory("/link/d");
        } catch (FileSystemException e) {
            assertTrue(e.getMessage().indexOf("/a/b/c") != -1);
        }

        try {
            myFileSystem.touchFile("/link/d/test");
        } catch (FileSystemException e) {
            assertTrue(e.getMessage().indexOf("/a/b/c") != -1);
        }
    }

    /**
     * Method: readLink(String s)
     */
    @Test
    public void testReadLink() throws Exception {
//TODO: Test goes here...
        try {
            myFileSystem.makeDirectory("/a");
            myFileSystem.touchFile("/a/test.txt");
            myFileSystem.makeDirectory("/b");
            myFileSystem.linkSoft("/a/test.txt", "/b/");
            myFileSystem.fileWrite("/a/test.txt", "123");
            myFileSystem.fileAppend("/b/test.txt", "456");
            myFileSystem.linkSoft("/a/test.txt", "/b/test2.txt");
            myFileSystem.linkSoft("/b/test.txt", "/b/test3.txt");
            assertEquals("/a/test.txt", myFileSystem.readLink("/b/test.txt"));
            assertEquals("/a/test.txt", myFileSystem.readLink("/b/test2.txt"));
            assertEquals("/a/test.txt", myFileSystem.readLink("/b/test3.txt"));
        } catch (FileSystemException e) {
            fail();
        }

        try {
            myFileSystem.makeDirectoryRecursively("/a/x/x");
            myFileSystem.linkHard("/a", "/a/x/x");
            fail();
        } catch (FileSystemException e) {
            assertTrue(e instanceof PathInvalidException);
        }

        try {
            myFileSystem.makeDirectoryRecursively("/z");
            myFileSystem.touchFile("/z/file");
            myFileSystem.linkHard("/a", "/z/file");
            fail();
        } catch (FileSystemException e) {
            assertTrue(e instanceof PathInvalidException);
        }

        try {
            myFileSystem.makeDirectory("/z/a");
            myFileSystem.linkHard("/a", "/z/");
            fail();
        } catch (FileSystemException e) {
            assertTrue(e instanceof PathInvalidException);
        }

        try {
            myFileSystem.linkSoft("/b", "/c");
            assertEquals("/b", myFileSystem.readLink("/c"));
        } catch (FileSystemException e) {
            fail();
            assertTrue(e instanceof PathInvalidException);
        }

        try {
            myFileSystem.linkHard("/b", "/e");
            // assertEquals("/b", myFileSystem.readLink("/e"));
            fail();
        } catch (FileSystemException e) {
            assertTrue(e instanceof PathInvalidException);
        }

        try {
            myFileSystem.readLink("/b");
            fail();
        } catch (FileSystemException e) {
            assertTrue(e instanceof PathInvalidException);
        }

        try {
            myFileSystem.readLink("///");
            fail();
        } catch (FileSystemException e) {
            assertTrue(e instanceof PathInvalidException);
        }

        try {
            myFileSystem.readLink("/c/");
            fail();
        } catch (FileSystemException e) {
            assertTrue(e instanceof PathInvalidException);
        }
    }

    /**
     * Method: linkHard(String src, String dst)
     */
    @Test
    public void testLinkHard() throws Exception {
//TODO: Test goes here...
        try {
            myFileSystem.makeDirectory("a");
            myFileSystem.linkHard("a", "a");
            fail();
        } catch (FileSystemException e) {
            assertTrue(e instanceof PathInvalidException);
        }
        try {
            myFileSystem.touchFile("/a/test.txt");
            myFileSystem.linkHard("test.txt", "a");
            fail();
        } catch (FileSystemException e) {
            assertEquals("Path test.txt is invalid", e.getMessage());
        }
        try {
            myFileSystem.linkHard("/a/test.txt", "/a");
            fail();
        } catch (FileSystemException e) {
            assertTrue(e instanceof PathExistsException);
        }
        try {
            myFileSystem.linkHard("/a/test.txt", "/a/");
            fail();
        } catch (FileSystemException e) {
            assertTrue(e instanceof PathExistsException);
        }
        try {
            myFileSystem.linkHard("/a/test.txt", "/b/");
            fail();
        } catch (FileSystemException e) {
            assertTrue(e instanceof PathInvalidException);
        }
        try {
            myFileSystem.makeDirectory("/b");
            myFileSystem.linkHard("/a/test.txt", "/b/");
            myFileSystem.fileWrite("/a/test.txt", "123");
            assertTrue(myFileSystem.catFile("/b/test.txt").equals("123"));
            myFileSystem.fileAppend("/b/test.txt", "456");
            assertTrue(myFileSystem.catFile("/a/test.txt").equals("123456"));
            myFileSystem.linkHard("/a/test.txt", "/b/test2.txt");
            assertTrue(myFileSystem.list("/b").indexOf("test2.txt") != -1);
            myFileSystem.linkHard("/b/test.txt", "/b/test3.txt");
            assertTrue(myFileSystem.list("/b").indexOf("test3.txt") != -1);
            myFileSystem.fileWrite("/b/test3.txt", "new");
            assertTrue(myFileSystem.catFile("/a/test.txt").equals("new"));
            assertTrue(myFileSystem.catFile("/b/test.txt").equals("new"));
            assertTrue(myFileSystem.catFile("/b/test2.txt").equals("new"));
        } catch (FileSystemException e) {
            fail();
        }


        try {
            assertTrue(myFileSystem.removeFile("/b/test.txt").indexOf("/b/test.txt") != -1);
            myFileSystem.fileWrite("/b/test3.txt", "again");
            assertTrue(myFileSystem.catFile("/a/test.txt").equals("again"));
        } catch (FileSystemException e) {
            fail();
        }
        try {
            assertTrue(myFileSystem.removeFile("/a/test.txt").indexOf("/a/test.txt") != -1);
            myFileSystem.fileWrite("/b/test3.txt", "final");
        } catch (FileSystemException e) {
            fail();
        }
        try {
            myFileSystem.touchFile("/a/test.txt");
            myFileSystem.fileWrite("/b/test3.txt", "final");
            assertTrue(myFileSystem.catFile("/a/test.txt").equals(""));
            assertTrue(myFileSystem.catFile("/b/test2.txt").equals("final"));
        } catch (FileSystemException e) {
            fail();
        }

        try {
            myFileSystem.makeDirectoryRecursively("/t");
            myFileSystem.touchFile("/t/f1");
            myFileSystem.linkHard("/t/f1", "/t/f1");
            fail();
        } catch (FileSystemException e) {
            assertEquals("Path /t/f1 is invalid", e.getMessage());
        }

        try {
            myFileSystem.touchFile("/t/f2");
            myFileSystem.linkHard("/t/f1", "/t/f2");
            fail();
        } catch (FileSystemException e) {
            assertEquals("Path /t/f2 exists", e.getMessage());
        }

    }

    /**
     * Method: move(String src, String dst)
     */
    @Test
    public void testMove() throws Exception {
//TODO: Test goes here...
        myFileSystem.fileWrite("test.txt", "123");
        myFileSystem.makeDirectory("a");
        myFileSystem.move("test.txt", "b");
        assertEquals("root root 0 3 3 2 /", myFileSystem.information("."));
        try {
            myFileSystem.makeDirectory("a");
            fail();
        } catch (FileSystemException e) {
            assertTrue(e instanceof PathExistsException);
        }
        try {
            myFileSystem.move("a", "a");
            fail();
        } catch (FileSystemException e) {
            assertTrue(e instanceof PathInvalidException);
        }
        try {
            myFileSystem.touchFile("/a/test.txt");
            myFileSystem.move("test.txt", "a");
            fail();
        } catch (FileSystemException e) {
            assertEquals("Path test.txt is invalid", e.getMessage());
        }
        try {
            myFileSystem.move("/a/test.txt", "/b/");
            fail();
        } catch (FileSystemException e) {
            assertTrue(e instanceof PathInvalidException);
        }

        try {
            myFileSystem.makeDirectory("/b");
            fail();
        } catch (FileSystemException e) {
            assertTrue(e instanceof PathInvalidException);
        }
        try {
            assertTrue(myFileSystem.list("/").indexOf("b") != -1);
        } catch (FileSystemException e) {
            fail();
            assertTrue(e instanceof PathInvalidException);
        }

        try {
            myFileSystem.makeDirectory("dir1");
            myFileSystem.move("b", "dir1");
            assertEquals("123", myFileSystem.catFile("/dir1/b"));
        } catch (FileSystemException e) {
            fail();
            assertTrue(e instanceof PathInvalidException);
        }

        try {
            myFileSystem.makeDirectory("dir2");
            myFileSystem.move("/dir1/b", "dir2/newb");
            assertEquals("123", myFileSystem.catFile("/dir2/newb"));
            myFileSystem.catFile("/dir1/b");
            fail();
        } catch (FileSystemException e) {
            assertTrue(e instanceof PathInvalidException);
        }
        try {
            myFileSystem.fileWrite("file2", "456");
            myFileSystem.move("file2", "/dir2/newb");
            assertEquals("456", myFileSystem.catFile("/dir2/newb"));
        } catch (FileSystemException e) {
            fail();
            assertTrue(e instanceof PathInvalidException);
        }
        try {
            myFileSystem.fileWrite("file2", "456");
            myFileSystem.move("file2", "/dir2/");
            assertEquals("456", myFileSystem.catFile("/dir2/file2"));
        } catch (FileSystemException e) {
            fail();
            assertTrue(e instanceof PathInvalidException);
        }
        try {
            myFileSystem.fileWrite("file3", "789");
            myFileSystem.fileWrite("/dir2/file3", "xxx");
            myFileSystem.move("file3", "/dir2/");
            assertEquals("789", myFileSystem.catFile("/dir2/file3"));
        } catch (FileSystemException e) {
            fail();
            assertTrue(e instanceof PathInvalidException);
        }

        try {
            myFileSystem.fileWrite("dir5", "yyy");
            myFileSystem.makeDirectoryRecursively("/dir4/dir5/");
            myFileSystem.move("dir5", "/dir4/");
            fail();
        } catch (FileSystemException e) {
            assertTrue(e instanceof PathExistsException);
        }
        try {
            myFileSystem.fileWrite("/dir1/b", "123");
            myFileSystem.move("/dir1", "/dir4");
            assertEquals("123", myFileSystem.catFile("/dir4/dir1/b"));
        } catch (FileSystemException e) {
            fail();
            assertTrue(e instanceof PathExistsException);
        }
        try {
            myFileSystem.makeDirectory("/dir6");
            myFileSystem.move("dir6", "dir4");
            myFileSystem.fileAppend("/dir4/dir6/a.txt", "adw");
        } catch (FileSystemException e) {
            fail();
            assertTrue(e instanceof PathExistsException);
        }
        try {
            myFileSystem.makeDirectory("/dir6");
            myFileSystem.move("dir6", "dir4");
            fail();
        } catch (FileSystemException e) {
            assertTrue(e instanceof PathExistsException);
        }
        try {
            myFileSystem.move("dir4/dir6", "dir4");
            fail();
        } catch (FileSystemException e) {
            assertTrue(e instanceof PathExistsException);
        }
        try {
            myFileSystem.makeDirectoryRecursively("/dir4/dir6/dir7");
            myFileSystem.move("dir4", "dir4/dir6/dir7");
            fail();
        } catch (FileSystemException e) {
            assertTrue(e instanceof PathInvalidException);
        }
        try {
            myFileSystem.fileWrite("/dir2/b", "123");
            myFileSystem.makeDirectory("b");
            myFileSystem.move("b", "dir2");
            fail();
        } catch (FileSystemException e) {
            assertTrue(e instanceof PathExistsException);
        }
        try {
            myFileSystem.move("/", "dir4");
            fail();
        } catch (FileSystemException e) {
            assertTrue(e instanceof PathInvalidException);
        }

        try {
            myFileSystem.touchFile("f1");
            myFileSystem.move("/f1", "/f2");
            assertEquals("root root 47 48 0 1 /f2", myFileSystem.information("/f2"));
        } catch (FileSystemException e) {
            fail();
        }

        try {
            myFileSystem.makeDirectoryRecursively("/w1/w2/w3");
            myFileSystem.touchFile("/w1/f1");
            myUserSystem.changeUser("lcy");
            myFileSystem.makeDirectoryRecursively("/x1/x2/w1");
            myFileSystem.move("/w1", "/x1/x2/");
            assertEquals("lcy lcy 53 54 0 2 /x1/x2/w1", myFileSystem.information("/x1/x2/w1"));
            assertEquals("root root 50 54 0 1 /x1/x2/w1/w2", myFileSystem.information("/x1/x2/w1/w2"));
            assertEquals("root root 51 54 0 1 /x1/x2/w1/f1", myFileSystem.information("/x1/x2/w1/f1"));
            assertEquals("root root 50 54 0 0 /x1/x2/w1/w2/w3", myFileSystem.information("/x1/x2/w1/w2/w3"));
            myUserSystem.exitUser();
        } catch (FileSystemException e) {
            fail();
        }

        try {
            myFileSystem.makeDirectoryRecursively("/w1/w2/w3");
            myFileSystem.touchFile("f1");
            myFileSystem.move("/w1", "/f1");
            fail();
        } catch (FileSystemException e) {
            assertEquals("Path /f1 exists", e.getMessage());
        }

        try {
            myFileSystem.makeDirectory("d1");
            myFileSystem.move("/d1", "/d2");
            assertEquals("root root 63 64 0 0 /d2", myFileSystem.information("/d2"));
        } catch (FileSystemException e) {
            fail();
        }
    }

    /**
     * Method: copy(String s, String s1)
     */
    @Test
    public void testCopy() throws Exception {
//TODO: Test goes here...

        try {
            myFileSystem.copy("/level_1/level_2/level_3/file1", "/");
            fail();
        } catch (FileSystemException e) {
            assertEquals("Path /level_1/level_2/level_3/file1 is invalid", e.getMessage());
        }

        try {
            myFileSystem.makeDirectoryRecursively("/level_1A/level_2A/level_3A");
            myFileSystem.makeDirectoryRecursively("/level_1B/level_2B/level_3B");
            myFileSystem.copy("/level_1A/level_2A/level_3A/", "/level_1B/level_2B/level_3B/level_4B");
            assertEquals("level_4B", myFileSystem.list("/level_1B/level_2B/level_3B"));
            assertEquals("root root 4 4 0 0 /level_1B/level_2B/level_3B/level_4B",
                    myFileSystem.information("/level_1B/level_2B/level_3B/level_4B"));
        } catch (FileSystemException e) {
            fail();
        }

        try {
            myFileSystem.makeDirectoryRecursively("/level_1C/level_2C/level_3C");
            myFileSystem.copy("/level_1A/level_2A/level_3A", "/level_1C");
            assertEquals("level_2C level_3A", myFileSystem.list("/level_1C"));
            assertEquals("root root 8 8 0 0 /level_1C/level_3A",
                    myFileSystem.information("/level_1C/level_3A"));
        } catch (FileSystemException e) {
            fail();
        }

        try {
            myFileSystem.copy("/level_1A/level_2A/level_3A", "/");
            assertEquals("level_1A level_1B level_1C level_3A", myFileSystem.list("/"));
            assertEquals("root root 11 11 0 0 /level_3A",
                    myFileSystem.information("/level_3A"));
        } catch (FileSystemException e) {
            fail();
        }

        try {
            myFileSystem.fileWrite("/level_1A/level_2A/file1.txt", "apple@n");
            myFileSystem.copy("/level_1A/level_2A/file1.txt", "/level_1B");
            assertEquals("file1.txt level_2B", myFileSystem.list("/level_1B"));
            assertEquals("apple\n", myFileSystem.catFile("/level_1B/file1.txt"));
            assertEquals("root root 3 15 6 2 /level_1B",
                    myFileSystem.information("/level_1B"));

            myFileSystem.copy("/level_1A/level_2A/file1.txt", "/level_1B/file2.txt");
            assertEquals("file1.txt file2.txt level_2B", myFileSystem.list("/level_1B"));
            assertEquals("apple\n", myFileSystem.catFile("/level_1B/file2.txt"));
            assertEquals("root root 3 19 12 3 /level_1B",
                    myFileSystem.information("/level_1B"));
        } catch (FileSystemException e) {
            fail();
        }

        try {
            myFileSystem.copy("/level_1A", "/level_1B/");
            assertEquals("file1.txt file2.txt level_1A level_2B", myFileSystem.list("/level_1B"));
            assertEquals("root root 23 23 6 1 /level_1B/level_1A",
                    myFileSystem.information("/level_1B/level_1A"));
            assertEquals("root root 23 23 6 2 /level_1B/level_1A/level_2A",
                    myFileSystem.information("/level_1B/level_1A/level_2A"));
        } catch (FileSystemException e) {
            fail();
        }

        try {
            myFileSystem.copy("/level_1B", "/level_1D");
            assertEquals("file1.txt file2.txt level_1A level_2B", myFileSystem.list("/level_1D"));
            assertEquals("root root 27 27 18 4 /level_1D",
                    myFileSystem.information("/level_1D"));
        } catch (FileSystemException e) {
            fail();
        }

        try {
            myFileSystem.makeDirectory("/level_1D/level_1B");
            myFileSystem.copy("/level_1B", "/level_1D");
            assertEquals(myFileSystem.list("/level_1B"), myFileSystem.list("/level_1D/level_1B"));
            assertEquals("root root 30 31 18 4 /level_1D/level_1B",
                    myFileSystem.information("/level_1D/level_1B"));
        } catch (FileSystemException e) {
            fail();
        }

        try {
            myFileSystem.copy("/level_1B", "/level_1D");
            fail();
        } catch (FileSystemException e) {
            assertEquals("Path /level_1D/level_1B exists", e.getMessage());
        }

        try {
            myFileSystem.fileWrite("/level_1D/text", "zyh nb!@n");
            assertEquals("apple\n", myFileSystem.catFile("/level_1A/level_2A/file1.txt"));
            myFileSystem.copy("/level_1D/text", "/level_1A/level_2A/file1.txt");
            assertEquals("zyh nb!\n", myFileSystem.catFile("/level_1A/level_2A/file1.txt"));
            assertEquals("root root 2 2 8 1 /level_1A",
                    myFileSystem.information("/level_1A"));
        } catch (FileSystemException e) {
            fail();
        }

        try {
            myFileSystem.fileWrite("/level_1D/file1.txt", "lcy nb!@n");
            assertEquals("zyh nb!\n", myFileSystem.catFile("/level_1A/level_2A/file1.txt"));
            myFileSystem.copy("/level_1D/file1.txt", "/level_1A/level_2A/");
            assertEquals("lcy nb!\n", myFileSystem.catFile("/level_1A/level_2A/file1.txt"));
            assertEquals("root root 2 2 8 1 /level_1A",
                    myFileSystem.information("/level_1A"));
        } catch (FileSystemException e) {
            fail();
        }

        try {
            myFileSystem.makeDirectoryRecursively("/level_1E/file1.txt");
            myFileSystem.copy("/level_1A/level_2A/file1.txt", "/level_1E");
            fail();
        } catch (FileSystemException e) {
            assertEquals("Path /level_1E/file1.txt exists", e.getMessage());
        }

        try {
            myFileSystem.copy("/level_1B", "/level_1B");
            fail();
        } catch (FileSystemException e) {
            assertEquals("Path /level_1B is invalid", e.getMessage());
        }

        try {
            myFileSystem.makeDirectoryRecursively("/level_1E/file1.txt");
            myFileSystem.copy("/level_1E/file1.txt", "/level_1A/level_2A/file1.txt");
            fail();
        } catch (FileSystemException e) {
            assertEquals("Path /level_1A/level_2A/file1.txt exists", e.getMessage());
        }

        try {
            myFileSystem.copy("/level_1E/", "/level_1E/file1.txt");
            fail();
        } catch (FileSystemException e) {
            assertEquals("Path /level_1E/file1.txt is invalid", e.getMessage());
        }
    }

    @Test
    public void testForHardLink() throws Exception {
        myFileSystem.makeDirectoryRecursively("/a1/a2/a3/a4");
        myFileSystem.makeDirectoryRecursively("/a1/a2_1/a3_1/a4_1");
        myFileSystem.makeDirectoryRecursively("/b1/b2/b3/b4");
        myFileSystem.makeDirectoryRecursively("/b1/b2/b3_1/b4_1");
        myFileSystem.makeDirectoryRecursively("/c1/c2/c3/c4_1");
        try {
            myFileSystem.fileWrite("/a1/a2/a3/a4/file1.txt", "lcyIsPig");
            myFileSystem.linkHard("/a1/a2/a3/a4/file1.txt", "/b1/b2/b3/");
            myFileSystem.linkHard("/a1/a2/a3/a4/file1.txt", "/b1/b2");
            myFileSystem.linkHard("/a1/a2/a3/a4/file1.txt", "/b1/b2/file2.txt");
            assertEquals("lcyIsPig", myFileSystem.catFile("/b1/b2/b3/file1.txt"));
            assertEquals("root root 3 7 8 2 /b1/b2/b3", myFileSystem.information("/b1/b2/b3/"));
            assertEquals("root root 3 9 24 4 /b1/b2", myFileSystem.information("/b1/b2"));
            assertEquals("root root 3 3 24 1 /b1", myFileSystem.information("/b1"));
            assertEquals("root root 0 5 32 3 /", myFileSystem.information("/"));
            myFileSystem.fileAppend("/a1/a2/a3/a4/file1.txt", "AndLike");
            assertEquals("lcyIsPigAndLike", myFileSystem.catFile("/b1/b2/file1.txt"));
            assertEquals("root root 3 7 15 2 /b1/b2/b3", myFileSystem.information("/b1/b2/b3/"));
            assertEquals("root root 3 9 45 4 /b1/b2", myFileSystem.information("/b1/b2"));
            assertEquals("root root 3 3 45 1 /b1", myFileSystem.information("/b1"));
            assertEquals("root root 0 5 60 3 /", myFileSystem.information("/"));
        } catch (FileSystemException e) {
            fail();
        }

        try {
            myFileSystem.linkSoft("/b1/b2/b3/file1.txt", "/c1/link_file");
            assertEquals("root root 21 21 0 1 /c1/link_file", myFileSystem.information("/c1/link_file"));
            myFileSystem.fileWrite("/c1/link_file", "0123456789");
            assertEquals("root root 0 5 40 3 /", myFileSystem.information("/"));
            myFileSystem.linkHard("/c1/link_file", "/c1/c2/link_link_file");
            myFileSystem.fileWrite("/c1/c2/link_link_file", "Java");
            assertEquals("Java", myFileSystem.catFile("/b1/b2/file2.txt"));
        } catch (FileSystemException e) {
            fail();
        }
    }

    @Test
    public void testSoftLink() throws Exception {
        myFileSystem.makeDirectoryRecursively("/a1/a2/a3/a4");
        myFileSystem.makeDirectoryRecursively("/a1/a2_1/a3_1/a4_1");
        myFileSystem.makeDirectoryRecursively("/b1/b2/b3/b4");
        myFileSystem.makeDirectoryRecursively("/b1/b2/b3_1/b4_1");
        myFileSystem.makeDirectoryRecursively("/c1/c2/c3/c4_1");
        try {
            myFileSystem.linkSoft("/a1/a2", "/e1");
            myFileSystem.linkSoft("/e1", "/b1/e1_link");
            myFileSystem.fileWrite("/a1/a2/file1", "aaa");
            assertEquals("aaa", myFileSystem.catFile("/e1/file1"));
            assertEquals("aaa", myFileSystem.catFile("/b1/e1_link/file1"));
            myFileSystem.removeFile("/e1");
            assertEquals("aaa", myFileSystem.catFile("/b1/e1_link/file1"));
        } catch (FileSystemException e) {
            fail();
        }

        try {
            myFileSystem.removeRecursively("/a1/a2");
            myFileSystem.fileWrite("/c1/c2/c3/file1", "bbb");
            myFileSystem.linkSoft("/c1/c2/c3", "/a1/a2");
            assertEquals("bbb", myFileSystem.catFile("/b1/e1_link/file1"));
        } catch (FileSystemException e) {
            fail();
        }
    }


}
