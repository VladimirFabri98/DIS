package vladimir.api.composite.game;

public class ServiceAddresses {

	private final String cmg;
	private final String game;
	private final String rev;
	private final String dlc;
	private final String event;
	
	public ServiceAddresses() {
		super();
		this.cmg = null;
		this.game = null;
		this.rev = null;
		this.dlc = null;
		this.event = null;
	}
	
	public ServiceAddresses(String compositeGameAddress, String gameAddress, String reviewAddress, 
			String dlcAdress, String eventAdress ) {
		this.cmg = compositeGameAddress;
		this.game = gameAddress;
		this.rev = reviewAddress;
		this.dlc = dlcAdress;
		this.event = eventAdress;	
	}

	public String getCmg() {
		return cmg;
	}

	public String getGame() {
		return game;
	}

	public String getRev() {
		return rev;
	}

	public String getDlc() {
		return dlc;
	}

	public String getEvent() {
		return event;
	}

}
