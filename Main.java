import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final String DATA_FILE = "transactions.csv";
    private static final String REPORT_FILE = "report.txt";

    private static final Scanner scanner = new Scanner(System.in);
    private static final ExpenseTracker tracker = new ExpenseTracker();
    private static final FileManager fileManager = new FileManager(DATA_FILE);
    private static final ReportGenerator reportGenerator = new ReportGenerator(tracker);
    private static final BudgetMonitor budgetMonitor = new BudgetMonitor(tracker, 5000);

    public static void main(String[] args) {
        System.out.println("=== Personal Expense Tracker ===");
        loadExistingData();

        budgetMonitor.start();

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> addTransactionFlow();
                case "2" -> viewAllTransactions();
                case "3" -> deleteTransactionFlow();
                case "4" -> viewByCategoryFlow();
                case "5" -> generateReportFlow();
                case "6" -> running = !confirmExit();
                default -> System.out.println("Invalid option, try again.");
            }
        }

        budgetMonitor.stop();
        saveData();
        System.out.println("Goodbye!");
    }

    private static void printMenu() {
        System.out.println("""

                1. Add transaction
                2. View all transactions
                3. Delete a transaction
                4. View transactions by category
                5. Generate report
                6. Save & Exit
                Choose an option:""");
    }

    private static void addTransactionFlow() {
        try {
            Transaction.Type type = readType();
            Category category = readCategory();
            double amount = readPositiveDouble("Amount: ");
            System.out.print("Description: ");
            String description = scanner.nextLine().trim();
            LocalDate date = readDateOrToday();

            Transaction t = tracker.addTransaction(amount, category, type, description, date);
            System.out.println("Added: " + t);

        } catch (InvalidTransactionException e) {
            System.out.println("Could not add transaction: " + e.getMessage());
        }
    }

    private static void viewAllTransactions() {
        List<Transaction> all = tracker.getAllTransactions();
        if (all.isEmpty()) {
            System.out.println("No transactions yet.");
            return;
        }
        System.out.println("\n--- All Transactions (" + all.size() + ") ---");
        for (Transaction t : all) {
            System.out.println("  " + t);
        }
    }

    private static void deleteTransactionFlow() {
        System.out.print("Enter transaction id to delete: ");
        String id = scanner.nextLine().trim();
        boolean removed = tracker.deleteTransaction(id);
        System.out.println(removed ? "Deleted." : "No transaction found with that id.");
    }

    private static void viewByCategoryFlow() {
        Category category = readCategory();
        List<Transaction> matches = tracker.getByCategory(category);
        if (matches.isEmpty()) {
            System.out.println("No transactions in " + category + ".");
            return;
        }
        System.out.println("\n--- " + category + " (" + matches.size() + ") ---");
        for (Transaction t : matches) {
            System.out.println("  " + t);
        }
    }

    private static void generateReportFlow() {
        String report = reportGenerator.generateSummaryReport();
        System.out.println("\n" + report);
        try {
            fileManager.writeReport(REPORT_FILE, report);
            System.out.println("(Also saved to " + REPORT_FILE + ")");
        } catch (java.io.IOException e) {
            System.out.println("Could not write report file: " + e.getMessage());
        }
    }

    private static boolean confirmExit() {
        System.out.print("Save and exit? (y/n): ");
        return scanner.nextLine().trim().equalsIgnoreCase("y");
    }

    private static void loadExistingData() {
        try {
            List<Transaction> loaded = fileManager.load();
            for (Transaction t : loaded) {
                tracker.restoreTransaction(t);
            }
            if (!loaded.isEmpty()) {
                System.out.println("Loaded " + loaded.size() + " transaction(s) from " + DATA_FILE);
            }
        } catch (java.io.IOException e) {
            System.out.println("Could not load existing data: " + e.getMessage());
        }
    }

    private static void saveData() {
        try {
            fileManager.save(tracker.getAllTransactions());
            System.out.println("Saved " + tracker.size() + " transaction(s) to " + DATA_FILE);
        } catch (java.io.IOException e) {
            System.out.println("Could not save data: " + e.getMessage());
        }
    }

    private static Transaction.Type readType() {
        while (true) {
            System.out.print("Type - 1) Income  2) Expense: ");
            String choice = scanner.nextLine().trim();
            if (choice.equals("1")) return Transaction.Type.INCOME;
            if (choice.equals("2")) return Transaction.Type.EXPENSE;
            System.out.println("Enter 1 or 2.");
        }
    }

    private static Category readCategory() {
        Category[] categories = Category.values();
        while (true) {
            System.out.println("Category:");
            for (int i = 0; i < categories.length; i++) {
                System.out.printf("  %d) %s%n", i + 1, categories[i]);
            }
            System.out.print("Choose: ");
            String choice = scanner.nextLine().trim();
            try {
                int index = Integer.parseInt(choice) - 1;
                if (index >= 0 && index < categories.length) {
                    return categories[index];
                }
            } catch (NumberFormatException ignored) {
            }
            System.out.println("Invalid choice, try again.");
        }
    }

    private static double readPositiveDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                double value = Double.parseDouble(input);
                if (value > 0) return value;
                System.out.println("Amount must be positive.");
            } catch (NumberFormatException e) {
                System.out.println("Not a valid number, try again.");
            }
        }
    }

    private static LocalDate readDateOrToday() {
        System.out.print("Date (YYYY-MM-DD, or leave blank for today): ");
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) return LocalDate.now();
        try {
            return LocalDate.parse(input);
        } catch (DateTimeParseException e) {
            System.out.println("Couldn't parse that date, using today instead.");
            return LocalDate.now();
        }
    }
}