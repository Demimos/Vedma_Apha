package com.Vedma.Roleplay.Vedma_Alpha.ADAPTER;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.Vedma.Roleplay.Vedma_Alpha.POJO.API.PropertyItem;
import com.Vedma.Roleplay.Vedma_Alpha.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;

public class PropertiesAdapter extends ArrayAdapter {
        private List<PropertyItem> propertyItems;
        private Context context;
        private boolean minimize;
        public PropertiesAdapter(@NonNull Context context, List<PropertyItem> propertyItems, boolean minimize) {
            //noinspection unchecked
            super(context, R.layout.player_row,R.id.attr_name, propertyItems);
            this.propertyItems = propertyItems;
            this.context=context;
            this.minimize=minimize;

        }
        @NonNull
        @Override
        public View getView(final int position, View convertView, @NonNull ViewGroup parent)
        {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("ViewHolder") View row = Objects.requireNonNull(inflater).inflate(R.layout.player_row, parent, false);
            ProgressBar line = row.findViewById(R.id.line);
            TextView header = row.findViewById(R.id.attr_name);
            PropertyItem item =propertyItems.get(position);
            header.setText(item.getTitle());
            TextView body = row.findViewById(R.id.attr_text);
            try {
                switch (item.getType()) {
                    case Text:
                        body.setVisibility(View.VISIBLE);
                        line.setVisibility(View.GONE);
                        body.setText(propertyItems.get(position).getBody());
                        break;
                    case TextArray:
                        body.setVisibility(View.VISIBLE);
                        line.setVisibility(View.GONE);
                        String result = "";
                        if (propertyItems.get(position).getBody().equals(""))
                            result = "нет";
                        else {
                            try {
                                JSONArray jsonArray = new JSONArray(propertyItems.get(position).getBody());
                                StringBuilder resultBuilder = new StringBuilder();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    resultBuilder.append(jsonArray.getString((i)));
                                    if (jsonArray.length() - 1 > i)
                                        resultBuilder.append("\n");

                                }
                                result = resultBuilder.toString();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        body.setText(result);
                        break;
                    case Number:
                        body.setVisibility(View.VISIBLE);
                        line.setVisibility(View.GONE);
                        switch (item.getVisualType()) {
                            case 0:
                                if (item.getNumericType() == 1) {
                                    body.setText(String.valueOf((int) item.getValue()));
                                } else {
                                    body.setText(String.valueOf(item.getValue()));
                                }
                                break;
                            case 1:
                                if (item.getNumericType() == 1) {
                                    body.setText(MessageFormat.format("{0}/{1}", (int) item.getValue(), item.getUpper()));
                                } else {
                                    body.setText(MessageFormat.format("{0}/{1}", item.getValue(), item.getUpper()));
                                }
                                break;
                            case 2:
                                line.setMax(item.getUpper() - item.getLower());
                                line.setVisibility(View.VISIBLE);
                                body.setVisibility(View.GONE);
                                line.setProgress((int) item.getValue());
                                break;
                        }
                        break;
                    case Identity:
                        body.setVisibility(View.VISIBLE);
                        line.setVisibility(View.GONE);
                        body.setText(propertyItems.get(position).getBody());
                        break;
                    //TODO
                }

            } catch (Exception e){e.printStackTrace();}
            if (minimize)
            {
                header.setTextSize(15);
                row.setPadding(16,8,16,8);
                header.setPadding(16,
                        8,
                        16,
                    header.getCompoundPaddingBottom());
                body.setPadding(16,
                        body.getCompoundPaddingTop(),
                       16,
                        8);
            }
            row.setTag(propertyItems.get(position));
            return row;
        }

    }
