package link.bosswang.factorymethod;

public class PythonVideoFactory implements VideoFactory {

    @Override
    public Video getVideo() {
        // TODO Auto-generated method stub
        return new PythonVideo();
    }

}
