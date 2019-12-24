package com.Vedma.Roleplay.Vedma_Alpha.ADAPTER;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.Vedma.Roleplay.Vedma_Alpha.ACTIVITY.ActivityNewArticle;
import com.Vedma.Roleplay.Vedma_Alpha.ACTIVITY.News;
import com.Vedma.Roleplay.Vedma_Alpha.POJO.API.Publisher;
import com.Vedma.Roleplay.Vedma_Alpha.R;
import com.Vedma.Roleplay.Vedma_Alpha.SERVICE.DownloadImageTask;

import java.util.List;
import java.util.Objects;

import static com.Vedma.Roleplay.Vedma_Alpha.ACTIVITY.ActivityNewArticle.PUBLISHER;
import static com.Vedma.Roleplay.Vedma_Alpha.ACTIVITY.ActivityNewArticle.VIDEO_ALLOWED;

public class PublisherAdapter extends ArrayAdapter {
    private List<Publisher> publishers;
    private Context context;
    private ProgressBar progressBar;

    public PublisherAdapter(@NonNull Context context, List<Publisher> publishers) {
        super(context, R.layout.publisher_row, publishers);
        this.publishers = publishers;
        this.context=context;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("ViewHolder") View row = Objects.requireNonNull(inflater).inflate(R.layout.publisher_row, parent, false);
        final Publisher item = publishers.get(position);
        TextView titleView = row.findViewById(R.id.Title);
        titleView.setText(item.getName());
        TextView address = row.findViewById(R.id.Address);
        address.setText(String.format("Адрес: %s", item.getAddress()));
        TextView email = row.findViewById(R.id.Email);
        progressBar = row.findViewById(R.id.progressBar3);
        email.setText(String.format("Email: %s", item.getEmail()));
        TextView phone = row.findViewById(R.id.Phone);
        phone.setText(String.format("Телефон: %s", item.getPhoneNumber()));
        ImageView imageView = row.findViewById(R.id.Image);
        imageView.setAdjustViewBounds(true);
        ImageButton imageButton = row.findViewById(R.id.AddButton);
        TextView tickets = row.findViewById(R.id.Tickets);
        if (item.getTickets()>0)
        {
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ActivityNewArticle.class);
                    intent.putExtra(PUBLISHER,item.getId());
                    intent.putExtra(VIDEO_ALLOWED,item.isVideoAllowed());
                    context.startActivity(intent);
                }
            });
            tickets.setText(String.valueOf(item.getTickets()));
        }
        else
        {
            imageButton.setVisibility(View.GONE);
        }
        row.setLongClickable(true);
//        row.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                Intent intent =new Intent(context, News.class );
//                intent.putExtra("Publisher", item.getReflectionId());
//                context.startActivity(intent);
//                return false;
//            }
//        });
        if (item.getCoverImg()!=null&&!item.getCoverImg().equals("")) {
            new DownloadImageTask(imageView, progressBar)
                    .execute(item.getCoverImg());
        }
        else
            imageView.setVisibility(View.INVISIBLE);
        row.setTag(publishers.get(position).getId());
        return row;
    }

}