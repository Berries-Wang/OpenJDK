import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 不要修改此代码
 */
public class CompletableFutureSTUThree {
    private static final AtomicLong THRED_ID = new AtomicLong(0);

    private static final PAThreadPoolExecutor TASK_THREAD_POOL = new PAThreadPoolExecutor(1,
            1, 60, TimeUnit.SECONDS, new LinkedBlockingDeque<>(),
            (task) -> {
                return new Thread(task, "CompletableFutureSTUTWO-Worker-" + THRED_ID.incrementAndGet());
            }, new ThreadPoolExecutor.CallerRunsPolicy());

    public static void main(String[] argv) {

        List<Integer> nums = new LinkedList<>();
        for (int i = 0; i < 50; i++) {
            nums.add(i);
        }

        List<CompletableFuture<Integer>> handleCFS = new LinkedList<>();
        for (Integer goalNum : nums) {
            CompletableFuture<Integer> handleCF = CompletableFuture.supplyAsync(() -> {
                System.out.println(Thread.currentThread().getName() + "This is " + goalNum);
                try {
                    Thread.sleep(800);
                } catch (Exception ex) {
                    System.out.println(Thread.currentThread().getName() + " " + goalNum + " 异常了: " + ex.getMessage());
                }

                {
                    List<Integer> numSon = new LinkedList<>();
                    for (int i = 0; i < 10; i++) {
                        numSon.add(i);
                    }
                    List<CompletableFuture<Integer>> handleCFSSON = new LinkedList<>();
                    for (Integer theNum : numSon) {
                        CompletableFuture<Integer> handleCFSON = CompletableFuture.supplyAsync(() -> {
                            System.out.println("_____> 我执行了....");
                            return theNum;
                        }, TASK_THREAD_POOL).thenApply(res -> {
                            return res;
                        }).exceptionally(err -> {
                            return 0;
                        });
                        handleCFSSON.add(handleCFSON);
                    }
                    CompletableFuture.allOf(handleCFSSON.toArray(new CompletableFuture[0])).join();
                }

                return goalNum;
            }, TASK_THREAD_POOL).thenApply((res) -> {
                System.out.println(Thread.currentThread().getName() + " " + goalNum + " Then Apply " + res);
                return res;
            }).exceptionally(err -> {
                System.out.println(Thread.currentThread().getName() + " " + goalNum + " Then Apply " + goalNum);
                return 0;
            });

            handleCFS.add(handleCF);
        }

        System.out.println(Thread.currentThread().getName() + " " + "提交完成，等待执行结束");
        CompletableFuture.allOf(handleCFS.toArray(new CompletableFuture[0])).join();
        handleCFS.forEach(eleCF -> {
            System.out.println(Thread.currentThread().getName() + " " + "结果: " + eleCF.join());
        });
        System.out.println(Thread.currentThread().getName() + " " + "执行结束");
    }
}
