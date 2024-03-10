import java.util.List;
import java.util.concurrent.*;
import java.util.ArrayList;
import java.lang.String;

public class CompletableFutureSTU {
    private static final PAThreadPoolExecutor TASK_THREAD_POOL = new PAThreadPoolExecutor(10,
            100, 60, TimeUnit.SECONDS, new LinkedBlockingDeque<>(),
            Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());

    public static void main(String[] args) {
        char a = 'a';
        System.out.println("a".getBytes().length);

        for (int j = 0; j <= 2; j++) {
            List<CompletableFuture<String>> lists = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                int finalI = i;
                int finalJ = j;
                CompletableFuture<String> hello = CompletableFuture.supplyAsync(() -> {
                    System.out.println("Hello");
                    return finalJ + "" + finalI;
                }, TASK_THREAD_POOL).handleAsync((res, err) -> {
                    if (null != res) {
                        return res;
                    } else {
                        return null;
                    }
                }, TASK_THREAD_POOL);
                lists.add(hello);
            }
            CompletableFuture.allOf(lists.toArray(new CompletableFuture[lists.size()])).join();
            lists.forEach(eleRes -> {
                System.out.println(eleRes.join());
            });
            System.out.println(j + "  : 执行结束");
        }

        System.out.println("END...");
    }
}
