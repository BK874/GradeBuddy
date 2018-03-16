package cs1635.gradebuddy.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cs1635.gradebuddy.R;

public class CalculateGpaFragment extends Fragment {
    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState){
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calculate_gpa, container, false);
    }

}
