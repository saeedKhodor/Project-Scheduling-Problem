/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package projectschedulingproblem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 *
 * @author saeed
 */
public class FeedbackManager {

    private ArrayList<ReworkRecordCapsule> reworkRecords;
    private OperationProject OPP;
    private Logger Log;
    public static int DIDNOTHING=-1;
    public static int CREATEDFC=1;
    private ArrayList<FeedbackCycle> FeedbackCycles;
    

    public FeedbackManager(OperationProject Opp,Logger log) {

        reworkRecords = new ArrayList<ReworkRecordCapsule>();
        OPP=Opp;
        this.Log=log;
        FeedbackCycles=new ArrayList<FeedbackCycle>();
    }
    
    public int getnumberofReworksforPA(OperationProjectActivity PA, String FCID, boolean withaddition ){

       ReworkRecordCapsule RRC=this.getReworkRecordCapsulebyFCID(FCID);
       return RRC.getOPARecord(PA,false);
        
    }
    public int AddOnetonumberofReworksforPA(OperationProjectActivity PA, String FCID){

       ReworkRecordCapsule RRC=this.getReworkRecordCapsulebyFCID(FCID);
       int val=RRC.addonetoOPArecord(PA,1);
       if(val!=-1){
           return val;
       }
        
        return -1;
        
    }
    

    public ReworkRecordCapsule getReworkRecordCapsulebyFCID(String Fcid) {

        for (ReworkRecordCapsule RRC : reworkRecords) {
            String tempfcid=RRC.getFCID(false);
            getLog().appendToLog(Logger.INFORMATION, "FeedbackManager:getReworkRecordCapsulebyFCID: comparing Fcid "+Fcid+" to "+tempfcid + " result "+ tempfcid.equalsIgnoreCase(Fcid));
            if (tempfcid.equalsIgnoreCase(Fcid)) {
 
                return RRC;
            }
        }
        return null;
    }
// Key1 Function that decides if Feedbackcycle can occur
    public int processOPA(OperationProjectActivity EndOpa,boolean UsedforGenetics){
        String Result=IsTherefeedbackPossibility(EndOpa);  
        getLog().appendToLog(Logger.INFORMATION, "FeedbackManager:processOPA: the OPA "+EndOpa.getOPAIdentifier(false)+ " Result "+Result);
        if(Result.equalsIgnoreCase("nothing")){
             //Log.appendToLog(Logger.INFORMATION, "FeedbackManager:processOPA: the OPA "+EndOpa.getOPAIdentifier(false)+" doesnt have a feedback rework possibility ");
              return DIDNOTHING;
        }
        
        String Split[]=Result.split(",");
        
        for(int i=0;i<Split.length;i+=2){
            Double val=Double.parseDouble(Split[i + 1]);
            if(UsedforGenetics){
                val=1.0;
            }   
            boolean FLipped=FeedbackFlipaCoin(val);
            if(!FLipped){
                continue;
            }
            String FCIDWithoutLevel=Split[i]+"|"+EndOpa.getOPAIdentifier(true);
            String SOPA=Split[i];
            String EOPA=EndOpa.getOPAIdentifier(true);
            
            OperationProjectActivity sopa=this.OPP.getOPAbyPAIDentifier(SOPA, true);
            float Maxreworksforsopa=sopa.getmaxnumberofreworks();
           // inserted 6-9-2013
            if(Maxreworksforsopa==0.0){
                return DIDNOTHING;
            }
            
            
           ReworkRecordCapsule RRC=this.getReworkRecordCapsulebyFCID(FCIDWithoutLevel);    
           if(RRC==null){
              // no RRC available add new RRC to the rework records
              RRC=new ReworkRecordCapsule(this,SOPA,EOPA);
              //RRCNew.analyzeTempOPAs(this.OPP.);
               RRC.analyzeTempOPAs(this.OPP.getOPAsinInterval(SOPA, EOPA,UsedforGenetics));
              int lr= RRC.addOnetoLevelReached();
             // OperationProjectActivity OriginalOPA=this.OPP.getOPAbyPAIDentifier(EOPA, true);
                getLog().appendToLog(Logger.INFORMATION, "FeedbackManager:processOPA: RRC of FCID " +RRC.getFCID(false)+"has reached "+ lr);
              reworkRecords.add(RRC);
              
            
           }else{
               if(RRC.isthisRRCfinished()){
                    getLog().appendToLog(Logger.INFORMATION, "FeedbackManager:processOPA: RRC of FCID " +RRC.getFCID(false)+"has finised");

                   continue;
               }
               int current = this.getnumberofReworksforPA(sopa, FCIDWithoutLevel, false);
            if(current>1){
                 String FakeEndOPAName = EndOpa.getOPAIdentifierFake(current - 1);
                 if(!this.doesFOPAexistinFCs(FakeEndOPAName)){
                       return DIDNOTHING;    
                 }
                 
            }
               RRC.addOnetoLevelReached();
               
               /// rrc Available
           }
           
            FeedbackCycle fc=new FeedbackCycle(this, sopa, EndOpa, getLog(), SOPA, EOPA,RRC.getLevelReached());

           ArrayList<OperationProjectActivity> OPAInterval=RRC.getInterval();
           // here we fix the Geneticspredescessors for the genetics  
                  
            for (OperationProjectActivity OPA : this.getOPP().getPool()) {
                ArrayList<String> Al = OPA.getOriginalPA().getPredecesorsIDsPAIdentfiers();
                if (Al.contains(EndOpa.getOPAIdentifier(true))) {
                    if(OPA.isIsfeedback()){
                        continue;
                    }
                    OPA.setWouldwaitforfeedbackCycle(true);
                    ArrayList<String> FBPreds = OPA.getGeneticsFeedbackPredecessors();
                    // added on 6/5/2013
                    
                    ArrayList<String> GFcids=OPA.getGeneticFCIDs();
                    if(!GFcids.contains(fc.getFCID())){
                        GFcids.add(fc.getFCID());
                    }
                    // to keep tot he genetics part the FCID to be used in identifying the Feedbacks
                    for (OperationProjectActivity IntervalOPA : OPAInterval) {
                      String Name=IntervalOPA.getOPAIdentifier(true);
                        if(!FBPreds.contains(Name)){
                          FBPreds.add(Name);   
                          Log.appendToLog(Logger.INFORMATION, "FeedbackManager:processOPA: genetics Predecessors adding " + Name+"  for "+OPA.getOPAIdentifier(false));
                        }
                    }

                }
            }
       ///////////////*****************
             ArrayList <OperationProjectActivity>OPAfs= fc.createOPAFs(OPAInterval,UsedforGenetics);
             fc.filloutPostsecors();
             getFeedbackCycles().add(fc);
            this.OPP.getPool().addAll(OPAfs);
        }
        
       // Log.appendToLog(Logger.INFORMATION, "FeedbackManager:processOPA:the OPA "+EndOpa.getOPAIdentifier(false)+" can start a reworkfeedback");
       
        
        return 0;
    }
    public boolean doesFOPAexistinFCs(String FOPANAme){
        
        for(FeedbackCycle fc:FeedbackCycles){
            if(fc.doesFOPAexistinFCs(FOPANAme)){
                return true;
            }
            
        }
       return false;
    }
     public void UpdateFCs() {

        for (int j = 0; j < getFeedbackCycles().size(); j++) {

            getFeedbackCycles().get(j).UpdateFinishedStatus();

        }

    }
        public ArrayList<FeedbackCycle> getUnfinishedFCs() {

        ArrayList<FeedbackCycle> AL = new ArrayList<FeedbackCycle>();

        for (FeedbackCycle FC : getFeedbackCycles()) {

            if (!FC.isIsFinished()) {
                AL.add(FC);
            }
        }

        return AL;
    }
     private String IsTherefeedbackPossibility(OperationProjectActivity OPA) {
        String OPAName = OPA.getOPAIdentifier(true);
        String str = "";

        for (OperationProjectActivity opa : this.getOPP().getPool()) {
        if(opa.isIsfeedback()){
            continue;
        }
            HashMap<String, Double> HM = opa.getOriginalPA().getReworkProbabilityPAIdentifiers();
             // Log.appendToLog(Logger.INFORMATION, "FeedbackManager:IsTherefeedbackPossibility: OPA NAME= "+OPAName + " HMSize " +HM.size());

            if (HM.containsKey(OPAName)) {

                str += (opa.getOPAIdentifier(true) + "," + HM.get(OPAName).toString() + ",");
                getLog().appendToLog(Logger.INFORMATION, "FeedbackManager:IsTherefeedbackPossibility: i found that there is a probability that after Finishing OPA " + OPAName + " OPA " + opa.getOPAIdentifier(false) + "can start");

            }

        }
        
        if (str.isEmpty()) {
            str = "nothing";

        }
        return str;
    }   
    
public static boolean FeedbackFlipaCoin(double d) {
        int Number = (int) (d * 100);

        Random r = new Random();
        int R = r.nextInt(100);
        //Log.appendToLog(Logger.INFORMATION,"OperationProject:FeedbackFlipaCoin: Number"+Number+" and R="+R);
        if (R < Number) {
            return true;
        }

        return false;
    }

    /**
     * @return the OPP
     */
    public OperationProject getOPP() {
        return OPP;
    }

    /**
     * @param OPP the OPP to set
     */
    public void setOPP(OperationProject OPP) {
        this.OPP = OPP;
    }

    /**
     * @return the FeedbackCycles
     */
    public ArrayList<FeedbackCycle> getFeedbackCycles() {
        return FeedbackCycles;
    }

    /**
     * @param FeedbackCycles the FeedbackCycles to set
     */
    public void setFeedbackCycles(ArrayList<FeedbackCycle> FeedbackCycles) {
        this.FeedbackCycles = FeedbackCycles;
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
