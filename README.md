INSTRUCTIONS TO USE-> 

	1. imported files->	
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
		import javax.crypto.Cipher;
		import javax.crypto.spec.SecretKeySpec;
	  For various things like Encription , file handling.

	2. Use of ArrayList data structure and Random function
	3. All the user account number starts from 10000 upto 15000
	   and can be added more .
	4. For now the pin was (AccountNumber).
	   (Taken Arbitrary (in real life user can decide and code also has an option to change it)).

	5. Made 9 classes and 1 interface.
	classes are
	namely -> ATM,BankDataBase,User,AtmOperation,CaseStudy2,EncryptionDecription,Transfer 
		FileGenertor,Admin.

	All of them contains the required methods 
	
	Interface -> ATM_Machine
	
	// for more details check comments provided in sorce code.

Path to code ->
	PM_CaseStudy2/src/CaseStudy2.java


Plan of use -> 

	Initially made a file of .txt format for BankDatBase which contains 
	all the important details for a user (Available in repo.) and then copying the data in an array list 
	everytime code is exicuted.

	##REASON -> fast access to different user, else we have to iterate in file which is both
	1. time consuming(less effictive), 2. More diffult to write and read.
	Update file method is always avaliable to change data in admin class and at the end too we call a by default update method
 	which updates the file and add required changes.

	password is Encrypted and the "Key for encryption" is the user's name.
	things like change in name will affect the user's encrypted pin(key changes)
	are already taken care of.

	Almost all the contraints and wrong inputs given by user are taken care of.

	user contains an array list consisting of account no. of his family members --> 

	you can only tranfer amount/withdraw amount from family member's account(because you have access to his account).
	
	while adding someone i am checking it to the user too.

	which is also a class (contains things like-> name, acc no. , pin, balance etc.)
	now the main method will also make ATM method which is basically our present ATM.
	ATM has it's own methods which also implements an interface called ATM_Machine
	as ATMs at different places can be implemented differently but all contains some basic operations.

	now we take all the detais in order mentioned in the question.



// Rest details can be checked in code.
//  Suggestions to improve code are always welcomed.
//  Thank you


  