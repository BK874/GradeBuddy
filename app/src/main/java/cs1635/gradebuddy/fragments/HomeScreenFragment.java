package cs1635.gradebuddy.fragments;

import android.app.AlertDialog;
import android.app.ExpandableListActivity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.text.TextWatcher;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cs1635.gradebuddy.R;
import cs1635.gradebuddy.activities.Calculations;
import cs1635.gradebuddy.activities.MainActivity;
import cs1635.gradebuddy.database.DatabaseAccess;
import cs1635.gradebuddy.database.GetClassesListener;
import cs1635.gradebuddy.database.GetGpaGoalListener;
import cs1635.gradebuddy.models.Course;
import cs1635.gradebuddy.fragments.Model.ClassCategory;
import cs1635.gradebuddy.fragments.Model.Classes;
import iammert.com.expandablelib.ExpandCollapseListener;
import iammert.com.expandablelib.ExpandableLayout;
import iammert.com.expandablelib.Section;


/* Fragment that acts as the home screen - shows current courses and allows for adding of new courses */
public class HomeScreenFragment extends Fragment implements View.OnClickListener, GetGpaGoalListener, GetClassesListener {

    /* Called when this Fragment is initialized */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home_screen, container, false);
        final DatabaseAccess dba = new DatabaseAccess();

        Button b = (Button) view.findViewById(R.id.addClassButton);
        b.setOnClickListener(this);
        Button gpaGoalButton = (Button) view.findViewById(R.id.checkGpaGoalButton);
        gpaGoalButton.setOnClickListener(this);
        final ExpandableLayout layout = (ExpandableLayout) view.findViewById(R.id.expandable_layout);
        layout.setRenderer(new ExpandableLayout.Renderer<Course,String>() {

            @Override
            public void renderParent(View view, Course course, boolean isExpanded, int parentPosition) {
                ((TextView)view.findViewById(R.id.tv_parent_name)).setText(course.getName());
                view.findViewById(R.id.arrow).setBackgroundResource(isExpanded?R.drawable.ic_arrow_up: R.drawable.ic_arrow_down);
            }

            @Override
            public void renderChild(View view, String string, int parentPosition, int childPosition) {
                ((TextView)view.findViewById(R.id.tv_child_name)).setText(string);
            }
        });

        layout.setExpandListener(new ExpandCollapseListener.ExpandListener<Course>() {
            @Override
            public void onExpanded(int i, final Course courseExpanded, View layout) {
                final Course c = courseExpanded;
                TextView parentText = (TextView)layout.findViewById(R.id.tv_parent_name);
                parentText.setTypeface(null, Typeface.BOLD);
                Button editClassButton = (Button)layout.findViewById(R.id.editClassButton);
                Button deleteClassButton = (Button)layout.findViewById(R.id.deleteClassButton);
                editClassButton.setVisibility(View.VISIBLE);
                deleteClassButton.setVisibility(View.VISIBLE);
                editClassButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        createAddClassPopup(true, c.getName(), Integer.toString(c.getCredits()), c.getGrade());
                    }
                });
                deleteClassButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Delete this course?");
                        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dba.deleteClass(courseExpanded);
                                displayToast(courseExpanded.getName() + " deleted");
                                dialog.dismiss();
                                refreshFragment();
                            }
                        });
                        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                displayToast(courseExpanded.getName() + " not deleted");
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                });
            }
        });

        layout.setCollapseListener(new ExpandCollapseListener.CollapseListener<Course>() {
            @Override
            public void onCollapsed(int i, Course courseCollapsed, View layout) {
                TextView parentText = (TextView)layout.findViewById(R.id.tv_parent_name);
                parentText.setTypeface(null, Typeface.NORMAL);
                Button editClassButton = (Button)layout.findViewById(R.id.editClassButton);
                editClassButton.setVisibility(View.INVISIBLE);
                Button deleteClassButton = (Button)layout.findViewById(R.id.deleteClassButton);
                deleteClassButton.setVisibility(View.INVISIBLE);
            }
        });

        // Display current classes (classes with an empty grade)
        dba.setGetClassListener(new GetClassesListener() {
            @Override
            public void getClasses(List<Course> courses) {
                for(Course currentCourse : courses) {
                    if(currentCourse.getGrade().equals("")) {
                        Section<Course, String> section = new Section();
                        Course course = currentCourse;
                        section.parent = course;
                        section.children.add("Course name: " + currentCourse.getName());
                        section.children.add("Number of Credits: " + (String) Integer.toString(currentCourse.getCredits()));
                        section.children.add("Current Grade: " + currentCourse.getDesiredGrade());
                        layout.addSection(section);
                    }
                }

            }
        });

        // Display username
        String currentUser = MainActivity.getCurrentUser();
        TextView userName = (TextView)view.findViewById(R.id.usernameTextView);
        userName.setText("Username: " + MainActivity.getCurrentUser());

        return view;
    }

    /* Handles button clicks in this Fragment */
    public void onClick(View view) {
        switch(view.getId()) {

            // Add Class button pressed - show popup window form input to get new class info
            case R.id.addClassButton:
                createAddClassPopup(false, "", "", "");
                break;
            case R.id.checkGpaGoalButton:
                createCheckGpaGoalPopup();
                break;
        }
    }

    /* Method that displays Add User popup window and all the logic that comes with it */
    public void createAddClassPopup(final boolean editting, final String cName, final String cCredits, final String cGrade) {
        //final View popupView = getLayoutInflater().inflate(R.layout.popup_add_class, null);
        LayoutInflater inflater = (LayoutInflater)getActivity().getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View popupView = inflater.inflate(R.layout.popup_add_class, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setFocusable(true);
        popupWindow.showAsDropDown(popupView, 0, 0);
        ((MainActivity) getActivity()).dimBehind(popupWindow);

        Button backButton = (Button) popupView.findViewById(R.id.addClassBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                popupWindow.dismiss();
            }});

        final TextView popupTitle = (TextView)popupView.findViewById(R.id.addClassTitle);
        final EditText checkClassEditText = (EditText)popupView.findViewById(R.id.classNameEditText);
        final EditText checkCreditsEditText = (EditText)popupView.findViewById(R.id.classCreditEditText);
        final EditText gradeEditText = (EditText)popupView.findViewById(R.id.gradeReceivedEditText);
        final CheckBox currentClassCheckBox = (CheckBox) popupView.findViewById(R.id.currentClassCheckBox);
        final Button addClassButton = (Button) popupView.findViewById(R.id.addClassSubmitButton);
        if(editting) {
            popupTitle.setText("Edit Course");
            checkClassEditText.setText(cName);
            checkClassEditText.setSelection(checkClassEditText.length());
            checkCreditsEditText.setText(cCredits);
            checkCreditsEditText.setSelection(checkCreditsEditText.length());
            gradeEditText.setText(cGrade);
            addClassButton.setText("Edit Course");
        }
        else {
            popupTitle.setText("Add Course");
            gradeEditText.setText("");
        }
        gradeEditText.setEnabled(false);
        addClassButton.setEnabled(false);

        // Make sure all fields are properly filled out before enabling Add Class button via the following button/checkbox listeners
        checkClassEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().trim().length() == 0 || checkCreditsEditText.getText().toString().trim().length() == 0)
                    addClassButton.setEnabled(false);
                else
                    addClassButton.setEnabled(true);
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });
        checkCreditsEditText.addTextChangedListener(new TextWatcher() {
            String gradeString = gradeEditText.getText().toString();
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().trim().length() == 0 || checkClassEditText.getText().toString().trim().length() == 0 || (gradeString.equals("") && currentClassCheckBox.isChecked()))
                    addClassButton.setEnabled(false);
                else
                    addClassButton.setEnabled(true);
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });
        gradeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(currentClassCheckBox.isChecked() && (s.toString().equals("A") || s.toString().equals("A-") || s.toString().equals("B+") || s.toString().equals("B") || s.toString().equals("B-") || s.toString().equals("C+") || s.toString().equals("C") || s.toString().equals("C-") || s.toString().equals("D") || s.toString().equals("F") || s.toString().equals("")))
                    addClassButton.setEnabled(true);
                else
                    addClassButton.setEnabled(false);
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });
        currentClassCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton checkBox, boolean isChecked) {
                String classString = checkClassEditText.getText().toString();
                String creditsString = checkCreditsEditText.getText().toString();
                String gradeString = gradeEditText.getText().toString();
                if(!checkBox.isChecked()) {
                    gradeEditText.setText("");
                    gradeEditText.setEnabled(false);
                    if(classString.length() > 0 && creditsString.length() > 0)
                        addClassButton.setEnabled(true);
                }
                else if(currentClassCheckBox.isChecked()) {
                    gradeEditText.setEnabled(true);
                    if (currentClassCheckBox.isChecked() && (gradeString.toString().equals("A") || gradeString.toString().equals("A-") || gradeString.toString().equals("B+") || gradeString.toString().equals("B") || gradeString.toString().equals("B-") || gradeString.toString().equals("C+") || gradeString.toString().equals("C") || gradeString.toString().equals("C-")  || gradeString.toString().equals("D") || gradeString.toString().equals("F")))
                        addClassButton.setEnabled(true);
                    else
                        addClassButton.setEnabled(false);
                }
            }
        });

        // Add Class button clicked, add this new class to Database while checking if it is a duplicate (same class name)
        addClassButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final DatabaseAccess dba = new DatabaseAccess();
                EditText classNameEditText = (EditText) popupView.findViewById(R.id.classNameEditText);
                EditText classCreditsEditText = (EditText) popupView.findViewById(R.id.classCreditEditText);
                final String className = classNameEditText.getText().toString();
                final int classCredits = Integer.parseInt((String) classCreditsEditText.getText().toString());
                String gradeReceived = gradeEditText.getText().toString();
                final Course newCourse = new Course(className, classCredits, gradeReceived);
                final Course oldCourse = new Course(cName, classCredits, cGrade);
                dba.setGetClassListener(new GetClassesListener() {
                    @Override
                    public void getClasses(List<Course> courses) {
                        boolean exists = false;
                        for(Course currentCourse : courses) {
                            if(newCourse.getName().equals(currentCourse.getName())) {
                                exists = true;
                                break;
                            }
                        }
                        // Editing a class, just update the old course with the new course
                        if(editting) {
                            dba.updateClass(oldCourse, newCourse);
                            displayToast(className + " edited");
                            refreshFragment();
                        }
                        else {
                            // Not editing and class doesn't exist - add it to database
                            if(!exists) {
                                dba.createClass(newCourse);
                                displayToast(className + " added");
                                refreshFragment();
                            }
                            // Not editing and class exists, ask user if they'd like to replace one in database with the one they just made
                            else {
                                Log.e("what", "what");
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setTitle("A class with this name already exists - replace it?");
                                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dba.createClass(newCourse);
                                        displayToast(className + " replaced");
                                        dialog.dismiss();
                                        refreshFragment();
                                    }
                                });
                                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        displayToast(className + " not replaced");
                                        dialog.dismiss();
                                        refreshFragment();
                                    }
                                });
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        }
                        popupWindow.dismiss();
                    }
                });
            }});
    }

    /* Method that displays the Check GPA Goal popup window and all the accompanying logic */
    public void createCheckGpaGoalPopup(){
        LayoutInflater inflater = (LayoutInflater)getActivity().getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View popupView = inflater.inflate(R.layout.popup_check_gpa_goal, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setFocusable(true);
        popupWindow.showAsDropDown(popupView, 0, 0);
        ((MainActivity) getActivity()).dimBehind(popupWindow);

        final  LinearLayout ll = (LinearLayout)popupView.findViewById(R.id.currentClasses);
        DatabaseAccess dba = new DatabaseAccess();

        dba.setGetGpaGoalListener(new GetGpaGoalListener() {
            @Override
            public void getGpaGoal(String gpaGoal) {
                TextView gpaGoalTextView = (TextView)popupView.findViewById(R.id.gpaGoalGoal);
                gpaGoalTextView.setText("Term GPA Goal:" + " " + gpaGoal);
            }
        });

        final List<String> notGraded = new ArrayList<>();
        dba.setGetClassListener(new GetClassesListener() {
            @Override
            public void getClasses(List<Course> courses) {
                for(Course currentCourse : courses) {
                    String checkGoalClasses = "";
                    if(currentCourse.getGrade().equals("")) {
                        checkGoalClasses = checkGoalClasses + currentCourse.getName() + " " + currentCourse.getDesiredGrade() + "\n";
                        TextView tv = new TextView(getActivity());
                        tv.setText(checkGoalClasses);
                        tv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        tv.setGravity(Gravity.CENTER);
                        tv.setTextSize(15f);
                        ll.addView(tv);
                        if(!currentCourse.getDesiredGrade().equals("")) {
                            notGraded.add(currentCourse.getDesiredGrade());
                            Calculations calc = new Calculations(0, 0);
                            double gpaAverage = calc.currentGPACalculatons(notGraded);
                            String gpaString = Double.toString(gpaAverage);
                            TextView gpa = (TextView)popupView.findViewById(R.id.gpaGoalCurrent);
                            gpa.setText("Term GPA: " + gpaString);
                        }
                    }
                }
            }
        });

        Button backButton = (Button) popupView.findViewById(R.id.gpaGoalBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                popupWindow.dismiss();
            }});
    }

    /* Refreshes the Fragment to reflect current Class changes/adds */
    public void refreshFragment() {
        getActivity().getFragmentManager().beginTransaction().replace(R.id.contentFrame, new HomeScreenFragment()).commit();
    }

    /* Displays a message at the bottom of the screen for a couple seconds - to be used when adding/editing/deleting courses */
    public void displayToast(String message) {
        Toast.makeText(getActivity(),  message, Toast.LENGTH_SHORT).show();
    }


    /* Dummy method here because Android Studio doesn't recognize that getClasses()
     * in the Listener implements the interface. This method doesn't do anything */
    public void getClasses(List<Course> courses) { }
    public void getGpaGoal(String gpaGoal) { }

}
