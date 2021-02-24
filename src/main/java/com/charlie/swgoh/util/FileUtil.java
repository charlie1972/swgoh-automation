package com.charlie.swgoh.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;

public class FileUtil {

  private static final String EXTENSION_SEPARATOR = ".";

  public static class FileComponents {
    private final String directoryName;
    private final String fileName;
    private final String extension;

    public FileComponents(String directoryName, String fileName, String extension) {
      this.directoryName = directoryName;
      this.fileName = fileName;
      this.extension = extension;
    }

    public String getDirectoryName() {
      return directoryName;
    }

    public String getFileName() {
      return fileName;
    }

    public String getExtension() {
      return extension;
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append(directoryName)
        .append(File.separatorChar)
        .append(fileName);
      if (extension != null && !extension.isEmpty()) {
        sb.append(EXTENSION_SEPARATOR)
          .append(extension);
      }
      return sb.toString();
    }
  }

  public static void writeToFile(String fileName, String line) throws IOException {
    Files.write(
            new File(fileName).toPath(),
            Collections.singletonList(line),
            StandardCharsets.UTF_8,
            StandardOpenOption.CREATE,
            StandardOpenOption.WRITE,
            StandardOpenOption.APPEND
    );
  }

  public static List<String> readFromFile(String fileName) throws IOException {
    File file = new File(fileName);
    if (!file.exists()) {
      return Collections.emptyList();
    }
    return Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
  }

  public static FileComponents getFileComponents(String fullFileName) {
    int pos = fullFileName.lastIndexOf(EXTENSION_SEPARATOR);
    String fileNameWithoutExtension = pos < 0 ? fullFileName : fullFileName.substring(0, pos);
    String extension = pos < 0 ? "" : fullFileName.substring(pos + 1);

    Path path = Paths.get(fileNameWithoutExtension);
    Path absolutePath = path.toAbsolutePath();
    String fileName = absolutePath.getFileName().toString();
    String directory = absolutePath.getParent().toString();
    return new FileComponents(directory, fileName, extension);
  }

}
