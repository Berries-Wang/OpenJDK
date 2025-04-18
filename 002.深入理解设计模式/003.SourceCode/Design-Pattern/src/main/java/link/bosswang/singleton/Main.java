package link.bosswang.singleton;

public class Main {

    public static void main(String[] args) {

        Thread t1 = new Thread(() -> {
            System.out.println(LazyDoubleCheckSingleton.getInstance());
            System.out.println(StaticInnerClassSingleton.getInstance());
        });

        Thread t2 = new Thread(() -> {
            System.out.println(LazyDoubleCheckSingleton.getInstance());
            System.out.println(StaticInnerClassSingleton.getInstance());
        });

        Thread t3 = new Thread(() -> {
            System.out.println(LazyDoubleCheckSingleton.getInstance());
            System.out.println(StaticInnerClassSingleton.getInstance());
        });

        t1.start();
        t2.start();
        t3.start();

    }

}