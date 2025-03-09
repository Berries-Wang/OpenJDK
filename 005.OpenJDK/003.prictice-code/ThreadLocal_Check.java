/**
 * 实验目的: 005.OpenJDK/002.OpenJDK8u312-GA/OpenJDK8U312-GA/jdk/src/share/classes/java/lang/ThreadLocal.java 弱引用处理
 */
public class ThreadLocal_Check{

    private static ThreadLocal<Object> test_thread_local = new ThreadLocal<Object>();
    
    public static void main(String[] args){

        test_thread_local.set(new Object());

        while(true){
            byte[] bytes = new byte[1024*256];

           Object obj =  test_thread_local.get();
           
           if(null == obj){
            System.out.println("被清理了");
            System.exit(1);
           }
        }
    }
}