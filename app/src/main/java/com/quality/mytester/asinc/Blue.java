package com.quality.mytester.asinc;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.quality.mytester.ListaActivity;
import com.quality.mytester.R;

import java.util.UUID;



public class Blue extends AsyncTask<Object, String, BluetoothGatt> {
    final String TAG = "BlueAsyncTask";
    BluetoothGatt bluetoothGatt;
    Context ctx;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //Toast.makeText(this.ctx, "Lanzando pruebas", Toast.LENGTH_SHORT).show();

        //v.findViewById(R.id.btnTest).setVisibility(View.GONE);
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        View v = View.inflate(this.ctx, R.layout.activity_main, null);
        //TextView status = (TextView) v.findViewById(R.id.resultStatusTextView);
        //status.setText("Grinder BluetoothTest init..");
    }

    @Override
    protected void onPostExecute(BluetoothGatt bluetoothGatt) {
        super.onPostExecute(bluetoothGatt);
        Log.e(TAG, "Tarea terminada devolbemos BluetoothGatt");
        publishProgress("Proceso terminado para " + this.bluetoothGatt.getDevice().getName() + " con MAC: " + this.bluetoothGatt.getDevice().getAddress());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //Toast.makeText(this.ctx, "PRUEBA FINALIZADA", Toast.LENGTH_LONG).show();
    }

    @Override
    protected BluetoothGatt doInBackground(Object... object) {
        this.ctx = (Context)object[1];
        this.bluetoothGatt = ((BluetoothDevice)object[0]).connectGatt(((Context)object[1]), false, new BluetoothGattCallback() {
            int operacionesEscritura = 0;
            int operacionesLectura = 0;
            BluetoothGattService bService;
            BluetoothGattCharacteristic write;
            BluetoothGattCharacteristic read;
            BluetoothGattDescriptor readDescriptor;

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
                        //Aqui la conexion al dispositivo BLE ha funcado
                        int w = Log.w("BluetoothGATT", "ESTADO CONECTADO");
                        publishProgress("Conectado al GATT de " + gatt.getDevice().getName() + " con MAC: " + gatt.getDevice().getName());
                        gatt.discoverServices(); /*CONEXION CONOCIENDO LOS SERVICIOS */
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
                    publishProgress("Seteando valores de cafes simples");
                    if(this.operacionesEscritura == 1) {
                        this.resetDoubleDoses(gatt);
                        publishProgress("Seteando valores de cafes dobles");
                        //this.readSingleDosesValue(gatt);
                        Log.e("BluetotGtatt:", "Escribiendo doubleDoses");
                    } else if(this.operacionesEscritura == 2) {
                        //this.readSingleDosesValue(gatt);
                        gatt.close();
                        Log.e("BluetotGtatt:", "leyendo SingleDoses");
                    }
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
                    this.bService = gatt.getServices().get(2);
                    this.write = this.bService.getCharacteristics().get(0);
                    this.read = this.bService.getCharacteristics().get(1);
                    this.readDescriptor = this.read.getDescriptors().get(0);

                    this.resetSingleDoses(gatt);


                    //boolean si = gatt.writeCharacteristic(read);
                } else {
                    Log.e("Error:", "Fatality");
                }
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

            @RequiresApi(api = Build.VERSION_CODES.O)
            int[] bytesToInts(byte [] in) {
                int[] out = new int[in.length];
                for(int i =0;i<in.length;i++) {
                    out[i]=Byte.toUnsignedInt(in[i]);
                }

                return out;
            }

            void resetSingleDoses(BluetoothGatt gatt) {
                this.write.setValue(new byte[]{0x01, 0x53, (byte) 0xD8, 0x07, 0x04, 0x00, 0x00, 0x00, 0x00, (byte) 0xF9}); //ESTO ESCRIBE 0 EN SINGLEDOSES
                //this.write.setValue(new byte[]{0x01, 0x53, (byte) 0xD8, 0x07, 0x04, 0x0A, 0x00, 0x00, 0x00, (byte) 0x65}); //ESTO ESCRIBE 10 EN SINGLEDOSES
                this.write.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                if(gatt.setCharacteristicNotification(this.write, true)) {
                    Log.i("Write","Puesto a tru");
                }
                if(gatt.setCharacteristicNotification(this.read, true)) {
                    Log.i("Write","Puesto a tru");
                }
                //gatt.writeDescriptor(Bservice.getCharacteristics().get(1).getDescriptors().get(0));
                this.readDescriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
                this.readDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                if(!gatt.writeCharacteristic(this.write)) {
                    Log.e("BluetoothGATT", "Error al poner el valor de dosis simples a 0");
                } else {
                    this.operacionesEscritura++;
                }
            }

            void resetDoubleDoses(BluetoothGatt gatt) {
                //BluetoothGattService Bservice = gatt.getServices().get(2);//getService(UUID.fromString("6e400001-c352-11e5-953d-0002a5d5c51b"));
                //BluetoothGattCharacteristic write = Bservice.getCharacteristics().get(0);
                this.write.setValue(new byte[]{0x01, 0x53, (byte) 0xDC, 0x07, 0x04, 0x00, 0x00, 0x00, 0x00, (byte) 0x8C}); //ESTO ESCRIBE 0 EN SINGLEDOSES
                //this.write.setValue(new byte[]{0x01, 0x53, (byte) 0xDC, 0x07, 0x04, 0x0A, 0x00, 0x00, 0x00, (byte) 0x10}); //ESTO ESCRIBE 10 EN SINGLEDOSES
                //byte[] values = write.getValue();
                gatt.setCharacteristicNotification(write, true);
                if(!gatt.writeCharacteristic(write)) {
                    Log.e("BluetoothGATT", "Error al poner el valor de dosis dobles a 0");
                } else {
                    this.operacionesEscritura++;
                }
            }

            void readSingleDosesValue(BluetoothGatt gatt) {
                //BluetoothGattService Bservice = gatt.getServices().get(2);//getService(UUID.fromString("6e400001-c352-11e5-953d-0002a5d5c51b"));
                //BluetoothGattCharacteristic read = Bservice.getCharacteristics().get(1);
                //write.setValue(new byte[]{0x01, 0x53, (byte) 0xDC, 0x07, 0x04, 0x00, 0x00, 0x00, 0x00, (byte) 0x8C}); //ESTO ESCRIBE 0 EN SINGLEDOSES
                //read.setValue(new byte[]{0x01, 0x53, (byte) 0xDC, 0x07, 0x04, 0x0A, 0x00, 0x00, 0x00, (byte) 0x10}); //ESTO ESCRIBE 10 EN SINGLEDOSES
                //read.getDescriptors().get(0).setValue(new byte[]{0x01, 0x47, (byte) 0xD0, 0x07,0x20, (byte) 0xD5});
                //byte[] values = read.getValue();
                if(!gatt.readCharacteristic(this.read)) {
                    Log.e("BluetoothGATT", "Error al poner el valor de dosis dobles a 0");
                } else {
                    this.operacionesEscritura++;
                }
            }

            @Override
            public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                if(status == BluetoothGatt.GATT_SUCCESS) {
                    Log.w("Descriptro","Leyendo");
                }
                super.onDescriptorRead(gatt, descriptor, status);
            }

            @Override
            public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                if(descriptor.getCharacteristic() == this.read) {
                    if(status == BluetoothGatt.GATT_SUCCESS) {
                        Log.i("WriteDescriptor","Okay");
                    }
                }
                super.onDescriptorWrite(gatt, descriptor, status);
            }

        });
        return this.bluetoothGatt;
    }
}
