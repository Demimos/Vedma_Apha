package com.Vedma.Roleplay.Vedma_Alpha.ADAPTER;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.Vedma.Roleplay.Vedma_Alpha.POJO.IEntity;
import com.Vedma.Roleplay.Vedma_Alpha.R;

import java.util.ArrayList;
import java.util.List;

public class EntityAdapter extends RecyclerView.Adapter<EntityAdapter.EntityViewHolder> {
    private Context context;
    private List<IEntity> entityItems;
    private List<IEntity> chosen;
    private List<Integer> chosenPositions;
    private boolean Choose;
    private boolean MultiChoose;

    public EntityAdapter(@NonNull Context context, List<IEntity> entities, boolean choose , boolean multiChoose) {

        entityItems=entities;
        Choose=choose;
        MultiChoose=multiChoose;
        this.context=context;
        chosen = new ArrayList<>();
        chosenPositions = new ArrayList<>();
    }
    @NonNull
    @Override
    public EntityViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.entity_row, parent, false);
        TextView Name = v.findViewById(R.id.Name);
        TextView Secondary = v.findViewById(R.id.Secondary);
        final CheckBox cb = v.findViewById(R.id.checkBox);
        final RadioButton rb = v.findViewById(R.id.radbut);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb.getVisibility()==View.VISIBLE)
                    cb.setChecked(!cb.isChecked());
                if (rb.getVisibility()==View.VISIBLE)
                    rb.setChecked(!cb.isChecked());
            }
        });
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                IEntity id = entityItems.get((int)v.getTag());
                if (isChecked){
                    chosenPositions.add((int)v.getTag());
                    chosen.add(id);
                } else {
                    if (chosen.contains(id))
                        chosen.remove(id);
                    if (chosenPositions.contains((int)v.getTag()))
                        chosenPositions.remove((Integer) v.getTag());
                }
            }
        });

        rb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                IEntity id = entityItems.get((int)v.getTag());
                if (isChecked){
                    for (Integer el:chosenPositions) {
                       View v = parent.findViewWithTag(el);
                       RadioButton r;
                       if (v!=null)
                       {
                            r= v.findViewById(R.id.radbut);
                            if (r!=null)
                                r.setChecked(false);
                       }
                       chosenPositions.clear();
                       chosen.clear();
                    }
                    chosenPositions.add((int)v.getTag());
                    chosen.add(id);
                } else {
                    if (chosen.contains(id))//умный дохуя, это же листенер
                        chosen.remove(id);
                    if (chosenPositions.contains((int)v.getTag()))
                        chosenPositions.remove((Integer)v.getTag());//ага, и ты попытаешься удалить
                                                                    // не сам элемент, а элемент
                                                                    // с его номером, да?
                }
            }
        });
        return new EntityViewHolder(v, Name, Secondary,cb,rb);
    }

    @Override
    public void onBindViewHolder(@NonNull EntityViewHolder holder, int position) {
        holder.v.setTag(holder.getAdapterPosition());
        final int p = holder.getAdapterPosition();
        if (Choose&&p<entityItems.size()){
            if (MultiChoose){
                holder.checkBox.setVisibility(View.VISIBLE);
                holder.checkBox.setChecked(chosenPositions.contains(p));
            }else {
                holder.radioButton.setVisibility(View.VISIBLE);
                holder.radioButton.setChecked(chosenPositions.contains(p));
            }
        }else if(p<entityItems.size()) {
            holder.v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(Intent.ACTION_DIAL,
                            Uri.fromParts("tel", entityItems.get(p).getSecondary(),
                                    null)));
                }
            });
        }
        holder.name.setText(entityItems.get(position).getName());
        if (!Choose)
            holder.secondary.setText(entityItems.get(position).getSecondary());
    }

    @Override
    public int getItemCount() {
        return entityItems.size();
    }

    class EntityViewHolder extends RecyclerView.ViewHolder {
        View v;
        TextView name;
        TextView secondary;
        CheckBox checkBox;
        RadioButton radioButton;
        EntityViewHolder(View v, TextView name, TextView secondary,CheckBox checkBox,RadioButton radioButton) {
            super(v);
            this.v = v;
            this.name=name;
            this.secondary=secondary;
            this.checkBox=checkBox;
            this.radioButton=radioButton;
        }
    }

    public List<IEntity> getChosen() {
        return chosen;
    }
}


