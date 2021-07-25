package link.bosswang.factorymethod;

/**
 * 
 * 工厂方法设计模式 代码示例
 */
public class Test {

    public static void main(String[] args) {

        VideoFactory jFactory = new JavaVideoFactory();
        VideoFactory pFactory = new PythonVideoFactory();
        VideoFactory feFactory = new FEVideoFactory();

        jFactory.getVideo().produce();
        pFactory.getVideo().produce();
        feFactory.getVideo().produce();

    }

}
