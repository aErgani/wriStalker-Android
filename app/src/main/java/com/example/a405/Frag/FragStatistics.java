package com.example.a405.Frag;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.example.a405.HttpReq.HttpRequest;
import com.example.a405.HttpReq.HttpRequestTask;
import com.example.a405.HttpReq.HttpResponse;
import com.example.a405.MainActivity;
import com.example.a405.R;

public class FragStatistics extends Fragment implements View.OnClickListener {
    TextView startText;
    TextView endText;

    TextView t1,t2,t3,t4,t5,t6;

    private DatePickerDialog.OnDateSetListener mDateSetListenerStart;
    private DatePickerDialog.OnDateSetListener mDateSetListenerEnd;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fraagstatistics_layout,container,false);

        Button startTime = view.findViewById(R.id.startTime);
        final Button endTime = view.findViewById(R.id.endTime);
        final Button getData = view.findViewById(R.id.GetData);
        startText = view.findViewById(R.id.StartText);
        endText = view.findViewById(R.id.EndText);

        t1 = view.findViewById(R.id.text_1);
        t2 = view.findViewById(R.id.text_2);
        t3 = view.findViewById(R.id.text_3);
        t4 = view.findViewById(R.id.text_4);
        t5 = view.findViewById(R.id.text_5);
        t6 = view.findViewById(R.id.text_6);

        startTime.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        view.getContext(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListenerStart,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListenerStart = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = month + "/" + day + "/" + year;
                startText.setText(date);
            }
        };

        endTime.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        view.getContext(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListenerEnd,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListenerEnd = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = month + "/" + day + "/" + year;
                endText.setText(date);
            }
        };

        getData.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                if (!startText.getText().toString().contains("/") || !endText.getText().toString().contains("/")){
                    return;
                }
                String startD,startM,startY,endD,endM,endY;
                String startDate,endDate;
                String[] tmp;

                tmp = startText.getText().toString().split("/");
                startY = tmp[2];
                startM = tmp[0];
                startD = tmp[1];

                tmp = endText.getText().toString().split("/");
                startDate = startY+"-"+startM+"-"+startD+" 00:00:00";
                endDate = tmp[2]+"-"+tmp[0]+"-"+tmp[1]+" 00:00:00";
                try{
                    //URL url= new URL("localhost::8080");
                    //HttpURLConnection con= (HttpURLConnection) url.openConnection();
                    //write additional POST data to url.getOutputStream() if you wanna use POST method

                    new HttpRequestTask(
                            new HttpRequest("http://188.166.57.29:55432/statics"+"?"+"start="+startDate+"&end="+endDate, HttpRequest.GET ),//status
                            new HttpRequest.Handler() {
                                @Override
                                public void response(HttpResponse response) {
                                    if (response.code == 200) {
                                        String[] tmp = response.body.split("/");
                                        t1.setText(tmp[5]);
                                        t2.setText(tmp[1]);
                                        t3.setText(tmp[2]);
                                        t4.setText(tmp[3]);
                                        t5.setText(tmp[4]);
                                        t6.setText(tmp[0]);
                                    }else {
                                        // err
                                    }
                                }
                            }).execute();

                }catch (Exception ex){
                    // exp
                }
            }
        });

        return view;
    }

    @Override
    public void onClick(View v) {
        startText.setText("yooolooooo");
    }
}
