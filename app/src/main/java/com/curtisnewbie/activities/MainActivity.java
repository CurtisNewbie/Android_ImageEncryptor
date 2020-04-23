package com.curtisnewbie.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.curtisnewbie.crypto.CryptoUtil;
import com.curtisnewbie.database.AppDatabase;
import com.curtisnewbie.database.DataStorage;
import com.curtisnewbie.database.User;

import java.util.Arrays;

/**
 * Activity for login page
 */
public class MainActivity extends AppCompatActivity {

    public static final String TAG = "Encryption_Status";

    // UI components of this activity.
    private EditText pwInput;
    private EditText nameInput;
    private Button loginBtn;

    /**
     * RoomDatabase
     */
    private AppDatabase db;

    // TODO: This is a terrible idea, as this loads the password in memory, find a way to fix it
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

        // create thread to prompt msg about whether the user should sign in or sign up
        new Thread(() -> {
            int n = db.userDao().getNumOfUsers();
            String msg;
            if (n == 0)
                msg = "Register a new account";
            else
                msg = "Sign in your account";
            this.runOnUiThread(() -> {
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            });
        }).start();
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

        new Thread(() -> {
            if (db.userDao().getNumOfUsers() == 0) {
                String msg;
                if (register(entName, entPW))
                    msg = "Account successfully registered";
                else
                    msg = "Account cannot be registered";
                this.runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                });
            } else {
                // check credential
                if (checkCredential(entName, entPW)) {
                    this.runOnUiThread(() -> {
                        // Remove entered credential
                        nameInput.setText("");
                        pwInput.setText("");

                        // jumps to ImageListActivity and passing the password to it.
                        Intent intent = new Intent(".ImageListActivity");
                        intent.putExtra(DataStorage.PW_TAG, this.password);
                        startActivity(intent);
                    });
                } else {
                    this.runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this, "Account is incorrect", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        }).start();
    }


    /**
     * Check Credential
     *
     * @param name name login name
     * @param pw   password login password
     * @return true/false indicating whether the credential is verified.
     */
    public boolean checkCredential(String name, String pw) {
        if (name.isEmpty() || pw.isEmpty()) // invalid arguments
            return false;

        User user = db.userDao().getUser(name);
        if (user != null) {
            // compare hashes
            byte[] hash = CryptoUtil.hash(pw, user.getSalt());
            if (Arrays.equals(hash, user.getHash())) {
                this.password = pw;
                return true;
            }
        }
        return false;
    }

    /**
     * Persist a new {@code User} (or new credential) in database
     *
     * @param name name
     * @param pw   password
     * @return true/false indicating whether the {@code User} is persisted
     */
    private boolean register(String name, String pw) {
        try {
            // create new user
            User user = new User();
            user.setUsername(name);
            user.setSalt(CryptoUtil.randSalt(4)); // TODO: create constant for salt length

            // hashing
            byte[] hash = CryptoUtil.hash(pw, user.getSalt());
            if (hash == null)
                return false; // Hashing operation failed
            user.setHash(hash);

            // persist user
            db.userDao().addUser(user);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
