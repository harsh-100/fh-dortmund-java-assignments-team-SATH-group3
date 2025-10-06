package selfAssignment3;

public class MaterialResources extends NonHumanResources{
	
	private Position position;
	private double quantityNeeded;
	
	
	public MaterialResources(String id, String name, double cost, Position position, double quantityNeeded) {
		super(id, name, cost);
		this.setPosition(position);
		this.setQuantityNeeded(quantityNeeded);
	}
	
	public double getResources() {
		return quantityNeeded * getCost();
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public double getQuantityNeeded() {
		return quantityNeeded;
	}

	public void setQuantityNeeded(double quantityNeeded) {
		this.quantityNeeded = quantityNeeded;
	}
}
