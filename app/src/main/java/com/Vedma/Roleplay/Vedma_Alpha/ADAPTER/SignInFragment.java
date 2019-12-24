package com.Vedma.Roleplay.Vedma_Alpha.ADAPTER;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.Vedma.Roleplay.Vedma_Alpha.POJO.API.BearerItem;
import com.Vedma.Roleplay.Vedma_Alpha.R;
import com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AsyncService;
import com.Vedma.Roleplay.Vedma_Alpha.SERVICE.MyServiceInterceptor;
import com.Vedma.Roleplay.Vedma_Alpha.SERVICE.VedmaExecutor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.Objects;
import java.util.zip.Inflater;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.Vedma.Roleplay.Vedma_Alpha.ACTIVITY.Account.AuthOk;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AsyncService.getToken;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AsyncService.sendNotification;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AsyncService.setToken;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.MyFirebaseMessagingService.sendRegistrationToServer;


public class SignInFragment extends DialogFragment implements View.OnClickListener {
    EditText user;
    EditText password;
    Button logIn;
    Context context;

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Inflate the layout for this fragment
        View v =  requireActivity().getLayoutInflater().inflate(R.layout.dialog_signin, null);
        user = v.findViewById(R.id.username);
        password = v.findViewById(R.id.password);
        logIn = v.findViewById(R.id.sign_in_button);
        logIn.setOnClickListener(this);
        builder.setCancelable(false).setView(v);
        return builder.create();
    }


    private void TryAuthorize(final Context context, String user, final String pass) {
        logIn.setActivated(false);
        VedmaExecutor.getInstance(getContext()).getJSONApi().TryAuthorize("password",user, pass).enqueue(new Callback<BearerItem>() {
            @Override
            public void onResponse(@NonNull Call<BearerItem> call, @NonNull Response<BearerItem> response) {
                Log.d("Vedma.Token", call.request()+" body: "+call.request().body()+" ");
                Log.d("Vedma.Token", response.code()+" "+ (response.body() != null ? response.body().getAccess_token() : null));
                if (response.code()==200 && response.body() != null) {
                    setToken(context, "Bearer "+response.body().getAccess_token());
                    MyServiceInterceptor.getInstance().setSessionToken(getToken(context));
                    FirebaseInstanceId.getInstance().getInstanceId()
                            .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                @Override
                                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                    if (!task.isSuccessful()) {
                                        Log.w("Vedma.token", "getInstanceId failed", task.getException());
                                        return;
                                    }
                                    final String token = Objects.requireNonNull(task.getResult()).getToken();
                                    sendRegistrationToServer(context,token);
                                }
                            });
                    context.sendBroadcast(new Intent(AuthOk));
                    dismiss();
                } else if (response.code()==401){
                        sendNotification(context,"Ошибка Авторизации",
                                "Логин или пароль не верны",
                                401, AsyncService.NotificationChannelID.Error);
                        password.setText("");
                } else if (response.code()==400){
                    sendNotification(context,"Ошибка Авторизации",
                            "Логин или пароль не верны",
                            401, AsyncService.NotificationChannelID.Error);
                    password.setText("");
                }else {
                    Toast.makeText(context,
                            "Что-то пошло не так. Проверьте соединение с интернетом или попробуйте позже",
                            Toast.LENGTH_SHORT).show();
                }
                logIn.setActivated(true);
            }

            @Override
            public void onFailure(@NonNull Call<BearerItem> call, @NonNull Throwable t) {
                t.printStackTrace();
                Log.e("Vedma.Token", String.valueOf(call.request()));
                Log.e("Vedma.Token", t.getMessage()+"");
                Toast.makeText(context,
                        "Что-то пошло не так. Проверьте соединение с интернетом.",
                        Toast.LENGTH_SHORT).show();
                logIn.setActivated(true);
            }
        });
    }

    @Override
    public void onClick(View v) {
        TryAuthorize(getContext(),user.getText().toString(), password.getText().toString());
    }
}
