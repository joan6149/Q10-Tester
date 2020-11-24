package com.quality.mytester.Listeners;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.RequiresPermission;

import com.quality.mytester.Adapters.MolinoAdapter;
import com.quality.mytester.ListaActivity;
import com.quality.mytester.Modelos.Molino;
import com.quality.mytester.R;
import com.quality.mytester.Receivers.BlueReceiver;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Action implements View.OnClickListener {
    private Context ctx;
    private int accion;
    private final BlueReceiver receiver = new BlueReceiver();
    private List<Molino> ret;

    public Action(Context ctx) {
        this.ctx = ctx;
        this.accion = 0;
        this.ret = new ArrayList<Molino>();
    }

    @Override
    public void onClick(View v) {
        ProgressBar spinner;
        if(v.getResources().getResourceEntryName(v.getId()).equals("btnDiscover")) {
            System.out.println("Aqui hemos pulsado el boton");
            this.EscanearMolinos();
            spinner = (ProgressBar) v.getRootView().findViewById(R.id.progressBar);
            spinner.setVisibility(View.VISIBLE);
        } else if(v.getResources().getResourceEntryName(v.getId()).equals("btnTestar")) {
            System.out.println("Aqui Testamos el molino");
        }
    }

    //METODOS DE ACTIONESSS
    void EscanearMolinos() {
        //Aqui la generaremos manualmente de momento.
        BluetoothAdapter BTAdapter = BluetoothAdapter.getDefaultAdapter();
        //Set<BluetoothDevice> pairedDevices = BTAdapter.getBondedDevices();
        //List<Molino> ret = new ArrayList<Molino>();
        //ret.add(new Molino("Molino 1", "1"));
        //ret.add(new Molino("Molino 2", "2"));
        //this.checkPermissions();
        /*if(pairedDevices.size() > 0) {
            for(BluetoothDevice device: pairedDevices) {
                ret.add(new Molino(device.getName(), device.getAddress()));
            }
        }*/
        //get unpaired device

        try {
            if(BTAdapter.isDiscovering()) {
                System.out.println("Bluyetooth esta buscando, se cancela...");
                BTAdapter.cancelDiscovery();
            }

            if(BTAdapter.startDiscovery()) {
                Toast.makeText(ctx, "Descubriendo dispositivos...", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ctx, "Error en la busqueda de dispositivos...", Toast.LENGTH_SHORT).show();
            }
        }catch(Exception e) {
            System.out.println(e);
        }

        IntentFilter filtro = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filtro.addAction(BluetoothDevice.ACTION_FOUND);
        filtro.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.ctx.registerReceiver(receiver, filtro);

    }

    public BlueReceiver getReceiver() {
        return receiver;
    }

    public void setRet(List<Molino> ret) {
        this.ret = ret;
    }

    public Context getCtx() {
        return ctx;
    }
}
