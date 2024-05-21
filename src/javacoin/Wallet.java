package javacoin;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Wallet {
    private PrivateKey privateKey;
    private PublicKey publicKey;

    private HashMap<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>();

    public Wallet() {
        generateKeypair();
    }

    public void generateKeypair() {
        try {
            Security.addProvider(new BouncyCastleProvider());
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
            keyGen.initialize(ecSpec, random);
            KeyPair keyPair = keyGen.generateKeyPair();
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public float getBalance() {
        float total = 0;
        for (Map.Entry<String, TransactionOutput> item : Main.UTXOs.entrySet()) {
            TransactionOutput UTXO = item.getValue();
            if (UTXO.isMine(publicKey)) {
                UTXOs.put(UTXO.getId(), UTXO);
                total += UTXO.getValue();
            }
        }
        return total;
    }

    public Transaction sendFunds(PublicKey receiver, float value) {
        Transaction result = null;
        if (getBalance() > value) {
            ArrayList<TransactionInput> inputs = new ArrayList<>();
            float total = 0;
            for (Map.Entry<String, TransactionOutput> item : UTXOs.entrySet()) {
                TransactionOutput UTXO = item.getValue();
                total += UTXO.getValue();
                inputs.add(new TransactionInput(UTXO.getId()));
                if (total > value) break;
            }
            Transaction newTransaction = new Transaction(publicKey, receiver, value, inputs);
            newTransaction.generateSignature(privateKey);
            for (TransactionInput input : inputs) {
                UTXOs.remove(input.getTransactionOutputId());
            }
            result = newTransaction;
        }
        return result;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }
}
