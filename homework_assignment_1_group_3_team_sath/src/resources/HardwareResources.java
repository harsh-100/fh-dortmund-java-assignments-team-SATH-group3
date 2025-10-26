package resources;

import utils.Position;

public class HardwareResources extends NonHumanResources{

	private Position position;

	public HardwareResources(String id, String name, double cost, Position position) {
		super(id, name, cost);
		this.setPosition(position);
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}
}
