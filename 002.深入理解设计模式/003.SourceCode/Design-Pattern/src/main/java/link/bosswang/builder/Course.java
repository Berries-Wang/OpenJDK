package link.bosswang.builder;

public class Course {

    private String courseName;
    private String coursePPT;
    private String courseVideo;
    private String courseArticle;
    private String courseQA;

    public Course(String courseName, String coursePPT, String courseVideo, String courseArticle, String courseQA) {
        this.courseName = courseName;
        this.coursePPT = coursePPT;
        this.courseVideo = courseVideo;
        this.courseArticle = courseArticle;
        this.courseQA = courseQA;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCoursePPT() {
        return coursePPT;
    }

    public void setCoursePPT(String coursePPT) {
        this.coursePPT = coursePPT;
    }

    public String getCourseVideo() {
        return courseVideo;
    }

    public void setCourseVideo(String courseVideo) {
        this.courseVideo = courseVideo;
    }

    public String getCourseArticle() {
        return courseArticle;
    }

    public void setCourseArticle(String courseArticle) {
        this.courseArticle = courseArticle;
    }

    public String getCourseQA() {
        return courseQA;
    }

    public void setCourseQA(String courseQA) {
        this.courseQA = courseQA;
    }

    @Override
    public String toString() {
        return "Course{" + "courseName='" + courseName + '\'' + ", coursePPT='" + coursePPT + '\'' + ", courseVideo='"
                + courseVideo + '\'' + ", courseArticle='" + courseArticle + '\'' + ", courseQA='" + courseQA + '\''
                + '}';
    }

    /**
     * Course建造工厂,用内部类来实现课程构建工厂
     */
    public static class CourseBuilder {

        private String courseName;
        private String coursePPT;
        private String courseVideo;
        private String courseArticle;
        private String courseQA;

        /**
         * 构建课程名称
         *
         * @param courseName
         * @return
         */
        public CourseBuilder buildCourseName(String courseName) {
            this.courseName = courseName;
            return this;
        }

        /**
         * 构建课程PPT
         *
         * @param coursePPT
         * @return
         */
        public CourseBuilder buildCoursePPT(String coursePPT) {
            this.coursePPT = coursePPT;
            return this;
        }

        /**
         * 构建课程视频
         *
         * @param courseVideo
         * @return
         */
        public CourseBuilder buildCourseVideo(String courseVideo) {
            this.courseVideo = courseVideo;
            return this;
        }

        /**
         * 构建课程笔记
         *
         * @param courseArticle
         * @return
         */
        public CourseBuilder buildCourseArticle(String courseArticle) {
            this.courseArticle = courseArticle;
            return this;
        }

        /**
         * 构建课程问题&&答案
         *
         * @param courseQA
         * @return
         */
        public CourseBuilder buildCourseQA(String courseQA) {
            this.courseQA = courseQA;
            return this;
        }

        /**
         * 构建课程
         *
         * @return
         */
        public Course build() {
            return new Course(this.courseName, this.coursePPT, this.courseVideo, this.courseArticle, this.courseQA);
        }
    }
}