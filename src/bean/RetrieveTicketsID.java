package bean;
import java.io.BufferedReader;
import java.io.File;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import entity.FileCsv;
import entity.Versions;

import org.json.JSONArray;

public class RetrieveTicketsID {

	
		static String filePath="C:\\Users\\valen\\Desktop\\file2.csv"; 
		static File path = new File("C:\\Users\\valen\\bookkeeper");
		static FileWriter fileWriter= null;
		static List<String> filteredFiles=new ArrayList<String>();
		
		//static FileCsv fileCsv=new FileCsv();
		static List<FileCsv> csvLines = new ArrayList<FileCsv>();
		static List<FileCsv> csvFinal = new ArrayList<FileCsv>();
		

   private static String readAll(Reader rd) throws IOException {
	      StringBuilder sb = new StringBuilder();
	      int cp;
	      while ((cp = rd.read()) != -1) {
	         sb.append((char) cp);
	      }
	      return sb.toString();
	   }

   public static JSONArray readJsonArrayFromUrl(String url) throws IOException, JSONException {
      InputStream is = new URL(url).openStream();
      try {
         BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
         String jsonText = readAll(rd);
         JSONArray json = new JSONArray(jsonText);
         return json;
       } finally {
         is.close();
       }
   }

   public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
      InputStream is = new URL(url).openStream();
      try {
         BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
         String jsonText = readAll(rd);
         JSONObject json = new JSONObject(jsonText);
         return json;
       } finally {
         is.close();
       }
   }


  
  	   public static void takeTicket(Object year) throws IOException, JSONException {
  		 fileWriter =new FileWriter(filePath);
  		 JSONObject json;
		   String projName ="bookkeeper";
	   Integer j = 0, i = 0, total = 1;
	   int p = 0;
	   int n=1;
	   listOfAllFile();
      //Get JSON API for closed bugs w/ AV in the project
      do {
         //Only gets a max of 1000 at a time, so must do this multiple times if bugs >1000
         j = i + 1000;
         String url = "https://issues.apache.org/jira/rest/api/2/search?jql=project=%22"
                + projName + "%22AND%22issueType%22=%22Bug%22AND(%22status%22=%22closed%22OR"
                + "%22status%22=%22resolved%22)AND%22resolution%22=%22fixed%22&fields=key,resolutiondate,versions,fixVersions,created&startAt="
                + i.toString() + "&maxResults=" + j.toString();
          json = readJsonFromUrl(url);
       // JSONArray version = json.getJSONArray("versions");
         JSONArray issues = json.getJSONArray("issues");
        // JSONArray fields = json.getJSONArray("fields");
         total = json.getInt("total");
         for (; i < total && i < j; i++) {
            //Iterate through each bug
        	
        	
        	    Object fields = issues.getJSONObject(i%1000).get("fields");
        	    //creation date of ticket
        	    String creationDate = ((JSONObject) fields).get("created").toString();
                String key = issues.getJSONObject(i%1000).get("key").toString();
                
                //f
           if(verifyDate(key,year)) {
                
            	JSONArray versions = ((JSONObject) fields).getJSONArray("versions");
            	JSONArray fixVersions = ((JSONObject) fields).getJSONArray("fixVersions");
            	//System.out.println(key);
            	
            	//CASE1: I have AV and FV

            	//int ov=takeFirstCommit(key,creationDate );
            	//int fv =takeLastCommit(key);
            	//if(fv==-1) {
            	//System.out.println("manaaaaaaaaaaaaaaaaaaaaaaaaaaaaapppppppppppppppppppppppppppppp");}
            	
            	if ( fixVersions.length()!=0 && versions.length()!=0 ) {
            		
            		String fVersion = takeFV(fixVersions);
            		ArrayList<String> av = takeAV(versions);
            		String iVersion= av.get(0);
            		int fv=readCsv(null,fVersion);
            		int iv=readCsv(null,iVersion);
            		int ov=takeFirstCommit(key,creationDate );
            		if(fv==iv) {
            			//System.out.println("non prop, class no bug");
            			takeVersions(key,iv,ov,fv,1,year);
            			continue;
            		}
            		if(fv==ov) {
            			takeVersions(key,iv,ov,fv,1,year);
            			//System.out.println("non prop, class no bug");
            			continue;
            		}
            		if(fv<iv) {
            			//System.out.println("non prop, scarto");
            			continue;
            		}
            		if(ov<iv && iv<fv) {
            			//System.out.println("bdajsdbsjbajsvajksfvafjksfvksjfbbajksfvjksfvbjksfvjksfvsakjfvksavfk");
            			takeVersions(key,iv,ov,fv,0,year);
            			System.out.println("non prop, lascio ticket");
            			continue;
            		}
            		if(ov<iv && !(iv<fv)) {
            			System.out.println("non prop, scarto ticket");
            			continue;
            		}
            		if(fv<ov && iv<fv) {
            		//	System.out.println("bdajsdbsjbajsvajksfvafjksfvksjfbbajksfvjksfvbjksfvjksfvsakjfvksavfk");
            			takeVersions(key,iv,ov,fv,0,year);
            			System.out.println("non prop, scarto ticket");
            			continue;
            		}
            		if(fv<ov && !(iv<fv)) {
            			System.out.println("non prop, scarto ticket");
            			continue;
            		}
            		if(iv<ov && ov<fv) {
            			//System.out.println("bdajsdbsjbajsvajksfvafjksfvksjfbbajksfvjksfvbjksfvjksfvsakjfvksavfk");
            			int newP=calculateP(iv,ov,fv);
            		//	System.out.println("firsttttttttttttttttttt");
            			//System.out.println(p);
            			p=(p+newP);
            			Versions list =new Versions();
                    	list.setFv(fv);
                    	list.setIv(iv);
                    	list.setOv(ov);
                    	list.setKey(key);
                    	/*System.out.println("proportionoooooooooooo");
                    	System.out.println(newP);
                    	System.out.println(n);
                    	System.out.println(p);
                    	System.out.println(list.getKey());
                    	System.out.println(list.getIv());
                    	System.out.println(list.getOv());
                    	System.out.println(list.getFv());*/
                    	takeVersions(key,iv,ov,fv,0,year);
                    	continue;
            			
            			
            		}
            		

            		
            		

            		   }
            	if ( versions.length()==0 ) {
            		n++;
            		int prop=p/n;
                	int ov=takeFirstCommit(key,creationDate );
                	int fv =takeLastCommit(key);
                	int iv=calculateIV(ov,fv,prop);
                	if(fv==iv) {
            			System.out.println("non prop, class no bug");
            			takeVersions(key,iv,ov,fv,1,year);
            			continue;
            		}
            		if(fv==ov) {
            			System.out.println("non prop, class no bug");
            			takeVersions(key,iv,ov,fv,1,year);
            			continue;
            		}
            		if(fv<iv) {
            			System.out.println("non prop, scarto");
            			continue;
            		}
            		if(ov<iv && iv<fv) {
            			takeVersions(key,iv,ov,fv,0,year);
            			System.out.println("non prop, lascio ticket");
            			continue;
            		}
            		if(ov<iv && !(iv<fv)) {
            			System.out.println("non prop, scarto ticket");
            			continue;
            		}
            		if(fv<ov && iv<fv) {
            			//System.out.println("bdajsdbsjbajsvajksfvafjksfvksjfbbajksfvjksfvbjksfvjksfvsakjfvksavfk");
            			takeVersions(key,iv,ov,fv,0,year);
            			System.out.println("non prop, prendo ticket");
            			continue;
            		}
            		if(fv<ov && !(iv<fv)) {
            			//System.out.println("bdajsdbsjbajsvajksfvafjksfvksjfbbajksfvjksfvbjksfvjksfvsakjfvksavfk");
            			takeVersions(key,iv,ov,fv,0,year);
            			System.out.println("non prop, scarto ticket");
            			continue;
            		}
            		if(iv<ov && ov<fv) {
                	Versions list =new Versions();
                	list.setFv(fv);
                	list.setIv(iv);
                	list.setOv(ov);
                	list.setKey(key);
                //	System.out.println("bdajsdbsjbajsvajksfvafjksfvksjfbbajksfvjksfvbjksfvjksfvsakjfvksavfk");
                	//System.out.println("ppppppppppppppppppppppppppppppppppp");
                	//System.out.println(prop);
                	//System.out.println(list.getKey());
                	//System.out.println(list.getIv());
                	//System.out.println(list.getOv());
                	//System.out.println(list.getFv());
                	takeVersions(key,iv,ov,fv,0,year);
                	continue;
            		
            	}}

             } } }while (i < total);
      
      
     
      for(int y=0;y<filteredFiles.size();y++) {
    	  System.out.println(filteredFiles.get(y));
    	  foundVersion(filteredFiles.get(y),-1,-1,-1,-1,-1);
      }
       orderCsv(csvLines);
       
       try {
		csvFinal=Metrics.setMetrics(csvLines);
	} catch (IOException | ParseException e) {
		
		e.printStackTrace();
	}
       writeCsv(csvFinal);
        return;
   }

	private static int calculateIV(int ov, int fv, int p) {
		int iv=0;
		iv=fv-((fv-ov)*p);
		return iv;
	}

	private static int calculateP(int iv, int ov, int fv) {
		int p=0;
		p=(fv-iv)/(fv-ov);
//		System.out.println("proportion");
	//	System.out.println(p);
		return p;
		
		
	}

	private static int takeFirstCommit(String key, String creationDate) throws IOException {

		    int ov;
        	//System.out.println("aaaaaaaaaaaaaa");
        	ov=readCsv(creationDate,null);
        	//System.out.println(creationDate);
		
		
	return ov;}

	private static boolean verifyDate(String key, Object year) throws IOException {
		
		Process logGit = null;
		String s;

        boolean check = false;
		

            
			//File path = new File("C:\\Users\\valen\\bookkeeper");
			
			//Prendo la data del primo commit per ogni ticket
			try {
				logGit = Runtime.getRuntime().exec("git -C "+path +" log -1 --pretty=format:\"%cs\" --grep=" + key );
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        BufferedReader stdInput = new BufferedReader(new InputStreamReader(logGit.getInputStream()));
	   

	        while ((s = stdInput.readLine()) != null ) {
	        	int foo = Integer.parseInt(s.substring(0, 4));
	        	int i=Integer.parseInt((String) year);
               
	        	
	        	if(foo<=i) {
	        		
	        		check= true;
	        	}
		

	}return check;}

	private static int takeLastCommit(String key) throws IOException {
		Process logGit = null;
		String s;

        int fv = 0;
		

            
			//File path = new File("C:\\Users\\valen\\bookkeeper");
			
			//Prendo la data del primo commit per ogni ticket
			try {
				logGit = Runtime.getRuntime().exec("git -C "+path +" log -1 --pretty=format:\"%cs\" --grep=" + key );
			} catch (IOException e) {
			
				e.printStackTrace();
			}
	        BufferedReader stdInput = new BufferedReader(new InputStreamReader(logGit.getInputStream()));


	        while ((s = stdInput.readLine()) != null ) {
	        	//System.out.println("sempreeeeeeeeeee\n");
	        	//System.out.println(s);

	        	 fv = readCsv(s,null);
	        }
			
			return fv;
	          
	}

	public static int readCsv(String s, String iVersion) throws IOException {
		
		String file = getReleaseInfo.outname;

		BufferedReader br = new BufferedReader(new FileReader(file));
	    String line;
	    int count = 0;
	    int n=0;
	    while ( (line = br.readLine()) != null ) {
	    	if(n==0) {
	    		n=1;
	    		
	    		continue;
	    	}
	    	if(n==1) {
	    		if(iVersion==null) {
	    		
	        String[] values = line.split(",");
	        
	        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	        Date y = new Date();
	        Date v = new Date();
	        try {
	            
	            v=dateFormat.parse(values[3]);
	            
	            y = dateFormat.parse(s);

	        } catch (ParseException e) {
	            
	            e.printStackTrace();
	        }

	        count++;
	        if(v.after(y) || v.equals(y)) {
	             
	            break;
	        }
	    	}
	    		//associo la versione al corrispettivo id esempio 4.4.0 coincide all'id 3
	    		if(iVersion!=null) {
	    			 String[] values = line.split(",");
	    		        
	    		        count++;
	    		        if(values[2].equals(iVersion)) {
	    		            break;
	    		        }
	    			} }
	    		}
	    
	   
	   
	    br.close();
	    return count;
		
	}

	private static ArrayList<String> takeAV(JSONArray versions) {
		ArrayList<String> AV = new ArrayList<String>();
		
    	for (int k = 0; k < versions.length(); k++ ) {
    		
    		String ver="";
    		   if (versions.getJSONObject(k).has("name")) {
    		       ver = versions.getJSONObject(k).get("name").toString();
    		       AV.add(ver);
                  
    		     }
    		   }return AV;
		
	}

	private static String takeFV(JSONArray fixVersions) {
	    
		String lastFV="";
		   if (fixVersions.getJSONObject(0).has("name")) {
		       lastFV = fixVersions.getJSONObject(0).get("name").toString();


		     }
		   return lastFV;
	}
	
	public static void listOfAllFile() throws IOException {
		
		
		List<String> rawFiles = new ArrayList<>();
		Process logGit = null;
		logGit = Runtime.getRuntime().exec("git -C "+path +" --no-pager log --pretty=format:\"\" --name-only *.java");
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(logGit.getInputStream()));
		String line;
		String prevLine="";
		while ((line=stdInput.readLine())!=null || prevLine!=null) {
			if(line!=null && !line.equals("")) 
				rawFiles.add(line);
			prevLine=line;
				
			
		}
		//elimino file duplicati
		filteredFiles=new ArrayList<String>(new LinkedHashSet<String>(rawFiles));
		/*FileWriter fileWriter2= null;
		fileWriter2 =new FileWriter("C:\\Users\\valen\\Desktop\\file3.csv");
		for(int i=0;i<filteredFiles.size();i++) {
			fileWriter2.append(filteredFiles.get(i));
			fileWriter2.append("\n");*/
		//}
		//System.out.println(filteredFiles);
		return;
		
	}

	private static void takeVersions( String key,int iv,int ov,int fv, int j,Object year) throws IOException {
		
		Process logGit = null;
		String s;

        String fileName;
		try{

            
			//File path = new File("C:\\Users\\valen\\bookkeeper");
			
			//Prendo il commit per ogni ticket
			logGit = Runtime.getRuntime().exec("git -C "+path +" log -1 --format=%H --grep=" + key );
	        BufferedReader stdInput = new BufferedReader(new InputStreamReader(logGit.getInputStream()));

	          
	           
	           
			while ((s = stdInput.readLine()) != null ) {
				//prendo i file java relativi ad ogni commit 
				Process logDiff = Runtime.getRuntime().exec("git -C "+path +" diff-tree --no-commit-id --name-only -r " +s+ " *.java" );
				//System.out.println(s);
				 BufferedReader stdInput1 = new BufferedReader(new InputStreamReader(logDiff.getInputStream()));
			
			     
			     while ((fileName = stdInput1.readLine()) != null ) {
                      
			    	 //elimino i file che ho trovato che hanno un certo commit
			    	 for(int i=0;i<filteredFiles.size();i++) {
			    	     if(fileName.equals(filteredFiles.get(i))) {
			    	    	 filteredFiles.remove(filteredFiles.get(i));
			    	     }
			    	   }

			          foundVersion(fileName,year,iv, ov, fv,j);

			     }
			}
		}
			catch(Exception ex)
			  {
			        if(logGit!=null)
			        {
			              logGit.destroy();
			        }
			        ex.printStackTrace();
			        System.exit(-1);}
	}
			          /*Process logDC=Runtime.getRuntime().exec("git -C "+path+" log --diff-filter=A --pretty=format:%cs -- "+fileName);
			          Process logDD=Runtime.getRuntime().exec("git -C "+path+" log --diff-filter=D --pretty=format:%cs -- "+fileName);
			          BufferedReader stdInput2 = new BufferedReader(new InputStreamReader(logDC.getInputStream()));
			          BufferedReader stdInput3 = new BufferedReader(new InputStreamReader(logDD.getInputStream()));
			          while ((s = stdInput2.readLine()) != null && (t=stdInput3.readLine()) != null)  {
			        	 
			        	  int d1 = Integer.parseInt(s.substring(0, 4));
			        	  int d2 = Integer.parseInt(t.substring(0, 4));
				          int k=Integer.parseInt((String) year);

				        //  System.out.println("\n\n");
				         // System.out.println(d2);
				         // System.out.println(d1<=k);
				          //System.out.println(d2<=k);
				     //  if(d1<=k ) {
			        	  int idCreation=readCsv(s,null);
			        	  int idDelete=readCsv(t,null);
			        	  
			        for(int i=idCreation;i<idDelete;i++) {
			        	if(j==0) {
			        		//System.out.println("ttttttttttttttttttttttttttttttt");
		        			// System.out.println(i);
		        			 //System.out.println(fv);
		        			 //System.out.println(iv);
			        		 if(i<=fv && i>=iv) {
			        			// System.out.println("ttttttttttttttttttttttttttttttt");
			        			 FileCsv fileCsv=new FileCsv();
			        			 fileCsv.setBug(true);
			        			 fileCsv.setFileName(fileName);
			        			 fileCsv.setId(i);
			        			 
			        			 
			        			 csvLines.add(fileCsv);
			        			 
			        			 
			        			 
			        			 //writeCsv(i,fileName,true);
			        			
			        		 }
			        		 else{
			        			 FileCsv fileCsv=new FileCsv();
			        			 fileCsv.setBug(false);
			        			 fileCsv.setFileName(fileName);
			        			 fileCsv.setId(i);
			        			 csvLines.add(fileCsv);
			        			 //writeCsv(i,fileName,false);
			        			 }
			        		 }
			        	 else {
			        		 FileCsv fileCsv=new FileCsv();
			        		 fileCsv.setBug(false);
		        			 fileCsv.setFileName(fileName);
		        			 fileCsv.setId(i);
		        			 csvLines.add(fileCsv);
			        			//writeCsv(i,fileName,false);
			        			
			        		  }
			        	  }
			        //}
			        	  

                                    
			          }
	        	   //System.out.println(fileName);
	        	   }
			    
		           while ((s = stdError1.readLine()) != null) {
		               //System.out.println(s);
		              
		           }

	           }
			//creationDate=
	           
	           // read any errors from the attempted command
			
	     
	           while ((s = stdError.readLine()) != null) {
	               //System.out.println(s);
	              
	           }
	           

	       
	  
		}
	  catch(Exception ex)
	  {
	        if(logGit!=null)
	        {
	              logGit.destroy();
	        }
	        ex.printStackTrace();
	        System.exit(-1);
	  }*/
		//return month;
		

	

	private static void foundVersion(String fileName, Object year, int iv, int ov, int fv, int j) throws IOException {
		
		 Process logDC=Runtime.getRuntime().exec("git -C "+path+" log --diff-filter=A --pretty=format:%cs -- "+fileName);
         Process logDD=Runtime.getRuntime().exec("git -C "+path+" log --diff-filter=D --pretty=format:%cs -- "+fileName);
         BufferedReader stdInput2 = new BufferedReader(new InputStreamReader(logDC.getInputStream()));
         BufferedReader stdInput3 = new BufferedReader(new InputStreamReader(logDD.getInputStream()));
         String s;
		 String t;
		while ((s = stdInput2.readLine()) != null)  {
       	 
		  t=stdInput3.readLine();
		 // int d1 = Integer.parseInt(s.substring(0, 4));
       	  //int d2 = Integer.parseInt(t.substring(0, 4));
	     // int k=Integer.parseInt((String) year);

	        //  System.out.println("\n\n");
	         // System.out.println(d2);
	         // System.out.println(d1<=k);
	          //System.out.println(d2<=k);
	     //  if(d1<=k ) {
       	  int idCreation=readCsv(s,null);
       	  int idDelete;
       	 
       if(t==null) {
    	   idDelete= getReleaseInfo.numberOfv-1;  
       }
       else {
    	    idDelete=readCsv(t,null);
       }
       	  
       for(int i=idCreation;i<idDelete;i++) {
       	if(j==0) {
       		//System.out.println("ttttttttttttttttttttttttttttttt");
   			// System.out.println(i);
   			 //System.out.println(fv);
   			 //System.out.println(iv);
       		 if(i<=fv && i>=iv) {
       			// System.out.println("ttttttttttttttttttttttttttttttt");
       			 FileCsv fileCsv=new FileCsv();
       			 fileCsv.setBug(true);
       			 fileCsv.setFileName(fileName);
       			 fileCsv.setId(i);
       			 
       			 
       			 csvLines.add(fileCsv);
       			 
       			 
       			 
       			 //writeCsv(i,fileName,true);
       			
       		 }
       		 else{
       			 FileCsv fileCsv=new FileCsv();
       			 fileCsv.setBug(false);
       			 fileCsv.setFileName(fileName);
       			 fileCsv.setId(i);
       			 csvLines.add(fileCsv);
       			 //writeCsv(i,fileName,false);
       			 }
       	 }
       	 else {
       		 FileCsv fileCsv=new FileCsv();
       		 fileCsv.setBug(false);
   			 fileCsv.setFileName(fileName);
   			 fileCsv.setId(i);
   			 csvLines.add(fileCsv);
       			//writeCsv(i,fileName,false);
       			
       		  }
       }
	}
 
}

	private static void writeCsv(List<FileCsv> file) throws IOException  {
		
		fileWriter.append("id");
		fileWriter.append(",");
		fileWriter.append("fileName");
		fileWriter.append(",");
		fileWriter.append("avgCghSetSize");
		fileWriter.append(",");
		fileWriter.append("avgChurn");
		fileWriter.append(",");
		fileWriter.append("avgLoc");
		fileWriter.append(",");
		fileWriter.append("cghSetSize");
		fileWriter.append(",");
		fileWriter.append("churn");
		fileWriter.append(",");
		fileWriter.append("locAdded");
		fileWriter.append(",");
		fileWriter.append("maxCghSetSize");
		fileWriter.append(",");
		fileWriter.append("maxChurn");
		fileWriter.append(",");
		fileWriter.append("maxLoc");
		fileWriter.append(",");
		fileWriter.append("bug");
		fileWriter.append("\n");

		for(int i=0; i<file.size();i++) {
	
  		try {
			int idNum=file.get(i).getId();
			fileWriter.append(String.valueOf(idNum));
			fileWriter.append(",");
			fileWriter.append(file.get(i).getFileName());
			fileWriter.append(",");
			fileWriter.append(String.valueOf(file.get(i).getAvgCghSetSize()));
			fileWriter.append(",");
			fileWriter.append(String.valueOf(file.get(i).getAvgChurn()));
			fileWriter.append(",");
			fileWriter.append(String.valueOf(file.get(i).getAvgLoc()));
			fileWriter.append(",");
			fileWriter.append(String.valueOf(file.get(i).getCghSetSize()));
			fileWriter.append(",");
			fileWriter.append(String.valueOf(file.get(i).getChurn()));
			fileWriter.append(",");
			fileWriter.append(String.valueOf(file.get(i).getLocAdded()));
			fileWriter.append(",");
			fileWriter.append(String.valueOf(file.get(i).getMaxCghSetSize()));
			fileWriter.append(",");
			fileWriter.append(String.valueOf(file.get(i).getMaxChurn()));
			fileWriter.append(",");
			fileWriter.append(String.valueOf(file.get(i).getMaxLoc()));
			fileWriter.append(",");
			if(file.get(i).isBug()) {
            fileWriter.append("yes");}
			else {
				fileWriter.append("no");
			}
			
			fileWriter.append("\n");
			System.out.println("fatto");
		} catch (IOException e) {
			
			e.printStackTrace();
		}finally {
			try {
				fileWriter.flush();
				//fileWriter.close();
			}
			catch(Exception e){
				e.printStackTrace();
				
			}
		
		
		} }}
	
	
	 static void orderCsv(List<FileCsv> csvLines) {
	
		   
		//   Comparator<List<String>> comp = new Comparator<List<String>>() {
		    Collections.sort(csvLines,new Comparator<FileCsv>() {
		            @Override
		            public int compare( FileCsv o1,  FileCsv o2) {
		            	
		            return Integer.valueOf(o1.getId()).compareTo(o2.getId());
		           
		                
		            }
		           

		        });
		 


		    } 
		}
		
	


	
	    
		
	

