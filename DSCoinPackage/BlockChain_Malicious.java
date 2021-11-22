package DSCoinPackage;

import HelperClasses.*;

public class BlockChain_Malicious {

	public int tr_count = 4;
	public static final String start_string = "DSCoin";
	public TransactionBlock[] lastBlocksList = new TransactionBlock[100];

	public static boolean checkTransactionBlock(TransactionBlock tB) {

// ---------------dgst check---------------------
		CRF c = new CRF(64);

		if (!tB.dgst.substring(0, 4).equals("0000")) {
			return false;
		}

		if (tB.previous == null) {
			if (!tB.dgst.equals(c.Fn(start_string + "#" + tB.trsummary + "#" + tB.nonce))) {
				return false;
			}
		} else {
			if (!tB.dgst.equals(c.Fn(tB.previous.dgst + "#" + tB.trsummary + "#" + tB.nonce))) {
				return false;
			}
		}
// --------------trsummary check-------------------
		MerkleTree tchk = new MerkleTree();
		String tsummchk = tchk.Build(tB.trarray);

		if ( !tB.trsummary.equals(tsummchk)) {
			return false;
		}

// -------------transactions validation------------

		for (int i = 0; i < tB.trarray.length; i++) {
			if ( !tB.checkTransaction(tB.trarray[i]) ) {
				return false;
			}
		}
		return true;
	}

	public TransactionBlock FindLongestValidChain() {
		Transaction[] z = new Transaction[1];
		TransactionBlock temp , tocheck, res = new TransactionBlock(z);
		int longestchainlength = 0 , x;

		for (int i = 0; i < lastBlocksList.length; i++) {
			x = 0;
			tocheck = lastBlocksList[i];
			res = temp = lastBlocksList[i];
			while( tocheck != null ) {
				if ( checkTransactionBlock(tocheck) ){
					x++;
				} else {
					x = 0;
					temp = tocheck.previous;
				}
				tocheck = tocheck.previous;
			}

			if ( x > longestchainlength) {
				res = temp;
				longestchainlength = x;
			}
		}
		
		return res;
	}

	public void InsertBlock_Malicious(TransactionBlock newBlock) {
		
		TransactionBlock q = FindLongestValidChain();

		newBlock.previous = q;
		newBlock.compute_nonce(start_string);
		
		int i = 0, totalTr = 0;
		
		while ( (lastBlocksList[i] != null) && (i < lastBlocksList.length)) {
			if (newBlock.previous == lastBlocksList[i]) {
				lastBlocksList[i] = newBlock;
				return;
			}
			i++;
		}
		if (i == 100) { i--;} // This might not happen, but just to evade indexoutofboundexception...
		lastBlocksList[i] = newBlock;

	}
}
