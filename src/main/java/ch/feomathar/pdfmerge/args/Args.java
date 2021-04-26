package ch.feomathar.pdfmerge.args;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

import ch.feomathar.pdfmerge.Main;

public class Args {

    private static final String DEFAULT_OUTPUT = "out.pdf";

    @Parameter(required = true, validateWith = FileExistsValidator.class, converter = InFileConverter.class)
    private List<File> inputs = new ArrayList<>();

    @Parameter(names = "-o", description = "Output file name", converter = OutFileSuffixer.class, order = 1)
    private String outputName = DEFAULT_OUTPUT;

    @Parameter(names = "-f", description = "Override existing output file", order = 2)
    private boolean force = false;

    @Parameter(names = "-h", description = "Display this message", help = true, order = 10)
    private boolean help;

    public static Args parse(String... args) throws ExistingOutputException {
        Args parsed = new Args();
        JCommander cmder = JCommander.newBuilder().addObject(parsed).build();
        cmder.setProgramName(Main.getProgramName());
        cmder.parse(args);
        if (parsed.isHelp()) {
            cmder.usage();
            System.exit(0);
        }
        parsed.validate();
        return parsed;
    }

    private void validate() throws ExistingOutputException {
        if (new File(getOutputName()).exists() && !isForce()) {
            throw new ExistingOutputException();
        }
    }

    public List<File> getInputs() {
        return inputs;
    }

    public String getOutputName() {
        return outputName;
    }

    public boolean isForce() {
        return force;
    }

    public boolean isHelp() {
        return help;
    }

    public static String getDefaultOutputName() {
        return DEFAULT_OUTPUT;
    }

    private static class OutFileSuffixer implements IStringConverter<String> {
        @Override
        public String convert(String value) {
            if (value.endsWith(".pdf")) {
                return value;
            } else {
                return value + ".pdf";
            }
        }
    }

    private static class InFileConverter implements IStringConverter<File> {
        @Override
        public File convert(String value) {
            return new File(value);
        }
    }

    public static class FileExistsValidator implements IParameterValidator {
        @Override
        public void validate(String name, String value) throws ParameterException {
            if (!(new File(value).exists())) {
                throw new ParameterException(String.format("Parameter %s contains files that do not exist", name));
            }
        }
    }
}