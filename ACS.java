import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;

public class ACS {

  public static void main(String[] args) {
    // Sort out arguments passed 
    boolean makeRoot = true;
    String userFile = null;
    String fileListFile = null;
    if(args[0].trim().equalsIgnoreCase("-r")){
      makeRoot = false;
      userFile = args[1];
      fileListFile = args[2];
    }else{
      userFile = args[0];
      fileListFile = args[1];
    }
    
    // Create user map
      // user   group
    HashMap<String, String> User2Group = new HashMap();
    try{
      BufferedReader fr = new BufferedReader(new FileReader(new File(userFile))); 
			String line = null; 
			while((line = fr.readLine()) != null){
				// Create string tokenizer 
				StringTokenizer st = new StringTokenizer(line); 
        User2Group.put(st.nextToken(), st.nextToken());
      }
      fr.close();
    } catch(IOException ioe){
      System.out.println("Could not read user list.");
    }
    
    if(makeRoot) User2Group.put("root", "root");
    
    // Create file list 
      // filename owner mode 
    ArrayList<FileObject> fileList = new ArrayList<FileObject>();
    try{
      BufferedReader fr = new BufferedReader(new FileReader(new File(fileListFile))); 
			String line = null; 
			while((line = fr.readLine()) != null){
				// Create string tokenizer 
				StringTokenizer st = new StringTokenizer(line); 
        String name = st.nextToken();
        String owner = st.nextToken();
        int mode = Integer.parseInt(st.nextToken());
        
        fileList.add(new FileObject(name, owner, User2Group.get(owner), mode));
      } // end while
      fr.close();
    }catch(IOException ioe){
      System.out.println("Error reading filelist");
    }
    
    // Create Controller Object 
    Controller cont = new Controller(User2Group, fileList);
    // Create Scanner 
    Scanner scan = new Scanner(System.in);
    // While 1 == 1
    
    while(true){
      StringTokenizer st = new StringTokenizer(scan.nextLine());
      String action = st.nextToken();
      
      if(Controller.matchAction(action) == null) continue;
      
      if(Controller.matchAction(action) == ACTION.EXIT){
        cont.doAction(ACTION.EXIT, null, null, 0);
        scan.close();
        break;
      }
      
      String user =  st.nextToken();
      String file =  st.nextToken();
      int permission  = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : 0;
      cont.doAction(Controller.matchAction(action), user, cont.matchFile(file), permission );
      

    }
      // Ask user input
      // doAction
      // Check for exit 
   
        
  } // end main 
  
} // end class 
