package cs1635.gradebuddy.models;

/* Model class that Keeps track of Course name, number of credits, and grade */
public class Course {

    private String name;
    private int credits;
    private String grade;

    public Course() { }

    public Course(String cName, int cCredits, String cGrade) {
        name = cName;
        credits = cCredits;
        grade = cGrade;
    }

    public String getName() {
        return name;
    }

    public int getCredits() {
        return credits;
    }

    public String getGrade() {
        return grade;
    }

    public void setName(String n) {
        name = n;
    }

    public void setCredits(int c) {
        credits = c;
    }

    public void setGrade(String g) {
        grade = g;
    }

}
