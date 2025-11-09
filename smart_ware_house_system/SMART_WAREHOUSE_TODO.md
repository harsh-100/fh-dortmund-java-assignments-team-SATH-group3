# Smart Warehouse MVP - Detailed Task List

This file breaks the high-level MVP plan into explicit, actionable tasks. Each task has:

- A short goal/title
- A detailed description and the concrete sub-steps to implement it
- Acceptance criteria (how we verify it's done)
- Suggested files/locations to change
- Dependencies and estimated effort (Low/Med/High)

Use the exact task title when assigning an item to the agent so it can start work immediately.

---

## 1. Create new Maven + JavaFX project folder

Goal
- Prepare a standalone project folder named `smart_warehouse_simulation` that contains a Maven structure and will host the UI + reused backend.

Description / Steps
1. Create `smart_warehouse_simulation/` at repository root.
2. Inside it create `pom.xml`, `src/main/java`, `src/main/resources`, `src/test/java`.
3. Add a README.md with short description and the minimal build/run commands.
4. Do not modify existing homework project files — this is a clean, separate copy.

Acceptance criteria
- Folder exists with the expected structure and a placeholder `pom.xml`.

Files to create/edit
- `smart_warehouse_simulation/pom.xml` (placeholder)
- `smart_warehouse_simulation/README.md`

Dependencies
- none

Effort: Low

---

## 2. Copy backend sources into the new project

Goal
- Copy the relevant backend code into the new project while preserving package structure so UI code can use the backend directly.

Description / Steps
1. Identify backend packages in `homework_assignment_3_group_3_team_sath/src/` (e.g., `storage`, `tasks`, `robots`, `charging`, `logging`, `exceptions`, `utils`, `warehouse`).
2. Recursively copy `.java` files from those packages into `smart_warehouse_simulation/src/main/java` maintaining package directories.
3. Remove or adapt any absolute file paths or environment-specific settings in the copied code (e.g., absolute log paths in `Robot`).
4. Run an initial `mvn -DskipTests compile` to find compile issues.

Acceptance criteria
- All backend `.java` files are present under `smart_warehouse_simulation/src/main/java` and package declarations are unchanged.
- `mvn -DskipTests compile` runs without fatal errors (may still show warnings)

Files to create/edit
- Multiple under `smart_warehouse_simulation/src/main/java/<package>`

Dependencies
- Task 1

Effort: Medium

---

## 3. Add and validate pom.xml (JavaFX + build)

Goal
- Provide a working `pom.xml` with Java (11+), JavaFX dependencies, and build plugins.

Description / Steps
1. Create `pom.xml` with at least:
   - `<maven-compiler-plugin>` (source/target 11+)
   - JavaFX dependencies or `org.openjfx:javafx-bom` import
   - `javafx-maven-plugin` or `exec-maven-plugin` to run the application
2. Add dependencies used by the backend if missing (e.g., junit for tests, any other libs used).
3. Run `mvn -DskipTests clean package` (requires network) and iterate until packaging completes.

Acceptance criteria
- `mvn -DskipTests clean package` completes successfully on a machine with network access.

Files to create/edit
- `smart_warehouse_simulation/pom.xml`

Dependencies
- Tasks 1–2

Effort: Medium

---

## 4. Make backend compile in the new project

Goal
- Fix package/import issues and environment-specific code so the copied backend compiles under `smart_warehouse_simulation`.

Description / Steps
1. Run `mvn -DskipTests compile` and inspect compiler errors.
2. Replace absolute file paths used by `LogManager` or `Robot` with relative `./logs` or configurable property.
3. Ensure any resources (e.g., log directories) are created at runtime; update constructors to use `Paths.get("logs")` or a configurable location.
4. Fix any package name mismatches or missing imports.

Acceptance criteria
- `mvn -DskipTests compile` succeeds locally.

Files to create/edit
- Any backend source files that reference absolute paths or have package issues (e.g., `robots/Robot.java`, `logging/LogManager.java`)

Dependencies
- Tasks 1–3

Effort: Medium

---

## 5. Add a simple JavaFX starter app

Goal
- Provide a minimal JavaFX application that launches a window and can load the UI skeleton.

Description / Steps
1. Under `src/main/java/app/` create `App.java` that extends `javafx.application.Application` and loads `Main.fxml` or displays a simple scene.
2. Add `src/main/resources/fxml/Main.fxml` (basic layout) and CSS if needed.
3. Wire `pom.xml` so `mvn javafx:run` launches the application.

Acceptance criteria
- `mvn javafx:run` launches a window titled `Smart Warehouse Demo`.

Files to create/edit
- `src/main/java/app/App.java`
- `src/main/resources/fxml/Main.fxml`
- `pom.xml` (ensure javafx plugin configured)

Dependencies
- Tasks 1–4

Effort: Low–Medium

---

## 6. Design UI skeleton (FXML + controllers)

Goal
- Build the UI skeleton with FXML screens and controllers in an MVC layout.

Description / Steps
1. Create FXML screens:
   - `Dashboard.fxml` (warehouse overview)
   - `AGVPanel.fxml` (AGV control)
   - `Inventory.fxml` (inventory management)
   - `Logs.fxml` (logs & errors)
2. For each FXML create a controller class under `app.controller` that exposes methods used by the UI (start/stop simulation, add item, create order).
3. Add a navigation controller or simple menu to switch screens.

Acceptance criteria
- JavaFX app can navigate between all four screens; controllers construct without exceptions.

Files to create/edit
- `src/main/resources/fxml/*.fxml`
- `src/main/java/app/controller/*.java`

Dependencies
- Tasks 1–5

Effort: Medium–High

---

## 7. Implement Warehouse Dashboard view

Goal
- Show system-level info: inventory summary, list of AGVs and positions, simulation status controls.

Description / Steps
1. Dashboard should show a table/list of storage units and their item counts.
2. Show a list of robots with ID, battery level, state (IDLE/WORKING/CHARGING) and current task.
3. Add Start/Stop buttons for the simulation and a readout of the number of pending/completed tasks.
4. For the demo, implement a simple polling mechanism (every 500ms) to refresh fields from backend.

Acceptance criteria
- Dashboard displays storage counts and robot list; fields update while simulation runs.

Files to create/edit
- `Dashboard.fxml`, `DashboardController.java`

Dependencies
- Tasks 5–6, 11, 13

Effort: Medium

---

## 8. Implement AGV Control Panel

Goal
- Provide per-robot controls and real-time task views.

Description / Steps
1. Show all robots with controls: Start, Stop, Force Recharge, Assign Manual Task (create a Tasks object and add it to TaskManager).
2. Allow selecting a robot and viewing its current task details.
3. Implement buttons to enqueue tasks manually (pop-up input dialog for item id/destination).

Acceptance criteria
- Users can start/stop robots and assign tasks via the UI; assigned tasks appear in the robot's view and in the backend queue.

Files to create/edit
- `AGVPanel.fxml`, `AGVPanelController.java`

Dependencies
- Tasks 5–7, 11, 13

Effort: Medium–High

---

## 9. Implement Inventory Management UI

Goal
- CRUD operations for Items and StorageUnits and an interface to create Orders which turn into tasks.

Description / Steps
1. Implement Add / Remove Item dialog and list view that shows all items.
2. Implement StorageUnit view showing contained items; allow moving items between storage units if desired.
3. Provide an Order creation form that chooses items and quantity and when submitted calls `TaskManager.createTasksFromOrders(order)`.

Acceptance criteria
- Items can be added/removed and orders can be created; creating an order populates TaskManager with tasks reflected in the UI.

Files to create/edit
- `Inventory.fxml`, `InventoryController.java`

Dependencies
- Tasks 5–7, 11

Effort: High

---

## 10. Implement Logs & Error Display screen

Goal
- Provide a UI view to read recent log lines (system, per-robot, charging) and display exception details.

Description / Steps
1. Implement a tail-like view for `logs/SYSTEM-<date>.log` and a per-robot log file selector.
2. When `ExceptionHandler` logs an exception, also push it into an in-memory buffer the UI can read (or the UI can read files directly).
3. Provide a filter by severity and search box.

Acceptance criteria
- UI shows recent lines and stacktraces; exceptions thrown in backend (testable with a small fault) appear in the Logs screen.

Files to create/edit
- `Logs.fxml`, `LogsController.java`, small updates to `exceptions/ExceptionHandler.java` to optionally notify UI listeners.

Dependencies
- Tasks 4, 5, 11, 12

Effort: Medium

---

## 11. Hook up logging framework and standardize logs

Goal
- Ensure consistent logging across backend and that UI can access logs.

Description / Steps
1. Update `logging.LogManager` to write per-robot, per-station and SYSTEM logs to `smart_warehouse_simulation/logs/`.
2. Provide a small wrapper that also keeps an in-memory rolling buffer (e.g., last 500 lines) for UI quick access.
3. Replace any absolute log paths in `Robot`, `ChargingStation`, etc. with a config-driven path.

Acceptance criteria
- Log files are produced under `logs/` with the naming convention and the UI can read recent entries from the in-memory buffer.

Files to create/edit
- `logging/LogManager.java`, `robots/Robot.java`, `charging/ChargingStation.java`, `exceptions/ExceptionHandler.java`

Dependencies
- Tasks 1–5

Effort: Medium

---

## 12. Enhance ExceptionHandler & add custom exceptions

Goal
- Create domain-specific exceptions and ensure exception logs are usable by UI and tests.

Description / Steps
1. Define exceptions: `TaskConflictException`, `ResourceUnavailableException`, `InvalidOrderException` under `exceptions/`.
2. Update `TaskManager`, `StorageUnit`, and `ChargingStation` to throw/use these exceptions where appropriate.
3. Ensure `ExceptionHandler` logs full stacktrace and also stores exceptions in an in-memory list for UI consumption.

Acceptance criteria
- Exceptions are thrown in appropriate scenarios and the UI Logs screen shows them with stacktraces.

Files to create/edit
- `exceptions/*.java`, `tasks/TaskManager.java`, `storage/StorageUnit.java`, `charging/ChargingStation.java`, `exceptions/ExceptionHandler.java`

Dependencies
- Tasks 4, 11

Effort: Medium–High

---

## 13. AGV concurrency & safety (backend)

Goal
- Make TaskManager and Robot interactions thread-safe and test concurrency scenarios.

Description / Steps
1. Review TaskManager methods that mutate shared state; add synchronization or concurrent collections as needed (e.g., `ConcurrentLinkedDeque`, `ConcurrentHashMap`).
2. Add unit tests that spawn multiple robots threads and assert no lost tasks or conflicting assignments.
3. Add short-run integration test (in test/ or a demo runner) that enqueues many tasks and checks eventual completion counts.

Acceptance criteria
- No data races in the tested scenarios; tests pass locally.

Files to create/edit
- `tasks/TaskManager.java`, `test/` concurrency tests

Dependencies
- Tasks 2, 4

Effort: High

---

## 14. Integrate UI with multithreaded simulation

Goal
- Ensure UI updates safely from background threads (robots) and stays responsive.

Description / Steps
1. Use JavaFX `Platform.runLater()` to update UI elements from background threads.
2. Use an executor or background updater to poll backend every X ms and push updates to UI.
3. Ensure heavy operations run off the JavaFX Application Thread.

Acceptance criteria
- UI does not throw `IllegalStateException` related to thread access during simulation and remains responsive.

Files to create/edit
- Controllers and small utility classes for background polling

Dependencies
- Tasks 6–8, 13

Effort: Medium–High

---

## 15. Demo scenarios and scripted flows

Goal
- Create pre-baked demo flows to demonstrate concurrency, logging, and exception handling.

Description / Steps
1. Build three scripts that programmatically perform sequences:
   - Scenario A: Single order, single robot — show full lifecycle.
   - Scenario B: Multiple orders, multiple robots — demonstrate concurrency and conflict resolution.
   - Scenario C: Drain battery on a robot to trigger charging station behavior.
2. Expose each scenario as a single-button action in the UI or a CLI script.

Acceptance criteria
- Each scenario runs end-to-end and produces logs that can be inspected.

Files to create/edit
- `app/demo/ScenarioRunner.java`, UI buttons in DashboardController

Dependencies
- Tasks 7–14

Effort: Medium

---

## 16. Documentation & slides snippets

Goal
- Prepare a README and a 1–2 page demo script for professor presentation explaining the Java concepts demonstrated.

Description / Steps
1. Write `README.md` with build/run steps, demo instructions, and list of key Java topics (concurrency, synchronization, logging, exception handling).
2. Prepare a one-page cheat-sheet describing where in the code each concept is implemented (files and line ranges).

Acceptance criteria
- README contains step-by-step demo instructions and a short explanation of the showcased concepts.

Files to create/edit
- `smart_warehouse_simulation/README.md`, `docs/demo_script.md`

Dependencies
- Tasks 1–15

Effort: Low–Medium

---

## 17. Unit & integration tests (JUnit)

Goal
- Add JUnit tests that validate storage, tasks, TaskManager concurrency and integration flows.

Description / Steps
1. Add test classes for `storage.Item`, `storage.Order`, `storage.StorageUnit` (simple assertions).
2. Add `TaskManagerConcurrencyTest` that spawns N threads to simulate robots and asserts that all tasks are processed.
3. Add a small integration test `OrderToRobotTest` that creates an order and asserts that a robot completes expected tasks (this may be an integration-style test that runs faster by shortening timers).

Acceptance criteria
- `mvn test` passes on a machine with network access (JUnit dependency available).

Files to create/edit
- `src/test/java/...` test classes and `pom.xml` test dependencies

Dependencies
- Tasks 3, 13

Effort: High

---

## 18. Packaging for demo (runnable artifact)

Goal
- Produce a runnable artifact (jar or packaged distribution) that can be launched on a demo machine.

Description / Steps
1. Configure Maven Shade or assembly plugin to produce a runnable jar (beware JavaFX modules — test locally).
2. Test the artifact on a clean machine / VM to ensure JavaFX runtime is present or provide instructions to install it.

Acceptance criteria
- Packaged artifact can launch the JavaFX app on a demo machine following README instructions.

Files to create/edit
- `pom.xml` packaging config; optionally scripts in `packaging/`

Dependencies
- Tasks 3, 5

Effort: Medium–High

---

## 19. Polish & UX improvements

Goal
- Make UI suitable for a professor-facing demo: clear, responsive, and configurable.

Description / Steps
1. Add pause/resume controls and sliders for the number of robots and update rates.
2. Improve layout and add color-coded states for robots (IDLE=green, WORKING=orange, CHARGING=blue).
3. Add tooltips and small help text describing what each control demonstrates.

Acceptance criteria
- UI is intuitive and configurable; professor can change simulation parameters at runtime.

Files to create/edit
- Controllers, CSS files, small helper classes

Dependencies
- Tasks 6–15

Effort: Medium

---

## 20. CI / build validation

Goal
- Add a GitHub Actions workflow to run builds on push/PR.

Description / Steps
1. Add `.github/workflows/maven.yml` that runs `mvn -DskipTests clean package`.
2. For CI that requires JavaFX, use a headless test or skip JavaFX execution in CI.

Acceptance criteria
- Pull requests trigger the workflow and show the build status.

Files to create/edit
- `.github/workflows/maven.yml`

Dependencies
- Tasks 3, 17

Effort: Low–Medium

---

## How to assign tasks to the agent

- Use the exact task title (e.g., "Implement AGV Control Panel") when asking the agent to start working.
- If a task is large, include the subtask ID or a short note (e.g., "Implement AGV Control Panel — UI only") to scope it further.

---

If you want, I can now start on Task #1 (create the Maven/JavaFX project skeleton) and produce the initial `pom.xml` and README in `smart_warehouse_simulation/`.
