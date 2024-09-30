import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;

public class LogProcessor {
    // Queue to store all log entries (LinkedList implementation)
    private LinkedList<String> logQueue = new LinkedList<>();
    // Stack to store only ERROR log entries (LinkedList implementation)
    private LinkedList<String> errorStack = new LinkedList<>();
    // HashMap to store count of log levels
    private Map<String, Integer> logCount = new HashMap<>();
    // LinkedList to store the last 100 error logs
    private LinkedList<String> recentErrors = new LinkedList<>();
    // Variable to track number of memory warnings
    private int memoryWarnings = 0;

    public LogProcessor() {
        logCount.put("INFO", 0);
        logCount.put("WARN", 0);
        logCount.put("ERROR", 0);
    }

    // Read log entries from the file into the queue
    public void readLogs(String logFilePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(logFilePath))) {
            System.out.println("Reading from: " + logFilePath);
            String line;
            while ((line = br.readLine()) != null) {
                logQueue.addLast(line.trim());
            }
        } catch (IOException e) {
            System.out.println("Error reading the log file: " + e.getMessage());
        }
    }

    // Process the logs from the queue and push errors onto the stack
    public void processLogs() {
        while (!logQueue.isEmpty()) {
            String logEntry = logQueue.pollFirst();  // Dequeue the first log entry
            analyzeLog(logEntry);                    // Analyze the log entry for data analysis
            if (logEntry.contains("ERROR")) {        // Push ERROR logs onto the stack
                errorStack.push(logEntry);
                if (recentErrors.size() == 100) {
                    recentErrors.removeFirst();      // Maintain the size of recentErrors to 100
                }
                recentErrors.addLast(logEntry);
            }
        }
    }

    // Analyze a log entry for log levels and specific conditions
    private void analyzeLog(String logEntry) {
        if (logEntry.contains("INFO")) {
            logCount.put("INFO", logCount.get("INFO") + 1);
        } else if (logEntry.contains("WARN")) {
            logCount.put("WARN", logCount.get("WARN") + 1);
            if (logEntry.contains("Memory")) {
                memoryWarnings++;
            }
        } else if (logEntry.contains("ERROR")) {
            logCount.put("ERROR", logCount.get("ERROR") + 1);
        }
    }

    // Display the results of the log analysis
    public void displayResults() {
        System.out.println("Log Level Counts:");
        System.out.println("INFO: " + logCount.get("INFO"));
        System.out.println("WARN: " + logCount.get("WARN"));
        System.out.println("ERROR: " + logCount.get("ERROR"));

        System.out.println("\nLast 100 Error Entries:");
        for (String error : recentErrors) {
            System.out.println(error);
        }

        System.out.println("\nNumber of Memory Warnings: " + memoryWarnings);
    }

    public static void main(String[] args) {
        LogProcessor processor = new LogProcessor();

        String logFilePath = "src/log-data.csv"; 

        processor.readLogs(logFilePath);
        processor.processLogs();
        processor.displayResults();
    }
}
