package entity;


public class FileCsv {
	
	int id;
	String fileName;
	boolean bug;
	int locAdded;
	int maxLoc;
	float avgLoc;
	float churn;
	int maxChurn;
	float avgChurn;
	int CghSetSize;
	float maxCghSetSize;
	float avgCghSetSize;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public boolean isBug() {
		return bug;
	}
	public void setBug(boolean bug) {
		this.bug = bug;
	}
	public int getLocAdded() {
		return locAdded;
	}
	public void setLocAdded(int locAdded) {
		this.locAdded = locAdded;
	}
	public int getMaxLoc() {
		return maxLoc;
	}
	public void setMaxLoc(int maxLoc) {
		this.maxLoc = maxLoc;
	}
	public float getAvgLoc() {
		return avgLoc;
	}
	public void setAvgLoc(float avgLoc) {
		this.avgLoc = avgLoc;
	}
	public float getChurn() {
		return churn;
	}
	public void setChurn(float churn2) {
		this.churn = churn2;
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
	public int getCghSetSize() {
		return CghSetSize;
	}
	public void setCghSetSize(int cghSetSize) {
		CghSetSize = cghSetSize;
	}
	public float getMaxCghSetSize() {
		return maxCghSetSize;
	}
	public void setMaxCghSetSize(float maxChange) {
		this.maxCghSetSize = maxChange;
	}
	public float getAvgCghSetSize() {
		return avgCghSetSize;
	}
	public void setAvgCghSetSize(float avgCghSetSize) {
		this.avgCghSetSize = avgCghSetSize;
	}

}
