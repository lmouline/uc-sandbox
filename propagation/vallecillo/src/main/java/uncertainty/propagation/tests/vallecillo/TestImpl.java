package uncertainty.propagation.tests.vallecillo;

public class TestImpl {


    public static void main(String[] args) {
        UReal ureal1 = new UReal(5,0.3);
        UReal ureal2 = new UReal(2,0.2);

        System.out.println("Addition: " + ureal1 + " + " + ureal2 + " = " + ureal1.add(ureal2));
        System.out.println("Subtraction: " + ureal1 + " - " + ureal2 + " = " + ureal1.minus(ureal2));
        System.out.println("Division: " + ureal1 + " / " + ureal2 + " = " + ureal1.divideBy(ureal2));
        System.out.println("Multiplication: " + ureal1 + " * " + ureal2 + " = " + ureal1.mult(ureal2));

        //Python version
//        Addition: UReal(value:5.0, precision:0.3) + UReal(value:2.0, precision:0.2) = 7.0+/-0.4
//        Subtraction: UReal(value:5.0, precision:0.3) - UReal(value:2.0, precision:0.2) = 3.0+/-0.4
//        Division: UReal(value:5.0, precision:0.3) / UReal(value:2.0, precision:0.2) = 2.50+/-0.29
//        Multiplication: UReal(value:5.0, precision:0.3) * UReal(value:2.0, precision:0.2) = 10.0+/-1.2

        //Java version
//        Addition: UReal(value:5, precision:0.3) + UReal(value:2, precision:0.2) = UReal(value:7, precision:0.36)
//        Subtraction: UReal(value:5, precision:0.3) - UReal(value:2, precision:0.2) = UReal(value:3, precision:0.36)
//        Division: UReal(value:5, precision:0.3) / UReal(value:2, precision:0.2) = UReal(value:2.52, precision:0.33)
//        Multiplication: UReal(value:5, precision:0.3) * UReal(value:2, precision:0.2) = UReal(value:10, precision:1.17)
    }
}
