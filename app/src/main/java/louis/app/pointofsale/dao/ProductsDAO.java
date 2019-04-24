package louis.app.pointofsale.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import louis.app.pointofsale.dto.Product;

public class ProductsDAO {
    private static List<String> mLableNames;
    private static List<Product> mProducts;

    public static List<Product> getProducts() {
        return mProducts;
    }
    public static List<String> getLabelNames() {
        return mLableNames;
    }

    public static void setProducts(List<Product> pProducts) {
        ProductsDAO.mProducts = pProducts;
        if(mProducts != null) {
            mLableNames = new ArrayList<>();
            for(Product p: mProducts) {
                mLableNames.add(p.getLabel());
            }
            Collections.sort(mLableNames);
        }
    }

    public static Product searchProductByLabel(String pLabel) {
        if(mProducts != null) {
            for(Product p : mProducts) {
                if(p.getLabel().equalsIgnoreCase(pLabel)) {
                    return p;
                }
            }
        }


        return null;
    }
}
