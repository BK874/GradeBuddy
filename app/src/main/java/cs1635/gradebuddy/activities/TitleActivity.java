package cs1635.gradebuddy.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

import cs1635.gradebuddy.R;

public class TitleActivity extends AppCompatActivity {


    private Button logoutButton;
    private Button continueButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);


        continueButton = (Button) findViewById(R.id.startButton);
        logoutButton = (Button) findViewById(R.id.exitButton);

        continueButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(new Intent(TitleActivity.this, MainActivity.class));
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(TitleActivity.this, LoginActivity.class));

            }
        });





    }







}