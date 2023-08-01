package vladimir.api.core.dlc;

public class Dlc {

	private int dlcId;
	private int gameId;
	private String name;
	private double price;
	private final String serviceAdress;

	public Dlc(int dlcId, int gameId, String name, double price, String serviceAdress) {
		super();
		this.dlcId = dlcId;
		this.gameId = gameId;
		this.name = name;
		this.price = price;
		this.serviceAdress = serviceAdress;
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

	public String getServiceAdress() {
		return serviceAdress;
	}

}
