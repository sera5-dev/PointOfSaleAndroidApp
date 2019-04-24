package louis.app.pointofsale.dto;

public class SaleItem {
	private Long item_id;
    private Sale sale;
	private int product_id;
	private String product;
	private String variant;
	private double priceEach;
	private int quantity;
	private double lineTotal;

	public Long getId() {
		return item_id;
	}

	public void setId(Long id) {
		this.item_id = id;
	}

	public Sale getSale() {
		return sale;
	}

	public void setSale(Sale sale) {
		this.sale = sale;
	}

	public int getProduct_id() {
		return product_id;
	}

	public void setProduct_id(int product_id) {
		this.product_id = product_id;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getVariant() {
		return variant;
	}

	public void setVariant(String variant) {
		this.variant = variant;
	}

	public double getPriceEach() {
		return priceEach;
	}

	public void setPriceEach(double priceEach) {
		this.priceEach = priceEach;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public double getLineTotal() {
		return lineTotal;
	}

	public void setLineTotal(double lineTotal) {
		this.lineTotal = lineTotal;
	}
	
	@Override
	public String toString() {
		String vResult = "{";
		vResult += "\"item_id\": " + item_id + ", ";
		vResult += "\"sale_id\": " + sale.getSale_id() + ", ";
		vResult += "\"product_id\": " + product_id + ", ";
		vResult += "\"product\": \"" + product + "\", ";
		vResult += "\"variant\": \"" + variant + "\", ";
		vResult += "\"priceEach\": " + priceEach + ", ";
		vResult += "\"quantity\": " + quantity + ", ";
		vResult += "\"lineTotal\": " + lineTotal;
		vResult += "}";
		return vResult;
	}
}
