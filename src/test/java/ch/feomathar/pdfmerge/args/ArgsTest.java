package ch.feomathar.pdfmerge.args;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.beust.jcommander.ParameterException;

class ArgsTest {

    private static final String EXISTING_OUTPUT = "existing.pdf";
    private static final String INPUT1 = "input1.pdf";
    private static final String INPUT2 = "input2.pdf";
    private static final String NOT_EXISTING_INPUT = "nonexist.pdf";
    private static final String[] INPUTS = new String[] { INPUT1, INPUT2 };
    private static final String NEW_OUTPUT = "clear";
    private static final String SUFFIXED_NEW_OUTPUT = "clear.pdf";

    @BeforeAll
    static void setup() {
        try {
            new File(EXISTING_OUTPUT).createNewFile();
            for (String input : INPUTS) {
                new File(input).createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    static void tearDown() {
        new File(EXISTING_OUTPUT).delete();
        for (String input : INPUTS) {
            new File(input).delete();
        }
    }

    @AfterEach
    void cleanUpInBetween() {
        new File(NEW_OUTPUT).delete();
        new File(Args.getDefaultOutputName()).delete();
    }

    @ParameterizedTest
    @ValueSource(strings = { "-o " + NEW_OUTPUT + " " + INPUT1 + " " + INPUT2,
                             "-o " + SUFFIXED_NEW_OUTPUT + " " + INPUT1 + " " + INPUT2,
                             INPUT1 + " " + INPUT2 + " " + "-o " + NEW_OUTPUT,
                             INPUT1 + " " + INPUT2 + " " + "-o " + SUFFIXED_NEW_OUTPUT })
    void testOutputParsedCorrectly(String cli) throws ExistingOutputException {
        String[] args = cli.split(" ");
        Args parsed = Args.parse(args);
        assertEquals(SUFFIXED_NEW_OUTPUT, parsed.getOutputName());
    }

    @Test
    void testNoOutputGivesDefaultName() throws ExistingOutputException {
        String[] args = new String[] { INPUT1, INPUT2 };
        Args parsed = Args.parse(args);
        assertEquals(Args.getDefaultOutputName(), parsed.getOutputName());
    }

    @Test
    void testNonExistingInputFileThrowsParameterException() {
        assertThrows(ParameterException.class, () -> Args.parse(NOT_EXISTING_INPUT));
    }

    @Test
    void testExistingOutputCannotBeOverriddenWithoutForce() {
        String[] args = new String[] { INPUT1, INPUT2, "-o", EXISTING_OUTPUT };
        assertThrows(ExistingOutputException.class, () -> Args.parse(args));
    }

    @Test
    void testExistingOutputWithForceWorks() throws ExistingOutputException {
        String[] args = new String[] { INPUT1, INPUT2, "-o", EXISTING_OUTPUT, "-f" };
        Args parsed = Args.parse(args);
        assertEquals(EXISTING_OUTPUT, parsed.getOutputName());
    }

    @Test
    void testOWithoutArgumentFails() {
        String[] args = new String[] { INPUT1, INPUT2, "-o" };
        assertThrows(ParameterException.class, () -> Args.parse(args));
    }

    @Test
    void testInputsConvertedSuccessfully() throws ExistingOutputException {
        String[] args = new String[] { INPUT1, INPUT2 };
        Args parsed = Args.parse(args);
        assertTrue(parsed.getInputs().contains(new File(INPUT1)));
        assertTrue(parsed.getInputs().contains(new File(INPUT2)));
    }

    @ParameterizedTest
    @ValueSource(strings = { "", "-o " + NEW_OUTPUT })
    void testNoInputsFails(String cli) {
        String[] args = cli.split(" ");
        assertThrows(ParameterException.class, () -> Args.parse(args));
    }
}