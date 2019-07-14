package com.curtisnewbie.androidDev;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.curtisnewbie.database.AppDatabase;
import com.curtisnewbie.database.Credential;
import com.curtisnewbie.database.DataStorage;
import com.curtisnewbie.database.ImageData;
import com.curtisnewbie.database.TestDATA;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // it is used in Logcat for logging
    public static final String TAG = "Encryption_Status";

    // UI components of this activity.
    private EditText pwInput;
    private EditText nameInput;
    private Button loginBtn;
    private AppDatabase db;


    /* Brief: onCreate() -> onStart() -> Running -> onPause()/OnStop();
     Starting point of the activity life cycle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // getDb or iniDb if first time
        db = DataStorage.getInstance(this).getDB();

        // setup the layout for this activity
        setContentView(R.layout.activity_main);

        // setup components of view
        pwInput = this.findViewById(R.id.pwInput);
        nameInput = this.findViewById(R.id.nameInput);
        loginBtn = this.findViewById(R.id.loginBtn);

        // for testing
        ImageData testImg = getTestData();
        db.dao().addImageData(testImg);

        // for login
        Credential root = new Credential();
        root.setCred_name("admin");
        root.setCred_pw("password");
        db.dao().addCredential(root);
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
        if (checkCredential(entName, entPW)) {

            // Create an Intent obj as a new operation, the arg is the Action name in AnroidManifest.xml.
            Intent intent = new Intent(".ImageListActivity");
            startActivity(intent);

        } else {
            // Show a message on the screen using Toast, similar to JOptionPane.
            Toast.makeText(MainActivity.this, "Account Not Okay", Toast.LENGTH_SHORT).show();
        }
    }

    // for testing
    public ImageData getTestData() {
        // setup test data
//        try {
//            InputStream in = this.getAssets().open("encrypted.txt");
            String bytes = TestDATA.str;
            byte[] data = bytes.getBytes();
//            in.read(data);
//            in.close();

            ImageData img = new ImageData();
            img.setImage_data(data);
            img.setImage_name("Encrypted Image 1");

            return img;

//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
    }

    /**
     * This is used to check credential
     *
     * @param name name
     * @param pw   password
     * @return true/false indicating whether the credential is verified.
     */
    private boolean checkCredential(String name, String pw) {
        List<Credential> creds = db.dao().getListOfCred();

        if(name.equals(creds.get(0).getCred_name()) && pw.equals(creds.get(0).getCred_pw()))
            return true;
        else
            return false;
    }

} // class
