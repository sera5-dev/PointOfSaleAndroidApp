package louis.app.pointofsale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import louis.Utils;
import louis.app.pointofsale.dao.ProductsDAO;
import louis.app.pointofsale.dao.SettingsDAO;
import louis.app.pointofsale.dto.LoginStatus;
import louis.app.pointofsale.dto.Product;
import louis.app.pointofsale.dto.Sale;
import louis.app.pointofsale.dto.SaleItem;
import louis.app.pointofsale.dto.SettingsDTO;
import louis.app.pointofsale.http.HttpRequests;
import louis.log.Log;

public class NewSaleActivity extends AppCompatActivity {

    private Sale mSale;

    private AutoCompleteTextView mEdtLabel;

    private TableRow mtrProductInfo;
    private TableRow mtrQuantity;
    private TableRow mtradd;

    private TableLayout mItemsTable;

    private TextView mtvProductName;
    private TextView mtvVariant;
    private TextView mtvPrice;

    private TextView mnedQuantity;
    private TextView mtxtTotal;
    private LinearLayout mllFinishButtons;

    private Product mSelectedProduct;

    private Activity vThis;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vThis = this;
        setContentView(R.layout.activity_new_sale);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LoginStatus vLoginStatus = SettingsDAO.getInstance().getLoginStatus();
        if((vLoginStatus == null) || !vLoginStatus.isLoggedIn()) {
            finish();
        }
        initGlobalReferences();
        resetSale();

        mnedQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {
                String vValue = Utils.CStr(mnedQuantity.getText());
                if(vValue.length() == 0)
                    vValue = "0";
                int quantityValue = Integer.parseInt(vValue);
                mtxtTotal.setText(String.format("R %.2f", mSelectedProduct.getPrice() * quantityValue));
                if(quantityValue > 0) {
                    mtradd.setVisibility(View.VISIBLE);
                } else {
                    mtradd.setVisibility(View.INVISIBLE);
                }
            }
        });

        FloatingActionButton scanButton = (FloatingActionButton) findViewById(R.id.btnStartScan);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent vIntent = new Intent(vThis, ScanActivity.class);
                vIntent.putExtra(ScanActivity.AutoFocus, true);
                vIntent.putExtra(ScanActivity.UseFlash, false);
                startActivityForResult(vIntent, 0);
            }
        });

        Button addButton = (Button) findViewById(R.id.btnAddProduct);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String vValue = Utils.CStr(mnedQuantity.getText());
                if (vValue.length() == 0)
                    vValue = "0";
                int quantityValue = Integer.parseInt(vValue);

                if(quantityValue > 0) {
                    SaleItem vNewItem = searchSaleItem(mSelectedProduct.getId().intValue());
                    if (vNewItem == null) {
                        vNewItem = new SaleItem();
                        vNewItem.setProduct_id(mSelectedProduct.getId().intValue());
                        vNewItem.setProduct(mSelectedProduct.getProduct());
                        vNewItem.setVariant(mSelectedProduct.getVariant());
                        vNewItem.setPriceEach(mSelectedProduct.getPrice());
                        vNewItem.setQuantity(quantityValue);
                        vNewItem.setLineTotal(mSelectedProduct.getPrice() * vNewItem.getQuantity());
                        addNewItem(vNewItem);
                    } else {
                        vNewItem.setQuantity(vNewItem.getQuantity() + quantityValue);
                        vNewItem.setLineTotal(mSelectedProduct.getPrice() * vNewItem.getQuantity());
                        updateItem(vNewItem);
                    }
                    closeKeyBord();
                    mEdtLabel.setText("");
                    if(mEdtLabel.requestFocus()) {
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                    }
                }
            }
        });

        Button acceptButton = (Button) findViewById(R.id.btnAcceptSale);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acceptSale();
            }
        });
        Button cancleButton = (Button) findViewById(R.id.btnCancelSale);
        cancleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancleSale();
            }
        });

        HttpRequests.setProducts(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == CommonStatusCodes.SUCCESS) {
            try {
                Barcode vBarcode = data.getParcelableExtra(ScanActivity.BarcodeObject);
                if (vBarcode != null) {
                    String vBarcodeString = vBarcode.rawValue;
                    vBarcodeString = vBarcodeString.replaceAll("\"", "");
                    Log.info("Barcode Result: " + vBarcode.rawValue);
                    mEdtLabel.setText(vBarcodeString);
                    if(mnedQuantity.requestFocus()) {
                        mnedQuantity.setText("");
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                    }
                }
            } catch (NullPointerException e) {
                Log.error(e);
            }
        }
    }

    public void productGetDone() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.hint_completion_layout, R.id.tvHintCompletion, ProductsDAO.getLabelNames());
        mEdtLabel.setAdapter(adapter);
        mEdtLabel.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {
                String vText = Utils.CStr(mEdtLabel.getText());
                Product vProduct = ProductsDAO.searchProductByLabel(vText);
                if(vProduct != null) {
                    mSelectedProduct = vProduct;
                    mtvProductName.setText(mSelectedProduct.getProduct());
                    mtvVariant.setText(Utils.CStr(mSelectedProduct.getVariant()));
                    mtvPrice.setText(String.format("R %.2f", mSelectedProduct.getPrice()));
                    mnedQuantity.setText("0");
                    mtrProductInfo.setVisibility(View.VISIBLE);
                    mtrQuantity.setVisibility(View.VISIBLE);
                    if(mnedQuantity.requestFocus()) {
                        mnedQuantity.setText("");
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                    }
                } else {
                    mSelectedProduct = null;
                    mtrProductInfo.setVisibility(View.INVISIBLE);
                    mtrQuantity.setVisibility(View.INVISIBLE);
                    mtradd.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void addNewItem(SaleItem vNewItem) {
        TableRow row = (TableRow)LayoutInflater.from(this).inflate(R.layout.fragment_item_row, null);
        row.setId(vNewItem.getProduct_id());
        ((TextView)row.findViewById(R.id.txtItemProductName)).setText(vNewItem.getProduct());
        ((TextView)row.findViewById(R.id.txtItemProductVariant)).setText(Utils.CStr(vNewItem.getVariant()));
        ((TextView)row.findViewById(R.id.txtItemPriceEach)).setText(String.format("R %.2f", vNewItem.getPriceEach()));
        ((TextView)row.findViewById(R.id.txtItemQuantity)).setText(vNewItem.getQuantity() + "");
        ((TextView)row.findViewById(R.id.txtItemTotal)).setText(String.format("R %.2f", vNewItem.getLineTotal()));

        ((Button)row.findViewById(R.id.btnRemoveSaleItem)).setOnClickListener(new RemoveItemListner(vNewItem.getProduct_id()));

        mItemsTable.addView(row,mItemsTable.getChildCount() - 1);
        mSale.getSaleItems().add(vNewItem);

        updateSaleTotals();
    }

    private void updateItem(SaleItem vNewItem) {
        TableRow row = (TableRow) findViewById(vNewItem.getProduct_id());
        ((TextView)row.findViewById(R.id.txtItemQuantity)).setText(vNewItem.getQuantity() + "");
        ((TextView)row.findViewById(R.id.txtItemTotal)).setText(String.format("R %.2f", vNewItem.getLineTotal()));

        updateSaleTotals();
    }

    private void removeItem(int productID) {
        SaleItem vItem = searchSaleItem(productID);
        if(vItem != null) {
            mSale.getSaleItems().remove(vItem);
            TableRow row = (TableRow) findViewById(vItem.getProduct_id());
            mItemsTable.removeView(row);

            updateSaleTotals();
        }
    }

    private void updateSaleTotals() {
        int vTotalQuantaty = 0;
        double vTotalAmount = 0;

        for(SaleItem si: mSale.getSaleItems()) {
            vTotalQuantaty += si.getQuantity();
            vTotalAmount += si.getLineTotal();
        }

        mSale.setTotalAmount(vTotalAmount);
        mSale.setTotalQuantity(vTotalQuantaty);

        ((TextView)findViewById(R.id.txtTotalSaleQuantity)).setText(mSale.getTotalQuantity() + "");
        ((TextView)findViewById(R.id.txtTotalSaleAmount)).setText(String.format("R %.2f", mSale.getTotalAmount()));

        if(mSale.getSaleItems().size() > 0) {
            mItemsTable.setVisibility(View.VISIBLE);
            mllFinishButtons.setVisibility(View.VISIBLE);
        } else {
            mItemsTable.setVisibility(View.INVISIBLE);
            mllFinishButtons.setVisibility(View.INVISIBLE);
        }
    }

    private SaleItem searchSaleItem(int itemId) {
        for(SaleItem si: mSale.getSaleItems()) {
            if(si.getProduct_id() == itemId)
                return si;
        }

        return null;
    }

    private void acceptSale(){
        HttpRequests.submitSale(this);
    }

    public void saleFinisedSuccess() {
        resetSale();
        UtilMethods.showToast(getApplicationContext(), "Sale Finished Successfully");
    }

    public void saleFinisedFailed(String message) {
        if(Utils.isEmpty(message)) {
            UtilMethods.showToast(getApplicationContext(), "Sale Finished Failed");
        } else {
            UtilMethods.showToast(getApplicationContext(), "Failed to add sale: " + message);
        }
    }

    private void cancleSale() {
        resetSale();
        UtilMethods.showToast(getApplicationContext(), "Sale Canceled Successfully");
    }

    private void resetSale() {
        if((mSale != null) && (mItemsTable.getChildCount() > 2)) {
            for(SaleItem si: mSale.getSaleItems()) {
                TableRow row = findViewById(si.getProduct_id());
                if(row != null) {
                    mItemsTable.removeView(row);
                }
            }
        }

        mSale = new Sale();

        mSelectedProduct = null;
        mtrProductInfo.setVisibility(View.INVISIBLE);
        mtrQuantity.setVisibility(View.INVISIBLE);
        mtradd.setVisibility(View.INVISIBLE);

        updateSaleTotals();
    }

    private void initGlobalReferences() {
        mEdtLabel = findViewById(R.id.edtLabel);

        mtrProductInfo = findViewById(R.id.trProductInfo);
        mtrQuantity = findViewById(R.id.trQuantity);
        mtradd = findViewById(R.id.trAdd);

        mtvProductName = findViewById(R.id.txtProduct);
        mtvVariant = findViewById(R.id.txtVariant);
        mtvPrice = findViewById(R.id.txtPrice);

        mnedQuantity = findViewById(R.id.nedQuantity);
        mtxtTotal = findViewById(R.id.txtTotal);

        mItemsTable = findViewById(R.id.tbItems);
        mllFinishButtons = findViewById(R.id.llFinishButtons);
    }

    private void closeKeyBord() {
        InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public Sale getSale() {
        return mSale;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_sale, menu);
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

        if (id == R.id.action_logout) {
            SettingsDTO vSettings = SettingsDAO.getInstance().getSettings();
            vSettings.setUserName("");
            vSettings.setPassword("");
            vSettings.setStayLogedIn(false);
            SettingsDAO.getInstance().setSettings(vSettings);

            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private class RemoveItemListner implements View.OnClickListener {
        private int productId;

        public RemoveItemListner(int productId) {
            this.productId = productId;
        }

        @Override
        public void onClick(View view) {
            removeItem(productId);
        }
    }
}
