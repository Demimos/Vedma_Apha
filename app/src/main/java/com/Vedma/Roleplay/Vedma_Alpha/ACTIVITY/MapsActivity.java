package com.Vedma.Roleplay.Vedma_Alpha.ACTIVITY;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.Vedma.Roleplay.Vedma_Alpha.ADAPTER.AbilitiesAdapter;
import com.Vedma.Roleplay.Vedma_Alpha.ADAPTER.GeoObjectFragment;
import com.Vedma.Roleplay.Vedma_Alpha.POJO.API.Ability;
import com.Vedma.Roleplay.Vedma_Alpha.POJO.API.ActionAdapter;
import com.Vedma.Roleplay.Vedma_Alpha.POJO.API.GeoPosition;
import com.Vedma.Roleplay.Vedma_Alpha.POJO.API.Invoker;
import com.Vedma.Roleplay.Vedma_Alpha.R;
import com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AsyncService;
import com.Vedma.Roleplay.Vedma_Alpha.SERVICE.VedmaExecutor;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.android.data.geojson.GeoJsonLayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.widget.Toast.LENGTH_LONG;
import static com.Vedma.Roleplay.Vedma_Alpha.ACTIVITY.Abilities.ACTION_ABILITY;
import static com.Vedma.Roleplay.Vedma_Alpha.ACTIVITY.Abilities.ACTION_ADAPTER;
import static com.Vedma.Roleplay.Vedma_Alpha.POJO.API.GeoPosition.GeoType.area;
import static com.Vedma.Roleplay.Vedma_Alpha.POJO.API.GeoPosition.GeoType.circle;
import static com.Vedma.Roleplay.Vedma_Alpha.POJO.API.GeoPosition.GeoType.geoJson;
import static com.Vedma.Roleplay.Vedma_Alpha.POJO.API.GeoPosition.GeoType.point;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AsyncService.Contains;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AsyncService.getCharId;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.AsyncService.sendNotification;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.MenuIntentService.startDIARY;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.MenuIntentService.startEVENTS;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.MenuIntentService.startMAIN;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.MenuIntentService.startNEWS;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.MenuIntentService.startOBJECT;
import static com.Vedma.Roleplay.Vedma_Alpha.SERVICE.MyFirebaseMessagingService.PUSH_UPDATE;
import static com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {
    private static final float CIRCLE_LAYER = 0.6f;
    private static final float AREA_LAYER = 0.1f;
    private static final float ROUTE_LAYER = 0.8f;
    private static final float GEOJSON_LAYER = 0.01f;
    public static final String POSITION = "GeoPosition";
    public static final String MAP_METHOD = "GeoMethod";
    BottomSheetBehavior<CoordinatorLayout> bottomSheetBehavior;
    FusedLocationProviderClient mFusedLocationClient;
    LocationCallback mLocationCallback;
    LocationRequest mLocationRequest;
    private GoogleMap mMap;
    Point size;
    private double lat;
    private AlertDialog alertDialog;
    private double lng;
    private double acc;
    boolean enough;
    private ListView onInteractList;
    private SparseArray<Marker> markers;
    private SparseArray<Circle> circles;
    private SparseArray<Polygon> areas;
    private SparseArray<Polyline> routes;
    private ArrayList<GeoJsonLayer> geoJsonLayers;
    private ArrayList<Integer> targets;
    Call<List<GeoPosition>>call;
    private SparseArray<GeoPosition> newGeoPositions;
    View header;
    private LatLng latLng;

//////////////////////////////////////
    float[] valuesAccelerator = new float[3];
    float[] valuesMagnet = new float[3];
    float[] valuesResult = new float[3];
    float[] valuesResult2 = new float[3];
    float[] inR = new float[9];
    float[] outR = new float[9];
    float[] r = new float[9];
 //////////////////////////////////////////////////// Rotation block
    Toast toast;
    private CoordinatorLayout llBottomSheet;
    private ImageView gpsState;
    private TextView BottomText;
    private TextView BottomTitle;
    private Marker myMarker;
    private Circle myAcc;
    private float rot;
    private Sensor sensorMagnet;
    private SensorManager sensorManager;
    private Timer timer;
    private int stepCounter=0;
    private int rotation;
    private Sensor sensorAccelerator;
    private boolean locToPlayer;
    private Animation animation;
    String[] buffer;
    FrameLayout mapNotReady;
//////////////////////////////////////////////////////////////////// BROADCAST RECEIVERS

    private BroadcastReceiver pushReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getGeoObjectsFromServer();
        }
    };
    ////////////////////////////////////////////////////////////// SENSOR LISTENER
    SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            switch (sensorEvent.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    System.arraycopy(sensorEvent.values, 0, valuesAccelerator, 0, 3);
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    System.arraycopy(sensorEvent.values, 0, valuesMagnet, 0, 3);
                    break;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };
   ///////////////////////////////////////////////////////////////

    private String charId;
    private GeoObjectFragment geoObjectDialog;
    private final Object Lock=new Object();

    @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.activity_maps_drawer,menu);
            return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.location_center)
        {
            locToPlayer=!locToPlayer;
            if (latLng!=null&&locToPlayer)
            {
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                item.setIcon(R.drawable.geo_u);
                //noinspection AccessStaticViaInstance
                toast.makeText(this, "Фокус на игроке", Toast.LENGTH_SHORT).show();
            } else {
                item.setIcon(R.drawable.geo);
                //noinspection AccessStaticViaInstance
                toast.makeText(this, "Свободный обзор", Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setScreenParameters();
        setToolBar();
        setNavigationBar();
        setSensor();
        Display display = getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
        enough=false;
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        Objects.requireNonNull(mapFragment).getMapAsync(this);
        BottomText = findViewById(R.id.onstepText);
        BottomTitle = findViewById(R.id.Map_title);
        markers = new SparseArray<>();
        circles = new SparseArray<>();
        areas = new SparseArray<>();
        routes = new SparseArray<>();
        geoJsonLayers = new ArrayList<>();
        targets= new ArrayList<>();
        newGeoPositions = new SparseArray<>();
        mapNotReady = findViewById(R.id.not_ready);
        gpsState = mapNotReady.findViewById(R.id.imageView3);
        animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.blink);
        gpsState.startAnimation(animation);
        charId=getCharId(this);
        PrepareBottomSheet();
    }
    @Override
    protected void onResume()
    {
        super.onResume();
        sensorManager.registerListener(listener, sensorAccelerator, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(listener, sensorMagnet, SensorManager.SENSOR_DELAY_NORMAL);
        getGpsLocationUpdates();
        enough=false;
        magnetTask();
    }
    @Override
    protected void onPause() {
        super.onPause();

        if (mFusedLocationClient!=null){
            stopLocUpdate(); }
        enough=true;
        sensorManager.unregisterListener(listener);
        timer.cancel();
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(pushReceiver, new IntentFilter(PUSH_UPDATE));
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if (pushReceiver !=null)
            unregisterReceiver(pushReceiver);
        alertDialog =null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (call!=null)
            call.cancel();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getGpsLocationUpdates();
        locToPlayer=true;
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(47.235982, 39.723529), 12);
        mMap.moveCamera(cameraUpdate);
        getGeoObjectsFromServer();
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                GeoPosition geo = (GeoPosition)marker.getTag();
                if (geo==null)
                    return false;
                if (marker.isInfoWindowShown())
                    marker.hideInfoWindow();
                showGeoInfoDialog(geo);
                return false;
            }
        });
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                if (locToPlayer&&latLng!=null)
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        });
        mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            @Override
            public void onPolygonClick(Polygon polygon) {
                GeoPosition geo = (GeoPosition)polygon.getTag();
                if (geo!=null)
                    showGeoInfoDialog(geo);
            }
        });

    }

    private void showGeoInfoDialog(final GeoPosition geo) {
        if (geoObjectDialog!=null)
            geoObjectDialog.dismiss();
        geoObjectDialog = GeoObjectFragment.newInstance(geo);
        geoObjectDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Ability ability = (Ability)view.getTag();
                ManageMapMethod(ability, geo.getId() , GeoPosition.MapMethod.onClick);
            }
        });
        geoObjectDialog.show(getSupportFragmentManager(), String.valueOf(geo.getId()));
    }


    //////////////////////////////////////////////////////////////////////// SENSOR
    private void magnetTask() {
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {

                VedmaExecutor.getInstance(MapsActivity.this).MainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        getActualDeviceOrientation();
                        rot=valuesResult2[0];
                        if (myMarker !=null)
                            myMarker.setRotation(rot);
                    }
                });
            }
        };
        timer.schedule(task, 0, 400);


        WindowManager windowManager = ((WindowManager) getSystemService(Context.WINDOW_SERVICE));
        Display display = Objects.requireNonNull(windowManager).getDefaultDisplay();
        rotation = display.getRotation();
    }

    private void getActualDeviceOrientation() {
        SensorManager.getRotationMatrix(inR, null, valuesAccelerator, valuesMagnet);
        int x_axis = SensorManager.AXIS_X;
        int y_axis = SensorManager.AXIS_Y;
        switch (rotation) {
            case (Surface.ROTATION_0): break;
            case (Surface.ROTATION_90):
                x_axis = SensorManager.AXIS_Y;
                y_axis = SensorManager.AXIS_MINUS_X;
                break;
            case (Surface.ROTATION_180):
                y_axis = SensorManager.AXIS_MINUS_Y;
                break;
            case (Surface.ROTATION_270):
                x_axis = SensorManager.AXIS_MINUS_Y;
                y_axis = SensorManager.AXIS_X;
                break;
            default: break;
        }
        SensorManager.remapCoordinateSystem(inR, x_axis, y_axis, outR);
        SensorManager.getOrientation(outR, valuesResult2);
        valuesResult2[0] = (float) Math.toDegrees(valuesResult2[0]);
        valuesResult2[1] = (float) Math.toDegrees(valuesResult2[1]);
        valuesResult2[2] = (float) Math.toDegrees(valuesResult2[2]);
    }


/////////////////////////////////////////////////////////////////////////////////////////


    private void getGeoObjectsFromServer()
    {
        if (call!=null)
            call.cancel();
        call = VedmaExecutor.getInstance(this).getJSONApi().getGeoObjects(charId);
        call.enqueue(new Callback<List<GeoPosition>>() {
            @Override
            public void onResponse(@NonNull Call<List<GeoPosition>> call, @NonNull Response<List<GeoPosition>> response) {
                if (response.code()==200&&response.body()!=null)
                {
                    synchronized (Lock) {
                        newGeoPositions.clear();
                        GeoPosition geo;
                        for (int i = 0; i < response.body().size(); i++) {
                            geo = response.body().get(i);
                            newGeoPositions.put(geo.getId(), geo);
                            Log.v("Vedma.map", geo.getName() + " is" + geo.getType().name());
                        }
                    }
                }
                MergeAndPlaceGeoPositions();
                UpdateMap();
                Log.d("Vedma.tick", "tick");
                if (geoObjectDialog!=null)
                {
                   GeoPosition geo = newGeoPositions.get(geoObjectDialog.getGeoId());
                   if (geo==null)
                       geoObjectDialog.dismiss();
                   else
                       geoObjectDialog.Update(geo);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<GeoPosition>> call, @NonNull Throwable t) {
                Log.e("Vedma.error", t.getMessage());
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_diary) {
            startDIARY(this);
        } else if (id == R.id.nav_main) {
           startMAIN(this);
        }  else if (id == R.id.nav_news) {
            startNEWS(this);
        }   else if (id == R.id.nav_object) {
            startOBJECT(this);
        } else if (id == R.id.nav_events) {
            startEVENTS(this);
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout_maps);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }

    public void MergeAndPlaceGeoPositions()
    {
        Log.d("Vedma.map",String.format("GeoObjects size: %d", newGeoPositions.size()));
        for (int i = 0; i < circles.size(); i++) {
            int key = circles.keyAt(i);
            Circle value = circles.valueAt(i);
            if (newGeoPositions.get(key) == null) {
                value.remove();
                circles.delete(key);
                if (markers.get(key)!=null){
                    markers.get(key).remove();
                    markers.delete(key);
                }
            } else if (newGeoPositions.get(key).getType()==circle /*&& !newGeoPositions.get(key).equals(circles.valueAt(i).getTag())*/) {
                circles.valueAt(i).remove();//TODO merge
                circles.delete(key);
                if (markers.get(key)!=null){
                    markers.get(key).remove();
                    markers.delete(key);
                }
                placeObj(newGeoPositions.get(key));
            }
        }
        for (int i = 0; i < newGeoPositions.size(); i++) {
            if (newGeoPositions.valueAt(i).getType()==circle && circles.get(newGeoPositions.keyAt(i)) == null)
                placeObj(newGeoPositions.valueAt(i));
        }
        for (int i = 0; i < markers.size(); i++) {
            int key = markers.keyAt(i);
            Marker value = markers.valueAt(i);
            if (newGeoPositions.get(key) == null) {
                value.remove();
                markers.delete(key);
            }else if (newGeoPositions.valueAt(i).getType()==point /*&& !newGeoPositions.get(key).equals(markers.valueAt(i).getTag())*/) {
                value.remove();
                markers.delete(key);
                placeObj(newGeoPositions.get(key));
            }
        }
        for (int i = 0; i < newGeoPositions.size(); i++) {
            if (markers.get(newGeoPositions.keyAt(i)) == null && newGeoPositions.valueAt(i).getType()==point)
                placeObj(newGeoPositions.valueAt(i));
        }
        for (int i = 0; i < areas.size(); i++) {
            int key = areas.keyAt(i);
            Polygon value = areas.valueAt(i);
            if (newGeoPositions.get(key) == null) {
                value.remove();
                areas.delete(key);
            } else if (newGeoPositions.valueAt(i).getType()==area /*&& !newGeoPositions.get(key).equals(areas.valueAt(i).getTag())*/) {
                value.remove();
                areas.delete(key);
                placeObj(newGeoPositions.get(key));
            }
        }
        for (int i = 0; i < newGeoPositions.size(); i++) {
            if (areas.get(newGeoPositions.keyAt(i)) == null && newGeoPositions.valueAt(i).getType()==area)
                placeObj(newGeoPositions.valueAt(i));
        }
            for (int i = 0; i < newGeoPositions.size(); i++) {
                if (newGeoPositions.valueAt(i).getType()==geoJson)
                    placeObj(newGeoPositions.valueAt(i));
            }
        Log.d("Vedma.map",String.format("Markers: %d, Circles %d, Areas%d, Routes%d",markers.size(), circles.size(), areas.size(), routes.size()));


    }
    public void placeObj(GeoPosition geoPosition) {
        // Log.d("mapUpdate",geoPosition.getType());\
        Marker marker;
        switch (geoPosition.getType()) {
            case geoJson:
                throw new UnsupportedOperationException();
                //JSONObject json = null;
//                try { TODO JSONLayer object
//                    json = new JSONObject(geoPosition.getLatLng());
//                    GeoJsonLayer geoJsonLayer = new GeoJsonLayer(mMap, json);
//                    GeoJsonLineStringStyle lineStringStyle = geoJsonLayer.getDefaultLineStringStyle();
//                    lineStringStyle.setColor(Color.GRAY);
//                    lineStringStyle.setWidth(2);
//                    lineStringStyle.setZIndex(0);
//
//                    GeoJsonPolygonStyle polygonStyle = geoJsonLayer.getDefaultPolygonStyle();
//                    polygonStyle.setFillColor(0xB1FF0000);
//                    polygonStyle.setStrokeColor(Color.BLACK);
//                    polygonStyle.setStrokeWidth(4);
//                    polygonStyle.setFillColor(0x99AAAAAA);
//                    polygonStyle.setZIndex(0);
//                    polygonStyle.setVisible(true);
//
//                    GeoJsonPointStyle pointStyle=geoJsonLayer.getDefaultPointStyle();
//                    pointStyle.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
//                    pointStyle.setAlpha(1);
//                    pointStyle.setVisible(true);
//                    geoJsonLayers.add(geoJsonLayer);
//                  geoJsonLayers.get(geoJsonLayers.size()-1).addLayerToMap();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
            case point:
                if (markers.get(geoPosition.getId()) == null) {
                    marker = mMap.addMarker(geoPosition.getMapObjectMarker());
                    marker.setTag(geoPosition);
                    markers.put(geoPosition.getId(), marker);
                }
                break;
            case circle:
                if (circles.get(geoPosition.getId()) != null) {
                    circles.get(geoPosition.getId()).remove();
                }
                    Circle circle = mMap.addCircle(geoPosition.getMapObjectCircle());
                    circle.setTag(geoPosition);
                    circles.put(geoPosition.getId(), circle);
                    circles.get(geoPosition.getId()).setZIndex(CIRCLE_LAYER);
                if ((geoPosition.getTransparency()!=0)&&geoPosition.getProperties().size()!=0){
                    markers.put(geoPosition.getId(), mMap.addMarker(geoPosition.getMapObjectMarker()));
                markers.get(geoPosition.getId()).setTag(geoPosition);
                markers.get(geoPosition.getId()).setTitle(geoPosition.getName());}
                //markers.get(geoPosition.getId()).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
               // markers.get(geoPosition.getId()).setAlpha(1f);
              //  circles.get(geoPosition.getId()).setVisible(true);
                Log.v("Vedma.map", String.format("Circle: Name=%s lat=%f, lng=%f, rad=%f; color=%h",
                        geoPosition.getName(),
                        geoPosition.getLatLng().latitude,
                        geoPosition.getLatLng().latitude,
                        circles.get(geoPosition.getId()).getRadius(),
                        circles.get(geoPosition.getId()).getFillColor()));
            break;
            case area:
                if (areas.get(geoPosition.getId())!=null)
                    areas.get(geoPosition.getId()).remove();
                if (geoPosition.getMapObjectPolygon()==null)
                    return;
                    Polygon area = mMap.addPolygon(geoPosition.getMapObjectPolygon());
                    area.setTag(geoPosition);
                    areas.put(geoPosition.getId(), area);
                    areas.get(geoPosition.getId()).setZIndex(AREA_LAYER);
                    if (geoPosition.IsObject())
                        area.setClickable(true);
                Log.v("Vedma.map", String.format("Area: Name=%s lat=%f, lng=%f; color=%h",
                        geoPosition.getName(),
                        geoPosition.getLatLng().latitude,
                        geoPosition.getLatLng().latitude,
                        geoPosition.getColorRGBA()));
                break;
            case route:
                break;
            default: throw new EnumConstantNotPresentException(GeoPosition.GeoType.class, geoPosition.getType().name());
        }
    }
    void placeSelf()
    {
        if (!gpsStatus())
        {
            lat = 0.0;
            lng = 0.0;
            acc = 0.0;
            mapNotReady.setVisibility(View.VISIBLE);
            gpsState.setImageResource(R.drawable.icons8no_gps);
            gpsState.startAnimation(animation);
            return;
        }

        if (myAcc ==null) {
            CircleOptions pl = new CircleOptions();
            pl.fillColor(0x80FF4000);
            pl.strokeWidth(2f);
            pl.strokeColor(0xCFFF4000);
            pl.center(latLng);
            pl.radius(acc);
            myAcc = mMap.addCircle(pl);
        }
        if (latLng!=null){
            myAcc.setCenter(latLng);
            myAcc.setRadius(acc);}
        if (myMarker ==null&&latLng!=null){
            MarkerOptions player = new MarkerOptions();
            player.position(latLng);
            player.icon(BitmapDescriptorFactory.fromResource(R.drawable.gps_arr));
            player.anchor(0.5f,0.5f);
            player.flat(false);
            player.zIndex(1f);
            myMarker = mMap.addMarker(player);

        }
        else if (latLng!=null)
            Objects.requireNonNull(myMarker).setPosition(latLng);
        Objects.requireNonNull(myMarker).setRotation(rot);
        UpdateMap();
    }

    void UpdateMap()
    {
        if (!gpsStatus())
        {
            lat = 0.0;
            lng = 0.0;
            acc = 0.0;
            mapNotReady.setVisibility(View.VISIBLE);
            gpsState.setImageResource(R.drawable.icons8no_gps);
            gpsState.startAnimation(animation);
            return;
        }
        if (latLng==null)
            return;
        int currentPosition=0;

        for (int y=0;y<areas.size();y++)
        {
            int MID = areas.keyAt(y);
            if (areas.get(MID) == null )
                continue;
            GeoPosition area = (GeoPosition) Objects.requireNonNull(areas.get(MID).getTag());
            if (area.getLatLngs()!=null
                    &&AsyncService.Contains(latLng, area.getLatLngs())) {
                Log.d("Vedma.ONSTEP", "!!!!" + MID);
                currentPosition=MID;
                if (stepCounter==0&&area.getONStep()!=null){
                    InvokeOnStep(MID);
                }
            }
        }
        for (int y=0;y<circles.size();y++)
        {
            int MID = circles.keyAt(y);
            Circle circle = circles.get(MID);
            if (Contains(latLng, circle)) {
                Log.d("Vedma.ONSTEP", "!!!!" + MID);
                currentPosition=MID;
                if (stepCounter==0&&((GeoPosition)circles.get(MID).getTag()).getONStep()!=null){
                    InvokeOnStep(MID);
                }
            }
        }
        for (int y=0;y<routes.size();y++)
        {
            int MID = routes.keyAt(y);
            if (Contains(latLng, ((GeoPosition)routes.get(MID).getTag()).getLatLngs(), ((GeoPosition)routes.get(MID).getTag()).getBorder())) {
                Log.d("Vedma.ONSTEP", "!!!!" + MID);
                currentPosition=MID;
                if (stepCounter==0&&((GeoPosition)routes.get(MID).getTag()).getONStep()!=null){
                    InvokeOnStep(MID);
                }
            }
        }
        if (currentPosition==0){
            UpdateBottomSheet(null);
        } else {
            UpdateBottomSheet(newGeoPositions.get(currentPosition));
        }
        stepCounter++;
        if (stepCounter>12)
            stepCounter=0;
    }

    public void getGpsLocationUpdates()
    {
        mLocationCallback = new LocationCallback() {
            FusedLocationProviderClient mFusedLocationClient1;
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult); // why? this. is. retarded. Android.
                if(enough&&mFusedLocationClient1!=null)
                    mFusedLocationClient1.removeLocationUpdates(this);
                mFusedLocationClient1=mFusedLocationClient;
                if (locationResult == null) {
                    return;
                }
                Location currentLocation;
                currentLocation = locationResult.getLastLocation();

                if (currentLocation.getAccuracy() != 0) {
//                    if (currentLocation.isFromMockProvider()){
//                        lat = 42.0;
//                        lng = 42.0;
//                        acc = 42.0;
//                        //todo FAKE GPS
//                    }
//                    else {
                        lat = currentLocation.getLatitude();
                        lng = currentLocation.getLongitude();
                        acc = currentLocation.getAccuracy();
                        latLng = new LatLng(lat, lng);
       //             }
                    mapNotReady.setVisibility(View.GONE);
                    gpsState.clearAnimation();
                    placeSelf();
                    if (locToPlayer) {
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    }
                }
            }
        };
        mLocationRequest=new LocationRequest();
        mLocationRequest.setPriority(PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setInterval(3000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        this.mFusedLocationClient = LocationServices.getFusedLocationProviderClient(MapsActivity.this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[] {Manifest.permission_group.LOCATION},0);
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback,
                Looper.myLooper());
        if (!gpsStatus())
        {
            lat = 0.0;
            lng = 0.0;
            acc = 0.0;
            mapNotReady.setVisibility(View.VISIBLE);
            gpsState.setImageResource(R.drawable.icons8no_gps);
            gpsState.startAnimation(animation);
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            if (location.getAccuracy() != 0) {
//                                if (location.isFromMockProvider()){
//                                    lat = 42.0;
//                                    lng = 42.0;
//                                    acc = 42.0;
//                                } else if (!gpsStatus()) {
//                                    lat = 0.0;
//                                    lng = 0.0;
//                                    acc = 0.0;
//                                } else {
                                    lat = location.getLatitude();
                                    lng = location.getLongitude();
                                    acc = location.getAccuracy();
                            //    }
                                //todo FAKE GPS
                                latLng = new LatLng(lat,lng);
                                mapNotReady.setVisibility(View.GONE);

                                placeSelf();
                            }
                            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                        }
                    }
                });
    }


    void PrepareBottomSheet()
    {
// получение вью нижнего экрана
        llBottomSheet = findViewById(R.id.bottom_sheet);
        llBottomSheet.setTag(0);
        onInteractList = findViewById(R.id.list);
        onInteractList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Ability ability = (Ability)view.getTag();
                ManageMapMethod(ability,(Integer)llBottomSheet.getTag(), GeoPosition.MapMethod.onInterract);
            }
        });
// настройка поведения нижнего экрана

        bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);
// настройка состояний нижнего экрана
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
// настройка максимальной высоты

        BottomTitle.setText("Ничего необычного");
        llBottomSheet.setMinimumHeight((int) (size.y*0.7));
// настройка возможности скрыть элемент при свайпе вниз
        bottomSheetBehavior.setHideable(false);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                if (i==BottomSheetBehavior.STATE_HALF_EXPANDED||i==BottomSheetBehavior.STATE_EXPANDED)
                 bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }

            @Override
            public void onSlide(@NonNull View view, float v) {
                if (v>0.2)
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
    }



    void UpdateBottomSheet(@Nullable GeoPosition geo) {
        if (geo == null) {
            BottomTitle.setText("Ничего необычного");
            BottomText.setText("");
            llBottomSheet.setTag(0);
            bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View view, int i) {
                    if (i == BottomSheetBehavior.STATE_HALF_EXPANDED || i == BottomSheetBehavior.STATE_EXPANDED)
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                }

                @Override
                public void onSlide(@NonNull View view, float v) {
                    if (v > 0.2)
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            });

        } else {
            BottomText.setText(geo.getDescription());
            BottomTitle.setText(geo.getName());
            llBottomSheet.setTag(geo.getId());
            onInteractList.setAdapter(new AbilitiesAdapter(this, geo.getONInteract(), false));
            bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View view, int i) {
                }

                @Override
                public void onSlide(@NonNull View view, float v) {

                }
            });
        }
    }
    private void  stopLocUpdate()
    {
    enough=true;
    mFusedLocationClient.removeLocationUpdates(mLocationCallback);

  //  mLocationCallback=null;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
    private boolean gpsStatus() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } else
            return false;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {

        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                     getGpsLocationUpdates();
                } else {
                    Toast.makeText(this,"Использование карты невозможно",LENGTH_LONG).show();
                }
            }
        }
    }
    private void setNavigationBar() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        header = navigationView.getHeaderView(0);
    }

    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar_maps);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout_maps);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void setScreenParameters() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES);
    }
    private void setSensor() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);           //magnetic field sensor
        if (sensorManager != null) {
            sensorMagnet = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            sensorAccelerator = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        buffer= new String[10];
    }
    private void ManageMapMethod(final Ability ability, final int position, final GeoPosition.MapMethod method) {
        final Context context = this;
        VedmaExecutor.getInstance(this).getJSONApi().getMapActionAdapter(charId,
                position,
                ability.getId(),
                ability.getChainId(),
                method).enqueue(new Callback<ArrayList<ActionAdapter>>() {
            @Override
            public void onResponse(Call<ArrayList<ActionAdapter>> call, Response<ArrayList<ActionAdapter>> response) {
                if(response.code()==200) {
                    if (response.body().size()==0){
                        (new AlertDialog.Builder(context)).setTitle("Подтверждение").setMessage("Подтвердите действие")
                                .setPositiveButton("Подтвердить", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        InvokeNow(position,ability, method, context);
                                        dialog.dismiss();
                                    }
                                }).setCancelable(true).setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
                    } else{
                        Intent intent = new Intent(context, ChooserActivity.class);
                        intent.putExtra(ACTION_ABILITY, ability);
                        intent.putExtra(POSITION, position);
                        intent.putExtra(MAP_METHOD,method);
                        intent.putExtra(ACTION_ADAPTER,response.body());
                        context.startActivity(intent);
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<ActionAdapter>> call, Throwable t) {
                Log.e("Vedma.Error", t.getMessage());
            }
        });
    }

    private void InvokeNow(int currentPosition, final Ability ability, GeoPosition.MapMethod method, final Context context) {
        if (method==GeoPosition.MapMethod.onClick) {
            VedmaExecutor.getInstance(this).getJSONApi().invokeMapClick(charId,currentPosition,
                    ability.getId(),ability.getChainId(),new ArrayList<Invoker>()).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.code() == 200) {
                        if (response.body().equals("wait")){
                            Toast.makeText(context, "Попробуйте позже", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (!response.body().replace(" ","").replace("\n","").equals("")) {
                            Toast.makeText(context, response.body(), Toast.LENGTH_SHORT).show();
                        }
                        timer.cancel();
                        timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                getGeoObjectsFromServer();
                            }
                        }, 3000);
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e("Vedma.Error", t.getMessage());
                }
            });
        } else if(method==GeoPosition.MapMethod.onInterract){
            VedmaExecutor.getInstance(this).getJSONApi().invokeMapInteract(charId,currentPosition,
                    ability.getId(),ability.getChainId(),new ArrayList<Invoker>()).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.code() == 200) {
                        if (response.body().equals("wait")){
                            Toast.makeText(context, "Попробуйте позже", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (!response.body().replace(" ","").replace("\n","").equals("")) {
                            Toast.makeText(context, response.body(), Toast.LENGTH_SHORT).show();
                        }
                        timer.cancel();
                        timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                getGeoObjectsFromServer();
                            }
                        }, 3000);
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e("Vedma.Error", t.getMessage());
                }
            });
        }
    }
    private void InvokeOnStep(final int currentPosition)
    {
        VedmaExecutor.getInstance(this).getJSONApi().invokeMapStep(charId,currentPosition).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.code() == 200) {
                   if (response.body().equals("wait")){
                       timer = new Timer();
                       timer.schedule(new TimerTask() {
                           @Override
                           public void run() {
                               InvokeOnStep(currentPosition);
                           }
                       }, 10000);
                   } else {
                       timer.cancel();
                       timer = new Timer();
                       timer.schedule(new TimerTask() {
                           @Override
                           public void run() {
                               getGeoObjectsFromServer();
                           }
                       }, 3000);
                   }
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("Vedma.Error", t.getMessage());
            }
        });
    }

}





