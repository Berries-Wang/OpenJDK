public class StringNewStu{
    public static void main(String[] args){
        String str1 = "Hello World";
        System.out.println(str1.getClass());

        String str2 = "Hello World";

        String str3 = new String(str1);

        synchronized(str1){
            System.out.println("synchronized(str1)");
        }


        System.out.println(str1 == str2);

        System.out.println(str1 == str3);

        System.out.println(str2 == str3);
    }
}