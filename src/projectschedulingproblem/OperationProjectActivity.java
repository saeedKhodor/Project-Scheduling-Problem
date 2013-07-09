/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package projectschedulingproblem;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/**
 *
 * @author saeed
 */
public class OperationProjectActivity {
    private ProjectActivity OriginalPA;
    private OperationProject Parent;
  //feedback variables
    private boolean wouldwaitforfeedbackCycle=false;
    private ArrayList <String> GeneticsFeedbackPredecessors;// will hold the interval of feedbacks 
    private ArrayList<String> GeneticFCIDs;
    
    private boolean isfeedback=false;
    private String feedbackDepth="";// will be set when the feebackcycle is setup and would be checked up to see the lowest depth 
    private String FPAName="";// new name for the PA after feedback
    private FeedbackCycle ParentFC=null;
    private ArrayList<FeedbackCycle> FeedbackCyclesPredecessors;// this would be added to OPA
    private ArrayList <String> FeedbackPredecessors;
    private boolean isFeddbackStarterOPA=false;
    //end of feedback variables
    // Status Variables
    public final static int UNSTARTED=-1;
    public final static int CANSTART=0;
    public final static int HASSTARTED=1;
    public final static int ISWAITINGFORRESOURCES=2;
    public final static int HASFINISHED=3;
    public final static int ISWAITINGFORFEEDBACKCYCLE=4;
    
    private int Status=UNSTARTED;
    private float Duration; //this is the chosen Duration from the function OriginalPA.getDuration
    private float CurrentDuration;// this one is to subtract the amount of time that has passed between one step and another this is what is added to the total time
    private String OPAIdentifier; //this is of the form %Projectname%ProjectReferenceNumber%
    private boolean HasLostaFight=false;
    DecimalFormat DFormat = new DecimalFormat(MainForm.PrecFormat);
    private HashMap <String,Double> neededRes; 
    HashMap<String, Double> ProbabilityofbeingworkedinFeedback;//changed 
    
    
       
    public OperationProjectActivity( ProjectActivity originalPA,OperationProject OP){
        
        this.OriginalPA=originalPA;
        Duration=this.OriginalPA.getDuration();
        CurrentDuration=Duration;
        this.Parent=OP;
        this.OPAIdentifier=OriginalPA.getPAIdentifier();
        FeedbackCyclesPredecessors =new ArrayList<FeedbackCycle> ();
        FeedbackPredecessors=new ArrayList<String>();
        GeneticsFeedbackPredecessors=new ArrayList<String>();
        GeneticFCIDs=new ArrayList<String>();
          
        //Setters for optimization
        ProbabilityofbeingworkedinFeedback= originalPA.getProbabilityofbeingworkedinFeedbackPAIdentifiers();
        neededRes=OriginalPA.getNeededRes();
        
    }
public int getNumberofPred(){
    
    if(isfeedback){
        return FeedbackPredecessors.hashCode();
        
    }else
    {
        return this.OriginalPA.getPredecesorsIDsPAIdentfiers().size();
    }
}
    public boolean isthisresourceneeded(String Name){
        if(neededRes.containsKey(Name)){
            return true;
        }
        return false;
    }
    public float getResourceNeededQtybyName(String Name){
        
        return this.getOriginalPA().getResourceQtybyName(Name);
    }
    /**
     * @return the OriginalPA
     */
    public ProjectActivity getOriginalPA() {
        return OriginalPA;
    }

    public ArrayList <String> getPredecessorIDs(boolean Override){
        
        if(isfeedback && !Override){
            
            return this.FeedbackPredecessors;
            
        }
        return this.OriginalPA.getPredecesorsIDsPAIdentfiers();
    }
    public float getProbability() {
        
        if (isIsfeedback()) {

            float AvgProbability = 1.0f;
            
            float feebackLevel =(float) Double.parseDouble(this.getFeedbackDepth());
            //    this.OriginalOPA.getOriginalPA().getLog().appendToLog(Logger.INFORMATION, "GeneticsOPA : getPropability : is feedback "+getOPAIdentifier()+ " ProbabilityofbeingworkedinFeedback Size "+ProbabilityofbeingworkedinFeedback.size()+ " feebackLevel "+feebackLevel); 
            if (ProbabilityofbeingworkedinFeedback.size() > 0) {
                AvgProbability = 0.0f;
                float SumProbability = 0.0f;
                int count = 0;
                Iterator itRWfeed = ProbabilityofbeingworkedinFeedback.entrySet().iterator();

                while (itRWfeed.hasNext()) {
                    Map.Entry<String, Double> pairsRWF = (Map.Entry) itRWfeed.next();

                    float Val = pairsRWF.getValue().floatValue();
                    SumProbability += Val;
                    count++;
//this.OriginalOPA.getOriginalPA().getLog().appendToLog(Logger.INFORMATION, "GeneticsOPA : getPropability : is feedback "+getOPAIdentifier()+ " ProbabilityofbeingworkedinFeedback Size "+ProbabilityofbeingworkedinFeedback.size()+ " Val "+SumProbability+ " Count "+count); 

                }
                AvgProbability = SumProbability / count;
            }else{
                
                AvgProbability=GeneticsMain.GeneticsGenerateNumberPercentage();
            }
            //ProbabilityofbeingworkedinFeedback.clear();
            return (float)Math.pow(AvgProbability, feebackLevel);
        } else {
            return 1.0f;
        }

        
    }
    /**
     * @param OriginalPA the OriginalPA to set
     */
    public void setOriginalPA(ProjectActivity OriginalPA) {
        this.OriginalPA = OriginalPA;
    }

    /**
     * @return the isfeedback
     */
    public boolean isIsfeedback() {
        return isfeedback;
    }

    /**
     * @param isfeedback the isfeedback to set
     */
    public void setIsfeedback(boolean isfeedback) {
        this.isfeedback = isfeedback;
    }

    /**
     * @return the feedbackDepth
     */
    public String getFeedbackDepth() {
        return feedbackDepth;
    }

    /**
     * @param feedbackDepth the feedbackDepth to set
     */
    public void setFeedbackDepth(String feedbackDepth) {
        this.feedbackDepth = feedbackDepth;
    }

    public float getmaxnumberofreworks() {
        return this.OriginalPA.getMaxiNumofReworks();

    }

    public void modifyCurrentDuration(int level) {
        // duration * rework impact * *(1-LF)   For task 2 = 10 * 0.2 * (1-0.2)
        float lF = this.OriginalPA.getLearningFactor();
        float Durval = Duration;
        float SumReworkIMpact = 0.0f;
        float AvgReworkIMpact = 0.0f;
        float NewDur = Durval;
        int count = 0;

        HashMap<String, Double> HM = this.OriginalPA.getReworkImpactPAIdentifiers();
        Set<Map.Entry<String, Double>> set = HM.entrySet();

        for (Map.Entry<String, Double> RW : set) {
            String ref = RW.getKey();
            double val = RW.getValue();
            SumReworkIMpact += val;
            count++;
        }
        if (count <= 0) {
            count = 1;
        }

        AvgReworkIMpact = SumReworkIMpact / count;
        if (AvgReworkIMpact == 0.0) {
            AvgReworkIMpact = 1.0f;

        }


        for (int i = 0; i < level; i++) {
            NewDur = NewDur * (1 - lF) * AvgReworkIMpact;
            NewDur = Float.valueOf(DFormat.format(NewDur));

        }


        OriginalPA.getLog().appendToLog(Logger.INFORMATION, "OperationProjectActivity:modifyCurrentDuration: the new  Duration  = " + NewDur + " Old  Duration " + Durval + " the sumReImpact = " + SumReworkIMpact + " AvgReworkIMpact " + AvgReworkIMpact + "  Learning Factor " + lF+ " Level "+level);
        this.Duration = NewDur;
    }
        //double RI=this.OriginalPA.get
       //get the rewok impact  to modify the duration 

    /**
     * @return the FPAName
     */
    public String getFPAName() {
        int depth=0;
        if(!this.getFeedbackDepth().isEmpty()){
            Double x=Double.parseDouble(this.getFeedbackDepth());
            depth=(int)x.intValue();
        }
       
       String F="-"; 
       for(int i=0;i<depth;i++){
            F+="f";
        }
        return this.OPAIdentifier+F;
    }

    /**
     * @param FPAName the FPAName to set
     */
    public void setFPAName(String FPAName) {
        this.FPAName = FPAName;
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

    /**
     * @return the OPAIdentifier
     */
    public String getOPAIdentifier(boolean OverrideFB) {
        if(this.isfeedback && !OverrideFB){
            
            return this.getFPAName();
        }
        return OPAIdentifier;
    }
    // be careful this is used only in the fixPredessorForOPAF since we needed to 
     public String getOPAIdentifierFake(int Level) {
       String F="-"; 
       for(int i=0;i<Level;i++){
            F+="f";
        }
       if(F.equals("-")){
          return  this.OPAIdentifier;
       }
        return this.OPAIdentifier+F;
  
    }
    
 /**
     * @return the OPAIdentifier
     */
    public String getOPAIdentifierandFCID() {
        if(this.isfeedback ){
            String Name=this.getFPAName()+"-"+this.ParentFC.getFCID();

            return Name;
        }
        return OPAIdentifier;
    }
    /**
     * @param OPAIdentifier the OPAIdentifier to set
     */
    public void setOPAIdentifier(String OPAIdentifier) {
        this.OPAIdentifier = OPAIdentifier;
    }

    /**
     * @return the HasLostaFight
     */
    public boolean isHasLostaFight() {
        return HasLostaFight;
    }

    /**
     * @param HasLostaFight the HasLostaFight to set
     */
    public void setHasLostaFight(boolean HasLostaFight) {
        this.HasLostaFight = HasLostaFight;
    }

    /**
     * @return the ParentFC
     */
    public FeedbackCycle getParentFC() {
        return ParentFC;
    }

    /**
     * @param ParentFC the ParentFC to set
     */
    public void setParentFC(FeedbackCycle ParentFC) {
        this.ParentFC = ParentFC;
    }

    /**
     * @return the FeedbackCyclesPredecessors
     */
    public ArrayList<FeedbackCycle> getFeedbackCyclesPredecessors() {
        return FeedbackCyclesPredecessors;
    }

    /**
     * @param FeedbackCyclesPredecessors the FeedbackCyclesPredecessors to set
     */
    public void setFeedbackCyclesPredecessors(ArrayList<FeedbackCycle> FeedbackCyclesPredecessors) {
        this.FeedbackCyclesPredecessors = FeedbackCyclesPredecessors;
    }

    /**
     * @return the FeedbackPredecessors
     */
    public ArrayList <String> getFeedbackPredecessors() {
        return FeedbackPredecessors;
    }

    /**
     * @param FeedbackPredecessors the FeedbackPredecessors to set
     */
    public void setFeedbackPredecessors(ArrayList <String> FeedbackPredecessors) {
        this.FeedbackPredecessors = FeedbackPredecessors;
    }

    /**
     * @return the Duration
     */
    public float getDuration() {
        return Duration;
    }

    /**
     * @param Duration the Duration to set
     */
    public void setDuration(float Duration) {
        this.Duration = Duration;
    }

    /**
     * @return the Parent
     */
    public OperationProject getParent() {
        return Parent;
    }

    /**
     * @param Parent the Parent to set
     */
    public void setParent(OperationProject Parent) {
        this.Parent = Parent;
    }

    /**
     * @return the GeneticsFeedbackPredecessors
     */
    public ArrayList <String> getGeneticsFeedbackPredecessors() {
        return GeneticsFeedbackPredecessors;
    }

    /**
     * @param GeneticsFeedbackPredecessors the GeneticsFeedbackPredecessors to set
     */
    public void setGeneticsFeedbackPredecessors(ArrayList <String> GeneticsFeedbackPredecessors) {
        this.GeneticsFeedbackPredecessors = GeneticsFeedbackPredecessors;
    }

    /**
     * @return the wouldwaitforfeedbackCycle
     */
    public boolean isWouldwaitforfeedbackCycle() {
        return wouldwaitforfeedbackCycle;
    }

    /**
     * @param wouldwaitforfeedbackCycle the wouldwaitforfeedbackCycle to set
     */
    public void setWouldwaitforfeedbackCycle(boolean wouldwaitforfeedbackCycle) {
        this.wouldwaitforfeedbackCycle = wouldwaitforfeedbackCycle;
    }

    /**
     * @return the GeneticFCIDs
     */
    public ArrayList<String> getGeneticFCIDs() {
        return GeneticFCIDs;
    }
    @Override
    public String toString(){
        String Preds=" Preds are ";
        for(String pred:this.getPredecessorIDs(false)){
            Preds+=pred+"/";
        }
        return " OPA "+this.getOPAIdentifier(false)+Preds;
    }

    /**
     * @return the isFeddbackStarterOPA
     */
    public boolean isIsFeddbackStarterOPA() {
        return isFeddbackStarterOPA;
    }

    /**
     * @param isFeddbackStarterOPA the isFeddbackStarterOPA to set
     */
    public void setIsFeddbackStarterOPA(boolean isFeddbackStarterOPA) {
        this.isFeddbackStarterOPA = isFeddbackStarterOPA;
    }
    
}
