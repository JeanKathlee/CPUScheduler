# CPU Scheduling Simulator (Java Console App)

This is a **Java-based CPU Scheduling Simulator** that supports five classic scheduling algorithms:

- First-Come, First-Served (FCFS)
- Shortest Job First (SJF)
- Shortest Remaining Time First (SRTF)
- Round Robin (RR) ✅ with configurable time quantum
- Multi-Level Feedback Queue (MLFQ) ✅ with user-defined time quantums for each level

---

📌 Features

- Simulates different CPU scheduling algorithms.
- Allows manual or random process input.
- Displays a detailed Gantt Chart in ASCII.
- Shows full process metrics:
  - Completion Time
  - Turnaround Time
  - Waiting Time
  - Response Time
- Computes average values for each metric.
- Time quantum input for:
  - Round Robin (time slice)
  - MLFQ (separate time slices per level)

---
ScreenShots
 
![image alt](https://github.com/JeanKathlee/CPUScheduler/blob/05a363ef348955ff3944a24607d42386ed520231/Screenshot%202025-07-17%20174625.png)

![image alt](https://github.com/JeanKathlee/CPUScheduler/blob/25deb2db7d146a41b37bd2a13173638628496b5b/Screenshot%202025-07-17%20174700.png)
## 📂 Project Structure
CPU-Scheduling-Simulator/
├── CPUSchedulingGUI.java         # GUI-based simulator (Java Swing)
├── CPUSchedulingConsole.java     # Console-based simulator
├── SchedulerAlgorithms.java      # Core scheduling algorithm implementations
├── Process.java                  # Process model class
└── README.md                     # Project documentation


How to use :

🖱️ GUI Mode only
You can run the graphical user interface version:
javac CPUSchedulingGUI.java
java CPUSchedulingGUI

Author
Jean Kathleen R. Villegas
