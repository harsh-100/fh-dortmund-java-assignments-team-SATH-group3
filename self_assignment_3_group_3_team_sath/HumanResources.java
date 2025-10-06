package selfAssignment3;

public class HumanResources extends Resources{
	private String skillLevel;
	private int hoursPerOperation;
	private double costPerHour;
	
	public HumanResources(String id, String name, String skillLevel, int hoursPerOperation, double costPerHour) {
		super(id, name);
		this.setSkillLevel(skillLevel);
		this.setHoursPerDay(hoursPerOperation);
		this.setCostPerHour(costPerHour);
	}
	
	public double getResources() {
		return hoursPerOperation * costPerHour;
	}
	
	// getters and setters

	public String getSkillLevel() {
		return skillLevel;
	}

	public void setSkillLevel(String skillLevel) {
		this.skillLevel = skillLevel;
	}

	public int getHoursPerDay() {
		return hoursPerOperation;
	}

	public void setHoursPerDay(int hoursPerDay) {
		this.hoursPerOperation = hoursPerDay;
	}

	public double getCostPerHour() {
		return costPerHour;
	}

	public void setCostPerHour(double costPerHour) {
		this.costPerHour = costPerHour;
	}
	
	
}
