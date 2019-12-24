package com.Vedma.Roleplay.Vedma_Alpha.ADAPTER;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.Vedma.Roleplay.Vedma_Alpha.ACTIVITY.MainActivity;
import com.Vedma.Roleplay.Vedma_Alpha.POJO.API.GameCharacter;
import com.Vedma.Roleplay.Vedma_Alpha.POJO.API.Game;
import com.Vedma.Roleplay.Vedma_Alpha.R;
import com.Vedma.Roleplay.Vedma_Alpha.SERVICE.VedmaExecutor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.Vedma.Roleplay.Vedma_Alpha.ACTIVITY.MainActivity.GAME_NAME;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AsyncService.setCharId;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AsyncService.setGameId;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.MyFirebaseMessagingService.sendRegistrationToServer;

public class GamesAdapter  extends RecyclerView.Adapter<GamesAdapter.GamesViewHolder> {
    private List<Game> games;
    private Context context;
    public GamesAdapter(@NonNull Context context, List<Game> games) {

        this.games = games;
        this.context =context;
    }


    @NonNull
    @Override
    public GamesAdapter.GamesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.game_row, parent, false);
        TextView textView = v.findViewById(R.id.GameName);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Game game = games.get((int)v.getTag());
                final String topic =game.getId() ;
                FirebaseMessaging.getInstance().subscribeToTopic(topic)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (!task.isSuccessful()) {
                                  Log.e("Vedma.error", "subscribe failed to topic "+topic);//TODO schedule a job
                                }
                             //TODO LOG errors
                            }
                        });
                List<GameCharacter> characters = getCharacters((int)v.getTag());
                setCharId(context,String.valueOf(characters.get(0).Id));//todo INFLATE DIALOG
                setGameId(context,game);
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
            }
        });
        return new GamesAdapter.GamesViewHolder(v, textView);
    }


    @Override
    public void onBindViewHolder(@NonNull GamesAdapter.GamesViewHolder holder, int position) {
        int p =holder.getAdapterPosition();
        holder.gameTitle.setText(games.get(p).getName());
        holder.v.setTag(p);
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    class GamesViewHolder extends RecyclerView.ViewHolder
    {
        TextView gameTitle;
        View v;
        GamesViewHolder(View v, TextView gameTitle) {
            super(v);
            this.v = v;
            this.gameTitle=gameTitle;
        }
    }
    private List<GameCharacter> getCharacters(int pos)
    {
        return games.get(pos).getCharacters();
    }
}
