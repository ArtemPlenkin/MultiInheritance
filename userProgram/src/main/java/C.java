import many.Extends;

@Extends(parents = {B.class, D.class, A.class})
public class C extends ISomeInterfaceRoot {

    private String test;

    public C() {
        System.out.println(method1(4, 4).length);
    }
}
