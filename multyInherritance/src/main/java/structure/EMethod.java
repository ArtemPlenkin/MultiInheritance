package structure;

import combinations.Strategy;

import javax.annotation.processing.Messager;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.util.*;

public class EMethod extends DefaultMethod {
    private final Messager __messager;
    private Strategy __strategy;
    private final List<VariableElement>  __varName = new ArrayList<>();
    private final List<TypeMirror> __varTypes = new ArrayList<>();

    public EMethod(ExecutableElement element, Messager messager, Strategy strategy){
        super();
        __messager = messager;
        createParameters(element);
        __strategy = strategy;
        String _name = element.getSimpleName().toString();
        setName(_name);
        String _return = element.getReturnType().toString();
        if (strategy == Strategy.ARRAY){
            _return += "[]";
        }
        setReturn(_return);
        Modifier _modifier = Modifier.PUBLIC;
        setModifier(_modifier);
        String _args = getArgsWithType();
        setArgs(_args);
        String _body = createBody();
        setBody(_body);
    }

    public Strategy getStrategy() {
        return __strategy;
    }

    private String createBody() {
        StringBuilder _builder = new StringBuilder();
        _builder.append(createStrategyMethodName())
                .append(getSimpleArgs())
                .append(";");

        return !getReturn().equals("void") ? "\t\treturn " + _builder: "\t\t" + _builder;
    }

    private String createBodyLines() {

        StringBuilder _builder = new StringBuilder();
        _builder.append(createStrategyMethodName())
                .append(getSimpleArgs());

        return !getReturn().equals("void") ? "\t\treturn " + _builder: "\t\t" + _builder;
    }

    public String createStrategyMethodName(){
        String first = getName().substring(0,1).toUpperCase();
        return __strategy.toString().toLowerCase(Locale.ROOT) + first + getName().substring(1);
    }

    public String getArgsWithType() {
        StringBuilder _builder = new StringBuilder();
        for (int i = 0; i < __varName.size(); i++) {
            _builder.append(__varTypes.get(i))
                    .append(" ")
                    .append(__varName.get(i))
                    .append(", ");
        }

        return "(" + _builder.substring(0, _builder.lastIndexOf(",")) + ")";
    }

    public String getSimpleArgs(){
        StringBuilder _builder = new StringBuilder();

        for (VariableElement variableElement : __varName) {
            _builder.append(variableElement)
                    .append(", ");
        }
        return "(" + _builder.substring(0, _builder.lastIndexOf(",")) + ")";
    }

    private void  createParameters(ExecutableElement element) {
        List<? extends VariableElement> _variableElements = element.getParameters();
        for (VariableElement _variableElement : _variableElements) {
            TypeMirror _typeMirror = _variableElement.asType();
            __varName.add(_variableElement);
            __varTypes.add(_typeMirror);

        }
    }


    private void printN(String msg){

        __messager.printMessage(Diagnostic.Kind.NOTE, getClass().getSimpleName() + " LOG: " + msg);
    }

    private void printE(String msg){
        __messager.printMessage(Diagnostic.Kind.ERROR, getClass().getSimpleName() + " LOG: " + msg);
    }



}
