package com.curtisnewbie.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.curtisnewbie.util.CryptoUtil;
import com.curtisnewbie.database.AppDatabase;
import com.curtisnewbie.database.DBManager;
import com.curtisnewbie.database.User;
import com.curtisnewbie.util.ThreadManager;

import java.util.Arrays;

/**
 * Activity for login page
 */
public class MainActivity extends AppCompatActivity implements Promptable {

    private EditText pwInput;
    private EditText nameInput;
    private Button loginBtn;

    /**
     * RoomDatabase
     */
    private AppDatabase db;
    private ThreadManager tm = ThreadManager.getThreadManager();

    /**
     * This key is used to encrypt and decrypt images, this is essentially a hash of
     * (password + img_salt).
     * @see User
     */
    private String imgKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // getDb (or iniDb if first time)
        db = DBManager.getInstance(this).getDB();

        // setup the layout for this activity
        setContentView(R.layout.activity_main);

        // setup components of view
        pwInput = this.findViewById(R.id.pwInput);
        nameInput = this.findViewById(R.id.nameInput);
        loginBtn = this.findViewById(R.id.loginBtn);

        // create thread to prompt msg about whether the user should sign in or sign up
        tm.submit(() -> {
            int n = db.userDao().getNumOfUsers();
            String msg;
            if (n == 0)
                msg = "Register a new account";
            else
                msg = "Sign in your account";
            prompt(msg);
        });
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

        tm.submit(() -> {
            if (db.userDao().getNumOfUsers() == 0) {
                String msg;
                if (register(entName, entPW)) {
                    msg = String.format("Welcome %s", entName);
                } else {
                    msg = "Account cannot be registered";
                }
                prompt(msg);
            } else {
                if (checkCredential(entName, entPW)) {
                    String imgSalt = db.userDao().getImgSalt(entName);
                    String imgKey = Base64.encodeToString(CryptoUtil.hash(entPW , imgSalt), Base64.DEFAULT);
                    this.login(imgKey);
                } else {
                    prompt("Account is incorrect");
                }
            }
        });
    }

    /**
     * Navigates to ImageListActivity. Should only be called when the user is authenticated.
     * This method is ran in a UI Thread.
     *
     * @param imgKey key for image encryption/decryption
     */
    private void login(String imgKey) {
        this.imgKey = imgKey;
        this.runOnUiThread(() -> {
            // Remove entered credential
            nameInput.setText("");
            pwInput.setText("");

            // jumps to ImageListActivity and passing the password to it.
            Intent intent = new Intent(".ImageListActivity");
            intent.putExtra(DBManager.PW_TAG, this.imgKey);
            startActivity(intent);
        });
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
            byte[] hash = CryptoUtil.hash(pw, user.getPw_salt());
            if (Arrays.equals(hash, user.getHash())) {
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
            user.setPw_salt(CryptoUtil.randSalt());
            user.setImg_salt(CryptoUtil.randSalt());

            // hashing
            byte[] hash = CryptoUtil.hash(pw, user.getPw_salt());
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

    @Override
    public void prompt(String msg) {
        this.runOnUiThread(() -> Toast.makeText(this, msg, Toast.LENGTH_LONG).show());
    }
}
