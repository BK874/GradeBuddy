package cs1635.gradebuddy.fragments;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import cs1635.gradebuddy.R;
import cs1635.gradebuddy.activities.Calculations;
import cs1635.gradebuddy.database.DatabaseAccess;
import cs1635.gradebuddy.database.GetClassesListener;
import cs1635.gradebuddy.models.Course;

/* Fragment that allows the user to calculate the GPA */
public class CalculateGpaFragment extends Fragment implements GetClassesListener, View.OnClickListener {
    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState){
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_calculate_gpa, container, false);

        final LinearLayout goalLayout = (LinearLayout)view.findViewById(R.id.gpaGoalLayout);
        final Button btngpa = new Button(getActivity());
        btngpa.setText("✓");
        btngpa.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.1f));
        goalLayout.addView(btngpa);

        final DatabaseAccess dba = new DatabaseAccess();
        dba.setGetClassListener(new GetClassesListener() {
            @Override
            public void getClasses(List<Course> courses) {
                LinearLayout ll = (LinearLayout)view.findViewById(R.id.goalsInputLayout);
                List<String> notGradedCourses = new ArrayList<>();
                for(Course currentCourse : courses) {
                    if(currentCourse.getGrade().equals("")) {
                        final Course currCourse = currentCourse;
                        LinearLayout nl = new LinearLayout(getActivity());
                        nl.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        TextView tv = new TextView(getActivity());
                        tv.setText(currentCourse.getName());
                        tv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.7f));
                        final EditText et = new EditText(getActivity());
                        et.setHint(currentCourse.getDesiredGrade());
                        et.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.2f));
                        nl.addView(tv);
                        nl.addView(et);

                        EditText etgpa = (EditText)view.findViewById(R.id.gpaGoal);
                        TextView tvgpa = (TextView)view.findViewById(R.id.expectedTermGpaNumberTextView);
                        tvgpa.setText(etgpa.getText().toString());

                        Button btn = new Button(getActivity());
                        btn.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.1f));
                        btn.setText("✓");
                        nl.addView(btn);
                        ll.addView(nl);

                        if(!currentCourse.getDesiredGrade().equals(""))  notGradedCourses.add(currentCourse.getDesiredGrade());

                        btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String desired = et.getText().toString();
                                String name = currCourse.getName();
                                String grade = currCourse.getGrade();
                                Course oldCourse = new Course(name, currCourse.getCredits(), grade, "");
                                Course newCourse = new Course(name, currCourse.getCredits(), grade, desired);
                                dba.updateClass(oldCourse, newCourse);
                            }
                        });
                    }
                }
                Calculations calc = new Calculations(0, 0);
                double gpaAverage = calc.currentGPACalculatons(notGradedCourses);
                String gpaString = Double.toString(gpaAverage);
                TextView gpa = (TextView)view.findViewById(R.id.currentGpaNumberTextView);
                gpa.setText(gpaString);

            }
        });

        return view;
    }

    public void onClick(View view) {}

    public void getClasses(List<Course> courses) { }

}
