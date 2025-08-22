package net.pdfix;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.channels.NonReadableChannelException;
import java.util.ArrayList;
import java.util.List;

import net.pdfix.pdfixlib.*;

public class FindDuplicateMcid {
    private static int kReportTypeDplicateMcid = 1;
    private static int kReportTypeArtifactMcid = 2;

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

    public static void reportMcid(int pageNum, PdsPageObject obj, int index, int mcid, int reportType) {
        // report type
        if (reportType == kReportTypeDplicateMcid) {
            System.out.println("Error: Duplicate MCID found");
        } else if (reportType == kReportTypeArtifactMcid) {
            System.out.println("Warning: Artifact with MCID found");
        }
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

    private static Boolean compareContentMarkMCID(PdsPageObject obj1, PdsPageObject obj2) {
        if (obj1 == obj2) {
            return true;
        }
        if ((obj1 == null) || (obj2 == null)) {
            return false;
        }
        PdsContentMark cm1 = obj1.GetContentMark();
        PdsContentMark cm2 = obj2.GetContentMark();

        // compare content mark index with MCID
        if (cm1.GetTagMcid() != cm2.GetTagMcid()) {
            return false;
        } 

        // compare content mark names, manes on each index must me equal
        for (int i = 0; i <= cm1.GetTagMcid(); i++) {
            if (cm1.GetTagName(i).compareTo(cm2.GetTagName(i)) != 0) {
                return false;
            }
        }
        return true;
    }

    // Check for duplicate MCIDs in a PDF file. Return the number of dulicate mcids
    // found
    public static int checkDuplicateMcid(String path) throws Exception {
        Pdfix pdfix = new Pdfix();

        File file = new File(path);
        if (!file.exists()) {
            throw new FileNotFoundException(path);
        }

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
            PdsPageObject lastObject = null;
            List<Integer> mcids = new ArrayList<Integer>();
            for (int j = 0; j < content.GetNumObjects(); j++) {
                PdsPageObject obj = content.GetObject(j);
                PdsContentMark contentMark = obj.GetContentMark();
                int mcid = obj.GetMcid();
                Boolean isArtifact = (contentMark.GetTagArtifact() != -1);

                // reports following options:
                // Error: duplicite MCID in tagged content (second MCID occurence can be in tagged content or artifact)
                // Warning: MCID set for Artifact (it may be used in tag tree)

                if ((mcid != lastMcid) || ((mcid != -1) && (lastObject != null) && (!compareContentMarkMCID(obj, lastObject)))) {
                    lastMcid = mcid;
                    if (mcid == -1) {
                        continue;
                    }
                    if (mcids.contains(mcid)) {
                        reportMcid(i, obj, j, mcid, kReportTypeDplicateMcid);
                        found++;
                    }
                    mcids.add(mcid);
                }
                if (isArtifact && (mcid != -1)) {
                    if (mcid != -1) {
                        reportMcid(i, obj, j, mcid, kReportTypeArtifactMcid);
                    }
                    lastMcid = -1;
                }
                lastObject = obj;
            }
            page.Release();
        }

        doc.Close();
        pdfix.Destroy();

        return found;
    }
}
