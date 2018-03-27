package cs1635.gradebuddy.fragments;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cs1635.gradebuddy.activities.Calculations;
import iammert.com.expandablelib.ExpandCollapseListener;
import iammert.com.expandablelib.ExpandableLayout;
import iammert.com.expandablelib.Section;

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
        final TableLayout table = (TableLayout)view.findViewById(R.id.tableLayout);

        //final TextView tv = (TextView) view.findViewById(R.id.classesTextView);
        DatabaseAccess dba = new DatabaseAccess();


        dba.setGetClassListener(new GetClassesListener() {
            @Override
            public void getClasses(List<Course> courses) {
                List<String> gradedCourses = new ArrayList<>();
                for(Course currentCourse : courses) {
                    TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT, 1);
                    TableRow tableRow = new TableRow(getActivity());
                    tableRow.setPadding(5, 5, 5, 5);
                    tableRow.setBackgroundColor(Color.parseColor("#ffffff"));

                    TextView classTextView = new TextView(getActivity());
                    classTextView.setText(currentCourse.getName());
                    classTextView.setLayoutParams(layoutParams);

                    TextView gradeTextView = new TextView(getActivity());
                    String currentGradeString = currentCourse.getGrade();
                    if(currentGradeString.equals(""))
                        gradeTextView.setText("In progress");
                    else {
                        gradeTextView.setText(currentCourse.getGrade());
                        gradedCourses.add(currentCourse.getGrade());
                    }
                    gradeTextView.setLayoutParams(layoutParams);

                    TextView creditsTextView = new TextView(getActivity());
                    String creditsString = Integer.toString(currentCourse.getCredits());
                    creditsTextView.setText(creditsString);
                    creditsTextView.setLayoutParams(layoutParams);

                    tableRow.addView(creditsTextView, 0);
                    tableRow.addView(gradeTextView, 0);
                    tableRow.addView(classTextView, 0);
                    table.addView(tableRow, 1);

                }
                Calculations calc = new Calculations(0, 0);
                double gpaAverage = calc.currentGPACalculatons(gradedCourses);
                String gpaString = Double.toString(gpaAverage);
                TextView gpa = (TextView)view.findViewById(R.id.overallTermGpaNumberTextView);
                gpa.setText(gpaString);
            }
        });




        return view;

    }

    /* Dummy method here because Android Studio doesn't recognize that getClasses()
     * in the Listener implements the interface. This method doesn't do anything */
    public void getClasses(List<Course> courses) { }
}
