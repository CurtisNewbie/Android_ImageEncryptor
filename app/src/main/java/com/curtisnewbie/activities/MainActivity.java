package com.curtisnewbie.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.curtisnewbie.database.AppDatabase;
import com.curtisnewbie.database.DataStorage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * Activity for login page
 */
public class MainActivity extends AppCompatActivity {

    public static final String TAG = "Encryption_Status";
    private static final String CRED_PATH = "cred.txt";

    // UI components of this activity.
    private EditText pwInput;
    private EditText nameInput;
    private Button loginBtn;

    /**
     * RoomDatabase
     */
    private AppDatabase db;

    // the password is set here only when the credential is checked.
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // getDb (or iniDb if first time)
        db = DataStorage.getInstance(this).getDB();

        // setup the layout for this activity
        setContentView(R.layout.activity_main);

        // setup components of view
        pwInput = this.findViewById(R.id.pwInput);
        nameInput = this.findViewById(R.id.nameInput);
        loginBtn = this.findViewById(R.id.loginBtn);
    }

    /**
     * when the button is clicked, the entered name and password is processed.
     * This is a listener method for loginBtn.
     *
     * @param view implicit view object
     */
    public void addOnButtonClick(View view) {

        // getText does not return String, it's a editable object, similar to StringBuilder.
        String entName = nameInput.getText().toString().trim();
        String entPW = pwInput.getText().toString().trim();

        // check credential
        if (checkCredential(entName, entPW)) {

            // Remove credential
            nameInput.setText("");
            pwInput.setText("");

            // jumps to ImageListActivity and passing the password to it.
            Intent intent = new Intent(".ImageListActivity");
            intent.putExtra(DataStorage.PW_TAG, this.password);
            startActivity(intent);

        } else {
            Toast.makeText(MainActivity.this, "Account Incorrect", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * This is used to check credential by comparing hashed name and password.
     *
     * @param name name login name
     * @param pw   password login password
     * @return true/false indicating whether the credential is verified.
     */
    public boolean checkCredential(String name, String pw) {
        // same dir as the openFileOutput() method
        File cred = new File(this.getFilesDir(), CRED_PATH);
        if (cred.exists()) {
            try {
                // read the bytes from existing files
                FileInputStream in = this.openFileInput(CRED_PATH);
                byte[] credBytes = new byte[in.available()];
                in.read(credBytes);
                in.close();

                // hash the password
                byte[] enteredCred = DataStorage.hashingCred(name, pw);
                // compare the hash rather than the actual password
                if (Arrays.equals(enteredCred, credBytes)) {
                    this.password = pw;
                    return true;
                } else {
                    return false;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (register(name, pw))
                return true;
            else
                return false;
        }
        return false;
    }

    /**
     * Register a new credential, by storing the hashed byte[] data locally.
     *
     * @param name name
     * @param pw   password
     * @return true/false indicating whether the hashed credential data are created and stored.
     */
    private boolean register(String name, String pw) {
        try {
            OutputStream out = this.openFileOutput(CRED_PATH, MODE_PRIVATE);
            byte[] hashedCred = DataStorage.hashingCred(name, pw);
            out.write(hashedCred);
            out.close();
            this.password = pw;
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
