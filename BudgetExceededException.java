public class BudgetExceededException extends RuntimeException{
    private final Category category;
    private final double spent;
    private final double limit;

    public BudgetExceededException(Category category, double spent,double limit){
        super(String.format("Budget exceeded for %s: spent %.2f / limit %.2f",
                category, spent, limit));
                this.category=category;
                this.spent=spent;
                this.limit=limit;
    }
    public Category getCategory(){return category;}
    public double getSpent(){return spent;}
    public double getLimit(){return limit;}
}
