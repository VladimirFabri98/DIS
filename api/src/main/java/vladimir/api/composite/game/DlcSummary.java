package vladimir.api.composite.game;

public class DlcSummary {

	private int dlcId;
	private String name;
	private double price;

	public DlcSummary(int dlcId, String name, double price) {
		super();
		this.dlcId = dlcId;
		this.name = name;
		this.price = price;
	}

	public int getDlcId() {
		return dlcId;
	}

	public void setDlcId(int dlcId) {
		this.dlcId = dlcId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

}
