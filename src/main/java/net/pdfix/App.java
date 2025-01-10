package net.pdfix;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

public class App {
  private static void displayVersion() {
    Properties properties = new Properties();
    try {
      properties.load(App.class.getClassLoader().getResourceAsStream("version.properties"));
      String version = properties.getProperty("project.version");
      String name = properties.getProperty("project.name");
      System.out.println(name + " v" + version + "\n");
    } catch (IOException e) {
      System.err.println("Error reading version information.");
    }
  }

  public static boolean isPDFFile(String filePath) {
    File file = new File(filePath);

    if (!file.exists() || !file.isFile()) {
      System.out.println("File does not exist or is not a regular file: " + filePath);
      return false;
    }

    try (FileInputStream fis = new FileInputStream(file)) {
      byte[] buffer = new byte[5];
      int bytesRead = fis.read(buffer);

      if (bytesRead < 5) {
        System.out.println("File is too small to be a valid PDF: " + filePath);
        return false;
      }

      String fileSignature = new String(buffer, "UTF-8");
      return "%PDF-".equals(fileSignature);
    } catch (IOException e) {
      System.out.println("Error reading file: " + e.getMessage());
      return false;
    }
  }

  // Collects all files from the specified directory
  private static List<File> collectFiles(String directoryPath) {
    List<File> fileList = new ArrayList<>();
    try {
      Files.walk(Paths.get(directoryPath))
          .filter(Files::isRegularFile) // Include only regular files (not directories)
          .forEach(path -> fileList.add(path.toFile()));
    } catch (IOException e) {
      System.err.println("Error reading files: " + e.getMessage());
    }
    return fileList;
  }

  private static void processFile(File file) throws Exception {
    // Process single file
    System.out.println("File: " + file.getPath() + "");

    if (!isPDFFile(file.getAbsolutePath())) {
      System.out.println("Not a PDF file");
      return;
    }

    int count = FindDuplicateMcid.checkDuplicateMcid(file.getAbsolutePath(), false);
    if (count == 0) {
      System.out.println("No duplicate MCIDs found");
    } else {
      System.out.println(String.format("Total %d duplicate MCIDs found", count));
    }
  }

  public static void main(String[] args) throws Exception {
    displayVersion();

    String inputFile = "";
    String inputDirectory = "";
    String last = "";

    for (String s : args) {
      if (!last.isEmpty()) {
        // expected parameter name in last
        if (last.equals("-i")) {
          inputFile = s;
        } else if (last.equals("-d")) {
          inputDirectory = s;
        }
        last = ""; // reset last after parameter is set
      } else {
        if (s.equals("--help")) {
          System.out.println("Usage:");
          System.out.println("  -i <file>    : Process a single PDF file.");
          System.out.println("  -d <folder>  : Process all PDF files in a folder.");
          return;
        } else if (s.equals("-i") || s.equals("--input")) {
          last = "-i";
        } else if (s.equals("-d") || s.equals("--directory")) {
          last = "-d";
        }
      }
    }

    try {
      List<File> fileList = new ArrayList<>();
      if (!inputFile.isEmpty()) {
        fileList.add(new File(inputFile));
      } else if (!inputDirectory.isEmpty()) {
        fileList = collectFiles(inputDirectory);
      }

      // Sort the files
      Collections.sort(fileList, new Comparator<File>() {
        @Override
        public int compare(File f1, File f2) {
          // Compare file paths first
          int pathComparison = f1.getAbsolutePath().compareTo(f2.getAbsolutePath());

          // If paths are the same, compare file names
          if (pathComparison == 0) {
            return f1.getName().compareTo(f2.getName());
          }
          return pathComparison;
        }
      });

      // Process each file
      for (File file : fileList) {
        System.out.println("===============================================================================");
        try {
          processFile(file);
        } catch (Exception e) {
          System.out.println(e.getLocalizedMessage());
        }
        System.out.println("===============================================================================\n");
      }
      System.out.println("Process complete");
    } catch (Exception e) {
      System.out.println(e.getLocalizedMessage());
    }
  }
}