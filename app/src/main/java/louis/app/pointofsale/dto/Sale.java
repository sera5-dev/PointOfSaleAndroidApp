package louis.app.pointofsale.dto;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class Sale {
	private Long sale_id;
	private Timestamp saleDate;
	private double totalAmount;
	private int totalQuantity;
	private List<SaleItem> saleItems;
	
	public Long getSale_id() {
		return sale_id;
	}

	public void setSale_id(Long sale_id) {
		this.sale_id = sale_id;
	}

	public Timestamp getSaleDate() {
		return saleDate;
	}

	public void setSaleDate(Timestamp saleDate) {
		this.saleDate = saleDate;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public int getTotalQuantity() {
		return totalQuantity;
	}

	public void setTotalQuantity(int totalQuantity) {
		this.totalQuantity = totalQuantity;
	}

	public List<SaleItem> getSaleItems() {
		if(saleItems == null) {
			saleItems = new ArrayList<SaleItem>();
		}		
		return saleItems;
	}

	public void setSaleItems(List<SaleItem> saleItems) {
		this.saleItems = saleItems;
	}
	
	@Override
	public String toString() {
		SimpleDateFormat vFormatter = new SimpleDateFormat("dd MMM YYYY HH:mm:ss");
		
		String vItems = "";
		for(SaleItem vItem : getSaleItems()) {
			vItems += vItem + ", ";
		}
		if(vItems.length() > 0)
			vItems = vItems.substring(0, vItems.length() - 2);
		
		String vResult = "{";
		vResult += "\"sale_id\": " + sale_id + ", ";
		vResult += "\"saleDate\": \"" + vFormatter.format(saleDate) + "\", ";
		vResult += "\"totalAmount\": " + totalAmount + ", ";
		vResult += "\"totalQuantity\": " + totalQuantity + ", ";
		vResult += "\"items\": [" + vItems + "]";
		vResult += "}";
		return vResult;
	}
}
