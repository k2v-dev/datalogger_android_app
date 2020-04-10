package com.decalthon.helmet.stability.model.DeviceModels.session;

import com.decalthon.helmet.stability.DB.Entities.SessionSummary;

import java.util.Date;

public class SensorDataModel {
//    1st byte: b1  - Output Packet Header 0xAA
//    Next 4 bytes : b2  b3  b4  b5 – Packet Number in unsigned integer format
//    Next 4 bytes : b6  b7  b8  b9  -Time Stamp in the following format , b6 –Hour in 24 hour format (unsigned byte) , b7 –Minute (unsigned byte) , b8 –Second (unsigned byte), b9 –Milisecond  (unsigned byte) scale factor 10.
//    Next 2 bytes : b10  b11  - Ax of the 9 axis Imu of Dev1 (signed short format) – Scale factor 0.00718092 – Unit m/s2 .
//            Next 2 bytes : b12  b13  - Ay of the 9 axis Imu of Dev1 (signed short format) – Scale factor 0.00718092 – Unit m/s2
//    Next 2 bytes : b14  b15  - Az of the 9 axis Imu of Dev1 (signed short format) – Scale factor 0.00718092 – Unit m/s2
//    Next 2 bytes : b16  b17  - Gx of the 9 axis Imu of Dev1 (signed short format) – Scale factor 0.07 – Unit deg/sec.
//            Next 2 bytes : b18  b19  - Gy of the 9 axis Imu of Dev1 (signed short format) – Scale factor 0.07 – Unit deg/sec.
//            Next 2 bytes : b20  b21  - Gz of the 9 axis Imu of Dev1 (signed short format) – Scale factor 0.07 – Unit deg/sec.
//            Next 2 bytes : b22  b23  - Mx of the 9 axis Imu of Dev1 (signed short format) – Scale factor 0.58 – Unit miligauss.
//    Next 2 bytes : b24  b25  - My of the 9 axis Imu of Dev1 (signed short format) – Scale factor 0.58 – Unit miligauss.
//    Next 2 bytes : b26  b27  - Mz of the 9 axis Imu of Dev1 (signed short format) – Scale factor 0.58 – Unit miligauss.
//    Next 2 bytes : b28  b29  - Ax of the 3 axis Imu of Dev1 (signed short format) – Scale factor 7.6518 – Unit m/s2 .
//    Next 2 bytes : b30  b31  - Ay of the 3 axis Imu of Dev1 (signed short format) – Scale factor 7.6518 – Unit m/s2 .
//    Next 2 bytes : b32  b33  - Az of the 3 axis Imu of Dev1 (signed short format) – Scale factor 7.6518 – Unit m/s2 .
//    Next  byte : b34  - Latitude  in unsigned byte format.
//    Next  byte : b35  - Longitude  in unsigned byte format.
//    Next  4 bytes : b36, b37,   b38,   b39,     -Reserved for GPS auxiliary data. (As of now, might change in future).
//    Next 6 bytes – b40  b41  b42  b43 b44  b45  Heart rate and timing values. (As of now, might change in future).
//    Next byte: b46  -GPS Tag Type.
//            Next byte: b47  - GPS Tagger.
//    Next 2 bytes : b48  b49  - Ax of the 9 axis Imu of Dev2 (signed short format) – Scale factor 0.00718092 – Unit m/s2 .
//            Next 2 bytes : b50  b51  - Ay of the 9 axis Imu of Dev2 (signed short format) – Scale factor 0.00718092 – Unit m/s2
//    Next 2 bytes : b52  b53  - Az of the 9 axis Imu of Dev2 (signed short format) – Scale factor 0.00718092 – Unit m/s2
//    Next 2 bytes : b54  b55  - Gx of the 9 axis Imu of Dev2 (signed short format) – Scale factor 0.07 – Unit deg/sec.
//            Next 2 bytes : b56  b57  - Gy of the 9 axis Imu of Dev2 (signed short format) – Scale factor 0.07 – Unit deg/sec.
//            Next 2 bytes : b58  b59  - Gz of the 9 axis Imu of Dev2 (signed short format) – Scale factor 0.07 – Unit deg/sec.
//            Next 2 bytes : b60  b61  - Mx of the 9 axis Imu of Dev2 (signed short format) – Scale factor 0.58 – Unit miligauss.
//    Next 2 bytes : b62  b63  - My of the 9 axis Imu of Dev2 (signed short format) – Scale factor 0.58 – Unit miligauss.
//    Next 2 bytes : b64  b65  - Mz of the 9 axis Imu of Dev2 (signed short format) – Scale factor 0.58 – Unit miligauss.
//    Next 2 bytes : b66  b67  - Ax of the 3 axis Imu of Dev2 (signed short format) – Scale factor 7.6518 – Unit m/s2 .
//            Next 2 bytes : b68  b69  - Ay of the 3 axis Imu of Dev2 (signed short format) – Scale factor 7.6518 – Unit m/s2 .
//            Next 2 bytes : b70  b71  - Az of the 3 axis Imu of Dev2 (signed short format) – Scale factor 7.6518 – Unit m/s2 .
//            Next 2 bytes : b72  b73  - Frontal Slippage Angle (signed short format) – Scale Factor .10 – Unit –deg.
//            Next 2 bytes : b74  b75  - Sagital Slippage Angle (signed short format) – Scale Factor .10 – Unit –deg.
//            Next 2 bytes : b76  b77  - Checksum in (unsigned short format).
//    Next 3 bytes :  b78  b79  b80      - Reserved for future use.
//    SessionHeader sessionHeader;

    SessionSummary session_summary;
    public long packet_number;
    public Date date; // dd-MM-YYYY HH:mm:SS.sss :- dd-MM-YYYY use from session summary
    public float ax_9axis_dev1;
    public float ay_9axis_dev1;
    public float az_9axis_dev1;
    public float gx_9axis_dev1;
    public float gy_9axis_dev1;
    public float gz_9axis_dev1;
    public float mx_9axis_dev1;
    public float my_9axis_dev1;
    public float mz_9axis_dev1;
    public float ax_3axis_dev1;
    public float ay_3axis_dev1;
    public float az_3axis_dev1;

    public float lat;
    public float lng;

// Commented below 3 line , It will be used in future
//    float gps_aux;
//    int hr;
//    Date hr_date;

    public float ax_9axis_dev2;
    public float ay_9axis_dev2;
    public float az_9axis_dev2;
    public float gx_9axis_dev2;
    public float gy_9axis_dev2;
    public float gz_9axis_dev2;
    public float mx_9axis_dev2;
    public float my_9axis_dev2;
    public float mz_9axis_dev2;
    public float ax_3axis_dev2;
    public float ay_3axis_dev2;
    public float az_3axis_dev2;
    public float frontal_slippage;
    public float sagital_slippage;
}
