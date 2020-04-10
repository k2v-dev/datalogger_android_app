package com.decalthon.helmet.stability.model.DeviceModels.session;

import com.decalthon.helmet.stability.DB.Entities.SessionSummary;

import java.util.ArrayList;
import java.util.List;

public class UnreadSessionDetails {
    int num_of_session; // Number of unread session data
    List<SessionSummary> sessionSummaryList =  new ArrayList<>();
}
