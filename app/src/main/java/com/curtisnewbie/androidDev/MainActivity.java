package com.curtisnewbie.androidDev;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.curtisnewbie.database.DatabaseHelper;

public class MainActivity extends AppCompatActivity {
    boolean firstTimeRun = true;

    // it is used in Logcat for logging
    public static final String TAG = "Encryption_Status";

    // UI components of this activity.
    private EditText pwInput;
    private EditText nameInput;
    private Button loginBtn;

    private DatabaseHelper db;


    /* Brief: onCreate() -> onStart() -> Running -> onPause()/OnStop();
     Starting point of the activity life cycle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // create db for the first time
        if (firstTimeRun) {
            db = new DatabaseHelper(this);
            firstTimeRun = false;
        }

        // setup the layout for this activity
        setContentView(R.layout.activity_main);

        // setup components of view
        pwInput = this.findViewById(R.id.pwInput);
        nameInput = this.findViewById(R.id.nameInput);
        loginBtn = this.findViewById(R.id.loginBtn);
    }

    /**
     * when the button is clicked, the entered name and password is processed.
     *
     * @param view implicit view object
     */
    public void addOnButtonClick(View view) {

        // getText does not return String, it's a editable object, similar to StringBuilder.
        String entName = nameInput.getText().toString().trim();
        String entPW = pwInput.getText().toString().trim();

        // check credential
        if (db.checkCredential(entName, entPW)) {

            // Create an Intent obj as a new operation, the arg is the Action name in AnroidManifest.xml.
            Intent intent = new Intent(".ImageListActivity");
            startActivity(intent);

        } else {
            // Show a message on the screen using Toast, similar to JOptionPane.
            Toast.makeText(MainActivity.this, "Account Not Okay", Toast.LENGTH_SHORT).show();
        }
    }

} // class
