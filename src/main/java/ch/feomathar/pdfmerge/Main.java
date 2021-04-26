package ch.feomathar.pdfmerge;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

import com.beust.jcommander.ParameterException;

import ch.feomathar.pdfmerge.args.Args;
import ch.feomathar.pdfmerge.args.ExistingOutputException;

public class Main {

    private static final String PROGRAM_NAME = "pdf_merge";

    public static void main(String[] args) {
        try {
            Args parsed = Args.parse(args);
            PDFMerger.merge(parsed.getOutputName(), parsed.getInputs());
        } catch (ExistingOutputException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        } catch (ParameterException e) {
            System.err.println(e.getMessage());
            e.usage();
            System.exit(-1);
        } catch (IOException e) {
            System.err.println("Error saving output");
            System.exit(-1);
        }

        System.out.println("File(s) merged successfully");
        System.exit(0);
    }

    public static String getProgramName() {
        return PROGRAM_NAME;
    }

    private static class PDFMerger {
        public static void merge(String outputName, List<File> inputFiles) throws IOException {
            var mergerUtility = new PDFMergerUtility();
            mergerUtility.setDestinationFileName(outputName);
            for (var file : inputFiles) {
                mergerUtility.addSource(file);
            }
            mergerUtility.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
        }
    }
}
