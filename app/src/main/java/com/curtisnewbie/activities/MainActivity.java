package com.curtisnewbie.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.curtisnewbie.services.App;
import com.curtisnewbie.services.AuthService;
import com.curtisnewbie.database.AppDatabase;
import com.curtisnewbie.services.ExecService;

import javax.inject.Inject;

/**
 * ------------------------------------
 * <p>
 * Author: Yongjie Zhuang
 * <p>
 * ------------------------------------
 * <p>
 * Main {@code Activity} which is the login page.
 * </p>
 */
public class MainActivity extends AppCompatActivity implements Promptable {

    private EditText pwInput;
    private EditText nameInput;
    private Button loginBtn;
    private TextView instructTv;
    @Inject
    protected ExecService es;

    @Inject
    protected AuthService authService;

    @Inject
    protected AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.getAppComponent().inject(this);
        super.onCreate(savedInstanceState);

        // setup the layout for this activity
        setContentView(R.layout.activity_main);
        pwInput = this.findViewById(R.id.pwInput);
        nameInput = this.findViewById(R.id.nameInput);
        loginBtn = this.findViewById(R.id.loginBtn);
        instructTv = this.findViewById(R.id.instructTv);

        // create thread to prompt msg about whether the user should sign in or sign up
        es.submit(() -> {
            int n = db.userDao().getNumOfUsers();
            String msg;
            if (n == 0) {
                msg = "Register a new account";
                instructTv.setText(R.string.register_instruction);
            } else {
                msg = "Sign in your account";
            }
            prompt(msg);
        });
    }

    /**
     * when the button is clicked, the entered name and password is processed. This
     * is a listener method for loginBtn.
     *
     * @param view implicit view object
     */
    public void addOnButtonClick(View view) {
        String entName = nameInput.getText().toString().trim();
        String entPW = pwInput.getText().toString().trim();

        es.submit(() -> {
            String msg;
            if (db.userDao().getNumOfUsers() == 0) {
                if (authService.register(entName, entPW)) {
                    msg = String.format("Registration Successful, Welcome %s", entName);
                } else {
                    msg = "Account cannot be registered";
                }
            } else {
                if (authService.login(entName, entPW) && authService.isAuthenticated()) {
                    msg = String.format("Welcome %s", entName);
                    navToImageList();
                } else {
                    msg = "Account is incorrect";
                }
            }
            this.runOnUiThread(() -> {
                // Remove entered credential
                nameInput.setText("");
                pwInput.setText("");
            });
            prompt(msg);
        });
    }

    /**
     * Navigates to ImageListActivity. Should only be called when the user is
     * authenticated.
     */
    private void navToImageList() {
        // navigates to ImageListActivity
        Intent intent = new Intent(this, ImageListActivity.class);
        startActivity(intent);
    }

    @Override
    public void prompt(String msg) {
        this.runOnUiThread(() -> Toast.makeText(this, msg, Toast.LENGTH_LONG).show());
    }
}
