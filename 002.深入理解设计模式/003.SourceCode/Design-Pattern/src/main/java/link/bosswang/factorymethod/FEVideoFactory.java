package link.bosswang.factorymethod;

public class FEVideoFactory implements VideoFactory {

    @Override
    public Video getVideo() {
        // TODO Auto-generated method stub
        return new FEVideo();
    }

}
