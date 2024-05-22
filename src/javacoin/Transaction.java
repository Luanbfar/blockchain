package javacoin;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

public class Transaction {
    private String transactionID;
    private PublicKey sender;
    private PublicKey reciever;
    private float value;
    private byte[] signature;

    private static int sequence = 0;

    public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
    public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

    public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs) {
        this.sender = from;
        this.reciever = to;
        this.value = value;
        this.inputs = inputs;
    }

    private String calulateHash() {
        sequence++;
        return StringUtil.applySha256(StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciever) + Float.toString(value) + sequence);
    }

    public void generateSignature(PrivateKey privateKey) {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciever) + Float.toString(value);
        signature = StringUtil.applyECDSASig(privateKey, data);
    }

    public boolean verifySignature() {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciever) + Float.toString(value);
        return StringUtil.verifyECDSASig(sender, data, signature);
    }

    public float getInputsValue() {
        float total = 0;
        for (TransactionInput i : inputs) {
            if (i.getUTXO() == null) continue;
            total += i.getUTXO().getValue();
        }
        return total;
    }

    public float getOutputsValue() {
        float total = 0;
        for (TransactionOutput o : outputs) {
            total += o.getValue();
        }
        return total;
    }

    public boolean processTransaction() {
        boolean result = true;
        if (!verifySignature()) {
            result = false;
        }
        for (TransactionInput i : inputs) {
            i.setUTXO(Main.UTXOs.get(i.getTransactionOutputId()));
        }
        if (getInputsValue() < Main.minimumTransaction) {
            result = false;
        }
        float leftOver = getInputsValue() - value;
        transactionID = calulateHash();
        outputs.add(new TransactionOutput(this.reciever, value, transactionID));
        outputs.add(new TransactionOutput(this.sender, leftOver, transactionID));

        for (TransactionOutput o : outputs) {
            Main.UTXOs.put(o.getId(), o);
        }
        for (TransactionInput i : inputs) {
            if (i.getUTXO() == null) continue;
            Main.UTXOs.remove(i.getUTXO().getId());
        }
        return result;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public PublicKey getSender() {
        return sender;
    }

    public PublicKey getReciever() {
        return reciever;
    }

    public float getValue() {
        return value;
    }

    public byte[] getSignature() {
        return signature;
    }

    public static int getSequence() {
        return sequence;
    }
}
