package com.example.grabguyod;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.List;


public class list_layout extends ArrayAdapter<addRequest> {

    private Activity context;
    private List<addRequest> addRequestList;
    private String keyName, id_request,id_user,noP_user,offlineBroadcastStatus,status_request, timestamp,user_location, requestCode, uid, user_destination;
    private Double lat, lng;
    public static Double translat ,translng;
    driver_driveMode dm = new driver_driveMode();
    FirebaseUser user;
    DatabaseReference querythis, drivloc;

    public list_layout(Activity context, List<addRequest> addRequestList){
        super(context, R.layout.activity_list_layout, addRequestList);
        this.context = context;
        this.addRequestList = addRequestList;
        driver_driveMode dm = new driver_driveMode();
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        querythis = FirebaseDatabase.getInstance().getReference("requestForm");
        drivloc = FirebaseDatabase.getInstance().getReference("MarkerViewRequest");
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.activity_list_layout,null,true);

        TextView textViewId = (TextView) listViewItem.findViewById(R.id.textView2);
        TextView tv_broadcast = (TextView) listViewItem.findViewById(R.id.textView_saferyCode);
        TextView tv_status = (TextView) listViewItem.findViewById(R.id.textView_Status);
        TextView tv_timestamp = (TextView) listViewItem.findViewById(R.id.textView_Timestamp);
        TextView textView_location = listViewItem.findViewById(R.id.textView_location);
        TextView tv_NoRiders = listViewItem.findViewById(R.id.textView_noRiders);
        Button bt_add = listViewItem.findViewById(R.id.button_picked);
        Button bt_map = listViewItem.findViewById(R.id.button_map);
        bt_map.setEnabled(false);


        final addRequest addRequest = addRequestList.get(position);
        id_request = addRequest.getRequest_id();
        id_user = addRequest.getUser_id();
        noP_user = addRequest.getUser_noP();
        offlineBroadcastStatus = addRequest.getOffline_BroadcastStatus();
        user_location = addRequest.getLocation();
        timestamp = addRequest.getTimeStamp();
        status_request = addRequest.getRequest_Status();
        requestCode = addRequest.getRequestCode();
        user_destination = addRequest.getDestination();



        lat = addRequest.getLat();
        lng = addRequest.getLon();


        if (status_request.equals("Accepted")){
            bt_map.setEnabled(true);
            bt_add.setEnabled(false);
        } else if (status_request.equals("Waiting Confirmation")) {
            bt_map.setEnabled(false);
            bt_add.setEnabled(false);
        } else if (status_request.equals("Waiting Confirmation") || status_request.equals("Accepted Code Sent"))  {
            bt_map.setEnabled(true);
            bt_add.setEnabled(false);
        }


/*
        textViewId.setText(addRequest.getUser_id());*/
        textView_location.setText(addRequest.getDestination());
        tv_timestamp.setText(addRequest.getLocation());
        tv_NoRiders.setText(addRequest.getUser_noP());
        tv_status.setText(addRequest.getRequest_Status());
        tv_broadcast.setText(addRequest.getSafety_Code());// Safety code




        bt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int countcheck1, countcheck2;
                countcheck1 = Integer.parseInt(noP_user);
                addRequest add_req = new addRequest(id_request, id_user, offlineBroadcastStatus, "Waiting Confirmation",noP_user, timestamp,user_location, requestCode, uid);
                querythis.child(id_request).setValue(add_req);
                dm.count = dm.count + countcheck1;
            }
        });

        bt_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                querythis.child(id_request).removeValue();
            }
        });




        /*textViewPhone.setText(addRequest.getUser_noP());
        tv_broadcast.setText(addRequest.getOffline_BroadcastStatus());
        tv_status.setText(addRequest.getRequest_Status());*/

        return listViewItem;
    }

}
