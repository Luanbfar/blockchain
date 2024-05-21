package javacoin;

import java.util.ArrayList;
import java.util.HashMap;

public class Main {
    public static ArrayList<Block> blockchain = new ArrayList<>();
    public static HashMap<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>();
    public static int difficulty = 5;
    public static float minimumTransaction = 0.1f;
    public static Wallet walletA;
    public static Wallet walletB;

    void main() {

        walletA = new Wallet();
        walletB = new Wallet();

        System.out.println("Private and public keys:");
        System.out.println(StringUtil.getStringFromKey(walletA.getPrivateKey()));
        System.out.println(StringUtil.getStringFromKey(walletA.getPublicKey()));
        Transaction transaction = new Transaction(walletA.getPublicKey(), walletB.getPublicKey(), 5);
        transaction.generateSignature(walletA.getPrivateKey());
        System.out.println("Is signature verified");
        System.out.println(transaction.verifySignature());
    }

    public static String chainValidation() {
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\u0000', '0');
        String result = "true";

        for (int i = 1; i < blockchain.size(); i++) {
            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i - 1);
            if (!currentBlock.getHash().equals(currentBlock.calculateHash())) {
                result = String.format("Current hashes are not the same at block %s", i + 1);
            }
            if (!previousBlock.getHash().equals(currentBlock.getPreviousHash())) {
                result = String.format("Previous hashes are not the same at block %s", i + 1);
            }
            if (!currentBlock.getHash().substring(0, difficulty).equals(hashTarget)) {
                result = String.format("This block hasn't been mined at block %s", i + 1);
            }
        }
        return result;
    }
}
