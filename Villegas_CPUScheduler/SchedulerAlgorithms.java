import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class SchedulerAlgorithms {

    public static void fcfs(List<Process> plist) {
        plist.sort(Comparator.comparingInt((Process p) -> p.arrival).thenComparingInt(p -> p.pid));
        int time = 0, totalTurnaround = 0, totalResponse = 0, totalWaiting = 0;
        StringBuilder gantt = new StringBuilder("Gantt Chart:\n|");
        for (Process p : plist) {
            if (time < p.arrival) time = p.arrival;
            p.start = time;
            p.response = time - p.arrival;
            time += p.burst;
            p.completion = time;
            p.turnaround = p.completion - p.arrival;
            p.waiting = p.turnaround - p.burst;
            totalTurnaround += p.turnaround;
            totalResponse += p.response;
            totalWaiting += p.waiting;
            gantt.append(" P" + p.pid + " |");
        }
        System.out.println(gantt);
        printTable(plist, totalTurnaround, totalWaiting, totalResponse);
    }

    // ✅ NEW: Non-Preemptive Shortest Job First
    public static void sjf(List<Process> plist) {
        List<Process> processes = new ArrayList<>();
        for (Process p : plist) processes.add(new Process(p.pid, p.arrival, p.burst));

        int n = processes.size();
        int time = 0, completed = 0;
        int totalTurnaround = 0, totalWaiting = 0, totalResponse = 0;
        boolean[] done = new boolean[n];
        StringBuilder gantt = new StringBuilder("Gantt Chart:\n|");

        while (completed < n) {
            int idx = -1;
            int minBurst = Integer.MAX_VALUE;

            for (int i = 0; i < n; i++) {
                Process p = processes.get(i);
                if (!done[i] && p.arrival <= time && p.burst < minBurst) {
                    minBurst = p.burst;
                    idx = i;
                }
            }

            if (idx == -1) {
                time++;
                continue;
            }

            Process p = processes.get(idx);
            p.start = time;
            p.response = time - p.arrival;
            time += p.burst;
            p.completion = time;
            p.turnaround = p.completion - p.arrival;
            p.waiting = p.turnaround - p.burst;

            totalTurnaround += p.turnaround;
            totalWaiting += p.waiting;
            totalResponse += p.response;
            done[idx] = true;
            completed++;

            gantt.append(" P").append(p.pid).append(" |");
        }

        System.out.println(gantt);
        printTable(processes, totalTurnaround, totalWaiting, totalResponse);
    }

    public static void srtf(List<Process> plist, int contextSwitch) {
    List<Process> processes = new ArrayList<>();
    for (Process p : plist)
        processes.add(new Process(p.pid, p.arrival, p.burst));

    int n = processes.size();
    int time = 0, completed = 0;
    int totalTurnaround = 0, totalWaiting = 0, totalResponse = 0;

    boolean[] started = new boolean[n];
    StringBuilder gantt = new StringBuilder("Gantt Chart:\n|");
    int lastIdx = -1;
    boolean inContextSwitch = false;
    int contextRemaining = 0;
    int nextIdx = -1;

    while (completed < n) {
        // Find the process with the shortest remaining time
        int idx = -1, minRemaining = Integer.MAX_VALUE;

        for (int i = 0; i < n; i++) {
            Process p = processes.get(i);
            if (p.arrival <= time && p.remaining > 0 && p.remaining < minRemaining) {
                minRemaining = p.remaining;
                idx = i;
            }
        }

        if (inContextSwitch) {
            gantt.append(" CS |");
            time++;
            contextRemaining--;
            if (contextRemaining == 0) {
                inContextSwitch = false;
                lastIdx = nextIdx;
            }
            continue;
        }

        if (idx == -1) {
            gantt.append(" idle |");
            time++;
            continue;
        }

        if (lastIdx != -1 && lastIdx != idx) {
            // Context switch needed
            inContextSwitch = true;
            contextRemaining = contextSwitch;
            nextIdx = idx;
            continue;
        }

        Process p = processes.get(idx);

        if (!started[idx]) {
            p.start = time;
            p.response = time - p.arrival;
            started[idx] = true;
        }

        if (lastIdx != idx) {
            gantt.append(" P").append(p.pid).append(" |");
        }

        p.remaining--;
        time++;
        lastIdx = idx;

        if (p.remaining == 0) {
            p.completion = time;
            p.turnaround = p.completion - p.arrival;
            p.waiting = p.turnaround - p.burst;
            totalTurnaround += p.turnaround;
            totalWaiting += p.waiting;
            totalResponse += p.response;
            completed++;
            // Don’t reset lastIdx here to avoid false context switch
        }
    }

    System.out.println(gantt);
    printTable(processes, totalTurnaround, totalWaiting, totalResponse);
}



public static void roundRobin(List<Process> plist, int quantum) {
    List<Process> processes = new ArrayList<>();
    for (Process p : plist)
        processes.add(new Process(p.pid, p.arrival, p.burst));

    int n = processes.size();
    int time = 0, completed = 0;
    int totalTurnaround = 0, totalWaiting = 0, totalResponse = 0;
    Queue<Integer> queue = new LinkedList<>();
    boolean[] visited = new boolean[n];
    boolean[] started = new boolean[n];
    StringBuilder gantt = new StringBuilder("Gantt Chart:\n|");

    // Sort by arrival
    processes.sort(Comparator.comparingInt(p -> p.arrival));
    int arrived = 0;

    // Add first arriving processes
    while (arrived < n && processes.get(arrived).arrival <= time) {
        queue.add(arrived);
        visited[arrived] = true;
        arrived++;
    }

    if (queue.isEmpty() && arrived < n) {
        // jump to the arrival of the first process
        time = processes.get(arrived).arrival;
        queue.add(arrived);
        visited[arrived] = true;
        arrived++;
    }

    int lastPid = -1;
    while (completed < n) {
        if (queue.isEmpty()) {
            gantt.append(" idle |");
            time++;
            // Check new arrivals during idle
            while (arrived < n && processes.get(arrived).arrival <= time) {
                queue.add(arrived);
                visited[arrived] = true;
                arrived++;
            }
            continue;
        }

        int idx = queue.poll();
        Process p = processes.get(idx);

        if (!started[idx]) {
            p.start = time;
            p.response = time - p.arrival;
            started[idx] = true;
        }

        if (lastPid != p.pid) {
            gantt.append(" P").append(p.pid).append(" |");
            lastPid = p.pid;
        }

        int exec = Math.min(quantum, p.remaining);
        for (int t = 0; t < exec; t++) {
            time++;
            p.remaining--;

            // Check for new arrivals every time unit
            while (arrived < n && processes.get(arrived).arrival <= time) {
                if (!visited[arrived]) {
                    queue.add(arrived);
                    visited[arrived] = true;
                }
                arrived++;
            }

            if (p.remaining == 0) break;
        }

        if (p.remaining == 0) {
            p.completion = time;
            p.turnaround = p.completion - p.arrival;
            p.waiting = p.turnaround - p.burst;
            totalTurnaround += p.turnaround;
            totalWaiting += p.waiting;
            totalResponse += p.response;
            completed++;
        } else {
            queue.add(idx); // Re-add unfinished process
        }
    }

    System.out.println(gantt);
    printTable(processes, totalTurnaround, totalWaiting, totalResponse);
}




   public static void mlfq(List<Process> plist, int[] quantums) {
    List<Process> processes = new ArrayList<>();
    for (Process p : plist)
        processes.add(new Process(p.pid, p.arrival, p.burst));

    int n = processes.size();
    int time = 0, completed = 0, lastPid = -1;
    int totalTurnaround = 0, totalWaiting = 0, totalResponse = 0;

    boolean[] started = new boolean[n];
    Queue<Integer>[] queues = new LinkedList[quantums.length];
    for (int i = 0; i < quantums.length; i++)
        queues[i] = new LinkedList<>();

    processes.sort(Comparator.comparingInt(p -> p.arrival));
    int arrived = 0;
    StringBuilder gantt = new StringBuilder("Gantt Chart:\n|");

    while (completed < n) {
        // Add newly arrived processes to Q0
        while (arrived < n && processes.get(arrived).arrival <= time) {
            queues[0].add(arrived);
            arrived++;
        }

        int qIdx = -1;
        for (int i = 0; i < quantums.length; i++) {
            if (!queues[i].isEmpty()) {
                qIdx = i;
                break;
            }
        }

        if (qIdx == -1) {
            gantt.append(" idle |");
            time++;
            continue;
        }

        int idx = queues[qIdx].poll();
        Process p = processes.get(idx);

        if (!started[idx]) {
            p.start = time;
            p.response = time - p.arrival;
            started[idx] = true;
        }

        int exec = Math.min(quantums[qIdx], p.remaining);
        gantt.append(" P").append(p.pid).append("(Q").append(qIdx).append(") |");

        for (int t = 0; t < exec; t++) {
            time++;
            p.remaining--;

            // Add newly arriving processes to Q0 during execution
            while (arrived < n && processes.get(arrived).arrival <= time) {
                queues[0].add(arrived);
                arrived++;
            }

            if (p.remaining == 0) break;
        }

        if (p.remaining == 0) {
            p.completion = time;
            p.turnaround = p.completion - p.arrival;
            p.waiting = p.turnaround - p.burst;
            totalTurnaround += p.turnaround;
            totalWaiting += p.waiting;
            totalResponse += p.response;
            completed++;
        } else {
            // Demote if not completed and not already in last queue
            if (qIdx < quantums.length - 1)
                queues[qIdx + 1].add(idx);
            else
                queues[qIdx].add(idx); // Stay in Q3
        }
    }

    System.out.println(gantt);
    printTable(processes, totalTurnaround, totalWaiting, totalResponse);
}


    private static void printTable(List<Process> plist, int totalTurnaround, int totalWaiting, int totalResponse) {
        int n = plist.size();
        System.out.printf("%-6s%-12s%-10s%-15s%-15s%-15s%-15s\n", "PID", "Arrival", "Burst", "Completion", "Turnaround", "Waiting", "Response");
        for (Process p : plist) {
            System.out.printf("%-6d%-12d%-10d%-15d%-15d%-15d%-15d\n", p.pid, p.arrival, p.burst, p.completion, p.turnaround, p.waiting, p.response);
        }
        System.out.println("\nAverages:");
        System.out.printf("Average Turnaround Time: %.2f\n", totalTurnaround / (double) n);
        System.out.printf("Average Waiting Time: %.2f\n", totalWaiting / (double) n);
        System.out.printf("Average Response Time: %.2f\n", totalResponse / (double) n);
    }
}
