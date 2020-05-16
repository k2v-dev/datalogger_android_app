package com.decalthon.helmet.stability.model.nineaxismodels;

import java.util.Map;
import java.util.TreeMap;

public class TabMetaData {
    public long start_ts;
    public long end_ts;
    public Map<Integer, SensorDataEntry> accChartData = new TreeMap<>();
    public Map<Integer, SensorDataEntry> gyrChartData = new TreeMap<>();
    public Map<Integer, SensorDataEntry> magChartData = new TreeMap<>();

    public void clear(){
        start_ts = 0;
        end_ts = 0;
        if(accChartData != null) {
            accChartData.clear();
           // accChartData = null;
        }
        if(gyrChartData != null) {
            gyrChartData.clear();
            //gyrChartData = null;
        }
        if(magChartData != null) {
            magChartData.clear();
            //magChartData = null;
        }
    }
}
