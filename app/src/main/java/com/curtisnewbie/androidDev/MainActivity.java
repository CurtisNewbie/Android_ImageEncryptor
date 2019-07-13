package com.curtisnewbie.androidDev;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

// import fo logging in the Android Studio
import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    // it is used in Logcat for logging
    public static final String TAG = "Encryption_Status";

    // UI components of this activity.
    private EditText pwInput;
    private EditText nameInput;
    private Button loginBtn;


    /* Brief: onCreate() -> onStart() -> Running -> onPause()/OnStop();
     Starting point of the activity life cycle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // setup the layout for this activity
        setContentView(R.layout.activity_main);

        // setup components of view
        pwInput = this.findViewById(R.id.pwInput);
        nameInput = this.findViewById(R.id.nameInput);
        loginBtn = this.findViewById(R.id.loginBtn);

        // logging message
        Log.i(TAG, "running");


    }

    @Override
    protected void onResume() {
        super.onResume();

        // logging message
        Log.i(TAG, "onResume");

    }

    @Override
    protected void onPause() {
        super.onPause();

        // logging message
        Log.i(TAG, "onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // logging message
        Log.i(TAG, "onDestroy");
    }

    /**
     * when the button is clicked, the entered name and password is processed.
     *
     * @param view implicit view object
     */
    public void addOnButtonClick(View view) {

                // for testing only - verify name and password
                final String NAME = "admin";
                final String PW = "admin";

                // getText does not return String, it's a editable object, similar to StringBuilder.
                String entName = nameInput.getText().toString().trim();
                String entPW = pwInput.getText().toString().trim();

                // Show a message on the screen using Toast, similar to JOptionPane.
                if (entName.equals(NAME) && entPW.equals(PW)) {

                    // Create an Intent obj as a new operation, the arg is the Action name in AnroidManifest.xml.
                    Intent intent = new Intent(".ImageListActivity");
                    startActivity(intent);

                } else {
                    Toast.makeText(MainActivity.this, "Account Not Okay", Toast.LENGTH_SHORT).show();
                }
            }

} // class
