public class Process {
    public int pid, arrival, burst, completion, turnaround, waiting, response, start;
    public int remaining;
    public boolean started = false;

    public Process(int pid, int arrival, int burst) {
        this.pid = pid;
        this.arrival = arrival;
        this.burst = burst;
        this.remaining = burst;
    }
}