package selfAssignment3;

public class NonHumanResources extends Resources{
	private double cost;
	
	public NonHumanResources(String id, String name, double cost) {
		super(id, name);
		this.setCost(cost);
	}
	

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}
	
	
}
