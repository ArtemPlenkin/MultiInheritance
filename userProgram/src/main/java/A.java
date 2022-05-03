public class A implements ISomeInterface {

    private int test;

    @Override
    public double method1(double a, int b) {
        return a + b;
    }

    @Override
    public void method3(Integer a, String b) {
        System.out.println("print A");
    }


    @Override
    public boolean method2(double a, int b) {
        return a > b;
    }
}
