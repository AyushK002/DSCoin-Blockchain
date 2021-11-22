package DSCoinPackage;

import java.util.*;
import HelperClasses.*;

public class Members {

   public String UID;
   public List<Pair<String, TransactionBlock>> mycoins = new ArrayList<Pair<String, TransactionBlock>>();
   public Transaction[] in_process_trans = new Transaction[100];

   public void initiateCoinsend(String destUID, DSCoin_Honest DSobj) {
      Pair<String, TransactionBlock> coin = mycoins.get(0);
      mycoins.remove(0);

      Transaction tobj = new Transaction();
      tobj.coinID = coin.first;
      tobj.coinsrc_block = coin.second;
      tobj.Source = this;

      for (int i = 0; i < DSobj.memberlist.length; i++) {
         if (DSobj.memberlist[i].UID.equals(destUID) ) {
            tobj.Destination = DSobj.memberlist[i];
            break;
         }
      }

      DSobj.pendingTransactions.AddTransactions(tobj);
      int i = 0;
      while(in_process_trans[i] != null) { i++; }
      if(i == 100) {i--;}
      in_process_trans[i] = tobj;

   }

   public void initiateCoinsend(String destUID, DSCoin_Malicious DSobj) {
      Pair<String, TransactionBlock> coin = mycoins.get(0);
      mycoins.remove(0);

      Transaction tobj = new Transaction();
      tobj.coinID = coin.first;
      tobj.coinsrc_block = coin.second;
      tobj.Source = this;

      for (int i = 0; i < DSobj.memberlist.length; i++) {
         if (DSobj.memberlist[i].UID.equals(destUID) ) {
            tobj.Destination = DSobj.memberlist[i];
            break;
         }
      }

      DSobj.pendingTransactions.AddTransactions(tobj);
      int i = 0;
      while(in_process_trans[i] != null) { i++; }
      if(i == 100) {i--;}
      in_process_trans[i] = tobj;

   }

   public Pair<List<Pair<String, String>>, List<Pair<String, String>>> finalizeCoinsend(Transaction tobj, 
   DSCoin_Honest DSObj) throws MissingTransactionException {

      TransactionBlock tb = DSObj.bChain.lastBlock;
      while (!(tb.contains(tobj).first)) {
         if (tb == null) {
            MissingTransactionException e = new MissingTransactionException();
            throw e;
         } else {
            tb = tb.previous;
         }
      }

      // Now we compute the sibling coupled path to root : Path ---------------

      int ind = (tb.Tree.numdocs)/2;
      int doc_idx = tb.contains(tobj).second;
      int d = (int)(StrictMath.log10((double)tb.Tree.numdocs) / StrictMath.log10(2.0d));

		List<Pair<String,String>> path = new ArrayList<Pair<String,String>>();
		Pair<String, String> p0 = new Pair<String,String>(tb.Tree.rootnode.val , null);
		path.add(p0);
		TreeNode currnode;
		currnode = tb.Tree.rootnode;
		for (int i = 1; i < d+1; i++) {
			Pair<String,String> p = new Pair<String, String>(currnode.left.val , currnode.right.val);
			path.add( p );
			if(doc_idx <= ind) {
				currnode = currnode.left;
			} else {
				currnode = currnode.right;
				doc_idx -= ind;
			}
			ind = ind/2;
		}
		Collections.reverse(path);
      // Path successfully computed.
      // Now we move to the second part of the proof that needs to be returned :

      List<Pair<String,String>> proof = new ArrayList<Pair<String,String>>();

      Pair<String, String> q0 = new Pair<String, String>(tb.previous.dgst , null);
      proof.add(q0);
      TransactionBlock q = tb;
      while (q != null) {
         Pair<String, String> x = new Pair<>(q.dgst, null);
         if (q.previous != null) {
            x.second = q.previous.dgst + "#" + q.trsummary + "#" + q.nonce;
         } else {
            x.second = DSObj.bChain.start_string + "#" + q.trsummary + "#" + q.nonce;
         }
         proof.add(x);

         q = q.previous;
      }

      // Removing the transaction from in_process_trans, and adding this coin to Destination's mycoins list :
      int i = 0;
      while (in_process_trans[i] != tobj) { i++; }
      in_process_trans[i] = null;
      Pair<String,TransactionBlock> c = new Pair<>(tobj.coinID , tobj.coinsrc_block);
      tobj.Destination.mycoins.add(c);

      Pair<List<Pair<String, String>>, List<Pair<String, String>>> res = new Pair<List<Pair<String, String>>, List<Pair<String, String>>>(path, proof);
      return res;
   }

   public void MineCoin(DSCoin_Honest DSObj) throws EmptyQueueException {

      HashMap<String, Boolean> tab = new HashMap<>(100);
      int j=0;
      Transaction[] arr = new Transaction[DSObj.bChain.tr_count];
      TransactionBlock ttemp = new TransactionBlock();
      ttemp.previous = DSObj.bChain.lastBlock;

      while( j < DSObj.bChain.tr_count-1 ) {
         Transaction t = DSObj.pendingTransactions.RemoveTransaction();
         if ( (tab.get(t.coinID) == null) && ttemp.checkTransaction(t)) {
            tab.put(t.coinID,true);
            Pair<String,TransactionBlock> pd = new Pair<>(t.coinID , t.coinsrc_block);
            t.Destination.mycoins.add(pd);
            arr[j] = t;
            j++;
         }
      }

      // Now we finally add the miner reward transaction.
      Transaction minerReward = new Transaction();
      DSObj.latestCoinID = minerReward.coinID = String.valueOf(Integer.parseInt(DSObj.latestCoinID) + 1);
      minerReward.Source = null;
      minerReward.Destination = this;
      minerReward.coinsrc_block = null;

      arr[j] = minerReward;

      // Finally, insert the block.
      TransactionBlock tb = new TransactionBlock(arr);
      DSObj.bChain.InsertBlock_Honest(tb);
      Pair<String,TransactionBlock> n = new Pair<>(minerReward.coinID , tb);
      mycoins.add(n);
   }

   public void MineCoin(DSCoin_Malicious DSObj) throws EmptyQueueException {

      
      HashMap<String, Boolean> tab = new HashMap<>(100);
      int j=0;
      Transaction[] arr = new Transaction[DSObj.bChain.tr_count];
      TransactionBlock ttemp = new TransactionBlock();
      ttemp.previous = DSObj.bChain.FindLongestValidChain();

      while( j < DSObj.bChain.tr_count-1 ) {
         Transaction t = DSObj.pendingTransactions.RemoveTransaction();
         if ( (tab.get(t.coinID) == null) && ttemp.checkTransaction(t)) {
            tab.put(t.coinID,true);
            arr[j] = t;
            j++;
         }
      }

      // Now we finally add the miner reward transaction.
      Transaction minerReward = new Transaction();
      DSObj.latestCoinID = minerReward.coinID = String.valueOf(Integer.parseInt(DSObj.latestCoinID) + 1);
      minerReward.Source = null;
      minerReward.Destination = this;
      minerReward.coinsrc_block = null;

      arr[j] = minerReward;

      // Finally, insert the block.
      TransactionBlock tb = new TransactionBlock(arr);
      DSObj.bChain.InsertBlock_Malicious(tb);
      Pair<String,TransactionBlock> n = new Pair<>(minerReward.coinID , tb);
      mycoins.add(n);
   }
}
