package therooster.jrtools.exception;

public class TemplateNotFoundException  extends RuntimeException {
    public TemplateNotFoundException(String message) {
        super(message);
    }

    public TemplateNotFoundException() {
        super("Template not found");
    }


}
