import java.util.LinkedList;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class StreamPeekTest {
    public static void main(String[] args) {
        System.out.println("Hello StreamPeekTest");
        LinkedList<String> linkedList = new LinkedList<>();
        linkedList.add("1");
        linkedList.add("2");
        linkedList.add("3");
        linkedList.stream().peek(eleNum -> { 
            System.out.println(eleNum.hashCode());
        }).collect(Collectors.toList());
    }
}