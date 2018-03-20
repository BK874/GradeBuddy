package cs1635.gradebuddy.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import cs1635.gradebuddy.R;
import cs1635.gradebuddy.activities.MainActivity;


/* Fragment that acts as the home screen - shows current courses and allows for adding of new courses */
public class HomeScreenFragment extends Fragment implements View.OnClickListener{

    /* Called when this Fragment is initialized */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_screen, container, false);

        Button b = (Button) view.findViewById(R.id.addClassButton);
        b.setOnClickListener(this);

        return view;
    }

    /* Handles button clicks */
    public void onClick(View view) {
        switch(view.getId()) {

            // Add Class button pressed - show popup window form input to get new class info
            case R.id.addClassButton:
                // Testing Firebase Create
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference myRef = database.getReference("message");
                myRef.setValue("Guy");

                View popupView = LayoutInflater.from(getActivity()).inflate(R.layout.popup_add_class, null);
                final PopupWindow popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

                Button backButton = (Button) popupView.findViewById(R.id.addClassBackButton);
                backButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        myRef.removeValue();  // Testing Firebase Delete
                        popupWindow.dismiss();
                    }});

                popupWindow.showAsDropDown(popupView, 0, 0);
                ((MainActivity) getActivity()).dimBehind(popupWindow);
                break;
        }
    }

}
