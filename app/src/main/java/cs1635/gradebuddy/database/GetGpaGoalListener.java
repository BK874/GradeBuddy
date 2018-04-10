package cs1635.gradebuddy.database;

import java.util.List;

import cs1635.gradebuddy.models.Course;
import cs1635.gradebuddy.models.GpaGoal;

/* Interface to be implemented by whatever fragment/activity wants to read the courses of the current user from database */
public interface GetGpaGoalListener {
    public void getGpaGoal(String gpaGoal);
}
