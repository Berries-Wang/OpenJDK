package link.bosswang.abstractfactory;

public class JavaCourseFactory implements CourseFactory {

    @Override
    public Video getVideo() {
        // TODO Auto-generated method stub
        return new JavaVideo();
    }

    @Override
    public Note getNote() {
        // TODO Auto-generated method stub
        return new JavaNote();
    }

}
