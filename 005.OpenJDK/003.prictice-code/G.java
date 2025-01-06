import java.io.Serializable;

public class G {
    public static void main(String[] args) {
        G_Inner obj1 = new G_Inner();
        G_Inner obj2 = new G_Inner();
        G_Inner obj3 = new G_Inner();

        System.out.println(obj1.toString());
        System.out.println(obj2.toString());
        System.out.println(obj3.toString());
    }

    public static class G_Inner implements Serializable{

    }

}
