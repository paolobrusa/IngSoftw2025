# *Galaxy Trucker Remake*

![Voto](https://img.shields.io/badge/Voto-29%2F30-brightgreen?style=for-the-badge)

**Gruppo AM31:** [Paolo Brusa](https://github.com/paolobrusa), [Giovanni Farruggio](https://github.com/giovanni-farruggio), [Diletta Del Ponte](https://github.com/dildelponte), [Edoardo Albini](https://github.com/edoardoalbini)

This project is a Java-based implementation developed at Politecnico di Milano of the table game *Galaxy Trucker* by Cranio Creations without graphic copyright contents.

## Implemented Features

| Feature                         | Implemented |
|:--------------------------------|:-----------:|
| Complete rules                  |      ✅      |
| TUI                             |      ✅      |
| GUI                             |      ✅      |
| RMI                             |      ✅      |
| Socket                          |      ✅      |
| Learning flight (volo di prova) |      ✅      |
| Multiple games                  |      ✅      |
| Persistence                     |      ❌      |
| Resilience to disconnections    |      ❌      |


## Running the Application

This application is distributed as a standalone JAR file. Before running it, please read the following:

### Requirements
- **Java Development Kit (JDK) 23 or higher**  
  The JAR was compiled using **JDK 23**. It **will not run** on earlier versions. Please ensure that your system has JDK 23 or newer installed.
- **Networking Requirements**  
  The application server uses port 1234 for RMI server and port 1235 for Socket server (arbitrary).
  These ports must not be in use on the host machine. If either port is occupied, the application will fail to start.

### Platform Compatibility
- The JAR was compiled and tested **only on Windows**.
- It includes **JavaFX native libraries for Windows only**.  
  As a result:
    - The **connectivity and text-based user interface (TUI)** are expected to work on other operating systems.
    - The **graphical user interface (GUI)** will **not work** on non-Windows platforms unless recompiled locally.

#### Running on Other Operating Systems
If you are using **Linux** or **macOS** and wish to use the **GUI**, rebuild the project locally (see [Building from Source](#building-from-source)).
Maven automatically downloads the JavaFX native libraries matching the operating system it is run on, so the JAR built on your machine will include the correct ones.

### Running the application via JAR
- Open the terminal in the directory where the file is located (that being `/deliverables` in the project directory)
- Run the jar normally:
```
java -jar IS25-AM31.jar
```
- A launcher prompt will appear. Type one of the following commands:

| Command                                                     | Description                                              |
|:------------------------------------------------------------|:---------------------------------------------------------|
| `run server`                                                | Starts the server (RMI on port 1234, Socket on port 1235) |
| `run rmi client tui` / `run rmi client gui`                 | Starts a client connected via RMI with TUI or GUI        |
| `run socket client tui` / `run socket client gui`           | Starts a client connected via Socket with TUI or GUI     |
| `quit`                                                      | Closes the launcher                                      |

When starting a client you will be asked for the server IP address (use `127.0.0.1` if the server runs on the same machine).

## Building from Source

### Prerequisites
- **JDK 23 or higher** (`java -version` and `javac -version` must report 23+, and `JAVA_HOME` must point to it)
- **Maven** is *not* required to be installed: the project includes the Maven Wrapper (`mvnw` / `mvnw.cmd`), which downloads Maven 3.8.5 on first use.
- An internet connection the first time, to download the dependencies (JavaFX 23.0.1, ControlsFX, Jackson).

### Build
From the root of the project:

**Windows (PowerShell)**
```
.\mvnw.cmd clean package
```

**Windows (cmd)**
```
mvnw.cmd clean package
```

**Linux / macOS**
```
./mvnw clean package
```

The build compiles the sources, runs the tests and produces, in the `target/` directory, the **shaded (fat) JAR** `IS25-AM31-1.0-SNAPSHOT.jar`, which already contains all the dependencies (JavaFX, Jackson, ...) and whose entry point is `it.polimi.ingsw.is25am31.Main`.
`target/original-IS25-AM31-1.0-SNAPSHOT.jar` is the plain JAR without dependencies: do not use it, it will fail at runtime.

To skip the tests, add `-DskipTests`:
```
./mvnw clean package -DskipTests
```

### Run the built JAR
```
java -jar target/IS25-AM31-1.0-SNAPSHOT.jar
```
and use the launcher commands described [above](#running-the-application-via-jar).

### Run the GUI directly from Maven (development)
```
./mvnw clean javafx:run
```
This launches the JavaFX GUI entry point (`view.gui.Gui`) without going through the launcher.
