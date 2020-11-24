package com.quality.mytester.Adapters;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.quality.mytester.Modelos.Molino;
import com.quality.mytester.R;

import java.util.List;

public class MolinoAdapter extends BaseAdapter {

    private Context ctx;
    private int layout;
    private List<BluetoothDevice> molinos;

    public MolinoAdapter(Context ctx, int layout, List<BluetoothDevice> molinos) {
        this.ctx = ctx;
        this.layout = layout;
        this.molinos = molinos;
    }

    public void addDevice(BluetoothDevice device) {
        if(!this.molinos.contains(device)) {
            if(device.getName() != null) {
                if(device.getName().equals("Q10-0")) {
                    this.molinos.add(device);
                }
            }
        }
    }

    @Override
    public int getCount() {
        return this.molinos.size();
    }

    @Override
    public Object getItem(int position) {
        return this.molinos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        LayoutInflater layoutInflater = LayoutInflater.from(this.ctx);
        v = layoutInflater.inflate(R.layout.list_item, null);
        BluetoothDevice currentMolino = (BluetoothDevice) this.getItem(position);

        TextView textView = (TextView) v.findViewById(R.id.textView);
        TextView mac = (TextView) v.findViewById(R.id.textView2);
        textView.setText("Name: " + currentMolino.getName());
        mac.setText("MAC: " + currentMolino.getAddress());

        return v;
    }
}
