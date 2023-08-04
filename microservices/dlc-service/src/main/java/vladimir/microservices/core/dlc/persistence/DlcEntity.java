package vladimir.microservices.core.dlc.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "dlcs")
@CompoundIndex(name = "game-dlc-id", unique = true, def = "{'gameId':1, 'dlcId':1, }")
public class DlcEntity {

	@Id
	private String id;

	@Version
	private Integer version;

	private int gameId;
	private int dlcId;
	private String name;
	private double price;

	public DlcEntity() {
		super();
	}

	public DlcEntity(int gameId, int dlcId, String name, double price) {
		super();
		this.gameId = gameId;
		this.dlcId = dlcId;
		this.name = name;
		this.price = price;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getGameId() {
		return gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
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
	
	public Integer getVersion() {
		return version;
	}

}
