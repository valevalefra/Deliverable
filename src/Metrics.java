import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;

import java.util.ArrayList;

import java.util.List;

import entity.Commit;
import entity.FileCsv;
import entity.FileMetrics;

public class Metrics {
	
	  private Metrics() {
		    throw new IllegalStateException("Utility class");
		  }
	
	static File path = new File("C:\\Users\\valen\\bookkeeper");
	static List<Commit> list = new ArrayList<Commit>();
	//lista contenente nome file, versione,maxChange,avgChange,change
	static List<FileMetrics> listCgh = new ArrayList<FileMetrics>();
	static List<FileMetrics> listFile = new ArrayList<FileMetrics>();
	
	static int maxLoc=0;
	
	//restituisce la lista di tutti i commeit con associate le date
	public static void TakeCommitVersions() throws IOException, ParseException {
		
		
		Process commit=null;
		
		
		
		try {
			 commit = Runtime.getRuntime().exec("git -C "+path+" --no-pager log --pretty=format:\"%cs,%H\" --reverse" );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(commit.getInputStream()));
        

        String s;
		while ((s = stdInput.readLine()) != null ) {
	         String[] l=s.split(",",2);
	         Commit c= new Commit();
	         String p=l[0];
	         c.setData(p);
	         c.setId(l[1]);
	         list.add(c);
	         	
        }
		//associo al commit la versione
		for(int i=0;i<list.size();i++) {
		int v=RetrieveTicketsID.readCsv(list.get(i).getData(), null);
		list.get(i).setVersion(v);
		}
		calculate();
	}
	
	public static void calculate() throws IOException {
		
		Process diff=null;
		int count;
		
		for(int i=0;i<list.size()-1;i++) {
			//controllo che gli elementi appartengono alla stessa versione
			if(list.get(i).getVersion()==list.get(i+1).getVersion()) {
			String commitBefore=list.get(i).getId();
			String commitAfter=list.get(i+1).getId();
		
			try {
				 diff = Runtime.getRuntime().exec("git -C "+path+" --no-pager diff --numstat "+ commitBefore +" "+ commitAfter+ " *.java" );
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	        BufferedReader stdInput = new BufferedReader(new InputStreamReader(diff.getInputStream()));
	        
			FileMetrics fileM= new FileMetrics();
	        String s;
			while ((s = stdInput.readLine()) != null ) {
				boolean found=false;
				String[] l=s.split("\t",3);
				for(int j=0;j<listFile.size();j++) {
					//se il file è già presente nella lista con la stessa aggiorno solo il numero di righe
					if(listFile.get(j).getFileName().equals(l[2]) && listFile.get(j).getVersion()==list.get(i).getVersion()) {
						found=true;
						count=listFile.get(j).getCount()+1;
						int newRowAdded=listFile.get(j).getRowAdded()+Integer.parseInt(l[0]);
						int newRowDeleted=listFile.get(j).getRowDeleted()+Integer.parseInt(l[1]);
						int churn=newRowAdded-newRowDeleted;
						int maxLoc=max(listFile.get(j).getMaxLoc(),newRowAdded);
						int maxChurn=max(listFile.get(j).getChurn(),churn);
						float avgLoc=newRowAdded/count;
						float avgChurn=churn/count;
						listFile.get(j).setAvgChurn(avgChurn);
						listFile.get(j).setCount(count);
						listFile.get(j).setAvgLoc(avgLoc);
						listFile.get(j).setMaxChurn(maxChurn);
						listFile.get(j).setMaxLoc(maxLoc);
						listFile.get(j).setChurn(churn);
						listFile.get(j).setRowDeleted(newRowDeleted);
						listFile.get(j).setRowAdded(newRowAdded);
						break;
					}
				}
				if(found==false) {
				//Significa che il file non è presente nella lista quindi lo aggiungo per la prima volta
					 int churn=Integer.parseInt(l[0])-Integer.parseInt(l[1]);
			         fileM.setRowAdded(Integer.parseInt(l[0]));
			         fileM.setRowDeleted(Integer.parseInt(l[1]));
			         count=1;
			         int maxLoc=Integer.parseInt(l[0]);
			         float avgLoc=Integer.parseInt(l[0]);
			         int maxChurn=churn;
			         float avgChurn=churn;
			         fileM.setAvgLoc(avgLoc);
			         fileM.setMaxLoc(maxLoc);
			         fileM.setFileName(l[2]);
			         fileM.setMaxChurn(maxChurn);
			         fileM.setAvgChurn(avgChurn);
			         fileM.setChurn(churn);
			         fileM.setCount(count);
			         fileM.setVersion(list.get(i).getVersion());

			         listFile.add(fileM);
				}

			}   //arrivata qui ho una lista di dati relativi a una revision 
				
		}

				
	}
				
			/*for(int k=0;k<listFile.size();k++) {
			System.out.println(listFile.get(k).getNameFile());
			System.out.println(listFile.get(k).getRowAdded());}*/
	}
	
private static int max(int max, int newValue) {
		
        int maxValue=0;
        if(max>newValue) {
        	maxValue=max;
        }
        else {
        	maxValue=newValue;
        }
		return maxValue;
	}

public static void calculateCghSetSize() throws IOException {
		
		Process diff=null;
		for(int i=0;i<list.size()-1;i++) {
			
			int maxCSS;
			float avgCSS;
			int count;
			List<FileMetrics> listForCommit = new ArrayList<FileMetrics>();
			try {
				 diff = Runtime.getRuntime().exec("git -C "+path+" --no-pager diff-tree --no-commit-id --name-only -r "+ list.get(i).getId()+" *.java" );
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	        BufferedReader stdInput = new BufferedReader(new InputStreamReader(diff.getInputStream()));
	       
			String s;
			while ((s = stdInput.readLine()) != null ) {
				boolean found=false;
				for(int k=0;k<listCgh.size();k++) {
					if(listCgh.get(k).getFileName().equals(s) && listCgh.get(k).getVersion()==list.get(i).getVersion()) {
						//aggiorna
						int newCSS=listCgh.get(k).getChangeSS()+1;
						maxCSS=newCSS;
						count=listCgh.get(k).getCount()+1;
						avgCSS=newCSS/count;
						listCgh.get(k).setAvgChange(avgCSS);
						listCgh.get(k).setMaxChange(maxCSS);
	                    listCgh.get(k).setChangeSS(newCSS);
						found=true;
						break;
					}
				}
				if(found==false) {
					int newCSS=1;
					count=1;
					FileMetrics fm= new FileMetrics();
					fm.setChangeSS(newCSS);
					fm.setFileName(s);
					fm.setVersion(list.get(i).getVersion());
					maxCSS=newCSS;
					fm.setMaxChange(maxCSS);
					fm.setAvgChange(1);
					fm.setCount(count);
					listCgh.add(fm);
					listForCommit.add(fm);
				}

					
				
			
			
			}
	
		}
}
		
		
	
	

/*	private static int maxCgh(List<FileMetrics> listCgh2) {
	// TODO Auto-generated method stub
	return 0;
}

	private static int avgChurn(List<FileMetrics> listFile) {
		int numC=0;
		int tot=listFile.size();
		int avg=0;
		
		for(int k=0;k<listFile.size();k++) {
			numC=numC+listFile.get(k).getChurn();
		}
		
		avg=numC/tot;
		return avg;
	}

	private static int maxChurn(List<FileMetrics> listFile) {
		
		int maxChurn=0;
		for(int k=0;k<listFile.size();k++) {
			if(listFile.get(k).getChurn()>maxChurn) {
				maxChurn=listFile.get(k).getChurn();
			}
		}
		return maxChurn;
	
	}

	private static int avgLoc(List<FileMetrics> listFile) {
		
		int numL=0;
		int tot=listFile.size();
		int avg=0;
		
		for(int k=0;k<listFile.size();k++) {
			numL=numL+listFile.get(k).getRowAdded();
		}
		
		avg=numL/tot;
		return avg;
	}*/
	
	public static List<FileCsv> unionList() {
		
		List<FileCsv> fileMetrics = new ArrayList<FileCsv>();
		
		for(int i=0;i<listCgh.size();i++) {
			for(int j=0;j<listFile.size();j++) {
				if((listCgh.get(i).getFileName()).equals(listFile.get(j).getFileName()) && listCgh.get(i).getVersion()==listFile.get(j).getVersion()) {
					FileCsv file=new FileCsv();
					String fileName=listCgh.get(i).getFileName();
					int version=listCgh.get(i).getVersion();
					float avgChange=listCgh.get(i).getAvgChange();
					float maxChange=listCgh.get(i).getMaxChange();
					int change=listCgh.get(i).getChangeSS();
					int locA=listFile.get(j).getRowAdded();
					int maxLoc=listFile.get(j).getMaxLoc();
					float avgLoc=listFile.get(j).getAvgLoc();
					int maxChurn=listFile.get(j).getMaxChurn();
					float avgChurn=listFile.get(j).getAvgChurn();
					float churn=listFile.get(j).getChurn();
					file.setAvgChurn(avgChurn);
					file.setAvgLoc(avgLoc);
					file.setMaxChurn(maxChurn);
					file.setMaxLoc(maxLoc);
					file.setLocAdded(locA);
					file.setMaxCghSetSize(maxChange);
					file.setAvgCghSetSize(avgChange);
					file.setFileName(fileName);
					file.setId(version);
					file.setChurn(churn);
					file.setCghSetSize(change);
					fileMetrics.add(file);
							
				}
			}
		}
		return fileMetrics;
	}

	public static List<FileCsv> setMetrics(List<FileCsv> csvLines) throws IOException, ParseException {
		
		TakeCommitVersions();
		calculate();
		calculateCghSetSize();
		
		List<FileCsv> fileMetrics=new ArrayList<FileCsv>();
		
		fileMetrics=unionList();
		for(int i=0;i<csvLines.size();i++) {
			for(int j=0;j<fileMetrics.size();j++) {
				if((csvLines.get(i).getFileName()).equals(fileMetrics.get(j).getFileName()) && csvLines.get(i).getId()==fileMetrics.get(j).getId()) {
					float avgChange=fileMetrics.get(j).getAvgCghSetSize();
					float maxChange=fileMetrics.get(j).getMaxCghSetSize();
					int change=fileMetrics.get(j).getCghSetSize();
					int locA=fileMetrics.get(j).getLocAdded();
					int maxLoc=fileMetrics.get(j).getMaxLoc();
					float avgLoc=fileMetrics.get(j).getAvgLoc();
					int maxChurn=fileMetrics.get(j).getMaxChurn();
					float avgChurn=fileMetrics.get(j).getAvgChurn();
					float churn=fileMetrics.get(j).getChurn();
					csvLines.get(i).setAvgChurn(avgChurn);
					csvLines.get(i).setAvgLoc(avgLoc);
					csvLines.get(i).setMaxChurn(maxChurn);
					csvLines.get(i).setMaxLoc(maxLoc);
					csvLines.get(i).setLocAdded(locA);
					csvLines.get(i).setMaxCghSetSize(maxChange);
					csvLines.get(i).setAvgCghSetSize(avgChange);
					csvLines.get(i).setChurn(churn);
					csvLines.get(i).setCghSetSize(change);
				}
			}
		}
		
		return csvLines;
	}
	
	
/*	public static int maxLoc(List<FileMetrics> listFile) {
		
		
		for(int k=0;k<listFile.size();k++) {
			if(listFile.get(k).getRowAdded()>maxLoc) {
				maxLoc=listFile.get(k).getRowAdded();
			}
		}
		return maxLoc;
	}*/

}
