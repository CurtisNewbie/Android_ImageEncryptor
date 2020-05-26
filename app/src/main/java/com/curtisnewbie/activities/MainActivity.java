package com.curtisnewbie.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.curtisnewbie.services.App;
import com.curtisnewbie.services.AuthService;
import com.curtisnewbie.database.AppDatabase;
import com.curtisnewbie.services.ExecService;

import javax.inject.Inject;

import static com.curtisnewbie.util.IntentUtil.hasIntentActivity;

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
public class MainActivity extends AppCompatActivity {

    public static final String DATA_FROM_MAIN = "main";

    private EditText pwInput;
    private EditText nameInput;
    private Button loginBtn;
    private TextView instructTv;
    private Uri receivedData = null;

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

        Intent intent = getIntent();
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_SEND) && intent.hasExtra(Intent.EXTRA_STREAM)) {
            receivedData = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            MsgToaster.msgLong(this, R.string.action_send_msg);
        }

        // setup the layout for this activity
        setContentView(R.layout.activity_main);
        pwInput = this.findViewById(R.id.pwInput);
        nameInput = this.findViewById(R.id.nameInput);
        loginBtn = this.findViewById(R.id.loginBtn);
        instructTv = this.findViewById(R.id.instructTv);
    }

    /**
     * when the button is clicked, the entered name and password is processed. This
     * is a listener for loginBtn.
     *
     * @param view implicit view object
     */
    public void addOnButtonClick(View view) {
        String entName = nameInput.getText().toString().trim();
        String entPW = pwInput.getText().toString().trim();

        es.submit(() -> {
            String msg;
            if (authService.isRegistered()) {
                if (authService.login(entName, entPW) && authService.isAuthenticated()) {
                    msg = String.format("Welcome %s", entName);
                    navToImageList();
                } else {
                    msg = getString(R.string.account_incorrect_msg);
                }
            } else {
                if (authService.register(entName, entPW)) {
                    msg = String.format("Registration Successful, Welcome %s", entName);
                } else {
                    msg = getString(R.string.account_not_registered_msg);
                }
            }
            this.runOnUiThread(() -> {
                // Remove entered credential
                nameInput.setText("");
                pwInput.setText("");
                MsgToaster.msgShort(this, msg);
            });
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // open github repo page for this app
        if (item.getItemId() == R.id.aboutMenuItem) {
            Intent openWebpageIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.app_repo_url)));
            if (hasIntentActivity(this, openWebpageIntent)) {
                startActivity(openWebpageIntent);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Navigates to ImageListActivity. Should only be called when the user is
     * authenticated.
     */
    private void navToImageList() {
        // navigates to ImageListActivity
        Intent intent = new Intent(this, ImageListActivity.class);
        if (receivedData != null) {
            try {
                String path = receivedData.toString();
                intent.putExtra(DATA_FROM_MAIN, path);
                receivedData = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        startActivity(intent);
    }
}
