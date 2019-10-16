import java.util.ArrayList;

public class JavaTest {


    public static void main(String args[]) {
        ArrayList<String> test = new ArrayList<>();
        test.add("1.23");
        test.add("3.77");

        double sum = test.stream().mapToDouble(Double::valueOf).sum();
        System.out.println(sum);
    }


}
