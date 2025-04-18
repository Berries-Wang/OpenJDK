package link.bosswang.factorymethod;

public class JavaVideoFactory implements VideoFactory {

    @Override
    public Video getVideo() {
        // TODO Auto-generated method stub
        return new JavaVideo();
    }

}
