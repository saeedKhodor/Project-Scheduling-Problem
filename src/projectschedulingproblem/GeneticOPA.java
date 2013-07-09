/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package projectschedulingproblem;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 *
 * @author saeed
 */
public class GeneticOPA {

   
    private OperationProjectActivity OriginalOPA;
    DecimalFormat DFormat = new DecimalFormat(MainForm.PrecFormat);
    private float ProbabiltytobPicked;
    private boolean startsafterfeedbackCycle;
    public  static final int UNSTARTED=-1;
    public final static int CANSTART=0;
    public final static int HASSTARTED=1;
    public final static int ISWAITINGFORRESOURCES=2;
    public final static int HASFINISHED=3;
    public final static int ISWAITINGFORFEEDBACKCYCLE=4;
    private int Status=UNSTARTED;
    private float CurrentDuration;

     
     
     
     
     public boolean isthisresourceneeded(String Res){
         
         return OriginalOPA.isthisresourceneeded(Res);
     }
     public float getResourceNeededQtybyName(String Res){
         
         return OriginalOPA.getResourceNeededQtybyName(Res);
     }
    /**
     * @return the startsafterfeedbackCycle
     */
    public boolean isStartsafterfeedbackCycle() {
        return startsafterfeedbackCycle;
    }
public boolean isFeddbackStarterOPA(){
    
    return this.OriginalOPA.isIsFeddbackStarterOPA();
}
    public GeneticOPA(OperationProjectActivity OPA) {
        this.OriginalOPA = OPA;
        ProbabiltytobPicked = getPropability();
        startsafterfeedbackCycle = this.OriginalOPA.isWouldwaitforfeedbackCycle();
        CurrentDuration=getDuration();
    }
public ArrayList<String> getGeneticFCIDs() {

        return this.getOriginalOPA().getGeneticFCIDs();
    }
    public ArrayList<String> getGeneticsFeedbackPredecessors() {

        return this.getOriginalOPA().getGeneticsFeedbackPredecessors();
    }

    public ArrayList<String> getPredOPAIDs() {

        return this.getOriginalOPA().getPredecessorIDs(false);

    }

    public float getDuration() {

        float Dur = this.getOriginalOPA().getDuration();
        return Float.valueOf(DFormat.format(Dur));
    }

    public boolean isfeedback() {

        return this.getOriginalOPA().isIsfeedback();

    }

    public String getOPAIdentifier(boolean Override) {

        return this.getOriginalOPA().getOPAIdentifier(Override);

    }

    private float getPropability() {

        return this.getOriginalOPA().getProbability();

    }

    public String toString(boolean ShortMode) {

        if (ShortMode) {
            return this.getOPAIdentifier(false);
        }
        return this.getOPAIdentifier(false) + " Pb " + this.getProbabiltytobPicked();
    }

    /**
     * @return the ProbabiltytobPicked
     */
    public float getProbabiltytobPicked() {
        return ProbabiltytobPicked;
    }

    /**
     * @param ProbabiltytobPicked the ProbabiltytobPicked to set
     */
    public void setProbabiltytobPicked(float ProbabiltytobPicked) {
        this.ProbabiltytobPicked = ProbabiltytobPicked;
    }

    /**
     * @return the OriginalOPA
     */
    public OperationProjectActivity getOriginalOPA() {
        return OriginalOPA;
    }

    /**
     * @return the Status
     */
    public int getStatus() {
        return Status;
    }

    /**
     * @param Status the Status to set
     */
    public void setStatus(int Status) {
        this.Status = Status;
    }
    public boolean IscanStart(){
        if(this.isfeedback()){
            // cannot start a project with a feedback 
            return false;
        }
        return this.OriginalOPA.getOriginalPA().isCanStart();
    }
    public void ResetGOPA(){
        Status=UNSTARTED;
        setCurrentDuration(getDuration());
        
    }

    /**
     * @return the CurrentDuration
     */
    public float getCurrentDuration() {
        return CurrentDuration;
    }

    /**
     * @param CurrentDuration the CurrentDuration to set
     */
    public void setCurrentDuration(float CurrentDuration) {
        this.CurrentDuration = CurrentDuration;
    }
    public String getFCID(){
        
        if(this.isfeedback()){
            return this.OriginalOPA.getParentFC().getFCID();
        }else{
            return "NoFCID";
        }
    }
    @Override
    public String toString(){
        
        return " OPAName "+this.getOPAIdentifier(false)+" FCID "+getFCID()+" Status "+getStatus();
        
    }
    public OperationProject getOperationProjectParentofGOPA(){
        return this.OriginalOPA.getParent();
    }
}
