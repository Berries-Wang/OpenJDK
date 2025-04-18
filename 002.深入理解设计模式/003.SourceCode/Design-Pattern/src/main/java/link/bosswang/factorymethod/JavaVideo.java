package link.bosswang.factorymethod;

public class JavaVideo extends Video {

    @Override
    public void produce() {
        System.out.println("生成Java视频");
    }
}
