import java.time.LocalDate;
import java.util.*;

public class ExpenseTracker {

    private final List<Transaction> transactions = new ArrayList<>();

    public synchronized Transaction addTransaction(double amount, Category category,Transaction.Type type, String description,LocalDate date) throws InvalidTransactionException {
        validate(amount, category, description);
        Transaction t = new Transaction(amount, category, type, description, date);
        transactions.add(t);
        return t;
    }

    public synchronized void restoreTransaction(Transaction t) {
        transactions.add(t);
    }

    private void validate(double amount, Category category, String description)
            throws InvalidTransactionException {
        if (amount <= 0) {
            throw new InvalidTransactionException(
                    "Amount must be positive, got: " + amount);
        }
        if (category == null) {
            throw new InvalidTransactionException("Category cannot be null");
        }
        if (description == null || description.isBlank()) {
            throw new InvalidTransactionException("Description cannot be blank");
        }
    }

    public synchronized boolean deleteTransaction(String id) {
        return transactions.removeIf(t -> t.getId().equals(id));
    }

    public synchronized List<Transaction> getAllTransactions() {
        return new ArrayList<>(transactions);
    }

    public synchronized List<Transaction> getByCategory(Category category) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction t : transactions) {
            if (t.getCategory() == category) {
                result.add(t);
            }
        }
        return result;
    }

    public synchronized List<Transaction> getByType(Transaction.Type type) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction t : transactions) {
            if (t.getType() == type) {
                result.add(t);
            }
        }
        return result;
    }

    public synchronized Map<Category, List<Transaction>> groupByCategory() {
        Map<Category, List<Transaction>> map = new HashMap<>();
        for (Transaction t : transactions) {
            map.computeIfAbsent(t.getCategory(), k -> new ArrayList<>()).add(t);
        }
        return map;
    }

    public synchronized double getTotalSpentInCategory(Category category) {
        double total = 0.0;
        for (Transaction t : transactions) {
            if (t.getCategory() == category && t.getType() == Transaction.Type.EXPENSE) {
                total += t.getAmount();
            }
        }
        return total;
    }

    public synchronized double getTotalIncome() {
        double total = 0.0;
        for (Transaction t : transactions) {
            if (t.getType() == Transaction.Type.INCOME) {
                total += t.getAmount();
            }
        }
        return total;
    }

    public synchronized double getTotalExpense() {
        double total = 0.0;
        for (Transaction t : transactions) {
            if (t.getType() == Transaction.Type.EXPENSE) {
                total += t.getAmount();
            }
        }
        return total;
    }

    public synchronized int size() {
        return transactions.size();
    }
}