package resources;
import java.time.Duration;

public class SoftwareResources extends NonHumanResources{
	private String licenseType;
	private Duration hoursOfUse;

	public SoftwareResources(String id, String name, double cost, String licenseType, Duration hoursOfUse) {
		super(id, name, cost);
		this.setLicenseType(licenseType);
		this.hoursOfUse = hoursOfUse;
	}

	public double getResources() {
		if ("per-seat".equalsIgnoreCase(licenseType)) {
			return getCost();
		} else if ("subscription".equalsIgnoreCase(licenseType)) {
			return (getCost() / 24 * 30) * hoursOfUse.toHours();
		} else return 0;
	}

	public String getLicenseType() {
		return licenseType;
	}

	public void setLicenseType(String licenseType) {
		this.licenseType = licenseType;
	}

	public Duration gethoursOfUse() {
		return hoursOfUse;
	}

	public void setHoursOfUse(Duration hoursOfUse) {
		this.hoursOfUse = hoursOfUse;
	}
}
