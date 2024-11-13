public class TestNewCommand {
    public static void main(String[] args) {
        InnerClass innerClass = new InnerClass();
        System.out.println(innerClass.toString());
    }

    public static class InnerClass {
        private String name;
    }
}
