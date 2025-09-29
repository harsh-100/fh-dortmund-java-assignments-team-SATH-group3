import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


public class IndustrialProcess {
    private String id;
    private List<IOperation> operations;

    public IndustrialProcess(String id) {
        this.id = id;
        this.operations = new ArrayList<>();
    }

    public IndustrialProcess() {
        this("IP-" + System.currentTimeMillis());
    }


    public String getId() { return id; }
    public List<IOperation> getOperations() { return new ArrayList<>(operations); }


    public void setId(String id) { this.id = id; }


    public void addOperation(IOperation operation) {
        if (!operations.contains(operation)) {
            operations.add(operation);
        }
    }



    public void removeOperation(IOperation operation) {
        operations.remove(operation);
    }


    public LocalTime getProcessDuration() {
        int totalMinutes = 0;
        int totalHours = 0;

        for (IOperation operation : operations) {
            LocalTime duration = operation.getDuration();
            totalHours += duration.getHour();
            totalMinutes += duration.getMinute();
        }

        totalHours += totalMinutes / 60;
        totalMinutes = totalMinutes % 60;

        return LocalTime.of(totalHours % 24, totalMinutes);
    }


    public List<AGV> getProcessResources() {
        List<AGV> allResources = new ArrayList<>();

        for (IOperation operation : operations) {
            List<AGV> operationResources = operation.getResources();
            for (AGV agv : operationResources) {
                if (!allResources.contains(agv)) {
                    allResources.add(agv);
                }
            }
        }

        return allResources;
    }



    public double calculateTotalEnergyConsumption() {
        double totalConsumption = 0.0;

        for (IOperation operation : operations) {
            if (operation instanceof Operation) {
                totalConsumption += ((Operation) operation).calculateEnergyConsumption();
            }
        }

        return totalConsumption;
    }


    public ProcessStatistics getStatistics() {
        return new ProcessStatistics(
                getProcessDuration(),
                getProcessResources().size(),
                calculateTotalEnergyConsumption()
        );
    }

    @Override
    public String toString() {
        return String.format("IndustrialProcess[ID=%s, Operations=%d, Duration=%s, AGVs=%d]",
                id, operations.size(), getProcessDuration(), getProcessResources().size());
    }



    public static class ProcessStatistics {
        private final LocalTime totalDuration;
        private final int totalAGVs;
        private final double totalEnergyConsumption;

        public ProcessStatistics(LocalTime totalDuration, int totalAGVs, double totalEnergyConsumption) {
            this.totalDuration = totalDuration;
            this.totalAGVs = totalAGVs;
            this.totalEnergyConsumption = totalEnergyConsumption;
        }

        public LocalTime getTotalDuration() { return totalDuration; }
        public int getTotalAGVs() { return totalAGVs; }
        public double getTotalEnergyConsumption() { return totalEnergyConsumption; }

        @Override
        public String toString() {
            return String.format("ProcessStatistics[Duration=%s, AGVs=%d, Energy=%.2f kWh]",
                    totalDuration, totalAGVs, totalEnergyConsumption);
        }
    }
}