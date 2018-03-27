package cs1635.gradebuddy.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import cs1635.gradebuddy.R;

public class RegisterActivity extends AppCompatActivity {

    private EditText username;
    private EditText passwordF;

    private Button registerButton;


    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener mAuthListener;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = (EditText) findViewById(R.id.loginEmail);
        passwordF = (EditText) findViewById(R.id.loginPassword);


        registerButton = (Button) findViewById(R.id.registerButton);

        mAuth = FirebaseAuth.getInstance();


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = username.getText().toString().trim();
                String password = passwordF.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(getApplicationContext(), "Please enter a valid username", Toast.LENGTH_LONG).show();

                }

                if(TextUtils.isEmpty(password)){
                    Toast.makeText(getApplicationContext(), "Please enter a valid password", Toast.LENGTH_LONG).show();
                }


                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "Registration Failed" +task.getException(), Toast.LENGTH_LONG).show();
                        }
                        else{
                            startActivity(new Intent(RegisterActivity.this, TitleActivity.class));
                        }
                    }
                });


            }
        });





    }


}