/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package projectschedulingproblem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author saeed
 */
public class FeedbackCycle {
    
    private ArrayList<OperationProjectActivity> Feedbacks;
    private String StartOPAName;
    private String EndOPAName;
    private OperationProjectActivity ENDOPA;
    private OperationProjectActivity STARTOPA;
    private Logger Log;
    private String Postseccors = "";
    private boolean isFinished = false;
    private Integer FCLevel = 0;
    private FeedbackManager FDM;
    private ArrayList<String> RemovedOPAs;
    
    public FeedbackCycle(FeedbackManager fdm, OperationProjectActivity SOPA, OperationProjectActivity EOPA, Logger log, String StartOPAname, String EndOPAname, int FCLevel) {
        
        Feedbacks = new ArrayList<OperationProjectActivity>();        
        this.Log = log;
        //   EndOPAName=SOPA.getOPAIdentifier(false);
        this.FDM = fdm;
        this.STARTOPA = SOPA;
        this.ENDOPA = EOPA;
        this.StartOPAName = StartOPAname;
        this.EndOPAName = EndOPAname;
        Log.appendToLog(Logger.PROCESSING, "FeedbackCycle:Constructor: Feedback Cycle has been created of FCID " + getFCID());
        RemovedOPAs = new ArrayList<String>();
        
    }

    // will iterate the rework Records and get the iteger of number of reworks ,
    /**
     * @return the Feedbacks
     */
    public ArrayList<OperationProjectActivity> getFeedbacks() {
        return Feedbacks;
    }

    /**
     * @param Feedbacks the Feedbacks to set
     */
    public void setFeedbacks(ArrayList<OperationProjectActivity> Feedbacks) {
        this.Feedbacks = Feedbacks;
    }

    /**
     * @return the StartOPAName
     */
    public String getStartOPA() {
        return StartOPAName;
    }

    /**
     * @param StartOPAName the StartOPAName to set
     */
    public void setStartOPA(String StartOPA) {
        this.StartOPAName = StartOPA;
    }

    /**
     * @return the EndOPAName
     */
    public String getEndOPA() {
        return EndOPAName;
    }

    /**
     * @param EndOPAName the EndOPAName to set
     */
    public void setEndOPA(String EndOPA) {
        this.EndOPAName = EndOPA;
    }
    
    public void filloutPostsecors() {
        String POSTSECSORS = "";        
        for (OperationProjectActivity OPA : this.FDM.getOPP().getPool()) {
            ArrayList<String> Al = OPA.getOriginalPA().getPredecesorsIDsPAIdentfiers();
            if (Al.contains(this.EndOPAName)) {
                Log.appendToLog(Logger.INFORMATION, "FeedbackCycle:filloutPostsecors:" + OPA.getOPAIdentifier(false) + "  " + this.EndOPAName);
                
                POSTSECSORS += OPA.getOPAIdentifier(false) + ",";
            }
            
        }
        Log.appendToLog(Logger.INFORMATION, "FeedbackCycle:filloutPostsecors: the feedback will be a predecessor for the following OPA" + POSTSECSORS);        
    }
    // this function will analyze the interval recived from the operation project and check which activities should be running ...

    // this is called by the OPP to check if the fc has finished 
    public boolean UpdateFinishedStatus() {
        
        boolean isfinished = true;
        
        for (OperationProjectActivity Fb : this.Feedbacks) {
            
            
            if (Fb.getStatus() != OperationProjectActivity.HASFINISHED) {
                isfinished = false;
                
            }
        }
        this.setIsFinished(isfinished);
        return isfinished;
        
    }

    // this will be called by 
    public boolean arePredecessorsfinished(OperationProjectActivity OPA) {
        boolean hasfinished = true;
        ArrayList<String> pred = OPA.getPredecessorIDs(false);
        
        for (OperationProjectActivity OPAF : Feedbacks) {
            if (pred.contains(OPAF.getOPAIdentifier(false))) {
                if (OPAF.getStatus() != OperationProjectActivity.HASFINISHED) {
                    hasfinished = false;
                    break;
                }
                
            }
            // here we check if the predecessor have finished
            
            
        }
        return hasfinished;
    }

    public boolean isFCPredessorforOPA(String OPA) {
        // this will be called by the OPP to check if the OPA it is checking has this FC a predessor
        String[] Split = this.Postseccors.split(",");
        for (int i = 0; i < Split.length; i++) {
            
            if (Split[i].equals(OPA)) {
                return true;
            }
            
        }
        return true;
    }

    public void fixPredessorForOPAF(OperationProjectActivity OPAF) {
        
        ArrayList<String> FPred = OPAF.getFeedbackPredecessors();
        ArrayList<String> OPreds = OPAF.getPredecessorIDs(true);
        // added on 5/4/2013
        
        if (this.StartOPAName.equals(OPAF.getOPAIdentifier(true))) {
            int current = this.FDM.getnumberofReworksforPA(this.getSTARTOPA(), this.getFCID(), false);
                
            Log.appendToLog(Logger.INFORMATION, "FeedbackCycle:fixPredessorForOPAF: Finding the StartOPA is easy StartOPAName" + StartOPAName + " OPAF NAME " + OPAF.getOPAIdentifier(true) + "OPAF NAME" + OPAF.getOPAIdentifier(false));
            Log.appendToLog(Logger.INFORMATION, "FeedbackCycle:fixPredessorForOPAF: Current Level of EndOPA " + this.getENDOPA().getOPAIdentifier(false) + " " + this.EndOPAName + " is " + current + " fake name=");
            
            if (current == -1) {
                Log.appendToLog(Logger.INFORMATION, "FeedbackCycle:fixPredessorForOPAF: Current Level of EndOPA " + this.EndOPAName + " is " + current + " fake name=");
                
                
            } else {
                if(current==1){
                     OPAF.setIsFeddbackStarterOPA(true);// for the genetics 5/6/2013 this will be only for the first time feedbackkersince they have a normal Pred not feedback
                }
                String FakeEndOPAName = this.getENDOPA().getOPAIdentifierFake(current - 1);
                
                if (!FPred.contains(FakeEndOPAName)) {
                    FPred.add(FakeEndOPAName);
                }
            }
            
        } else {
            for (String s : OPreds) {
                for (OperationProjectActivity fb : this.Feedbacks) {
                    String OName = fb.getOPAIdentifier(true);
                    String FName = fb.getOPAIdentifier(false);
                    if (s.equals(OName)) {
                        if (!FPred.contains(FName)) {
                            FPred.add(FName);
                        } else {
                            Log.appendToLog(Logger.INFORMATION, "FeedbackCycle:fixPredessorForOPAF: skipping  " + " OName " + FName);                            
                            
                        }

                        //Log.appendToLog(Logger.INFORMATION,"FeedbackCycle:fixPredessorForOPAF: was called with OPAF = "+OPAF.getOPAIdentifier(false)+" OName "+OName +" Fname "+FName );  
                    } else {
                        //  Log.appendToLog(Logger.INFORMATION,"FeedbackCycle:fixPredessorForOPAF: skipping  "+" OName "+OName );  
                        
                        
                    }
                }
                
            }
            // added 
            if(OPAF.getNumberofPred()==0){
                String FName = STARTOPA.getOPAIdentifier(false);
                
            }
        }
        ArrayList<String> FPreds = OPAF.getPredecessorIDs(false);
        String x = OPAF.getOPAIdentifier(false)+" Isfeedback "+OPAF.isIsfeedback()+" woudlwait for fc "+OPAF.isWouldwaitforfeedbackCycle();
        for (String s : FPreds) {
            x += (s + " , ");
        }
        Log.appendToLog(Logger.INFORMATION, "FeedbackCycle:fixPredessorForOPAF: Predessors for OPAF after Fixation " + x);        
    }
    
    @Override
    public String toString() {
        String feedbacks = "";
        for (OperationProjectActivity OPAF : Feedbacks) {
            
            feedbacks += OPAF.getOPAIdentifier(false) + "/n";
            
        }
        return feedbacks;
        
        
    }

    /**
     * @return the isFinished
     */
    public boolean isIsFinished() {
        return isFinished;
    }

    /**
     * @param isFinished the isFinished to set
     */
    public void setIsFinished(boolean isFinished) {
        this.isFinished = isFinished;
    }

    /**
     * @return the FCID
     */
    public String getFCID() {
        return StartOPAName + "|" + EndOPAName;
    }

    /**
     * @param FCID the FCID to set
     */
    public ArrayList<OperationProjectActivity> createOPAFs(ArrayList<OperationProjectActivity> Interval,boolean UsedForgenetics) {
// will remove any OPAs that have reached the max number fo reworks
        for (int i = 0; i < Interval.size(); i++) {
            int current = this.FDM.getnumberofReworksforPA(Interval.get(i), this.getFCID(), false);
            
            int Maxreworksforsopa = (int)Interval.get(i).getmaxnumberofreworks();
            if (current >= Maxreworksforsopa) {
                this.RemovedOPAs.add(Interval.get(i).getOPAIdentifier(true));
                Log.appendToLog(Logger.INFORMATION, " FeedbackCycle:createOPAFs: Removed " + Interval.get(i).getOPAIdentifier(true));
            }
        }
        // remove the rework probability is finshed here
        
        for (int i = 0; i < Interval.size(); i++) {
            HashMap<String, Double> HM = Interval.get(i).getOriginalPA().getProbabilityofbeingworkedinFeedbackPAIdentifiers();
            if (HM.size() > 0) {
                
                Iterator itRWfeed = HM.entrySet().iterator();
                
                while (itRWfeed.hasNext()) {
                    Map.Entry<String, Double> pairsRWF = (Map.Entry) itRWfeed.next();
                    String PA = pairsRWF.getKey();
                    double Val = pairsRWF.getValue();
                    if(UsedForgenetics){
                        Val=1.0;
                    }
                    if (!RemovedOPAs.contains(PA)) {
                        if (!FeedbackManager.FeedbackFlipaCoin(Val)) {
                            
                            RemovedOPAs.add(Interval.get(i).getOPAIdentifier(true));
                            Log.appendToLog(Logger.INFORMATION, " FeedbackCycle:createOPAFs: Removed for rework probability val  " + Val + " OPAID " + Interval.get(i).getOPAIdentifier(true));
                        }
                    }
                }
                
            }
            
        }
        
        //added 6/5/2013 after removing based ont the probability and max number of reworks we need to check of any of the removed OPA is a predecessor 
        for (int i = 0; i < Interval.size(); i++) {
            OperationProjectActivity OPA = Interval.get(i);
            if(!this.RemovedOPAs.contains(OPA.getOPAIdentifier(true))&& !OPA.getOPAIdentifier(true).equals(this.StartOPAName)){
                ArrayList<String> OPreds = OPA.getPredecessorIDs(true);
                for (String s : OPreds) {
                    if(this.RemovedOPAs.contains(s)){
                        boolean add;
                        add = this.RemovedOPAs.add(OPA.getOPAIdentifier(true));
                    }
                }

            }

        }
         //end of added 6/5/2013 aft
        //create OPAF
        for (int i = 0; i < Interval.size(); i++) {
            //for(OperationProjectActivity OPA:AL){
            OperationProjectActivity OPA = Interval.get(i);
            boolean wasremoved = this.RemovedOPAs.contains(OPA.getOPAIdentifier(true));
            if(wasremoved){
                continue;
            }
            if (hasAnypredbeenRemoved(OPA,Interval) ) {
                Log.appendToLog(Logger.INFORMATION, " FeedbackCycle:createOPAFs: OPA " + OPA.getOPAIdentifier(false) + "has been removed since a predecssor has been removed");
                
                
            }else{
            OperationProjectActivity OPAF = new OperationProjectActivity(OPA.getOriginalPA(), this.FDM.getOPP());
            Integer Reqorknumber = this.FDM.AddOnetonumberofReworksforPA(OPA, this.getFCID());
            if (Reqorknumber != -1) {
                Log.appendToLog(Logger.INFORMATION, " FeedbackCycle:createOPAFs: all seems good for add 1 to the Reworkrecord"+ " rek Record "+Reqorknumber+OPA.getOPAIdentifier(false));
                
            } else {
                
                Log.appendToLog(Logger.INFORMATION, " FeedbackCycle:createOPAFs: not a good sign in adding 1 to the Reworkrecord");
                
            }
            
            OperationProjectActivity Oopa = this.FDM.getOPP().getOPAbyPAIDentifier(OPA.getOPAIdentifier(true), false);
            Log.appendToLog(Logger.INFORMATION, " FeedbackCycle:createOPAFs: getting duration from " + Oopa.getOPAIdentifier(false) + " DUR " + Oopa.getDuration());
            // Log.appendToLog(Logger.INFORMATION, " FeedbackCycle:createOPAFs: getting duration from "+ Oopa.getOPAIdentifier(false)+" DUR "+Oopa.getDuration()); 
            OPAF.setFeedbackDepth(new Double(Reqorknumber).toString());// this is imporants  for the getOPAIdentifier
            OPAF.modifyCurrentDuration(Reqorknumber);
            /////////////* this.fixPredessorForOPAF(OPAF); this was removed and moved down on 4-5-2013
            //Log.appendToLog(Logger.INFORMATION," FeedbackCycle:analyzeTempOPAs: after adding number of reworks "+this.getnumberofReworksforPA(OPA.getOPAIdentifier(false)));
            OPAF.setIsfeedback(true);
            
            OPAF.setFPAName(OPAF.getOPAIdentifier(false));
            OPAF.setParentFC(this);
            
            this.Feedbacks.add(OPAF);

            //this.FDM.getOPP().getPool().add(OPAF)
            // here add the OPAF to pool... and to the feebacks array list 
            // ;
            // Log.appendToLog(Logger.INFORMATION, " FeedbackCycle:analyzeTempOPAs: after adding number of reworks " + OPAF.getOPAIdentifier(false) + "if override is true" + OPAF.getOPAIdentifier(true));
            }
        }
        for (OperationProjectActivity fb : Feedbacks) {
            
            this.fixPredessorForOPAF(fb);
            
        }
        
        return this.Feedbacks;
    }
    
    public boolean hasAnypredbeenRemoved(OperationProjectActivity OPA,ArrayList<OperationProjectActivity> interval) {
        ArrayList<String> OPreds = OPA.getPredecessorIDs(true);
        
        
        for (String s : OPreds) {
            if (this.RemovedOPAs.contains(s)) {
                return true;
            }
        }
        
       
        return false;
    }

    /**
     * @return the ENDOPA
     */
    public OperationProjectActivity getENDOPA() {
        return ENDOPA;
    }

    /**
     * @param ENDOPA the ENDOPA to set
     */
    public void setENDOPA(OperationProjectActivity ENDOPA) {
        this.ENDOPA = ENDOPA;
    }

    /**
     * @return the STARTOPA
     */
    public OperationProjectActivity getSTARTOPA() {
        return STARTOPA;
    }

    /**
     * @param STARTOPA the STARTOPA to set
     */
    public void setSTARTOPA(OperationProjectActivity STARTOPA) {
        this.STARTOPA = STARTOPA;
    }
    public boolean doesFOPAexistinFCs(String FOPANAme){
        
        for(OperationProjectActivity OPA:Feedbacks){
        if(OPA.getOPAIdentifier(false).equals(FOPANAme)){
            return true;
        }
    }
        return false;
    }
}
