package com.decalthon.helmet.stability.model.indoor_timeline;

import com.decalthon.helmet.stability.database.entities.MarkerData;

public class IndoorMarker {
    public MarkerViewType markerViewType;
    public long timestamp;
    public MarkerData markerData = null;

    public IndoorMarker(MarkerViewType markerViewType, long timestamp) {
        this.markerViewType = markerViewType;
        this.timestamp = timestamp;
        markerData = null;
    }

    public IndoorMarker(MarkerViewType markerViewType, long timestamp, MarkerData markerData) {
        this.markerViewType = markerViewType;
        this.timestamp = timestamp;
        this.markerData = markerData;
    }
}
