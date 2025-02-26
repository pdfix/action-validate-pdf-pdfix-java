package net.pdfix;

import java.nio.channels.NonReadableChannelException;
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

    public static void reportMcid(int pageNum, PdsPageObject obj, int index, int mcid) {
        System.out.println("Duplicate MCID Found:");
        String objType = getNiceObjType(obj.GetObjectType());
        String objBBox = getObjBBox(obj);
        String objContent = getObjContent(obj);

        StringBuilder info = new StringBuilder();
        info.append(String.format("  %-10s: %d\n", "MCID", mcid));
        info.append(String.format("  %-10s: %d\n", "Page", pageNum + 1));
        info.append(String.format("  %-10s: %d\n", "Index", index));
        info.append(String.format("  %-10s: %s\n", "Type", objType));
        info.append(String.format("  %-10s: %s\n", "BBox", objBBox));
        if (!objContent.isEmpty()) {
            String truncatedContent = objContent.length() > 80 ? objContent.substring(0, 80) + "…"
                    : objContent;
            info.append(String.format("  %-10s: %s\n", "Content", truncatedContent));
        }

        System.out.println(info.toString());
    }

    // Check for duplicate MCIDs in a PDF file. Return the number of dulicate mcids
    // found
    public static int checkDuplicateMcid(String path) throws Exception {
        Pdfix pdfix = new Pdfix();

        PdfDoc doc = pdfix.OpenDoc(path, "");
        if (doc == null) {
            throw new RuntimeException(pdfix.GetError());
        }

        int found = 0;

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
            PdsPageObject lastObj = null;
            for (int j = 0; j < content.GetNumObjects(); j++) {
                PdsPageObject obj = content.GetObject(j);
                int mcid = obj.GetMcid();
                if ((mcid != -1) && (mcid == lastMcid)) {
                    // content marks must be equal for equal mcid
                    if (lastObj != null) {
                        if (obj.GetNumEqualTags(lastObj) != obj.GetContentMark().GetNumTags()) {
                            reportMcid(i, obj, j, mcid);
                            found++;
                        }
                    }
                } else if (mcid != lastMcid) {
                    lastMcid = mcid;
                    if (mcid == -1) {
                        continue;
                    }

                    if (mcids.contains(mcid)) {
                        reportMcid(i, obj, j, mcid);
                        found++;
                    }
                    mcids.add(mcid);
                }
                lastObj = obj;
            }

            page.Release();
        }

        doc.Close();
        pdfix.Destroy();

        return found;
    }
}
