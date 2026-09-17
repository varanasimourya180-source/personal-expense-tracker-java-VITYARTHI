# Problem Statement

## The Problem

Personal spending is easy to record and hard to control. Most people who track
their expenses at all do so passively — entries go into a notebook, a spreadsheet,
or an app, and the total is only discovered when someone deliberately sits down to
check it. By that point the money is already spent. The information exists, but it
arrives too late to change the decision it should have influenced.

The gap is not data capture. It is the absence of feedback at the moment spending
happens. A tracker that only answers questions when asked cannot prevent
overspending; it can only document it afterwards.

## Proposed Solution

This project is a command-line personal expense tracker that adds an active
monitoring layer on top of ordinary transaction recording. Alongside the usual
ability to add, view, filter and delete income and expense records, a separate
background thread continuously recalculates spending against per-category budget
limits and surfaces a warning as soon as a category approaches its limit, and an
alert once it is exceeded.

Because the monitoring runs concurrently with user input rather than as a step in
the menu flow, the user receives the warning while they are still working — not
only when they choose to run a report.

## Scope of the Project

### In scope

- Recording income and expense transactions with amount, category, description
  and date
- Seven predefined spending categories, each with an associated monthly budget
  limit
- Create, read, filter and delete operations on transactions
- Persistence of all data to a local CSV file, loaded automatically at startup
  and written on exit
- Continuous background monitoring of category spending with threshold-based
  warnings at 80% and alerts at 100% of a budget
- Summary reporting: total income, total expenditure, per-category spend against
  budget, and a category-by-month spending grid
- Report output both to the console and to a text file
- Graceful handling of invalid user input and corrupted stored data

### Out of scope

- Graphical or web user interface — the application is command-line only
- Multi-user accounts, authentication, or access control
- Bank account integration or automatic transaction import
- Currency conversion or multi-currency support
- Network, cloud, or database-backed storage
- Financial forecasting, investment tracking, or tax calculation

## Target Users

The primary users are individuals managing a modest personal budget who prefer a
lightweight tool over a full financial application:

- **Students** living on a fixed monthly allowance, for whom a single overspend in
  one category has immediate consequences
- **Early-career professionals** starting to track expenses seriously and wanting
  visibility into where a salary actually goes
- **Anyone on a fixed budget** who wants to be told when a category is running out
  rather than having to check

A secondary consideration is the technically comfortable user: this is a terminal
application with plain-text storage, which suits someone who wants their data in a
readable, portable format they control rather than locked in an app.

## High-Level Features

1. **Transaction Management** — add, list, filter by category or type, and delete
   income and expense records, with validation on every field.

2. **Category Budgets** — each spending category carries its own monthly limit
   against which actual spending is continuously measured.

3. **Background Budget Monitoring** — a concurrent monitoring thread evaluates
   spending against limits at a fixed interval and emits warnings and alerts
   without blocking or interrupting user input.

4. **Persistent Storage** — all transactions are saved to and restored from a
   local CSV file, with malformed records skipped individually rather than
   failing the entire load.

5. **Reporting and Analytics** — summary totals, per-category spend versus budget,
   and a month-by-category breakdown, viewable on screen and exportable to a file.

6. **Fault Tolerance** — invalid amounts, unparseable dates, unknown menu choices
   and corrupted data lines are all handled through a purpose-built exception
   hierarchy so the application does not terminate unexpectedly.