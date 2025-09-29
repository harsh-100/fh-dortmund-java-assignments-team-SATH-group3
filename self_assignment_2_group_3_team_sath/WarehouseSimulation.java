import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class WarehouseSimulation {
    private List<IndustrialProcess> processes;
    private List<AGV> agvFleet;

    public WarehouseSimulation() {
        this.processes = new ArrayList<>();
        this.agvFleet = new ArrayList<>();
        initializeAGVFleet();
    }


    private void initializeAGVFleet() {
        for (int i = 1; i <= 5; i++) {
            AGV agv = new AGV("AGV-" + String.format("%03d", i));
            agv.setMaxSpeed(4.0f + i * 0.5f); // Varying max speeds
            agv.setBatteryLoad(90.0 + i * 2); // Varying battery levels
            agvFleet.add(agv);
        }
    }


    public IndustrialProcess createSampleProcess(String processId) {
        IndustrialProcess process = new IndustrialProcess(processId);


        Operation loading = new Operation("OP001", "Load items from storage");
        loading.setNominalTime(LocalTime.of(0, 30)); // 30 minutes
        loading.addResource(agvFleet.get(0));
        loading.addResource(agvFleet.get(1));

        Operation transport = new Operation("OP002", "Transport to processing area");
        transport.setNominalTime(LocalTime.of(0, 45)); // 45 minutes
        transport.addResource(agvFleet.get(1));
        transport.addResource(agvFleet.get(2));

        Operation unloading = new Operation("OP003", "Unload at destination");
        unloading.setNominalTime(LocalTime.of(0, 20)); // 20 minutes
        unloading.addResource(agvFleet.get(2));

        process.addOperation(loading);
        process.addOperation(transport);
        process.addOperation(unloading);

        return process;
    }


    public void addProcess(IndustrialProcess process) {
        processes.add(process);
    }


    public void runSimulation() {
        System.out.println("=== Warehouse Industrial Process Simulation ===\n");


        for (int i = 1; i <= 3; i++) {
            IndustrialProcess process = createSampleProcess("PROCESS-" + i);
            addProcess(process);
        }


        displaySimulationResults();
    }


    private void displaySimulationResults() {
        System.out.println("AGV Fleet Status:");
        System.out.println("-----------------");
        for (AGV agv : agvFleet) {
            System.out.println(agv);
        }

        System.out.println("\nProcess Analysis:");
        System.out.println("-----------------");

        LocalTime totalSystemDuration = LocalTime.of(0, 0);
        double totalSystemEnergy = 0.0;
        int totalSystemAGVs = 0;

        for (int i = 0; i < processes.size(); i++) {
            IndustrialProcess process = processes.get(i);
            IndustrialProcess.ProcessStatistics stats = process.getStatistics();

            System.out.printf("Process %d: %s\n", i + 1, process.getId());
            System.out.printf("  Duration: %s\n", stats.getTotalDuration());
            System.out.printf("  AGVs Required: %d\n", stats.getTotalAGVs());
            System.out.printf("  Energy Consumption: %.2f kWh\n", stats.getTotalEnergyConsumption());


            System.out.println("  Operations:");
            for (IOperation operation : process.getOperations()) {
                System.out.printf("    - %s\n", operation);
            }


            LocalTime processDuration = stats.getTotalDuration();
            totalSystemDuration = addTime(totalSystemDuration, processDuration);
            totalSystemEnergy += stats.getTotalEnergyConsumption();
            totalSystemAGVs = Math.max(totalSystemAGVs, stats.getTotalAGVs());

            System.out.println();
        }


        System.out.println("System Totals:");
        System.out.println("--------------");
        System.out.printf("Total Processing Time: %s\n", totalSystemDuration);
        System.out.printf("Maximum Concurrent AGVs: %d\n", totalSystemAGVs);
        System.out.printf("Total Energy Consumption: %.2f kWh\n", totalSystemEnergy);
        System.out.printf("Average Energy per Process: %.2f kWh\n",
                processes.isEmpty() ? 0 : totalSystemEnergy / processes.size());
    }


    private LocalTime addTime(LocalTime time1, LocalTime time2) {
        int totalMinutes = time1.getMinute() + time2.getMinute();
        int totalHours = time1.getHour() + time2.getHour() + (totalMinutes / 60);
        totalMinutes = totalMinutes % 60;
        totalHours = totalHours % 24;

        return LocalTime.of(totalHours, totalMinutes);
    }


    public static void main(String[] args) {
        WarehouseSimulation simulation = new WarehouseSimulation();
        simulation.runSimulation();

        System.out.println("\n=== Simulation Complete ===");
    }
}