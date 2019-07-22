package com.example.a405;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.TextView;

import com.example.a405.HttpReq.HttpRequest;
import com.example.a405.HttpReq.HttpRequestTask;
import com.example.a405.HttpReq.HttpResponse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.sql.Time;
import java.util.UUID;

import static android.content.ContentValues.TAG;

public class BluetoothService {

    private boolean stop = false;
    private String FILE_NAME = "out.txt";
    private String rawData = "";
    private InputStream mmIn;
    private OutputStream mmOut;
    private BluetoothAdapter BT_Adapter;
    private BluetoothDevice device;
    private BluetoothSocket mmSocket;
    private TextView dataView = null;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private String BTAddr = "3C:71:BF:1D:5D:72";
    //private String BTAddr = "3C:71:BF:AA:FD:E6";
    //private String BTAddr = "3C:71:BF:1D:5D:AB";
    //private String BTAddr = "B4:E6:2D:C3:20:63";
    public boolean isBtConnected = false;

    File Path;

    private Activity activity;

    public BluetoothService(File pt, TextView data, Activity ct)
    {

        activity = ct;
        dataView = data;
        BT_Adapter = BluetoothAdapter.getDefaultAdapter();
        connectWristalker();
        Path = pt;
    }


    private void connectWristalker()
    {
        BluetoothSocket tmp= null;
        device = BT_Adapter.getRemoteDevice(BTAddr);
        try {
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            Method m = device.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
            tmp = (BluetoothSocket) m.invoke(device, 1);
        } catch (Exception e) {
            showError(e.getMessage());
            Log.e(TAG, "create() failed", e);
        }
        mmSocket = tmp;
        try {
            if(mmSocket != null) {
                mmSocket.connect();
                if(mmSocket.isConnected())
                    isBtConnected = true;
            }
            else
                return;
        } catch (IOException e) {
            showError("Could not connect to your WRISTALKER");
            e.printStackTrace();
        }
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        try {
            tmpIn = mmSocket.getInputStream();
            tmpOut = mmSocket.getOutputStream();
        } catch (IOException | NullPointerException e) {
            showError(e.getMessage());
            Log.e(TAG, "temp sockets not created", e);
        }
        mmIn = tmpIn;
        mmOut = tmpOut;
    }

    public void startService(String filename)
    {
        FILE_NAME = filename;
        stop = false;
        ConnectedThread connectedThread = new ConnectedThread(mmSocket);
        connectedThread.start();
    }

    private class ConnectedThread extends Thread {
        InputStream inputStream;
        int availableBytes = 0;

        public ConnectedThread(BluetoothSocket socket){
            InputStream temp=null;
            try{
                temp=socket.getInputStream();
            }catch (IOException e){
                showError(e.getMessage());
                e.printStackTrace();
            }
            //conecdiv = (TextView) findViewById(R.id.conecdiv);
            //conecdiv.setMovementMethod(new ScrollingMovementMethod());
            inputStream=temp;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        public void run() {
            String fullData;
            int counter = 0;

            int bytes;
            while (true){
                try{
                    availableBytes =inputStream.available();
                    byte[] buffer=new byte[availableBytes];
                    //mmOut.write("s*".getBytes());
                    rawData += receiveDataBT();          //SERVER  REQUEST HERE
                    //&& nextWorkTime < TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())
                    if(rawData.contains("}") )
                    {
                        fullData = rawData.substring(0,rawData.indexOf("}") + 1);
                        rawData = rawData.substring(rawData.indexOf("}") + 1 );
                        if (counter >= 2){
                            MainActivity.series.AddTimeValue(fullData);

                            // adım belkji ?

                            counter =0;
                        }else {
                            ++counter;
                        }

                        String[] dataPair = (fullData.substring(0,fullData.length()-1)).split(",");
                        //if(dataPair.length != 8)
                        //throw new Exception("data size != 8");
                        float[] allVal = new float[6];
                        for ( int i = 0; i < 6 ; i++) {
                            allVal[i] = Float.parseFloat(dataPair[i + 1].split(":")[1]);
                        }
                        Calendar currentTime = Calendar.getInstance();
                        int hour                = currentTime.get(Calendar.HOUR_OF_DAY) ;
                        int minute              = currentTime.get(Calendar.MINUTE)      ;
                        int second              = currentTime.get(Calendar.SECOND)      ;



                        new HttpRequestTask(
                                new HttpRequest("http://188.166.57.29:55432/hardware?input1="+allVal[0]+"&input2="+allVal[1]+"&input3="+allVal[2]+"&input4="+allVal[3]+"&input5="+allVal[4]+"&input6="+allVal[5]
                                        + "&time=2019-5-17%" + hour + ":" + minute + ":" + second, HttpRequest.POST ),//status
                                new HttpRequest.Handler() {
                                    @Override
                                    public void response(HttpResponse response) {
                                        if (response.code == 200) {

                                        }else {
                                            //err
                                        }
                                    }
                                }).execute();

                        save(fullData);
                    }
                    if(stop)
                    {break;}
                    //dataView.setText(rawData);
                    //save(rawData);
                }catch (Exception e){
                    showError(e.getMessage());
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    private void showError(final String message)
    {
        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                isBtConnected = false;
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("BLUETOOTH CONNECTION ERROR");
                builder.setMessage(message);
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }
    //*****************************************
    public boolean isExternalStorageWritable()
    {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state))
        {
            return true;
        }
        return false;
    }


    public void save(String text)
    {
        if(isExternalStorageWritable())
        {
            File textFile = new File(Path,FILE_NAME);
            if (!textFile.exists()) {
                try {
                    textFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            FileOutputStream fout = null;
            try
            {
                fout = new FileOutputStream(textFile,true);
                fout.write(text.getBytes());

                //Toast.makeText(this,"Saved " + "/" + FILE_NAME,Toast.LENGTH_LONG).show();
            }
            catch(FileNotFoundException ex)
            {
                ex.printStackTrace();
            }
            catch(IOException ex)
            {
                ex.printStackTrace();
            }
            finally
            {
                if (fout != null)
                {
                    try
                    {
                        fout.close();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    //*****************************************
    String receiveDataBT()
    {
        byte[] buffer = null;
        InputStream tmpIn;
        int bytes = 0;
        try {
            buffer = new byte[128];
            tmpIn = mmSocket.getInputStream();
            bytes = tmpIn.read(buffer);
            Log.e(TAG, "a");
        } catch (IOException e) {
            showError(e.getMessage());
            e.printStackTrace();
        }
        String readMessage = new String(buffer, 0, bytes);
        //Toast.makeText(MainActivity.this, "Data Received : " + readMessage, Toast.LENGTH_LONG).show();
        return readMessage;

    }

    public void stop(){stop = true;}

    void sendDataBT(String data){
        try {
            mmOut.write(data.getBytes());
        } catch (IOException e) {
            showError(e.getMessage());
            e.printStackTrace();
        }
    }
}
