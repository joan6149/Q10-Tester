package com.quality.mytester;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    private Button btnDiscover;
    //private static ProgressBar spinner;
    //private Action actions;
    public final static String PERMISOS = "Permisos: ";


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.btnDiscover = (Button) findViewById(R.id.btnDiscover);
        /*this.spinner = (ProgressBar) findViewById(R.id.progressBar);
        this.spinner.setVisibility(View.GONE);*/
        this.btnDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(MainActivity.this, ListaActivity.class);
                startActivity(intent2);
            }
        });
        this.Permissions();


    }


    private void Permissions() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED) {
                Log.i(PERMISOS, "Tienes permisos para bluetooth");
            }
            if(checkSelfPermission(Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED) {
                Log.i(PERMISOS, "Tienes permisos para bluetooth admin");
            }
            if(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                Log.w(PERMISOS, "Permisos para COARSE denegados!!");
            }
        }
    }


    @Override
    protected void onDestroy() {
        //unregisterReceiver(this.actions.getReceiver());
        super.onDestroy();
    }

    public Button getBtnDiscover() {
        return btnDiscover;
    }

    public void setBtnDiscover(Button btnDiscover) {
        this.btnDiscover = btnDiscover;
    }

}