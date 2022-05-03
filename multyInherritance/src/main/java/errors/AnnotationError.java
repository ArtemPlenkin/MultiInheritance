package errors;

public class AnnotationError extends Exception{

    public AnnotationError(String className) {
        super(className + "error! " + "Анотация @manyextends.Extends применима только к классам");
    }
}
