package com.quality.mytester.Adapters;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.quality.mytester.Modelos.Molino;
import com.quality.mytester.R;

import java.util.ArrayList;
import java.util.List;

public class MolinoAdapter extends BaseAdapter {

    private Context ctx;
    private int layout;
    private List<BluetoothDevice> molinos;
    private String singleDoses;
    private String doubleDoses;
    private boolean correct;
    private boolean init;
    //private List<View> vistas;

    public MolinoAdapter(Context ctx, int layout, List<BluetoothDevice> molinos) {
        this.ctx = ctx;
        this.layout = layout;
        this.molinos = molinos;
        this.init = true;
        this.correct = false;
        //this.vistas = new ArrayList<View>();
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
        TextView singleDosesText = (TextView) v.findViewById(R.id.textView4);
        TextView doubleDosesText = (TextView) v.findViewById(R.id.textView5);
        ImageView result = (ImageView) v.findViewById(R.id.imageView2);
        textView.setText("Name: " + currentMolino.getName());
        mac.setText("MAC: " + currentMolino.getAddress());
        singleDosesText.setText("Single doses: -");
        doubleDosesText.setText("Double doses: - ");

        if(this.singleDoses != null) {
            this.init = false;
            singleDosesText.setText("Single doses: " + this.singleDoses);
        }
        if(this.doubleDoses != null) {
            this.init = false;
            doubleDosesText.setText("Double doses: " + this.doubleDoses);
        }
        if(this.correct == true && this.init == false) {
            //IMAGEN VISTO
            result.setImageResource(R.drawable.visto);
        } else if(this.correct == false && init == false){
            //IMAGEN CRUZ
            result.setImageResource(R.drawable.error);
        }

        return v;
    }

    public void setSingleDoses(String singleDoses) {
        this.singleDoses = singleDoses;
    }

    public void setDoubleDoses(String doubleDoses) {
        this.doubleDoses = doubleDoses;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }
}
