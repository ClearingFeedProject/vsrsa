package clearing.feed;

public class Feed 
{
	private String TransactionRef,Payer,Payee,ValueDate;
	private double Amount;
	@Override
	public String toString() {
		return "Feed [TransactionRef=" + TransactionRef + ", Payer=" + Payer + ", Payee=" + Payee + ", Amount=" + Amount
				+ ", ValueDate=" + ValueDate + "]\n";
	}
	public String getTransactionRef() {
		return TransactionRef;
	}
	public void setTransactionRef(String transactionRef) {
		TransactionRef = transactionRef;
	}
	public String getPayer() {
		return Payer;
	}
	public void setPayer(String payer) {
		Payer = payer;
	}
	public String getPayee() {
		return Payee;
	}
	public void setPayee(String payee) {
		Payee = payee;
	}
	public double getAmount() {
		return Amount;
	}
	public void setAmount(double amount) {
		Amount = amount;
	}
	public String getValueDate() {
		return ValueDate;
	}
	public void setValueDate(String valueDate) {
		ValueDate = valueDate;
	}
}
