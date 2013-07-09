/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package projectschedulingproblem;

import java.util.ArrayList;

/**
 *
 * @author saeed
 */
public class Project {
    /**
     *
     */
private Portfolio PortfolioParent;
private String ProjectName; //will be extracted from the upper left corner 
private Integer TotalNumberofActivities;//this will be the number of activities ( number of rows)
private String sourcefilepath; //this will be the excel file name
//private String StartCell;//this will be inputted by the user in order to  locate the project data( upper left corner)
//private String EndCell;  //this will be inputted by the user in order to locate the project data( lower right corner)  
//private String SheetName;//this will be inputted by the user in order to locate the project data
private ProjectCapsule PCap;
private ArrayList <ProjectActivity> PAs;
private Logger Log;

public Project(){
    
    
    this.ProjectName="Not Named";
    
}

public Project(String ProjectName){
    
    
    this.ProjectName=ProjectName;
    
}
    public String getNextPAsAfter(String Reference){
        String Sa="";
        
      
      Log.appendToLog(Logger.PROCESSING, "Project:getNextPAsAfter: value of the parameter is"+Reference+"the PA Name is "+this.getPAbyRefNum(Reference));
        
        return Sa; 
    }


/**
     *
     * @return
     */
    @Override
    public String toString(){
        return  "Project : " + getProjectName()+"\n"+" Total Number of Activities ="+this.TotalNumberofActivities;
    
    
}
      /**
     * @param name wiich is the PA Name  loaded previously
     * @return PA needed Project Activity
     *  
     */
public ProjectActivity getPAbyName(String Name){
    
    for( ProjectActivity PA : this.PAs){
        
        if(PA.getName().equalsIgnoreCase(Name)){
            return PA;
        }
        
    }
    return null;
}
    /**
     * @param Ref wiich is the reference number loaded previously
     * @return needed Project Ativity
     *  
     */
public ProjectActivity getPAbyRefNum(String Ref){
    Double RefD=Double.parseDouble(Ref);
    for( ProjectActivity PA : this.PAs){
     //   Log.appendToLog(Logger.INFORMATION, "Checking if PA of Ref ="+PA.getProjectReferenceNumber()+" Ref= " + Ref);
        if(Double.parseDouble(PA.getReferenceNumber())==RefD){
            Log.appendToLog(Logger.INFORMATION, " Project :getPAbyRefNum : Found the PA of Ref= " + Ref); 
            return PA;
        }
       
    }
    return null;
}
    /**
     * this si called to return an ArrayList of the Activities that can start
     */
public ArrayList<ProjectActivity> GetCanStartActivities(){
    
    ArrayList<ProjectActivity> PAsStart=new ArrayList<ProjectActivity>();
    
    for(ProjectActivity PA : this.getPAs()){
        
       if(PA.isCanStart()){
           
           PAsStart.add(PA);
       }
    }
    return PAsStart;
}
    /**
     * this is called when all the activities have been created...
     */
    public void allActivitieshavebeenloaded(){
        
        TotalNumberofActivities=this.getPAs().size();
        
    }
    /**
     * @return the PortfolioParent
     */
    public Portfolio getPortfolioParent() {
        return PortfolioParent;
    }

    /**
     * @param PortfolioParent the PortfolioParent to set
     */
    public void setPortfolioParent(Portfolio PortfolioParent) {
        this.PortfolioParent = PortfolioParent;
    }

    /**
     * @return the ProjectName
     */
    public String getProjectName() {
        return ProjectName;
    }

    /**
     * @param ProjectName the ProjectName to set
     */
    public void setProjectName(String ProjectName) {
        this.ProjectName = ProjectName;
    }

    /**
     * @return the TotalNumberofActivities
     */
    public Integer getTotalNumberofActivities() {
        return TotalNumberofActivities;
    }

    /**
     * @param TotalNumberofActivities the TotalNumberofActivities to set
     */
    public void setTotalNumberofActivities(Integer TotalNumberofActivities) {
        this.TotalNumberofActivities = TotalNumberofActivities;
    }

    /**
     * @return the sourcefilepath
     */
    public String getSourcefilepath() {
        return sourcefilepath;
    }

    /**
     * @param sourcefilepath the sourcefilepath to set
     */
    public void setSourcefilepath(String sourcefilepath) {
        this.sourcefilepath = sourcefilepath;
    }

    /**
     * @return the PAs
     */
    public ArrayList <ProjectActivity> getPAs() {
        return PAs;
    }

    /**
     * @param PAs the PAs to set
     */
    public void setPAs(ArrayList <ProjectActivity> PAs) {
        this.PAs = PAs;
    }

    /**
     * @return the PCap
     */
    public ProjectCapsule getPCap() {
        return PCap;
    }

    /**
     * @param PCap the PCap to set
     */
    public void setPCap(ProjectCapsule PCap) {
        this.PCap = PCap;
    }

    /**
     * @return the Log
     */
    public Logger getLog() {
        return Log;
    }

    /**
     * @param Log the Log to set
     */
    public void setLog(Logger Log) {
        this.Log = Log;
    }
}
