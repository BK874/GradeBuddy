package cs1635.gradebuddy.fragments;

import android.app.AlertDialog;
import android.app.ExpandableListActivity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.text.TextWatcher;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cs1635.gradebuddy.R;
import cs1635.gradebuddy.activities.MainActivity;
import cs1635.gradebuddy.database.DatabaseAccess;
import cs1635.gradebuddy.database.GetClassesListener;
import cs1635.gradebuddy.models.Course;
import cs1635.gradebuddy.fragments.Model.ClassCategory;
import cs1635.gradebuddy.fragments.Model.Classes;
import iammert.com.expandablelib.ExpandableLayout;
import iammert.com.expandablelib.Section;


/* Fragment that acts as the home screen - shows current courses and allows for adding of new courses */
public class HomeScreenFragment extends Fragment implements View.OnClickListener, GetClassesListener {

    /* Called when this Fragment is initialized */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home_screen, container, false);

        Button b = (Button) view.findViewById(R.id.addClassButton);
        b.setOnClickListener(this);
        final ExpandableLayout layout = (ExpandableLayout) view.findViewById(R.id.expandable_layout);
        layout.setRenderer(new ExpandableLayout.Renderer<ClassCategory,String>() {

            @Override
            public void renderParent(View view, ClassCategory classCategory, boolean isExpanded, int parentPosition) {
                ((TextView)view.findViewById(R.id.tv_parent_name)).setText(ClassCategory.name);
                view.findViewById(R.id.arrow).setBackgroundResource(isExpanded?R.drawable.ic_arrow_up: R.drawable.ic_arrow_down);
            }

            @Override
            public void renderChild(View view, String string, int parentPosition, int childPosition) {
                ((TextView)view.findViewById(R.id.tv_child_name)).setText(string);

            }


        });

        // Display current classes (classes with an empty grade)
        DatabaseAccess dba = new DatabaseAccess();
        dba.setGetClassListener(new GetClassesListener() {
            @Override
            public void getClasses(List<Course> courses) {
                for(Course currentCourse : courses) {
                    if(currentCourse.getGrade().equals("")) {
                        Section<ClassCategory, String> section = new Section();
                        ClassCategory classCategory = new ClassCategory(currentCourse.getName());
                        section.parent = classCategory;
                        section.children.add("Course name: " + currentCourse.getName());
                        section.children.add("# Credits: " + (String) Integer.toString(currentCourse.getCredits()));
                        section.children.add("Current Grade: " + currentCourse.getGrade());
                        layout.addSection(section);
                    }
                }

            }
        });

        // Display username
        String currentUser = MainActivity.getCurrentUser();
        TextView userName = (TextView)view.findViewById(R.id.usernameTextView);
        userName.setText(MainActivity.getCurrentUser());

        // Using dummy user "User1" to act as the current user - create this user in the database
        DatabaseAccess db = new DatabaseAccess();
        db.createUser(currentUser, "user1@gmail.com", "password1234");

        return view;
    }

    /* Handles button clicks in this Fragment */
    public void onClick(View view) {
        switch(view.getId()) {

            // Add Class button pressed - show popup window form input to get new class info
            case R.id.addClassButton:
                createAddClassPopup();
                break;
        }
    }

    /* Method that displays Add User popup window and all the logic that comes with it */
    public void createAddClassPopup() {
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

        final EditText checkClassEditText = (EditText)popupView.findViewById(R.id.classNameEditText);
        final EditText checkCreditsEditText = (EditText)popupView.findViewById(R.id.classCreditEditText);
        final EditText gradeEditText = (EditText)popupView.findViewById(R.id.gradeReceivedEditText);
        final CheckBox currentClassCheckBox = (CheckBox) popupView.findViewById(R.id.currentClassCheckBox);
        final Button addClassButton = (Button) popupView.findViewById(R.id.addClassSubmitButton);
        gradeEditText.setText("");
        gradeEditText.setEnabled(true);
        addClassButton.setEnabled(false);

        // Make sure both text boxes are filled in before unlocking add Add Class Button in the input form
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
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().trim().length() == 0 || checkClassEditText.getText().toString().trim().length() == 0)
                    addClassButton.setEnabled(false);
                else
                    addClassButton.setEnabled(true);
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });
        currentClassCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton checkBox, boolean isChecked) {
                if(checkBox.isChecked()) {
                    gradeEditText.setText("");
                    gradeEditText.setEnabled(false);
                }
                else
                    gradeEditText.setEnabled(true);
            }
        });

        addClassButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final DatabaseAccess dba = new DatabaseAccess();
                EditText classNameEditText = (EditText) popupView.findViewById(R.id.classNameEditText);
                EditText classCreditsEditText = (EditText) popupView.findViewById(R.id.classCreditEditText);
                final String className = classNameEditText.getText().toString();
                final int classCredits = Integer.parseInt((String) classCreditsEditText.getText().toString());
                String gradeReceived = gradeEditText.getText().toString();
                final Course newCourse = new Course(className, classCredits, gradeReceived);
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
                        // Class doesn't exist - add it to database
                        if(!exists) {
                            dba.createClass(newCourse);
                            Toast.makeText(getActivity(), className + " added", Toast.LENGTH_LONG).show();
                            refreshFragment();
                        }
                        // Class exists, ask user if they'd like to replace one in database with the one they just made
                        else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle("A class with this name already exists - replace it?");
                            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dba.createClass(newCourse);
                                    //Toast.makeText(getActivity(), className + " replaced", Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                    refreshFragment();
                                }
                            });
                            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Toast.makeText(getActivity(), className + " not replaced", Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                    refreshFragment();
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                        popupWindow.dismiss();
                    }
                });
            }});
    }

    /* Refreshes the Fragment to reflect current Class changes/adds */
    public void refreshFragment() {
        getActivity().getFragmentManager().beginTransaction().replace(R.id.contentFrame, new HomeScreenFragment()).commit();
    }



    /* Dummy method here because Android Studio doesn't recognize that getClasses()
     * in the Listener implements the interface. This method doesn't do anything */
    public void getClasses(List<Course> courses) { }

}
