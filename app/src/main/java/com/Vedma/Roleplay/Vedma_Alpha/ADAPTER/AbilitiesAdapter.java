package com.Vedma.Roleplay.Vedma_Alpha.ADAPTER;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.Vedma.Roleplay.Vedma_Alpha.ACTIVITY.Abilities;
import com.Vedma.Roleplay.Vedma_Alpha.POJO.API.Ability;
import com.Vedma.Roleplay.Vedma_Alpha.R;

import java.util.List;
import java.util.Objects;

public class AbilitiesAdapter extends ArrayAdapter {
    private List<Ability> abilities;
    private boolean minimize;
    public AbilitiesAdapter(@NonNull Context context, List<Ability> abilities, boolean minimize) {
        super(context, R.layout.events_row,abilities);
        this.abilities = abilities;
        this. minimize = minimize;
    }
    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("ViewHolder") View row = Objects.requireNonNull(inflater).inflate(R.layout.events_row, parent, false);
        android.support.v7.widget.AppCompatTextView titleView = row.findViewById(R.id.title);
        titleView.setText(abilities.get(position).getTitle());
        if (minimize)
        {
            titleView.setTextSize(17);

            titleView.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
            titleView.setPadding(32,
                    16,
                    32,
                    16);
        }
        row.setTag(abilities.get(position));
        return row;
    }

}
