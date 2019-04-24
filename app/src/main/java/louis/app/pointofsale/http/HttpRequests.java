package louis.app.pointofsale.http;

import android.content.Intent;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import louis.Utils;
import louis.app.pointofsale.MainActivity;
import louis.app.pointofsale.NewSaleActivity;
import louis.app.pointofsale.UtilMethods;
import louis.app.pointofsale.dao.ProductsDAO;
import louis.app.pointofsale.dao.SettingsDAO;
import louis.app.pointofsale.dto.LoginStatus;
import louis.app.pointofsale.dto.NewSaleResponsDTO;
import louis.app.pointofsale.dto.Product;
import louis.app.pointofsale.dto.Sale;
import louis.app.pointofsale.dto.SettingsDTO;
import louis.log.Log;

public class HttpRequests {

    public static void login(final MainActivity pMainActivity, final String pUsername, final String pPassword, final boolean stayLogedIn) {
        RequestParams rp = new RequestParams();
        rp.add("a_user", Utils.CStr(pUsername));
        rp.add("a_pass", Utils.CStr(pPassword));

        HttpUtils.get("/appLogin", rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray

                Log.info("HTTPRespons: " + response);
                LoginStatus vStatus = Utils.fromJson(response.toString(), LoginStatus.class);
                SettingsDTO vSettings = SettingsDAO.getInstance().getSettings();
                vSettings.setUserName("");
                vSettings.setPassword("");
                vSettings.setStayLogedIn(false);
                if(!vStatus.isLoggedIn()) {
                    pMainActivity.loginFailed(vStatus.getMessage());
                } else {
                    if(stayLogedIn) {
                        vSettings.setUserName(pUsername);
                        vSettings.setPassword(pPassword);
                        vSettings.setStayLogedIn(true);
                    }

                    vStatus.setPassword(pPassword);

                    SettingsDAO.getInstance().setLoginStatus(vStatus);
                    HttpUtils.updateLoginDetail();
                    pMainActivity.loginSuccess();
                }
                SettingsDAO.getInstance().setSettings(vSettings);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.error(throwable);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Pull out the first event on the public timeline

            }
        });
    }

    public static void setProducts(final NewSaleActivity pNewSaleActivity) {
        Log.info("setProducts :: START");
        HttpUtils.get("/Web/Products", new RequestParams(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {}

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.error(throwable);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                Log.info("HTTPRespons: " + timeline);

                List<Product> vProducts = new ArrayList<>();
                if(timeline != null) {
                    for(int i = 0; i < timeline.length(); i++) {
                        try {
                            JSONObject jObj = timeline.getJSONObject(i);
                            Product vProduct = Utils.fromJson(jObj.toString(), Product.class);
                            vProducts.add(vProduct);
                        } catch (JSONException e) {
                            Log.error(e);
                        }
                    }
                }
                ProductsDAO.setProducts(vProducts);
                pNewSaleActivity.productGetDone();
            }
        });
    }

    public static void submitSale(final NewSaleActivity pNewSaleActivity) {
        Log.info("submitSale :: START");
        Sale vSale = pNewSaleActivity.getSale();
        try {
            JSONObject jsonParams = new JSONObject();
            jsonParams.put("totalQuantity", vSale.getTotalQuantity() + "");
            jsonParams.put("totalAmount", vSale.getTotalAmount() + "");
            jsonParams.put("items", Utils.toJson(vSale.getSaleItems(), vSale.getSaleItems().getClass()));


            HttpUtils.post(pNewSaleActivity.getApplicationContext(), "/Web/app/finishSale", jsonParams, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.info("HTTPRespons: " + response);
                    NewSaleResponsDTO vRespons = Utils.fromJson(response.toString(), NewSaleResponsDTO.class);
                    if((vRespons != null) && vRespons.isSuccess()) {
                        pNewSaleActivity.saleFinisedSuccess();
                    } else {

                        pNewSaleActivity.saleFinisedFailed(vRespons.getMessage());
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.error(throwable);
                    Log.info("onFailure: " + throwable);
                    throwable.printStackTrace();
                    pNewSaleActivity.saleFinisedFailed(null);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                }
            });
        } catch (JSONException | UnsupportedEncodingException e) {
            Log.error(e);
            pNewSaleActivity.saleFinisedFailed(null);
        }
    }

}
