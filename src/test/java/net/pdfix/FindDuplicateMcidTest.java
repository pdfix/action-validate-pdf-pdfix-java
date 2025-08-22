package net.pdfix;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class FindDuplicateMcidTest {

  // Test validation of dulicate MCID
  // test.pdf contains a duplicate MCID '0' with content 'Multi-Platform PDF
  // Library SDK'
  // on top of the page

  @Test
  void testDuplicateMcid() throws Exception {

    // list of files to test. Each item contains path to the file and number of
    // expected errors found
    List<AbstractMap.SimpleEntry<String, Integer>> testFiles = new ArrayList<>();
    testFiles.add(new AbstractMap.SimpleEntry<>("/resources/test.pdf", 1));
    testFiles.add(new AbstractMap.SimpleEntry<>("/resources/test1.pdf", 40));

    String basePath = System.getProperty("user.dir"); // path to current folder

    for (AbstractMap.SimpleEntry<String, Integer> entry : testFiles) {
      String pdfPath = basePath + entry.getKey();
      System.out.println(pdfPath);
      int numberOfDuplicities = FindDuplicateMcid.checkDuplicateMcid(pdfPath);
      if (numberOfDuplicities != entry.getValue()) {
        throw new Exception(String.format("testDuplicateMcid Failed - Expected %d duplicate MCIDs, found %d",
            entry.getValue(), numberOfDuplicities));
      }
    }
  }
}
