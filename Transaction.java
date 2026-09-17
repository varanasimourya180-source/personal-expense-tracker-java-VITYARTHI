import java.time.LocalDate;
import java.util.UUID;

public class Transaction {

    public enum Type { INCOME, EXPENSE }

    private final String id;
    private final double amount;
    private final Category category;
    private final Type type;
    private final String description;
    private final LocalDate date;

    public Transaction(double amount, Category category, Type type,
                       String description, LocalDate date) {
        this(UUID.randomUUID().toString().substring(0, 8), amount, category,
                type, description, date);
    }

    public String getId() { return id; }
    public double getAmount() { return amount; }
    public Category getCategory() { return category; }
    public Type getType() { return type; }
    public String getDescription() { return description; }
    public LocalDate getDate() { return date; }

    @Override
    public String toString() {
        return String.format("[%s] %s | %-8s | %-13s | %8.2f | %s",
                id, date, type, category, amount, description);
    }

    public String toCsv() {
        return String.join(",", id, date.toString(), type.toString(),
                category.toString(), String.valueOf(amount),
                description.replace(",", ";"));
    }

    public static Transaction fromCsv(String line) throws FileParseException {
        try {
            String[] parts = line.split(",", 6);
            if (parts.length < 6) {
                throw new FileParseException("Expected 6 fields, got " + parts.length
                        + " in line: " + line);
            }
            String id = parts[0];
            LocalDate date = LocalDate.parse(parts[1]);
            Type type = Type.valueOf(parts[2]);
            Category category = Category.valueOf(parts[3]);
            double amount = Double.parseDouble(parts[4]);
            String description = parts[5];

            return new Transaction(id, amount, category, type, description, date);

        } catch (FileParseException e) {
            throw e;
        } catch (Exception e) {
            throw new FileParseException("Malformed line: " + line, e);
        }
    }

    private Transaction(String id, double amount, Category category, Type type,
                         String description, LocalDate date) {
        this.id = id;
        this.amount = amount;
        this.category = category;
        this.type = type;
        this.description = description;
        this.date = date;
    }
}