package net.pdfix;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

public class App {
  private static String VERSION = "1.0.0";
  private static String APP_NAME = "Validate PDF Accessibility";

  private static void displayVersion() {
    Properties properties = new Properties();
    try {
      properties.load(App.class.getClassLoader().getResourceAsStream("version.properties"));
      VERSION = properties.getProperty("project.version");
      APP_NAME = properties.getProperty("project.name");
      System.out.println(APP_NAME + " v" + VERSION + "\n");
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

  private static int processFile(File file) throws Exception {
    // Process single file
    System.out.println("File: " + file.getPath() + "");

    if (!isPDFFile(file.getAbsolutePath())) {
      System.out.println("Not a PDF file");
      return 0;
    }

    int count = FindDuplicateMcid.checkDuplicateMcid(file.getAbsolutePath());
    if (count == 0) {
      System.out.println("No duplicate MCIDs found");
    } else {
      System.out.println(String.format("Total %d duplicate MCID(s) found", count));
    }
    return count;
  }

  private static String OP_DUPLICATE_MCID = "OP_DUPLICATE_MCID";

  public static void main(String[] args) throws Exception {
    displayVersion();

    String inputFile = "";
    String inputDirectory = "";
    String last = "";

    String op = "";
    try {
      for (String s : args) {
        if (last.isEmpty()) {
          if (s.equals("--help")) {
            System.out.println("Usage:");
            System.out.println("  java -jar net.pdfix.validate-pdf-" + VERSION + ".jar [operation] [arguments]");
            System.out.println("");
            System.out.println("Operations:");
            System.out.println("  duplicate-mcid    : Validate and report duplicate MCID entries in the tagged content");
            System.out.println("");
            System.out.println("Arguments:");
            System.out.println("  -i <file>         : Path to a PDF file to process");
            System.out.println("  -d <directory>    : Path to a directory to process");
            return;
          } else if (s.equals("-i") || s.equals("--input")) {
            last = "-i";
          } else if (s.equals("-d") || s.equals("--directory")) {
            last = "-d";
          } else if (s.equals("duplicate-mcid")) {
            op = OP_DUPLICATE_MCID;
          } else {
            throw new RuntimeException("Unexpected argument " + s);
          }
        } else {
          // expected parameter name in last
          if (last.equals("-i")) {
            inputFile = s;
          } else if (last.equals("-d")) {
            inputDirectory = s;
          }
          last = ""; // reset last after parameter is set
        }
      }

      if (op.isEmpty()) {
        throw new RuntimeException("Missing operation argument. See --help");
      }

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

      int count = 0;

      // Process each file
      for (File file : fileList) {
        System.out.println("===============================================================================");
        try {
          if (op == OP_DUPLICATE_MCID) {
            count += processFile(file);
          }
        } catch (Exception e) {
          System.err.println(e.getLocalizedMessage());
        }
        System.out.println("===============================================================================\n");
      }
      System.out.println("Process complete");
      System.exit(Math.min(count, ExitCodes.MAX_SUCCESS));
    } catch (Exception e) {
      System.err.println(ExitCodes.GENERAL_MESSAGE);
      System.err.println(e.getLocalizedMessage());
      System.exit(ExitCodes.GENERAL_ERROR);
    }
  }
}