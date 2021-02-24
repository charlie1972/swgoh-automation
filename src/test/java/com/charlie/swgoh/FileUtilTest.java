package com.charlie.swgoh;

import com.charlie.swgoh.util.FileUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FileUtilTest {

  @Test
  public void testGetFileComponents() {
    String file1 = "C:\\Temp\\files\\12.txt";
    FileUtil.FileComponents components1 = FileUtil.getFileComponents(file1);
    Assertions.assertEquals("C:\\Temp\\files", components1.getDirectoryName());
    Assertions.assertEquals("12", components1.getFileName());
    Assertions.assertEquals("txt", components1.getExtension());
    Assertions.assertEquals(file1, components1.toString());

    String file2 = "C:\\Temp\\files\\12";
    FileUtil.FileComponents components2 = FileUtil.getFileComponents(file2);
    Assertions.assertEquals("C:\\Temp\\files", components2.getDirectoryName());
    Assertions.assertEquals("12", components2.getFileName());
    Assertions.assertEquals("", components2.getExtension());
    Assertions.assertEquals(file2, components2.toString());

    String file3 = "12.html";
    FileUtil.FileComponents components3 = FileUtil.getFileComponents(file3);
    Assertions.assertEquals("12", components3.getFileName());
    Assertions.assertEquals("html", components3.getExtension());
    Assertions.assertTrue(components3.toString().endsWith(file3));
  }

}
