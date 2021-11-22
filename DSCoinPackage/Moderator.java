package DSCoinPackage;

import HelperClasses.Pair;
import java.util.*;

public class Moderator
{
  public void initializeDSCoin(DSCoin_Honest DSObj, int coinCount) throws EmptyQueueException {

    Members moder = new Members();
    moder.UID = "Moderator";
    
    long lastcoin = 99999;

    for (int i = 0; i < coinCount; i++) {
        Pair<String,TransactionBlock> newc = new Pair<>(String.valueOf(lastcoin+1) , null);
        lastcoin++;
        DSObj.latestCoinID = String.valueOf(lastcoin);

        moder.mycoins.add(newc);
        moder.initiateCoinsend(DSObj.memberlist[ i % DSObj.memberlist.length].UID , DSObj);
    }

    for (int i=0; i < coinCount/DSObj.bChain.tr_count ; i++) {
        HashMap<String, Boolean> tab = new HashMap<>(100);
        int j=0;
        Transaction[] arr = new Transaction[DSObj.bChain.tr_count];

        while( j < DSObj.bChain.tr_count ) {
            Transaction t = DSObj.pendingTransactions.RemoveTransaction();
            if (tab.get(t.coinID) == null) {
                tab.put(t.coinID,true);
                Pair<String,TransactionBlock> pd = new Pair<>(t.coinID , t.coinsrc_block);
                t.Destination.mycoins.add(pd);
                arr[j] = t;
                j++;
            }
            
        }


        TransactionBlock tb = new TransactionBlock(arr);
        DSObj.bChain.InsertBlock_Honest(tb);
    }
}
    
  public void initializeDSCoin(DSCoin_Malicious DSObj, int coinCount) throws EmptyQueueException {
    
    Members moder = new Members();
    moder.UID = "Moderator";
    
    long lastcoin = 99999;

    for (int i = 0; i < coinCount; i++) {
        Pair<String,TransactionBlock> newc = new Pair<>(String.valueOf(lastcoin+1) , null);
        lastcoin++;
        DSObj.latestCoinID = String.valueOf(lastcoin);

        moder.mycoins.add(newc);
        moder.initiateCoinsend(DSObj.memberlist[ i % DSObj.memberlist.length].UID , DSObj);
    }

    for (int i=0; i < coinCount/DSObj.bChain.tr_count ; i++) {
        HashMap<String, Boolean> tab = new HashMap<>(100);
        int j=0;
        Transaction[] arr = new Transaction[DSObj.bChain.tr_count];

        while( j < DSObj.bChain.tr_count ) {
            Transaction t = DSObj.pendingTransactions.RemoveTransaction();
            if (tab.get(t.coinID) == null) {
                tab.put(t.coinID,true);
                Pair<String,TransactionBlock> pd = new Pair<>(t.coinID , t.coinsrc_block);
                t.Destination.mycoins.add(pd);
                arr[j] = t;
                j++;
            }
            
        }


        TransactionBlock tb = new TransactionBlock(arr);
        DSObj.bChain.InsertBlock_Malicious(tb);
    }
  }
}
