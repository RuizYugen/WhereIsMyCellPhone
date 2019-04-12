package com.example.ruiz.lugarescercanos;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SMSReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle inteBundle=intent.getExtras();
        if(inteBundle!=null){
            Object[] sms=(Object[])inteBundle.get("pdus");

            for (int i=0; i<sms.length; i++){
                SmsMessage smsMessage=SmsMessage.createFromPdu((byte[])sms[i]);
                String phone=smsMessage.getOriginatingAddress();
                String message=smsMessage.getMessageBody().toString();
                String[] arr=message.split("##");
                if(arr.length==3){
                    if(phone.equals("+524451216913")&&arr[0].equals("SOS")){
                        Toast.makeText(context,"Encontro",Toast.LENGTH_SHORT).show();
                        Intent abri=new Intent(context,MapsActivity.class);
                        abri.putExtra("lat",arr[1]);
                        abri.putExtra("lon",arr[2]);
                        context.startActivity(abri);
                    }
                }
            }
        }
    }
}
