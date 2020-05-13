package com.example.grabguyod;

import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class list_currPass extends  ArrayAdapter<addCurrentRequest> {

    private Activity context;
    driver_driveMode dm = new driver_driveMode();
    public List<addCurrentRequest> addCurrentRequestList;
    private String keyName, id_request,id_user,noP_user,offlineBroadcastStatus,status_request, timestamp,user_location, requestCode, uid;
    FirebaseUser user;
    DatabaseReference querythis;

    public list_currPass(Activity context, List<addCurrentRequest> addCurrentRequestList){
        super(context, R.layout.activity_list_curr_pass, addCurrentRequestList);
        this.context = context;
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.activity_list_curr_pass,null,true);

        TextView tv_status = listViewItem.findViewById(R.id.textView_StatusData);
        TextView tv_location = listViewItem.findViewById(R.id.textView_Location);
        TextView tv_destination = listViewItem.findViewById(R.id.textView_Destination);
        TextView tv_safetycode = listViewItem.findViewById(R.id.textView_SecurityCode);
        Button bt_add = listViewItem.findViewById(R.id.button_picked);
        final addCurrentRequest acr = addCurrentRequestList.get(position);



        tv_status.setText(acr.getRequest_Status());
        tv_location.setText(acr.getLocation());
        tv_destination.setText(acr.getDestination());
        tv_safetycode.setText(acr.getSafety_Code());




        bt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                querythis = FirebaseDatabase.getInstance().getReference("requestForm").child(id_request);
                querythis.removeValue();
            }
        });

        return listViewItem;


    }




}
