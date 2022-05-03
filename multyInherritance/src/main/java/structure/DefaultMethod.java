package structure;

import javax.lang.model.element.Modifier;

public class DefaultMethod {
    private Modifier __modifier;
    private String __return;
    private String __name;
    private String __args;
    private String __body;

    public DefaultMethod(Modifier modifier, String aReturn, String name, String args, String body) {
        __modifier = modifier;
        __return = aReturn;
        __name = name;
        __args = args;
        __body = body;
    }

    public DefaultMethod() {
    }

    public void setModifier(Modifier modifier) {
        __modifier = modifier;
    }

    public void setReturn(String aReturn) {
        __return = aReturn;
    }

    public void setName(String name) {
        __name = name;
    }

    public void setArgs(String args) {
        __args = args;
    }

    public void setBody(String body) {
        __body = body;
    }

    public Modifier getModifier() {
        return __modifier;
    }

    public String getReturn() {
        return __return;
    }

    public String getName() {
        return __name;
    }

    public String getArgs() {
        return __args;
    }

    public String getBody() {
        return __body;
    }



    @Override
    public String toString() {
        return constructMethod(
                __modifier.toString(),
                __return,
                __name,
                __args,
                __body);
    }

    private String constructMethod(String modifier, String returnType, String name, String params, String body) {

        return "\t" + modifier + " " + returnType + " " + name + params + " {\n" + body + "\n\t}\n";


    }

}
