package louis.app.pointofsale.http;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.entity.StringEntity;
import louis.app.pointofsale.dao.SettingsDAO;
import louis.app.pointofsale.dto.LoginStatus;
import louis.app.pointofsale.dto.SettingsDTO;
import louis.log.Log;

public class HttpUtils {
    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(Context context, String url, JSONObject jsonParams, AsyncHttpResponseHandler responseHandler) throws UnsupportedEncodingException {
        StringEntity entity = new StringEntity(jsonParams.toString());
        client.post(context, getAbsoluteUrl(url), entity, "application/json", responseHandler);
    }

    public static void updateLoginDetail() {
        LoginStatus vStatus = SettingsDAO.getInstance().getLoginStatus();
        client.setBasicAuth(vStatus.getUsername(), vStatus.getPassword());
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        SettingsDTO vSettings = SettingsDAO.getInstance().getSettings();
        String baseURL = vSettings.getHostUrl();
        String fullURL = "http://" + baseURL;
        if(vSettings.getPort() > 0) {
            fullURL += ":" + vSettings.getPort();
        }
        fullURL += relativeUrl;
        Log.info("Full URL: " + fullURL);
        return fullURL;
    }
}
