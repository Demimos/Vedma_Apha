package com.Vedma.Roleplay.Vedma_Alpha.ACTIVITY;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.Vedma.Roleplay.Vedma_Alpha.ADAPTER.EntityAdapter;
import com.Vedma.Roleplay.Vedma_Alpha.POJO.API.Ability;
import com.Vedma.Roleplay.Vedma_Alpha.POJO.API.ActionAdapter;
import com.Vedma.Roleplay.Vedma_Alpha.POJO.API.DataItem;
import com.Vedma.Roleplay.Vedma_Alpha.POJO.API.GameCharacter;
import com.Vedma.Roleplay.Vedma_Alpha.POJO.API.GeoPosition;
import com.Vedma.Roleplay.Vedma_Alpha.POJO.API.Invoker;
import com.Vedma.Roleplay.Vedma_Alpha.POJO.IEntity;
import com.Vedma.Roleplay.Vedma_Alpha.POJO.MyIntentType;
import com.Vedma.Roleplay.Vedma_Alpha.POJO.ObjectType;
import com.Vedma.Roleplay.Vedma_Alpha.R;
import com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AsyncService;
import com.Vedma.Roleplay.Vedma_Alpha.SERVICE.VedmaExecutor;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.Vedma.Roleplay.Vedma_Alpha.ACTIVITY.Abilities.ACTION_ABILITY;
import static com.Vedma.Roleplay.Vedma_Alpha.ACTIVITY.Abilities.ACTION_ADAPTER;
import static com.Vedma.Roleplay.Vedma_Alpha.ACTIVITY.MapsActivity.MAP_METHOD;
import static com.Vedma.Roleplay.Vedma_Alpha.ACTIVITY.MapsActivity.POSITION;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AsyncService.LogOff;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AsyncService.getCharId;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AsyncService.sendNotification;

public class ChooserActivity extends AppCompatActivity {
    private List<Invoker> Invokers;
    Ability ability;
    private List<ActionAdapter> Adapters;
    private RecyclerView recyclerView;
    private int iCurrent=0;
    private GeoPosition.MapMethod method;
    private int geoPosition;
    private boolean needSource = true;
    private boolean needTarget = true;
    private boolean needExtra = true;
    private Invoker invoker;
    private ActionAdapter adapter;
    private List<GameCharacter> characterList;
    private List<GeoPosition> geoPositionList;
    private List<DataItem> dataItemList;
    Call<Integer> tagCall;
    TextView Label;
    Context context;
    private String charId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chooser);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Intent intent = getIntent();
        if (intent.hasExtra(ACTION_ADAPTER)&&intent.hasExtra(ACTION_ABILITY))
            {
            //noinspection unchecked
            Adapters = (List<ActionAdapter>) intent.getSerializableExtra(ACTION_ADAPTER);
            ability = (Ability)intent.getSerializableExtra(ACTION_ABILITY);
            if (ability.getPresetId()==0&&intent.hasExtra(POSITION)) {
                geoPosition = intent.getIntExtra(POSITION, 0);
                method=(GeoPosition.MapMethod)intent.getSerializableExtra(MAP_METHOD);
            }
        }else {
            finish();
        }
        if (Adapters.size()==0) {
            AllDone();
        }
        adapter = Adapters.get(iCurrent);
        characterList=adapter.getCharacters();
        geoPositionList=adapter.getGeoObjects();
        dataItemList=adapter.getParameters();
        charId = getCharId(this);
        invoker = new Invoker();
        Invokers= new ArrayList<>();
        context=this;
        Label = findViewById(R.id.Label);
        recyclerView = findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        SolveNext();
    }

    private void BindAdapter() {
        adapter = Adapters.get(iCurrent);
        characterList.addAll(adapter.getCharacters());
        geoPositionList.addAll(adapter.getGeoPositions());
        dataItemList=adapter.getParameters();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tagCall!=null)
        {
            tagCall.cancel();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chooser_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.clear:
                AlertDialog cancel = (new AlertDialog.Builder(this)).setCancelable(true)
                        .setMessage("Вы уверены, что хотите прекратить использование способности?")
                        .setTitle("Выход")
                        .setPositiveButton("Прекратить и выйти", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
                cancel.show();
                break;
            case R.id.news:
                final AlertDialog next = (new AlertDialog.Builder(this)).setCancelable(true)
                        .setMessage("Всё готово?")
                        .setTitle("Продолжить")
                        .setPositiveButton("Всё верно", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                BindRecycler();
                            }
                        })
                        .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
                next.show();
                break;
        }
        return true;
    }

    private void BindRecycler() {
        List<IEntity> chosen = ((EntityAdapter)recyclerView.getAdapter()).getChosen();
        Log.d("Vedma.Chooser",chosen.size()+" items chosen");
        for (IEntity item:chosen) {
            if (needSource){
                invoker.Sources.add(item.getReflectedId());
            }else if(needTarget){
                invoker.Targets.add(item.getReflectedId());
            }else if (needExtra){
                //noinspection ConstantConditions/*компилятор, ты еблан?*/
                if (needExtra&&adapter.getIntentType()==MyIntentType.share) {
                    invoker.Shares.add(item.getReflectionId());
                }
            }
        }
        if (needSource){
            needSource=false;
        }else if(needTarget){
            needTarget=false;
        }else if (needExtra){
            needExtra=false;
        }
        SolveNext();
//TODO manage animation
        //TODO manage buttonBlock

    }

    private void SolveNext() {
        if (needSource) {
            if (adapter.isNeedSource()){
                ManageSource();
            } else {
                needSource=adapter.isNeedSource();
                SolveNext();
            }
        } else if (needTarget){
            if (adapter.isNeedTarget()){
                ManageTarget();
            } else {
                needTarget=adapter.isNeedTarget();
                SolveNext();
            }
        } else if (needExtra) {
            switch (adapter.getIntentType())
            {
                case share:
                    Label.setText(adapter.getShareDescription());
                   SetChooser();
                    break;
                case change:
                    if (dataItemList.size()>0)
                    {
                        AlertDialog dialog= getDataDialog(dataItemList.get(0));
                        dialog.show();
                    } else {
                        needExtra=false;
                        SolveNext();
                    }
                    break;
                case insight:
                   needExtra=false;
                    SolveNext();
                    break;//why not, lol?
                default: throw new EnumConstantNotPresentException(adapter.getIntentType().getClass(),
                        adapter.getIntentType().toString());
            }
        } else {
            iCurrent++;
            needTarget=true;
            needSource=true;
            needExtra=true;
            invoker.Id=adapter.getId();
            Invokers.add(invoker);
            if (iCurrent >= Adapters.size()) {
                AllDone();
                return;
            } else {
                BindAdapter();
            }
            invoker = new Invoker();
        }

    }
    public AlertDialog getDataDialog(final DataItem dataItem) {
        View v = getLayoutInflater().inflate(R.layout.data_item_alert_layout,null);
        TextView tv = v.findViewById(R.id.label);
        final EditText et = v.findViewById(R.id.dataitem);
        tv.setText(dataItem.Description);
        switch (dataItem.getDataType())
        {
            case Text:
                et.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            case TextArray:
                et.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            case Number:
                et.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            case Identity:
                et.setInputType(InputType.TYPE_CLASS_NUMBER);//Maybe Chooser
                break;
            default: finish();
        }
        return (new AlertDialog.Builder(this)).setView(v)
                .setTitle("Введите параметр")
                .setPositiveButton("Готово", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String result = et.getText().toString();
                        if (!result.equals("")){

                            switch (dataItem.getDataType())
                            {
                                case Text:
                                   dataItem.StringValue=result;
                                    break;
                                case TextArray:
                                    dataItem.StringArrayValue=new ArrayList<>();
                                    dataItem.StringArrayValue.add(result);    //TODO that's just lame
                                    break;
                                case Number:
                                    if (result.matches("\\d+"))
                                    {
                                    try {
                                        dataItem.NumericValue=Integer.parseInt(result);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        BadInt();
                                    }}
                                    else
                                        BadInt();
                                    break;
                                case Identity:
                                    if (result.matches("\\d+"))//TODO that's lame too
                                    {
                                        try {
                                            dataItem.NumericValue=Integer.parseInt(result);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            BadInt();
                                        }}
                                    BadInt();
                                    break;
                                    default: finish();
                            }
                            invoker.Parameters.add(dataItem);
                            dataItemList.remove(dataItem);
                            SolveNext();
                        }else {
                            Toast.makeText(context, "Значение не верно", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                })
                .setNegativeButton("Отменить и выйти", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setCancelable(false)
                .create();
    }

    private void BadInt() {
        Toast.makeText(context, "Неправильный ввод", Toast.LENGTH_SHORT).show();
    }

    private void ManageTarget() {
        Label.setText(adapter.getTargetDescription());
        if (adapter.isTargetByTag()) {
            AlertDialog dialog= getTagDialogBuilder(adapter.getTargetDescription())
                    .create();
            dialog.show();
        }else {
            SetChooser();
        }
    }

    private void ManageSource() {
        Label.setText(adapter.getSourceDescription());
        if (adapter.isSourceByTag()) {
            AlertDialog dialog= getTagDialogBuilder(adapter.getSourceDescription())
                    .setMessage(adapter.getSourceDescription())
                    .create();
            dialog.show();
        }else {
            SetChooser();
        }
    }

    private AlertDialog.Builder getTagDialogBuilder(String description) {
        View v = getLayoutInflater().inflate(R.layout.data_item_alert_layout,null);
        TextView tv = v.findViewById(R.id.label);
        final EditText et = v.findViewById(R.id.dataitem);
        if (description!=null&&description.length()>0)
            tv.setText(description);
        else tv.setVisibility(View.GONE);
        et.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        return (new AlertDialog.Builder(this))
                .setView(v)
                .setPositiveButton("Готово", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String text = et.getText().toString();
                        if (text.length()!=4){
                            Toast.makeText(ChooserActivity.this, "Неверный код "+text, Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        SetByTag(text);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Отменить и выйти", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setTitle("Введите метку")
                .setCancelable(false);
    }

    private void AllDone() {
        if (ability.getPresetId()!=0) {
            VedmaExecutor.getInstance(this).getJSONApi()
                    .invokeAbility(charId,
                            ability.getID(),
                            ability.getPresetId(),
                            ability.getChainId(),
                            Invokers).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.code() == 200) {
                        if (!response.body().equals(""))
                            Toast.makeText(context, response.body(), Toast.LENGTH_LONG).show();
                        finish();
                    }
                    finish();
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e("Vedma.Error", t.getMessage());
                    Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        } else if (method==GeoPosition.MapMethod.onClick) {
            VedmaExecutor.getInstance(this).getJSONApi().invokeMapClick(charId,geoPosition,
                    ability.getId(),ability.getChainId(),Invokers).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.code() == 200) {
                        if (!response.body().equals(""))
                            Toast.makeText(context, response.body(), Toast.LENGTH_LONG).show();
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e("Vedma.Error", t.getMessage());
                }
            });
        } else if(method==GeoPosition.MapMethod.onInterract){
            VedmaExecutor.getInstance(this).getJSONApi().invokeMapInteract(charId,geoPosition,
                    ability.getId(),ability.getChainId(),Invokers).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.code() == 200) {
                        Toast.makeText(context, response.body(), Toast.LENGTH_LONG).show();
                    }
                    finish();
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e("Vedma.Error", t.getMessage());
                }
            });
        }
    }


    private void SetByTag(String text) {
        Toast.makeText(context, "Подождите", Toast.LENGTH_SHORT).show();

        if (tagCall!=null)
            tagCall.cancel();
        tagCall = VedmaExecutor.getInstance(this).getJSONApi().getEntityByTag(charId, text);
        tagCall.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.code()==200){
                    if (needSource){
                        invoker.Sources.add(response.body());
                        needSource=false;
                        SolveNext();
                    } else {
                        invoker.Targets.add(response.body());
                        needTarget=false;
                        SolveNext();
                    }
                }
                else {
                    Toast.makeText(context,"Не корректная метка", Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.e("Vedma.Error", t.getMessage());
            }
        });
    }

    private void SetChooser() {
        EntityAdapter entityAdapter=null;
        recyclerView.setHasFixedSize(true);
        if (needSource){
            entityAdapter =new EntityAdapter(this,(List<IEntity>) (List<? extends IEntity>)characterList,true,adapter.isMultiSource());
        } else if (needTarget){
            if (adapter.getTarget()== ObjectType.character)
                entityAdapter = new EntityAdapter(this, (List<IEntity>) (List<? extends IEntity>)characterList,true,adapter.isMultiTarget());
            else
                entityAdapter = new EntityAdapter(this,(List<IEntity>) (List<? extends IEntity>)geoPositionList,true,adapter.isMultiTarget());
        } else if (needExtra && adapter.getIntentType()== MyIntentType.share){
            Label.setText(adapter.getShareDescription());
            if (adapter.getShares()==ObjectType.character)
                entityAdapter = new EntityAdapter(this, (List<IEntity>) (List<? extends IEntity>)characterList,true,true);
            else {
                entityAdapter = new EntityAdapter(this, (List<IEntity>) (List<? extends IEntity>)geoPositionList,true,true);
            }
        }
        if (entityAdapter!=null)
            recyclerView.setAdapter(entityAdapter);
        else {
            Toast.makeText(context, "Ошибка, попробуйте позже", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
