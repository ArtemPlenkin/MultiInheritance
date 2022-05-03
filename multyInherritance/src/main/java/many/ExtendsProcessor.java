package many;

import com.google.auto.service.AutoService;
import combinations.Combination;
import structure.DefaultMethod;
import structure.EClass;
import structure.EField;
import structure.EMethod;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

@SupportedAnnotationTypes("many.Extends")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
public class ExtendsProcessor extends AbstractProcessor {
    private final List<EMethod> __eMethods = new ArrayList<>();
    private final Map<String, List<String>> __orders = new HashMap<>();
    private final List<DefaultMethod> __combinationMethods = new ArrayList<>();
    private final List<EField> __fields = new ArrayList<>();
    private EClass __eClass;
    private TypeMirror __interface;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        Set<? extends Element> annotatedElements =
                roundEnv.getElementsAnnotatedWith(Extends.class);

        for (Element e : annotatedElements) {
            try {
                createFile(e);
            } catch (IOException ex) {
                printE(ex.getMessage());
            }
        }
        return true;
    }

    private void createFile(Element element) throws IOException {
        Extends _annotation = element.getAnnotation(Extends.class);
        List<? extends TypeMirror> _typeMirror = getValueMirror(_annotation);

        List<TypeElement> _typeElements = new ArrayList<>();
        for (TypeMirror t : _typeMirror) {
            TypeElement _element = asTypeElement(t);
            _typeElements.add(_element);
        }

        findInterface(_typeElements);
        if (__interface != null) {
            TypeElement _eInterface = asTypeElement(__interface);
            createClass(_eInterface, processingEnv.getMessager());
            createDefaultMethods(_eInterface);
            createFields(_typeElements);
            createCombinationMethods(_eInterface);

            JavaFileObject jfo = processingEnv.getFiler().createSourceFile(__eClass.getName());
            PrintWriter out = new PrintWriter(jfo.openWriter());
            out.println("public class " + __eClass.getName() + " {\n");
            __fields.forEach(f -> {
                out.println(f.toString());
            });

            __eMethods.forEach(m -> {
                out.println(m.toString());
            });

            __combinationMethods.forEach(d -> {
                out.println(d.toString());
            });
            out.println("}");
            out.close();

        } else {
            printE("Не найден интерфейс помеченный анотацией @Homogeneous");
        }
    }

    private void createCombinationMethods(TypeElement eInterface) {
        printN("Create combination methods...");


        for (int i = 0; i < __eMethods.size(); i++) {
            EMethod _eMethod = __eMethods.get(i);
            Modifier _modifier = Modifier.PRIVATE;
            String _return = _eMethod.getReturn();
            String _strategyMethodName = _eMethod.createStrategyMethodName();
            String _defaultMethodName = _eMethod.getName();
            String _args = _eMethod.getArgsWithType();
            String _simpleArgs = _eMethod.getSimpleArgs();
            String _body = "";

            switch (_eMethod.getStrategy()) {
                case SUM -> _body = createBodyForStrategyAction(
                        _defaultMethodName, _simpleArgs, _return, "+ ");
                case OR -> _body = createBodyForStrategyAction(
                        _defaultMethodName, _simpleArgs, _return, "|| ");
                case AND -> _body = createBodyForStrategyAction(
                        _defaultMethodName, _simpleArgs, _return, "&& ");
                case ARRAY -> _body = createBodyForArrayStrategy(
                        _defaultMethodName, _simpleArgs, _return);
                case STANDARD -> _body = createBodyForStrategyStandard(
                        _defaultMethodName, _simpleArgs);
            }

            DefaultMethod _result = new DefaultMethod(_modifier,
                    _return,
                    _strategyMethodName,
                    _args,
                    _body);
            __combinationMethods.add(_result);

        }
    }

    private String createBodyForStrategyStandard(String defaultMethodName, String simpleArgs) {
        StringBuilder _builder = new StringBuilder();

        __fields.forEach(f ->
            _builder.append("\t\t")
                    .append(f.getName())
                    .append(".")
                    .append(defaultMethodName)
                    .append(simpleArgs)
                    .append(";\n"));
        return _builder.toString();
    }

    private String createBodyForArrayStrategy(String name, String simpleArgs, String returns) {
        StringBuilder _builder = new StringBuilder();
        _builder.append("\t\t")
                .append(returns)
                .append(" _result")
                .append(" = ")
                .append("new ")
                .append(new StringBuilder(returns).insert(returns.indexOf("]"), __fields.size()))
                .append(";")
                .append("\n");

        _builder.append("\t\t")
                .append("for (int i = 0; i < ")
                .append(__fields.size())
                .append(";i++){")
                .append("\n");

        __fields.forEach(f -> {
            _builder.append("\t\t\t")
                    .append("_result[i] = ")
                    .append(f.getName())
                    .append(".")
                    .append(name)
                    .append(simpleArgs)
                    .append(";")
                    .append("\n");
        });

        _builder.append("\t\t")
                .append("}")
                .append("\n");

        _builder.append("\t\t")
                .append("return _result;");

        return _builder.toString();
    }

    private String createBodyForStrategyAction(String name, String args, String returns, String action) {
        StringBuilder _builder = new StringBuilder();
        __fields.forEach(f ->
                _builder.append(f.getName())
                        .append(".")
                        .append(name)
                        .append(args)
                        .append(" ")
                        .append(action));

        String _result = _builder.substring(0, _builder.lastIndexOf(action)) + ";";

        return returns.equals("void") ? "\t\t" + _result : "\t\treturn " + _result;
    }


    private void createFields(List<TypeElement> typeElements) {
        printN("Create fields...");
        typeElements.forEach(t -> {
            Modifier _modifier = Modifier.PRIVATE;
            String _type = t.getSimpleName().toString();
            String _name = "class" + typeElements.indexOf(t);
            String _value = "new " + _type + "()";
            EField _field = new EField(_modifier, _type, _name, _value);
            __fields.add(_field);
            printN("Created field " + _field.getName());
        });
    }

    private void createDefaultMethods(TypeElement eInterface) {
        printN("Create methods...");
        List<? extends Element> _elements = eInterface.getEnclosedElements();
        for (Element e : _elements) {
            if (e instanceof ExecutableElement method) {
                Combination _combination = method.getAnnotation(Combination.class);
                if (_combination != null) {
                    EMethod _eMethod = new EMethod(
                            method,
                            processingEnv.getMessager(),
                            _combination.strategy());
                    __eMethods.add(_eMethod);
                    createOrder(_eMethod.getName(), _combination);

                } else {
                    printE(method.getSimpleName() + " не помечен аннотацией @Combination");
                }
            }
        }
    }

    private void createOrder(String name, Combination combination) {
        List<? extends TypeMirror> _typeMirrors = getValueMirror(combination);
        assert _typeMirrors != null;
        if (_typeMirrors.size() > 0){
            List<TypeElement> _elements = new ArrayList<>();
            _typeMirrors.forEach(t ->{
                TypeElement _e = asTypeElement(t);
                _elements.add(_e);
            });

            List<String> _list = _elements.stream()
                    .map(TypeElement::getSimpleName)
                    .map(Objects::toString)
                    .collect(Collectors.toList());
            __orders.put(name,  _list);
        }
    }

    private void createClass(TypeElement name, Messager messager) {
        printN("Create class...");
        __eClass = new EClass(name.getSimpleName().toString(), messager);
    }

    private void findInterface(List<TypeElement> typeElements) {
        printN("Find interface...");
        boolean isFind = false;
        for (TypeElement _typeElement : typeElements) {
            for (TypeMirror _typeMirror : _typeElement.getInterfaces()) {
                printN(_typeMirror.toString());
                TypeElement _element = asTypeElement(_typeMirror);
                if (_element.getAnnotation(Homogeneous.class) != null) {
                    __interface = _typeMirror;
                    printN("Find!");
                    isFind = true;
                    break;
                }
            }
            if (!isFind) {
                printE("Класс " + _typeElement.getSimpleName() + " не является элементом гомогенной иерархии");
                break;
            }
            isFind = false;
        }
    }

    private List<? extends TypeMirror> getValueMirror(Extends annotation) {
        try {
            annotation.parents();
        } catch (MirroredTypesException e) {
            printN("getAnnotationMirror... ");
            return e.getTypeMirrors();
        }
        return null;
    }

    private List<? extends TypeMirror> getValueMirror(Combination annotation) {
        try {
            annotation.order();
        } catch (MirroredTypesException e) {
            printN("getAnnotationMirror... ");
            return e.getTypeMirrors();
        }
        return null;
    }


    private TypeElement asTypeElement(TypeMirror typeMirror) {
        Types TypeUtils = processingEnv.getTypeUtils();
        return (TypeElement) TypeUtils.asElement(typeMirror);
    }


    private void printN(String msg) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "LOGGING: " + msg);
    }

    private void printE(String msg) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "LOGGING: " + msg);
    }
}
