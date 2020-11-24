package com.quality.mytester;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ThemedSpinnerAdapter;
import android.widget.Toast;

import com.ble.ellamma.bleand.Ble;
import com.quality.mytester.Adapters.MolinoAdapter;
import com.quality.mytester.Modelos.Molino;
import com.quality.mytester.Receivers.BlueReceiver;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ListaActivity extends AppCompatActivity {
    //private List<Molino> molinos;
    private List<BluetoothDevice> molinos;
    private List<BluetoothDevice> devices;
    private ListView listView;
    private ProgressBar spinner;
    private Button test;
    BluetoothAdapter BTAdapter;
    private boolean mScanning = true;
    private Handler handler = new Handler();
    private MolinoAdapter leDeviceListAdapter;
    private Ble ble;
    private BluetoothAdapter.LeScanCallback leScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi,
                                     byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //devices.add(device); //Aqui los mete todos
                            leDeviceListAdapter.addDevice(device); //Aqui no mete repetidos ni mete otra cosa que no sean Molinos con lo que no s eve nada en la lista
                            leDeviceListAdapter.notifyDataSetChanged();
                        }
                    });
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);
        this.molinos = new ArrayList<BluetoothDevice>();
        this.devices = new ArrayList<BluetoothDevice>();
        this.leDeviceListAdapter = new MolinoAdapter(ListaActivity.this, R.layout.list_item, this.molinos);
        this.listView = (ListView) findViewById(R.id.listView);
        this.spinner = (ProgressBar) findViewById(R.id.progressBar);
        this.test = (Button) findViewById(R.id.btnTest);
        this.test.setEnabled(false);
        //this.spinner.setVisibility(View.GONE);


        final BluetoothManager bluetoothManager;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            this.BTAdapter = bluetoothManager.getAdapter();
        }else {
            this.BTAdapter = BluetoothAdapter.getDefaultAdapter();
        }

        this.listView.setAdapter(leDeviceListAdapter);
        this.scanDevices(true);
        this.test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testDevices();
            }
        });
    }


    public ListView getListView() {
        return listView;
    }

    @Override
    protected void onPostResume() {
        //this.spinner.setVisibility(View.GONE);
        super.onPostResume();
    }

    private void scanDevices(final boolean enable) {
        if(enable) {
            this.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    spinner.setVisibility(View.GONE);
                    if(leDeviceListAdapter.isEmpty()) {
                        Toast.makeText(ListaActivity.this, "NO SE HAN ENCONTRADO MOLINOS Q10-EVO CERCANOS!", Toast.LENGTH_LONG).show();
                    } else {
                        test.setEnabled(true);
                    }
                    BTAdapter.stopLeScan(leScanCallback);
                    //testDevices();
                }
            }, 5000);

            mScanning = true;
            BTAdapter.startLeScan(leScanCallback);
        } else {
            mScanning = false;
            BTAdapter.stopLeScan(leScanCallback);
        }
    }

    public void testDevices() {
        //Ponemos los dispositivos en un list de BluetoothDevice
        final int molinoActual = 0;
        List<Thread> threads = new ArrayList<Thread>();
        for(int i =0;i<this.leDeviceListAdapter.getCount();i++) {
            this.devices.add((BluetoothDevice) this.leDeviceListAdapter.getItem(i));
        }
        //Nos conectamos al servidor GATT (aqui no podremos NUNCA USAR UN BLUCLE PERO ESTE METODO ES ASYNCRONOUS Y SE LIARA UNA TANGANA BRUTAL
        //Asi que nos conectamos el primer dispositivo si es que existe y en la captura de la señal de acabado pasaremos el siguiente editando el valor de value
        for(int i=0;i<this.devices.size();i++) {
            int finalI = i;
            threads.add(new Thread(new Runnable() {
                @Override
                public void run() {
                    BluetoothDevice device = devices.get(finalI);
                    final BluetoothGatt bluetoothGatt = device.connectGatt(ListaActivity.this, false, new CallBack());
                }
            }));
        }
        /*for(BluetoothDevice device : this.devices) { //ESTA ES LA PARTE WENA
            final BluetoothGatt bluetoothGatt = device.connectGatt(ListaActivity.this, false, new CallBack());
        }*/
        //BluetoothDevice device = this.devices.get(molinoActual);
        for(int i=0;i<this.devices.size();i++) {
            threads.get(i).start();
        }
        for(int i=0;i<this.devices.size();i++) {
            try {
                threads.get(i).join();
                Log.i("Thread:","Hilo con nombre :" + threads.get(i).getName() + " iniciado!");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.w("Threads: ","Threads Lanzados a saco");
    }


}

class CallBack extends BluetoothGattCallback {

    int operacionesEscritura = 0;
    int operacionesLectura = 0;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        String deviceAddress = gatt.getDevice().getAddress();
        UUID service = UUID.fromString("6e400001-c352-11e5-953d-0002a5d5c51b");
        UUID readCharacteristic = UUID.fromString("6e400003-c352-11e5-953d-0002a5d5c51b");
        UUID writeCharacteristic = UUID.fromString("6e400002-c352-11e5-953d-0002a5d5c51b");
        BluetoothGatt bluetoothGatt;

        if(status == BluetoothGatt.GATT_SUCCESS) {
            if(newState == BluetoothProfile.STATE_CONNECTED) {
                //Aqui la conexion al dispositivo BLE ha funcado
                int w = Log.w("BluetoothGATT", "ESTADO CONECTADO");
                bluetoothGatt = gatt;
                bluetoothGatt.discoverServices();
                /*new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        bluetoothGatt.discoverServices();
                    }
                });*/
                Log.w("BluetoothGatt","Buscando servicios");
            } else if(newState == BluetoothProfile.STATE_DISCONNECTED) {
                //Aqui el dispositivo se ha desconectado
                Log.w("BluetoothGATT", "Estado desconectado");
                gatt.close();
            }
        } else {
            Log.w("BluetoothGATT", "ERROR AL CONECTAR A DISPOSITIVO: " + gatt.getDevice().getAddress());
            gatt.close();
        }
        super.onConnectionStateChange(gatt, status, newState);
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        Log.w("Escribiendo", "Escribiendo en el dispositivo");
        if(status == BluetoothGatt.GATT_SUCCESS) {
            byte[] bytes = characteristic.getValue();
            Log.i("Valor leido: ",characteristic.getValue().toString());
            //this.resetDoubleDoses(gatt);
            if(this.operacionesEscritura == 1) {
                this.resetDoubleDoses(gatt);
                Log.e("BluetotGtatt:", "Escribiendo doubleDoses");
            } else if(this.operacionesEscritura == 2) {
                this.SingleDosesValue(gatt);
                Log.e("BluetotGtatt:", "Leyendo singleDoses");
            } else if(this.operacionesEscritura == 3) {
                this.DoubleDosesValue(gatt);
                Log.e("BluetotGtatt:", "Leyendo doubleDoses");
            } else {
                Log.e("BluetotGtatt:", "Escribiendo singledoses");
            }
        } else {
            Log.e("Error;:","Fatality");
        }
        super.onCharacteristicWrite(gatt, characteristic, status);
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {

        if(status == BluetoothGatt.GATT_SUCCESS) {
            Log.i("BluetoothGattCallback", "Scan finished caracteristic sfinished correctly");
            this.resetSingleDoses(gatt);
            //boolean si = gatt.writeCharacteristic(read);
        } else {
            Log.e("Error:", "Fatality");
        }

        //BluetoothGattService Bservice = gatt.getServices().get(2);//getService(UUID.fromString("6e400001-c352-11e5-953d-0002a5d5c51b"));
        //BluetoothGattCharacteristic write = Bservice.getCharacteristics().get(0);
        //BluetoothGattCharacteristic read = Bservice.getCharacteristics().get(0); //0->WRITE 1->READ
        //read.setValue(new byte[]{0x01, 0x47, (byte) 0xD0, 0x07, 0x20, (byte) 0xD5}); //HAY QUE COMPROBAR SI ESTO FUNCIONA
        //read.getDescriptors().get(0).setValue(new byte[]{0x01, 0x47, (byte) 0xD0, 0x07, 0x20, (byte) 0xD5});
        //write.setValue(new byte[]{0x01, 0x53, (byte) 0xD8, 0x07, 0x04, (byte) 0xFE, 0x03, 0x00, 0x00, (byte) 0x83}); //ESTO ESCRIBE BIEN 1024 EN SINGLEDOSES
        //write.setValue(new byte[]{0x01, 0x53, (byte) 0xD8, 0x07, 0x04, 0x00, 0x00, 0x00, 0x00, (byte) 0xF9}); //ESTO ESCRIBE 0 EN SINGLEDOSES
        /*if(!gatt.readCharacteristic(read)) { //READ CARACTERISTIC NO FUNCIONA POR ALGUNA RAZÓN
            Log.e("BluetoothGATT", "ERROR AL leer CARACTERISTICA");
        }*/
        /*if(!gatt.writeCharacteristic(write)) {
            Log.e("BluetoothGATT", "ERROR AL escribir CARACTERISTICA");
        }*/
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

        if(status == BluetoothGatt.GATT_SUCCESS) {
            Log.w("Leyendo", "Leyendo en el dispositivo");
        } else {
            Log.e("Leyendo", "Eror de lectuta");
        }
        super.onCharacteristicRead(gatt, characteristic, status);

    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        Log.w("Onchanges","Onchanges");
        super.onCharacteristicChanged(gatt, characteristic);
    }

    public CallBack() {
        super();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    int[] bytesToInts(byte [] in) {
        int[] out = new int[in.length];
        for(int i =0;i<in.length;i++) {
            out[i]=Byte.toUnsignedInt(in[i]);
        }

        return out;
    }

    void resetSingleDoses(BluetoothGatt gatt) {
        BluetoothGattService Bservice = gatt.getServices().get(2);//getService(UUID.fromString("6e400001-c352-11e5-953d-0002a5d5c51b"));
        BluetoothGattCharacteristic write = Bservice.getCharacteristics().get(0);
        //write.setValue(new byte[]{0x01, 0x53, (byte) 0xD8, 0x07, 0x04, 0x00, 0x00, 0x00, 0x00, (byte) 0xF9}); //ESTO ESCRIBE 0 EN SINGLEDOSES
        write.setValue(new byte[]{0x01, 0x53, (byte) 0xD8, 0x07, 0x04, 0x0A, 0x00, 0x00, 0x00, (byte) 0x65}); //ESTO ESCRIBE 10 EN SINGLEDOSES
        if(!gatt.writeCharacteristic(write)) {
            Log.e("BluetoothGATT", "Error al poner el valor de dosis simples a 0");
        } else {
            this.operacionesEscritura++;
        }
    }

    void resetDoubleDoses(BluetoothGatt gatt) {
        BluetoothGattService Bservice = gatt.getServices().get(2);//getService(UUID.fromString("6e400001-c352-11e5-953d-0002a5d5c51b"));
        BluetoothGattCharacteristic write = Bservice.getCharacteristics().get(0);
        //write.setValue(new byte[]{0x01, 0x53, (byte) 0xDC, 0x07, 0x04, 0x00, 0x00, 0x00, 0x00, (byte) 0x8C}); //ESTO ESCRIBE 0 EN SINGLEDOSES
        write.setValue(new byte[]{0x01, 0x53, (byte) 0xDC, 0x07, 0x04, 0x0A, 0x00, 0x00, 0x00, (byte) 0x10}); //ESTO ESCRIBE 10 EN SINGLEDOSES
        byte[] values = write.getValue();
        if(!gatt.writeCharacteristic(write)) {
            Log.e("BluetoothGATT", "Error al poner el valor de dosis dobles a 0");
        } else {
            this.operacionesEscritura++;
        }
    }

    void SingleDosesValue(BluetoothGatt gatt) {
        Log.i("Lectura:","Leyendo dosis simple");
        BluetoothGattService Bservice = gatt.getServices().get(2);//getService(UUID.fromString("6e400001-c352-11e5-953d-0002a5d5c51b"));
        BluetoothGattCharacteristic read = Bservice.getCharacteristics().get(1);
        read.setValue(new byte[]{0x01, 0x47, (byte) 0xD0, 0x07, 0x20, (byte) 0xD5}); //ESTO ESCRIBE 10 EN SINGLEDOSES
        BluetoothGattDescriptor desc = read.getDescriptors().get(0);
        desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        desc.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
        //desc.setValue(BluetoothGattDescriptor.PERMISSION_READ);
        gatt.setCharacteristicNotification(read, true);
        try {
            if(!gatt.readCharacteristic(read)) {
                Log.e("BluetoothGATT", "Error al poner el valor de dosis dobles a 0");
            } else {
                this.operacionesEscritura++;
            }
        }catch (Exception e) {
            Log.e("Error",e.toString());
        }
    }

    void DoubleDosesValue(BluetoothGatt gatt) {
        Log.i("Lectura:","Leyendo dosis doble");
    }



}