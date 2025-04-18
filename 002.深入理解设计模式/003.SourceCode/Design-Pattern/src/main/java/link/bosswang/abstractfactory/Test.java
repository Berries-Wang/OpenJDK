package link.bosswang.abstractfactory;

public class Test {
    public static void main(String[] args) {
        CourseFactory courseFactory = new JavaCourseFactory();
        Note note = courseFactory.getNote();
        Video video = courseFactory.getVideo();

        note.produce();
        video.produce();
    }
}
