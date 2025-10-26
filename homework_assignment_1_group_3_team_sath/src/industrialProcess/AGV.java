package industrialProcess;
import java.time.Duration;
import resources.HardwareResources;
import utils.Position;
import storage.Item;
import tasks.Tasks;

public class AGV extends HardwareResources{

	private double batteryLoad;
	private double consumption; // a number of percent drying out per hour of work (moving from point A to B doesn't consume battery)
	private Duration chargingTime; // duration of charging AGV's battery for 1%
	private float maxSpeed; //as I use position class to handle positioning, I assume that maxSpeed - is a number of points AGV can pass per minute (from 0*0 to 10*10 can go per 1 minute if its maxSpeed is 100)
	private float actSpeed; 
	private String status; // "idle", "moving", "charging"
	private Tasks currentTask; // item being transported	

	
	public AGV(String id, String name, Position position, double batteryLoad, double consumption, Duration chargingTime, float maxSpeed, float actSpeed) {
		// super(id, name, cost, position); removing the cost as AGV we are not considering cost here
		super(id, name, 0, position);
		this.setBatteryLoad(batteryLoad);
		this.setConsumption(consumption);
		this.setChargingTime(chargingTime);
		this.setMaxSpeed(maxSpeed);
		this.setActSpeed(actSpeed);
		this.status = "idle";
		this.currentTask = null;
	}

	public AGV(String id, String name, Position position, double batteryLoad, double consumption, Duration chargingTime, float maxSpeed) {
		this(id, name, position, batteryLoad, consumption, chargingTime, maxSpeed, maxSpeed);
	}

	public double getBatteryLoad() {
		return batteryLoad;
	}

	public void setBatteryLoad(double batteryLoad) {
		this.batteryLoad = batteryLoad;
	}

	public double getConsumption() {
		return consumption;
	}

	public void setConsumption(double consumption) {
		this.consumption = consumption;
	}

	public float getMaxSpeed() {
		return maxSpeed;
	}


	public void setMaxSpeed(float maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	public Duration getChargingTime() {
		return chargingTime;
	}

	public Tasks getCurrentTask() {
		return currentTask;
	}

	public void setCurrentTask(Tasks currentTask) {
		this.currentTask = currentTask;
	}

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public void setChargingTime(Duration chargingTime) {
		this.chargingTime = chargingTime;
	}

	public float getActSpeed() {
		return actSpeed;
	}

	public void setActSpeed(float actSpeed) {
		this.actSpeed = actSpeed;
	}

	public boolean isAvailable(){
		return status.equals("idle");
	}
	
	
}
