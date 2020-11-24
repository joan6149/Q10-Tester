package com.quality.mytester.Receivers;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.quality.mytester.Adapters.MolinoAdapter;
import com.quality.mytester.ListaActivity;
import com.quality.mytester.Listeners.Action;
import com.quality.mytester.Modelos.Molino;
import com.quality.mytester.R;

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
        if (BluetoothDevice.ACTION_FOUND.equals(action))
        {
            // Acciones a realizar al descubrir un nuevo dispositivo
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            //this.unpairedMolinos.add(new Molino(device.getName(), device.getAddress()));
            this.unpairedMolinos.add(device);
            System.out.println(device.getName()  + " with " + device.getAddress() + " added!");

        }

        // Codigo que se ejecutara cuando el Bluetooth finalice la busqueda de dispositivos.
        else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
        {
            // Acciones a realizar al finalizar el proceso de descubrimiento
            System.out.println("Busqueda finalizada...");

            Bundle bundle = new Bundle();
            //bundle.putSerializable("molinos", (ArrayList<Molino>) this.unpairedMolinos);
            bundle.putSerializable("molinos", (ArrayList<BluetoothDevice>) this.unpairedMolinos);
            Intent intent2 = new Intent(context, ListaActivity.class);
            intent2.putExtra("Molinos", bundle);
            context.startActivity(intent2);

        } else if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
            System.out.println("Empezando escanero...");
            //this.unpairedMolinos = new ArrayList<Molino>();
            this.unpairedMolinos = new ArrayList<BluetoothDevice>();
        }
    }


}