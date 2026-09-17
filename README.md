# Personal Expense Tracker with Smart Budget Alerts

A command-line personal finance tracker written in Java. It records income and
expenses, persists them to a CSV file between runs, generates spending reports,
and runs a background thread that watches your category budgets and warns you in
real time when you approach or exceed them.

---

## Overview

Most simple expense trackers are passive — you only find out you overspent when
you go looking. This project adds an active monitoring layer: a separate daemon
thread periodically recalculates spending per category and pushes alerts to the
console while you continue using the app, without interrupting or blocking your
input.

The project was built to demonstrate core Java concepts in a practical setting:
exception handling with a custom exception hierarchy, multithreading with
synchronized shared state, the Collections Framework, String and array
operations, and character-oriented I/O streams.

---

## Features

- **Transaction management (CRUD)** — add, view, filter, and delete income and
  expense records
- **Category-based organisation** — seven built-in categories, each with its own
  monthly budget limit
- **File persistence** — transactions are loaded automatically on startup and
  saved on exit, in plain CSV format
- **Live budget monitoring** — a background thread checks spending every few
  seconds and prints a warning at 80% of a budget and an alert at 100%
- **Reporting** — totals, per-category spend against budget, and a
  category-by-month spending grid, printed to screen and written to a report file
- **Robust input handling** — invalid amounts, malformed dates, unknown menu
  options, and corrupted data lines are all handled without crashing

---

## Technologies Used

| Item | Detail |
|------|--------|
| Language | Java 17 or later (uses `switch` expressions and text blocks) |
| Build | `javac` — no external build tool or dependencies |
| Storage | Plain CSV file (`transactions.csv`), created automatically |
| Libraries | Java standard library only (`java.io`, `java.util`, `java.time`) |

No third-party libraries are used, so no dependency installation is required.

---

## Project Structure

```
.
├── Main.java                          # CLI entry point and menu loop
├── ExpenseTracker.java                # Core CRUD + in-memory storage (synchronized)
├── Transaction.java                   # Transaction data model + CSV conversion
├── Category.java                      # Category enum with budget limits
├── FileManager.java                   # Save/load via character I/O streams
├── BudgetMonitor.java                 # Background thread for budget alerts
├── ReportGenerator.java               # Summary + monthly grid reporting
├── InvalidTransactionException.java   # Checked — validation failures
├── FileParseException.java            # Checked — malformed saved data
└── BudgetExceededException.java       # Unchecked — budget threshold breach
```

Generated at runtime (not committed):
- `transactions.csv` — saved transaction data
- `report.txt` — most recently generated report

---

## Prerequisites

You need a **JDK (Java Development Kit) version 17 or later** — a JRE alone is
not enough, since you need the `javac` compiler.

Check what you have:

```
java -version
javac -version
```

If `javac` is not found, install a JDK (for example Eclipse Temurin or Oracle
JDK 17+) and ensure its `bin` directory is on your `PATH`.

---

## Setup and Running

### 1. Clone the repository

```
git clone https://github.com/{your-username}/{your-repo-name}.git
cd {your-repo-name}
```

### 2. Compile

**Linux / macOS / Git Bash:**
```
javac *.java
```

**Windows PowerShell:**
```
javac *.java
```

> Note: PowerShell 5 does not support `&&` as a command separator. Run the
> compile and run commands on separate lines, or join them with `;` instead:
> `javac *.java; java Main`

### 3. Run

```
java Main
```

That is the entire setup — there is no configuration file to edit, no database
to provision, and no dependencies to install. On first run the data file does
not exist yet, which is expected; it is created when you exit.

---

## Using the Application

On launch you get a numbered menu:

```
1. Add transaction
2. View all transactions
3. Delete a transaction
4. View transactions by category
5. Generate report
6. Save & Exit
```

- **Adding a transaction** prompts for type (income/expense), category, amount,
  description, and date. Leaving the date blank uses today.
- **Deleting** requires the 8-character transaction ID shown in the listing.
- **Generating a report** prints the summary and also writes it to `report.txt`.
- **Save & Exit** stops the monitoring thread cleanly and writes all data to
  `transactions.csv`.

While you use the menu, the budget monitor runs in the background and prints
lines like:

```
[WARNING] FOOD at 82.5% of budget (2475.00 / 3000.00)
[ALERT]   ENTERTAINMENT over budget! (1650.00 / 1500.00)
```

---

## Testing the Application

The project can be verified manually from the command line. The following
scenarios cover the main paths:

**1. Validation / exception handling**
- Choose "Add transaction" and enter `-100` as the amount → the input helper
  rejects it and re-prompts.
- Enter `abc` as the amount → caught as a `NumberFormatException` and re-prompted.
- Enter `31-02-2026` as the date → falls back to today's date with a message.

**2. Persistence (I/O streams)**
- Add two or three transactions, then choose "Save & Exit".
- Confirm `transactions.csv` now exists and contains one line per transaction.
- Run `java Main` again → the transactions are reloaded and listed.

**3. Corrupted data handling**
- Open `transactions.csv` in a text editor and change one line to `this,is,broken`.
- Run the app → that single line is reported as skipped and the remaining valid
  transactions still load, rather than the whole file failing.

**4. Multithreading / budget alerts**
- Add expenses in the `FOOD` category totalling more than 2,400 (80% of its
  3,000 limit) → within a few seconds a `[WARNING]` line appears on its own
  while the menu remains usable.
- Continue until the total exceeds 3,000 → the message escalates to `[ALERT]`.
- Confirm that the alert appears without you pressing anything, which shows the
  monitoring thread is genuinely running concurrently with the main input thread.

**5. Reporting**
- Choose "Generate report" → verify the per-category figures match the
  transactions you entered, and that `report.txt` is written.

---

## Design Notes

- `ExpenseTracker`'s public methods are `synchronized` because the background
  monitor thread reads the transaction data while the main thread may be
  modifying it. Without this, concurrent access to the underlying `ArrayList`
  is a race condition.
- The exception hierarchy deliberately mixes checked and unchecked exceptions:
  validation and file-parsing failures are checked (the caller must handle a
  foreseeable, recoverable problem), while a budget breach is unchecked
  (it is a notification, not an error every caller must guard against).
- The monitor tracks which categories it has already warned about so a single
  overspend does not reprint the same alert on every cycle.

---

## Future Enhancements

- Configurable budget limits loaded from a properties file instead of hard-coded
  values in the `Category` enum
- Date-range filtering and month-over-month comparison in reports
- Recurring transaction support (rent, subscriptions) added automatically
- Export reports to CSV or PDF for sharing
- Replace flat-file storage with an embedded database such as SQLite