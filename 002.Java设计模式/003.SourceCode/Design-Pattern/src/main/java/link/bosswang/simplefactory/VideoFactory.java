package link.bosswang.simplefactory;

/**
 * 
 * Video工厂类
 * 
 */
public final class VideoFactory {

    public static Video getVideo(String type) {
        if ("java".equals(type)) {
            return new JavaVideo();
        } else if ("python".equals(type)) {
            return new PythonVideo();
        } else {
            throw new IllegalArgumentException("参数异常");
        }
    }

}
