package cs1635.gradebuddy.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import cs1635.gradebuddy.fragments.AddClassFragment;
import cs1635.gradebuddy.fragments.CalculateGpaFragment;
import cs1635.gradebuddy.fragments.HistoryFragment;
import cs1635.gradebuddy.fragments.HomeScreenFragment;
import cs1635.gradebuddy.R;

public class MainActivity extends FragmentActivity {

    private TextView mTextMessage;
    private boolean navbarLocked = false;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if(item.getItemId() == R.id.navigation_home && !navbarLocked) {
                setFragment(new HomeScreenFragment());
                return true;
            }
            else if(item.getItemId() == R.id.navigation_calculate_gap && !navbarLocked) {
                setFragment(new CalculateGpaFragment());
                return true;
            }
            else if(item.getItemId() == R.id.navigation_history && !navbarLocked) {
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

        setFragment(new HomeScreenFragment());

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    protected void setFragment(Fragment fragment) {
        android.support.v4.app.FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.contentFrame, fragment);
        t.commit();
    }

    public void setNavbarLocked(boolean locked) {
        navbarLocked = locked;
    }

}
