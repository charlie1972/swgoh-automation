package com.charlie.swgoh.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
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

    public String getFileAndExtension() {
      if (extension != null && !extension.isEmpty()) {
        return fileName + EXTENSION_SEPARATOR + extension;
      }
      else {
        return fileName;
      }
    }

    public FileComponents withDirectoryName(String directoryName) {
      return new FileComponents(directoryName, this.fileName, this.extension);
    }

    public FileComponents withFileName(String fileName) {
      return new FileComponents(this.directoryName, fileName, this.extension);
    }

    public FileComponents withExtension(String extension) {
      return new FileComponents(this.directoryName, this.fileName, extension);
    }

  }

  public static List<FileComponents> getFilesInDirectory(String directoryName) throws IOException {
    List<FileComponents> fileComponents = new ArrayList<>();
    try (DirectoryStream<Path> stream = Files.newDirectoryStream(new File(directoryName).toPath(), path -> !Files.isDirectory(path))) {
      stream.forEach(path -> fileComponents.add(getFileComponents(path.toAbsolutePath().toString())));
    }
    return fileComponents;
  }

  public static void deleteFileIfExists(String fileName) throws IOException {
    Files.deleteIfExists(new File(fileName).toPath());
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

  public static boolean exists(String fileName) {
    return Files.exists(new File(fileName).toPath());
  }

  public static String readFromFile(String fileName) throws IOException {
    File file = new File(fileName);
    if (!file.exists()) {
      return "";
    }
    return Files.readString(file.toPath(), StandardCharsets.UTF_8);
  }

  public static FileComponents getFileComponents(String fullFileName) {
    int extPos = fullFileName.lastIndexOf(EXTENSION_SEPARATOR);
    int dirPos = fullFileName.lastIndexOf(File.separatorChar);

    String fileNameWithoutExtension;
    String extension;

    if (extPos < 0 || dirPos >= 0 && extPos < dirPos) {
      fileNameWithoutExtension = fullFileName;
      extension = "";
    }
    else {
      fileNameWithoutExtension = fullFileName.substring(0, extPos);
      extension = fullFileName.substring(extPos + 1);
    }

    Path path = Paths.get(fileNameWithoutExtension);
    Path absolutePath = path.toAbsolutePath();
    String fileName = absolutePath.getFileName().toString();
    String directory = absolutePath.getParent().toString();
    return new FileComponents(directory, fileName, extension);
  }

}
