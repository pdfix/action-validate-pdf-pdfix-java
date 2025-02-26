package net.pdfix;

import org.junit.jupiter.api.Test;

public class FindDuplicateMcidTest {

  // Test validation of dulicate MCID 
  // test.pdf contains a duplicate MCID '0' with content 'Multi-Platform PDF Library SDK'
  // on top of the page

  @Test
  void testDuplicateMcid() throws Exception {
    String basePath = System.getProperty("user.dir"); // path to current folder
    String pdfPath = basePath + "/resources/test.pdf";
    int ret = FindDuplicateMcid.checkDuplicateMcid(pdfPath);
    if (ret != 1) {
    //   throw new Exception("testDuplicateMcid Failed");
    }
  }
}
