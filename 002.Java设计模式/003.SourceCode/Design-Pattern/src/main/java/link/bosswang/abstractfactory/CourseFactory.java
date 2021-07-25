package link.bosswang.abstractfactory;

/**
 * 课程工厂---抽象工厂设计模式
 */
public interface CourseFactory {
    /**
     * 获取视频接口---某一产品族下的视频
     */
    public Video getVideo();

    /**
     * 获取笔记接口--getVideo返回的视频对应的产品族下的笔记
     */
    public Note getNote();
}
