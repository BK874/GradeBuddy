package cs1635.gradebuddy.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import cs1635.gradebuddy.R;

/* Fragment that displays previous semester's GPAs */
public class HistoryFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_history, container, false);
        ScrollView sv = (ScrollView) view.findViewById(R.id.classesScrollView);
        TextView tv = (TextView) view.findViewById(R.id.classesTextView);
        StringBuilder classList = new StringBuilder();
        for(int i =0; i < 100; i++){
            classList.append("Class " + i + "\n");
        }
        tv.setText(classList.toString());

        // Inflate the layout for this fragment
        return view;

    }
}
