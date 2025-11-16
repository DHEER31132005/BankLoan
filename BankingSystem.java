import java.util.ArrayList;
import java.util.List;


abstract class BankAccount {
    // Encapsulation: Private fields with public getters/setters
    private String accountNumber;
    private String accountHolder;
    protected double balance;
    private final String accountType;
    
    public BankAccount(String accountNumber, String accountHolder, double balance, String accountType) {
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.balance = balance;
        this.accountType = accountType;
    }
    
    // Abstraction: Abstract methods
    public abstract void calculateInterest();
    public abstract void applyFees();
    
    // Encapsulation: Public methods to access private data
    public String getAccountNumber() { return accountNumber; }
    public String getAccountHolder() { return accountHolder; }
    public double getBalance() { return balance; }
    public String getAccountType() { return accountType; }
    
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            System.out.println("Deposited: $" + amount);
        }
    }
    
    public void withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            System.out.println("Withdrawn: $" + amount);
        }
    }
    
    // Polymorphism: Method that can be overridden
    public void displayAccountInfo() {
        System.out.println("Account: " + accountNumber + " | Holder: " + 
                          accountHolder + " | Balance: $" + balance);
    }
}

// Inheritance: SavingsAccount extends BankAccount
class SavingsAccount extends BankAccount {
    private double interestRate;
    
    public SavingsAccount(String accountNumber, String accountHolder, double balance, double interestRate) {
        super(accountNumber, accountHolder, balance, "Savings");
        this.interestRate = interestRate;
    }
    
    // Polymorphism: Overriding abstract methods
    @Override
    public void calculateInterest() {
        double interest = balance * interestRate / 100;
        balance += interest;
        System.out.println("Interest added: $" + interest);
    }
    
    @Override
    public void applyFees() {
        // No fees for savings account
        System.out.println("No fees applied for savings account");
    }
    
    // Polymorphism: Overriding display method
    @Override
    public void displayAccountInfo() {
        super.displayAccountInfo();
        System.out.println("Type: Savings | Interest Rate: " + interestRate + "%");
    }
}

// Inheritance: CheckingAccount extends BankAccount
class CheckingAccount extends BankAccount {
    private double monthlyFee;
    
    public CheckingAccount(String accountNumber, String accountHolder, double balance, double monthlyFee) {
        super(accountNumber, accountHolder, balance, "Checking");
        this.monthlyFee = monthlyFee;
    }
    
    // Polymorphism: Different implementation
    @Override
    public void calculateInterest() {
        System.out.println("No interest for checking account");
    }
    
    @Override
    public void applyFees() {
        balance -= monthlyFee;
        System.out.println("Monthly fee applied: $" + monthlyFee);
    }
    
    @Override
    public void displayAccountInfo() {
        super.displayAccountInfo();
        System.out.println("Type: Checking | Monthly Fee: $" + monthlyFee);
    }
}

// Inheritance: LoanAccount extends BankAccount
class LoanAccount extends BankAccount {
    private double loanAmount;
    private double interestRate;
    
    public LoanAccount(String accountNumber, String accountHolder, double loanAmount, double interestRate) {
        super(accountNumber, accountHolder, 0, "Loan");
        this.loanAmount = loanAmount;
        this.interestRate = interestRate;
        this.balance = -loanAmount; // Negative balance represents debt
    }
    
    @Override
    public void calculateInterest() {
        double interest = Math.abs(balance) * interestRate / 100;
        balance -= interest;
        System.out.println("Loan interest added: $" + interest);
    }
    
    @Override
    public void applyFees() {
        double lateFee = 25.0;
        if (balance < -1000) {
            balance -= lateFee;
            System.out.println("Late fee applied: $" + lateFee);
        }
    }
    
    public void makePayment(double amount) {
        if (amount > 0) {
            balance += amount;
            System.out.println("Payment made: $" + amount);
        }
    }
    
    @Override
    public void displayAccountInfo() {
        System.out.println("Loan Account: " + getAccountNumber() + " | Holder: " + 
                          getAccountHolder() + " | Outstanding: $" + Math.abs(balance));
    }
}

// Bank class to demonstrate polymorphism
class Bank {
    private List<BankAccount> accounts; 
    
    public Bank() {
        accounts = new ArrayList<>();
    }
    
    public void addAccount(BankAccount account) {
        accounts.add(account);
    }
    
    // Polymorphism: Same method works with different account types
    public void processMonthlyStatements() {
        System.out.println("\n=== Processing Monthly Statements ===");
        for (BankAccount account : accounts) {
            account.calculateInterest();
            account.applyFees();
            account.displayAccountInfo();
            System.out.println("-------------------");
        }
    }
    
    public void displayAllAccounts() {
        System.out.println("\n=== All Bank Accounts ===");
        for (BankAccount account : accounts) {
            account.displayAccountInfo();
        }
    }
}

public class BankingSystem {
    public static void main(String[] args) {
        Bank bank = new Bank();
        
        // Creating different types of accounts
        SavingsAccount savings = new SavingsAccount("SAV001", "John Doe", 5000, 2.5);
        CheckingAccount checking = new CheckingAccount("CHK001", "John Doe", 2500, 10);
        LoanAccount loan = new LoanAccount("LN001", "John Doe", 10000, 5.0);
        
        // Adding accounts to bank
        bank.addAccount(savings);
        bank.addAccount(checking);
        bank.addAccount(loan);
        
        // Demonstrate operations
        savings.deposit(1000);
        checking.withdraw(500);
        loan.makePayment(2000);
        
        // Polymorphism in action
        bank.displayAllAccounts();
        bank.processMonthlyStatements();
    }
}