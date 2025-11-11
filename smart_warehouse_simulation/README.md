Smart Warehouse Simulation (MVP)

This folder will host a standalone Maven + JavaFX project that reuses the backend code from the homework project and provides a UI for demonstration.

Quick start (after dependencies are available):

# Build (skip tests)
mvn -DskipTests clean package

# Run from maven (JavaFX plugin)
mvn javafx:run

# Or compile and run manually
javac -d out $(find src -name '*.java')
java -cp out app.App

Notes:
- The project expects Java 11+ and JavaFX runtime available for your Java version.
- If Maven can't download dependencies in your environment, copy the backend code into this folder and compile locally with javac.


I need : 

- Right now robots do NOT navigate to pickup or drop-off points. 
- Tasks carry an Item (and now an orderId), but Tasks.destination is unused. 
- Robots simulate work by polling the TaskManager, setting status IN_PROGRESS, then running a TASK_DURATION counter , when the counter expires the robot calls completeTask(). 
- There is no movement, pickup, or delivery logic implemented.
