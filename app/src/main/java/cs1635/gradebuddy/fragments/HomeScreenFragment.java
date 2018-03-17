package cs1635.gradebuddy.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import cs1635.gradebuddy.R;
import cs1635.gradebuddy.activities.MainActivity;

public class HomeScreenFragment extends Fragment implements View.OnClickListener{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_screen, container, false);

        Button b = (Button) view.findViewById(R.id.addClassButton);
        b.setOnClickListener(this);

        return view;
    }

    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.addClassButton:
                ((MainActivity) getActivity()).setNavbarLocked(true);
                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.contentFrame, new AddClassFragment(), "NewFragmentTag");
                ft.commit();
                ft.addToBackStack(null);
                break;
        }
    }
}
