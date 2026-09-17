import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class FileManager {

    private final String filePath;

    public FileManager(String filePath) {
        this.filePath = filePath;
    }

    public void save(List<Transaction> transactions) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Transaction t : transactions) {
                writer.write(t.toCsv());
                writer.newLine();
            }
        }
    }

    public List<Transaction> load() throws IOException {
        List<Transaction> transactions = new ArrayList<>();

        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            return transactions;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) continue;
                try {
                    transactions.add(Transaction.fromCsv(line));
                } catch (FileParseException e) {
                    System.out.println("Skipping corrupted line " + lineNumber
                            + ": " + e.getMessage());
                }
            }
        }
        return transactions;
    }

    public void writeReport(String reportPath, String content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(reportPath))) {
            writer.write(content);
        }
    }
}