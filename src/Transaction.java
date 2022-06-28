class Transaction{// To store the history of Transaction(Amount and type of transaction)
    double amount;
    static int topUpNo=0,purchaseNo=0;// To give transactionIds
    TransactionType typeOfTransaction;
    Transaction(double amount, TransactionType typeOfTransaction)
    {
        this.amount=amount;
        this.typeOfTransaction =typeOfTransaction;
    }
    static int getTopUpNo()
    {return topUpNo+1;}
    static void setTopUpNo(int n){
        topUpNo=n;
    }
    static void setPurchaseNo(int n){
        purchaseNo=n;
    }
    static int getPurchaseNo()
    {return purchaseNo+1;}
}
