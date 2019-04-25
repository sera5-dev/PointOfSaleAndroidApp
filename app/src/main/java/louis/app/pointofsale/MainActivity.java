package louis.app.pointofsale;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import java.io.File;

import louis.app.pointofsale.dao.SettingsDAO;
import louis.app.pointofsale.dto.SettingsDTO;
import louis.app.pointofsale.http.HttpRequests;
import louis.log.Log;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_WRITE_PERMISSION = 786;
    private static final int REQUEST_READ_PERMISSION = 785;
    private static final int REQUEST_INTERNET_PERMISSION = 130;

    private EditText mEdtUsername;
    private EditText mEdtPassword;
    private Button mBtnLogin;
    private Switch mSwRemeberMe;
    private Activity vThis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.errorEnabled = true;
        vThis = this;
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_PERMISSION);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.INTERNET}, REQUEST_INTERNET_PERMISSION);
        }

        SettingsDAO.APP_CONFIG_FOLDER = getApplicationContext().getFilesDir().getAbsolutePath() + "/Config";
        Log.info("App config folder: " + SettingsDAO.APP_CONFIG_FOLDER, true);
        File appSettingsFolder = new File(SettingsDAO.APP_CONFIG_FOLDER);
        if(!appSettingsFolder.exists()) {
            Log.info("Creating app config folder...");
            appSettingsFolder.mkdirs();
        }

        SettingsDTO vSettingsDTO =  SettingsDAO.getInstance().getSettings();
        if(vSettingsDTO.isStayLogedIn()) {
            Log.info("Attempting Autologin");
            doLogin(vSettingsDTO.getUserName(), vSettingsDTO.getPassword(), vSettingsDTO.isStayLogedIn());
        }

        mEdtUsername = (EditText) findViewById(R.id.edtUsername);
        mEdtUsername.setText(vSettingsDTO.getUserName());
        mEdtPassword = (EditText) findViewById(R.id.edtPassword);
        mEdtPassword.setText(vSettingsDTO.getPassword());
        mBtnLogin = (Button) findViewById(R.id.btnLogin);
        mSwRemeberMe = (Switch) findViewById(R.id.swStayLoggedIn);
        mSwRemeberMe.setChecked(vSettingsDTO.isStayLogedIn());

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doLogin(mEdtUsername.getText().toString(), mEdtPassword.getText().toString(), mSwRemeberMe.isChecked());
            }
        });
    }

    private void doLogin(String pUsername, String pPassword, boolean stayLogedIn) {
        HttpRequests.login(this, pUsername, pPassword, stayLogedIn);
    }

    public void loginSuccess() {
        Intent intent = new Intent(vThis, NewSaleActivity.class);
        startActivity(intent);
    }

    public void loginFailed(String message) {
        UtilMethods.showToast(getApplicationContext(), "Failed to log in: " + message);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
