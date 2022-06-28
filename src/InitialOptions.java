import com.mysql.cj.protocol.Resultset;

import java.util.*;
import java.sql.*;

class InitialOptions { //To do various operations like login nd signup

    private static InitialOptions ioObject;

    private InitialOptions(){}

    public static InitialOptions getInstance(){
        if( ioObject == null) {
            ioObject = new InitialOptions();
    }

    // returns the singleton object
       return  ioObject;}
    public static Connection ConnectDB(){
        try{

            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/giftcardsystem","anir12","ArtDLLM1122@");
            Statement stmt = con.createStatement();

            String[] sql={"CREATE TABLE IF NOT EXISTS Account_Details" +
                    "(AccountNo INTEGER(10) Primary Key , " +
                    " Balance DOUBLE) " ,"CREATE TABLE IF NOT EXISTS CUSTOMER_Details" +
                    "(CustId INTEGER(4) Primary Key , " +
                    " CustomerName VARCHAR(20), " +
                    " ENCRYPTEDPASSWORD VARCHAR(15)) ",
                    "CREATE TABLE IF NOT EXISTS Customer_Account" +
                            "(CustId INTEGER(4), " +
                            " AccountNo INTEGER(10) ,"+
                            " PRIMARY KEY(CustId, AccountNo),"+
                            "FOREIGN KEY (CustId) REFERENCES Customer_Details(CustId),"+
                            "FOREIGN KEY (AccountNo) REFERENCES Account_Details(AccountNo))",
                    "CREATE TABLE IF NOT EXISTS GiftCard" +
                            "(CardNo DECIMAL(5) , "+
                            "CardPin DECIMAL(4) , "+
                            "CardAmount DOUBLE ,"+
                            "RewardPoints INTEGER(2), "+
                            "PRIMARY KEY(CardNo),"+
                            " STATUS VARCHAR(7))",
                    "CREATE TABLE IF NOT EXISTS GiftCard_AccountNo"+
                            "(AccountNo INTEGER(10) ," +
                            "CardNo DECIMAL(5) PRIMARY KEY, " +
                            "FOREIGN KEY(AccountNo) REFERENCES account_details(AccountNo),"+
                            "FOREIGN KEY(CardNo) REFERENCES GiftCard(CardNo))",
                    "CREATE TABLE IF NOT EXISTS TransactionHistory" +
                            "(Trans_id VARCHAR(10) PRIMARY KEY,"+
                            "TransactionType VARCHAR(10),"+
                            "TransactionAmount DOUBLE)",
                    "CREATE TABLE IF NOT EXISTS CardNoTransactions" +
                            "(CardNo DECIMAL(5) , " +
                            " Trans_id VARCHAR(10) PRIMARY KEY,"+
                            "FOREIGN KEY (CardNo) REFERENCES GiftCard(CardNo),"+
                            "FOREIGN KEY (Trans_id) REFERENCES  TransactionHistory(Trans_id))",
                    "CREATE TABLE IF NOT EXISTS Reward"+
                            "(Purchase_id VARCHAR(10) ,"+
                            "PurchaseAmount DOUBLE,"+
                            "PurchasePoints INTEGER(2),"+
                            "PRIMARY KEY(Purchase_id),"+
                            "FOREIGN KEY(Purchase_id) REFERENCES TransactionHistory(Trans_id))"


            };

            for(String s:sql)
            {
                stmt.executeUpdate(s);
            }
            return con;
        }
        catch(SQLException e){
            return null;
        }
    }

    Connection con = ConnectDB();
    String sql;
    PreparedStatement stmt;

    int initialChoice=0,choiceAfterLogin=0,i;long accountNo;//i for loop traversal,count to count no of digits in anumber
    Customer cust;//To add inputted elements to the class
    boolean validName,validPassword,validPurchase,invalidType,cardNoExist,cardPinExist,validDigits=false,cardIsBlocked;
    HashMap<Long, Customer> accounts =new HashMap<>();// To store customer details mapping to their accountNo
    ArrayList<Long> accountNos=new ArrayList<>();
    Customer account;
    Iterator<Map.Entry<Long, Customer> > iterator ; //Traversing through map
    Iterator<Long> iterator1;
    int topUpCardNo=0,blockCardNo=0,displayCardNo=0,attempt;//attempt is for no of attempts to enter password
    HashMap<Integer,Boolean> cards=new HashMap<>();//To store cardNo generated and check everytime while generating another cardNo to avoid duplication and boolean is to know if it is blocked
    String password;//To input password
    double amount=0;//amount is the amount in customer account
    char YorN;
    Scanner sc=new Scanner(System.in);
    String menu1="\nEnter \n 1.Signup \n 2.Login\n 3.Purchase\n 4.Exit",menu2="  --------**Menu**--------  \n 1.Create a new GiftCard \n 2.Top-up the existing Card \n 3.Show Gift Card Transaction History \n 4.Block existing Card  \n 5.LogOut ";

    void getDoubleValue(double num) //Checking for -ve value
    {   try {
        if (num < 0) {
            throw new NumberRangeException("");
        }
        else if (num==0){
            System.out.println("0 not allowed");
        }
    }catch (NumberRangeException e1){System.out.println("Negative Values Not allowed");}
    }
    double getDoubleValue(String s) //Checking for -ve value
    {   int num=-1;
        do { // To make sure choice is input and in positive
            invalidType = false;
            try {
                System.out.print(s);num = sc.nextInt();
                if (num < 0) {
                    throw new NumberRangeException("");
                } else if (num == 0) {
                    System.out.println("0 not allowed");
                }

            } catch (NumberRangeException e1) {
                System.out.println("Negative Values Not allowed");
            } catch (InputMismatchException e) {
                System.out.println("Numbers only allowed ");
                invalidType = true;
            }
            sc.nextLine(); // clears the buffer
        }while(num<=0||invalidType);
        return num;
    }
    boolean getDigitCount(int num, int digitNos) //To check if there is required no of digits
    {
        try {

                int count = 0;
                while (num != 0) {
                    num /= 10;
                    ++count;
                }
                if (count != digitNos)// To check if count is 5 digit
                { throw new NumberRangeException("To be "+digitNos+" digits");}
                else
                    return true;

        }catch (NumberRangeException e1){}
        return false;

    }

    int getIntegerValue(String s) // Checking for -ve value
    {   int num=-1;
        do { // To make sure choice is input and in positive
            invalidType = false;
            try {
                System.out.print(s);num = sc.nextInt();
                if (num < 0) {
                    throw new NumberRangeException("");
                } else if (num == 0) {
                    System.out.println("0 not allowed");
                }

            } catch (NumberRangeException e1) {
                System.out.println("Negative Values Not allowed");
            } catch (InputMismatchException e) {
                System.out.println("Numbers only allowed ");
                invalidType = true;
            }
            sc.nextLine(); // clears the buffer
        }while(num<=0||invalidType);
        return num;
    }
    char checkCharacterValue()// For YorN Yes or No purpose
    {
        String s = "temp";char ch='a';
        while (s.length() > 1) {// To make sure you enter a single character
            System.out.print("\n Enter Y to try again and N to exit:");
            try {
                s = sc.nextLine();
                ch=s.charAt(0);
                if (s.length() > 1) {
                    throw new RuntimeException(" Input should be a single character!\n");


                } else {

                    if (ch != 'Y' && ch != 'y' && ch != 'N' && ch != 'n')
                        System.out.println(" Enter either Y or N");


                }


            } catch (RuntimeException re) {
                System.out.print(re.getMessage());
                // you can break the loop or try again
            }
        }
        return ch;
    }
    void signUp()
    {
        String name;

        validName=false;
        do {// Checking the Name is in specified format
            System.out.print("\n Enter Name(Alphabets,Spacing and - allowed):");name = sc.nextLine();
            if(name.matches("[a-zA-Z-\\s+]+"))
            {validName=true;}
            else
            {System.out.println("Enter correctly");}
        }while(!validName);
        validPassword=false;
        do { // Checking the Password in specified format
            System.out.print(" Enter Password(Only alphabets and digits allowed):");
            password = sc.nextLine();
            if(password.length()<5|| password.length()>15)
            {System.out.println("Password to be in range from 5 to 15");}
            else if( password.matches("[a-zA-Z0-9]+"))
            {validPassword=true;}
            else
            {System.out.println("Enter correctly");}
        }while(!validPassword);
        do { //To check that we type digits and not use -ve value
            try {
                invalidType=false;
                System.out.print(" Enter Amount:");
                amount = sc.nextDouble();
                if(amount!=0)
                    getDoubleValue(amount);
            }catch (InputMismatchException e) {
                System.out.println("Invalid (Only numbers allowed) ");
                invalidType=true;

            }

            sc.nextLine(); // clears the buffer
        } while (amount <0||invalidType);
        try{
            int no=0;
            sql="Select MAX(CustId) FROM customer_details";
            stmt=con.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                no = rs.getInt(1);
            }
            Customer.setNo(no+1);
            cust=new Customer(name,amount, password);//Adding to customer class
            long accountNoSignUp=cust.getAccountNo();
            accounts.put(accountNoSignUp,cust); // Adding customer details to accounts hashmap
            System.out.println(" --*Successfully added Account!!!*--");
            System.out.println("\n --*Customer Account Details*--" +
                    "\n  Customer Id    :" + accounts.get(accountNoSignUp).getCustId() +
                    "\n  Account No     :" + accounts.get(accountNoSignUp).getAccountNo() +
                    "\n  Password       :" + accounts.get(accountNoSignUp).getPassword() +
                    "\n  Name           :" + accounts.get(accountNoSignUp).getName() +
                    "\n  Account Balance:" + accounts.get(accountNoSignUp).getBalance());
            sql = "insert into customer_details values("+accounts.get(accountNoSignUp).getCustId()+",'"+accounts.get(accountNoSignUp).getName() +"','"+accounts.get(accountNoSignUp).getEncryptedPassword()+"')";
            stmt = con.prepareStatement(sql);
            stmt.executeUpdate(sql);

            sql = "insert into account_details values("+accounts.get(accountNoSignUp).getAccountNo()+","+accounts.get(accountNoSignUp).getBalance()+")";
            stmt.executeUpdate(sql);

            sql = "insert into customer_account values("+accounts.get(accountNoSignUp).getCustId()+","+accounts.get(accountNoSignUp).getAccountNo()+")";
            stmt.executeUpdate(sql);
            accounts.remove(accountNoSignUp);


    }catch (Exception e) {
        e.printStackTrace();
    }
        accounts.clear();

    }

    void displayCards(Set<Integer> cardNos) {// To show the available cards

        System.out.print(" The cards are:");
        cardNos.forEach((b) -> {
            System.out.print(b + ",");
        });
        System.out.print("\b.");
    }

     void login(){
         int custId=0;
         YorN='y';
         do {

             custId = getIntegerValue("\n Enter your Id:");
             String samp = Integer.toString(custId);
             samp = samp + "0";
             samp += samp;
             accountNo = Long.parseLong(samp);
             try{

                 sql=" Select customer_account.custId,customer_account.accountNo,customerName,encryptedPassword,Balance From " +
                         "((customer_details INNER JOIN customer_account ON customer_details.custid=customer_account.custid)" +
                         "INNER JOIN account_details ON customer_account.accountno=account_details.accountno)";
                 stmt=con.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery();
                 while(rs.next()) {
                     cust=new Customer(rs.getInt(1),(long)rs.getInt(2),rs.getString(3),rs.getString(4),rs.getDouble(5));
                     accounts.put((long)rs.getInt(2),cust);
                 }
             }
             catch(SQLException e)
             {e.printStackTrace();}
             if (accounts.keySet().contains(accountNo)) {
                 YorN='n';//To exit the last loop in this case
                 iterator = accounts.entrySet().iterator();
                 while (iterator.hasNext())// Iterating to check if account exist
                 {
                     Map.Entry<Long, Customer> entry = iterator.next();
                     if (accountNo == entry.getKey()) {
                         for(attempt=0;attempt <= 3;attempt++) {

                             System.out.print("Enter Password:");
                             password = sc.nextLine();
                             String str = entry.getValue().getPassword();
                             if (str.equals(password)) {
                                 attempt = 4;// To get out of that loop
                                 System.out.println(" --*Successfully LoggedIn AccountNo:" + accountNo + "*--");
                                 do {


                                     System.out.print(menu2);
                                     choiceAfterLogin = getIntegerValue("\nEnter choice:");
                                     switch (choiceAfterLogin) {
                                         case 1://Create New GiftCard
                                             Random r = new Random();// To generate random number

                                             if(accounts.get(accountNo).getBalance()==0)
                                             {System.out.println("No amount is there in balance");}
                                             else {
                                                 try {
                                                     sql = "select giftcard_accountno.cardno,STATUS from giftcard_accountno,giftcard where accountno=" + accountNo;
                                                     ResultSet rs=stmt.executeQuery(sql);
                                                     while(rs.next())
                                                     {  boolean status;
                                                         if(rs.getString(2).equals("ACTIVE"))
                                                             status=true;
                                                         else
                                                             status=false;

                                                         cards.put(rs.getInt(1),status);
                                                     }
                                                 }catch(SQLException e)
                                                 {e.printStackTrace();}
                                                 int giftCardNo;
                                                 do { //To make sure one copy of card no is there
                                                     cardNoExist = false;
                                                     giftCardNo = (10000 + r.nextInt(90000));//Make sures in 5 digit
                                                     if (cards.keySet().contains(giftCardNo)) {// Checks if card no is there
                                                         cardNoExist = true;
                                                     } else cards.put(giftCardNo,true);
                                                 } while (cardNoExist);
                                                 int giftCardPin = (1000 + r.nextInt(9000));
                                                 double newAmount = 0;// Amount to be present in giftcard
                                                 do {// Check that input is number and +ve
                                                     newAmount = getDoubleValue("\n Available amount is Rs." + accounts.get(accountNo).getBalance() + "\n Enter amount to add :");

                                                     if (newAmount > accounts.get(accountNo).getBalance())
                                                         System.out.println("That much amount is not available.");

                                                 } while (newAmount > accounts.get(accountNo).getBalance());
                                                 GiftCard g = new GiftCard(giftCardNo, giftCardPin, newAmount);

                                                 accounts.get(accountNo).removeBalance(newAmount);// Remove the amount added to card from customer balance
                                                 System.out.println("\n --*Added Successfully*--\n  1.Card No :" + giftCardNo +
                                                         "\n  2.Card Pin:" + giftCardPin);
                                                 try {
                                                     String initialId="I"+giftCardNo;
                                                     sql = "insert into giftcard values("+giftCardNo+","+giftCardPin+","+newAmount+","+0+",'ACTIVE')";
                                                     stmt.executeUpdate(sql);
                                                     sql = "insert into GiftCard_AccountNo values(" + accountNo + "," + giftCardNo + ")";
                                                     stmt = con.prepareStatement(sql);
                                                     stmt.executeUpdate(sql);
                                                     sql = "insert into transactionhistory values('"+initialId+"','INITIAL',"+newAmount+")";
                                                     stmt.executeUpdate(sql);
                                                     sql = "insert into cardnotransactions values("+giftCardNo+",'"+initialId+"')";
                                                     stmt.executeUpdate(sql);
                                                     sql = "update account_details SET balance=balance-"+newAmount+" Where accountno="+accountNo;
                                                     stmt.executeUpdate(sql);

                                                 }catch(SQLException e)
                                                 {e.printStackTrace();}
                                                 cards.clear();
                                             }
                                             break;
                                         case 2://Top-up Existing Card

                                             double topUpAmount = 0;

                                             try {
                                                 sql = "select giftcard_accountno.cardno,STATUS from giftcard_accountno inner join giftcard where accountno=" + accountNo;
                                                 stmt=con.prepareStatement(sql);
                                                 ResultSet rs=stmt.executeQuery();
                                                 while(rs.next())
                                                 {  boolean status;
                                                     if(rs.getString(2).equals("ACTIVE"))
                                                         status=true;
                                                     else
                                                         status=false;

                                                     cards.put(rs.getInt(1),status);
                                                 }
                                             }catch(SQLException e)
                                             {e.printStackTrace();}
                                             do {
                                                 displayCards(cards.keySet());
                                                 topUpCardNo = getIntegerValue("\n Enter cardNo to topUp:");
                                                 validDigits= getDigitCount(topUpCardNo,5);

                                             } while (!validDigits  );
                                             if(cards.keySet().contains(topUpCardNo)) {
                                                 if(cards.get(topUpCardNo))

                                                 {
                                                    do{
                                                     try {
                                                         invalidType = false;
                                                         System.out.print("\n Available amount is Rs." + accounts.get(accountNo).getBalance() + "\n Enter amount to topUp:");
                                                         topUpAmount = sc.nextDouble();

                                                         if (topUpAmount <= 0) {
                                                             throw new NumberRangeException("Negative values not allowed");
                                                         } else if (topUpAmount > accounts.get(accountNo).getBalance()) {
                                                             throw new NumberRangeException("Amount not present");
                                                         }

                                                     } catch (InputMismatchException e) {
                                                         System.out.println("Invalid (Only numbers allowed) ");
                                                         invalidType = true;

                                                     } catch (NumberRangeException e1) {
                                                     }
                                                     sc.nextLine(); // clears the buffer
                                                 } while (topUpAmount <= 0 || topUpAmount > accounts.get(accountNo).getBalance() || invalidType);
                                                     try{
                                                         int topUpNo=0;
                                                         sql="Select COUNT(trans_id) FROM cardNoTransactions where (cardno="+topUpCardNo+") AND (trans_id LIKE 'T%')";
                                                         stmt=con.prepareStatement(sql);
                                                         ResultSet rs = stmt.executeQuery();
                                                         while(rs.next()) {
                                                             topUpNo = rs.getInt(1);
                                                         }
                                                         Transaction.setTopUpNo(topUpNo);
                                                         String topUpId="T"+topUpCardNo+String.format("%04d",Transaction.getTopUpNo());
                                                         sql = "update GiftCard SET CardAmount=CardAmount+"+topUpAmount+" Where CardNo="+topUpCardNo;
                                                         stmt = con.prepareStatement(sql);
                                                         stmt.executeUpdate(sql);
                                                         sql = "insert into transactionhistory values('"+topUpId+"','TOPUP',"+topUpAmount+")";
                                                         stmt.executeUpdate(sql);
                                                         sql = "insert into cardnotransactions values("+topUpCardNo+",'"+topUpId+"')";
                                                         stmt.executeUpdate(sql);
                                                         sql = "update account_details SET balance=balance-"+topUpAmount+" Where accountno="+accountNo;
                                                         stmt.executeUpdate(sql);
                                                         stmt=con.prepareStatement("select cardamount from giftcard where cardno="+topUpCardNo);
                                                         rs=stmt.executeQuery();
                                                         while(rs.next())
                                                         {
                                                              amount=rs.getDouble(1);
                                                         }

                                                     }catch(SQLException e)
                                                     {e.printStackTrace();}
                                                     System.out.println(" --*Successfully added and now the current gift card amount:" + amount + "*--");
                                                 }
                                                 else
                                                     System.out.println("Card is blocked");

                                             }
                                             else System.out.println("Card does not exist");
                                             cards.clear();
                                             break;
                                         case 3://Display

                                             try {
                                                 sql = "select cardno from giftcard_accountno  where accountno=" + accountNo;
                                                 ResultSet rs=stmt.executeQuery(sql);
                                                 while(rs.next())
                                                 {
                                                     cards.put(rs.getInt(1),true);


                                                 }
                                             }catch(SQLException e)
                                             {e.printStackTrace();}

                                             do {
                                                 displayCards(cards.keySet());

                                                 displayCardNo = getIntegerValue("\n Enter cardNo to display:");
                                                 validDigits= getDigitCount(displayCardNo,5);
                                             } while ( !validDigits);
                                             ArrayList<Transaction>  transactions = new ArrayList<>();
                                             if(cards.keySet().contains(displayCardNo)) {
                                                try{
                                                 sql="select transactiontype,transactionamount from transactionhistory,cardnotransactions where " +
                                                         "transactionhistory.trans_id=cardnotransactions.trans_id and cardnotransactions.cardno="+displayCardNo;
                                                 stmt=con.prepareStatement(sql);
                                                 ResultSet rs=stmt.executeQuery();

                                                 while(rs.next())
                                                 {
                                                     TransactionType transactionType;
                                                     if(rs.getString(1).equals("TOPUP"))
                                                         transactionType=TransactionType.Credited;
                                                     else if(rs.getString(1).equals("PURCHASE"))
                                                         transactionType=TransactionType.Debited;
                                                     else
                                                         transactionType=TransactionType.Initial;
                                                  transactions.add(new Transaction(rs.getDouble(2),transactionType));
                                                 }
                                             }catch(SQLException e)
                                         {e.printStackTrace();}

                                                         System.out.println("\n --*Transaction History*--");

                                                         transactions.forEach((a) -> {
                                                             if(a.typeOfTransaction ==TransactionType.Credited)
                                                                 System.out.println("  Top-up of amount:"+a.amount);
                                                             else if(a.typeOfTransaction ==TransactionType.Debited)
                                                                 System.out.println("  Purchased amount:"+a.amount);
                                                             else
                                                                 System.out.println("    Initial amount:"+a.amount);



                                                         });

                                                         System.out.println("\n -----------***-----------" );


                                             }
                                             else System.out.println("Card does not exist");
                                             cards.clear();
                                             break;
                                         case 4://Blocking Card
                                             try {
                                                 sql = "select cardno from giftcard_accountno  where accountno=" + accountNo;
                                                 stmt=con.prepareStatement(sql);
                                                 ResultSet rs=stmt.executeQuery();
                                                 while(rs.next())
                                                 {
                                                     cards.put(rs.getInt(1),true);


                                                 }
                                             }catch(SQLException e)
                                             {e.printStackTrace();}
                                             do {
                                                 displayCards(cards.keySet());
                                                 blockCardNo = getIntegerValue("\n Enter cardNo to block:");
                                                 validDigits= getDigitCount(blockCardNo,5);

                                             } while ( !validDigits);
                                             if(cards.keySet().contains(blockCardNo)) {
                                                 try{
                                                     sql="select STATUS from giftcard where cardno="+blockCardNo;
                                                     ResultSet rs = stmt.executeQuery(sql);
                                                     while(rs.next())
                                                     {boolean status;
                                                         if(rs.getString(1).equals("ACTIVE"))
                                                             status=true;
                                                         else
                                                             status=false;
                                                         cards.put(blockCardNo,status);

                                                     }
                                                 }
                                                 catch(SQLException e)
                                                 {e.printStackTrace();}
                                                 if(cards.get(blockCardNo)) {
                                                     cardNoExist = true;
                                                     double blockedAmount=0;
                                                     try{
                                                         sql="select cardamount from giftcard where cardno="+blockCardNo;
                                                         ResultSet rs = stmt.executeQuery(sql);
                                                         while(rs.next())
                                                         {blockedAmount=rs.getDouble(1);
                                                         }
                                                     }
                                                     catch(SQLException e)
                                                     {e.printStackTrace();}
                                                             accounts.get(accountNo).addBalance(blockedAmount);// Add back the amount to customer balance
                                                             System.out.println("Amount added back to account successfully");
                                                             System.out.println("Current amount in account:" + accounts.get(accountNo).getBalance());

                                                             try {
                                                                 sql = "update account_details SET balance=balance+" + blockedAmount + " Where accountno=" + accountNo;
                                                                 stmt.executeUpdate(sql);
                                                                 sql = "update giftcard SET STATUS='BLOCKED' Where cardNo=" + blockCardNo;
                                                                 stmt.executeUpdate(sql);
                                                             }catch(SQLException e){
                                                                 e.printStackTrace();
                                                             }



                                                 }
                                                 else{
                                                     System.out.println("Card is already blocked");
                                                 }

                                             }
                                             else {
                                                 System.out.println("Card does not exist");
                                                 cardNoExist=false;
                                             }
                                             if(cardNoExist)
                                                 cards.put(blockCardNo,false);
                                             cards.clear();
                                             break;
                                         case 5:
                                             System.out.println(" --*Logged Out successfully*--");
                                             break;
                                         default:
                                             System.out.println("Wrong choice");


                                     }


                                 } while (choiceAfterLogin != 5);
                             } else if (attempt == 3) {
                                 System.out.println("Wrong Password(Attempts over)");
                             } else {
                                 System.out.println("Wrongly typed !!(" + (3 - attempt) + " attempts left)");

                             }
                         }

                     }
                 }
             } else {
                 System.out.println("Account does not exist");

                 do{
                     YorN= checkCharacterValue();
                 }while(YorN != 'Y' && YorN != 'y' && YorN != 'N' && YorN != 'n');
             }
         }while(YorN=='Y'||YorN=='y');
     }

    void purchase()
    {
        Customer temp;int index;
        int purchaseCardNo=0,purchaseCardPin=0;
        attempt=0;YorN='n';
        cardNoExist=false;cardPinExist=false;validPurchase=false;cardIsBlocked=true;// To check if amount is there and card no and pin matching
        do {
                try{
                    sql="select cardno,status from giftcard";
                    stmt=con.prepareStatement(sql);
                    ResultSet rs=stmt.executeQuery();
                    while(rs.next())
                    {
                        boolean status;
                        if(rs.getString(2).equals("ACTIVE"))
                            status=true;
                        else
                            status=false;
                        cards.put(rs.getInt(1),status);
                    }
                }
                catch(SQLException e){e.printStackTrace();}
                if(!cardNoExist)
                do {

                    purchaseCardNo = getIntegerValue("Enter your cardNo :");
                    validDigits= getDigitCount(purchaseCardNo,5);


                } while (!validDigits);



            Iterator<Integer> iteratorCardNo = cards.keySet().iterator();//Traversing to all accounts

            while (iteratorCardNo .hasNext())// Iterating to check if email exist
            {
                Integer cardNo = iteratorCardNo .next();

                if (cardNo== purchaseCardNo) {// Check for cardNo
                        cardNoExist = true;
                        if (cards.get(purchaseCardNo))
                        {   cardIsBlocked=false;
                            attempt=0;
                            int cardPin=0;
                            try{
                                sql="select cardpin from giftcard where cardNo="+purchaseCardNo;
                                ResultSet rs=stmt.executeQuery(sql);
                                while(rs.next())
                                {cardPin=rs.getInt(1);}
                            }
                            catch (SQLException e){e.printStackTrace();}
                            if(!cardPinExist)
                                do {
                                purchaseCardPin = getIntegerValue("Enter your cardPin:");
                                validDigits= getDigitCount(purchaseCardPin,4);

                                } while (!validDigits);
                            if (cardPin == purchaseCardPin) {//Check for pin
                                cardPinExist = true;
                                attempt=0;
                                double cardAmount=0;
                                int rp=0;
                                try{
                                    sql="select cardamount,rewardpoints from giftcard where cardNo="+purchaseCardNo;
                                    stmt=con.prepareStatement(sql);
                                    ResultSet rs=stmt.executeQuery();
                                    while(rs.next())
                                    {cardAmount=rs.getDouble(1);
                                    rp=rs.getInt(2);}
                                }
                                catch(SQLException e)
                                {e.printStackTrace();}
                                System.out.println("Amount present:" +cardAmount);
                                amount = getDoubleValue("Enter bill amount:");
                                if (cardAmount == 0) {
                                    System.out.println("No amount is present int the card!!(" + (3 - attempt++) + "attempts left)" );
                                } else if (cardAmount < amount) {
                                    System.out.println("That much amount not present!!\nAmount present" +
                                           cardAmount+ "(" + (3 - attempt++) + "attempts left)");
                                } else {

                                    validPurchase = true;
                                    attempt=0;

                                    try {
                                        int purcNo=0;
                                        sql="Select COUNT(trans_id) FROM cardNoTransactions where (cardno="+purchaseCardNo+")AND (trans_id like ?)";
                                        stmt=con.prepareStatement(sql);
                                        stmt.setString(1,"P%" );
                                        ResultSet rs = stmt.executeQuery();
                                        while(rs.next()) {
                                            purcNo = rs.getInt(1);
                                        }
                                        Transaction.setPurchaseNo(purcNo);
                                        String purchaseId="P"+purchaseCardNo+String.format("%04d",Transaction.getPurchaseNo());

                                        sql = "insert into transactionhistory values('" + purchaseId + "','PURCHASE'," + amount + ")";
                                        stmt.executeUpdate(sql);
                                        sql = "insert into cardnotransactions values("+purchaseCardNo+",'"+purchaseId+"')";
                                        stmt.executeUpdate(sql);
                                        double tempAmount=amount;
                                        int points=(int)(amount/100);
                                        if(amount>=100)// To check for reward points
                                            rp+=(int)(amount/100);

                                        if(rp>=10){ // Adds amount when points greater than 10
                                            tempAmount-=rp;
                                            rp=0;
                                        }

                                        sql = "insert into reward values('" + purchaseId + "'," + amount +","+points+ ")";
                                        stmt.executeUpdate(sql);
                                        sql = "update giftcard SET cardamount=cardamount-" +tempAmount  + ",rewardpoints=" +rp + " Where cardno=" + purchaseCardNo;
                                        stmt.executeUpdate(sql);
                                        sql="select cardamount,rewardpoints from giftcard where cardNo="+purchaseCardNo;
                                        rs=stmt.executeQuery(sql);
                                        while(rs.next())
                                        {amount=rs.getDouble(1);
                                        rp=rs.getInt(2);}
                                        double newCardAmount=cardAmount-amount;
                                        System.out.print("\n AvailableBalance:" + amount );
                                        System.out.print("\n Reward Points:" + rp);

                                    }catch(SQLException e)
                                    {e.printStackTrace();}




                                }

                            }
                        }

                    }




            }
            if(attempt==3)
            {
                System.out.println("(Attempts over)");
                attempt++;
            }
            else if(!cardNoExist)
            {
                System.out.println("CardNo Does not exist !!(" + (3 - attempt++) + "attempts left)");


            }
            else if(cardIsBlocked)
            {
                System.out.println("Card is Blocked !!(" + (3 - attempt++) + "attempts left)");
            }
            else if(!cardPinExist)
            {
                System.out.println("Card Pin is not matching D !!(" + (3 - attempt++) + "attempts left)");


            }
            else if(validPurchase)
            {
                cardNoExist=cardPinExist=validPurchase=false;
            }

            if(attempt<=3)
            do {
                YorN = checkCharacterValue();
            } while (YorN != 'Y' && YorN != 'y' && YorN != 'N' && YorN != 'n');


        }while(attempt<=3&&(YorN=='y'||YorN=='Y'));
        cards.clear();
    }


    void initialOptions(){

        do{// For 1st menu to login or signup or purchase

            System.out.print(menu1);
            initialChoice= getIntegerValue("\nEnter choice:");

            switch(initialChoice)
            {
                case 1://Signup
                    signUp();
                    break;

                case 2://Login and other activities
                    login();
                    break;
                case 3:
                    purchase();
                    break;
                case 4:System.out.println(" --*Exited the server*--");break;
                default:System.out.println("Wrong choice");

            }
    }while(initialChoice!=4);



}
}
