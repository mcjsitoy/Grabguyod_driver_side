package com.example.grabguyod;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class user_login extends AppCompatActivity {
    private EditText memail, mpassword;
    private Button dlogin, bt_back;
    private TextView tv_linkRegister;
    DatabaseReference db_reqForm;
    FirebaseUser user;
    public String uid;
    private FirebaseAuth mauth;
    private FirebaseAuth.AuthStateListener firebaseauthlistener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driverlogin);

        mauth = FirebaseAuth.getInstance();

        firebaseauthlistener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null){
                    Intent intent = new Intent(user_login.this, driver_landingpage.class);
                    startActivity(intent);
                    finish();
                }
            }
        };

        memail = (EditText) findViewById(R.id.email);
        mpassword = (EditText) findViewById(R.id.password);
        dlogin = (Button) findViewById(R.id.drlogin);
        bt_back = (Button) findViewById(R.id.button_back);
        tv_linkRegister = (TextView) findViewById(R.id.tv_linkRegister);



        //Login Function
        dlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = memail.getText().toString();
                final String pass = mpassword.getText().toString();
                mauth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(user_login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(user_login.this,"Sign-in error", Toast.LENGTH_SHORT).show();
                        } else{
                            Toast.makeText(user_login.this,"Logged In", Toast.LENGTH_SHORT).show();
                            openLandingPage();
                        }
                    }
                });
            }
        });



        tv_linkRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(user_login.this, riderlogin.class);
                startActivity(intent);
                finish();
                return;
            }
        });

        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(user_login.this, Main3Activity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
    }


    //Linking Data to users table
    public void openLandingPage(){

        db_reqForm = FirebaseDatabase.getInstance().getReference("users");
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();

        Intent intent = new Intent(user_login.this, driver_landingpage.class);
        startActivity(intent);
        finish();
        return;
    }


    @Override
    protected void onStart() {
        super.onStart();
        mauth.addAuthStateListener(firebaseauthlistener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mauth.removeAuthStateListener(firebaseauthlistener);

    }
}
