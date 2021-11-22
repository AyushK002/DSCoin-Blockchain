package DSCoinPackage;

public class TransactionQueue {

  public Transaction firstTransaction;
  public Transaction lastTransaction;
  public int numTransactions = 0;

  public void AddTransactions (Transaction transaction) {
    if (firstTransaction == null){
      firstTransaction = transaction;
    }
    if (lastTransaction != null) {
      lastTransaction.next = transaction;
    }
    //transaction.next = null;
    lastTransaction = transaction;
    numTransactions += 1;
  }
  
  public Transaction RemoveTransaction () throws EmptyQueueException {
    Transaction temp = firstTransaction;
    if (firstTransaction == null) {
      EmptyQueueException e = new EmptyQueueException();
      throw e;
    }
    numTransactions -= 1;
    firstTransaction = firstTransaction.next;
    return temp;
  }

  public int size() {
    return numTransactions;
  }
}

