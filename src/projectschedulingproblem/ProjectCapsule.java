/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package projectschedulingproblem;

/**
 *
 * @author saeed
 */
public class ProjectCapsule {
    private String DSMSheetname;
    private String DSMStartCell;
    private String DSMEndCell;

    
    private String ActivitiesSheetname;
    private String ActivitiesStartCell;
    private String ActivitiesEndCell;
    
     private String ReworkSheetname;
    private String ReworkStartCell;
    private String ReworkEndCell;
   private Logger Log;
    public ProjectCapsule(Logger log){
        this.Log=log;
        this.Log.appendToLog(Logger.INFORMATION, " Project Capsule has been Created");
    }
    /**
     * @return the DSMSheetname
     */
    public String getDSMSheetname() {
        return DSMSheetname;
    }

    /**
     * @param DSMSheetname the DSMSheetname to set
     */
    public void setDSMSheetname(String DSMSheetname) {
        this.DSMSheetname = DSMSheetname;
    }

    /**
     * @return the DSMStartCell
     */
    public String getDSMStartCell() {
        return DSMStartCell;
    }

    /**
     * @param DSMStartCell the DSMStartCell to set
     */
    public void setDSMStartCell(String DSMStartCell) {
        this.DSMStartCell = DSMStartCell;
    }

    /**
     * @return the DSMEndCell
     */
    public String getDSMEndCell() {
        return DSMEndCell;
    }

    /**
     * @param DSMEndCell the DSMEndCell to set
     */
    public void setDSMEndCell(String DSMEndCell) {
        this.DSMEndCell = DSMEndCell;
    }

    /**
     * @return the ActivitiesSheetname
     */
    public String getActivitiesSheetname() {
        return ActivitiesSheetname;
    }

    /**
     * @param ActivitiesSheetname the ActivitiesSheetname to set
     */
    public void setActivitiesSheetname(String ActivitiesSheetname) {
        this.ActivitiesSheetname = ActivitiesSheetname;
    }

    /**
     * @return the ActivitiesStartCell
     */
    public String getActivitiesStartCell() {
        return ActivitiesStartCell;
    }

    /**
     * @param ActivitiesStartCell the ActivitiesStartCell to set
     */
    public void setActivitiesStartCell(String ActivitiesStartCell) {
        this.ActivitiesStartCell = ActivitiesStartCell;
    }

    /**
     * @return the ActivitiesEndCell
     */
    public String getActivitiesEndCell() {
        return ActivitiesEndCell;
    }

    /**
     * @param ActivitiesEndCell the ActivitiesEndCell to set
     */
    public void setActivitiesEndCell(String ActivitiesEndCell) {
        this.ActivitiesEndCell = ActivitiesEndCell;
    }

    /**
     * @return the ReworkSheetname
     */
    public String getReworkSheetname() {
        return ReworkSheetname;
    }

    /**
     * @param ReworkSheetname the ReworkSheetname to set
     */
    public void setReworkSheetname(String ReworkSheetname) {
        this.ReworkSheetname = ReworkSheetname;
    }

    /**
     * @return the ReworkStartCell
     */
    public String getReworkStartCell() {
        return ReworkStartCell;
    }

    /**
     * @param ReworkStartCell the ReworkStartCell to set
     */
    public void setReworkStartCell(String ReworkStartCell) {
        this.ReworkStartCell = ReworkStartCell;
    }

    /**
     * @return the ReworkEndCell
     */
    public String getReworkEndCell() {
        return ReworkEndCell;
    }

    /**
     * @param ReworkEndCell the ReworkEndCell to set
     */
    public void setReworkEndCell(String ReworkEndCell) {
        this.ReworkEndCell = ReworkEndCell;
    }
    
    @Override
    public String toString(){
        return  "The Project Capsule includes DSMInformation of  " + this.getDSMSheetname()+" ' "+ this.getDSMStartCell()+" ' "+ this.getDSMEndCell()+ " and "+" ' "+ this.getActivitiesSheetname()+" ' "+ this.getActivitiesStartCell()+" ' "+ this.getActivitiesEndCell() + " and "+" ' "+ this.getReworkSheetname() +" ' "+ this.getReworkStartCell() +" ' "+ this.getReworkEndCell();      
        
    }
}
