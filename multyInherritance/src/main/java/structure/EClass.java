package structure;

import combinations.Combination;
import combinations.Strategy;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.ArrayList;
import java.util.List;


public class EClass {
    private final String __name;
    private final List<EMethod> __methods = new ArrayList<>();
    private final List<EField> __fields = new ArrayList<>();
    private final Messager __message;
    private String __package;


    public EClass(String name, Messager message) {
        __message = message;
        __name = name + "Root";
        printN("create name: " + __name);
    }


    public String getPackage() {
        return __package;
    }

    public String getName() {
        return __name;
    }


    public List<EMethod> getMethods() {
        return __methods;
    }

    private void printN(String msg) {

        __message.printMessage(Diagnostic.Kind.NOTE, getClass().getSimpleName() + " LOG: " + msg);
    }

    private void printE(String msg) {
        __message.printMessage(Diagnostic.Kind.ERROR, getClass().getSimpleName() + "LOG: " + msg);
    }
}
