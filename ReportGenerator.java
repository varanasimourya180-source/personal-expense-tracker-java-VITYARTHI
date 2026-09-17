import java.time.LocalDate;
import java.time.Month;
import java.util.List;

public class ReportGenerator {

    private final ExpenseTracker tracker;

    public ReportGenerator(ExpenseTracker tracker) {
        this.tracker = tracker;
    }

    public double[][] buildMonthlyCategoryGrid() {
        Category[] categories = Category.values();
        double[][] grid = new double[categories.length][12];

        List<Transaction> all = tracker.getAllTransactions();
        for (Transaction t : all) {
            if (t.getType() != Transaction.Type.EXPENSE) continue;
            int catIndex = indexOf(categories, t.getCategory());
            int monthIndex = t.getDate().getMonthValue() - 1;
            grid[catIndex][monthIndex] += t.getAmount();
        }
        return grid;
    }

    private int indexOf(Category[] categories, Category target) {
        for (int i = 0; i < categories.length; i++) {
            if (categories[i] == target) return i;
        }
        return -1;
    }

    public String renderMonthlyGrid() {
        Category[] categories = Category.values();
        double[][] grid = buildMonthlyCategoryGrid();

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-15s", "Category"));
        for (Month m : Month.values()) {
            sb.append(String.format("%8s", m.name().substring(0, 3)));
        }
        sb.append("\n");

        for (int i = 0; i < categories.length; i++) {
            sb.append(String.format("%-15s", categories[i]));
            for (int j = 0; j < 12; j++) {
                sb.append(String.format("%8.0f", grid[i][j]));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public String generateSummaryReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("===== Expense Tracker Report =====\n");
        sb.append("Generated: ").append(LocalDate.now()).append("\n\n");

        sb.append(String.format("Total Income:  %.2f%n", tracker.getTotalIncome()));
        sb.append(String.format("Total Expense: %.2f%n", tracker.getTotalExpense()));
        sb.append(String.format("Net Balance:   %.2f%n%n",
                tracker.getTotalIncome() - tracker.getTotalExpense()));

        sb.append("--- Spend by Category ---\n");
        for (Category c : Category.values()) {
            if (c.getBudgetLimit() <= 0) continue;
            double spent = tracker.getTotalSpentInCategory(c);
            double limit = c.getBudgetLimit();
            double percent = limit > 0 ? (spent / limit) * 100 : 0;
            sb.append(String.format("  %-15s %8.2f / %8.2f (%.0f%%)%n",
                    c, spent, limit, percent));
        }

        sb.append("\n--- Monthly Breakdown ---\n");
        sb.append(renderMonthlyGrid());

        return sb.toString();
    }
}