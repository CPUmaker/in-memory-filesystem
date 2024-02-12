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

public class ParserTest {
    @Test
    public void testSpeed() throws FileSystemException {
        String ts = "na@";
        MyFileSystem.instruction_id = 0;
        MyFileSystem myFileSystem = new MyFileSystem();
        for (int i = 0; i < 100000; i++) {
            myFileSystem.fileAppend("file", ts);
        }
    }
}
