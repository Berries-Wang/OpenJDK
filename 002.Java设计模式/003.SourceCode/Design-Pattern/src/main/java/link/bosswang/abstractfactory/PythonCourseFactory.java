package link.bosswang.abstractfactory;

public class PythonCourseFactory implements CourseFactory {

    @Override
    public Video getVideo() {
        // TODO Auto-generated method stub
        return new PythonVideo();
    }

    @Override
    public Note getNote() {
        // TODO Auto-generated method stub
        return new PythonNote();
    }

}
