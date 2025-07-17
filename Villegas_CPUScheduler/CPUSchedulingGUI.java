import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;




public class CPUSchedulingGUI extends JFrame {
    private JPanel algoPanel;
    private JPanel inputPanel;
    private JSlider speedSlider;
    private GanttPanel ganttPanel;
    private JComboBox<String> algoBox;
    private JTextField nameField, arrivalField, burstField;
    private JTextField quantumField;
    private JTextField[] mlfqFields = new JTextField[4];
    private JButton addButton, randomButton, runButton, clearButton;
    private JTable processTable;
    private DefaultTableModel liveStatusModel;
    private JTable liveStatusTable;
    private String cachedMetrics = "";
    private JLabel liveStatusBar;
    private JProgressBar totalProgressBar;
    private JPanel liveProgressPanel;
    private java.util.Map<Integer, JProgressBar> progressBars = new java.util.HashMap<>();



    private DefaultTableModel tableModel;
    private JTextArea metricsArea;
    private JPanel metricsPanel;

    private List<Process> processList = new ArrayList<>();
    private JPanel quantumPanel, mlfqPanel;
    private JLabel statusBar;
    private JProgressBar liveProgressBar;


    // Step mode and export controls
    private JCheckBox stepModeCheck;
    private JButton nextStepButton, exportButton;
    private JTextField contextSwitchField;
    private List<GUIScheduler.GanttSegment> stepSegments;
    private int stepIndex;

    public CPUSchedulingGUI() {
        setTitle("CPU Scheduling Simulator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(950, 750);
        setLocationRelativeTo(null);
        //setLayout(new BorderLayout(10,10));

        // Set dark theme colors
        Color bg = new Color(36, 37, 42);
        Color fg = new Color(220, 220, 220);
        Color accent = new Color(0x4F81BD);
        Font font = new Font("Segoe UI", Font.PLAIN, 16);
        Font fontBold = new Font("Segoe UI", Font.BOLD, 18);
        BackgroundPanel bgPanel = new BackgroundPanel("bg.jpg");
         bgPanel.setLayout(new BorderLayout(10,10));
        setContentPane(bgPanel);


        // Top panel: Input
     




         inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setBackground(bg);
        JLabel nameLbl = new JLabel("Name:"); nameLbl.setForeground(fg); nameLbl.setFont(font);
        fieldsPanel.add(nameLbl);
        nameField = new JTextField(5); nameField.setFont(font); fieldsPanel.add(nameField);
        JLabel arrLbl = new JLabel("Arrival:"); arrLbl.setForeground(fg); arrLbl.setFont(font);
        fieldsPanel.add(arrLbl);
        arrivalField = new JTextField(5); arrivalField.setFont(font); fieldsPanel.add(arrivalField);
        JLabel burstLbl = new JLabel("Burst:"); burstLbl.setForeground(fg); burstLbl.setFont(font);
        fieldsPanel.add(burstLbl);
        burstField = new JTextField(5); burstField.setFont(font); fieldsPanel.add(burstField);
        addButton = new JButton("Add Process");
        styleButton(addButton, accent, fg, fontBold);
        fieldsPanel.add(addButton);
        randomButton = new JButton("Random");
        styleButton(randomButton, accent, fg, fontBold);
        fieldsPanel.add(randomButton);
        inputPanel.add(fieldsPanel);

   JPanel algoContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
algoContainer.setBackground(bg);

algoPanel = new JPanel();
algoPanel.setLayout(new BoxLayout(algoPanel, BoxLayout.Y_AXIS));
algoPanel.setBackground(bg);

// add components to algoPanel as before...

algoContainer.add(algoPanel);
inputPanel.add(algoContainer); // instead of inputPanel.add(algoPanel);


algoPanel.setBackground(bg);

// Algorithm row
JPanel algoRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

algoRow.setBackground(bg);
JLabel algoLbl = new JLabel("Algorithm:");
algoLbl.setForeground(fg); 
algoLbl.setFont(font);
algoRow.add(algoLbl);

algoBox = new JComboBox<>(new String[]{"FCFS","SJF","SRTF","Round Robin","MLFQ"});
algoBox.setFont(font);
algoRow.add(algoBox);

// ✅ Center align this row:
algoRow.setAlignmentX(CENTER_ALIGNMENT);
algoPanel.add(algoRow);

// Quantum panel
quantumPanel = new JPanel();
quantumPanel.setBackground(bg);
JLabel quantumLbl = new JLabel("Quantum:");
quantumLbl.setForeground(fg);
quantumLbl.setFont(font);
quantumPanel.add(quantumLbl);
quantumField = new JTextField(4);
quantumField.setFont(font);
quantumPanel.add(quantumField);

// ✅ Center align this:
quantumPanel.setAlignmentX(CENTER_ALIGNMENT);
algoPanel.add(quantumPanel);

// MLFQ panel
mlfqPanel = new JPanel();
mlfqPanel.setBackground(bg);
for(int i=0;i<4;i++) {
    JLabel qLbl = new JLabel("Q"+i+":");
    qLbl.setForeground(fg);
    qLbl.setFont(font);
    mlfqPanel.add(qLbl);
    mlfqFields[i] = new JTextField(3);
    mlfqFields[i].setFont(font);
    mlfqPanel.add(mlfqFields[i]);
}

// ✅ Center align this:
mlfqPanel.setAlignmentX(CENTER_ALIGNMENT);
algoPanel.add(mlfqPanel);

// Context Switch panel
JPanel ctxRow = new JPanel();
ctxRow.setLayout(new BoxLayout(ctxRow, BoxLayout.X_AXIS));
ctxRow.setBackground(bg);
ctxRow.setAlignmentX(CENTER_ALIGNMENT);
ctxRow.setOpaque(false); // optional

JPanel ctxInner = new JPanel();
ctxInner.setLayout(new FlowLayout(FlowLayout.CENTER));
ctxInner.setBackground(bg);
JLabel ctxLbl = new JLabel("Context Switch:");
ctxLbl.setForeground(fg);
ctxLbl.setFont(font);

contextSwitchField = new JTextField("0", 5);
contextSwitchField.setFont(font);


ctxInner.add(ctxLbl);
ctxInner.add(contextSwitchField);
ctxRow.add(ctxInner);




// ✅ Center align this too:
ctxRow.setAlignmentX(CENTER_ALIGNMENT);
algoPanel.add(ctxRow);



       
              
       // Button panel to arrange buttons neatly
JPanel buttonPanel = new JPanel();
buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
buttonPanel.setBackground(bg);
buttonPanel.setAlignmentX(CENTER_ALIGNMENT);

// Add spacing between buttons
runButton = new JButton("Run Simulation");
styleButton(runButton, new Color(0x27ae60), fg, fontBold);
buttonPanel.add(runButton);
buttonPanel.add(javax.swing.Box.createRigidArea(new java.awt.Dimension(20, 10)));

clearButton = new JButton("Clear");
styleButton(clearButton, new Color(0xe74c3c), fg, fontBold);
buttonPanel.add(clearButton);
buttonPanel.add(javax.swing.Box.createRigidArea(new java.awt.Dimension(20, 10)));

exportButton = new JButton("Export CSV");
styleButton(exportButton, new Color(0x8e44ad), fg, fontBold);
buttonPanel.add(exportButton);
buttonPanel.add(javax.swing.Box.createRigidArea(new java.awt.Dimension(20, 10)));

nextStepButton = new JButton("Next Step");
styleButton(nextStepButton, accent, fg, fontBold);
nextStepButton.setEnabled(false);
buttonPanel.add(nextStepButton);
buttonPanel.add(javax.swing.Box.createRigidArea(new java.awt.Dimension(20, 10)));

stepModeCheck = new JCheckBox("Step-by-step Mode");
stepModeCheck.setBackground(bg);
stepModeCheck.setForeground(new Color(0xFFD700));
stepModeCheck.setFont(font);
buttonPanel.add(stepModeCheck);




// Create inputPanel and add subpanels in correct order


inputPanel.setBackground(bg); // Set background


inputPanel.add(fieldsPanel);    // Row 1: Name, Arrival, Burst
inputPanel.add(algoPanel);      // Row 2: Algorithm settings (algo, quantum, mlfq, context)
inputPanel.add(buttonPanel);    // Row 3: Buttons (Run, Clear, Export, Step...)

add(inputPanel, BorderLayout.NORTH); // Add inputPanel once, at the top


        // Center: Table and Gantt
        // Center: Table, Gantt, and Speed Slider
tableModel = new DefaultTableModel(new Object[]{"PID","Arrival","Burst"},0);
processTable = new JTable(tableModel);
processTable.setFont(font);
processTable.setRowHeight(28);
processTable.getTableHeader().setFont(fontBold);
processTable.setBackground(new Color(44, 47, 51));
processTable.setForeground(fg);
processTable.getTableHeader().setBackground(accent);
processTable.getTableHeader().setForeground(Color.WHITE);
JScrollPane tableScroll = new JScrollPane(processTable);
tableScroll.getViewport().setBackground(bg);

ganttPanel = new GanttPanel();
ganttPanel.setPreferredSize(new java.awt.Dimension(800, 300));
ganttPanel.setBackground(new Color(30, 32, 36));

// New panel just for the speed slider
JPanel speedPanel = new JPanel();
speedPanel.setBackground(bg);
JLabel speedLabel = new JLabel("Simulation Speed:");
speedLabel.setForeground(fg);
speedLabel.setFont(font);
speedPanel.add(speedLabel);

speedSlider = new JSlider(25, 200, 100);
speedSlider.setMajorTickSpacing(25);
speedSlider.setPaintTicks(true);
speedSlider.setPaintLabels(true);
speedSlider.setBackground(bg);
speedSlider.setForeground(fg);
speedPanel.add(speedSlider);

// Arrange center components vertically
JPanel centerPanel = new JPanel();
centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
centerPanel.setBackground(bg);

centerPanel.add(tableScroll);
centerPanel.add(ganttPanel);
centerPanel.add(speedPanel);

// Live Status Table
String[] liveCols = {"PID", "Completion %", "Remaining", "Waiting"};
liveStatusModel = new DefaultTableModel(liveCols, 0);
liveStatusTable = new JTable(liveStatusModel);
liveStatusTable.setFont(font);
liveStatusTable.setRowHeight(28);
// Set column widths
liveStatusTable.getColumnModel().getColumn(0).setPreferredWidth(80);   // PID
liveStatusTable.getColumnModel().getColumn(1).setPreferredWidth(120);  // Completion %
liveStatusTable.getColumnModel().getColumn(2).setPreferredWidth(100);  // Remaining
liveStatusTable.getColumnModel().getColumn(3).setPreferredWidth(100);  // Waiting

liveStatusTable.getTableHeader().setFont(fontBold);
liveStatusTable.setBackground(new Color(44, 47, 51));
liveStatusTable.setForeground(fg);
liveStatusTable.getTableHeader().setBackground(accent);
liveStatusTable.getTableHeader().setForeground(Color.WHITE);
JScrollPane liveScroll = new JScrollPane(liveStatusTable);
liveScroll.getViewport().setBackground(bg);
liveScroll.setPreferredSize(new java.awt.Dimension(800, 150));

// Live status bar below the table
liveStatusBar = new JLabel("Live Status: Waiting...");
liveStatusBar.setFont(new Font("Segoe UI", Font.PLAIN, 15));
liveStatusBar.setForeground(Color.LIGHT_GRAY);
totalProgressBar = new JProgressBar(0, 100);
totalProgressBar.setStringPainted(true);
totalProgressBar.setForeground(new Color(0x27ae60));
totalProgressBar.setBackground(new Color(30, 30, 30));
totalProgressBar.setFont(new Font("Segoe UI", Font.BOLD, 14));
totalProgressBar.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));

liveStatusBar.setBackground(new Color(30, 30, 30));
liveStatusBar.setOpaque(true);
liveStatusBar.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));

// Add live table to centerPanel

// ✅ Set preferred and max size to expand height


// ✅ Now add the container to the centerPanel



JPanel liveContainer = new JPanel(new BorderLayout());
liveContainer.setBackground(bg);
// Create a panel to hold both progress bar and status label vertically
JPanel statusPanel = new JPanel();
statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
statusPanel.setBackground(bg);
statusPanel.add(totalProgressBar); // add bar first
statusPanel.add(liveStatusBar);    // then the status text

liveContainer.add(liveScroll, BorderLayout.CENTER);
liveContainer.add(statusPanel, BorderLayout.SOUTH);

liveContainer.setPreferredSize(new java.awt.Dimension(800, 270));

liveContainer.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 300));

// Add the scroll pane to this container
liveContainer.add(liveScroll, BorderLayout.CENTER);
centerPanel.add(liveContainer);




add(centerPanel, BorderLayout.CENTER);
statusBar = new JLabel("Ready");
statusBar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
statusBar.setForeground(Color.LIGHT_GRAY);
statusBar.setBackground(new Color(36, 37, 42));
statusBar.setOpaque(true);
statusBar.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));
statusBar.setPreferredSize(new java.awt.Dimension(getWidth(), 30));




        // South: Metrics
   // South: Metrics and Status Bar Panel
// Metrics Area Panel (separate)
// Combine metrics and status bar into one south panel
JPanel southPanel = new JPanel();
southPanel.setLayout(new BorderLayout());
southPanel.setBackground(bg);
metricsArea = new JTextArea(); // ✅ This is missing

metricsArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
metricsArea.setEditable(false);
metricsArea.setForeground(new Color(230, 230, 230));
metricsArea.setBackground(new Color(44, 44, 50));

JScrollPane metricsScroll = new JScrollPane(metricsArea);

metricsPanel = new JPanel(new BorderLayout());
metricsPanel.setBackground(new Color(36, 37, 42));
metricsPanel.add(metricsScroll, BorderLayout.CENTER);
// ✅ FIX: Limit the height of metricsPanel so it doesn't hide the status bar
metricsPanel.setPreferredSize(new java.awt.Dimension(getWidth(), 140));
southPanel.add(metricsPanel, BorderLayout.CENTER);

// ✅ Set preferred size for the status bar
statusBar.setPreferredSize(new java.awt.Dimension(getWidth(), 30));
statusBar.setBackground(new Color(36, 37, 42));
statusBar.setForeground(Color.LIGHT_GRAY);
statusBar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
statusBar.setOpaque(true);
statusBar.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));

// ✅ Add status bar to the SOUTH of the south panel
southPanel.add(statusBar, BorderLayout.SOUTH);

// ✅ Add south panel to the frame
add(southPanel, BorderLayout.SOUTH);



// Status Bar (separate panel)


// Add status bar directly to the PAGE_END




        // Listeners
        addButton.addActionListener(e -> addProcess());
        randomButton.addActionListener(e -> randomProcesses());
        runButton.addActionListener(e -> runSimulation());
        clearButton.addActionListener(e -> clearAll());
        algoBox.addActionListener(e -> updateQuantumFields());
        nextStepButton.addActionListener(e -> nextStep());
        exportButton.addActionListener(e -> exportResults());
        updateQuantumFields();
    }

    class BackgroundPanel extends JPanel {
    private Image bgImage;
    public BackgroundPanel(String path) {
        bgImage = new ImageIcon(path).getImage();
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (bgImage != null) {
            g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
    private void styleButton(JButton btn, Color bg, Color fg, Font font) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(font);
        btn.setFocusPainted(false);
        btn.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(bg.darker(), 2, true),
            javax.swing.BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }
    
    // Color-coded Gantt chart panel as static inner class
    static class GanttPanel extends JPanel {
        private java.util.List<GUIScheduler.GanttSegment> segments = new ArrayList<>();
        private static final Color[] COLORS = {
            new Color(0x00B8D9), new Color(0xFF5630), new Color(0x36B37E), new Color(0x6554C0),
            new Color(0xFFAB00), new Color(0xFF8B00), new Color(0x00C7E6), new Color(0xFFB900),
            new Color(0xFF5E3A), new Color(0x6E00FF), new Color(0x00D084), new Color(0xFF4B4B)
        };
        public void setSegments(java.util.List<GUIScheduler.GanttSegment> segs) {
            this.segments = new ArrayList<>(segs);
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (segments == null || segments.isEmpty()) return;
            int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
            for (GUIScheduler.GanttSegment s : segments) {
                if (s.start < min) min = s.start;
                if (s.end > max) max = s.end;
            }
            int total = max - min;
            int w = getWidth() - 40, h = getHeight() - 40;
            int x0 = 20, y0 = 30;
            for (int i = 0; i < segments.size(); i++) {
                GUIScheduler.GanttSegment s = segments.get(i);
                int x = x0 + (int)((s.start - min) * (w * 1.0 / total));
                int x2 = x0 + (int)((s.end - min) * (w * 1.0 / total));
                int barW = Math.max(1, x2 - x);
                Color c = COLORS[(s.pid-1) % COLORS.length];

             Graphics g2 = g.create();
    g2.setColor(c); // Set color first
    g2.fillRoundRect(x, y0, barW, h, 18, 18); // Draw background bar
    g2.setColor(Color.WHITE); // Now set color for text and border
    g2.drawRoundRect(x, y0, barW, h, 18, 18);
    g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
    g2.drawString("P" + s.pid + (s.queue >= 0 ? "(Q" + s.queue + ")" : ""), x + 8, y0 + h / 2 + 6);
    g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    g2.drawString("" + s.start, x - 2, y0 + h + 18);
    g2.drawString("" + s.end, x2 - 10, y0 + h + 18);
    g2.dispose();


            }
        }
    }

    private void addProcess() {
        try {
            String name = nameField.getText().trim();
            int arrival = Integer.parseInt(arrivalField.getText().trim());
            int burst = Integer.parseInt(burstField.getText().trim());
            processList.add(new Process(processList.size()+1, arrival, burst));
            tableModel.addRow(new Object[]{name.isEmpty()?"P"+(processList.size()):name, arrival, burst});
            nameField.setText(""); arrivalField.setText(""); burstField.setText("");
            statusBar.setText("Added process P" + processList.size());

        } catch(Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid input.");
        }
    }
    private void randomProcesses() {
        String nStr = JOptionPane.showInputDialog(this, "How many processes?");
        try {
            int n = Integer.parseInt(nStr);
            Random rand = new Random();
            for(int i=0;i<n;i++) {
                int arrival = rand.nextInt(5*n);
                int burst = rand.nextInt(9)+1;
                processList.add(new Process(processList.size()+1, arrival, burst));
                tableModel.addRow(new Object[]{"P"+(processList.size()), arrival, burst});
            }
            statusBar.setText(n + " random processes generated.");

        } catch(Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid number.");
        }
    }
    private void runSimulation() {
        if(processList.isEmpty()) { JOptionPane.showMessageDialog(this, "Add processes first."); return; }
        String algo = (String)algoBox.getSelectedItem();
        statusBar.setText("Running simulation with " + algo + "...");
        List<Process> copy = new ArrayList<>();
        for(Process p:processList) copy.add(new Process(p.pid,p.arrival,p.burst));
        StringBuilder gantt = new StringBuilder();
        StringBuilder metrics = new StringBuilder();
        int[] quantums = new int[4];
        int quantum = 0;
        int ctxDelay = 0;
        try { ctxDelay = Integer.parseInt(contextSwitchField.getText().trim()); } catch(Exception ex) { ctxDelay = 0; }
       if (algo.equals("Round Robin")) {
    try {
        quantum = Integer.parseInt(quantumField.getText().trim());
        if (quantum <= 0) {
            throw new NumberFormatException();
        }
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Please enter a valid positive integer for Time Quantum.");
        return;
    }
}

        if(algo.equals("MLFQ")) {
            try { for(int i=0;i<4;i++) quantums[i]=Integer.parseInt(mlfqFields[i].getText().trim()); } catch(Exception ex) { JOptionPane.showMessageDialog(this,"Enter all MLFQ quantums"); return; }
        }
        GUIScheduler.simulate(copy, algo, quantum, quantums, gantt, metrics);
        if (stepModeCheck.isSelected()) {
            stepIndex = 0;
            stepSegments = new ArrayList<>(GUIScheduler.ganttSegments);
            ganttPanel.setSegments(new ArrayList<>());
            nextStepButton.setEnabled(true);
           metricsArea.setText(""); // clear now
            this.cachedMetrics = metrics.toString(); // ✅ Correct source

            statusBar.setText("Step-by-step mode: click Next Step to proceed.");

        } else {
            animateGantt(GUIScheduler.ganttSegments, ctxDelay, metrics.toString());
            nextStepButton.setEnabled(false);
            statusBar.setText("Simulation complete.");

        }
    }
    private void nextStep() {
        if (stepIndex < stepSegments.size()) {
            List<GUIScheduler.GanttSegment> current = new ArrayList<>(stepSegments.subList(0, stepIndex+1));
            ganttPanel.setSegments(current);
            ganttPanel.repaint();
           int currentTime = stepSegments.get(stepIndex).end;

            updateLiveStatus(processList, currentTime);

             stepIndex++;   
            statusBar.setText("Step " + stepIndex + "/" + stepSegments.size());

            if (stepIndex == stepSegments.size()) {
                nextStepButton.setEnabled(false);
                // Show metrics at end
                StringBuilder metrics = new StringBuilder();
              metricsArea.setText(this.cachedMetrics);
                statusBar.setText("Step-by-step simulation finished.");

            }
        }
    }

    // Animate Gantt chart with context switch delay
    private void animateGantt(List<GUIScheduler.GanttSegment> segments, int delay, String metrics) {
        List<GUIScheduler.GanttSegment> shown = new ArrayList<>();
         int animSpeed = speedSlider.getValue(); // 100 = 1.0x
int effectiveDelay = delay > 0 ? delay : (200 * 100 / animSpeed);
Timer timer = new Timer(effectiveDelay, null);

        final int[] idx = {0};
        timer.addActionListener(e -> {
            if (idx[0] < segments.size()) {
                shown.add(segments.get(idx[0]));
                // Update live table using current simulation time
                int currentTime = segments.get(idx[0]).end;
                updateLiveStatus(processList, currentTime);

                ganttPanel.setSegments(new ArrayList<>(shown));
                ganttPanel.repaint();
                idx[0]++;
            } else {
                timer.stop();
                metricsArea.setText(metrics);
            }
        });
        timer.start();
    }

   private void updateLiveStatus(List<Process> processes, int currentTime) {
    liveStatusModel.setRowCount(0);
    int totalRemaining = 0;
    int totalBurst = 0;


// Moved below to fix order
for (Process p : processes) {
    totalBurst += p.burst;
}



int percentDone = (int)(100.0 * (totalBurst - totalRemaining) / totalBurst);
totalProgressBar.setValue(percentDone);


    for (Process p : processes) {
        int done = p.burst - p.remaining;
        int percent = (int)((done * 100.0) / p.burst);
        int waiting = currentTime - p.arrival - done;
        waiting = Math.max(0, waiting); // No negatives
        totalRemaining += p.remaining;
        liveStatusModel.addRow(new Object[]{
            "P" + p.pid,
            percent + "%",
            p.remaining,
            waiting
        });
    }
   




    // ✅ Update liveStatusBar text here
    liveStatusBar.setText("Live Status: Time = " + currentTime + " | Remaining Workload = " + totalRemaining);
}

    // Export results to CSV
    private void exportResults() {
     JFileChooser fileChooser = new JFileChooser();
fileChooser.setSelectedFile(new File("cpu_scheduling_results.csv"));
int option = fileChooser.showSaveDialog(this);
if (option == JFileChooser.APPROVE_OPTION) {
    File file = fileChooser.getSelectedFile();
    if (file.exists()) {
        int confirm = JOptionPane.showConfirmDialog(this, "File exists. Overwrite?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
    }
    try (FileWriter fw = new FileWriter(file)) {
        fw.write(metricsArea.getText());
        JOptionPane.showMessageDialog(this, "Results exported to " + file.getName());
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage());
    }
}

    }
   private void clearAll() {
    processList.clear();
    tableModel.setRowCount(0);
    ganttPanel.setSegments(new ArrayList<>());
    ganttPanel.repaint();
    metricsArea.setText("");

    // ✅ Clear live status table
    liveStatusModel.setRowCount(0);

    // ✅ Reset live status bar
    liveStatusBar.setText("Live Status: Waiting...");

    // ✅ Reset total progress bar
    totalProgressBar.setValue(0);

    statusBar.setText("Cleared all data.");
}

   private void updateQuantumFields() {
    String algo = (String)algoBox.getSelectedItem();
    quantumPanel.setVisible(algo.equals("Round Robin"));
    mlfqPanel.setVisible(algo.equals("MLFQ"));

    if (!algo.equals("Round Robin")) {
        quantumField.setText(""); // Clear if not Round Robin
    }
}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CPUSchedulingGUI().setVisible(true));
    }
}

// Placeholder for GUIScheduler. You must implement this class to connect the GUI to your scheduling logic.
class GUIScheduler {
    // Gantt segment: process id, start, end, queue (for MLFQ, else -1)
    public static class GanttSegment {
        public int pid, start, end, queue;
        public GanttSegment(int pid, int start, int end, int queue) {
            this.pid = pid; this.start = start; this.end = end; this.queue = queue;
        }
    }
    public static java.util.List<GanttSegment> ganttSegments = new ArrayList<>();
    public static void simulate(List<Process> processes, String algo, int quantum, int[] mlfqQuantums, StringBuilder gantt, StringBuilder metrics) {
        ganttSegments.clear();
        if (processes == null || processes.isEmpty()) return;
        List<Process> plist = new ArrayList<>();
        for (Process p : processes) plist.add(new Process(p.pid, p.arrival, p.burst));
        int n = plist.size(), totalTurnaround = 0, totalWaiting = 0, totalResponse = 0;
        StringBuilder ganttChart = new StringBuilder("Gantt Chart:\n|");
        switch (algo) {
            case "FCFS": {
                plist.sort((a, b) -> a.arrival != b.arrival ? a.arrival - b.arrival : a.pid - b.pid);
                int time = 0;
                for (Process p : plist) {
                    if (time < p.arrival) time = p.arrival;
                    p.start = time;
                    p.response = time - p.arrival;
                    ganttSegments.add(new GanttSegment(p.pid, time, time + p.burst, -1));
                    time += p.burst;
                    p.completion = time;
                    p.turnaround = p.completion - p.arrival;
                    p.waiting = p.turnaround - p.burst;
                    totalTurnaround += p.turnaround;
                    totalResponse += p.response;
                    totalWaiting += p.waiting;
                    ganttChart.append(" P" + p.pid + " |");
                }
                break; }
            case "SJF": {
                List<Process> procs = new ArrayList<>(plist);
                boolean[] done = new boolean[n];
                int completed = 0, t = 0;
                while (completed < n) {
                    int idx = -1, minBurst = Integer.MAX_VALUE;
                    for (int i = 0; i < n; i++) {
                        Process p = procs.get(i);
                        if (!done[i] && p.arrival <= t && p.burst < minBurst) {
                            minBurst = p.burst; idx = i;
                        }
                    }
                    if (idx == -1) { t++; continue; }
                    Process p = procs.get(idx);
                    p.start = t;
                    p.response = t - p.arrival;
                    ganttSegments.add(new GanttSegment(p.pid, t, t + p.burst, -1));
                    t += p.burst;
                    p.completion = t;
                    p.turnaround = p.completion - p.arrival;
                    p.waiting = p.turnaround - p.burst;
                    totalTurnaround += p.turnaround;
                    totalResponse += p.response;
                    totalWaiting += p.waiting;
                    ganttChart.append(" P" + p.pid + " |");
                    done[idx] = true;
                    completed++;
                }
                plist = procs;
                break; }
            case "SRTF": {
                List<Process> procs = new ArrayList<>();
                for (Process p : plist) procs.add(new Process(p.pid, p.arrival, p.burst));
                int completed = 0, t = 0, lastPid = -1, segStart = 0;
                boolean[] started = new boolean[n];
                while (completed < n) {
                    int idx = -1, minRem = Integer.MAX_VALUE;
                    for (int i = 0; i < n; i++) {
                        Process p = procs.get(i);
                        if (p.arrival <= t && p.remaining > 0 && p.remaining < minRem) {
                            minRem = p.remaining; idx = i;
                        }
                    }
                    if (idx == -1) { t++; continue; }
                    Process p = procs.get(idx);
                    if (!started[idx]) { p.start = t; p.response = t - p.arrival; started[idx] = true; }
                    if (lastPid != p.pid) {
                        if (lastPid != -1) ganttSegments.add(new GanttSegment(lastPid, segStart, t, -1));
                        segStart = t;
                        ganttChart.append(" P" + p.pid + " |"); lastPid = p.pid;
                    }
                    p.remaining--;
                    t++;
                    if (p.remaining == 0) {
                        p.completion = t;
                        p.turnaround = p.completion - p.arrival;
                        p.waiting = p.turnaround - p.burst;
                        totalTurnaround += p.turnaround;
                        totalResponse += p.response;
                        totalWaiting += p.waiting;
                        completed++;
                        // End segment if process finished
                        ganttSegments.add(new GanttSegment(p.pid, segStart, t, -1));
                        lastPid = -1;
                    }
                }
                // If a process was running at the end
                if (lastPid != -1) ganttSegments.add(new GanttSegment(lastPid, segStart, t, -1));
                plist = procs;
                break; }
            case "Round Robin": {
                List<Process> procs = new ArrayList<>();
                for (Process p : plist) procs.add(new Process(p.pid, p.arrival, p.burst));
                int completed = 0, t = 0, lastPid = -1, arrived = 0, segStart = 0;
                boolean[] started = new boolean[n], inQueue = new boolean[n];
                java.util.Queue<Integer> q = new java.util.LinkedList<>();
                while (completed < n) {
                    while (arrived < n && procs.get(arrived).arrival <= t) { q.add(arrived); inQueue[arrived] = true; arrived++; }
                    if (q.isEmpty()) { t++; continue; }
                    int idx = q.poll();
                    Process p = procs.get(idx);
                    if (!started[idx]) { p.start = t; p.response = t - p.arrival; started[idx] = true; }
                    int exec = Math.min(quantum, p.remaining);
                    if (lastPid != p.pid) {
                        if (lastPid != -1) ganttSegments.add(new GanttSegment(lastPid, segStart, t, -1));
                        segStart = t;
                        ganttChart.append(" P" + p.pid + " |"); lastPid = p.pid;
                    }
                    for (int tt = 0; tt < exec; tt++) {
                        t++;
                        while (arrived < n && procs.get(arrived).arrival <= t) { if (!inQueue[arrived]) { q.add(arrived); inQueue[arrived] = true; } arrived++; }
                    }
                    p.remaining -= exec;
                    if (p.remaining == 0) {
                        p.completion = t;
                        p.turnaround = p.completion - p.arrival;
                        p.waiting = p.turnaround - p.burst;
                        totalTurnaround += p.turnaround;
                        totalResponse += p.response;
                        totalWaiting += p.waiting;
                        completed++;
                        ganttSegments.add(new GanttSegment(p.pid, segStart, t, -1));
                        lastPid = -1;
                    } else {
                        q.add(idx);
                    }
                }
                if (lastPid != -1) ganttSegments.add(new GanttSegment(lastPid, segStart, t, -1));
                plist = procs;
                break; }
            case "MLFQ": {
                List<Process> procs = new ArrayList<>();
                for (Process p : plist) procs.add(new Process(p.pid, p.arrival, p.burst));
                int completed = 0, t = 0, lastPid = -1, arrived = 0, segStart = 0, segQueue = 0;
                boolean[] started = new boolean[n];
                java.util.Queue<Integer>[] queues = new java.util.LinkedList[4];
                for (int i = 0; i < 4; i++) queues[i] = new java.util.LinkedList<>();
                while (completed < n) {
                    while (arrived < n && procs.get(arrived).arrival <= t) { queues[0].add(arrived); arrived++; }
                    int qIdx = -1;
                    for (int i = 0; i < 4; i++) if (!queues[i].isEmpty()) { qIdx = i; break; }
                    if (qIdx == -1) { t++; continue; }
                    int idx = queues[qIdx].poll();
                    Process p = procs.get(idx);
                    if (!started[idx]) { p.start = t; p.response = t - p.arrival; started[idx] = true; }
                    int exec = Math.min(mlfqQuantums[qIdx], p.remaining);
                    if (lastPid != p.pid || segQueue != qIdx) {
                        if (lastPid != -1) ganttSegments.add(new GanttSegment(lastPid, segStart, t, segQueue));
                        segStart = t; segQueue = qIdx;
                        ganttChart.append(" P" + p.pid + "(Q" + qIdx + ") |"); lastPid = p.pid;
                    }
                    for (int tt = 0; tt < exec; tt++) {
                        t++;
                        while (arrived < n && procs.get(arrived).arrival <= t) { queues[0].add(arrived); arrived++; }
                    }
                    p.remaining -= exec;
                    if (p.remaining == 0) {
                        p.completion = t;
                        p.turnaround = p.completion - p.arrival;
                        p.waiting = p.turnaround - p.burst;
                        totalTurnaround += p.turnaround;
                        totalResponse += p.response;
                        totalWaiting += p.waiting;
                        completed++;
                        ganttSegments.add(new GanttSegment(p.pid, segStart, t, qIdx));
                        lastPid = -1;
                    } else {
                        if (qIdx < 3) queues[qIdx+1].add(idx);
                        else queues[3].add(idx);
                    }
                }
                if (lastPid != -1) ganttSegments.add(new GanttSegment(lastPid, segStart, t, segQueue));
                plist = procs;
                break; }
        }
        // Output Gantt chart
        gantt.append(ganttChart.toString());
        // Output metrics table
       metrics.append(String.format("%-6s %-8s %-8s %-11s %-12s %-10s %-10s\n", "PID", "Arrival", "Burst", "Completion", "Turnaround", "Waiting", "Response"));

        for (Process p : plist) {
           metrics.append(String.format("%-6s %-8d %-8d %-11d %-12d %-10d %-10d\n",
    "P" + p.pid, p.arrival, p.burst, p.completion, p.turnaround, p.waiting, p.response));

        }
        metrics.append("\nAverages:\n");
        metrics.append(String.format("Average Turnaround Time: %.2f\n", totalTurnaround/(double)n));
        metrics.append(String.format("Average Waiting Time: %.2f\n", totalWaiting/(double)n));
        metrics.append(String.format("Average Response Time: %.2f\n", totalResponse/(double)n));
    }
    // Add this below the GUIScheduler class


}
class Process {
    public int pid, arrival, burst, remaining;
    public int start = -1, completion = -1;
    public int turnaround = 0, waiting = 0, response = 0;

    public Process(int pid, int arrival, int burst) {
        this.pid = pid;
        this.arrival = arrival;
        this.burst = burst;
        this.remaining = burst;
    }
}