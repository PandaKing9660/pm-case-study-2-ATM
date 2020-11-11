import java.util.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

// for encryption, decryption -> with secret key of user's name.
// so every time it will be different.

class EncryptionDecryption {
    private static SecretKeySpec secretKey;
    private static byte[] key;
    private static final String ALGORITHM = "AES";
    
    
    public void prepareSecreteKey(String myKey) {
        MessageDigest sha = null;
        try {
            key = myKey.getBytes(StandardCharsets.UTF_8);
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public String encrypt(String strToEncrypt, String secret) {
        try {
            prepareSecreteKey(secret);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
        } catch (Exception e) {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }

    public String decrypt(String strToDecrypt, String secret) {
        try {
            prepareSecreteKey(secret);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (Exception e) {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }
}

// ATM machine interface , 
//As ATM's properties may vary from time to time but some of the basic options
// will always be present.
// it contains the options and scanner object. 
interface ATM_Machine{
	Scanner sc = new Scanner(System.in);
	public void welcome();
	public Transfer options(User user);
	
}

// ATM implementing ATM machine
class ATM implements ATM_Machine {
	private int currentUserAccountNo; // changes for every user
	private String currentUserPinNo; // changes for every user
	Admin admin = new Admin();
	
	
	// Ask about Account number.
	public void welcome() {
		System.out.println("Welcome to the ATM , please enter your Account number(5 digits) to proceed. ");
		
		setCurrentUserAccountNo(sc.next());
	}
	
	// setters and getters for Account number 
	public int getCurrentUserAccountNo() {
		return currentUserAccountNo;
	}
	public void setCurrentUserAccountNo(String currentUserAccountNo) {
		// input validation
		int input = isValidInput(currentUserAccountNo);
		if(input>0)
			this.currentUserAccountNo = input;
		else {
			System.out.println("Please enter valid input i.e. 5 digit integer as your Account number.");
			welcome();
		}
	}
	
	
	// setters and getters for USer pin number
	public String getCurrentUserPinNo() {
		return currentUserPinNo;
	}
	public void setCurrentUserPinNo() {
		System.out.println("now sir, please fill your pin.(5 digits) ");
		
		String currentUserPinNo = sc.next();
		
		// input validation
		String input = Integer.toString(isValidInput(currentUserPinNo));
		if(!input.equals("-1")) {
			
			this.currentUserPinNo = input;
			
		}
		else {
			System.out.println("Please enter valid input i.e. 5 digit integer as your account pin number.");
			setCurrentUserPinNo();
		}
	}
	
	// Overriding method of interface. shows the options to user and select them
	public Transfer options(User user) {
		System.out.println("What can we do for you?");
		String options[] =  {
				"Check balance" ,
				"Withdraw cash", 
				"Deposite cash" ,
				"Add Family Accounts",
				"Change PIN",
				"Transfer money to another Account",
				"Get MINI Statements",
				"Change Other Bank Details",
				"Maximum transaction done by you",
				"Exit"
				};

		Transfer trans = new Transfer();
		
		System.out.println("WARNING/INFO. : you will automataclly logout from the atm"
				+ " if you deposite/withdraw cash. (For safty Purposes.)");
		
		while(true) {
			for(int i=0;i<options.length;i++) {
				System.out.println("To "+options[i]+" Press "+(i+1));
			}
			
			
			
			int op=sc.nextInt();
			// options and methods are clear via name. 
			switch(op) {
				case 1:
					checkBalance(user);
					break;
				case 2:
					withdrawCash(user);
					return trans;
				case 3:
					trans = depositeCash(user); 
					return trans;
				case 4:
					addFamilyAccount(user);
					break;
				case 5:
					changePIN(user);
					return trans;
				case 6:
					trans = transferMoneyToAnotherAccount(user);
					return trans;
				case 7:
					getMiniStament(user);
					break;
				case 8:
					ChangeOtherDetails(user);
					break;
				case 9:
					maxTransaction(user);
					break;
				case 10:
					return trans;
				default:
					System.out.println("Invalid option, try again.\n\n");
			}
			/*
			 * whenever the user transfer money from account to family account
			 * the options() function will terminate for safety purposes. 
			 * 
			 * 
			 * */
		}
		
	}
	// max transaction
	private void maxTransaction(User user) {
		int maxWitdrawl = -1, maxDeposite = -1;
		for(AtmOperation op : admin.atmOperation) {
			if(op.getAccountNum()==user.getAccNo()) {
				if(op.getOperation().equals("Deposite"))maxDeposite=Math.max(maxDeposite, op.getMoney());
				else maxWitdrawl=Math.max(maxWitdrawl, op.getMoney());
			}
		}
		
		if(maxWitdrawl<0&&maxDeposite<0) {
			System.out.println("No transaction yet.");
		}else {
		
			if(maxWitdrawl>0)
				System.out.println("Maximum Withdrawl is -> "+maxWitdrawl);
			else
				System.out.println("No Withdrawl yet!!");
			if(maxDeposite>0) {
				System.out.println("Maximum Deposit is -> "+maxDeposite);
			}else {
				System.out.println("No Deposite yet!!!");
			}
		}
	}

	// changing personal details
	private void ChangeOtherDetails(User user) {
		System.out.println("Current Details of the user -> ");
		
		System.out.println("Name -> "+user.getName());
		System.out.println("Phone No. -> "+user.getPhoneNo());
		System.out.println("Added Family Accounts.(Only those which are varified from you...)");
		for(int i=0;i<user.FamilyMembers.size();i++) {
			System.out.println("Account no. -> "+user.FamilyMembers.get(i));
		}
		
		System.out.println("What you want to change?");
		String options[] =  {
								"Change Name",
								"Change Phone number",
								"Remove Someone from Family list.",
							};
		for(int i=0;i<options.length;i++) {
			System.out.println("To "+options[i]+" Press "+(i+1));
		}
		
		int op=sc.nextInt();
		switch(op) {
			case 1:
				changeName(user);
				break;
			case 2:
				changePhoneNumber(user);
				break;
			case 3:
				removeSomeoneFromFamilyList(user);
				break;
			default:
				System.out.println("Invalide output.");
		}
	}
	
	// removing someone from list
	private void removeSomeoneFromFamilyList(User user) {
		System.out.println("Type the Account number of the member which is to be removed.");
		int acc= sc.nextInt();
		for(int i=0;i<user.FamilyMembers.size();i++) {
			if(user.FamilyMembers.get(i)==acc) {
				user.FamilyMembers.remove(i);
				return;
			}
		}
		System.out.println("User was already not present.");
	}
	
	// changing number
	private void changePhoneNumber(User user) {
		while(true) {
			System.out.println("Type new number. (10 digits.)");
			String ph = sc.next();
			int f=1;
			if(ph.length()!=10) {
				System.out.println("Wrong input, Try again.");
			}
			
			for(int i=0;i<10;i++) {
				if(ph.charAt(i)>'9'||ph.charAt(i)<'0') {
					System.out.println("Wrong input, Try again.");
					f=0;
					break;
				}
			}
			if(f==0)continue;
			
			user.setPhoneNo(ph);
			return;
		}
		
		
	}
	private void changeName(User user) {
		// name is the secret key so changing it will affect pin too.
		EncryptionDecryption ed = new EncryptionDecryption();
		
		String oldName = user.getName();
		String oldPin = ed.decrypt(user.getPinNo(), oldName);
		System.out.println("Type new name.");
		String name=sc.next();
		
		String newPinEncoded = ed.encrypt(oldPin, name);
		
		user.setPinNo(newPinEncoded);
		user.setName(name);
	}
	
	// shows the old transactions made by user's account by user or his family members
	private void getMiniStament(User user) {
		System.out.println("Here are your last few Transactions -> ");
		
		System.out.println("From your Account. \n\n");
		for(int i=0;i<admin.atmOperation.size();i++) {
			if(user.getAccNo()==admin.atmOperation.get(i).getAccountNum()) {
				System.out.println("An amount of "+admin.atmOperation.get(i).getMoney()
						+ " was " + admin.atmOperation.get(i).getOperation()+" from account ");
				// Date can be added if required.	
			}
		}
		
		System.out.println("\nFrom your Family Accounts.\n\n");
		
		for(int i=0;i<admin.atmOperation.size();i++) {
			if(user.FamilyMembers.contains(admin.atmOperation.get(i).getAccountNum())) {
				System.out.println("An amount of "+admin.atmOperation.get(i).getMoney()
						+ " was " + admin.atmOperation.get(i).getOperation()+" from family account "
								+ admin.atmOperation.get(i).getAccountNum());
				// Date can be added if required.	
			}
		}
		
	}
	
	
	private Transfer transferMoneyToAnotherAccount(User user) {
		// Possible only when the account is a family account.
		while(true) {
			System.out.println("Write the desired Account number.\n");
			int AccNo = sc.nextInt();
			Transfer trans = new Transfer();
			
			if(user.FamilyMembers.contains(AccNo)) {
				
				System.out.println("Type the Amount you want to deposite/withdraw to/from the given account?");
				System.out.println("\n(Give +ve number if it is a Deposite else a -ve number)\n");
				
				
				int amount = sc.nextInt();
				
				System.out.println("Give your Bank IFSC code for varification -> ");
				
				String ifsc = sc.next();
				
				if(ifsc.equals(user.getIFSC())) {
					user.setAmountInBank((user.getAmountInBank()-amount)+"");
					trans.AccNo=AccNo;
					trans.isTransferd = true;
					trans.Amount=Math.abs(amount);
					
					AtmOperation e = new AtmOperation();
					
					e.setAccountNum(user.getAccNo());
					e.setMoney(amount);
					if(amount>0) {
						e.setOperation("Deposite");
					}else {
						e.setOperation("Withdraw");
					}
					e.setUser(user.getName());
					e.setTransferTo(AccNo);
					
					
					admin.atmOperation.add(e);
					
					System.out.println("Transfer Sucessfull!!!");
					return trans;
					
				}else {
					System.out.println("Sorry Wrong Code, Please Check again.");
					
				}
			}else {
				System.out.println("Sorry, given user is not in family list.\nTry Again..");
				
			}
			System.out.println("Press 1 to exit. ");
			int exit = sc.nextInt();
			if(exit==1) {
				return trans;
			}
		}
		
		
	}
	
	// changing pin  --> encrypting it also
	private void changePIN(User user) {
		System.out.println("\nType current pin\n");
		String currPin = sc.next();
		EncryptionDecryption ed = new EncryptionDecryption();
		
		String nowPin = ed.decrypt(user.getPinNo(),user.getName());
		
		if(nowPin.equals(currPin)) {
			System.out.println("\nType New Pin.\n");
			String newPin = Integer.toString(isValidInput(sc.next()));
			
			user.setPinNo(ed.encrypt(newPin, user.getName()));
			System.out.println("Changed Sucessfully.\n"
					+ "\nYou will be Logged out from the system for Safety purposes.");
			
			
		}else {
			System.out.println("Wrong PIN, No change Possible.");
		}
		
	}
	
	
	// giving family accounts to be added. After the user logs out he will receive an message.
	// if he types his PIN correctly then he will be added and this user will also be added in his family list.
	// "After other user confirm it" --> it's kind of an Adjacency list.
	
	private void addFamilyAccount(User user) {
		System.out.println("Give the Account number you want Acess to -> ");
		int ac = sc.nextInt();
		System.out.println("User has been Added to your family list, "
				+ "we will confirm the changes from the other user."
				+" if he does not allow we will not add it.");
		
		if(user.FamilyMembers.contains(ac)) {
			System.out.println("user already a part of family!!");
		}
		else
			user.FamilyMembers.add(ac);
		
		
		
	}
	
	
	// Cash deposit function.
	private Transfer depositeCash(User user) {
		System.out.println("What amount you want to deposit?");
		int amount = sc.nextInt();
		System.out.println("Transfering amount to -> 1. self or 2. family account??");
		
		int op = sc.nextInt();
		if(op==1) {
			Transfer trans = new Transfer();
			
			System.out.println("Please put the cash in the gap below .");
			// add the user amount in bank.
			user.addAmountInBank(amount);
			System.out.println("Sucessfully Added balance updated from "+(user.getAmountInBank()-amount)
					+" to "+ (user.getAmountInBank()));
			// Add the amount given to ATM 
			// As user need not to know about it so their are 2 functions for this job.
			// one for user, other for bank 
	
			admin.AddCash(amount);
			admin.currentOperation.setMoney(amount);
			admin.currentOperation.setOperation("Deposite");
			admin.currentOperation.setUser(user.getName());
			admin.currentOperation.setAccountNum(user.getAccNo());
			
			admin.atmOperation.add(admin.currentOperation);
			admin.currentOperation=new AtmOperation();	
			return trans;
		}else {
			System.out.println("Now the amount will be transfered from your account to another account.");
			return transferMoneyToAnotherAccount(user, amount);
		}
	}
	
	//overriding the function.
	private Transfer transferMoneyToAnotherAccount(User user, int amount) {
		// Possible only when the account is a family account.
		while(true) {
			System.out.println("Write the desired Account number.\n");
			int AccNo = sc.nextInt();
			Transfer trans = new Transfer();
			
			if(user.FamilyMembers.contains(AccNo)) {
				
				System.out.println("Give your Bank IFSC code for varification -> ");
				
				String ifsc = sc.next();
				
				if(ifsc.equals(user.getIFSC())) {
					user.setAmountInBank((user.getAmountInBank()-amount)+"");
					trans.AccNo=AccNo;
					trans.isTransferd = true;
					trans.Amount=Math.abs(amount);
					
					AtmOperation e = new AtmOperation();
					
					e.setAccountNum(user.getAccNo());
					e.setMoney(amount);
					if(amount>0) {
						e.setOperation("Deposite");
					}else {
						e.setOperation("Withdraw");
					}
					e.setUser(user.getName());
					e.setTransferTo(AccNo);
					
					
					admin.atmOperation.add(e);
					
					System.out.println("Transfer Sucessfull!!!");
					return trans;
					
				}else {
					System.out.println("Sorry Wrong Code, Please Check again.");
				}
			}else {
				System.out.println("Sorry, given user is not in family list.\nTry Again..");
				
			}
			System.out.println("Press 1 to exit. ");
			int exit = sc.nextInt();
			if(exit==1) {
				return trans;
			}
		}
	}
	
	// Withdrawing of cash is a bit different 
	// we need to check about the user current balance and ATM current balance.
	
	
	private void withdrawCash(User user) {
		
		System.out.println("Enter the amount you want to withdraw."
				+ "\n NOTE : only multiple of 100$");
		while(true) {
			int amount = sc.nextInt();
			
			if(amount%100!=0) {
				System.out.println("No Transaction Possible. (Use multiple of 100)");
				continue;
			}
			
			// user has sufficient money or not
			if(amount>user.getAmountInBank()) {
				checkBalance(user);
				System.out.println("\nWithdraw is not possible, as you don't have sufficient balance.");
			}else if(amount>admin.currentCashInATM){
				// ATM does not have sufficient balance
				System.out.println("\nSorry, No Available balance in ATM.\n");
			}else{
				// case for successful Withdraw
				// Still user should provide a number for otp details.
				
				if(OTP(user)) {
					
					System.out.println("\nTake the money...\n");
					user.removeAmountInBank(amount);
					System.out.println("balance updated from "+(user.getAmountInBank()+amount)
							+" to "+ (user.getAmountInBank()));
					// remove cash from user account and ATM .
					admin.subCashInAtm(amount);
					admin.currentOperation.setMoney(amount);
					admin.currentOperation.setOperation("WithDraw");
					admin.currentOperation.setUser(user.getName());
					admin.currentOperation.setAccountNum(user.getAccNo());
					
					admin.atmOperation.add(admin.currentOperation);
					admin.currentOperation=new AtmOperation();
					
					int ct2000=0;
					int ct500=0;
					int ct100=0;
					
					while(amount>0) {
						if(amount-2000>=0) {
							ct2000++;
							amount-=2000;
						}else if(amount-500>=0) {
							ct500++;
							amount-=500;
						}else {
							ct100++;
							amount-=100;
						}
					}
					
					if(ct2000<=admin.noteOf2000)
						admin.noteOf2000-=ct2000;
					else {
						ct2000-=admin.noteOf2000;
						ct500+=4*ct2000;
						ct2000=admin.noteOf2000;
						admin.noteOf2000=0;
					}
					if(ct500<=admin.noteOf500)
						admin.noteOf500-=ct500;
					else {
						ct500-=admin.noteOf500;
						ct100+=5*ct500;
						ct500=admin.noteOf500;
						admin.noteOf500=0;
					}
					
					admin.noteOf100-=ct100;
					
					// denomination
					System.out.println("Used "+ct2000+" notes of 2000$");
					System.out.println("Used "+ct500+" notes of 500$");
					System.out.println("Used "+ct100+" notes of 100$");
					
				}
			}
			break;
		}
		
		
	}
	
	// OTP msg
	private boolean OTP(User user){
		System.out.println("An OTP will be send to the user number.(Fill it)\n");
		System.out.println("<<--OTP in phone-->>");
		
		Random rand = new Random();
		int otp = Math.abs(rand.nextInt())%10000+10000;
		
		System.out.println(otp+" is your OTP, on user phone no. -> " + user.getPhoneNo());
		
		// ON ATM screen
		System.out.println("Type the OTP ");
		int otp2 = sc.nextInt();
		
		return otp==otp2;
	}
	
	// current balance in user account
	private void checkBalance(User user) {
		System.out.println("Your Current balance is "+user.getAmountInBank());
		
	}
	
	// check input validation (5 digit integer)
	private int isValidInput(String input) {
		if(input.length()==5) {
			int output=0;
			for(int i = 0;i<5;i++) {
				if(input.charAt(i)<='9'&&input.charAt(i)>='0') {
					output=output*10+(input.charAt(i)-'0');
				}else {
					return -1;
				}
			}
			
			return output;
		}else {
			return -1;
		}
	}
	
	
}

class Admin{
	public int noteOf100;
	public int noteOf500;
	public int noteOf2000;
	Scanner scan = new Scanner(System.in);
	Admin(){
		// Assuming initial balance in ATM is 10^9
		currentCashInATM = 300000000;
		noteOf2000=50000;
		noteOf100=1000000;
		noteOf500=200000;
		
	}
	public long currentCashInATM; // initially has a value and can be added time to time by bank.
	public ArrayList<AtmOperation> atmOperation = new ArrayList<>();
	/*
	 *to keep track of all the activities done in the ATM 
	 *(like name of user, amount taken, account number etc.) 
	 * 
	 */ 
	public AtmOperation currentOperation = new AtmOperation();
	// for current user, will change after every transaction.
	
	// options for bank employee
		public void bankOptions(ArrayList<User> array) {
			while(true) {
				System.out.println("What do You want to do ?\n");
				String options[] =  {
						"Check Current balance in ATM" ,
						"Increase/Add cash in ATM",
						"See All The Operations done by Customers" ,
						"Update the current DataBase",
						"Exit",
						};
				
				for(int i=0;i<options.length;i++) {
					System.out.println("To "+options[i]+" Press "+(i+1));
				}
				int op=scan.nextInt();
	
				// options and methods are clear via name.
				if(op==1) {
					checkCurrentBalance();
				}else if(op==2)AddCash();
				else if(op==3)seeAll();
				else if(op==4)update(array);
				else if(op==5)return;
				else {
					System.out.println("Invalid option, try again.\n\n");
				}
			}
		
		}
		

	public void update(ArrayList<User> array) {
		try {
		    File myObj = new File("BankData.txt");
			List<String> fileContent = new ArrayList<>(Files.readAllLines(myObj.toPath(), StandardCharsets.UTF_8));
			int j=0;
			for (int i = 0; i < fileContent.size(); j++,i+=7) {
				fileContent.set(i, array.get(j).getName());
				fileContent.set(i+1,Integer.toString(array.get(j).getAccNo()));
				fileContent.set(i+2, array.get(j).getPinNo());
				fileContent.set(i+3, Integer.toString(array.get(j).getAmountInBank()));
				fileContent.set(i+4, array.get(j).getPhoneNo());
				fileContent.set(i+5, array.get(j).getIFSC());
				String accountFamily="";
				for(int k=0;k<array.get(j).FamilyMembers.size();k++) {
					accountFamily+=array.get(j).FamilyMembers.get(k);
					accountFamily+=" ";
				}
				fileContent.set(i+6, accountFamily);
			}
	
			Files.write(myObj.toPath(), fileContent, StandardCharsets.UTF_8);
			System.out.println("File Updated Sucessfully!!!");
		}catch(Exception e) {
			System.out.println("Error occured -> "+e);
		}
	}

	public void checkCurrentBalance() {
		System.out.println("Current balance is -> "+currentCashInATM);
	}
	// subtract ATM money
	public void subCashInAtm(int amount) {
		currentCashInATM-=amount;
	}
	// Add money deposit by user
	public void AddCash(int amount) {
		currentCashInATM+=amount;
	}
	// Add money deposit/send by bank
	public void AddCash() {
		System.out.println("Give the amount to be added. ");
		int amount = scan.nextInt();
		currentCashInATM+=amount;
		System.out.println("Added sucessfully =>\nCurrent balance is -> "+currentCashInATM);
	}
	
	// see all the entries (Bank ATM transactions).
	
	public void seeAll() {
		int n = atmOperation.size();
		
		if(n>0) {
			for(int i=0;i<n;i++) {
				System.out.println("Mr. "+atmOperation.get(i).getUser()
						+" With Account number "+atmOperation.get(i).getAccountNum()
						+" has "+atmOperation.get(i).getOperation()
						+ " Amount of "+atmOperation.get(i).getMoney());
				if(atmOperation.get(i).getTransferTo()>0) {
					System.out.println("To the Account Number -> "+atmOperation.get(i).getTransferTo());
				}
			}
		}else {
				System.out.println("No operation yet.");
		}
		
	}
	
}


// basic ATM operations for users
class AtmOperation{
	private String user;
	private int money;
	private String operation;
	private int AccountNum;
	private int TransferTo=-1;
	private String newName = "";
	private String newNumber = "";
	
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public int getMoney() {
		return money;
	}
	public void setMoney(int money) {
		this.money = money;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public int getAccountNum() {
		return AccountNum;
	}
	public void setAccountNum(int accountNum) {
		AccountNum = accountNum;
	}
	public int getTransferTo() {
		return TransferTo;
	}
	public void setTransferTo(int transferTo) {
		TransferTo = transferTo;
	}
	public String getNewName() {
		return newName;
	}
	public void setNewName(String newName) {
		this.newName = newName;
	}
	public String getNewNumber() {
		return newNumber;
	}
	public void setNewNumber(String newNumber) {
		this.newNumber = newNumber;
	}
}

// A normal user 
class User{
	private int accNo;
	private String pinNo;
	private int amountInBank;
	private String name;
	private String PhoneNo;
	ArrayList<Integer> FamilyMembers = new ArrayList<>();
	private String IFSC;
	
	public void AddFamilyMember(int e) {
		FamilyMembers.add(e);
	}
	
	public int getAccNo() {
		return accNo;
	}

	public void setAccNo(String accNoS) {
		int accNo = toInt(accNoS);
		this.accNo = accNo;
	}

	

	public String getPinNo() {
		return pinNo;
	}

	public void setPinNo(String pinNo) {
		this.pinNo = pinNo;
	}

	public int getAmountInBank() {
		return amountInBank;
	}

	public void setAmountInBank(String amountInBankS) {
		int amountInBank = toInt(amountInBankS);
		this.amountInBank = amountInBank;
	}
	
	public void addAmountInBank(int amount) {
		this.amountInBank+=amount;
	}
	
	public void removeAmountInBank(int amount) {
		this.amountInBank-=amount;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhoneNo() {
		return PhoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		PhoneNo = phoneNo;
	}

	public String getIFSC() {
		return IFSC;
	}

	public void setIFSC(String iFSC) {
		IFSC = iFSC;
	}
	private int toInt(String string) {
		int num = 0;
		for(int i=0;i<string.length();i++){
			num=num*10+(string.charAt(i)-'0');
		}
		
		return num;
	}
	
}

class Transfer {
	int AccNo;
	boolean isTransferd;
	int Amount;
}


// Bank Data base (Assumed)
// Using file handling. 
class BankDataBase {
	// ArrayList for all the bank accounts 
	ArrayList<User> accountList = new ArrayList<>();
	
	BankDataBase(){
		 try {
		      File myObj = new File("BankData.txt");
		      Scanner myReader = new Scanner(myObj);
		      int i=0;

				User user = new User();
		      while (myReader.hasNextLine()) {
			    String data = myReader.nextLine();
			    //taking data from file.
					
			    
			    // Available in the form.
			    /*
			     * name
			     * account number
			     * pin(encrypted)
			     * amount in bank
			     * phone number
			     * bank IFSC code
			     * arrayList of family members (initially empty for most of the cases)
			     * 
			     */
				switch(i) {
					case 0:
						user.setName(data);
						i++;
						break;
					case 1:
						user.setAccNo(data);
						i++;
						break;
					case 2:
						user.setPinNo(data);
						i++;
						break;
					case 3:
						user.setAmountInBank(data);
						i++;
						break;
					case 4:
						user.setPhoneNo(data);
						i++;
						break;
					case 5:
						user.setIFSC(data);
						i++;
						break;
					case 6:
						int num=0;
						for(int j=0;j<data.length();j++) {
							
							if(data.charAt(j)==' ') {
								user.FamilyMembers.add(num);
								num=0;
								continue;
							}
							num = num*10 + (data.charAt(j)-'0');
						}
						
						accountList.add(user);
						user = new User();
						i=0;
						break;
				}
				// Making and adding random user
		      }
		      myReader.close();
		    } catch (FileNotFoundException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }
		
		
	}
	
	

	//check valid user pin
	public boolean checkUserAccountPin(int AccNo, String Pin) {
		EncryptionDecryption decp = new EncryptionDecryption();
		String RealPassword=accountList.get(AccNo-10000).getPinNo();
		// decripiting data
		try {
			RealPassword = decp.decrypt(RealPassword, accountList.get(AccNo-10000).getName());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(Pin+" "+RealPassword);
		if(RealPassword.equals(Pin)) {
			return true;
		}else {
			return false;
		}
	}
	
	// check valid Account number
	public boolean checkUserAccount(int AccNo) {
		
		if(AccNo>=10000&&AccNo<=accountList.size()+10000) {
			
			return true;
		}
		return false;
	}
	
	// Confirm that the person is bank employee or not.
	
	public boolean checkBankEmployee() {
		System.out.println("Please tell the password given to Employees.");
		Scanner sc = new Scanner(System.in);
		String password = sc.next();
		
		
		// Assume secret password be "CoolAdityaItsMe".
		if(password.equals("CoolAdityaItsMe")) {
			return true;
		}else return false;
		
	}
}


public class CaseStudy2 {

	public static void main(String[] args) {
		// initially we have scanner object, ATM , BankDatBase and a string
		ATM atm = new ATM ();
		BankDataBase bankDataBase = new BankDataBase();
		
		String s;
		Scanner sc = new Scanner(System.in);
		
		while(true) {
			System.out.println("Who are you sir, \n1. -> user \n2. -> bank employee. ");
			s = sc.next();
			if(s.equals("1")) {
				// if person is a user he will be get a 
				//welcome msg alone with Account number Asking.
				
				atm.welcome();
				// Validation if account number exist or not.
				
				if(bankDataBase.checkUserAccount(atm.getCurrentUserAccountNo())) {
					
					// ask for pin number
					atm.setCurrentUserPinNo();
					
					// checking of pin number
					if(bankDataBase.checkUserAccountPin(atm.getCurrentUserAccountNo(),atm.getCurrentUserPinNo())) {
						
						// now we are sure that the user is a bank client
						// ask for his options
						
						int index = atm.getCurrentUserAccountNo()-10000;
						int famSize = bankDataBase.accountList.get(index).FamilyMembers.size();
						
						
						System.out.println("Welcome to the ATM Mr. "+bankDataBase.accountList.get(index).getName()+"\n");
						Transfer trans = atm.options(bankDataBase.accountList.get(index));
						if(trans.isTransferd) {
							bankDataBase.accountList.get(trans.AccNo-10000).setAmountInBank((bankDataBase.accountList.get(trans.AccNo-10000).getAmountInBank()+trans.Amount)+"");
						}
						ArrayList<Integer> extra = new ArrayList<>();
						
						if(famSize<bankDataBase.accountList.get(index).FamilyMembers.size()) {
							System.out.println("As you have logged out, Newly added family"
									+ "members will be checked and varified\n\n");
							
							int size=bankDataBase.accountList.get(index).FamilyMembers.size();
							for(int i=famSize;i<size;i++) {
								// Message to all the new users about verification.
								System.out.println("Mr. "+bankDataBase.accountList.get(index).getName()
										+ " Added you in his Family Account , it gives him Access to your Account."
										+ "\nType your pin to validate or the request will be denied.\n");
								String pin = sc.next();
								EncryptionDecryption ed = new EncryptionDecryption();
								String realPin = ed.decrypt(bankDataBase.accountList.get(bankDataBase.accountList.get(index).FamilyMembers.get(i)-10000).getPinNo(),
										bankDataBase.accountList.get(bankDataBase.accountList.get(index).FamilyMembers.get(i)-10000).getName());
								
								if(realPin.equals(pin)){
									System.out.println("Varified");
									bankDataBase.accountList.get(bankDataBase.accountList.get(index).FamilyMembers.get(i)-10000).FamilyMembers.add(index+10000);
									
									
								}else {
									System.out.println("Wrong, user not Added\n");
									extra.add(bankDataBase.accountList.get(index).FamilyMembers.get(i));
									
								}
								
							}
							for(int i=0;i<extra.size();i++) {
								bankDataBase.accountList.get(index).FamilyMembers.remove(extra.get(i));
							}
						}
						
						
					}else {
						System.out.println("Not a valid pin for the given Account number, Check the pin and try again..\n");
					}
				}else {
					System.out.println("This user is not avalible.\n");
				}
			}else if(s.equals("2")) {
				// bank employee will be asked the password 
				//(which is in this case is "CoolAdityaItsMe")
				if(bankDataBase.checkBankEmployee())
					atm.admin.bankOptions(bankDataBase.accountList);
				else
					System.out.println("Not correct info.");
			}else {
				System.out.println("Invalid option\n");
			}
			
			// to exit/close the ATM. 
			// Not very realistic in real life as ATM will run forever
			// option given to avoid infinite loop.
			
			System.out.println("Press 1 to exit. else press anything.");
			
			String isExit = sc.next();
			
			if(isExit.equals("1"))break;
		   
		}
		atm.admin.update(bankDataBase.accountList);
	}
}