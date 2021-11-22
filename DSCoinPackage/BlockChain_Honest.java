package DSCoinPackage;

import HelperClasses.CRF;

public class BlockChain_Honest {

  public int tr_count = 4;
  public static final String start_string = "DSCoin";
  public TransactionBlock lastBlock;

  public void InsertBlock_Honest (TransactionBlock newBlock) {
    
    newBlock.previous = lastBlock;

    newBlock.compute_nonce(start_string);   // The computation of nonce and dgst for newBlock has been done using this attribute, which is described in the TransactionBlock file.

    lastBlock = newBlock;
  }
}
