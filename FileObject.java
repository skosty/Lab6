/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 */
public class FileObject {
  
  String name;
  String owner;
  String group;
  int permission; 
  String running_user;
  String running_group;
  
  
  public FileObject(String name, String owner, String group, int permission){
    this.name = name;
    this.owner = owner;
    this.group = group;
    this.permission = Controller.oct2int(permission); 
  } // end Constructor 
  
  //************ Getters and Setters 
  
  
  
  public void setOwner(String owner){
    this.owner = owner;
  }
  
  public void setGroup(String group){
    this.group = group;
  }
  
  public void setPermission(int permission){
    this.permission = Controller.oct2int(permission);
  }
  
  public void setRunningUser(String running_user){
    this.running_user = running_user;
  }
  
  public void setRunningGroup(String running_group){
    this.running_group = running_group;
  }
  
  public String getName(){
    return name;
  }
  
  public String getOwner(){
    return owner;
  }
  
  public String getGroup(){
    return this.group;
  }
  
  public int getPermission(){
    return permission;
  }
  
  public String getRunningGroup(){
    return this.running_group;
  }
  
  public String getRunningUser(){
    return this.running_user;
  }
  
  
  //************ 
  
  
  
  
} // end FileObject 
