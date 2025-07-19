# CPU Scheduling Simulator (Java Console App)

This is a **Java-based CPU Scheduling Simulator** that supports five classic scheduling algorithms:

- First-Come, First-Served (FCFS)
- Shortest Job First (SJF)
- Shortest Remaining Time First (SRTF)
- Round Robin (RR) ✅ with configurable time quantum
- Multi-Level Feedback Queue (MLFQ) ✅ with user-defined time quantums for each level

How to use :

🖱️ GUI Mode only
You can run the graphical user interface version:
javac CPUSchedulingGUI.java
java CPUSchedulingGUI

🧠 Scheduling Algorithm Descriptions
1. First-Come, First-Served (FCFS)
Non-preemptive scheduling.
Processes are scheduled in the order of their arrival times.
Simple and fair but may lead to convoy effect (long processes block shorter ones).

2. Shortest Job First (SJF)
Non-preemptive algorithm.
Selects the process with the shortest burst time from the ready queue.
Optimizes average turnaround time but suffers from starvation of long processes.

3. Shortest Remaining Time First (SRTF)
Preemptive version of SJF.
At every time unit, picks the process with the least remaining burst time.
More responsive than SJF but involves frequent context switching.

4. Round Robin (RR)
Preemptive scheduling with a fixed time quantum.
Each process gets CPU time in a circular queue.
Fair to all processes; best for time-sharing systems, but performance depends on the quantum size.

5. Multilevel Feedback Queue (MLFQ)
Preemptive, multi-level queue-based algorithm with different time quantums per level.
Processes start in the highest-priority queue; if they don’t finish, they move to a lower-priority queue.
Good balance between responsiveness and fairness; dynamically adapts to process behavior.

---
ScreenShots
 
![image alt](https://github.com/JeanKathlee/CPUScheduler/blob/05a363ef348955ff3944a24607d42386ed520231/Screenshot%202025-07-17%20174625.png)

![image alt](https://github.com/JeanKathlee/CPUScheduler/blob/25deb2db7d146a41b37bd2a13173638628496b5b/Screenshot%202025-07-17%20174700.png)

Sample input-
Number of processes: 4

Enter process details:
Process 1 - Arrival Time: 0, Burst Time: 5
Process 2 - Arrival Time: 1, Burst Time: 4
Process 3 - Arrival Time: 2, Burst Time: 6
Process 4 - Arrival Time: 3, Burst Time: 3

Select Scheduling Algorithm:
4. Round Robin

Enter Time Quantum:
2

Sample output-
PID    Arrival  Burst  Completion  Turnaround  Waiting  Response
P1     0        5        15          15           10         0         
P2     1        4        12          11           7          1         
P3     2        6        18          16           10         2         
P4     3        3        16          13           10         5         

Average Turnaround Time: 13.75
Average Waiting Time   : 9.25
Average Response Time  : 2.00

🔧 Known Bugs / Limitations / Incomplete Features
-Inaccurate Context Switch Handling:
The current implementation does not correctly simulate or reflect the delay or time cost of context switching between processes, which may slightly affect the timing of process completion and metrics like turnaround or waiting time in certain scenarios.


-Limited Error Handling for Input:
Manual input mode assumes correct user input (e.g., non-negative integers), and invalid data may cause unexpected behavior.

-Gantt Chart Time Markers Missing:
The current Gantt chart only displays the order of process execution (e.g., P1 P2 P1 P3) without showing actual time markers (e.g., 0 P1 4 P2 6 P1). This makes it harder to trace the exact time each process starts and ends.

-No Time Allotment for MLFQ:
The Multi-Level Feedback Queue (MLFQ) implementation does not use time allotments for each queue level. Processes are demoted only after using up the time quantum, without consideration for a fixed time budget per level.

Author
Jean Kathleen R. Villegas

## 📂 Project Structure
CPU-Scheduling-Simulator/
├── CPUSchedulingGUI.java         # GUI-based simulator (Java Swing)
├── CPUSchedulingConsole.java     # Console-based simulator
├── SchedulerAlgorithms.java      # Core scheduling algorithm implementations
├── Process.java                  # Process model class
└── README.md                     # Project documentation

