package com.Vedma.Roleplay.Vedma_Alpha.ADAPTER;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.Vedma.Roleplay.Vedma_Alpha.POJO.IMessage;
import com.Vedma.Roleplay.Vedma_Alpha.R;

import java.util.List;

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.DiaryViewHolder> {

    private List<IMessage> messages;
    public DiaryAdapter(@NonNull Context context, List<IMessage> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public DiaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.diary_row, parent, false);
        TextView textView = v.findViewById(R.id.text);
        TextView titleView = v.findViewById(R.id.title);
        TextView tsView = v.findViewById(R.id.time);
        return new DiaryViewHolder(v, tsView, titleView, textView);
    }

    @Override
    public void onBindViewHolder(@NonNull DiaryViewHolder holder, int position) {
        IMessage item = messages.get(holder.getAdapterPosition());
        holder.text.setText(item.getBody());
        holder.title.setText(item.getTitle());
        holder.time.setText(item.getTime());


    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class DiaryViewHolder extends RecyclerView.ViewHolder
    {
        TextView time;
        public TextView title;
        public TextView text;
        View v;
        DiaryViewHolder(View v, TextView ts, TextView title, TextView text) {
            super(v);
            this.v = v;
            this.time = ts;
            this.title = title;
            this.text = text;
        }
    }
}
