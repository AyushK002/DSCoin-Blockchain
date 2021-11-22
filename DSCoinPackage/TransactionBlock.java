package DSCoinPackage;

import HelperClasses.*;

public class TransactionBlock {

	public Transaction[] trarray;
	public TransactionBlock previous;
	public MerkleTree Tree;
	public String trsummary;
	public String nonce;
	public String dgst;

	CRF c = new CRF(64);
	

	public void compute_nonce( String startstr ) {
		long i = 1000000001;
		String temp;

		if (previous == null) {
			while (i / 10000 < 1000000) {
				temp = c.Fn(startstr + "#" + trsummary + "#" + String.valueOf(i));

				if (temp.substring(0, 4).equals("0000")) {
					dgst = temp;
					nonce = String.valueOf(i);
					return;
				} else {
					i++;
				}
			}
		} else {
			while (i / 10000 < 1000000) {
				temp = c.Fn(previous.dgst + "#" + trsummary + "#" + String.valueOf(i));

				if (temp.substring(0, 4).equals("0000")) {
					dgst = temp;
					nonce = String.valueOf(i);
					return;
				} else {
					i++;
				}
			}
		}
	}

	TransactionBlock() {
		Tree = null;
		trarray = new Transaction[0];
		previous = null;
		trsummary = null;
		dgst = null;
	}

	TransactionBlock(Transaction[] t) {

		Tree = new MerkleTree();

		trarray = new Transaction[t.length];

		for (int i = 0; i < t.length; i++) {
			trarray[i] = t[i];
		}
		previous = null;
		trsummary = Tree.Build(trarray);
		dgst = null;
	}

	public boolean checkTransaction(Transaction t) {
		TransactionBlock tb = previous;

    	if (t.coinsrc_block == null) { return true; }

		while (tb != t.coinsrc_block) {
			if (tb == null) { return false; }   // This means that t.coinsrc_block wasn't found in the transaction block, which should not happen in a correct transaction.
			for (int i = 0; i < tb.trarray.length; i++) {
				if (tb.trarray[i].coinID.equals(t.coinID)) {
					return false;
				}
			}
			tb = tb.previous;
		}

		for (int i = 0; i < tb.trarray.length; i++) {
			if (tb.trarray[i].coinID.equals(t.coinID)) {
				if (tb.trarray[i].Destination == t.Source) {
					return true;
				}
			}
		}

		return false;
	}

	public Pair<Boolean,Integer> contains(Transaction t) {
		Pair<Boolean,Integer> p = new Pair<Boolean,Integer>(false,0);
		if (trarray == null) {
			return p;
		}
		for (int i = 0; i<trarray.length; i++) {
			if (trarray[i].coinID.equals(t.coinID)) {
				p.first = true;
				p.second = i;
				return p;
			}
		}
		return p;
	}
}
