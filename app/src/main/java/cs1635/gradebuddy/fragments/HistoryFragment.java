package cs1635.gradebuddy.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.List;

import cs1635.gradebuddy.R;
import cs1635.gradebuddy.database.DatabaseAccess;
import cs1635.gradebuddy.database.GetClassesListener;
import cs1635.gradebuddy.models.Course;

/* Fragment that displays previous semester's GPAs */
public class HistoryFragment extends Fragment implements GetClassesListener {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_history, container, false);

        DatabaseAccess dba = new DatabaseAccess();
        dba.setGetClassListener(new GetClassesListener() {
            @Override
            public void getClasses(List<Course> courses) {
                for(Course currentCourse : courses) {
                    // Do work with each individual course here - these are the courses of the current user
                    // Firebase is async so you have to use this List<Course> in this getClasses() method
                }
            }
        });

        return view;
    }

    /* Dummy method here because Android Studio doesn't recognize that getClasses()
     * in the Listener implements the interface. This method doesn't do anything */
    public void getClasses(List<Course> courses) { }
}
