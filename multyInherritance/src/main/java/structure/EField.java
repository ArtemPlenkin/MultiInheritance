package structure;

import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;

public class EField {
    private Modifier __modifier;
    private String __type;
    private String __name;
    private String __value;

    public EField(Modifier modifier, String type, String name, String value) {
        __modifier = modifier;
        __type = type;
        __name = name;
        __value = value;
    }

    public String getName() {
        return __name;
    }

    @Override
    public String toString() {
        return "\t" + __modifier + " " + __type + " " + __name + " = " + __value  + ";\n";
    }
}
