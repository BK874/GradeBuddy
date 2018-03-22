package cs1635.gradebuddy.database;

import java.util.List;

import cs1635.gradebuddy.models.Course;

/* Interface to be implemented by whatever fragment/activity wants to read the courses of the current user from database */
public interface GetClassesListener {
    public void getClasses(List<Course> courses);
}
