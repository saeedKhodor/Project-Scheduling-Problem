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
import java.util.Random;
import java.util.Set;


/**
 *
 * @author saeed
 */
public class ProjectActivity {
    private Project Projectparent;
    private String Name;
    private String ReferenceNumber; // this is the link between name of the PA and the 
    private float MinimumDuration;
    private float mostlikelyDuration;
    private float MaximumDuration;
    private HashMap <String,Double> neededRes; //here the hashmap has two value the Key is the name of the resource and the Value is the quantity needed
    private String ResNames;
    private float MaxiNumofReworks;
    private float LearningFactor;
    private ArrayList <ProjectActivity> Predecesors;// this will be loaded from the DSM
    private ArrayList <String> PredecesorsIDs;// this will be loaded from the DSM
    private ArrayList <String> PredecesorsIDsPAIdentfiers;// this will be loaded from the DSM
    
    private HashMap <String,Double> ReworkProbability;//upper Traingle this is where the IDs of reworks are being set as set in Excel , this has the  IDs as Numbers
    private HashMap <String,Double> ReworkProbabilityPAIdentifiers ;// this is converted ReowrkProbability HashMAP to PA identifiers so we can use in the Operation projects, Has the Ids as PA identifiers
    private HashMap <String,Double> ProbabilityofbeingworkedinFeedback;//LowerTraingle
    private HashMap <String,Double> ProbabilityofbeingworkedinFeedbackPAIdentifiers;//LowerTraingle
    private HashMap <String,Double> ReworkImpact;
    private HashMap <String,Double> ReworkImpactPAIdentifiers;
    private Logger log;
    private boolean CanStart=true;// this will be used to collect the Project activities that can start in a project
    
    
    public ProjectActivity(Project Pp){
        this.Projectparent=Pp;
        neededRes=new HashMap<String,Double>();
        ReworkProbability=new HashMap<String,Double>();
        ReworkProbabilityPAIdentifiers=new HashMap<String,Double>();
        ProbabilityofbeingworkedinFeedback=new HashMap<String,Double>();
        ReworkImpact=new HashMap<String,Double>();
        PredecesorsIDs=new ArrayList<String>();
        PredecesorsIDsPAIdentfiers=new ArrayList<String>();
        ProbabilityofbeingworkedinFeedbackPAIdentifiers=new HashMap<String,Double>();
        ReworkImpactPAIdentifiers=new HashMap<String,Double>();
        
    }
   public float getResourceQtybyName(String Name){
       
       return this.getNeededRes().get(Name).floatValue();
   }
  
   public float getDuration(){
       
     DecimalFormat DFormat = new DecimalFormat(MainForm.PrecFormat);
   //          
 if(this.MinimumDuration==this.MaximumDuration && this.MaximumDuration==this.mostlikelyDuration ){
     
    return this.MinimumDuration;
 }
    //   Declarations
     float  R=0.0f;
    //   Initialise
    Random r = new Random();
   
     R = r.nextFloat();       //between 0.0 and 1.0 gaussian
     float Result=0.0f;
    //    Triangular
    if ( R == (( this.mostlikelyDuration -  this.MinimumDuration) / ( this.MaximumDuration -  this.MinimumDuration)))
    {
        Result=mostlikelyDuration;
        
    }
    else if ( R < (( mostlikelyDuration -  MinimumDuration) / ( MaximumDuration -  MinimumDuration)))
    {
        Result= (float) (MinimumDuration + Math.sqrt( R * ( MaximumDuration -  MinimumDuration) * ( mostlikelyDuration -  MinimumDuration)));
    }
    else
    {
        Result= (float) (MaximumDuration - Math.sqrt((1 -  R) * ( MaximumDuration -  MinimumDuration) * ( MaximumDuration -  mostlikelyDuration)));
    }
    
return Float.valueOf(DFormat.format(Result));
   }
    public void filloutResourceHashMap(String Resname,String Qty){
         getLog().appendToLog(Logger.INFORMATION,"ProjectActvity : filloutResourceHashMap : Resname = " +Resname +" , Qty = "+Qty);
         this.ResNames=Resname;
        String [] Resnames=Resname.split(",");
        String [] Qtys=Qty.split(",");
        
        if(Resnames.length==Qtys.length){
            for(int i=0;i<Resnames.length;i++){
                String TrimmedResName=Resnames[i].trim();
                neededRes.put(TrimmedResName,new Double(Qtys[i]));
                getLog().appendToLog(Logger.INFORMATION,"ProjectActvity : filloutResourceHashMap needed Resource is Set to"+TrimmedResName +", Qty = "+Qtys[i]);
           }
          
        }else{
          getLog().appendToLog(Logger.HAS_TO_SHOW,"ProjectActvity : filloutResourceHashMap : There is a Problem in the ExcelSheet the resources and the qtys are not equal in " + Projectparent.getProjectName());
        }
                
    }
    public void convertPredessorIdstoPAIdentifiers(){
        getLog().appendToLog(Logger.HAS_TO_SHOW,"ProjectActvity : convertPredessorIdstoPAIdentifiers : PredecesorsIDs size is "+PredecesorsIDs.size() );
   
        for(String Ref :PredecesorsIDs){
           
            this.getPredecesorsIDsPAIdentfiers().add(this.Projectparent.getPAbyRefNum(Ref).getPAIdentifier());
                   getLog().appendToLog(Logger.HAS_TO_SHOW,"ProjectActvity : convertPredessorIdstoPAIdentifiers : Converted the Ref "+Ref+"to "+this.Projectparent.getPAbyRefNum(Ref).getPAIdentifier() );
   
            
        }
        
   }
     public void convertReworkerstoPAIdentifiers(){
   
          getLog().appendToLog(Logger.HAS_TO_SHOW,"ProjectActvity : convertReworkerstoPAIdentifiers : ReworkProbability size is "+ReworkProbability.size() +" in "+this.getPAIdentifier() );
                  Set<Map.Entry<String, Double>> set = ReworkProbability.entrySet();
                  
                for (Map.Entry<String, Double> RW : set) {
                    String ref=RW.getKey();
                    Double val=RW.getValue();
                     getReworkProbabilityPAIdentifiers().put(this.Projectparent.getPAbyRefNum(ref).getPAIdentifier(), val);
                   getLog().appendToLog(Logger.HAS_TO_SHOW,"ProjectActvity : convertReworkerstoPAIdentifiers : Converted the Ref "+ref+"to "+this.Projectparent.getPAbyRefNum(ref).getPAIdentifier() );
                     
                }
    ReworkProbability.clear();
   }
          public void convertProbabilityofbeingworkedinFeedbacktoPAIdentifiers(){
   
          getLog().appendToLog(Logger.HAS_TO_SHOW,"ProjectActvity : convertProbabilityofbeingworkedinFeedbacktoPAIdentifiers : ReworkProbability size is "+ReworkProbability.size() +" in "+this.getPAIdentifier() );
                  Set<Map.Entry<String, Double>> set = ProbabilityofbeingworkedinFeedback.entrySet();
                  
                for (Map.Entry<String, Double> RW : set) {
                    String ref=RW.getKey();
                    Double val=RW.getValue();
                     ProbabilityofbeingworkedinFeedbackPAIdentifiers.put(this.Projectparent.getPAbyRefNum(ref).getPAIdentifier(), val);
                   getLog().appendToLog(Logger.HAS_TO_SHOW,"ProjectActvity : convertProbabilityofbeingworkedinFeedbacktoPAIdentifiers : Converted the Ref "+ref+"to "+this.Projectparent.getPAbyRefNum(ref).getPAIdentifier() );
                     
                }
  ProbabilityofbeingworkedinFeedback.clear();
   }
  public void convertReworkProbabilitytoPAIdentifiers(){
   
          getLog().appendToLog(Logger.HAS_TO_SHOW,"ProjectActvity : convertReworkProbabilitytoPAIdentifiers : ReworkImpact size is "+ReworkImpact.size() +" in "+this.getPAIdentifier() );
                  Set<Map.Entry<String, Double>> set = ReworkImpact.entrySet();
                  
                for (Map.Entry<String, Double> RW : set) {
                    String ref=RW.getKey();
                    Double val=RW.getValue();
                     getReworkImpactPAIdentifiers().put(this.Projectparent.getPAbyRefNum(ref).getPAIdentifier(), val);
                   getLog().appendToLog(Logger.HAS_TO_SHOW,"ProjectActvity : convertReworkProbabilitytoPAIdentifiers : Converted the Ref "+ref+"to "+this.Projectparent.getPAbyRefNum(ref).getPAIdentifier() );
                     
                }
  ReworkImpact.clear();
   }
    /**
     * @return the Projectparent
     */
    public Project getProjectparent() {
        return Projectparent;
    }

    /**
     * @param Projectparent the Projectparent to set
     */
    public void setProjectparent(Project Projectparent) {
        this.Projectparent = Projectparent;
    }

    /**
     * @return the Name
     */
    public String getName() {
        return Name;
    }

    /**
     * @param Name the Name to set
     */
    public void setName(String Name) {
        this.Name = Name;
    }

    /**
     * @return the ReferenceNumber
     */
    public String getReferenceNumber() {
        return ReferenceNumber;
    }

    /**
     * @param ReferenceNumber the ReferenceNumber to set
     */
    public void setReferenceNumber(String ReferenceNumber) {
        this.ReferenceNumber=ReferenceNumber;
    }

    /**
     * @return the MinimumDuration
     */
    public double getMinimumDuration() {
        return MinimumDuration;
    }

    /**
     * @param MinimumDuration the MinimumDuration to set
     */
    public void setMinimumDuration(float MinimumDuration) {
        this.MinimumDuration = MinimumDuration;
    }

    /**
     * @return the mostlikelyDuration
     */
    public float getMostlikelyDuration() {
        return mostlikelyDuration;
    }

    /**
     * @param mostlikelyDuration the mostlikelyDuration to set
     */
    public void setMostlikelyDuration(float mostlikelyDuration) {
        this.mostlikelyDuration = mostlikelyDuration;
    }

    /**
     * @return the MaximumDuration
     */
    public float getMaximumDuration() {
        return MaximumDuration;
    }

    /**
     * @param MaximumDuration the MaximumDuration to set
     */
    public void setMaximumDuration(float MaximumDuration) {
        this.MaximumDuration = MaximumDuration;
    }

    /**
     * @return the neededRes
     */
    public HashMap <String,Double> getNeededRes() {
        return neededRes;
    }

    /**
     * @param neededRes the neededRes to set
     */
    public void setNeededRes(HashMap <String,Double> neededRes) {
        this.neededRes = neededRes;
    }

    /**
     * @return the MaxiNumofReworks
     */
    public float getMaxiNumofReworks() {
        return MaxiNumofReworks;
    }

    /**
     * @param MaxiNumofReworks the MaxiNumofReworks to set
     */
    public void setMaxiNumofReworks(float MaxiNumofReworks) {
        this.MaxiNumofReworks = MaxiNumofReworks;
    }

    /**
     * @return the LearningFactor
     */
    public float getLearningFactor() {
        return LearningFactor;
    }

    /**
     * @param LearningFactor the LearningFactor to set
     */
    public void setLearningFactor(float LearningFactor) {
        this.LearningFactor = LearningFactor;
    }

    /**
     * @return the Predecesors
     */
    public ArrayList <ProjectActivity> getPredecesors() {
        return Predecesors;
    }

    /**
     * @param Predecesors the Predecesors to set
     */
    public void setPredecesors(ArrayList <ProjectActivity> Predecesors) {
        this.setPredecesors(Predecesors);
    }
    
    @Override
    public String toString(){
        //still have to add iteration  through the Hashmap to point out the resources and also the predessors
        String PredIds="\nPredescessors IDs are";
        for(String ID: this.PredecesorsIDs){
            PredIds=PredIds+" , "+ID;
        }
       String Res="\nResources used are";
        Iterator it;
        it = this.getNeededRes().entrySet().iterator();
       while (it.hasNext()){
        Map.Entry pairs = (Map.Entry)it.next();
            Res=Res+" ,Resource Name="+pairs.getKey()+" ,Resource Quantity="+ pairs.getValue();
        }
       String RWProb="there is no probabilty of being reworked";
       if(this.getReworkProbability().size()>0){
        RWProb="\n ProbabilityofbeingworkedinFeedback Afterfinishing  ";
        Iterator itRW;
        itRW = this.getReworkProbability().entrySet().iterator();
            while (itRW.hasNext()){
                 Map.Entry pairsRW = (Map.Entry)itRW.next();
                     RWProb=RWProb+"PA ID ="+pairsRW.getKey()+" ,Probabilty="+ pairsRW.getValue()+",";
                 }
            RWProb+="that it will be reworked";
       }

      String ifRWProbbeingDone="\nThere is no rework probabailty ";
       if(this.getProbabilityofbeingworkedinFeedback().size()>0){
        ifRWProbbeingDone="\n if Reworking happens after the Predecessor ";
        Iterator itRWfeed;
        itRWfeed = this.getProbabilityofbeingworkedinFeedback().entrySet().iterator();
            while (itRWfeed.hasNext()){
                 Map.Entry pairsRWF = (Map.Entry)itRWfeed.next();
                     ifRWProbbeingDone=ifRWProbbeingDone+"PA ID ="+pairsRWF.getKey()+" ,Probabilty="+ pairsRWF.getValue()+",";
                 }
            ifRWProbbeingDone+="that it will be reworked";
       }
       
       
      String RWImpact="\nNo Reowrk Impact ";
       if(this.getReworkImpact().size()>0){
        RWImpact="\n the reworkimpact when if rework is done is  ";
        Iterator itRWImpact;
        itRWImpact = this.getReworkImpact().entrySet().iterator();
            while (itRWImpact.hasNext()){
                 Map.Entry pairsRWI = (Map.Entry)itRWImpact.next();
                     RWImpact=RWImpact+"PA ID ="+pairsRWI.getKey()+" ,Impact is ="+ pairsRWI.getValue()+",";
                 }
            RWImpact+="after reworking";
       }
       
        return " Name="+this.getName() +" \nRef=" +this.getReferenceNumber()+" \nMinDuration=" +this.getMinimumDuration()+" \nMLDuration=" +this.getMostlikelyDuration()+" \nMAXDur= " +this.getMaximumDuration()+" \nMAX#Reworks= " +this.getMaxiNumofReworks()+" \nLF= " +this.getLearningFactor()+" \nCanStart="+this.isCanStart()+PredIds+Res+RWProb+ifRWProbbeingDone+RWImpact;
        
    } 
    
    public String getPAIdentifier(){

        return this.Projectparent.getProjectName()+this.getReferenceNumber();
    }
    /**
     * @return the log
     */
    public Logger getLog() {
        return log;
    }

    /**
     * @param log the log to set
     */
    public void setLog(Logger log) {
        this.log = log;
    }

    /**
     * @return the CanStart
     */
    public boolean isCanStart() {
        return CanStart;
    }

    /**
     * @param CanStart the CanStart to set
     */
    public void setCanStart(boolean CanStart) {
        this.CanStart = CanStart;
    }

    /**
     * @return the PredecesorsIDs
     */
    public ArrayList <String> getPredecesorsIDs() {
        return PredecesorsIDs;
    }

    /**
     * @param PredecesorsIDs the PredecesorsIDs to set
     */
    public void setPredecesorsIDs(ArrayList <String> PredecesorsIDs) {
        this.setPredecesorsIDs(PredecesorsIDs);
    }

    /**
     * @return the ReworkProbability
     */
    public HashMap <String,Double> getReworkProbability() {
        return ReworkProbability;
    }

    /**
     * @param ReworkProbability the ReworkProbability to set
     */
    public void setReworkProbability(HashMap <String,Double> ReworkProbability) {
        this.setReworkProbability(ReworkProbability);
    }

    /**
     * @return the ProbabilityofbeingworkedinFeedback
     */
    public HashMap <String,Double> getProbabilityofbeingworkedinFeedback() {
        return ProbabilityofbeingworkedinFeedback;
    }

    /**
     * @param ProbabilityofbeingworkedinFeedback the ProbabilityofbeingworkedinFeedback to set
     */
    public void setProbabilityofbeingworkedinFeedback(HashMap <String,Double> ProbabilityofbeingworkedinFeedback) {
        this.setProbabilityofbeingworkedinFeedback(ProbabilityofbeingworkedinFeedback);
    }

    /**
     * @return the ReworkImpact
     */
    public HashMap <String,Double> getReworkImpact() {
        return ReworkImpact;
    }

    /**
     * @param ReworkImpact the ReworkImpact to set
     */
    public void setReworkImpact(HashMap <String,Double> ReworkImpact) {
        this.setReworkImpact(ReworkImpact);
    }

    /**
     * @return the PredecesorsIDsPAIdentfiers
     */
    public ArrayList <String> getPredecesorsIDsPAIdentfiers() {
        return PredecesorsIDsPAIdentfiers;
    }

    /**
     * @param PredecesorsIDsPAIdentfiers the PredecesorsIDsPAIdentfiers to set
     */
    public void setPredecesorsIDsPAIdentfiers(ArrayList <String> PredecesorsIDsPAIdentfiers) {
        this.PredecesorsIDsPAIdentfiers = PredecesorsIDsPAIdentfiers;
    }

    /**
     * @return the ResNames
     */
    public String getResNames() {
        return ResNames;
    }

    /**
     * @param ResNames the ResNames to set
     */
    public void setResNames(String ResNames) {
        this.ResNames = ResNames;
    }

    /**
     * @return the ReworkProbabilityPAIdentifiers
     */
    public HashMap <String,Double> getReworkProbabilityPAIdentifiers() {
        return ReworkProbabilityPAIdentifiers;
    }

    /**
     * @param ReworkProbabilityPAIdentifiers the ReworkProbabilityPAIdentifiers to set
     */
    public void setReworkProbabilityPAIdentifiers(HashMap <String,Double> ReworkProbabilityPAIdentifiers) {
        this.ReworkProbabilityPAIdentifiers = ReworkProbabilityPAIdentifiers;
    }

    /**
     * @return the ProbabilityofbeingworkedinFeedbackPAIdentifiers
     */
    public HashMap <String,Double> getProbabilityofbeingworkedinFeedbackPAIdentifiers() {
        return ProbabilityofbeingworkedinFeedbackPAIdentifiers;
    }

    /**
     * @param ProbabilityofbeingworkedinFeedbackPAIdentifiers the ProbabilityofbeingworkedinFeedbackPAIdentifiers to set
     */
    public void setProbabilityofbeingworkedinFeedbackPAIdentifiers(HashMap <String,Double> ProbabilityofbeingworkedinFeedbackPAIdentifiers) {
        this.ProbabilityofbeingworkedinFeedbackPAIdentifiers = ProbabilityofbeingworkedinFeedbackPAIdentifiers;
    }

    /**
     * @return the ReworkImpactPAIdentifiers
     */
    public HashMap <String,Double> getReworkImpactPAIdentifiers() {
        return ReworkImpactPAIdentifiers;
    }

    /**
     * @param Predecesors the Predecesors to set
     */
  
    
}
