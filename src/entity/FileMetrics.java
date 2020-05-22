package entity;

public class FileMetrics {
	
	String fileName;
	int rowAdded;
	int version;
	int rowDeleted;
	int maxLoc;
	int maxChurn;
	float avgChurn;
	float avgLoc;
	int churn;
	int count;
	int changeSS;
	int maxChange;
	float avgChange;
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String nameFile) {
		this.fileName = nameFile;
	}
	public int getRowAdded() {
		return rowAdded;
	}
	public void setRowAdded(int rowAdded) {
		this.rowAdded = rowAdded;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public int getRowDeleted() {
		return rowDeleted;
	}
	public void setRowDeleted(int rowDeleted) {
		this.rowDeleted = rowDeleted;
	}
	public int getMaxLoc() {
		return maxLoc;
	}
	public void setMaxLoc(int maxLoc) {
		this.maxLoc = maxLoc;
	}
	public int getChurn() {
		return churn;
	}
	public void setChurn(int churn) {
		this.churn = churn;
	}
	public int getMaxChurn() {
		return maxChurn;
	}
	public void setMaxChurn(int maxChurn) {
		this.maxChurn = maxChurn;
	}
	public float getAvgChurn() {
		return avgChurn;
	}
	public void setAvgChurn(float avgChurn) {
		this.avgChurn = avgChurn;
	}
	public float getAvgLoc() {
		return avgLoc;
	}
	public void setAvgLoc(float avgLoc) {
		this.avgLoc = avgLoc;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getChangeSS() {
		return changeSS;
	}
	public void setChangeSS(int changeSS) {
		this.changeSS = changeSS;
	}
	public int getMaxChange() {
		return maxChange;
	}
	public void setMaxChange(int maxChange) {
		this.maxChange = maxChange;
	}
	public float getAvgChange() {
		return avgChange;
	}
	public void setAvgChange(float avgChange) {
		this.avgChange = avgChange;
	}

}
