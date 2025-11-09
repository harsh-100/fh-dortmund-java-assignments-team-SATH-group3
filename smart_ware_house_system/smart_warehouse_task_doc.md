# Smart Warehouse Simulation Project

**Project Context:**
This project simulates a **smart warehouse system** with AGVs (Automated Guided Vehicles), inventory management, concurrency, logging, and exception handling. The current codebase is based on your previous homework project (`homework_assignment_3_group_3_team_sath`). The agent’s task is to **set up the project**, maintain the backend, and develop the **UI and MVP features** for demonstration.

**Agent:** Single agent responsible for full implementation.

---

## Phase 1: Project Setup

**Objective:** Prepare a working Maven + JavaFX project using the existing backend code.

### Tasks:

1. **Create New Project Folder**
   - Folder Name: `smart_warehouse_simulation`
   - Ensure it is a **clean copy**, without modifying the original homework project.

2. **Copy Existing Backend Code**
   - Source folder: `homework_assignment_3_group_3_team_sath`
   - Copy **all Java classes** related to backend:
     - Inventory management
     - AGV control
     - Data models, services, and controllers
   - Maintain package structure.

3. **Setup Maven**
   - Ensure `pom.xml` includes:
     - Java version compatible with JavaFX
     - Dependencies for JavaFX
     - Any other libraries used in previous homework (logging, concurrency frameworks)
   - Test Maven build: `mvn clean install` → confirm no errors.

4. **Setup JavaFX**
   - Configure project to run JavaFX applications.
   - Ensure the agent can **launch a sample JavaFX window** from this project.
   - Keep backend and frontend modular.

5. **Initial Testing**
   - Verify backend methods (from homework) work correctly.
   - Ensure Maven build + JavaFX run without issues.

**Deliverable:** Fully setup Maven + JavaFX project with backend ready for UI development.

---

## Phase 2: UI & Feature Development

**Objective:** Develop a structured **UI MVP** demonstrating smart warehouse simulation features, concurrency, logging, and exception handling.

### 2.1: UI Structure

The UI should have **different sections** representing warehouse operations:

1. **Warehouse Dashboard**
   - Display inventory status
   - Track AGV positions
   - Show simulation status

2. **AGV Control Panel**
   - Start/stop AGV movement
   - Display real-time tasks for each AGV
   - Visualize AGV concurrency operations

3. **Inventory Management**
   - Add/remove products
   - Update stock levels
   - Simulate warehouse tasks

4. **Logs & Error Display**
   - Show logs of operations
   - Display any exceptions or errors

**Agent Tasks for UI:**
- Develop each section as **separate JavaFX screens or FXML files**
- Use **MVC structure**:
  - **Model:** Backend data
  - **View:** JavaFX UI
  - **Controller:** Link backend logic to UI

---

### 2.2: Feature Implementation Tasks

1. **Concurrency & AGV Simulation**
   - Implement **multithreading** for AGVs to simulate real-time warehouse operations.
   - Show how multiple AGVs can work concurrently without conflicts.

2. **Logging**
   - Configure logging framework (e.g., `java.util.logging` or `log4j`)
   - Log important actions:
     - AGV movements
     - Inventory changes
     - Errors/exceptions

3. **Exception Handling**
   - Add **try-catch blocks** for:
     - Invalid inputs
     - Task conflicts between AGVs
     - Backend errors
   - Implement **custom exceptions** for warehouse-specific operations.

---

### 2.3: MVP Planning

- Each UI section should be **independently testable**
- Backend logic must remain reusable across UI screens
- MVP should **clearly demonstrate**:
  - UI navigation
  - Concurrency in AGV operations
  - Logging of actions
  - Exception handling in tasks

**Agent Tasks for MVP:**
1. Complete Warehouse Dashboard UI
2. Complete AGV Control Panel UI with concurrency simulation
3. Complete Inventory Management UI
4. Integrate Logging & Exception handling
5. Test full workflow for demonstration

---

### Important Notes

- Reuse **existing backend code** as much as possible.
- Maintain **Maven project structure** and **JavaFX modularity**.
- Keep each feature **independently functional** for professor evaluation.
- Document screens, concurrency demo, logging, and exception handling clearly for presentation.

