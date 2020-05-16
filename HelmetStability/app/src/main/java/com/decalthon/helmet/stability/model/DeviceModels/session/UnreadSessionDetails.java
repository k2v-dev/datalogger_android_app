package com.decalthon.helmet.stability.model.devicemodels.session;

import com.decalthon.helmet.stability.database.entities.SessionSummary;

import java.util.ArrayList;
import java.util.List;

public class UnreadSessionDetails {
    int num_of_session; // Number of unread session data
    List<SessionSummary> sessionSummaryList =  new ArrayList<>();
}
