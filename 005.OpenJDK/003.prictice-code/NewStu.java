public class NewStu {
    private String name;

    public NewStu(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public static void main(String[] args) {
        NewStu obj = new NewStu("NewStu");
        System.out.println(obj.toString());
    }
}