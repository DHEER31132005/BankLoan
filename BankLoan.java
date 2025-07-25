import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@SpringBootApplication
@RestController
@RequestMapping("/bank")
public class BankLoan {

    public static void main(String[] args) {
        SpringApplication.run(BankLoanSystemApplication.class, args);
    }

    static class Loan {
        String loanId;
        String customerId;
        double principal;
        int years;
        double rate;
        double totalInterest;
        double totalAmount;
        double monthlyEmi;
        double amountPaid = 0;
        int emisPaid = 0;
        List<String> transactions = new ArrayList<>();

        Loan(String loanId, String customerId, double principal, int years, double rate) {
            this.loanId = loanId;
            this.customerId = customerId;
            this.principal = principal;
            this.years = years;
            this.rate = rate;
            this.totalInterest = (principal * years * rate) / 100;
            this.totalAmount = principal + totalInterest;
            this.monthlyEmi = Math.ceil(totalAmount / (years * 12));
            transactions.add("LEND: P=" + principal + ", N=" + years + ", R=" + rate);
        }

        int totalEmis() {
            return (int) Math.ceil(totalAmount / monthlyEmi);
        }

        int emisLeft() {
            return totalEmis() - emisPaid;
        }

        double balanceAmount() {
            return totalAmount - amountPaid;
        }
    }

    Map<String, List<Loan>> customerLoans = new HashMap<>();

    @PostMapping("/lend")
    public Map<String, Object> lend(@RequestParam String customerId,
                                    @RequestParam double principal,
                                    @RequestParam int years,
                                    @RequestParam double rate) {
        String loanId = UUID.randomUUID().toString();
        Loan loan = new Loan(loanId, customerId, principal, years, rate);
        customerLoans.computeIfAbsent(customerId, k -> new ArrayList<>()).add(loan);

        Map<String, Object> response = new HashMap<>();
        response.put("loanId", loanId);
        response.put("totalAmount", loan.totalAmount);
        response.put("monthlyEmi", loan.monthlyEmi);
        return response;
    }


    @PostMapping("/pay")
    public String payment(@RequestParam String customerId,
                          @RequestParam String loanId,
                          @RequestParam double amount,
                          @RequestParam(required = false, defaultValue = "false") boolean isEmi) {
        Loan loan = getLoan(customerId, loanId);
        if (loan == null) return "Loan not found.";

        if (isEmi) {
            int emis = (int) (amount / loan.monthlyEmi);
            loan.amountPaid += emis * loan.monthlyEmi;
            loan.emisPaid += emis;
            loan.transactions.add("EMI PAYMENT: " + emis + " EMI(s) = " + (emis * loan.monthlyEmi));
        } else {
            loan.amountPaid += amount;
            int reducedEmis = (int) (amount / loan.monthlyEmi);
            loan.emisPaid += reducedEmis;
            loan.transactions.add("LUMP SUM: Paid = " + amount);
        }
        return "Payment recorded.";
    }

 
    @GetMapping("/ledger")
    public Map<String, Object> ledger(@RequestParam String customerId,
                                      @RequestParam String loanId) {
        Loan loan = getLoan(customerId, loanId);
        if (loan == null) return Map.of("error", "Loan not found.");

        Map<String, Object> data = new HashMap<>();
        data.put("transactions", loan.transactions);
        data.put("balanceAmount", loan.balanceAmount());
        data.put("monthlyEmi", loan.monthlyEmi);
        data.put("emisLeft", loan.emisLeft());
        return data;
    }

   
    @GetMapping("/overview")
    public Object overview(@RequestParam String customerId) {
        List<Loan> loans = customerLoans.get(customerId);
        if (loans == null || loans.isEmpty()) return "No loans found.";

        List<Map<String, Object>> result = new ArrayList<>();
        for (Loan loan : loans) {
            Map<String, Object> data = new HashMap<>();
            data.put("loanId", loan.loanId);
            data.put("principal", loan.principal);
            data.put("totalAmount", loan.totalAmount);
            data.put("monthlyEmi", loan.monthlyEmi);
            data.put("totalInterest", loan.totalInterest);
            data.put("amountPaid", loan.amountPaid);
            data.put("emisLeft", loan.emisLeft());
            result.add(data);
        }
        return result;
    }

    
    private Loan getLoan(String customerId, String loanId) {
        List<Loan> loans = customerLoans.get(customerId);
        if (loans == null) return null;
        for (Loan loan : loans) {
            if (loan.loanId.equals(loanId)) return loan;
        }
        return null;
    }
}
