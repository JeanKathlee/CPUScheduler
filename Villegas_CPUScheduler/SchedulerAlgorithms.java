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
        for (Process p : plist) processes.add(new Process(p.pid, p.arrival, p.burst));

        int n = processes.size(), time = 0, completed = 0, lastIdx = -1;
        int totalTurnaround = 0, totalResponse = 0, totalWaiting = 0;
        boolean[] started = new boolean[n];
        StringBuilder gantt = new StringBuilder("Gantt Chart:\n|");

        while (completed < n) {
            int idx = -1, minRem = Integer.MAX_VALUE;

            for (int i = 0; i < n; i++) {
                Process p = processes.get(i);
                if (p.arrival <= time && p.remaining > 0 && p.remaining < minRem) {
                    minRem = p.remaining;
                    idx = i;
                }
            }

            if (idx == -1) {
                time++;
                continue;
            }

            if (lastIdx != -1 && lastIdx != idx) {
                time += contextSwitch;
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
                totalResponse += p.response;
                totalWaiting += p.waiting;
                completed++;
                lastIdx = -1;
            }
        }

        System.out.println(gantt);
        printTable(processes, totalTurnaround, totalWaiting, totalResponse);
    }

    public static void roundRobin(List<Process> plist, int quantum) {
        List<Process> processes = new ArrayList<>();
        for (Process p : plist) processes.add(new Process(p.pid, p.arrival, p.burst));
        int n = processes.size(), time = 0, completed = 0, lastPid = -1;
        int totalTurnaround = 0, totalResponse = 0, totalWaiting = 0;
        boolean[] started = new boolean[n], inQueue = new boolean[n];
        Queue<Integer> q = new LinkedList<>();
        StringBuilder gantt = new StringBuilder("Gantt Chart:\n|");
        int arrived = 0;

        while (completed < n) {
            while (arrived < n && processes.get(arrived).arrival <= time) {
                q.add(arrived);
                inQueue[arrived] = true;
                arrived++;
            }
            if (q.isEmpty()) {
                time++;
                continue;
            }

            int idx = q.poll();
            Process p = processes.get(idx);
            if (!started[idx]) {
                p.start = time;
                p.response = time - p.arrival;
                started[idx] = true;
            }
            int exec = Math.min(quantum, p.remaining);
            if (lastPid != p.pid) {
                gantt.append(" P").append(p.pid).append(" |");
                lastPid = p.pid;
            }
            for (int t = 0; t < exec; t++) {
                time++;
                while (arrived < n && processes.get(arrived).arrival <= time) {
                    if (!inQueue[arrived]) {
                        q.add(arrived);
                        inQueue[arrived] = true;
                    }
                    arrived++;
                }
            }
            p.remaining -= exec;
            if (p.remaining == 0) {
                p.completion = time;
                p.turnaround = p.completion - p.arrival;
                p.waiting = p.turnaround - p.burst;
                totalTurnaround += p.turnaround;
                totalResponse += p.response;
                totalWaiting += p.waiting;
                completed++;
            } else {
                q.add(idx);
            }
        }
        System.out.println(gantt);
        printTable(processes, totalTurnaround, totalWaiting, totalResponse);
    }

    public static void mlfq(List<Process> plist, int[] quantums) {
        List<Process> processes = new ArrayList<>();
        for (Process p : plist) processes.add(new Process(p.pid, p.arrival, p.burst));
        int n = processes.size(), time = 0, completed = 0, lastPid = -1;
        int totalTurnaround = 0, totalResponse = 0, totalWaiting = 0;
        boolean[] started = new boolean[n];
        Queue<Integer>[] queues = new LinkedList[quantums.length];
        for (int i = 0; i < quantums.length; i++) queues[i] = new LinkedList<>();
        StringBuilder gantt = new StringBuilder("Gantt Chart:\n|");
        int arrived = 0;

        while (completed < n) {
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
            if (lastPid != p.pid) {
                gantt.append(" P").append(p.pid).append("(Q").append(qIdx).append(") |");
                lastPid = p.pid;
            }
            for (int t = 0; t < exec; t++) {
                time++;
                while (arrived < n && processes.get(arrived).arrival <= time) {
                    queues[0].add(arrived);
                    arrived++;
                }
            }
            p.remaining -= exec;
            if (p.remaining == 0) {
                p.completion = time;
                p.turnaround = p.completion - p.arrival;
                p.waiting = p.turnaround - p.burst;
                totalTurnaround += p.turnaround;
                totalResponse += p.response;
                totalWaiting += p.waiting;
                completed++;
            } else {
                if (qIdx < quantums.length - 1) queues[qIdx + 1].add(idx);
                else queues[qIdx].add(idx);
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
