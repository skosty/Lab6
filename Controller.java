import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

public class Controller {
  
  private Map<String,String> User2Group;
  public List<FileObject> FileList;  
  
   Controller(Map<String,String> User2Group, List<FileObject> fileList){
     this.User2Group = User2Group;
     this.FileList = fileList;
   } // end Constructor 
 
  public static int oct2int(int oct){
    return Integer.parseInt(Integer.toString(oct), 8);
  } // end oct2int 
  
  public static int int2oct(int dec){
    return Integer.valueOf(Integer.toOctalString(dec), 10);
  }
  
  
  private boolean hasPermission(ACTION act, String user, String group, FileObject file){
    
    if(act == ACTION.EXIT) return true;
    if(user.equalsIgnoreCase("root") || group.equalsIgnoreCase("root")) return true;
    
    //  11 10 9   8 7 6   5 4 3   2 1 0 
    switch(act){
      
      case READ: 
        if(file.getOwner().equalsIgnoreCase(user) && isBitLit(file.getPermission(), 8)){
          return true;
        } // User owns it
        else if(file.getGroup().equalsIgnoreCase(group) && isBitLit(file.getPermission(), 5)){
          return true;
        } // Group owns it 
        else if( isBitLit(file.getPermission(), 2)){
          return true;
        } // Everyone access 

        return false;
        
      case WRITE:
        if(file.getOwner().equalsIgnoreCase(user) && isBitLit(file.getPermission(), 7)){
          return true;
        } // User owns it
        else if(file.getGroup().equalsIgnoreCase(group) && isBitLit(file.getPermission(), 4)){
          return true;
        } // Group owns it 
        else if( isBitLit(file.getPermission(), 1)){
          return true;
        } // Everyone access 

        return false;
        
      case EXECUTE:
        if(file.getOwner().equalsIgnoreCase(user) && isBitLit(file.getPermission(), 6)){
          return true;
        } // User owns it
        else if(file.getGroup().equalsIgnoreCase(group) && isBitLit(file.getPermission(), 4)){
          return true;
        } // Group owns it 
        else if( isBitLit(file.getPermission(), 0)){
          return true;
        } // Everyone access 

        return false;
        
      case CHMOD: 
        if(file.getOwner().equalsIgnoreCase(user)) return true;
        
      case EXIT: return true;
      
      default:
        System.out.println("Action unrecognized");
        return false;
      
    } // end switch
   
  } // end hasPermission 
 
  public static boolean isBitLit(int i, int bit){
    return ((i & (1 << bit)) != 0);
  } // isBitLit
  
  public void doAction(ACTION act, String user, FileObject file, int permission){
    
    if(act == ACTION.EXIT ){
      writeStatusToFile();
      return;
    }
    
    // If user or file not known
    if( ! User2Group.keySet().contains(user) ){
      System.out.println("Unknown user " + user + "." );
      return;
    }
    if(file == null){
      System.out.println("Unknown file.");
      return;
    }
    
    
    if( ! hasPermission(act, user, User2Group.get(user), file)){
      printStatus(act, user, file);
      return;
    } // if user does not have permission 
    
    switch(act){
      case READ:
        file.setRunningUser(user);
        file.setRunningGroup(User2Group.get(user));
        printStatus(act,user,file);
        break;
      case WRITE:
        file.setRunningUser(user);
        file.setRunningGroup(User2Group.get(user));
        printStatus(act,user,file);
        break;
      case EXECUTE:
        if(isBitLit(file.getPermission(),11)){
          file.setRunningUser(file.getOwner());
        }else{
          file.setRunningUser(user);
        } // if setUID bit lit
        if(isBitLit(file.getPermission(),10)){
          file.setRunningGroup(User2Group.get(file.getOwner()));
        }else{
          file.setRunningGroup(User2Group.get(user));
        }  // if setGID bit lit  
        printStatus(act,user,file);
        break;
      case CHMOD: 
        file.setRunningUser(user);
        file.setRunningGroup(User2Group.get(user));
        if(permission == 0){
          System.out.println("Unrecognized mode format. Using default permissions: 0700");
          permission = 700;
        }
        file.setPermission(permission);
        printStatus(act,user,file);
        break;
      case EXIT:
        writeStatusToFile();
        // OUTPUT TO FILE
        break;
      
    } // end switch
    
  } // end doAction
  
  
  private void printStatus(ACTION act, String user, FileObject file){
    
    String status;
    switch(act){
      case READ:
        status = hasPermission(act, user, User2Group.get(user), file) ? "1" : "0";
        System.out.println("READ " + file.getRunningUser() + " " + file.getRunningGroup() + " " + status);
        break;
      case WRITE:
        status = hasPermission(act, user, User2Group.get(user), file) ? "1" : "0";
        System.out.println("WRITE " + file.getRunningUser() + " " + file.getRunningGroup() + " " + status);
        break;
      case EXECUTE:
        status = hasPermission(act, user, User2Group.get(user), file) ? "1" : "0";
        System.out.println("EXECUTE " + file.getRunningUser() + " " + file.getRunningGroup() + " " + status);
        break;
      case CHMOD:
        status = hasPermission(act, user, User2Group.get(user), file) ? "1" : "0";
        System.out.println("CHMOD " + file.getRunningUser() + " " + file.getRunningGroup() + " " + status);
        break;
      case EXIT: break;
        // OUTPUT TO FILE
      
    } // end switch
    
    
  } // end printStatus
  
  
  public void writeStatusToFile(){
    
    try{
    	File outFile = new File("state.log");
      PrintWriter pw = new PrintWriter(new FileOutputStream(outFile, true));
      for(FileObject f : FileList){
        
        pw.write(getModeString(f) + " " + f.getOwner() + " " + f.getGroup() + " " + f.getName() + "\n");

      } // end for each loop 
      pw.flush();
      pw.close();
      System.out.println("Status successfully written to " + outFile.getAbsolutePath() );
    }catch(IOException ioe){
      System.out.println("Error writing exit status to file....");
    }
    
    
  } // end writeStatusToFile
  
  
  public static String getModeString(FileObject file){
    StringBuilder mode = new StringBuilder("---------");
    //            0 1 2       3 4 5     6 7 8     
    // 11 10 9    8 7 6       5 4 3     2 1 0 
    
    
    
    for(int i = 8; i >= 0; i--){
      if(isBitLit(file.getPermission(), i)){ // bit is lit 
        // 8 - i
        int rem = (8 - i) % 3;
        switch(rem){
          case 0: // READ
            mode.setCharAt(8 - i, 'r');
            break;
          case 1: // WRITE
            mode.setCharAt(8-i, 'w'); 
            break;
          case 2: // EXECUTE
            mode.setCharAt(8-i, 'x');
            break;
        } // end switch 
        
      } // bit is lit 
    } // for Loop 
    
    // Check for special bits 
    // Owner 
    if( isBitLit(file.getPermission(), 11)){
      if( isBitLit(file.getPermission(), 6)){
        mode.setCharAt(2, 's');
      }else{
        mode.setCharAt(2, 'S');
      }
    } 
    // Group 
    if( isBitLit(file.getPermission(), 10)){
      if( isBitLit(file.getPermission(), 3)){
        mode.setCharAt(5, 's');
      }else{
        mode.setCharAt(5, 'S');
      }
    } 
    // Executable 
    if( isBitLit(file.getPermission(), 9)){
      if( isBitLit(file.getPermission(), 0)){
        mode.setCharAt(8, 't');
      }else{
        mode.setCharAt(8, 'T');
      }
    }
    
    
//    // Read bits
//    for(int i = 0; i < 7; i+=3){
//      // 11 - i 
//      if(isBitLit(file.getPermission(), 8 - i)) mode.setCharAt(i, 'r');
//    } // read bits 
//    
//    // Write bits 
//    for(int i = 1; i < 8; i+=3){
//      // 11 - i 
//      if(isBitLit(file.getPermission(), 8 - i)) mode.setCharAt(i, 'w');
//    } // write bits 
//    
//    
//    // Owner executable bit 
//      // Is set-UserID-bit is set
//        // Is NOT Executable 
//          // S (big)
//        // Is Executable
//          // s (small)
//      // set-UserID-bit is NOT set
//        // Is Executable 
//          // x
//        // Is Not Executable
//          // -
//    if( isBitLit(file.getPermission(), 11)){ // set-UserID lit
//      if( isBitLit(file.getPermission(), 6)){
//        mode.setCharAt(2, 's');
//      } else{
//        mode.setCharAt(2, 'S');
//      }
//    }else{
//      
//      if( isBitLit(file.getPermission(), 6)){
//        mode.setCharAt(2, 'x');
//      } 
//      
//    } // Owner executable bit 
//    
//    
//    
//    // Group Executable 
//    //            0 1 2       3 4 5     6 7 8     
//    // 11 10 9    8 7 6       5 4 3     2 1 0 
//    
//    if( isBitLit(file.getPermission(), 10)){ // set-GroupID lit
//      if( isBitLit(file.getPermission(), 3)){
//        mode.setCharAt(5, 's');
//      } else{
//        mode.setCharAt(5, 'S');
//      }
//    }else{
//      
//      if( isBitLit(file.getPermission(), 3)){
//        mode.setCharAt(5, 'x');
//      } 
//      
//    } // Group executable bit 
//    
//    
//    // Other Executable 
//    if( isBitLit(file.getPermission(), 9)){ // if sticky bit lit 
//      if(isBitLit(file.getPermission(), 0)){ // other executable
//        mode.setCharAt(8, 't');
//      }else{
//        mode.setCharAt(8, 'T');
//      }
//    } else{
//      if(isBitLit(file.getPermission(), 0)){ // other executable
//        mode.setCharAt(8, 'x');
//      }
//    }
//    
//    
//    
    return mode.toString();
    
  }
  
  public static ACTION matchAction(String act){
    if(act.equalsIgnoreCase("read")) return ACTION.READ;
    else if(act.equalsIgnoreCase("write")) return ACTION.WRITE;
    else if(act.equalsIgnoreCase("execute")) return ACTION.EXECUTE;
    else if(act.equalsIgnoreCase("chmod")) return ACTION.CHMOD;
    else if(act.equalsIgnoreCase("exit")) return ACTION.EXIT;
    
    System.out.println("Unrecognized action in matchAction");
    return null;
  }
  
  
  public FileObject matchFile(String file){
    for(FileObject f : FileList){
      if(file.equalsIgnoreCase(f.getName())) return f;
    }
    
    return null;
  }
  
} // end Controller 
