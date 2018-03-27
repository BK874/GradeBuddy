package cs1635.gradebuddy.activities;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import cs1635.gradebuddy.database.DatabaseAccess;
import cs1635.gradebuddy.database.GetClassesListener;
import cs1635.gradebuddy.fragments.CalculateGpaFragment;
import cs1635.gradebuddy.fragments.HistoryFragment;
import cs1635.gradebuddy.fragments.HomeScreenFragment;
import cs1635.gradebuddy.R;
import cs1635.gradebuddy.models.Course;

public class MainActivity extends Activity implements GetClassesListener {
    public static String currentUser = "User1";

    // Changes showing fragment based on what button is pressed in nav bar
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        // Switches which fragment is loaded on bottom nav button pressed
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if(item.getItemId() == R.id.navigation_home) {
                setFragment(new HomeScreenFragment());
                return true;
            }
            else if(item.getItemId() == R.id.navigation_calculate_gap) {
                setFragment(new CalculateGpaFragment());
                return true;
            }
            else if(item.getItemId() == R.id.navigation_history) {
                setFragment(new HistoryFragment());
                return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
        String currentUserEmail = mFirebaseUser.getEmail();
        String[] tokenized = currentUserEmail.split("@");
        DatabaseAccess dba = new DatabaseAccess();
        dba.createUser(tokenized[0], currentUserEmail);
        currentUser = tokenized[0];

        setFragment(new HomeScreenFragment());

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    /* Sets current fragment */
    protected void setFragment(android.app.Fragment fragment) {
        FragmentTransaction t = getFragmentManager().beginTransaction();
        t.replace(R.id.contentFrame, fragment);
        t.commit();
    }

    /* Static method that returns current user that any class can call with MainActivity.getCurrentUser() */
    public static String getCurrentUser() {
        return currentUser;
    }

    /* Dims the background Activity when a PopupWindow is showing */
    public void dimBehind(PopupWindow popupWindow) {
        View container;
        if (popupWindow.getBackground() == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                container = (View) popupWindow.getContentView().getParent();
            } else {
                container = popupWindow.getContentView();
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                container = (View) popupWindow.getContentView().getParent().getParent();
            } else {
                container = (View) popupWindow.getContentView().getParent();
            }
        }
        Context context = popupWindow.getContentView().getContext();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        p.dimAmount = 0.7f;
        wm.updateViewLayout(container, p);
    }

    /* Dummy method here because Android Studio doesn't recognize that getClasses()
     * in the Listener implements the interface. This method doesn't do anything */
    public void getClasses(List<Course> courses) { }

}