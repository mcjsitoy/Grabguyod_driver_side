package com.example.grabguyod;

public class addRequest {
    String request_id;
    String user_id;
    String offline_BroadcastStatus;
    String request_Status;
    String user_noP;
    String timeStamp;
    String location;
    String requestCode;
    String safety_Code;
    String driver_number;
    double lat;
    double lon;
    String destination;

    public addRequest(){

    }

    public addRequest(String request_id, String user_id, String user_noP, String offline_BroadcastStatus, String request_Status, String timeStamp, String location, String requestCode) {
        this.request_id = request_id;
        this.user_id = user_id;
        this.user_noP = user_noP;
        this.offline_BroadcastStatus = offline_BroadcastStatus;
        this.request_Status = request_Status;
        this.timeStamp = timeStamp;
        this.location = location;
        this.requestCode = requestCode;
    }

    public addRequest(String request_id, String user_id, String offline_BroadcastStatus, String request_Status, String user_noP, String timeStamp, String location, String requestCode, String driver_number) {
        this.request_id = request_id;
        this.user_id = user_id;
        this.offline_BroadcastStatus = offline_BroadcastStatus;
        this.request_Status = request_Status;
        this.user_noP = user_noP;
        this.timeStamp = timeStamp;
        this.location = location;
        this.requestCode = requestCode;
        this.driver_number = driver_number;
    }

    public addRequest(String request_id, String user_id, String offline_BroadcastStatus, String request_Status, String user_noP, String timeStamp, String location, String requestCode, String driver_number, String safety_Code) {
        this.request_id = request_id;
        this.user_id = user_id;
        this.offline_BroadcastStatus = offline_BroadcastStatus;
        this.request_Status = request_Status;
        this.user_noP = user_noP;
        this.timeStamp = timeStamp;
        this.location = location;
        this.requestCode = requestCode;
        this.safety_Code = safety_Code;
        this.driver_number = driver_number;
    }

    public addRequest(String request_id, String user_id, String offline_BroadcastStatus, String request_Status, String user_noP, String timeStamp, String location, String requestCode, String safety_Code, String driver_number, double lat, double lon, String destination) {
        this.request_id = request_id;
        this.user_id = user_id;
        this.offline_BroadcastStatus = offline_BroadcastStatus;
        this.request_Status = request_Status;
        this.user_noP = user_noP;
        this.timeStamp = timeStamp;
        this.location = location;
        this.requestCode = requestCode;
        this.safety_Code = safety_Code;
        this.driver_number = driver_number;
        this.lat = lat;
        this.lon = lon;
        this.destination = destination;
    }

    public addRequest(String request_id, String user_id, String offline_BroadcastStatus, String request_Status, String user_noP, String timeStamp, String location, String requestCode, double lat, double lon, String UserDestination) {
        this.request_id = request_id;
        this.user_id = user_id;
        this.offline_BroadcastStatus = offline_BroadcastStatus;
        this.request_Status = request_Status;
        this.user_noP = user_noP;
        this.timeStamp = timeStamp;
        this.location = location;
        this.requestCode = requestCode;
        this.lat = lat;
        this.lon = lon;
        this.destination = UserDestination;
    }

    public addRequest(String user_id, double lat, double lon) {
        this.user_id = user_id;
        this.lat = lat;
        this.lon = lon;
    }

    public String getRequest_id() {
        return request_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getUser_noP() {
        return user_noP;
    }

    public String getOffline_BroadcastStatus() {
        return offline_BroadcastStatus;
    }

    public String getRequest_Status() {
        return request_Status;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getLocation() {
        return location;
    }

    public String getRequestCode() {
        return requestCode;
    }

    public String getSafety_Code() {
        return safety_Code;
    }

    public String getDriver_number() {
        return driver_number;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLon() {
        return lon;
    }

    public String getDestination() {
        return destination;
    }
}
