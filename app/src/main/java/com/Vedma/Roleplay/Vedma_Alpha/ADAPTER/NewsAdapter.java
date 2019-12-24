package com.Vedma.Roleplay.Vedma_Alpha.ADAPTER;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.Vedma.Roleplay.Vedma_Alpha.ACTIVITY.News_one;
import com.Vedma.Roleplay.Vedma_Alpha.POJO.API.NewsCapture;
import com.Vedma.Roleplay.Vedma_Alpha.R;
import com.Vedma.Roleplay.Vedma_Alpha.SERVICE.DownloadImageTask;

import java.util.ArrayList;
import java.util.List;

import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AsyncService.LogError;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    private List<NewsCapture> NewsList;
    private Context context;
    private SparseArray<DownloadImageTask> tasks;
    public NewsAdapter(@NonNull Context context, List<NewsCapture> news) {

            this.NewsList = news;
            this.context =context;
            tasks = new SparseArray<>();
    }


    @NonNull
    @Override
    public NewsAdapter.NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        try{
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.news_row, parent, false);
        TextView title = v.findViewById(R.id.newsTitle);
        TextView preview = v.findViewById(R.id.NewsPreview);
        ImageView img = v.findViewById(R.id.news_IMG);
        TextView time = v.findViewById(R.id.time);
        TextView publisher = v.findViewById(R.id.Publisher);
        ProgressBar progressBar = v.findViewById(R.id.progress);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Intent intent = new Intent(context, News_one.class);
                int num=0;
                NewsCapture article = NewsList.get((int)v.getTag());
                num=article.getId();
                intent.putExtra("newsNum",num);
                  context.startActivity(intent);
            }
        });

        return new NewsAdapter.NewsViewHolder(v, title, preview, img, time, publisher,progressBar);
        }
        catch (Exception e){
            LogError(context, e);
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        try{

        final int p = holder.getAdapterPosition();
        holder.Title.setText(NewsList.get(p).getTitle());
        holder.Preview.setText(NewsList.get(p).getPreview());
        if (tasks.get(position)!=null) {
            tasks.get(position).cancel(true);
            tasks.remove(position);
        }
            holder.progressBar.setVisibility(View.GONE);
        holder.imageView.setVisibility(View.INVISIBLE);
        if (NewsList.get(p).getIMG()!=null&&!NewsList.get(p).getIMG().equals("")) {
            holder.progressBar.setVisibility(View.VISIBLE);
         tasks.put(position, new DownloadImageTask((ImageView) holder.v.findViewById(R.id.news_IMG), holder.progressBar));
                 tasks.get(position).execute(NewsList.get(p).getIMG());
            }
        else
            holder.imageView.setVisibility(View.INVISIBLE);
        holder.v.setTag(p);
        holder.Time.setText(NewsCapture.GetTime(NewsList.get(p).getDateTime()));
        holder.Publisher.setText(NewsList.get(p).getPublisherName());
        }
        catch (Exception e){
            LogError(context, e);
        }
    }

    @Override
    public int getItemCount() {
        return NewsList.size();
    }

    class NewsViewHolder extends RecyclerView.ViewHolder
    {
        TextView Title;
        View v;
        ImageView imageView;
        TextView Preview;
        TextView Time;
        TextView Publisher;
        ProgressBar progressBar;
        NewsViewHolder(View v, TextView Title, TextView Preview, ImageView imageView, TextView Time, TextView Publisher, ProgressBar progressBar) {
            super(v);
            this.v = v;
            this.Title=Title;
            this.imageView=imageView;
            this.Preview=Preview;
            this.Time=Time;
            this.Publisher=Publisher;
            this.progressBar = progressBar;
        }
    }
//        @Override
//        public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
//            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            @SuppressLint("ViewHolder") View row = Objects.requireNonNull(inflater).inflate(R.layout.news_row,parent,false);
//
//
//            ImageView imageView = row.findViewById(R.id.news_IMG);
//            imageView.setAdjustViewBounds(true);
//            if (!NewsList.get(position).getIMG().equals("false")) {
//                new DownloadImageTask((ImageView) row.findViewById(R.id.news_IMG))
//                        .execute(NewsList.get(position).getIMG());
//            }
//            else
//                imageView.setVisibility(View.GONE);
//            TextView header = row.findViewById(R.id.newsTitle);
//            header.setText(NewsList.get(position).getName());
//            row.setTag(NewsList.get(position).getNID());
//            return row;
//        }

}
