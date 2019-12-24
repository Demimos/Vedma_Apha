package com.Vedma.Roleplay.Vedma_Alpha.ADAPTER;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.Vedma.Roleplay.Vedma_Alpha.POJO.API.GeoPosition;
import com.Vedma.Roleplay.Vedma_Alpha.R;

import java.util.ArrayList;
import java.util.List;

public class GeoObjectFragment extends DialogFragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String GEO = "geo";
    private static final String SCREEN_SIZE = "ScreenSize";
    private Context context;
    private GeoPosition geoPosition;
    private OnFragmentInteractionListener mListener;
    private ListView properties;
    private ListView abilities;
    private TextView label;
    private View v;
    private int GeoId;
    private AdapterView.OnItemClickListener listener;
    public GeoObjectFragment() {
        // Required empty public constructor
    }


    public static GeoObjectFragment newInstance(GeoPosition geo) {
        GeoObjectFragment fragment = new GeoObjectFragment();
        Bundle args = new Bundle();
        args.putSerializable(GEO, geo);
        ArrayList<Integer> l = new ArrayList<>();
        args.putIntegerArrayList(SCREEN_SIZE,l);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Inflate the layout for this fragment
        v =  requireActivity().getLayoutInflater().inflate(R.layout.fragment_geo_object, null);
        properties=v.findViewById(R.id.properties);
        abilities=v.findViewById(R.id.abilities);
        label = v.findViewById(R.id.label);
        UpdateView();
        builder.setView(v);
        return builder.create();
    }

    private void UpdateView() {

        properties.setAdapter(new PropertiesAdapter(context,geoPosition.getProperties(),true));
        Log.d ("Vedma.tick","updated");
        abilities.setAdapter(new AbilitiesAdapter(context, geoPosition.getOnClick(),true));
        if (geoPosition.getOnClick().size()==0){
            label.setVisibility(View.GONE);
            abilities.setVisibility(View.GONE);
        } else {
            abilities.setOnItemClickListener(listener);
        }
    }
    public void Update(GeoPosition geo){
        geoPosition=geo;
        UpdateView();
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener){
        this.listener = listener;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            geoPosition = (GeoPosition)getArguments().getSerializable(GEO);
            GeoId=geoPosition.getId();
        }
    }

    public int getGeoId() {
        return GeoId;
    }

    public OnFragmentInteractionListener getmListener() {
        return mListener;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
        this.context=context;
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction();
    }
}
