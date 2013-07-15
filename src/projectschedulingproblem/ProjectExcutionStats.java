/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package projectschedulingproblem;

import java.text.DecimalFormat;

/**
 *
 * @author saeed
 */
public class ProjectExcutionStats {
    
    public static int UNTOUCHED=0;
    public static int STARTED=1;
    public static int FINISHED=2;
    
    OperationProject ParentOP;
    private String ProjectID="";
    private int PESStatus;
    private float ProjectStartTime=0.0f;
    private float ProjectEndTime=0.0f;
    
    public ProjectExcutionStats( OperationProject POP) {
        
        this.ParentOP=POP;
        this.ProjectID=POP.getProjectName();
        PESStatus=ProjectExcutionStats.UNTOUCHED;     
    }
public ProjectExcutionStats() {

        PESStatus=ProjectExcutionStats.UNTOUCHED;     
    }
    /**
     * @return the ProjectID
     */
    public String getProjectID() {
        return ProjectID;
    }

    /**
     * @param ProjectID the ProjectID to set
     */
    public void setProjectID(String ProjectID) {
        this.ProjectID = ProjectID;
    }


    /**
     * @return the ProjectStartTime
     */
    public float getProjectStartTime() {
        return ProjectStartTime;
    }

    /**
     * @param ProjectStartTime the ProjectStartTime to set
     */
    public void setProjectStartTime(float ProjectStartTime) {
        this.ProjectStartTime = ProjectStartTime;
    }

    /**
     * @return the ProjectEndTime
     */
    public float getProjectEndTime() {
        return ProjectEndTime;
    }

    /**
     * @param ProjectEndTime the ProjectEndTime to set
     */
    public void setProjectEndTime(float ProjectEndTime) {
        this.ProjectEndTime = ProjectEndTime;
    }

    /**
     * @return the PESStatus
     */
    public int getPESStatus() {
        return PESStatus;
    }

    /**
     * @param PESStatus the PESStatus to set
     */
    public void setPESStatus(int PESStatus) {
        this.PESStatus = PESStatus;
    }
    public float getDuration(){
        
        return ProjectEndTime-ProjectStartTime;
        
    }
    public String getDelay(){
        
        float Delay=getDuration()-ParentOP.getCPCapsule().getTotalDuration();
         DecimalFormat DFormat = new DecimalFormat(MainForm.PrecFormat);
        return Float.valueOf(DFormat.format(Delay)).toString();
         
    }
    @Override
    public String toString(){
        
        return "Pname "+ProjectID+" ST "+ProjectStartTime+ " ET "+ProjectEndTime + " Duration "+getDuration()+" Criticalpath Dur= "+ParentOP.getCPCapsule().getTotalDuration();
    }
    
    
}
