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
    private static String getObjInfo(PdsPageObject obj) {
        StringBuilder info = new StringBuilder();
        PdfRect bbox = obj.GetBBox();
        info.append(String.format("BBox: [%.2f, %.2f, %.2f, %.2f]\n", bbox.left, bbox.bottom, bbox.right, bbox.top));

        if (obj.GetObjectType() == PdfPageObjectType.kPdsPageText) {
            PdsText textObj = (PdsText) obj;
            info.append("Text: ").append(textObj.GetText()).append("\n");
        }

        return info.toString();
    }

    // Check for duplicate MCIDs in a PDF file. Return the number of dulicate mcids found
    public static int checkDuplicateMcid(String path, boolean autotag) throws Exception {
        Pdfix pdfix = new Pdfix();
        System.out.println(
                "PDFix SDK " + pdfix.GetVersionMajor() + "." + pdfix.GetVersionMinor() + "." + pdfix.GetVersionPatch());

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
                System.out.println("Warning: page " + (i + 1) + " is null in " + path);
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
                        String objType = getNiceObjType(obj.GetObjectType());
                        String objInfo = getObjInfo(obj);
                        System.out.printf("Duplicate MCID: '%d'\nPage: %d, Index: %d, Type: %s\n%s\n", mcid, i + 1, j,
                                objType, objInfo);
                        found ++;
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
