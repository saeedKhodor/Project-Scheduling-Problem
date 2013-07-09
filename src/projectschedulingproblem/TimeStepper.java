/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package projectschedulingproblem;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author saeed
 */
public class TimeStepper implements Runnable{

    Logger Log;
    Portfolio Pfolio;
    OperationResources OPR;
    private ArrayList<String> ExcutionStepsLog;
    ArrayList<OperationProjectActivity> ExcutionList;
    ArrayList<OperationProject> OPP;
    DecimalFormat DFormat = new DecimalFormat(MainForm.PrecFormat);
  

    public TimeStepper(Logger log, Portfolio Pfolio) {

        this.Log = log;
        this.Pfolio = Pfolio;
        this.OPR = new OperationResources(Pfolio.getGlobalResources(), this.Log);
        OPR.createOperationReses();
        this.ExcutionList = new ArrayList<OperationProjectActivity>();
        ExcutionStepsLog = new ArrayList<String>();

    }
public ArrayList<OperationProjectActivity> GetAllOPAforGeneticpool(){
    ArrayList<OperationProjectActivity> AllOPAs=new ArrayList<OperationProjectActivity>();
     for (OperationProject OPPs : OPP) {
         AllOPAs.addAll(OPPs.getFinishedPool());
     }
  
    return AllOPAs;
}
    /**
     * this function will prepare all the Excution List memebers at the start of
     * start Function
     *
     *
     */
    public int BuildExcution(ArrayList<OperationProject> OPP, int step,float TimeConsumed) {
        this.ExcutionList.clear(); // rebuilding the excutionList 
        for (OperationProject OPPs : OPP) {
            OPPs.UpdateCurrentPool("0.1", step,TimeConsumed);//

        }
        int y = 1;
        for (OperationProject OPPs : OPP) {
            if (!OPPs.isProjectHasFinished()) {
                // this is not entered when there is any of the  projects still running. 
                y = 0;
            }

            this.ExcutionList.addAll(OPPs.getWorkingPool());
        }

        for (OperationProjectActivity OPA : ExcutionList) {
            if (OPA.getStatus() != OperationProjectActivity.HASSTARTED) {
                OPA.setStatus(OperationProjectActivity.CANSTART);
                Log.appendToLog(Logger.INFORMATION, "TimeStepper:BuildExcution setting " + OPA.getOPAIdentifier(false) + " can start");
            }

        }
        return y;
    }
    @Override
    public void run(){
    Start();
}
    public ArrayList<String> Start() {
        //create operation Porjects 
        OPP = new ArrayList<OperationProject>();
        String StepLog = "";
        for (Project P : Pfolio.getProjects()) {
            OPP.add(new OperationProject(P, Log));
            Log.appendToLog(Logger.INFORMATION, "TimeStepper:Start: Operation Project has been Create for Project" + P.toString());
        }
        
        int x = 0;
        int Numberofsteps = 0;
        float Timeconsumed = 0.0f;
       Timeconsumed= Float.valueOf(DFormat.format(Timeconsumed));
        
ArrayList<String> ProblemResources = new ArrayList<String>();
        while (x != 1) {
            String ExcutionstepLog = "";
            float StepDur = 0.0f;
            boolean Ubnormally = false;
            Numberofsteps++;
            x = BuildExcution(OPP, Numberofsteps,Timeconsumed);
            if (Numberofsteps == 1000000) {
                Log.appendToLog(Logger.INFORMATION, "TimeStepper:Start: Aborting Ubnormally");
                MainForm.CSVier.WriteError("TimeStepper:Start:Ubnormal Finishing for loop ");
                x = 1;
                Ubnormally = true;
            }
            Log.appendToLog(Logger.INFORMATION, "TimeStepper:Start: ExcutionList Rebuilt");
            if (x == 1) {
                if (!Ubnormally) {
                    Log.appendToLog(Logger.INFORMATION, "TimeStepper:Start: All Projects Have FInished Normally");
                    Log.appendToLog(Logger.INFORMATION, "TimeStepper:Start: OperationActivity NumberofSteps " + Numberofsteps + " Time consumed =" + Timeconsumed);
               
                }
                continue;
            }
// check for resource availability
            ProblemResources.clear();
            //*moved the arraylist creation up 
           
            for (OperationProjectActivity OPA : ExcutionList) {
                if (OPA.getStatus() == OperationProjectActivity.HASSTARTED) {// has started doesnt fight for resources
                    Log.appendToLog(Logger.INFORMATION, "TimeStepper:Start: NO Need to check already started OPA " + OPA.getOPAIdentifier(false));
                    //continue;
                } else {
                    // this part will take the resource of each OPA that hasnt started , and check the resources needed , we have passed it as a string from the excel 
                    // so we have used it as is since it easier to manupulate the String and get the Resources and quantityies needed
                    String Res = OPA.getOriginalPA().getResNames();
                    String[] Resnames = Res.split(",");

                    for (int i = 0; i < Resnames.length; i++) {
                        String trimmedResName = Resnames[i].trim();
                        OperationRes OR = this.OPR.getOperationResourcebyName(trimmedResName);

                        float QtyAvailable = OR.getAvailableQuantity();
                        if (QtyAvailable != 0.0) {
                            Log.appendToLog(Logger.INFORMATION, "TimeStepper:Start: QTY Available for resources " + trimmedResName + " is " + QtyAvailable);
                            double QtyNeeded = 0.0;
                            String OPAsFighting = "";
                            for (OperationProjectActivity OPA2 : ExcutionList) {
                                if (OPA2.isthisresourceneeded(trimmedResName)) {

                                    OPAsFighting += OPA2.getOPAIdentifier(false) + ",";
                                    double QtyN = OPA2.getResourceNeededQtybyName(trimmedResName);
                                    QtyNeeded += QtyN;
                                    //Log.appendToLog(Logger.INFORMATION,"TimeStepper:Start: the resource  "+trimmedResName+" is needed by "+OPA2.getOPAIdentifier(false));  
                                }

                                //need to check if this resource is needed by multiple OPAs and their sum is higher than the availabel quantity
                            }
                            if (QtyNeeded > QtyAvailable) {
                                String OPAFight = trimmedResName + "," + QtyNeeded + "," + QtyAvailable + "," + OPAsFighting;
                                Log.appendToLog(Logger.INFORMATION, "TimeStepper:Start: OPAFight " + OPAFight);
                                // this line is very important since it will check if there are in the fight any already waiting or has started OPAs , since it is useless to fight them 
                                if (!ProblemResources.contains(OPAFight) && !DoesStringhasStartedOPA(OPAFight) && !isanyOPAwaitingforResource(OPAFight)) {
                                    ProblemResources.add(OPAFight);

                                }
                            }
                        }
                    }

                }
            }
            //solve Fight
            for (String RName : ProblemResources) {
                Log.appendToLog(Logger.IMPORTANT, "TimeStepper:Start: the Resource  " + RName + " have caused a fight in step " + Numberofsteps);
                solveFight(RName, ExcutionList); // this should set the OPAs that are waiting   
            }
            //finish PAs that are less then or equal the excustiontime

//             for(OperationProjectActivity OPA :this.ExcutionList){
//                 Log.appendToLog(Logger.IMPORTANT,"TimeStepper:Start: OPA Statuses "+OPA.getOPAIdentifier(false)+" Status "+OPA.getStatus());
//             }

            for (OperationProjectActivity OPA : this.ExcutionList) {

                if (OPA.getStatus() != OperationProjectActivity.ISWAITINGFORRESOURCES && OPA.getStatus() != OperationProjectActivity.HASSTARTED) {
                    // the Below Linr is very important since it will check if the OPA that havent has a fight, if it can researve all the resources needed.
                    if (this.OPR.canOPALockAllItsResources(OPA)) {
                        OPA.setStatus(OperationProjectActivity.HASSTARTED);
                        // set the PES statistics
                        ProjectExcutionStats PES=OPA.getParent().getPES();
                        int PESStatus=PES.getPESStatus();
                        if(PESStatus==ProjectExcutionStats.UNTOUCHED){
                            PES.setProjectStartTime(Timeconsumed);
                            PES.setPESStatus(ProjectExcutionStats.STARTED);
                              Log.appendToLog(Logger.IMPORTANT, "TimeStepper:Start:  PES the Project  " +PES.getProjectID() + " Started at "+ Timeconsumed);            
                        }
                        //lock resources
                        this.OPR.LockResourceOPA(OPA, Numberofsteps);
                    } else {
                        OPA.setStatus(OperationProjectActivity.ISWAITINGFORRESOURCES);
                    }

                }
            }
            
            StepDur = calculateDurationofstep();// this has to be in between since the function  depends on the set status to has finished 
            Timeconsumed += StepDur;
            Timeconsumed= Float.valueOf(DFormat.format(Timeconsumed));
            String StepLogFinished = "";
            Log.appendToLog(Logger.IMPORTANT, "TimeStepper:Start:  Step Dur " + StepDur);
            for (OperationProjectActivity OPA : this.ExcutionList) {
                if (OPA.getStatus() == OperationProjectActivity.HASSTARTED) {

                    Log.appendToLog(Logger.IMPORTANT, "TimeStepper:Start: OPA Before Statuses " + OPA.getOPAIdentifier(false) + " Status " + OPA.getStatus());

                    if (OPA.getCurrentDuration() <= StepDur) {
                        OPA.setStatus(OperationProjectActivity.HASFINISHED);
                       
                        //release resources
                        this.OPR.ReleaseResourceOPA(OPA, Numberofsteps);
                        //finish the OPA
                        // add to Step Log
                        StepLogFinished += OPA.getOPAIdentifier(false);
                    } else {
                        if (StepDur > OPA.getCurrentDuration()) {
                            OPA.setCurrentDuration(StepDur - OPA.getCurrentDuration());
                        } else {
                            Log.appendToLog(Logger.IMPORTANT, "TimeStepper:Start: the stepDUR<Current for  OPA" + OPA.getOPAIdentifier(false) + "  Dur " + OPA.getCurrentDuration());
                        }
                    }

                    Log.appendToLog(Logger.IMPORTANT, "TimeStepper:Start: OPA AFTER Statuses " + OPA.getOPAIdentifier(false) + " Status " + OPA.getStatus());
                }
            }
            ExcutionstepLog = ExcutionstepLog + StepDur + "," + StepLogFinished;
            getExcutionStepsLog().add(Numberofsteps + "," + ExcutionstepLog);
            Log.appendToLog(Logger.IMPORTANT, "TimeStepper:Start: Add stepLog " + Numberofsteps + "  steplog " + ExcutionstepLog);


        }

        System.gc();
        System.runFinalization();
        return this.getExcutionStepsLog();
    }

    public void generateReportofExcution() {
        
        for (String s : getExcutionStepsLog()) {
           String line = "";

            String[] Exlog = s.split(",");
            if (Exlog.length < 2) {
                return;
            }
            line += "t=" + Exlog[0] + " stepduration=" + Exlog[1];

            line = line + " finished Activities are " + Exlog[2]+"\n";
     Log.appendToLog(Logger.HAS_TO_SHOW, "TimeStepper:generateReportofExcution " + line);
        

        }
   }
    //this function will check if one of the fighters has a status of Has started since it is not resonable to fight one that has already started

    public boolean DoesStringhasStartedOPA(String str) {
        String SplitString[] = str.split(",");
        for (int i = 3; i < SplitString.length; i++) {
            for (OperationProjectActivity Opa : ExcutionList) {
                if (Opa.getOPAIdentifier(false).equalsIgnoreCase(SplitString[i]) && Opa.getStatus() == OperationProjectActivity.HASSTARTED) {
                    return false;

                }
            }
        }
        return true;
    }

    //this function will check if one of the fighters is of status iswaiting for resources since it is useless to fight with someone who is already waiting for a resource
    public boolean isanyOPAwaitingforResource(String str) {
        String SplitString[] = str.split(",");
        for (int i = 3; i < SplitString.length; i++) {
            for (OperationProjectActivity Opa : ExcutionList) {
                if (Opa.getOPAIdentifier(false).equalsIgnoreCase(SplitString[i]) && Opa.getStatus() == OperationProjectActivity.ISWAITINGFORRESOURCES) {
                    return false;

                }
            }
        }
        return true;
    }

    public void solveFight(String ResFight, ArrayList<OperationProjectActivity> AL) {
        String SplitString[] = ResFight.split(",");
        String ResName = SplitString[0];
        Random generator = new Random();
        float  QuantityNeeded =(float) Double.parseDouble(SplitString[1]);
        float QuantityAvailable = (float)Double.parseDouble(SplitString[2]);
        ArrayList<OperationProjectActivity> Fighters = new ArrayList<OperationProjectActivity>();

        OperationProjectActivity MinPA = null;

        for (int i = 3; i < SplitString.length; i++) {
            for (OperationProjectActivity Opa : AL) {
                if (Opa.getOPAIdentifier(false).equalsIgnoreCase(SplitString[i])) {
                    Fighters.add(Opa);
                    Log.appendToLog(Logger.INFORMATION, "TimeStepper:solveFight: add OPA " + SplitString[i] + " to Fighters");
                    break;
                }

            }
        }
        float Min = 99999.0f;
        boolean allEqual = true;
        float val = 0.0f;
        int x = 0;
        while (x != -1) {
            for (OperationProjectActivity OPa : Fighters) {

                 float current =(float) OPa.getResourceNeededQtybyName(ResName);
                 
                if (val == 0.0) {
                    val = current;
                }
                if (val==current) {
                    allEqual = false;
                    Log.appendToLog(Logger.INFORMATION, "TimeStepper:solveFight: they are not equal " + val + "Cuurent " + current);
                }
                if (current <= Min) {
                    Min = current;
                    MinPA = OPa;
                }
            }

            if (Min <= QuantityAvailable) {
                if (allEqual) {
                    
                    int randomIndex = generator.nextInt(Fighters.size());
                    OperationProjectActivity opa = Fighters.get(randomIndex);
                    opa.setStatus(OperationProjectActivity.CANSTART);
                    QuantityAvailable -= Min;
                    Fighters.remove(opa);
                    Log.appendToLog(Logger.INFORMATION, "TimeStepper:solveFight: all equal and randomizing " + opa.getOPAIdentifier(false) + " can start and use  " + ResName);
                } else {
                    Log.appendToLog(Logger.INFORMATION, "TimeStepper:solveFight: Opa " + MinPA.getOPAIdentifier(false) + " can start and use  " + ResName);
                    MinPA.setStatus(OperationProjectActivity.CANSTART);
                    QuantityAvailable -= Min;
                    Fighters.remove(MinPA);
                }
            } else if (Min > QuantityAvailable) {
                x = -1;
                for (OperationProjectActivity OPa : Fighters) {
                    OPa.setStatus(OperationProjectActivity.ISWAITINGFORRESOURCES);
                    Log.appendToLog(Logger.INFORMATION, "TimeStepper:solveFight: Opa " + OPa.getOPAIdentifier(false) + " is waiting for  " + ResName);
                }
            }
        }
        Fighters.clear();
        System.gc();
    }

    public float calculateDurationofstep() {
        float MinDuration = Float.MAX_VALUE;
        for (OperationProjectActivity OPA : ExcutionList) {
            if (OPA.getStatus() == OperationProjectActivity.HASSTARTED) {
                float Dur = OPA.getCurrentDuration();
                Log.appendToLog(Logger.INFORMATION, "TimeStepper:solveFight: calculateDurationofstep OPA " + OPA.getOPAIdentifier(false) + " DUr " + Dur + " MD  " + MinDuration);
                if (Dur <= MinDuration) {
                    MinDuration = Dur;
                }

            }
        }
        if (MinDuration == Float.MAX_VALUE) {
            return 0.0f;
        }
        return MinDuration;
    }

    /**
     * @return the ExcutionStepsLog
     */
    public ArrayList<String> getExcutionStepsLog() {
        return ExcutionStepsLog;
    }

    /**
     * @param ExcutionStepsLog the ExcutionStepsLog to set
     */
    public void setExcutionStepsLog(ArrayList<String> ExcutionStepsLog) {
        this.ExcutionStepsLog = ExcutionStepsLog;
    }

    public void GenerateLogFiles() {
        
        for(OperationProject OP:this.OPP){
            
            MainForm.CSVier.WriteExcutionProjecttoTxt(OP);
        }
        
    }
}
