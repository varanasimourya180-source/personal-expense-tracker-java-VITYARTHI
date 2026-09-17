import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class BudgetMonitor implements Runnable {

    private final ExpenseTracker tracker;
    private final long intervalMillis;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private Thread workerThread;

    private final Set<String> alreadyWarned80 = new HashSet<>();
    private final Set<String> alreadyWarned100 = new HashSet<>();

    public BudgetMonitor(ExpenseTracker tracker, long intervalMillis) {
        this.tracker = tracker;
        this.intervalMillis = intervalMillis;
    }

    @Override
    public void run() {
        running.set(true);
        while (running.get()) {
            checkBudgets();
            try {
                Thread.sleep(intervalMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void checkBudgets() {
        for (Category category : Category.values()) {
            double limit = category.getBudgetLimit();
            if (limit <= 0) continue;

            double spent = tracker.getTotalSpentInCategory(category);
            double percent = (spent / limit) * 100.0;

            if (percent >= 100.0 && alreadyWarned100.add(category.name())) {
                System.out.printf("%n[ALERT] Budget EXCEEDED for %s: spent %.2f / limit %.2f (%.0f%%)%n",
                        category, spent, limit, percent);
            } else if (percent >= 80.0 && alreadyWarned80.add(category.name())) {
                System.out.printf("%n[WARNING] %s is at %.0f%% of its budget (%.2f / %.2f)%n",
                        category, percent, spent, limit);
            }
        }
    }

    public void start() {
        workerThread = new Thread(this, "BudgetMonitor-Thread");
        workerThread.setDaemon(true);
        workerThread.start();
    }

    public void stop() {
        running.set(false);
        if (workerThread != null) {
            workerThread.interrupt();
        }
    }

    public boolean isRunning() {
        return running.get();
    }
}