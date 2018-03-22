package cs1635.gradebuddy.database;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import cs1635.gradebuddy.activities.MainActivity;
import cs1635.gradebuddy.models.Course;

/* Class that handles interaction with the database. It also keeps track of the current user */
public class DatabaseAccess extends AppCompatActivity {
    private FirebaseDatabase database;
    private String currentUser;
    private GetClassesListener mListener;

    public DatabaseAccess() {
        currentUser = MainActivity.getCurrentUser();
        database = FirebaseDatabase.getInstance();

        // This Listener is constantly feeding back the current user's classes from database and fires the
        // GetClassListener that passes a List<Course> to which ever fragment/activity implements it.
        // Example of getting a List<Course> is in the onCreate() method in HistoryFragment.
        DatabaseReference coursesRef = database.getReference("users").child(currentUser).child("classes");
        coursesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Course> courses = new ArrayList<>();
                for(DataSnapshot child : dataSnapshot.getChildren()) {
                    Course course = child.getValue(Course.class);
                    courses.add(course);
                }
                if(mListener != null)
                    mListener.getClasses(courses);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    /* Creates a user in the database with username, email, and password fields */
    public void createUser(String username, String email, String password) {
        database.getReference("users").child(username).child("email").setValue(email);
        database.getReference("users").child(username).child("password").setValue(password);
    }

    /* Creates a course in the database for the current user */
    public void createClass(Course newCourse) {
        database.getReference("users").child(currentUser).child("classes").child(newCourse.getName()).setValue(newCourse);
    }

    public void updateClass(Course oldCourse, Course newCourse) {
        database.getReference("users").child(currentUser).child("classes").child(oldCourse.getName()).setValue(newCourse);
    }

    public void deleteClass(Course courseToBeDeleted) {
        database.getReference("users").child(currentUser).child("classes").child(courseToBeDeleted.getName()).removeValue();
    }


    /* Listener to be used in fragments/activities that implement this class - example in HistoryFragment */
    public void setGetClassListener(GetClassesListener eventListener) {
        this.mListener = eventListener;
    }

}
