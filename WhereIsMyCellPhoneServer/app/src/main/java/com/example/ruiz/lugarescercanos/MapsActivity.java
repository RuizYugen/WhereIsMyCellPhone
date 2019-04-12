package com.example.ruiz.lugarescercanos;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    TextView txt;
    TextView cel;
    private final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION=101;
    private final int PERMISSIONS_REQUEST_SEND_SMS=99;
    private final int PERMISSIONS_REQUEST_RECEIVE_SMS=98;
    private final int PERMISSIONS_REQUEST_READ_SMS=97;
    double lat=0, lon=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        txt=findViewById(R.id.txtCordenadas);
        cel=findViewById(R.id.txtCordenadasTel);
        Intent intent=getIntent();
        if(intent.getStringExtra("lat")!=null){
            cel.setText("Coordenadas Tel: "+intent.getStringExtra("lat")+","+intent.getStringExtra("lon"));
            lon=Double.parseDouble(intent.getStringExtra("lon"));
            lat=Double.parseDouble(intent.getStringExtra("lat"));

        }

        //
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new
                            String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            );
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new
                            String[]{Manifest.permission.SEND_SMS},
                    PERMISSIONS_REQUEST_SEND_SMS
            );
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new
                            String[]{Manifest.permission.RECEIVE_SMS},
                    PERMISSIONS_REQUEST_RECEIVE_SMS
            );
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new
                            String[]{Manifest.permission.READ_SMS},
                    PERMISSIONS_REQUEST_READ_SMS
            );
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        UbicacionActual();
        // Add a marker in Sydney and move the camera
       /* LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
    }

    private void UbicacionActual(){


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(this, "Notiene", Toast.LENGTH_SHORT).show();
            return;
        }
        LocationManager locationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location location=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        actualizar(location);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,locationListener);


    }
    public void actualizar(Location location){
        LatLng lugar;
        if(location!=null){
            txt.setText("Cordenadas: "+location.getLatitude()+"  "+location.getLongitude());
            lugar=new LatLng(location.getLatitude(),location.getLongitude());
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(lugar).title("Ubicacion Actual"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lugar,12));
            if(lat!=0||lon!=0){
                LatLng perdido=new LatLng(lat,lon);
                mMap.addMarker(new MarkerOptions().position(perdido).title("Ubicacion del telefono Perdido"));
                mMap.addPolyline(new PolylineOptions().add(lugar,perdido).width(5).color(Color.RED));
            }
        }
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            actualizar(location);

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {


        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }


    };

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    return;
                } else {
                    finish();
                }
                return;
            case PERMISSIONS_REQUEST_SEND_SMS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    return;
                } else {
                    finish();
                }
                return;
            case PERMISSIONS_REQUEST_RECEIVE_SMS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    return;
                } else {
                    finish();
                }
                return;
            case PERMISSIONS_REQUEST_READ_SMS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    return;
                } else {
                    finish();
                }
                return;
        }
    }

    public void buscar(View v){
        SmsManager smsManager=SmsManager.getDefault();
        String clave="SOS";
        smsManager.sendTextMessage("4451216913",null,clave,null,null);
        Toast.makeText(this, "Buscando", Toast.LENGTH_SHORT).show();
    }

    public void Enviar(View view) {
        SmsManager smsManager=SmsManager.getDefault();
        String clave="SOS##20##-100";
        smsManager.sendTextMessage("4451216913",null,clave,null,null);
        Toast.makeText(this, "Buscando", Toast.LENGTH_SHORT).show();
    }
}
