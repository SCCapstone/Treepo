package com.example.user.treepository;

/**
 * Created by Alex on 11/19/2016.
 */

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class ImportFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_import,container,false);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Contact Information");
    }
}
