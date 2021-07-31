package link.bosswang.builder;

public class Test {
    public static void main(String[] args) {

        Course javaCourse = (new Course.CourseBuilder()).buildCourseName("Java课程名称").buildCoursePPT("Java课程PPT")
                .buildCourseVideo("Java课程视频").buildCourseArticle("Java课程笔记").buildCourseQA("Java课程QA").build();

        System.out.println(javaCourse.toString());
    }
}
