package com.example.user.treepository;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class TourFragment extends Fragment implements AdapterView.OnItemClickListener {
    View view;
    ListView lvTour;
    private ArrayList<String> Tours = new ArrayList<String>();
    ArrayAdapter<String> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_tour, container, false);
        lvTour = (ListView) view.findViewById(R.id.TourList);

        //Add a new text string for each additional tour to be added, handle their click events in the
        //onItemClick method below. Position is the order in which they are added starting at 0.
        Tours.add("Wales Garden Tree Tour \n    Start: Maxcy Gregg Park \n    2.0 miles \n    40-60 minutes"); //Position 0
        Tours.add("Belser Arboretum Tour \n    Start: 4080 Bloomwood Rd. \n    0.6 miles \n    20-30 minutes"); //Position 1
        Tours.add("Columbia Riverfront Park Tour \n    Start: Riverfront Park \n    1.5 miles \n    35-45 minutes"); //Position 2
        Tours.add("Shandon Tour \n    Start: 614 Holly St. \n    2.0 miles \n    45-60 minutes"); // Position 3

        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, Tours);
        lvTour.setOnItemClickListener(this);

        lvTour.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        return view;

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            //To create a tour to be passed to Google Maps, make the starting point coordinates the daddr and
            //add each subsequent stop as a waypoint following +to:
            Uri gmmIntentUri = Uri.parse("https://www.google.com/maps?daddr=33.996450, -81.020219+to:33.995745, -81.016282+to:33.994034, -81.016282+to:" +
                    "33.993091, -81.015477+to:33.990103, -81.014919+to:33.993951, -81.01738&dirflg=w");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");

            //Check that Google Maps is installed on the phone, handle the case where it isn't as well.
            if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null)
                startActivity(mapIntent);
            else {
                Toast.makeText(getActivity(), "Please install Google Maps to use this feature", Toast.LENGTH_SHORT).show();
            }
        } else if (position == 1) {
            //To create a tour to be passed to Google Maps, make the starting point coordinates the daddr and
            //add each subsequent stop as a waypoint following +to:
            Uri gmmIntentUri = Uri.parse("https://www.google.com/maps?daddr=33.993322, -80.984346+to:33.993189, -80.984475+to:33.992504, -80.984373+to:" +
                    "33.992802, -80.985312+to:33.992784, -80.985553+to:33.993327, -80.985435+to:33.993367, -80.985338+to:33.993862, -80.984534&dirflg=w");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");

            //Check that Google Maps is installed on the phone, handle the case where it isn't as well.
            if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null)
                startActivity(mapIntent);
            else {
                Toast.makeText(getActivity(), "Please install Google Maps to use this feature", Toast.LENGTH_SHORT).show();
            }
        } else if (position == 2){
            //To create a tour to be passed to Google Maps, make the starting point coordinates the daddr and
            //add each subsequent stop as a waypoint following +to:
            Uri gmmIntentUri = Uri.parse("https://www.google.com/maps?daddr=34.003167, -81.051981+to:34.002909, -81.054444+to:34.004586, -81.056965+to:" +
                    "34.005404, -81.057523+to:34.005782, -81.057727+to:34.008495, -81.058419+to:34.009820, -81.058129&dirflg=w");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");

            //Check that Google Maps is installed on the phone, handle the case where it isn't as well.
            if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null)
                startActivity(mapIntent);
            else {
                Toast.makeText(getActivity(), "Please install Google Maps to use this feature", Toast.LENGTH_SHORT).show();
            }
        } else if (position == 3){
            //To create a tour to be passed to Google Maps, make the starting point coordinates the daddr and
            //add each subsequent stop as a waypoint following +to:
            Uri gmmIntentUri = Uri.parse("https://www.google.com/maps?daddr=33.997306, -81.001158+to:33.995305, -81.002410+to:33.994206, -81.003829+to:" +
                    "33.991119, -81.008239+to:33.994659, -81.009891+to:33.997870, -81.005739&dirflg=w");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");

            //Check that Google Maps is installed on the phone, handle the case where it isn't as well.
            if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null)
                startActivity(mapIntent);
            else {
                Toast.makeText(getActivity(), "Please install Google Maps to use this feature", Toast.LENGTH_SHORT).show();
            }
        }
    }
}