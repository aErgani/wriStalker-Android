package com.example.a405;

import com.example.a405.Frag.FragReal;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

class rawData{
    public float gyroX;
    public float gyroY;
    public float gyroZ;
    public float accX;
    public float accY;
    public float accZ;

    rawData(float gx,float gy, float gz, float ax, float ay, float az)
    {
        gyroX = gx;
        gyroY = gy;
        gyroZ = gz;
        accX = ax;
        accY = ay;
        accZ = az;
    }
}

public class InUseGraph {
    boolean needPositive = true;
    GraphView g;
    public static int stepCount = 0;
    public static int status = 3;
    int dataCount = 0;
    float totalAccX = (float) 0.0;
    rawData array[] = new rawData[20];

    final int maxCount = 200;
    int count = 0;

    LineGraphSeries<DataPoint> seriesX;
    LineGraphSeries<DataPoint> seriesY;
    LineGraphSeries<DataPoint> seriesZ;
    float time = 1;

    void  init(){
        DataPoint[] datX = new  DataPoint[50];
        DataPoint[] datY = new  DataPoint[50];
        DataPoint[] datZ = new  DataPoint[50];

        for (int i = 0; i < 50;++i){
            datX[i] = new DataPoint((i-50) / 50 + time,0);
            datY[i] = new DataPoint((i-50) / 50 + time,0);
            datZ[i] = new DataPoint((i-50) / 50 + time,0);
        }

        seriesX = new LineGraphSeries<DataPoint>(datX);
        seriesY = new LineGraphSeries<DataPoint>(datY);
        seriesZ = new LineGraphSeries<DataPoint>(datZ);

        seriesX.setDrawDataPoints(false);
        seriesX.setAnimated(false);
        g.addSeries(seriesX);
        seriesX.setColor(-10746548);

        seriesY.setDrawDataPoints(false);
        seriesX.setAnimated(false);
        g.addSeries(seriesY);
        seriesY.setColor(-14746548);

        seriesZ.setDrawDataPoints(false);
        seriesX.setAnimated(false);
        g.addSeries(seriesZ);
        seriesZ.setColor(-16746548);
    }

    public void AddTimeValue(float x,float y,float z){
        seriesX.appendData(new DataPoint(time, x),false,0);
        seriesY.appendData(new DataPoint(time, y),false,0);
        seriesZ.appendData(new DataPoint(time, z),false,0);

        ++count;

        if (count > maxCount){
            g.removeAllSeries();
            init();
            count = 0;
        }

        time+=1;
    }

    public void step(float accZ)
    {
        if(accZ >= 100.0 && needPositive)
        {
            stepCount++;
            needPositive = false;
        }
        if(accZ <= -100.0 && !needPositive)
        {
            stepCount++;
            needPositive = true;
        }
    }

    boolean check(rawData vars[]) {
        for (int i = 0; i < 20; ++i)
        {
            if (vars[i].accZ >= 100 || vars[i].accZ <= -100)
            {
                return true;
            }
        }

        return false;
    }

    public void changeStatus()
    {
        float mean []= new float[6];
        for(int i = 0; i < 20; i++)
        {
            mean[0] += array[i].gyroX;
            mean[1] += array[i].gyroY;
            mean[2] += array[i].gyroZ;
            mean[3] += array[i].accX;
            mean[4] += array[i].accY;
            mean[5] += array[i].accZ;
        }
        for (int i = 0;i < 6; i++)
        {
            mean[i] = mean[i] / 20;
        }


        if(mean[1] > -700  && !check(array)) {//sit
            status = 2;
        }
        else if(mean[1] <= -700 && !check(array)) { //stand
            status = 3;
        }
        else if (mean[1] >= 0 && check(array)) { //walk
            status = 1;
        }
        else if (mean[1] < 0 && check(array)) { //jog
            status = 5;
        }
        else {
            status = 3;
        }
    }

    public void wait20(float data[])
    {
        if(dataCount >= 20)
        {
            dataCount = 0;
            changeStatus();
            array = new rawData[20];
        }
        array[dataCount] = new rawData(data[0],data[1],data[2],data[3],data[4],data[5]);
        dataCount++;
    }

    public void AddTimeValue(String Data) throws Exception {
        String[] dataPair = (Data.substring(0,Data.length()-1)).split(",");
        //if(dataPair.length != 8)
            //throw new Exception("data size != 8");
        float[] allVal = new float[6];
        for ( int i = 0; i < 6 ; i++) {
            allVal[i] = Float.parseFloat(dataPair[i + 1].split(":")[1]); // i+1 gyro i + 4 ivme
        }
        wait20(allVal);
        step(allVal[5]);
        //AddTimeValue(allVal[0],allVal[1],allVal[2]);
    }


}
