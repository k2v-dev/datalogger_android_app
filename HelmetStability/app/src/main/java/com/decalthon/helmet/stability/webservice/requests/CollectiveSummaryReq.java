package com.decalthon.helmet.stability.webservice.requests;

import java.util.ArrayList;
import java.util.List;

public class CollectiveSummaryReq {
    public long total_sessions = 0;
    public long total_size = 0;
    public long total_duration = 0;
    public List<Long> activity_types = new ArrayList<>();
}
