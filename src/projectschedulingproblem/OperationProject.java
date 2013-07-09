/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package projectschedulingproblem;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author saeed
 */
public class OperationProject {

    private Project OriginalProject;
    private ArrayList<OperationProjectActivity> workingPool;
    private ArrayList<OperationProjectActivity> FinishedPool;
    private ArrayList<OperationProjectActivity> Pool;
    private ArrayList<OperationProjectActivity> OriginalPool;
    private Logger Log;
    private boolean ProjectHasFinished;
    private HashMap<Integer, String> StepLog;// logical method timestepper
    private FeedbackManager FM;
    private ProjectExcutionStats PES;// this is used int he Logical method and the timestepper
    private ArrayList<String> EndingActivityOPANames = new ArrayList<String>();
    private CriticalPathCapsule CPCapsule;

    public OperationProject(Project OP, Logger log) {
        ProjectHasFinished = false;
        OriginalPool = new ArrayList<OperationProjectActivity>();
        OriginalProject = OP;
        workingPool = new ArrayList<OperationProjectActivity>();
        FinishedPool = new ArrayList<OperationProjectActivity>();
        Pool = new ArrayList<OperationProjectActivity>();
        this.Log = log;
        for (ProjectActivity PA : OriginalProject.getPAs()) {
            OperationProjectActivity OPA = new OperationProjectActivity(PA, this);
            Pool.add(OPA);
            OriginalPool.add(OPA);

        }
        Log.appendToLog(Logger.INFORMATION, "OperationProject:OperationProject: initial Pool Size =" + Pool.size());
        StepLog = new HashMap<Integer, String>();
        FM = new FeedbackManager(this, this.Log);
        this.PES = new ProjectExcutionStats(this);
        setEndingActivityOPANames();
        CPCapsule= GetCriticalPath();
    }
    // for the critical path  

    private void setEndingActivityOPANames() {
        for (OperationProjectActivity Opas : OriginalPool) {
            boolean isPredecessorforAnyone = false;

            for (OperationProjectActivity InnerOPAs : OriginalPool) {
                ArrayList<String> Preds = InnerOPAs.getPredecessorIDs(false);
                if (Preds.contains(Opas.getOPAIdentifier(false))) {
                    isPredecessorforAnyone = true;
                    break;
                }
            }
            if (!isPredecessorforAnyone) {
                this.getEndingActivityOPANames().add(Opas.getOPAIdentifier(false));
                Log.appendToLog(Logger.INFORMATION, "OperationProject:setEndingActivityOPAName:the OPA   " + Opas.getOPAIdentifier(false)+" is considered an ending OPA and would need a virtual Ending in CPath");
            }
        }

    }

    public String getProjectName() {

        return this.OriginalProject.getProjectName();
    }

    private CriticalPathCapsule GetCriticalPath() {
        ArrayList<CPathOPA> CPOPAs = new ArrayList<CPathOPA>();
        //initialize the Cpath OPAs


        for (OperationProjectActivity Opa : this.OriginalPool) {
            CPOPAs.add(new CPathOPA(Opa));
            
        }
        CPOPAs.add(new CPathOPA(null,true,this.getEndingActivityOPANames()));


        for (CPathOPA CPOpa : CPOPAs) {
            if (CPOpa.isCanstart()) {
                CPOpa.setEarliestPossibleStartforActivity(0.0f);

            }

        }
        //Forward calculation has been Started
        boolean allESTshasnotbeenset = true;
        while (allESTshasnotbeenset) {

            for (CPathOPA CPOpa : CPOPAs) {
                // if all the pred have the EST been Set else skip it 
                ArrayList<String> Al = CPOpa.getprednames();
                ArrayList<CPathOPA> Cpreds = this.getCOPAsbyPAIDentifiers(Al, CPOPAs);
                //make sure that all predecossors  Ests has been set
                boolean allPredEstsbeenset = true;
                for (CPathOPA Cpred : Cpreds) {
                    if (!Cpred.isESThasbeenset()) {
                        allPredEstsbeenset = false;
                    }
                }
                if (allPredEstsbeenset && Cpreds.size() > 0) {
                    float Est = OPAgetmaxEST(Cpreds);
                    CPOpa.setEarliestPossibleStartforActivity(Est);
                   // Log.appendToLog(Logger.INFORMATION, "OperationProject:GetCriticalPath:the EST for " + CPOpa.GetCOPAIDentifier() + "has been set");
                }
            }

            if (getVirtualEnding(CPOPAs).isESThasbeenset()) {
                Log.appendToLog(Logger.INFORMATION, "OperationProject:GetCriticalPath:the forword run should be finished");
                allESTshasnotbeenset = false;

            }


        }
        //Forward calculation has been finished

        //backward calculation has been started    
        //set the ending COPA 
        CPathOPA EndingCOPA = getVirtualEnding(CPOPAs);
        EndingCOPA.setLatestPossibleFinishforActivity(EndingCOPA.getEarliestPossibleFinishforActivity());
        Log.appendToLog(Logger.INFORMATION, "OperationProject:GetCriticalPath:EndingCOPA " + EndingCOPA.toString());
        boolean allLFTshasnotbeenset = true;
        while (allLFTshasnotbeenset) {
            // all the COPAs that have this OPA as a predecessor has been set 

            for (CPathOPA CPOpa : CPOPAs) {
                ArrayList<CPathOPA> Cpostseccors = areAllLftsbeenSet(CPOpa, CPOPAs);
                if (Cpostseccors != null) {
                    // get minimum of EFTs
                    float LFt = this.getMinimumofEFTs(Cpostseccors);
                    CPOpa.setLatestPossibleFinishforActivity(LFt);
                }

            }

            boolean tempLFThasbeenset = false;
            for (CPathOPA CPOpa : CPOPAs) {
                if (!CPOpa.isLFThasbeenset()) {
                    tempLFThasbeenset = true;
                }
            }
            if(!tempLFThasbeenset){
                 Log.appendToLog(Logger.INFORMATION, "OperationProject:GetCriticalPath:the Backward run should be finished");
            }
            allLFTshasnotbeenset = tempLFThasbeenset;

        }
        //backward calculation has been finished
//         for(CPathOPA CPOpa :CPOPAs){
//              Log.appendToLog(Logger.INFORMATION, "OperationProject:GetCriticalPath:Critcalpath element is  "+ CPOpa.toString()+ " is critical  "+CPOpa.ISCritical());  
//         }
        CriticalPathCapsule CPCap = new CriticalPathCapsule(CPOPAs);
        Log.appendToLog(Logger.INFORMATION, "OperationProject:GetCriticalPath:Critcalpath for this project is   " + CPCap.toString());

        return CPCap;




    }
private CPathOPA getVirtualEnding(ArrayList<CPathOPA> Pool){
    
    for (CPathOPA copa : Pool) {
         if(copa.isIsVirtual()){
           return copa;   
         }
        
     }
    
    return null;
}
    private ArrayList<CPathOPA> areAllLftsbeenSet(CPathOPA CPOpa, ArrayList<CPathOPA> Pool) {

        ArrayList<String> Al = new ArrayList<String>();
        for (CPathOPA copa : Pool) {
            if (copa.getprednames().contains(CPOpa.GetCOPAIDentifier())) {
                Al.add(copa.GetCOPAIDentifier());
            }
        }
        ArrayList<CPathOPA> Cpostseccors = this.getCOPAsbyPAIDentifiers(Al, Pool);

        boolean allEfts = true;
        for (CPathOPA copa1 : Cpostseccors) {
            if (!copa1.isLFThasbeenset()) {
                allEfts = false;
            }

        }
        if (allEfts && Cpostseccors.size() > 0) {
            return Cpostseccors;
        } else {
            return null;
        }



    }

    private float getMinimumofEFTs(ArrayList<CPathOPA> PostPool) {
        float min = Float.MAX_VALUE;
        for (CPathOPA OPA : PostPool) {
            float LST = OPA.getLatestPossibleStartforActivity();
            if (LST < min) {

                min = LST;
            }
        }
        return min;
    }

    private float OPAgetmaxEST(ArrayList<CPathOPA> PredPool) {

        float max = Float.MIN_VALUE;
       
        for (CPathOPA OPA : PredPool) {
            float EST = OPA.getEarliestPossibleFinishforActivity();
            if (EST > max) {

                max = EST;
            }
        }
        return max;
    }

    private CPathOPA getCOPAbyPAIDentifier(String CoPA, ArrayList<CPathOPA> Pool) {

        for (CPathOPA OPA : Pool) {
            if (OPA.GetCOPAIDentifier().equalsIgnoreCase(CoPA)) {

                return OPA;
            }
        }
        return null;

    }

    private ArrayList<CPathOPA> getCOPAsbyPAIDentifiers(ArrayList<String> CoPANames, ArrayList<CPathOPA> Pool) {
        ArrayList<CPathOPA> AL = new ArrayList<CPathOPA>();
        for (String s : CoPANames) {
            for (CPathOPA OPA : Pool) {

                if (OPA.GetCOPAIDentifier().equalsIgnoreCase(s)) {

                    if (!AL.contains(OPA)) {
                        AL.add(OPA);
                        break;
                    }

                }
            }
        }
        return AL;

    }

    public ArrayList<OperationProjectActivity> getExhaustivePool() {// Exhaustive method
        ArrayList Al = new ArrayList<OperationProjectActivity>();

        for (OperationProjectActivity OPA : this.Pool) {
            // get the maximum number fo reworks , 
            Al.add(OPA);
          
            int Mx = (int)OPA.getmaxnumberofreworks();
            for (int i = 0; i < Mx; i++) {

                OperationProjectActivity OPAF = new OperationProjectActivity(OPA.getOriginalPA(), this);
                OPAF.setFeedbackDepth(new Double(i + 1).toString());// this is imporants  for the getOPAIdentifier
                OPAF.setIsfeedback(true);
                OPAF.setParentFC(null);
                
                Al.add(OPAF);

                //  Log.appendToLog(Logger.INFORMATION, "OperationProject:getExhaustivePool: ExhaustiveMethod : OPAF has been created "+OPAF.getOPAIdentifier(false));

            }
            // here run all OPAs and insert in the duration value and level... until maximum number of reworks
        }

        return Al;
    }

    public ArrayList<OperationProjectActivity> getOPAsinInterval(String StartOPA, String EndOPA,boolean usedforgenetics) {
        OperationProjectActivity OPAL = this.getOPAbyPAIDentifier(StartOPA, true);
        OperationProjectActivity OPAH = this.getOPAbyPAIDentifier(EndOPA, true);

        ArrayList<OperationProjectActivity> AL = new ArrayList<OperationProjectActivity>();
        Double Href = Double.parseDouble(OPAH.getOriginalPA().getReferenceNumber());
        Double Lref = Double.parseDouble(OPAL.getOriginalPA().getReferenceNumber());
        for (ProjectActivity PA : OriginalProject.getPAs()) {
            Double val = Double.parseDouble(PA.getReferenceNumber());

            // Log.appendToLog(Logger.INFORMATION,"OperationProject:getOPAsinInterval: Href " +Href+" Lref "+Lref+ " val "+val);

            if (val <= Href && val >= Lref) {
                AL.add(new OperationProjectActivity(PA, this));

            }

        }

        //Log.appendToLog(Logger.INFORMATION,"OperationProject:getOPAsinInterval: Function has been called Reference="+AL.size());

        return AL;
    }
    //will check if all the resouceres have finished

    public boolean arePredesecorsfinished(OperationProjectActivity PA) {
        if (PA.isIsfeedback()) {
            return PA.getParentFC().arePredecessorsfinished(PA);
        }
        boolean arefinished = true;
        ArrayList<String> ALs = PA.getOriginalPA().getPredecesorsIDsPAIdentfiers();

        for (String PANAme : ALs) {

            if (isOPAinFinishedPool(PANAme)) {
                arefinished = false;

                break;
            }
        }
        //if(OPA.)
        if (arefinished) {
            arefinished = !isOPAwaitingforaFeedbackCycle(PA);


        }


        return arefinished;

    }

    public boolean isOPAwaitingforaFeedbackCycle(OperationProjectActivity OPA) {
        boolean iswaiting = false;
        for (FeedbackCycle fc : this.FM.getUnfinishedFCs()) {
            if (fc.isFCPredessorforOPA(OPA.getOPAIdentifier(false))) {
                iswaiting = true;
                Log.appendToLog(Logger.INFORMATION, "OperationProject:isOPAwaitingforaFeedbackCycle: OPA is waiting for cycle " + OPA.getOPAIdentifier(false) + " Fc " + fc.toString());
            }


        }

        return iswaiting;


    }
    // will check if the  OPerationPrjectactivity for a PA exists in the finfished pool it is called by arePredesecorsfinished

    public boolean isOPAinFinishedPool(String OPAName) {

        for (OperationProjectActivity OPA : FinishedPool) {
            //Log.appendToLog(Logger.INFORMATION,"OperationProject:OperationProject: OPAName ="+OPAName+ "PAIDEntifier "+OPA.getOriginalPA().getPAIdentifier()) ;
            if (OPA.getOriginalPA().getPAIdentifier().equals(OPAName)) {
                return false;
            }

        }
        return true;

    }

//this is called by the timestepper on each step
    public void UpdateCurrentPool(String Reference, int Step, float TimeConsumed) {
        String StepLogString = "";
        Log.appendToLog(Logger.INFORMATION, "OperationProject:UpdateCurrentPool: Function has been called Reference=" + Reference);
        // for ( OperationProjectActivity OPA : this.Pool ){

        if (Reference.equals("0.0")) {
            for (int j = 0; j < this.getPool().size(); j++) {
                // this condition is to remove 
                OperationProjectActivity OPA = getPool().get(j);
                if (OPA.getOriginalPA().isCanStart()) {
                    this.workingPool.add(OPA);
                    //this.Pool.remove(OPA);
                    Log.appendToLog(Logger.INFORMATION, "OperationProject:UpdateCurrentPool: adding  PA that can start name and removing it from Pool Name=" + OPA.getOriginalPA().getPAIdentifier());

                }
            }
        } else {
            // Log.appendToLog(Logger.INFORMATION,"OperationProject:UpdateCurrentPool: I am Inside the else statement ");
            //firstloop 
            for (int i = 0; i < this.getPool().size(); i++) {
                boolean removedfromworkingpool = false;
                OperationProjectActivity OPA = getPool().get(i);
                //    Log.appendToLog(Logger.INFORMATION,"OperationProject:UpdateCurrentPool: I am Inside the for statement " +OPA.getStatus());
                if (OPA.getStatus() == OperationProjectActivity.HASFINISHED) {
                    //Log.appendToLog(Logger.INFORMATION,"OperationProject:UpdateCurrentPool: Has finished  "); 
                    if (this.workingPool.remove(OPA)) {
                        removedfromworkingpool = true;
                        Log.appendToLog(Logger.INFORMATION, "OperationProject:UpdateCurrentPool: removed from WorkingPool " + OPA.getOriginalPA().getName() + OPA.getOriginalPA().getPAIdentifier() + OPA.getStatus());
                        // ********** Feedback ******
                        if (FM.processOPA(OPA,false) == FeedbackManager.DIDNOTHING) {
                            Log.appendToLog(Logger.INFORMATION, "OperationProject:UpdateCurrentPool: FM decision was to do nothing ");

                        }


                        // ********** Feedback ******
                    }

//                       if(this.Pool.remove(OPA)){
//                             Log.appendToLog(Logger.INFORMATION,"OperationProject:UpdateCurrentPool: removed from Pool "+OPA.getOriginalPA().getName()+OPA.getOriginalPA().getPAIdentifier()+OPA.getStatus());
//                        }
                    if (removedfromworkingpool) {
                        this.FinishedPool.add(OPA);
                        Log.appendToLog(Logger.INFORMATION, "OperationProject:UpdateCurrentPool:added to Finishedpool " + OPA.getOriginalPA().getName() + OPA.getOriginalPA().getPAIdentifier() + OPA.getStatus());
                        StepLogString += OPA.getOPAIdentifier(false);
                    }
                }
            }
            if (!this.isProjectHasFinished()) {
                this.getStepLog().put(Step, StepLogString);
                Log.appendToLog(Logger.INFORMATION, "OperationProject:UpdateCurrentPool:adding to step log Step=" + Step + "  StepLogString  " + StepLogString);
            }
            FM.UpdateFCs();
            //this will add to the working pool the OPAs that can start
            //canstartLoop
            for (int i = 0; i < this.getPool().size(); i++) {
                OperationProjectActivity OPA = getPool().get(i);
                // Log.appendToLog(Logger.INFORMATION,"OperationProject:UpdateCurrentPool: I am Inside the second  for statement " +OPA.getStatus());
                if ((OPA.getStatus() == OperationProjectActivity.UNSTARTED && arePredesecorsfinished(OPA))) {
                    Log.appendToLog(Logger.INFORMATION, "OperationProject:UpdateCurrentPool: Has been added to working pool " + OPA.getOPAIdentifier(false));
                    if (!this.workingPool.contains(OPA)) {
                        this.workingPool.add(OPA);
                    }
                }
            }
            //this will check if all activities have finished and noe are in the other statuses, if so ... then the project has finished 
            //hasfinishedLoop
            // newly added
            if (!this.isProjectHasFinished()) {

                boolean Hasfinisheddummy = true;
                for (int i = 0; i < this.getPool().size(); i++) {
                    OperationProjectActivity OPA = getPool().get(i);
                    // Log.appendToLog(Logger.INFORMATION,"OperationProject:UpdateCurrentPool: I am Inside the second  for statement " +OPA.getStatus());
                    int Stats = OPA.getStatus();
                    if (Stats == OperationProjectActivity.UNSTARTED || Stats == OperationProjectActivity.HASSTARTED || Stats == OperationProjectActivity.ISWAITINGFORRESOURCES) {
                        Hasfinisheddummy = false;
                    }
                }
                if (Hasfinisheddummy) {

                    this.PES.setProjectEndTime(TimeConsumed);
                    this.PES.setPESStatus(ProjectExcutionStats.FINISHED);
                    Log.appendToLog(Logger.INFORMATION, "OperationProject:UpdateCurrentPool:Project has finished" + this.OriginalProject.getProjectName() + " on time " + TimeConsumed);
                }

                this.setProjectHasFinished(Hasfinisheddummy);
            }
        }
        if (this.ProjectHasFinished) {
            
          //  this.workingPool.clear();
            
            
        }

    }

    public OperationProjectActivity getOPAbyPAIDentifier(String PA, boolean Overrided) {
// we may have to change this since the overided value may come with 2 OPA same name
        for (OperationProjectActivity OPA : this.getPool()) {
            if (OPA.getOPAIdentifier(Overrided).equalsIgnoreCase(PA)) {

                return OPA;
            }
        }
        return null;

    }

    /**
     * @return the OriginalProject
     */
    public Project getOriginalProject() {
        return OriginalProject;
    }

    /**
     * @param OriginalProject the OriginalProject to set
     */
    public void setOriginalProject(Project OriginalProject) {
        this.OriginalProject = OriginalProject;
    }

    /**
     * @return the workingPool
     */
    public ArrayList<OperationProjectActivity> getWorkingPool() {
        return workingPool;
    }

    /**
     * @param workingPool the workingPool to set
     */
    public void setWorkingPool(ArrayList<OperationProjectActivity> workingPool) {
        this.workingPool = workingPool;
    }

    /**
     * @return the FinishedPool
     */
    public ArrayList<OperationProjectActivity> getFinishedPool() {
        return FinishedPool;
    }

    /**
     * @param FinishedPool the FinishedPool to set
     */
    public void setFinishedPool(ArrayList<OperationProjectActivity> FinishedPool) {
        this.FinishedPool = FinishedPool;
    }

    /**
     * @return the ProjectHasFinished
     */
    public boolean isProjectHasFinished() {
        return ProjectHasFinished;
    }

    /**
     * @param ProjectHasFinished the ProjectHasFinished to set
     */
    public void setProjectHasFinished(boolean ProjectHasFinished) {
        this.ProjectHasFinished = ProjectHasFinished;
    }

    /**
     * @return the StepLog
     */
    public HashMap<Integer, String> getStepLog() {
        return StepLog;
    }

    /**
     * @param StepLog the StepLog to set
     */
    public void setStepLog(HashMap<Integer, String> StepLog) {
        this.StepLog = StepLog;
    }

    /**
     * @return the Pool
     */
    public ArrayList<OperationProjectActivity> getPool() {
        return Pool;
    }

    /**
     * @param Pool the Pool to set
     */
    public void setPool(ArrayList<OperationProjectActivity> Pool) {
        this.Pool = Pool;
    }

    /**
     * @return the PES
     */
    public ProjectExcutionStats getPES() {
        return PES;
    }

    /**
     * @return the CPCapsule
     */
    public CriticalPathCapsule getCPCapsule() {
        return CPCapsule;
    }

    /**
     * @return the EndingActivityOPANames
     */
    public ArrayList<String> getEndingActivityOPANames() {
        return EndingActivityOPANames;
    }

    /**
     * @param EndingActivityOPANames the EndingActivityOPANames to set
     */
    public void setEndingActivityOPANames(ArrayList<String> EndingActivityOPANames) {
        this.EndingActivityOPANames = EndingActivityOPANames;
    }
}
