package com.quality.mytester;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ThemedSpinnerAdapter;
import android.widget.Toast;

import com.ble.ellamma.bleand.Ble;
import com.quality.mytester.Adapters.MolinoAdapter;
import com.quality.mytester.Modelos.Info;
import com.quality.mytester.Modelos.Molino;
import com.quality.mytester.Receivers.BlueReceiver;
import com.quality.mytester.asinc.Blue;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

public class ListaActivity extends AppCompatActivity {
    //private List<Molino> molinos;
    private List<BluetoothDevice> molinos;
    private List<BluetoothDevice> devices;
    private ProgressDialog progres;
    private ListView listView;
    private TextView resultStatusTextView;
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
        this.progres = new ProgressDialog(ListaActivity.this);
        //this.resultStatusTextView = (TextView) findViewById(R.id.resultStatusTextView);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
        //BluetoothDevice device = this.devices.get(0);
        //final BluetoothGatt bluetoothGatt = device.connectGatt(ListaActivity.this, false, new CallBack());
        for(int i =0;i<this.devices.size();i++) {
            new Blue().execute(this.devices.get(i));
        }

        Log.w("Threads: ","Threads Lanzados a saco");
        /*BlueReceiver rec = new BlueReceiver();
        IntentFilter filter = new IntentFilter("CONNECTED");
        filter.addAction("ACTION_DATA_AVAIABLE");
        registerReceiver(rec, filter);*/
    }

    private class Blue extends AsyncTask<BluetoothDevice, String, BluetoothGatt> {

        final String TAG = "BlueAsyncTask";
        BluetoothGatt bluetoothGatt;

        @Override
        protected void onPreExecute() {
            
            //spinner.setVisibility(View.VISIBLE);

            super.onPreExecute();

            //Toast.makeText(ListaActivity.this, "Lanzando pruebas", Toast.LENGTH_SHORT).show();
            //resultStatusTextView.setText("Bluetooth Test Init...");
        }

        @Override
        protected void onProgressUpdate(String... values) {
            //resultStatusTextView.setText(values[0]);
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(BluetoothGatt bluetoothGatt) {
            spinner.setVisibility(View.GONE);
            super.onPostExecute(bluetoothGatt);
            Log.e(TAG, "Tarea terminada devolbemos BluetoothGatt");
            //leDeviceListAdapter.notifyDataSetChanged();
            //Toast.makeText(ListaActivity.this, "PRUEBA FINALIZADA", Toast.LENGTH_LONG).show();
        }

        @Override
        protected BluetoothGatt doInBackground(BluetoothDevice... bluetoothDevices) {
            this.bluetoothGatt = bluetoothDevices[0].connectGatt(ListaActivity.this, false, new BluetoothGattCallback() {
                int operacionesEscritura = 0;
                int operacionesLectura = 0;
                String singleDosesValue;
                String doubleDosesValue;
                private static final String TAG = "BluetoothLE: ";
                BluetoothGattService bService;
                BluetoothGattCharacteristic write;
                BluetoothGattCharacteristic read;
                BluetoothGattDescriptor readDescriptor;
                private byte[] escribirDiezSingleDoses;
                private byte[] escribirDiezDoubleDoses;
                private byte[] leerSingleDoses;
                private byte[] leerDoubleDoses;
                private Queue<Runnable> commandQueque; //LISTADO DONDE GUARDAMOS LOS COMANDOS BLUETOOTH
                private boolean commandQuequeBusy; //BOLEANO QUE DETERMINA SI ESTA OCUPADO EL COMANDO


                private static final String ACTION_DATA_AVAIABLE = "ACTION_DATA_AVAIABLE";
                private static final String CONNECTED = "CONNECTED";
                private static final String FINALIZE = "FINALIZE";

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
                            Log.w(TAG, "ESTADO CONECTADO");
                            publishProgress("GATT conected to " + gatt.getDevice().getName());
                            /*BUSCAMOS LOS SERVICIOS DEL DISPOSITIVO*/
                            this.commandQuequeBusy = false;
                            this.commandQueque = new LinkedList<Runnable>();
                            gatt.discoverServices();
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



                @Override
                public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                    Log.w("Escribiendo", "Escribiendo en el dispositivo");
                    if(status == BluetoothGatt.GATT_SUCCESS) {
                        completedCommand();
                    } else {
                        Log.e("Error;:","Fatality");
                    }
                    super.onCharacteristicWrite(gatt, characteristic, status);
                    //gatt.close();

                }

                @Override
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {

                    if(status == BluetoothGatt.GATT_SUCCESS) {
                        Log.i("BluetoothGattCallback", "Scan finished caracteristic sfinished correctly");
                        //this.resetSingleDoses(gatt);
                        /*CUANDO DESCUBRO EL SERVICIO LO GUARDO JUNTO CON SUS CARACTERISTICAS DE LECTURA/ESCRITURA Y EL DESCRIPTOR QUE USARE MAS ADELANTE*/
                        this.bService = gatt.getServices().get(2);
                        this.write = this.bService.getCharacteristics().get(0);
                        this.read = this.bService.getCharacteristics().get(1);
                        this.readDescriptor = this.read.getDescriptors().get(0);
                        this.leerSingleDoses = new byte[]{0x01, 0x47, (byte) 0xD0, 0x07, 0x20, (byte) 0xD5};
                        this.escribirDiezDoubleDoses = new byte[]{0x01, 0x53, (byte) 0xDC, 0x07, 0x04, 0x0A, 0x00, 0x00, 0x00, (byte) 0x10};
                        this.escribirDiezSingleDoses = new byte[]{0x01, 0x53, (byte) 0xD8, 0x07, 0x04, 0x0A, 0x00, 0x00, 0x00, (byte) 0x65};
                        /*CONFIGURO EL DESCRIPTOR PARA QUE ADEMINA NOTIFICACIONES Y INDICACIONES*/
                        this.readDescriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
                        this.readDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        /*ESCRIBO LAS OPCIONES AL DESCRIPTOR OJO QUE ESTOS METODOS SON ASYNCRONOS Y TIENEN QUE ESTAR ENCOLADOS*/
                        notoficationsConfigure(gatt, this.read);
                        configureDescriptor(gatt, this.readDescriptor);
                        /*LANZO LOS 4 METODOS PARA LAS PRUEBAS PRIMERO LEERA LOS VALORES DE DOSIS SIMPLE Y DOBLE Y LUEGO LOS DEJARA EN 10*/
                        obtainSingleDoses(gatt, this.write);
                        //readValue(gatt, this.read);
                        /*obtainDoubleDoses(BluetoothGatt gatt);
                        modifySingleDoses(BluetoothGatt gatt);
                        modifyDoubleDoses(BluetoothGatt gatt);*/

                    } else {
                        Log.e(TAG, "Fallo en onServiceDiscovered");
                    }
                    Log.e(TAG, "Fin???");
                }

                @Override
                public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

                    if(status == BluetoothGatt.GATT_SUCCESS) {
                        Log.w("Leyendo", "Leyendo en el dispositivo");
                        completedCommand();
                    } else {
                        Log.e("Leyendo", "Eror de lectuta");
                        completedCommand();
                    }
                    super.onCharacteristicRead(gatt, characteristic, status);

                }

                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                    Log.w(TAG,"NOTIFICADO EN LA CARACTERISTICA DE LECTURA");
                    byte[] traza = this.read.getValue();
                    String[] hexValues = new String[traza.length];
                    for(int i=0;i<traza.length;i++) {
                        if(traza[i] < 0) {
                            hexValues[i] = Integer.toHexString(Byte.toUnsignedInt(traza[i]));
                        } else {
                            hexValues[i] = Integer.toHexString(traza[i]);
                        }
                    }
                    /*AQUI YA SE PUEDE EDITAR LA INTERFACE PARA QUE SALGA EL NUMERO DE DOSIS SIMPLES Y DOSIS DOBLES DE LOS MOLINOS*/
                    Info infoDevice = new Info();
                    infoDevice.setSingleDoses((int) traza[10]);
                    infoDevice.setDoubleDoses((int) traza[14]);
                    this.singleDosesValue = Integer.toString(infoDevice.getSingleDoses());
                    this.doubleDosesValue = Integer.toString(infoDevice.getDoubleDoses());
                    actualizaDispositivo(Integer.toString(infoDevice.getSingleDoses()), Integer.toString(infoDevice.getDoubleDoses()));
                    /**************************************************************************************************************/
                    //infoDevice.setSingleDoses();
                    //int[] respuesta = bytesToInts(this.read.getValue());
                    super.onCharacteristicChanged(gatt, characteristic);
                }


                @Override
                public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                    if(status == BluetoothGatt.GATT_SUCCESS) {
                        Log.w("Descriptro","Leyendo");
                        completedCommand();
                    }
                    super.onDescriptorRead(gatt, descriptor, status);
                }

                @Override
                public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                    if(descriptor.getCharacteristic() == this.read) {
                        if(status == BluetoothGatt.GATT_SUCCESS) {
                            Log.i("WriteDescriptor","Okay");
                            completedCommand();
                        }
                    }
                    super.onDescriptorWrite(gatt, descriptor, status);
                }

                /********************************************************************METODOS NUEVOS******************************************************************/
                void configureDescriptor(BluetoothGatt gatt, BluetoothGattDescriptor descriptor) {
                    //gatt.writeDescriptor(this.readDescriptor);
                    boolean result = this.commandQueque.add(new Runnable() {
                        @Override
                        public void run() {
                           if(gatt.writeDescriptor(descriptor)) {
                               Log.i(TAG, "WriteDescription operation successfully");
                           } else {
                               Log.e(TAG,"Error with writeDescriptor");
                               completedCommand();
                           }
                        }
                    });
                    if(result) {
                        nextCommand();
                    } else {
                        Log.e(TAG, "Error al encolar el comando WriteDescriptor");
                    }
                }

                void readDescriptor(BluetoothGatt gatt, BluetoothGattDescriptor descriptor) {
                    boolean result = this.commandQueque.add(new Runnable() {
                        @Override
                        public void run() {
                            if(gatt.readDescriptor(descriptor)) {
                                Log.i(TAG, "WriteDescription operation successfully");
                            } else {
                                Log.e(TAG,"Error with readDescriptor");
                                completedCommand();
                            }
                        }
                    });
                    if(result) {
                        nextCommand();
                    } else {
                        Log.e(TAG, "Error al encolar el comando readDescriptor");
                    }
                }

                void obtainSingleDoses(BluetoothGatt gatt, BluetoothGattCharacteristic write) {
                    write.setValue(this.leerSingleDoses);
                    boolean resultWrite = this.commandQueque.add(new Runnable() {
                        @Override
                        public void run() {
                            if(gatt.writeCharacteristic(write)) {
                                Log.i(TAG, "Write 10 coffes to SingleDoses Correctly");
                            } else {
                                Log.e(TAG, "Write characteristic error on SIngleDoses");
                                completedCommand();
                            }
                        }
                    });
                    if(resultWrite) {
                        nextCommand();
                    } else {
                        Log.e(TAG, "Error al encolar el comando WriteCharacteristic");
                    }
                }

                void readValue(BluetoothGatt gatt, BluetoothGattCharacteristic read) {
                    byte[] bytes = this.read.getValue();
                }

                void notoficationsConfigure(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                    boolean resultNotifi = this.commandQueque.add(new Runnable() {
                        @Override
                        public void run() {
                            if(gatt.setCharacteristicNotification(characteristic, true)) {
                                Log.i(TAG, "Set true on notifications Correctly");
                                completedCommand();
                            } else {
                                Log.e(TAG, "Set true on notifications error");
                                completedCommand();
                            }
                        }
                    });
                    if(resultNotifi) {
                        nextCommand();
                    } else {
                        Log.e(TAG, "Error al encolar el comando WriteCharacteristic");
                    }
                }

                private void completedCommand() {
                    //isRetrying = false;
                    this.commandQueque.poll();
                    this.commandQuequeBusy = false;
                    nextCommand();
                }

                private void nextCommand() {
                    if(commandQuequeBusy) { //AQUI NOSE SI HACER UN WAIT()
                        return;
                    }
                    if(this.commandQueque.size()>0) {
                        final Runnable command = this.commandQueque.peek();
                        commandQuequeBusy = true;

                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    command.run();
                                }catch (Exception e) {
                                    Log.e(TAG, e.toString());
                                }
                            }
                        });
                    }

                }

                void actualizaDispositivo(String singleDoses, String doubleDoses) {
                    String MAC = bluetoothDevices[0].getAddress();
                    View vista;
                    for(int i=0;i<leDeviceListAdapter.getCount();i++) {
                        if(((BluetoothDevice) leDeviceListAdapter.getItem(i)).getAddress().equals(MAC)) {
                            leDeviceListAdapter.setSingleDoses(singleDoses);
                            leDeviceListAdapter.setDoubleDoses(doubleDoses);
                            leDeviceListAdapter.setCorrect(true);
                        }
                    }

                    //leDeviceListAdapter.notifyDataSetChanged();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            leDeviceListAdapter.notifyDataSetChanged();
                        }
                    });
                    Log.i(TAG, "Editamos vista");
                }
                /******************************************************************************************************************************/


            });
            return this.bluetoothGatt;
        }
    }

}


