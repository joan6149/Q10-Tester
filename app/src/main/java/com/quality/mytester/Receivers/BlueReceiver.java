package com.quality.mytester.Receivers;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.quality.mytester.ListaActivity;

import java.util.ArrayList;
import java.util.List;

public class BlueReceiver extends BroadcastReceiver {
    //public static List<Molino> unpairedMolinos = new ArrayList<Molino>();
    //private List<Molino> unpairedMolinos;
    private List<BluetoothDevice> unpairedMolinos;
    private Context ctx;


    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        String action = intent.getAction();

        // Cada vez que se descubra un nuevo dispositivo por Bluetooth, se ejecutara
        // este fragmento de codigo
        if ("CONNECTED".equals(action))
        {
            Log.w("Broadcast","El Servicio ha sido conectado!!");
        }
        else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
        {

        } else if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {

        }
    }


}