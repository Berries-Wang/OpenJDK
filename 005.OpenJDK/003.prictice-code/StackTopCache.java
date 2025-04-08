public class StackTopCache{
    public static void main(String[] args){

     for(int i =0;i<100000;i++){
        System.out.println(add(1,2));
     System.out.println(add2(1,2));
     }

    }

    public static int add(int x, int y){
        int z = 8;
        int sum = x+y;
        return sum;
    }

    public static int add2(int x, int y){
        int z = 8;
        int sum = y+z;
        return sum;
    }
}