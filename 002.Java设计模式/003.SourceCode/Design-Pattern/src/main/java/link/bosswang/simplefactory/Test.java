package link.bosswang.simplefactory;

public class Test {
    public static void main(String[] args) {
        Video video = VideoFactory.getVideo("java");
        video.produce();
    }

}
