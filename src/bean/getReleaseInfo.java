package bean;
import java.io.BufferedReader;
import java.io.File;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import java.util.Collections;
import java.util.Comparator;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;



public class getReleaseInfo {
	
	   public static HashMap<LocalDateTime, String> releaseNames;
	   public static HashMap<LocalDateTime, String> releaseID;
	   public static ArrayList<LocalDateTime> releases;
	   public static Integer numVersions;
	   public static String outname = null;
       public static int numberOfv=0;
	public static void main(String[] args) throws IOException, JSONException, ParseException {
		   
		   String projName ="BOOKKEEPER";
		   
		 //Fills the arraylist with releases dates and orders them
		   //Ignores releases with missing dates
		   releases = new ArrayList<>();
		         Integer i;
		         String url = "https://issues.apache.org/jira/rest/api/2/project/" + projName;
		         JSONObject json = readJsonFromUrl(url);
		         JSONArray versions = json.getJSONArray("versions");
		         
		         releaseNames = new HashMap<>();
		        
		         releaseID = new HashMap<> ();
		         for (i = 0; i < versions.length(); i++ ) {
		            String name = "";
		            String id = "";
		            if(versions.getJSONObject(i).has("releaseDate")) {
		               if (versions.getJSONObject(i).has("name"))
		                  name = versions.getJSONObject(i).get("name").toString();
		               if (versions.getJSONObject(i).has("id"))
		                  id = versions.getJSONObject(i).get("id").toString();
		               addRelease(versions.getJSONObject(i).get("releaseDate").toString(),
		                          name,id);
		            }
		         }
		         // order releases by date
		         Collections.sort(releases, new Comparator<LocalDateTime>(){
		            //@Override
		            public int compare(LocalDateTime o1, LocalDateTime o2) {
		                return o1.compareTo(o2);
		            }
		         });
		         if (releases.size() < 6)
		            return;
		         FileWriter fileWriter = null;
			 try {
		            fileWriter = null;
		             outname = projName + "VersionInfo.csv";
						    //Name of CSV for output
						    fileWriter = new FileWriter(outname);
		            fileWriter.append("Index,Version ID,Version Name,Date");
		            fileWriter.append("\n");
		            numVersions = releases.size();
		            for ( i = 0; i < releases.size(); i++) {
		               Integer index = i + 1;
		               fileWriter.append(index.toString());
		               fileWriter.append(",");
		               fileWriter.append(releaseID.get(releases.get(i)));
		               fileWriter.append(",");
		               fileWriter.append(releaseNames.get(releases.get(i)));
		               fileWriter.append(",");
		               fileWriter.append(releases.get(i).toString());
		               fileWriter.append("\n");
		            }

		         } catch (Exception e) {
		            System.out.println("Error in csv writer");
		            e.printStackTrace();
		         } finally {
		            try {
		               fileWriter.flush();
		               fileWriter.close();
		            } catch (IOException e) {
		               System.out.println("Error while flushing/closing fileWriter !!!");
		               e.printStackTrace();
		            }
		         }
			 Object year = takeFirstHalf(outname);
			 
			 RetrieveTicketsID.takeTicket(year);

		         return;
		   }
 
	
	   private static  Object takeFirstHalf(String outname) throws IOException {
		    
		    File file = new File(outname);
		    int count=0;
		    
		    ArrayList<Object> array = new ArrayList<Object>();
		    FileReader fileReader = new FileReader(file);
		    try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
				String line;
				
               
               
				while ((line = bufferedReader.readLine()) != null) {
					
					numberOfv++;
					String[] res = Arrays.copyOfRange(line.split(","), 3,4);
					String date=Arrays.toString(res);
					String[] res1 = Arrays.copyOfRange(date.split("-"), 0,1);
					String year=Arrays.toString(res1);
					//System.out.println(year);
					//System.out.println(count);
					
					if(count==1) {
						
						count=2;
						array.add(year);
						//System.out.println(array);
						continue;
						
						
					}
					if(count==2) {
						boolean found =false;
						for(int i=0;i<array.size();i=i+1) {

							
							if(array.get(i).equals(year)) {
								found=true;
								break;
								
								

							   
							  
							   
							  
							   
							}
							}
						if (found==false) {
							array.add(year);}
							
							}
						
					if(count==0) {
						count=1;
					}}
					
				

					
					}
		    int lenght=array.size();
		    int halfLen=lenght/2;
		    System.out.println(halfLen);
		    Object lastYear = array.get(halfLen);
		    String convertedToString = String.valueOf(lastYear);
		    String ly=(String) convertedToString.subSequence(2, 6);
		    System.out.println(ly);
		    return ly;
		    //System.out.println(array);
		    //System.out.println(lastYear);
		    }
		
	


	public static void addRelease(String strDate, String name, String id) {
		      LocalDate date = LocalDate.parse(strDate);
		      LocalDateTime dateTime = date.atStartOfDay();
		      if (!releases.contains(dateTime))
		         releases.add(dateTime);
		      releaseNames.put(dateTime, name);
		      releaseID.put(dateTime, id);
		      return;
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
	   
	   private static String readAll(Reader rd) throws IOException {
		      StringBuilder sb = new StringBuilder();
		      int cp;
		      while ((cp = rd.read()) != -1) {
		         sb.append((char) cp);
		      }
		      return sb.toString();
		   }

	
}