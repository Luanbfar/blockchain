package javacoin;

import java.util.ArrayList;
import java.util.Date;

public class Block {
    private String hash;
    private String previousHash;
    private String merkleRoot;
    private long timeStamp;
    private int nonce;
    private ArrayList<Transaction> transactions;

    public Block(String previousHash) {
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash();
    }

    public String calculateHash() {
        String calculatedHash = StringUtil.applySha256(previousHash + Long.toString(timeStamp) + Integer.toString(nonce) + merkleRoot);
        return calculatedHash;
    }

    public String mineBlock(int difficulty) {
        String result;
        merkleRoot = StringUtil.getMerkleRoot(transactions);
        String target = StringUtil.getDificultyString(difficulty);
        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = calculateHash();
        }
        result = "Block mined" + hash;
        return result;
    }

    public Boolean addTransaction(Transaction transaction) {
        Boolean result = true;
        if (transaction == null) {
            result = false;
        }
        if (previousHash != "0") {
            if (transaction.processTransaction() != "true") {
                result = false;
            }
        }
        transactions.add(transaction);
        return result;
    }

    public String getHash() {
        return hash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public int getNonce() {
        return nonce;
    }
}
