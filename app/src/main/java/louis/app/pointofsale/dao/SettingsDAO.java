package louis.app.pointofsale.dao;

import louis.Utils;
import louis.app.pointofsale.dto.LoginStatus;
import louis.app.pointofsale.dto.SettingsDTO;
import louis.log.Log;

public class SettingsDAO {
    public static String APP_CONFIG_FOLDER;
    private static final String SETTINGS_FILE = "/Settings.json";
    private SettingsDTO mSettings;
    private LoginStatus mLoginStatus;

    private SettingsDAO() {
        mSettings = Utils.readJsonFile(APP_CONFIG_FOLDER + SETTINGS_FILE, SettingsDTO.class);
        if(mSettings == null) {
            SettingsDTO vNewSettings = new SettingsDTO();
            vNewSettings.setHostUrl("localhost");
            vNewSettings.setPort(8080);
            setSettings(vNewSettings);
        }
    };

    public LoginStatus getLoginStatus() {
        return mLoginStatus;
    }

    public void setLoginStatus(LoginStatus pLoginStatus) {
        this.mLoginStatus = pLoginStatus;
    }


    public void setSettings(SettingsDTO pSettings) {
        mSettings = pSettings;
        Log.info("Saving: " + Utils.toJson(mSettings, SettingsDTO.class));
        Utils.writeJsonFile(APP_CONFIG_FOLDER + SETTINGS_FILE, mSettings, SettingsDTO.class);
    }

    public SettingsDTO getSettings() {
        return mSettings;
    }

    private static SettingsDAO INSTANCE;

    public static SettingsDAO getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new SettingsDAO();
        }

        return INSTANCE;
    }
}
