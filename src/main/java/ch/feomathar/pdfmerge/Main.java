package ch.feomathar.pdfmerge;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

import com.beust.jcommander.JCommander;

import ch.feomathar.pdfmerge.args.Args;
import ch.feomathar.pdfmerge.args.ArgsValidationException;

public class Main {

    private static final String PROGRAM_NAME = "pdf_merge";

    public static void main(String[] args) {
        try {
            var jCommander = new JCommander();
            jCommander.setProgramName(Main.PROGRAM_NAME);
            var parsed = Args.createAndValidate(jCommander, args);
            if (parsed.isHelp()) {
                jCommander.usage();
                System.exit(0);
            }
            PDFMerger.merge(parsed.getOutputName(), parsed.getInputs());
        } catch (ArgsValidationException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        } catch (IOException e) {
            System.err.println("Error saving output");
            System.exit(-1);
        }

        System.out.println("Files merged successfully");
        System.exit(0);
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
