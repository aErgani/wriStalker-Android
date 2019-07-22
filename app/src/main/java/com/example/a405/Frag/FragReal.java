package com.example.a405.Frag;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.a405.HttpReq.HttpRequest;
import com.example.a405.HttpReq.HttpRequestTask;
import com.example.a405.HttpReq.HttpResponse;
import com.example.a405.InUseGraph;
import com.example.a405.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class FragReal extends Fragment {
    ImageView img;
    TextView dat;
    private TextView stepMonitor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragreal_layout,container,false);

        img = view.findViewById(R.id.statusImage);
        dat = view.findViewById(R.id.data);
        stepMonitor = view.findViewById(R.id.StepNum);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                new HttpRequestTask(
                        new HttpRequest("http://188.166.57.29:55432/status?val="+InUseGraph.status, HttpRequest.GET ),//status
                        new HttpRequest.Handler() {
                            @Override
                            public void response(HttpResponse response) {
                                stepMonitor.setText("" + InUseGraph.stepCount);
                                    stepMonitor.setText("" + InUseGraph.stepCount);
                                    switch (InUseGraph.status){
                                        case 0:
                                            img.setImageResource(R.drawable.down);
                                            dat.setText("GOING DOWNSTAIRS");
                                            break;
                                        case 1:
                                            img.setImageResource(R.drawable.run);
                                            dat.setText("JOGGING");
                                            break;
                                        case 2:
                                            img.setImageResource(R.drawable.sit);
                                            dat.setText("SITTING");
                                            break;
                                        case 3:
                                            img.setImageResource(R.drawable.stand);
                                            dat.setText("STANDING");
                                            break;
                                        case 4:
                                            img.setImageResource(R.drawable.up);
                                            dat.setText("GOING UPSTAIRS");
                                            break;
                                        case 5:
                                            img.setImageResource(R.drawable.walk);
                                            dat.setText("WALKING");
                                            break;
                                    }


                            }
                        }).execute();

            }
        }, 0, 1000);

        return view;
    }
}
