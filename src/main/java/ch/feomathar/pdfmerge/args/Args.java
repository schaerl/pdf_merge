package ch.feomathar.pdfmerge.args;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

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

    /**
     * Creates a new Args object based on the given input array with the help of the provided JCommander
     *
     * The provided JCommander will have the descriptions of this class' parameters (main, '-o', '-f) added.
     * The passed arguments will be validated and if this validation fails, this method will throw an exception.
     * Its message will specify the reason. In case of an exception, the args should not be used!
     *
     * @param cmder a JCommander instance that will be used to parse the arguments
     * @param args the array of string arguments to be parsed
     * @return an instance containing the parsed and validated arguments
     * @throws ArgsValidationException if the validation failed. The message will contain the reason
     */
    public static Args createAndValidate(JCommander cmder, String... args) throws ArgsValidationException {
        try {
            var parsed = new Args();
            cmder.addObject(parsed);
            cmder.parse(args);
            parsed.validate();
            return parsed;
        } catch (ParameterException e){
            throw new ArgsValidationException(e.getMessage());
        }
    }

    private void validate() throws ArgsValidationException {
        if (new File(getOutputName()).exists() && !isForce()) {
            throw new ArgsValidationException("The specified output file already exists. Change name or use '-f' to force override");
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