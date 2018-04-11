package cs1635.gradebuddy.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
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
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cs1635.gradebuddy.activities.Calculations;
import cs1635.gradebuddy.activities.MainActivity;
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
                for(final Course currentCourse : courses) {
                    TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT, 1);
                    TableRow tableRow = new TableRow(getActivity());
                    tableRow.setPadding(5, 5, 5, 5);
                    tableRow.setBackgroundColor(Color.parseColor("#ffffff"));

                    TextView classTextView = new TextView(getActivity());
                    classTextView.setText(currentCourse.getName());
                    classTextView.setLayoutParams(layoutParams);

                    TextView gradeTextView = new TextView(getActivity());
                    final String currentGradeString = currentCourse.getGrade();
                    if(currentGradeString.equals(""))
                        gradeTextView.setText("Ongoing");
                    else {
                        gradeTextView.setText(currentCourse.getGrade());
                        gradedCourses.add(currentCourse.getGrade());
                    }
                    gradeTextView.setLayoutParams(layoutParams);
                    gradeTextView.setGravity(Gravity.CENTER);

                    TextView creditsTextView = new TextView(getActivity());
                    String creditsString = Integer.toString(currentCourse.getCredits());
                    creditsTextView.setText(creditsString);
                    creditsTextView.setLayoutParams(layoutParams);
                    creditsTextView.setGravity(Gravity.CENTER);

                    Button editButton = new Button(getActivity());
                    editButton.setText("Edit");
                    editButton.setLayoutParams(layoutParams);
                    editButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            createAddClassPopup(true, currentCourse.getName(), Integer.toString(currentCourse.getCredits()), currentCourse.getGrade());
                        }
                    });
                    Button deleteButton = new Button(getActivity());
                    deleteButton.setText("Delete");
                    deleteButton.setLayoutParams(layoutParams);
                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            DatabaseAccess dba = new DatabaseAccess();
                            displayToast(currentCourse.getName() + " deleted");
                            dba.deleteClass(currentCourse);
                        }
                    });

                    tableRow.addView(editButton);
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

        RelativeLayout addClassLayout = (RelativeLayout)popupView.findViewById(R.id.addClassFormLayout);
        Button deleteClassButton = new Button(getActivity());
        Button editCourseButton = (Button)popupView.findViewById(R.id.addClassSubmitButton);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(85, 50, 85, 0);
        params.addRule(RelativeLayout.BELOW, editCourseButton.getId());
        deleteClassButton.setText("Delete Course");
        deleteClassButton.setGravity(Gravity.CENTER);
        addClassLayout.addView(deleteClassButton, params);

        deleteClassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Course courseToBeDeleted = new Course(cName, Integer.parseInt(cCredits), cGrade);
                final DatabaseAccess dba = new DatabaseAccess();
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Delete this course?");
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dba.deleteClass(courseToBeDeleted);
                        displayToast(courseToBeDeleted.getName() + " deleted");
                        dialog.dismiss();
                        refreshFragment();
                        popupWindow.dismiss();
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        displayToast(courseToBeDeleted.getName() + " not deleted");
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

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
        if(!cGrade.equals("")) {
            currentClassCheckBox.setChecked(true);
            gradeEditText.setEnabled(true);
        }

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

    /* Refreshes the Fragment to reflect current Class changes/adds */
    public void refreshFragment() {
        getActivity().getFragmentManager().beginTransaction().replace(R.id.contentFrame, new HistoryFragment()).commit();
    }

    /* Displays a message at the bottom of the screen for a couple seconds - to be used when adding/editing/deleting courses */
    public void displayToast(String message) {
        Toast.makeText(getActivity(),  message, Toast.LENGTH_SHORT).show();
    }

    /* Dummy method here because Android Studio doesn't recognize that getClasses()
     * in the Listener implements the interface. This method doesn't do anything */
    public void getClasses(List<Course> courses) { }
}