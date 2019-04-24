package louis.app.pointofsale;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import louis.Utils;
import louis.app.pointofsale.dao.SettingsDAO;
import louis.app.pointofsale.dto.SettingsDTO;
import louis.log.Log;

public class SettingsActivity extends AppCompatActivity {

    private EditText mEdtHostURL;
    private EditText mEdtHostPORT;
    private SettingsDTO mSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(toolbar);

        mSettings = SettingsDAO.getInstance().getSettings();

        mEdtHostURL = findViewById(R.id.edtHostURL);
        mEdtHostURL.setText(mSettings.getHostUrl());
        mEdtHostPORT = findViewById(R.id.edtHostPort);
        mEdtHostPORT.setText(mSettings.getPort() + "");

        Button vSaveButton = findViewById(R.id.btnSave);
        vSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String vText = mEdtHostURL.getText().toString().trim();
                if(Utils.isNotEmpty(vText)) {
                    if(vText.endsWith("/") || vText.endsWith("\\")) {
                        vText = vText.substring(0, vText.length() - 1);
                    }

                    mSettings.setHostUrl(vText);

                    if(Utils.isEmpty(mEdtHostPORT.getText().toString())) {
                        mSettings.setPort(0);
                    } else {
                        try {
                            mSettings.setPort(Integer.parseInt(mEdtHostPORT.getText().toString()));
                        } catch (NumberFormatException e) {
                            mSettings.setPort(0);
                        }
                    }

                    SettingsDAO.getInstance().setSettings(mSettings);
                    finish();
                }
            }
        });
    }
}
