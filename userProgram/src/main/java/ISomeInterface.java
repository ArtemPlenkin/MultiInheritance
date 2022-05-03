import combinations.Combination;
import combinations.Strategy;
import many.Homogeneous;

@Homogeneous
public interface ISomeInterface {


    @Combination(strategy = Strategy.OR)
    boolean method2(double a, int b);

    @Combination(strategy = Strategy.ARRAY)
    double method1(double a, int b);

    @Combination()
    void method3(Integer a, String b);

}
