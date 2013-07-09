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
public class CPathOPA {
    
    private OperationProjectActivity OPA;
    private boolean canstart=false;// the ones that can start in the beginining 
    private int CpathOPAStatus=OperationProjectActivity.UNSTARTED;
    private float earliestPossibleStartforActivity;
    private float earliestPossibleFinishforActivity;
    private float LatestPossibleStartforActivity;
    private float LatestPossibleFinishforActivity;
    private boolean ESThasbeenset = false;
    private boolean LFThasbeenset = false;
    DecimalFormat DFormat = new DecimalFormat(MainForm.PrecFormat);
    private boolean isVirtual=false;
    private ArrayList<String> VirtualPredNames;
    private String VOPAName="Vitual-End-0326";

    
    
    
    public CPathOPA(OperationProjectActivity OPA){
          this.OPA=OPA;
          this.canstart=OPA.getOriginalPA().isCanStart();
          VirtualPredNames=new  ArrayList<String>();
          if(this.canstart){
              
              earliestPossibleStartforActivity=0.f;
          }
    }
        public CPathOPA(OperationProjectActivity OPA, boolean virtual, ArrayList<String> Preds){
          this.OPA=null;
          this.canstart=false;
          this.isVirtual=true;
          this.VirtualPredNames=new  ArrayList<String>();
          VirtualPredNames.addAll(Preds);
              


    }
    public ArrayList<String> getprednames(){
        if(isVirtual){
            return VirtualPredNames;
            
        }
        return this.OPA.getPredecessorIDs(false);
        
    }      
    public boolean ISCritical(){
        if(isVirtual){
            return false;
        }
        float SlackForward=LatestPossibleStartforActivity-earliestPossibleStartforActivity;
        float SlackBackwards=LatestPossibleFinishforActivity-earliestPossibleFinishforActivity;
        
                if(SlackForward==0.0 ||SlackBackwards==0.0){
                          return true;  
                }
    return false;
        
        
    }
    public float getDuration(){
        if(this.isVirtual){
            return Float.valueOf(DFormat.format(0.0));
        }
        return Float.valueOf(DFormat.format(this.OPA.getCurrentDuration()));
        
    }
 public String  GetCOPAIDentifier(){
     if(isVirtual){
         return VOPAName;
     }
     return this.OPA.getOPAIdentifier(false);
 }

    /**
     * @return the canstart
     */
    public boolean isCanstart() {
        return canstart;
    }

    /**
     * @param canstart the canstart to set
     */
    public void setCanstart(boolean canstart) {
        this.canstart = canstart;
    }

    /**
     * @return the CpathOPAStatus
     */
    public int getCpathOPAStatus() {
        return CpathOPAStatus;
    }

    /**
     * @param CpathOPAStatus the CpathOPAStatus to set
     */
    public void setCpathOPAStatus(int CpathOPAStatus) {
        this.CpathOPAStatus = CpathOPAStatus;
    }

    /**
     * @return the earliestPossibleStartforActivity
     */
    public float getEarliestPossibleStartforActivity() {
        
        return earliestPossibleStartforActivity;
    }

    /**
     * @param earliestPossibleStartforActivity the earliestPossibleStartforActivity to set
     */
    public void setEarliestPossibleStartforActivity(float earliestPossibleStartforActivity) {
        this.earliestPossibleStartforActivity = earliestPossibleStartforActivity;
        this.earliestPossibleFinishforActivity=earliestPossibleStartforActivity+this.getDuration();
        
        earliestPossibleFinishforActivity=Float.valueOf(DFormat.format(earliestPossibleFinishforActivity));
        this.ESThasbeenset=true;
    }

    /**
     * @return the earliestPossibleFinishforActivity
     */
    public float getEarliestPossibleFinishforActivity() {
        return earliestPossibleFinishforActivity;
    }

    /**
     * @param earliestPossibleFinishforActivity the earliestPossibleFinishforActivity to set
     */
    public void setEarliestPossibleFinishforActivity(float earliestPossibleFinishforActivity) {
        this.earliestPossibleFinishforActivity = earliestPossibleFinishforActivity;
    }

    /**
     * @return the LatestPossibleStartforActivity
     */
    public float getLatestPossibleStartforActivity() {
        return LatestPossibleStartforActivity;
    }

    /**
     * @param LatestPossibleStartforActivity the LatestPossibleStartforActivity to set
     */
    public void setLatestPossibleStartforActivity(float LatestPossibleStartforActivity) {
        this.LatestPossibleStartforActivity = LatestPossibleStartforActivity;
    
        
    }

    /**
     * @return the LatestPossibleFinishforActivity
     */
    public float getLatestPossibleFinishforActivity() {
        return LatestPossibleFinishforActivity;
    }

    /**
     * @param LatestPossibleFinishforActivity the LatestPossibleFinishforActivity to set
     */
    public void setLatestPossibleFinishforActivity(float LatestPossibleFinishforActivity) {
        
        this.LatestPossibleFinishforActivity = LatestPossibleFinishforActivity;
        LatestPossibleFinishforActivity = Float.valueOf(DFormat.format(LatestPossibleFinishforActivity));
        this.LatestPossibleStartforActivity = LatestPossibleFinishforActivity - getDuration();
        LatestPossibleStartforActivity = Float.valueOf(DFormat.format(LatestPossibleStartforActivity));
        this.LFThasbeenset = true;
    
    
    }
    @Override
    public String toString(){
        
        return this.GetCOPAIDentifier() +" EST "+earliestPossibleStartforActivity + " EFT "+earliestPossibleFinishforActivity+" LST "+LatestPossibleStartforActivity +" LFT "+LatestPossibleFinishforActivity + " Duration "+this.getDuration();
    
}
    /**
     * @return the ESThasbeenset
     */
    public boolean isESThasbeenset() {
        return ESThasbeenset;
    }

    /**
     * @param ESThasbeenset the ESThasbeenset to set
     */
    public void setESThasbeenset(boolean ESThasbeenset) {
        this.ESThasbeenset = ESThasbeenset;
    }

    /**
     * @return the LFThasbeenset
     */
    public boolean isLFThasbeenset() {
        return LFThasbeenset;
    }

    /**
     * @param LFThasbeenset the LFThasbeenset to set
     */
    public void setLFThasbeenset(boolean EFThasbeenset) {
        this.LFThasbeenset = EFThasbeenset;
    }

    /**
     * @return the isVirtual
     */
    public boolean isIsVirtual() {
        return isVirtual;
    }

    /**
     * @param isVirtual the isVirtual to set
     */
    public void setIsVirtual(boolean isVirtual) {
        this.isVirtual = isVirtual;
    }
 
 
}
