package com.Vedma.Roleplay.Vedma_Alpha.ACTIVITY;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;

import com.Vedma.Roleplay.Vedma_Alpha.POJO.API.GeoPosition;
import com.Vedma.Roleplay.Vedma_Alpha.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;



public class ActionMap extends AppCompatActivity implements OnMapReadyCallback {
    String mapGroup="";
    ArrayList<GeoPosition> mapObjects;
    SparseArray<Marker> markers;
    private GoogleMap mMap;
    Intent intent;
    ArrayList<GeoPosition> pack;
    String gid;
    AlertDialog.Builder ad;
    AlertDialog.Builder cad;
    String vars;
    Polyline polyline;
    private static String parse="";
    private String PID;

    public static void setParse(String parse) {
        ActionMap.parse = parse;
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("content"))
                getObjects(parse);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_map);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        intent=getIntent();
      //  gid = readGID(ActionMap.this);
    //   PID=readPID(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        IntentFilter intentFilter = new IntentFilter("com.Vedma.Roleplay.witch_20.update_map_now");
        registerReceiver(receiver, intentFilter );
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        pack = new ArrayList<>();

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
   //     prepareDialogs();

        mapGroup = "";
//        postMessage(ActionMap.this, "/characters/"+gid+"/mapobjects/",
//                "device_id=" + PID , "map_sync","GET");TODO

    }
    void getObjects(String parse)
    {
//        mapObjects = new ArrayList<>();
//        GeoPosition geoPosition;
//        JSONArray jsonArray;
//        JSONArray filter;
//        String field;
//        String value;
//        JSONArray filtered = new JSONArray();
//        try {
//            jsonArray= new JSONArray(parse);
//
//            if (intent.hasExtra("filter")) {
//                filter = new JSONArray(intent.getStringExtra("filter"));
//                for (int j =0; j<filter.length(); j++)
//                {
//                    field= filter.getJSONObject(j).getString("field");
//                    value= filter.getJSONObject(j).getString("value");
//                    for (int i =0; i<jsonArray.length(); i++) {
//                        JSONObject buffer = jsonArray.getJSONObject(i);
//                            if (buffer.getString(field).equals(value))
//                                filtered.put(buffer);
//                        }
//                    }
//                    if (filter.length()>0)
//                        jsonArray=filtered;
//                }
//                //if (filtered.length()>0)
//
//            }
//         catch (JSONException e) { e.printStackTrace(); return;}
//
//            for (int i =0; i<jsonArray.length(); i++)
//            {
//                try{
//
//                    JSONObject buffer = jsonArray.getJSONObject(i);
//                     geoPosition = new GeoPosition(buffer.getString("id"),buffer.getJSONObject("mapobject").getJSONObject("coord").toString(),buffer.getJSONObject("mapobject").getString("type")
//                        , buffer.getString("name") , buffer.getBoolean("active") ,buffer.getString("layer"), buffer.getString("color"));
//                     geoPosition.setVisited(buffer.getString("visited"));
//                     geoPosition.setMapGroup(buffer.getString("group"));
//                    mapObjects.add(geoPosition);}
//                catch (JSONException e) { e.printStackTrace(); }
//            }
//
//        placeObjects();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_active_map_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.news)
        {
        //    onClickSendMapObj();
        } else if (item.getItemId()==R.id.clear)
        {
         //.   onClearMapObj();
        }
        else
            super.onBackPressed();
        return true;
    }

//    void placeObjects() {
//        markers = new SparseArray<>();
//        ArrayList<LatLng> latLngs = new ArrayList<>();
//        for (int i = 0; i < mapObjects.size(); i++) {
//            GeoPosition geoPosition = mapObjects.get(i);
//            if (geoPosition !=null&& geoPosition.getVisited()!=null&& geoPosition.getVisited().equals("true")){
//                Marker marker = mMap.addMarker(geoPosition.getMapObjectMarker());
//                marker.setTitle(geoPosition.getName());
//               // marker.showInfoWindow();
//                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//                marker.setTag(geoPosition);
//                latLngs.add(marker.getPosition());
//                markers.put(geoPosition.getMID(), marker);
//            }
//        }
//        if (markers.size() > 0) {
//           // mMap.moveCamera(CameraUpdateFactory.newLatLng(markers.valueAt(0).getPosition()));
//            LatLngBounds.Builder b = new LatLngBounds.Builder();
//            for (int g=0;g<markers.size();g++) {
//                b.include(markers.valueAt(g).getPosition());
//            }
//            LatLngBounds bounds = b.build();
//            final CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(
//                    bounds,
//                    300);
//            mMap.animateCamera(cameraUpdate);
//
//            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//                @Override
//                public boolean onMarkerClick(Marker marker) {
//                    marker.showInfoWindow();
//                  //  mMap.moveCamera(cameraUpdate);///Добавить точку
//                    if (!pack.contains((GeoPosition) marker.getTag())) {
//                        pack.add(((GeoPosition) marker.getTag()));
//                        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
//                     //   Toast.makeText(ActionMap.this, marker.getSnippet(), Toast.LENGTH_SHORT).show();
//
//                        if (polyline!=null) {
////                            polyline.setVisible(false);
////                            for (int i = 0; i < polyline.getPoints().size(); i++)
//                                polyline.remove();
//                        }
//                        PolylineOptions polylineOptions = new PolylineOptions();
//                        for (int i = 0; i < pack.size(); i++) {
//                            LatLng latLng;
//                            JSONObject p = null;
//                            try {
//                                p = new JSONObject(pack.get(i).getLatlng());
//                                latLng = new LatLng(Float.valueOf(p.getString("lat")), Float.valueOf(p.getString("lng")));
//                                polylineOptions.add(latLng);
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                                break;
//                            }
//
//
//
//                        }
//                        polyline = mMap.addPolyline(polylineOptions);
//                    }
//                    else if (pack.get(pack.size()-1).equals(marker.getTag())){
//                        pack.remove(pack.size()-1);///Отменить точку
//                        if (!pack.contains(marker.getTag()))
//                            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//                        // Toast.makeText(ActionMap.this, marker.getSnippet(), Toast.LENGTH_SHORT).show();
//
//                        if (polyline!=null) {
////                            polyline.setVisible(false);
////                            for (int i = 0; i < polyline.getPoints().size(); i++)
//                                polyline.remove();
//                        }
//                        //mMap.clear();
//                        PolylineOptions polylineOptions = new PolylineOptions();
//                        for (int i = 0; i < pack.size(); i++) {
//                            LatLng latLng;
//                            JSONObject p;
//                            try {
//                                p = new JSONObject(pack.get(i).getLatlng());
//                                latLng = new LatLng(Float.valueOf(p.getString("lat")), Float.valueOf(p.getString("lng")));
//                                polylineOptions.add(latLng);
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                                break;
//                            }
//
//                        }
//                        polyline = mMap.addPolyline(polylineOptions);
//                    } else
//                        marker.hideInfoWindow();
//
//
//                    return true;
//
//                }
//            });
//        }
//    }
//}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
