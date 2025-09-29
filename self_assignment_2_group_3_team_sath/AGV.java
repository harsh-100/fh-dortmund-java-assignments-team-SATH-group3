import java.time.LocalTime;


public class AGV {
    private String id;
    private double batteryLoad;
    private double corruption;
    private LocalTime chargingTime;
    private Position position;
    private float maxSpeed;
    private float actualSpeed;

    public AGV(String id) {
        this.id = id;
        this.batteryLoad = 100.0; // Start with full battery
        this.corruption = 0.0;
        this.chargingTime = LocalTime.of(0, 0);
        this.position = new Position();
        this.maxSpeed = 5.0f; // Default max speed
        this.actualSpeed = 0.0f;
    }


    public String getId() { return id; }
    public double getBatteryLoad() { return batteryLoad; }
    public double getCorruption() { return corruption; }
    public LocalTime getChargingTime() { return chargingTime; }
    public Position getPosition() { return position; }
    public float getMaxSpeed() { return maxSpeed; }
    public float getActualSpeed() { return actualSpeed; }


    public void setId(String id) { this.id = id; }
    public void setBatteryLoad(double batteryLoad) {
        this.batteryLoad = Math.max(0, Math.min(100, batteryLoad));
    }
    public void setCorruption(double corruption) {
        this.corruption = Math.max(0, corruption);
    }
    public void setChargingTime(LocalTime chargingTime) {
        this.chargingTime = chargingTime;
    }
    public void setPosition(Position position) { this.position = position; }
    public void setMaxSpeed(float maxSpeed) { this.maxSpeed = maxSpeed; }
    public void setActualSpeed(float actualSpeed) {
        this.actualSpeed = Math.min(actualSpeed, maxSpeed);
    }



    public void setData(Object data) {
        if (data instanceof Position) {
            setPosition((Position) data);
        } else if (data instanceof Double) {
            setBatteryLoad((Double) data);
        } else if (data instanceof Float) {
            setActualSpeed((Float) data);
        }
    }


    public Object getData(String dataType) {
        switch (dataType.toLowerCase()) {
            case "position":
                return position;
            case "battery":
                return batteryLoad;
            case "speed":
                return actualSpeed;
            case "corruption":
                return corruption;
            default:
                return null;
        }
    }


    public double calculateEnergyConsumption(double operationTime) {
        // Energy consumption formula: base consumption + speed factor + corruption factor
        double baseConsumption = 0.5; // kWh per hour
        double speedFactor = actualSpeed * 0.1;
        double corruptionFactor = corruption * 0.05;

        return (baseConsumption + speedFactor + corruptionFactor) * operationTime;
    }

    @Override
    public String toString() {
        return String.format("AGV[ID=%s, Battery=%.1f%%, Speed=%.1f/%.1f, Position=%s]",
                id, batteryLoad, actualSpeed, maxSpeed, position);
    }
}