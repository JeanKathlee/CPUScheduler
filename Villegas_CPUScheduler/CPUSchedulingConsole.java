import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class CPUSchedulingConsole {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("CPU Scheduling Simulator (Console Mode)");
        System.out.println("Select input mode:");
        System.out.println("1. Manual process entry");
        System.out.println("2. Random process generator");
        int mode = sc.nextInt();
        int n;
        List<Process> processes = new ArrayList<>();
        if (mode == 1) {
            System.out.print("Enter number of processes: ");
            n = sc.nextInt();
            for (int i = 0; i < n; i++) {
                System.out.println("Process " + (i+1) + ":");
                System.out.print("Arrival Time: ");
                int arrival = sc.nextInt();
                System.out.print("Burst Time: ");
                int burst = sc.nextInt();
                processes.add(new Process(i+1, arrival, burst));
            }
        } else {
            System.out.print("Enter number of processes: ");
            n = sc.nextInt();
            Random rand = new Random();
            for (int i = 0; i < n; i++) {
                int arrival = rand.nextInt(5*n);
                int burst = rand.nextInt(9) + 1;
                processes.add(new Process(i+1, arrival, burst));
            }
        }
        System.out.println("Select scheduling algorithm:");
        System.out.println("1. FCFS");
        System.out.println("2. SJF (Non-Preemptive)");
        System.out.println("3. SRTF (Preemptive)");
        System.out.println("4. Round Robin");
        System.out.println("5. Multilevel Feedback Queeue (MLFQ)");
        int algo = sc.nextInt();
        int quantum = 0;
        int[] mlfqQuantums = new int[4];
        if (algo == 4) {
            System.out.print("Enter time quantum for Round Robin: ");
            quantum = sc.nextInt();
        } else if (algo == 5) {
            for (int i = 0; i < 4; i++) {
                System.out.print("Enter time quantum for MLFQ Q" + i + ": ");
                mlfqQuantums[i] = sc.nextInt();
            }
        }
        switch (algo) {
            case 1: SchedulerAlgorithms.fcfs(processes); break;
            case 2: SchedulerAlgorithms.sjf(processes); break;
            case 3: SchedulerAlgorithms.srtf(processes); break;
            case 4: SchedulerAlgorithms.roundRobin(processes, quantum); break;
            case 5: SchedulerAlgorithms.mlfq(processes, mlfqQuantums); break;
            default: System.out.println("Invalid selection");
        }
        sc.close();
    }
}