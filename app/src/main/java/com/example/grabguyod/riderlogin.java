package com.example.grabguyod;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class riderlogin extends AppCompatActivity {
    private EditText memail, mpassword;
    public Button rlogin, rregister, rrequest;

    private FirebaseAuth mauth;
    private FirebaseAuth.AuthStateListener firebaseauthlistener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riderlogin);

        mauth = FirebaseAuth.getInstance();

        firebaseauthlistener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null){
                    Intent intent = new Intent(riderlogin.this, ridermap.class);
                    startActivity(intent);
                    finish();

                }

            }
        };

        memail = (EditText) findViewById(R.id.email);
        mpassword = (EditText) findViewById(R.id.password);

        rlogin = (Button) findViewById(R.id.rlogin);
        rregister = (Button) findViewById(R.id.rregister);

        rregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = memail.getText().toString();
                final String password = mpassword.getText().toString();
                mauth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(riderlogin.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(riderlogin.this,"sign-up error", Toast.LENGTH_SHORT).show();
                        }else{
                            String user_id = mauth.getCurrentUser().getUid();
                            DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("users").child("drivers").child(user_id);
                            current_user_db.setValue(true);
                        }

                    }
                });
            }
        });
        rlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = memail.getText().toString();
                final String pass = mpassword.getText().toString();
                mauth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(riderlogin.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(riderlogin.this,"sign-in error", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });
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
