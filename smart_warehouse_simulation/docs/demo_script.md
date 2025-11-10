Smart Warehouse — Demo Script (one page)

Goal

Quick demo to showcase: UI (Dashboard/AGV/Inventory/Logs), scenario flows, logging, exception reporting, and concurrency behavior.

Length

~5–8 minutes interactive, 10–12 minutes with brief explanation.

Steps

1. Setup (30s)
   - Ensure you built the project: `mvn -DskipTests compile`.
   - Start the app: `mvn javafx:run` and open the Dashboard window.

2. Dashboard overview (30s)
   - Point out the Simulation status, Pending / Completed counters, and the Robots list.
   - Explain that robots run in background threads and TaskManager uses concurrent collections.

3. Scenario A — single order (60s)
   - Click `Scenario A`.
   - Observe the Pending/Completed counters change, watch robot states in AGV panel.
   - Open `Logs` and show the robot log lines and SYSTEM log entries.

4. Scenario B — concurrency stress (90–120s)
   - Click `Scenario B`.
   - Explain that many orders are being enqueued quickly and multiple robots will process tasks.
   - Point out that TaskManager concurrency test exists (unit test) and that the UI remains responsive.

5. Scenario C — charging behavior (90–120s)
   - Click `Scenario C`.
   - This drains robot batteries and forces charging station activity; open `AGV Panel` and `Logs` to show charging events.

6. Logs & Exceptions (60s)
   - Open `Logs` and show tailing of the active log file and the in-memory exception buffer (if any exceptions occurred).
   - Mention `exceptions.ExceptionStore` keeps a recent buffer for UI display.

7. Wrap-up (30s)
   - Mention next steps: polish Inventory UI, add domain-specific exceptions and more unit/integration tests, produce a runnable artifact for demo machines.

Notes for presenter

- If a scenario needs longer time, the ScenarioRunner stops the simulation automatically after a configured duration.
- The Dashboard disables scenario buttons while one runs to prevent accidental double-starts.
- To run headless tests or CI-friendly demos, I can add a small CLI runner that invokes the ScenarioRunner programmatically (useful for automated verification).

Files to reference during Q&A

- `app.demo.ScenarioRunner` — scenario flows and durations
- `tasks/TaskManager` — concurrency design
- `logging/LogManager` and `logging/LogService` — logging design and in-memory tail buffer
- `exceptions/ExceptionStore` — in-memory exception buffer shown in UI

End of script
