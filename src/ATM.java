import java.io.IOException;
import java.util.Scanner;

public class ATM {
    
    private Scanner in;
    private BankAccount activeAccount;
    private Bank bank;
    private User newUser;
    
    public static final int VIEW = 1;
    public static final int DEPOSIT = 2;
    public static final int WITHDRAW = 3;
    public static final int TRANSFER = 4;
    public static final int LOGOUT = 5;
    
    public static final int INVALID = 0;
    public static final int INSUFFICIENT = 1;
    public static final int SUCCESS = 2;
    public static final int OVERFLOW = 3;
    
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
    
    public void newAccount() {
    	System.out.println("\nFirst name:");
    	String firstName = in.nextLine();
    	System.out.println("\nLast name:");
    	String lastName = in.nextLine();
    	System.out.println("\nPin:");
    	int pin = in.nextInt();
    	
    	newUser = new User(firstName, lastName);
    	bank.createAccount(pin, newUser);
    	bank.save();
    }
    
    public void startup() {
        System.out.println("Welcome to the AIT ATM!\n");
        
        while (true) {
            System.out.print("Account No.: ");
            if (in.hasNextLong()) {
            long accountNo = in.nextLong();
            } else if (in.nextLine().strip().equals("+")) {
            	long accountNo = 0;
            	newAccount();
            }
            
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
        
        if (in.hasNextInt()) {
        	return in.nextInt();
        } else {
        	in.nextLine();
        	return 0;
        }
    }
    
    public void showBalance() {
        System.out.println("\nCurrent balance: " + activeAccount.getBalance());
    }
    
    public void deposit() {
    	double amount = 0;
		boolean validAmount = true;
		System.out.print("\nEnter amount: ");
		try {
			amount = in.nextDouble();
		}catch(Exception e) {
			validAmount = false;
			in.nextLine();
		}
		
		if(validAmount) {
			int status = activeAccount.deposit(amount);
            if (status == ATM.INVALID) {
                System.out.println("\nDeposit rejected. Amount must be greater than $0.00.\n"); 
            } else if(status == ATM.OVERFLOW) {
            	System.out.println("\nDeposit rejected. Amount would cause balance to exceed $999,999,999,999.99.\n");
            }else if (status == ATM.SUCCESS) {
                System.out.println("\nDeposit accepted.\n");
                bank.save();
            }
		}else {
			System.out.println("\nDeposit rejected. Enter vaild amount.\n");
		}
        
    }
    
    public void withdraw() {
    	boolean valid = true;
        System.out.print("\nEnter amount: ");
        double amount = in.nextDouble();
        try {
        	amount = in.nextDouble();
        } catch (Exception e) {
        	valid = false;
        	in.nextLine();
        }
        int status = activeAccount.withdraw(amount);
        if (status == ATM.INVALID) {
            System.out.println("\nWithdrawal rejected. Amount must be greater than $0.00.\n");
        } else if (status == ATM.INSUFFICIENT) {
            System.out.println("\nWithdrawal rejected. Insufficient funds.\n");
        } else if (status == ATM.SUCCESS) {
            System.out.println("\nWithdrawal accepted.\n");
        }
    }
    
    public void transfer() {
    	boolean valid = true;
    	System.out.print("\nEnter account: ");
        long newAccountNumber = in.nextLong();
        System.out.print("\nEnter amount:");
        double amount = in.nextDouble();
        if (bank.getAccount(newAccountNumber) == null) {
        	valid = false;
        }
        if (valid) {
        	BankAccount transferAccount = bank.getAccount(newAccountNumber);
        	int status = activeAccount.withdraw(amount);
        	if (status == INVALID) {
        		System.out.print("\nTransfer rejected. Amount must be greater than $0.00.\n");
        	} else if (status == INSUFFICIENT) {
        		System.out.print("\nTransfer rejected. Insufficient funds.\n");
        	} else if (status == SUCCESS) {
        		transferAccount.deposit(amount);
                System.out.println("\nTransfer accepted.\n");
            }
        } else {
        	System.out.print("\nTransfer denied, invalid account.\n");
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
