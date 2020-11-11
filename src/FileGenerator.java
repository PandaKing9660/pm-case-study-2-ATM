import java.util.*;


import java.io.FileWriter;
import java.io.IOException;


public class FileGenerator {

	public static void main(String[] args) {
		
		try {
		      FileWriter myWriter = new FileWriter("BankData.txt");

				// contractor will make 5000 arbitrary users with random name, 
				// But Account number and pin are calculated prior.
				// (Pin can be changed but for experimental/Example it is like this.)
				
				
				// IFSC code for my bank and if money is needed to be transfered to another bank,
				// we can use the same IFSC thing with different values.
				
				Random rand = new Random();
				EncryptionDecryption encrip = new EncryptionDecryption();
				String alph = "abcdefghijklmnopqrstuvwxyz";
				String number = "0123456789";
				String IFSC = "MYBANK0000";
				
				for(int i=10000;i<=15000;i++) {
					
					String name = "";
					for(int j=0;j<Math.abs(rand.nextInt())%25+2;j++) {
						name+=alph.charAt(Math.abs(rand.nextInt())%26);
					}
					name=name.substring(0,1).toUpperCase()+name.substring(1);
					
					
					String password = null;
					try {
						password = encrip.encrypt(Integer.toString(i), name);
					} catch (Exception e) {
						e.printStackTrace();
					}
					// only encrypted data will be available to the admin.
					// so he will not understand the data. 
					//as each key will the user name
					
					String phoneNum = "9";
					for(int j=0;j<9;j++) {
						phoneNum+=number.charAt(Math.abs(rand.nextInt())%number.length());
					}
					
					myWriter.write(name+"\n");
					myWriter.write(i+"\n");
					myWriter.write(password+"\n");
					myWriter.write(Math.abs(rand.nextInt())%1000000+"\n");
					myWriter.write(phoneNum+"\n");
					myWriter.write(IFSC+""+i+"\n");
					myWriter.write("\n");
					
				}
				myWriter.close();
		    } catch (IOException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }
		
	}

}