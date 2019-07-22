package com.example.a405;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.example.a405.HttpReq.HttpRequest;
import com.example.a405.HttpReq.HttpRequestTask;
import com.example.a405.HttpReq.HttpResponse;
import com.example.a405.ui.main.SectionsPagerAdapter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    public static String wifiData;
    boolean is_Bluetooth = false;
    BluetoothService bluetoothService;
    private String  timeStamp;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    public static InUseGraph series;
    public static TextView stepMonitor;

    //--------------------------------------------------------------------------------------------//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView showData = findViewById(R.id.data);
        final Button power = findViewById(R.id.onoff);

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()){
            mBluetoothAdapter.enable();
        }
        //---------------------------|
        power.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                 bluetoothService = new BluetoothService(getExternalFilesDir((Environment.DIRECTORY_DOWNLOADS)),showData,MainActivity.this);

                timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
                if(bluetoothService.isBtConnected)
                    showDialog();
            }
        });
        //---------------------------|
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "DURDUR", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                bluetoothService.stop();
            }
        });

    }

    private void showDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Connection Type");
        builder.setMessage("Via Bluetooth or Via WIFI");

        builder.setPositiveButton("Via Bluetooth", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                //setContentView(R.layout.activity_main);
                is_Bluetooth = true;
                bluetoothService.sendDataBT("b*" + timeStamp + "*");
                //bluetoothService.sendDataBT("1*");
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                bluetoothService.startService("out.txt");
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Via WIFI", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {


                is_Bluetooth = false;
                bluetoothService.sendDataBT("w*" + timeStamp + "*");
                createSSIDPopUp("WIFI","Enter WIFI name");

                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();


        //---------------------------|

        //GraphView graph = findViewById(R.id.RealGraph);
        //graph.removeAllSeries();
        series = new InUseGraph();

        //-----------TEMP------------------------
        /*
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //your method
                //GraphView graph = (GraphView) findViewById(R.id.RealGraph);
                series.AddTimeValue(new Random().nextInt(61),new Random().nextInt(61),new Random().nextInt(61));
                //series.appendData(new DataPoint(time, new Random().nextInt(61)),false,1);
            }
        }, 0, 100);
        */
        //-----------TEMP------------------------
    }

    private void createSSIDPopUp(final String dialogTitle, final String popupTitle) {
        final EditText SSIDin;
        final EditText PASSin;
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.connect);
        dialog.setTitle(dialogTitle);
        TextView textSSID = dialog.findViewById(R.id.textSSID1);

        Button dialogButton = dialog.findViewById(R.id.okButton);
        SSIDin = dialog.findViewById(R.id.textName);
        PASSin = dialog.findViewById(R.id.textPassword);
        textSSID.setText(popupTitle);

        // if button is clicked, connect to the network;
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifiData = SSIDin.getText().toString();
                wifiData += "*";

                wifiData += PASSin.getText().toString();
                wifiData += "*";
                bluetoothService.sendDataBT(wifiData + "*");


                //finallyConnect(checkPassword, wifiSSID);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static void ShowMessage(String message, Activity activity){
    }

}
