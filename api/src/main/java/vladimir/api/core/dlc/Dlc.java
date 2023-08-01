package vladimir.api.core.dlc;

public class Dlc {

	private int dlcId;
	private int gameId;
	private String name;
	private double price;
	private final String serviceAddress;

	public Dlc() {
		this.dlcId = 0;
		this.gameId = 0;
		this.name = null;
		this.price = 0;
		this.serviceAddress = null;
	}
	
	public Dlc(int dlcId, int gameId, String name, double price, String serviceAddress) {
		super();
		this.dlcId = dlcId;
		this.gameId = gameId;
		this.name = name;
		this.price = price;
		this.serviceAddress = serviceAddress;
	}

	public int getDlcId() {
		return dlcId;
	}

	public void setDlcId(int dlcId) {
		this.dlcId = dlcId;
	}

	public int getGameId() {
		return gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
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

	public String getServiceAddress() {
		return serviceAddress;
	}

}
