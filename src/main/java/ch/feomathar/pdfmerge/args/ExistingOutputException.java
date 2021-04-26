package ch.feomathar.pdfmerge.args;

public class ExistingOutputException extends Exception{

    public ExistingOutputException(){
        super("The specified output file already exists. Change name or use '-f' to force override");
    }
}
