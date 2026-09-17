public enum Category{
    FOOD(3000.0),
    RENT(10000.0),
    ENTERTAINMENT(1500.0),
    TRANSPORT(2000.0),
    UTILITIES(2500.0),
    SALARY(0.0),
    OTHER(1000.0);

    private final double budgetLimit;
 
    Category(double budgetLimit) {
        this.budgetLimit = budgetLimit;
    }

    public double getBudgetLimit(){
        return budgetLimit;
    }
}