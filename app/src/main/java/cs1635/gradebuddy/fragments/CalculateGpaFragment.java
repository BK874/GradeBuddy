package cs1635.gradebuddy.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import cs1635.gradebuddy.R;
import cs1635.gradebuddy.activities.Calculations;
import cs1635.gradebuddy.database.DatabaseAccess;
import cs1635.gradebuddy.database.GetClassesListener;
import cs1635.gradebuddy.database.GetGpaGoalListener;
import cs1635.gradebuddy.models.Course;
import cs1635.gradebuddy.models.GpaGoal;

/* Fragment that allows the user to calculate the GPA */
public class CalculateGpaFragment extends Fragment implements GetClassesListener, GetGpaGoalListener, View.OnClickListener {
    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState){
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_calculate_gpa, container, false);

        final DatabaseAccess dba = new DatabaseAccess();

        dba.setGetGpaGoalListener(new GetGpaGoalListener() {
            @Override
            public void getGpaGoal(String gpaGoal) {
                Log.d("What", "guy");
                EditText gpaGoalEditText = (EditText)view.findViewById(R.id.gpaGoal);
                gpaGoalEditText.setText(gpaGoal);
                TextView gpaGoalTextView = (TextView)view.findViewById(R.id.expectedTermGpaNumberTextView);
                gpaGoalTextView.setText(gpaGoal);
            }
        });

        dba.setGetClassListener(new GetClassesListener() {
            @Override
            public void getClasses(List<Course> courses) {
                LinearLayout ll = (LinearLayout)view.findViewById(R.id.goalsInputLayout);
                List<String> notGradedCourses = new ArrayList<>();
                final List<Pair> pairs = new ArrayList<>();
                final Button submitBtn = (Button)view.findViewById(R.id.submitGoalsButton);
                LinearLayout linearLayout = (LinearLayout)view.findViewById(R.id.gpaGoalLayout);
                Pair gpaGoalPair = new Pair();
                Course gpaGoalDummyCourse = new Course("gpagoal", 0, "");
                gpaGoalPair.setCourse(gpaGoalDummyCourse);
                gpaGoalPair.setLinearLayout(linearLayout);
                pairs.add(gpaGoalPair);
                for(Course currentCourse : courses) {
                    if(currentCourse.getGrade().equals("")) {
                        final Course currCourse = currentCourse;
                        final LinearLayout nl = new LinearLayout(getActivity());
                        nl.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        TextView tv = new TextView(getActivity());
                        tv.setText(currentCourse.getName());
                        tv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.8f));
                        final EditText et = new EditText(getActivity());
                        et.setSelection(et.length());
                        et.setText(currentCourse.getDesiredGrade());
                        et.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.2f));
                        et.setGravity(Gravity.CENTER_HORIZONTAL);
                        nl.addView(tv);
                        nl.addView(et);

                        Pair pair = new Pair();
                        pair.setCourse(currentCourse);
                        pair.setLinearLayout(nl);
                        pairs.add(pair);

                        ll.addView(nl);

                        if(!currentCourse.getDesiredGrade().equals(""))  notGradedCourses.add(currentCourse.getDesiredGrade());

                    }
                }

                submitBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        for(Pair p : pairs) {
                            if(p.getCourse().getName().equals("gpagoal")) {
                                EditText gpaGoalEditText = (EditText)p.getLinearLayout().getChildAt(1);
                                GpaGoal gpaGoal = new GpaGoal(gpaGoalEditText.getText().toString());
                                dba.setUserGpaGoal(gpaGoal);
                            }
                            else {
                                EditText layoutsEditText = (EditText) p.getLinearLayout().getChildAt(1);
                                String desired = layoutsEditText.getText().toString();
                                String name = p.getCourse().getName();
                                String grade = p.getCourse().getGrade();
                                Course oldCourse = new Course(name, p.getCourse().getCredits(), grade, "");
                                Course newCourse = new Course(name, p.getCourse().getCredits(), grade, desired);
                                dba.updateClass(oldCourse, newCourse);
                            }
                        }
                        displayToast("Goals Set!");
                        refreshFragment();
                    }
                });

                Calculations calc = new Calculations(0, 0);
                double gpaAverage = calc.currentGPACalculatons(notGradedCourses);
                String gpaString = Double.toString(gpaAverage);
                TextView gpa = (TextView)view.findViewById(R.id.currentGpaNumberTextView);
                gpa.setText(gpaString);

            }
        });

        return view;
    }

    public void refreshFragment() {
        FragmentTransaction tr = getFragmentManager().beginTransaction();
        tr.replace(R.id.contentFrame, new CalculateGpaFragment());
        tr.commit();
    }

    /* Displays a message at the bottom of the screen for a couple seconds - to be used when adding/editing/deleting courses */
    public void displayToast(String message) {
        Toast.makeText(getActivity(),  message, Toast.LENGTH_SHORT).show();
    }

    public void onClick(View view) {}

    public void getClasses(List<Course> courses) { }
    public void getGpaGoal(String gpaGoal) { }

}

class Pair {
    Course course;
    LinearLayout linearLayout;
    public Pair() { }
    public void setCourse(Course c) { course = c; }
    public void setLinearLayout(LinearLayout ll) { linearLayout = ll; }
    public Course getCourse() { return course; }
    public LinearLayout getLinearLayout() { return linearLayout; }
}
