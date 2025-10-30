import industrialProcess.AGV;
import tasks.TaskManager;
import tasks.Tasks;
import storage.Item;
import utils.Position;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class Main {
	public static void main(String[] args) throws Exception {
		// create two AGVs
		AGV agv1 = new AGV("AGV-001", "Carrier-1", new Position(0,0), 95.0, 0.5, Duration.ofMinutes(5), 5.0f, 4.0f);
		AGV agv2 = new AGV("AGV-002", "Carrier-2", new Position(1,1), 88.0, 0.6, Duration.ofMinutes(6), 4.5f, 4.5f);

		// create TaskManager
		TaskManager mgr = new TaskManager();

		// create sample tasks
		List<Item> itemsA = new ArrayList<>();
		itemsA.add(new Item("I-001", "Widget", 2.5));
		Tasks t1 = new Tasks("T1", new Position(5,5), itemsA);

		List<Item> itemsB = new ArrayList<>();
		itemsB.add(new Item("I-002", "Gadget", 1.2));
		Tasks t2 = new Tasks("T2", new Position(3,2), itemsB);

		mgr.addTask(t1);
		mgr.addTask(t2);

		// assign tasks to AGVs - this will log assignment
		boolean assigned1 = mgr.assignTaskToRobot(agv1);
		boolean assigned2 = mgr.assignTaskToRobot(agv2);

		System.out.println("Assigned1=" + assigned1 + ", Assigned2=" + assigned2);

		// simulate completion of first task (this will log completion)
		mgr.removeTask("T1");

		System.out.println("Done. Check ./logs for AGV-<id>-<date>.log files containing assignment and completion entries.");
	}
}

