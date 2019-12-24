package com.Vedma.Roleplay.Vedma_Alpha.ADAPTER;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.Vedma.Roleplay.Vedma_Alpha.POJO.API.PropertyItem;
import com.Vedma.Roleplay.Vedma_Alpha.R;

import java.util.ArrayList;


public class CharacterPage extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String Title;
    private ArrayList<PropertyItem> properties;


    ListView Content;
    public CharacterPage() {
        // Required empty public constructor
    }



    public static CharacterPage newInstance(String Title, ArrayList<PropertyItem> properties) {
        CharacterPage fragment = new CharacterPage();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, Title);
        args.putSerializable(ARG_PARAM2, properties);
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressWarnings("MoveFieldAssignmentToInitializer")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Title = getArguments().getString(ARG_PARAM1);
            //noinspection MoveFieldAssignmentToInitializer,MoveFieldAssignmentToInitializer,MoveFieldAssignmentToInitializer,MoveFieldAssignmentToInitializer
            properties = (ArrayList<PropertyItem>) getArguments().getSerializable(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_character_page, container, false);
        Content = v.findViewById(R.id.playerAttr);
        Content.setAdapter(new PropertiesAdapter(getContext(),properties,false));
        return v;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

}
