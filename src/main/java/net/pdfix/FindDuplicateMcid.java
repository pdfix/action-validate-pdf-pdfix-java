package net.pdfix;

import java.util.ArrayList;
import java.util.List;

import net.pdfix.pdfixlib.*;

public class FindDuplicateMcid {
  // Helper function to get a readable object type
  private static String getNiceObjType(PdfPageObjectType type) {
    switch (type) {
      case kPdsPageText:
        return "text";
      case kPdsPagePath:
        return "path";
      case kPdsPageImage:
        return "image";
      case kPdsPageShading:
        return "shading";
      case kPdsPageForm:
        return "form";
      default:
        return "unknown";
    }
  }

  // Helper function to get object information
  private static String getObjBBox(PdsPageObject obj) {
    StringBuilder info = new StringBuilder();
    PdfRect bbox = obj.GetBBox();
    info.append(String.format("[%.2f, %.2f, %.2f, %.2f]", bbox.left, bbox.bottom, bbox.right, bbox.top));
    return info.toString();
  }

  private static String getObjContent(PdsPageObject obj) {
    StringBuilder info = new StringBuilder();
    if (obj.GetObjectType() == PdfPageObjectType.kPdsPageText) {
      PdsText textObj = (PdsText) obj;
      info.append(textObj.GetText());
    }
    return info.toString();
  }

  // Check for duplicate MCIDs in a PDF file. Return the number of dulicate mcids
  // found
  public static int checkDuplicateMcid(String path, boolean autotag) throws Exception {
    Pdfix pdfix = new Pdfix();

    PdfDoc doc = pdfix.OpenDoc(path, "");
    if (doc == null) {
      throw new RuntimeException(pdfix.GetError());
    }

    int found = 0;

    if (autotag) {
      PdfTagsParams params = new PdfTagsParams();
      doc.RemoveTags();
      doc.RemoveStructTree();
      doc.AddTags(params);
    }

    for (int i = 0; i < doc.GetNumPages(); i++) {
      PdfPage page = doc.AcquirePage(i);
      if (page == null) {
        System.out.println("Warning: Unable to load page " + (i + 1));
        continue;
      }

      PdsContent content = page.GetContent();
      if (content == null) {
        page.Release();
        continue;
      }

      int lastMcid = -1;
      List<Integer> mcids = new ArrayList<Integer>();
      for (int j = 0; j < content.GetNumObjects(); j++) {
        PdsPageObject obj = content.GetObject(j);
        int mcid = obj.GetMcid();
        if (mcid != lastMcid) {
          lastMcid = mcid;
          if (mcid == -1) {
            continue;
          }

          if (mcids.contains(mcid)) {
            System.out.println("Duplicate MCID Found:");
            String objType = getNiceObjType(obj.GetObjectType());
            String objBBox = getObjBBox(obj);
            String objContent = getObjContent(obj);

            StringBuilder info = new StringBuilder();
            info.append(String.format("  %-10s: %d\n", "MCID", mcid));
            info.append(String.format("  %-10s: %d\n", "Page", i + 1));
            info.append(String.format("  %-10s: %d\n", "Index", j));
            info.append(String.format("  %-10s: %s\n", "Type", objType));
            info.append(String.format("  %-10s: %s\n", "BBox", objBBox));
            if (!objContent.isEmpty()) {
              String truncatedContent = objContent.length() > 80 ? objContent.substring(0, 80) + "…"
                  : objContent;
              info.append(String.format("  %-10s: %s\n", "Content", truncatedContent));
            }

            System.out.println(info.toString());
            found++;
          }
          mcids.add(mcid);
        }
      }

      page.Release();
    }

    doc.Close();
    pdfix.Destroy();

    return found;
  }
}
