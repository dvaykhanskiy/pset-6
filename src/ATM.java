import java.io.IOException;
import java.util.Scanner;

public class ATM {
    
    private Scanner in;
    private BankAccount activeAccount;
    private Bank bank;
    
    public static final int VIEW = 1;
    public static final int DEPOSIT = 2;
    public static final int WITHDRAW = 3;
    public static final int TRANSFER = 4;
    public static final int LOGOUT = 5;
    
    public static final int INVALID = 0;
    public static final int INSUFFICIENT = 1;
    public static final int SUCCESS = 2;
    
    ////////////////////////////////////////////////////////////////////////////
    //                                                                        //
    // Refer to the Simple ATM tutorial to fill in the details of this class. //
    // You'll need to implement the new features yourself.                    //
    //                                                                        //
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Constructs a new instance of the ATM class.
     */
    
    public ATM() {
        this.in = new Scanner(System.in);
        
        try {
			this.bank = new Bank();
		} catch (IOException e) {
			// cleanup any resources (i.e., the Scanner) and exit
		}
    }
    
    public void startup() {
        System.out.println("Welcome to the AIT ATM!\n");
        
        while (true) {
            System.out.print("Account No.: ");
            long accountNo = in.nextLong();
            
            System.out.print("PIN        : ");
            int pin = in.nextInt();
            
            if (isValidLogin(accountNo, pin)) {
            	activeAccount = bank.login(accountNo, pin);
                System.out.println("\nHello, again, " + activeAccount.getAccountHolder().getFirstName() + "!\n");
                
                boolean validLogin = true;
                while (validLogin) {
                    switch (getSelection()) {
                        case VIEW: showBalance(); break;
                        case DEPOSIT: deposit(); break;
                        case WITHDRAW: withdraw(); break;
                        case TRANSFER: transfer(); break;
                        case LOGOUT: validLogin = false; break;
                        default: System.out.println("\nInvalid selection.\n"); break;
                    }
                }
            } else {
                if (accountNo == -1 && pin == -1) {
                    shutdown();
                } else {
                    System.out.println("\nInvalid account number and/or PIN.\n");
                }
            }
        }
    }
    
    public boolean isValidLogin(long accountNo, int pin) {
    	boolean isValid = false;
    	try {
    		isValid = bank.login(accountNo, pin) != null ? true : false;
    	} catch (Exception e) {
    		isValid = false;
    	}
        return isValid;
    }
    
    public int getSelection() {
        System.out.println("[1] View balance");
        System.out.println("[2] Deposit money");
        System.out.println("[3] Withdraw money");
        System.out.println("[4] Transfer money");
        System.out.println("[5] Logout");
        
        return in.nextInt();
    }
    
    public void showBalance() {
        System.out.println("\nCurrent balance: " + activeAccount.getBalance());
    }
    
    public void deposit() {
        System.out.print("\nEnter amount: ");
        double amount = in.nextDouble();
        
        activeAccount.deposit(amount);
        System.out.println();
    }
    
    public void withdraw() {
        System.out.print("\nEnter amount: ");
        double amount = in.nextDouble();
        
        activeAccount.withdraw(amount);
        System.out.println();
    }
    
    public void transfer() {
    	boolean valid = true;
    	System.out.print("\nEnter account: ");
        long newAccountNumber = in.nextLong();
        System.out.print("\nEnter amount:");
        double amount = in.nextDouble();
        BankAccount transferAccount = bank.getAccount(newAccountNumber);
        if (bank.getAccount(newAccountNumber) == null) {
        	valid = false;
        }
        if (valid) {
        activeAccount.withdraw(amount);
        transferAccount.deposit(amount);
        System.out.print("Transfer accepted.");
        } else {
        	System.out.print("\nTransfer denied.\n");
        }
    }
    
    public void shutdown() {
        if (in != null) {
            in.close();
        }
        
        System.out.println("\nGoodbye!");
        System.exit(0);
    }
    
    /*
     * Application execution begins here.
     */
    
    public static void main(String[] args) {
        ATM atm = new ATM();
        atm.startup();
    }
}
