package cs1635.gradebuddy.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import cs1635.gradebuddy.R;
import cs1635.gradebuddy.activities.MainActivity;

public class AddClassFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_class, container, false);

        final Button button = (Button) view.findViewById(R.id.addClassBackButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {
                    ((MainActivity) getActivity()).setNavbarLocked(false);
                    fm.popBackStack();
                }
            }});

        return view;
    }
}
