public class StackTopCache{
    public static void main(String[] args){

     for(long i =0;i<10000000000L;i++){
        System.out.println(add(1,2));
         System.out.println(add2(1,2));
          byte[] a =   new byte[1024*256];
     }

    }

    public static int add(int x, int y){
        byte[] a =   new byte[1024*256];
        int z = 8;
        int sum = x+y;
        return sum;
    }

    public static int add2(int x, int y){
        byte[] a =   new byte[1024*256];
        int z = 8;
        int sum = y+z;
        return sum;
    }
}
// 005.OpenJDK/003.prictice-code/jit-log.StackTopCache.txt