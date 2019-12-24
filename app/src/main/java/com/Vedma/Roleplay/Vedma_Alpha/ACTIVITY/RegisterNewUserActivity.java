package com.Vedma.Roleplay.Vedma_Alpha.ACTIVITY;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.Vedma.Roleplay.Vedma_Alpha.POJO.API.AccountInfo;
import com.Vedma.Roleplay.Vedma_Alpha.R;
import com.Vedma.Roleplay.Vedma_Alpha.SERVICE.VedmaExecutor;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterNewUserActivity extends AppCompatActivity {
    EditText Name;
    EditText Phone;
    EditText Email;
    Switch EmailSignal;
    EditText Password;
    EditText PasswordConfirm;
    Button button;
    private static final Pattern rfc2822 = Pattern.compile(
            "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$"
    );
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_new_user);
        Name = findViewById(R.id.Name);
        Phone = findViewById(R.id.Phone);
        Email = findViewById(R.id.Email);
        EmailSignal = findViewById(R.id.emailSwitch);
        Password = findViewById(R.id.Password);
        PasswordConfirm = findViewById(R.id.Password_confirm);
        button = findViewById(R.id.ok);
    }

    public void RegisterClick(View view) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber myPhone;
        try {
            myPhone = phoneUtil.parse(Phone.getText().toString(), "RU");
        } catch (NumberParseException e) {
            System.err.println("NumberParseException was thrown: " + e.toString());
            Toast.makeText(this, "Неверный номер телефона", Toast.LENGTH_LONG).show();
            return;
        }
        if (Name==null)
            return;
        Matcher m = rfc2822.matcher(Email.getText().toString());
        if (Name.getText().toString().equals("")){
            Toast.makeText(this, "Введите имя", Toast.LENGTH_LONG).show();
        } else if (!m.matches()){
            Toast.makeText(this, "Неверный адрес электронной почты", Toast.LENGTH_LONG).show();
        } else if (!phoneUtil.isValidNumber(myPhone)) {
            Toast.makeText(this, "Неверный номер телефона", Toast.LENGTH_LONG).show();
        }else if (!Password.getText().toString().matches("(?=.*[A-Za-z])(?=.*[a-z])(?=.*\\d)[A-Za-z\\d]{6,}")){//todo check
                Toast.makeText(this, "Пароль должен быть  не менее 6 символов, на латиннице, содержать цифру и заглавную букву", Toast.LENGTH_LONG).show();
        } else if (!Password.getText().toString().equals(PasswordConfirm.getText().toString())){
            Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_LONG).show();//todo password regex
        } else {
            AccountInfo accountInfo = new AccountInfo();
            accountInfo.Name = Name.getText().toString();
            accountInfo.Phone = phoneUtil.format(myPhone, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
            accountInfo.Email = Email.getText().toString();
            accountInfo.Password = Password.getText().toString();
            accountInfo.EmailSignal = EmailSignal.isChecked();
            button.setActivated(false);
            VedmaExecutor.getInstance(this).getJSONApi().Register(accountInfo).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (button!=null)
                        button.setActivated(true);
                    if (response.code()==200){
                        Toast.makeText(RegisterNewUserActivity.this, "Профиль успешно зарегистрирован", Toast.LENGTH_LONG).show();
                       finish();
                    } else {
                        Toast.makeText(RegisterNewUserActivity.this, "Попробуйте позже", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    if (button!=null)
                        button.setActivated(true);
                    Log.e("Vedma.error", t.getMessage());
                }
            });
        }
    }
}
