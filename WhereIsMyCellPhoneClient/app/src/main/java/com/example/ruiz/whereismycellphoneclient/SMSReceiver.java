package com.example.ruiz.whereismycellphoneclient;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

import java.util.ArrayList;

public class SMSReceiver extends BroadcastReceiver {
    private Context context;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context=context;
        Bundle inteBundle = intent.getExtras();
        if (inteBundle != null) {
            Object[] sms = (Object[]) inteBundle.get("pdus");

            for (int i = 0; i < sms.length; i++) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);
                String phone = smsMessage.getOriginatingAddress();
                String message = smsMessage.getMessageBody().toString();
                if (phone.equals("+524451216913") && message.equals("SOS")) {
                    contador=0;
                    //Obtener cordenada

                    LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    enviar(location);
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,locationListener);

                }

            }
        }
    }
    private int contador=0;
    public void enviar(Location location){
        if(contador==0){
            if (location!=null){
                double lat = 20, lon = -100;
                SmsManager smsManager = SmsManager.getDefault();
                lat=location.getLatitude();
                lon=location.getLongitude();
                String mensaje="SOS##"+lat+"##"+lon;
                if(mensaje.length()>1600){
                    ArrayList messageParts=smsManager.divideMessage(mensaje);
                    smsManager.sendMultipartTextMessage("4451216913",null,messageParts,null,null);
                    Toast.makeText(context, "Enviando Ubicacion", Toast.LENGTH_SHORT).show();
                }else{
                    smsManager.sendTextMessage("4451216913",null,mensaje,null,null);
                    Toast.makeText(context, "Enviando ubicacion", Toast.LENGTH_SHORT).show();
                }
                contador++;
            }
        }


    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            enviar(location);

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
}
