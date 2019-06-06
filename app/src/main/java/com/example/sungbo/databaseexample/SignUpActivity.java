package com.example.sungbo.databaseexample;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sungbo.databaseexample.Model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {
    private EditText email;
    private EditText password;
    private Button singup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        email = findViewById(R.id.signup_email);
        password = findViewById(R.id.signup_password);
        singup = findViewById(R.id.email_signup_button);

        singup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(email.getText().toString() == null || password.getText().toString() == null ){
                    return;
                }
                else if(password.getText().toString().length() < 6){
                    Toast.makeText(SignUpActivity.this, "Password Should be Longer than 6 characters", Toast.LENGTH_LONG).show();
                    return;
                }
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                UserModel userModel = new UserModel();
                                String uid = task.getResult().getUser().getUid();
                                userModel.setUid(uid);
                                userModel.setEmail(email.getText().toString());
                                userModel.setPassword(password.getText().toString());

                                FirebaseDatabase.getInstance().getReference().child("users")
                                        .child(uid).setValue(userModel);

                                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                                finish();
                            }
                        });
            }
        });
    }
}
