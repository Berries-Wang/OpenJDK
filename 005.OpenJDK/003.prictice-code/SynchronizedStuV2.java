
public class SynchronizedStuV2 {
    private static final SynchronizedStuV2 obj = new SynchronizedStuV2();
     private static final SynchronizedStuV2 obj2 = new SynchronizedStuV2();

     
    public static void main(String[] args) {

       
        System.out.println("Main->: SynchronizedStuV2");


        synchronized (obj) {
            synchronized(obj2){
             
            SynchronizedStuV2 obj3 = new SynchronizedStuV2();
            System.out.println("Hello");

            (new Thread(()->{
                System.out.println("I am Inner: " + Thread.currentThread().getId());
                try{Thread.sleep(10000000);}catch(Exception e){

                }
                 synchronized (obj){
                    System.out.println("Inner");
                 }
            })).start();
            }
            System.out.println("Hello");
        }


    }

    public static void say() {
 
    }

}