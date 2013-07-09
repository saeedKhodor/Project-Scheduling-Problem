/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package projectschedulingproblem;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 *
 * @author saeed
 */
public class Chromosome {

    private ArrayList<GeneticOPA> Elements;
    private double Duration = 0.0;
    private double Fitness;// 1/Duration
    private Generation GenParent;
    private GeneticPool GP;
    private GeneticsVariablesCapsule GVC;
    DecimalFormat DFormat = new DecimalFormat(MainForm.PrecFormat);
    private boolean withgrouping;
    private double GenProbability;// this is the probability chosen by the generation 
    private ChromosomeExcutionStats CHES;
    private boolean HasbeenMutated = false;

    public Chromosome(Generation Gen, boolean withgrouping) {

        this.GenParent = Gen;
        this.GP = GenParent.getGP();
        this.GVC = GenParent.getGVC();
        this.Elements = new ArrayList<GeneticOPA>();
        this.withgrouping = withgrouping;
        CHES = new ChromosomeExcutionStats(this);


    }

    public Chromosome(Generation Gen, Chromosome Chr) {

        this.GenParent = Gen;
        this.GP = GenParent.getGP();
        this.GVC = GenParent.getGVC();
        this.Elements = Chr.getElements();
        CHES = new ChromosomeExcutionStats(this);
        if (GVC.isFeasilbiltyON()) {
            this.ChromosomeApplyFeasibility();

        }
        this.setDuration();
    }

    public void Mutate() {

        int i = 0;
        for (GeneticOPA Gopa : Elements) {
            //Double Pcntage=GeneticsMain.GeneticsGenerateNumberPercentage();
            float Mutationfactor = this.GVC.getMutationFactor();
            if (GeneticsMain.GeneticsFlipaCoin(Mutationfactor)) {
                int Gopasize = this.GP.getGOPAPool().size();
                GeneticOPA GoPA = this.GP.getGOPAPool().get(GeneticsMain.GeneticsGenerateNumberabovezero(Gopasize));
                this.GenParent.getLog().appendToLog(Logger.INFORMATION, "Chromosome:Mutate: Mutating GOPA " + Gopa.getOPAIdentifier(false) + " at i " + i + " with " + GoPA.getOPAIdentifier(false));
                Elements.remove(i);
                Elements.add(i, GoPA);
                setHasbeenMutated(true);
                return;
            }
            i++;
        }


    }

    public void CreateFirstGenerationGeneticOPAs(int MethodMode) {

        int RandomorNot = GVC.getFirstGenChoosingMode();
        if (MethodMode == GeneticsMain.LMMETHODMODE) {
            if (RandomorNot == GeneticsMain.RANDOMFIRSTGEN) {
                if (withgrouping) {
                    for (GeneticOPA OPa : GP.getGOPAPool()) {

                        float Prob = OPa.getProbabiltytobPicked();
                        boolean choosme = GeneticsMain.GeneticsFlipaCoin(Prob);
                        if (choosme) {
                            this.Elements.add(OPa);
                        }
                    }
                    Collections.shuffle(Elements);

                } else {
                    for (GeneticOPA OPa : GP.getGOPAPool()) {

                        this.Elements.add(OPa);
                    }

                    Collections.shuffle(Elements);
                }
                if (GVC.isFeasilbiltyON()) {
                    this.ChromosomeApplyFeasibility();

                }

            } else {
                // here LMMETHODMODE and NOTRANDOMFIRSTGEN
                for (GeneticOPA OPa : GP.getGOPAPool()) {

                    this.Elements.add(OPa);
                }

                RunLMandNotRandom();
            }
        } else if (MethodMode == GeneticsMain.EMMETHODMODE) {
            if (RandomorNot == GeneticsMain.RANDOMFIRSTGEN) {
                for (GeneticOPA OPa : GP.getGOPAPool()) {

                    double Prob = (double) OPa.getProbabiltytobPicked();
                    boolean choosme = GeneticsMain.GeneticsFlipaCoin(Prob);
                    if (choosme) {
                        this.Elements.add(OPa);
                    }
                }
                Collections.shuffle(Elements);
                if (GVC.isFeasilbiltyON()) {
                    this.ChromosomeApplyFeasibility();

                }
            } else {

                if (RandomorNot == GeneticsMain.NOTRANDOMFIRSTGEN) {
                    for (GeneticOPA OPa : GP.getGOPAPool()) {

                        double Prob = (double) OPa.getProbabiltytobPicked();
                        boolean choosme = GeneticsMain.GeneticsFlipaCoin(Prob);
                        if (choosme) {
                            this.Elements.add(OPa);
                        }
                    }
                    Collections.shuffle(Elements);

                    this.ChromosomeApplyFeasibility();

                }


                // here is EM and NotRandom 
            }
        }

        this.setDuration();
    }

    public Double RunforDuration() {

        OperationResources OPR = new OperationResources(this.GenParent.getGMParent().getPfolio().getGlobalResources(), this.GenParent.getLog());
        OPR.createOperationReses();
        double StepDur = 0.0;
        ArrayList<GeneticOPA> ExcutionPool = new ArrayList<GeneticOPA>();
        ExcutionPool.addAll(Elements);


        for (GeneticOPA GOPA : ExcutionPool) {
            OperationProject OPP = GOPA.getOperationProjectParentofGOPA();
            // here we initialize the starting of CHES
            if (!this.CHES.getOppsAdded().contains(OPP)) {

                this.getCHES().addProject(OPP);
            }
            GOPA.ResetGOPA();
        }
        ArrayList<GeneticOPA> WorkingPool = new ArrayList<GeneticOPA>();
        ArrayList<GeneticOPA> FinishedPool = new ArrayList<GeneticOPA>();
        // will set all the activities that can start to start the project
        for (GeneticOPA GOPA : ExcutionPool) {
            if (GOPA.IscanStart()) {
                GOPA.setStatus(GeneticOPA.CANSTART);
            }

        }
        boolean AllhaveFinished = false;
        Double TimeConsumed = 0.0;
        int Ubnormal = 0;

        this.GenParent.getLog().appendToLog(Logger.INFORMATION, "Chromosome:getDuration: HASSSSS STARTED");
        while (!AllhaveFinished) {
            if (Ubnormal == 100000) {
                AllhaveFinished = true;
                this.GenParent.getLog().appendToLog(Logger.INFORMATION, "Chromosome:RunforDuration:Ubnormal");
                MainForm.CSVier.WriteError("Chromosome:RunforDuration:Ubnormal Finishing for loop");
                continue;
            }



            ArrayList<String> ProblemResources = new ArrayList<String>();
            for (GeneticOPA GOPA : ExcutionPool) {
                // check for resource availability

                if (GOPA.getStatus() == GeneticOPA.HASSTARTED) {// has started doesnt fight for resources
                    //continue;
                } else if (GOPA.getStatus() == GeneticOPA.CANSTART) {

                    String Res = GOPA.getOriginalOPA().getOriginalPA().getResNames();
                    String[] Resnames = Res.split(",");

                    for (int i = 0; i < Resnames.length; i++) {
                        String trimmedResName = Resnames[i].trim();
                        OperationRes OR = OPR.getOperationResourcebyName(trimmedResName);

                        double QtyAvailable = (double) OR.getAvailableQuantity();
                        if (QtyAvailable != 0.0) {
                            //Log.appendToLog(Logger.INFORMATION, "TimeStepper:Start: QTY Available for resources " + trimmedResName + " is " + QtyAvailable);
                            double QtyNeeded = 0.0;
                            String OPAsFighting = "";
                            for (GeneticOPA OPA2 : ExcutionPool) {
                                if (OPA2.isthisresourceneeded(trimmedResName) && OPA2.getStatus() == GeneticOPA.CANSTART) {

                                    OPAsFighting += OPA2.getOPAIdentifier(false) + ",";
                                    double QtyN = (double) OPA2.getResourceNeededQtybyName(trimmedResName);
                                    QtyNeeded += QtyN;
                                    //Log.appendToLog(Logger.INFORMATION,"TimeStepper:Start: the resource  "+trimmedResName+" is needed by "+OPA2.getOPAIdentifier(false));  
                                }

                                //need to check if this resource is needed by multiple OPAs and their sum is higher than the availabel quantity
                            }
                            if (QtyNeeded > QtyAvailable) {
                                String OPAFight = trimmedResName + "," + QtyNeeded + "," + QtyAvailable + "," + OPAsFighting;

                                // this line is very important since it will check if there are in the fight any already waiting or has started OPAs , since it is useless to fight them 
                                if (!ProblemResources.contains(OPAFight) && !DoesStringhasStartedOPA(OPAFight, ExcutionPool) && !isanyOPAwaitingforResource(OPAFight, ExcutionPool)) {
                                    ProblemResources.add(OPAFight);

                                }
                            }
                        }
                    }

                }

            }
            for (String RName : ProblemResources) {

                solveFight(RName, ExcutionPool); // this should set the OPAs that are waiting   
            }

            for (GeneticOPA GOPA : ExcutionPool) {

                if (GOPA.getStatus() == GeneticOPA.CANSTART) {
                    // the Below Linr is very important since it will check if the OPA that havent has a fight, if it can researve all the resources needed.
                    if (OPR.canOPALockAllItsResources(GOPA)) {
                        GOPA.setStatus(GeneticOPA.HASSTARTED);
                        //lock resources
                        // check if u need to register that Project has started
                        this.getCHES().checkGOPA(GOPA, TimeConsumed.floatValue());
                        OPR.LockResourceOPA(GOPA);
                    } else {
                        GOPA.setStatus(GeneticOPA.ISWAITINGFORRESOURCES);
                    }

                }
            }
            StepDur = calculateDurationofstep(ExcutionPool);
            TimeConsumed += StepDur;
            TimeConsumed = Double.valueOf(DFormat.format(TimeConsumed));
            // Milestone End of check for resource availability

            for (GeneticOPA GOPA : ExcutionPool) {
                if (GOPA.getStatus() == GeneticOPA.HASSTARTED) {

                    //Log.appendToLog(Logger.IMPORTANT, "TimeStepper:Start: OPA Before Statuses " + GOPA.getOPAIdentifier(false) + " Status " + OPA.getStatus());
                    if (GOPA.getCurrentDuration() <= StepDur) {
                        GOPA.setStatus(GeneticOPA.HASFINISHED);
                        this.getCHES().checkGOPA(GOPA, TimeConsumed.floatValue());
                        //release resources
                        OPR.ReleaseResourceOPA(GOPA);
                        if (!FinishedPool.contains(GOPA)) {
                            this.GenParent.getLog().appendToLog(Logger.INFORMATION, "Chromosome:RunforDuration:Adding  Gopa to finished pool " + GOPA.getOPAIdentifier(false));
                            FinishedPool.add(GOPA);
                        }
                        //finish the OPA
                        // add to Step Log

                    } else {
                        if ( GOPA.getCurrentDuration()> StepDur) {
                            Double Res = GOPA.getCurrentDuration() -StepDur ;
                            GOPA.setCurrentDuration(Res.floatValue());
                        } else {
                            //  Log.appendToLog(Logger.IMPORTANT, "TimeStepper:Start: the stepDUR<Current for  OPA" + OPA.getOPAIdentifier(false) + "  Dur " + OPA.getCurrentDuration());
                        }
                    }

                    //  Log.appendToLog(Logger.IMPORTANT, "TimeStepper:Start: OPA AFTER Statuses " + OPA.getOPAIdentifier(false) + " Status " + OPA.getStatus());
                }
            }
            for (GeneticOPA GOPA : ExcutionPool) {
                if (GOPA.getStatus() != GeneticOPA.HASSTARTED && GOPA.getStatus() != GeneticOPA.HASFINISHED) {

                    if (arePredecessorsFinished(GOPA, ExcutionPool)) {
                        this.GenParent.getLog().appendToLog(Logger.INFORMATION, "Chromosome:getDuration: GOPA : ExcutionPool can start =" + GOPA.getOPAIdentifier(false));

                        GOPA.setStatus(GeneticOPA.CANSTART);
                    } else {
                        GOPA.setStatus(GeneticOPA.UNSTARTED);
                    }

                }
            }

            // Milestone check if all Tasks are FinishedPool
            boolean AllhaveFinishedtemp = true;
            for (GeneticOPA Gopa : ExcutionPool) {
                if (Gopa.getStatus() != GeneticOPA.HASFINISHED) {
                    AllhaveFinishedtemp = false;
                    break;
                }
            }
            AllhaveFinished = AllhaveFinishedtemp;


            Ubnormal++;
        }

        //Elements = FinishedPool;
        String s = "FinishedPool Elements are ";
        for (GeneticOPA Gopa : Elements) {
            s += Gopa.getOPAIdentifier(false);

        }
        this.GenParent.getLog().appendToLog(Logger.INFORMATION, "Chromosome:GetDuration: Elements Elements are =" + s);

        String s1 = "FinishedPool Elements are ";
        for (GeneticOPA Gopa : FinishedPool) {
            s1 += Gopa.getOPAIdentifier(false);

        }
        this.GenParent.getLog().appendToLog(Logger.INFORMATION, "Chromosome:GetDuration: FinishedPool Elements are =" + s1);

        return TimeConsumed;
    }

    public GeneticOPA getGOPAbyNAme(String Name) {

        for (GeneticOPA Gopa : Elements) {
            if (Gopa.getOPAIdentifier(false).equalsIgnoreCase(Name)) {
                return Gopa;
            }
        }
        return null;
    }

    public final ArrayList<GeneticOPA> ChromosomeApplyFeasibility() {
        // check if all the  are there
        int OrdinaryAlterationValues = 0;
        int FeedbackAlterationValues = 0;
        int totalnumberofOrdinariesinTemporary = 0;
        int totalnumberoffeedbacksinTemporary = 0;
        ArrayList<GeneticOPA> Temporary = new ArrayList<GeneticOPA>();
        Temporary.addAll(Elements);
        
        this.GenParent.getLog().appendToLog(Logger.INFORMATION, " Chromosome ApplyFeasibility for Chr " + this.toString());
        ArrayList<GeneticOPA> GOPAppool = this.GP.getGOPAPool();

//           this.GenParent.getLog().appendToLog(Logger.INFORMATION, "Chromosome:ChromosomeApplyFeasibility: we have ");
        //  while(checkFeasibility()!=-1){
        int TemporaryDifference = GOPAppool.size() - Temporary.size();
        for (int i = 0; i < GOPAppool.size(); i++) {


            GeneticOPA Gopa = GOPAppool.get(i);
            this.GenParent.getLog().appendToLog(Logger.INFORMATION, "Chromosome:ChromosomeApplyFeasibility: checking for  Ordinary GOPA " + Gopa.getOPAIdentifier(false));
            int Occurences = Collections.frequency(Temporary, Gopa);

            if (Occurences == 0 && !Gopa.isfeedback()) {
// here we have a missing, need to add it to the pool

                Temporary.add(Gopa);
                OrdinaryAlterationValues++;

                this.GenParent.getLog().appendToLog(Logger.INFORMATION, "Chromosome:ChromosomeApplyFeasibility: we have a zero Occurence for OPA " + Gopa.getOPAIdentifier(false) + "Ordinary Alternative Value" + OrdinaryAlterationValues + "Feedback Alternative Value" + FeedbackAlterationValues + " Elementssize " + Elements.size() + " Temp size " + Temporary.size());

            } else if (Occurences > 1) {
                int OC = Occurences - 1;

                Temporary.removeAll(Collections.singleton(Gopa));
                Temporary.add(Gopa);

                if (!Gopa.isfeedback()) {
                    OrdinaryAlterationValues -= OC;
                } else {
                    FeedbackAlterationValues -= OC;
                }
                this.GenParent.getLog().appendToLog(Logger.INFORMATION, "Chromosome:ChromosomeApplyFeasibility: we have " + Occurences + " prev Occurences for OPA " + Gopa.getOPAIdentifier(false) + " no changed to " + Collections.frequency(Temporary, Gopa));

            } else {
                this.GenParent.getLog().appendToLog(Logger.INFORMATION, "Chromosome:ChromosomeApplyFeasibility: we have " + Occurences + " Occurences for OPA " + Gopa.getOPAIdentifier(false));

            }
        }

        this.GenParent.getLog().appendToLog(Logger.INFORMATION, "Chromosome:ChromosomeApplyFeasibility: we have done the follwoing alterations " + "Ordinary Alternative Value" + OrdinaryAlterationValues + "Feedback Alternative Value" + FeedbackAlterationValues);

        // at this Point there are no duplicates or missing ordinary OPas
        // next we need to Check  what Feedbacks  dont have there predecessor found  we will use the FCID and the Name to make sure 
        ArrayList<GeneticOPA> Feedbacks = getFeedbacksinList(Temporary);
        totalnumberoffeedbacksinTemporary = Feedbacks.size();
        totalnumberofOrdinariesinTemporary = getCountofOrdinaryGOpasinList(Temporary);

        boolean canremove = true;
        int threshold = 2;
        int currentval = 0;// this value will make the whil eloop when it reaches the threshold

        while (currentval < threshold) {
            canremove = false;

            for (int i = 0; i < Feedbacks.size(); i++) {
                GeneticOPA MainGopa = Feedbacks.get(i);

                ArrayList<String> Gpred = MainGopa.getPredOPAIDs();
                String GFCID = MainGopa.getFCID();
                if (!MainGopa.isFeddbackStarterOPA()) {// starter Opas always have their predecessors since they are promary 

                    for (String Pred : Gpred) {
                        boolean hasFoundPred = false;
                        for (int j = 0; j < Feedbacks.size(); j++) {
                            GeneticOPA SearcherGopa = Feedbacks.get(j);
                            String SearcherFCID = SearcherGopa.getFCID();
                            if (SearcherGopa.getOPAIdentifier(false).equalsIgnoreCase(Pred) && SearcherFCID.equalsIgnoreCase(GFCID)) {
                                hasFoundPred = true;
                            }

                        }
                        if (!hasFoundPred) {
                            if (Temporary.remove(MainGopa)) {
                                Feedbacks.remove(i);
                                FeedbackAlterationValues--;
                                this.GenParent.getLog().appendToLog(Logger.INFORMATION, "Chromosome:ChromosomeApplyFeasibility: Removing Feedback since not all pred " + Pred + "are found for OPA " + MainGopa.getOPAIdentifier(false) + GFCID + "OAV" + OrdinaryAlterationValues + "FAV" + FeedbackAlterationValues);
                                canremove = true;
                            }
                        }

                    }
                }
            }
            if (!canremove) {

                currentval++;
            }
        }
        Random R = new Random();
        int extraadditions = 0;
        if (TemporaryDifference > 0) {
            extraadditions = this.GVC.getMethodMode() == GeneticsMain.EMMETHODMODE ? R.nextInt(TemporaryDifference) : 0;
        }

        Feedbacks.clear();
        int neededextraalteration = FeedbackAlterationValues + OrdinaryAlterationValues - extraadditions;
        if (Math.abs(neededextraalteration) > totalnumberoffeedbacksinTemporary) {
            neededextraalteration = totalnumberoffeedbacksinTemporary;
        }
        if (neededextraalteration < 0) {
            // we need to add some feedbacks

            ArrayList<GeneticOPA> feedbacksinsourceandnotindes = getfeedbacksinsourceandnotindest(GOPAppool, Temporary);
            boolean passedaloopanddidntdoalteration = false;
            while (neededextraalteration < 0 && !passedaloopanddidntdoalteration) {
                passedaloopanddidntdoalteration = true;
                for (GeneticOPA Gopa : feedbacksinsourceandnotindes) {
                    ArrayList<String> Gpred = Gopa.getPredOPAIDs();
                    String GFCID = Gopa.getFCID();
                    boolean caninsert = false;
                    for (String Pred : Gpred) {
                        boolean hasFoundPred = false;
                        for (int j = 0; j < Temporary.size(); j++) {
                            GeneticOPA SearcherGopa = Temporary.get(j);
                            String SearcherFCID = SearcherGopa.getFCID();
                            if (Gopa.isFeddbackStarterOPA()) {
                                if (SearcherGopa.getOPAIdentifier(false).equalsIgnoreCase(Pred)) {
                                    hasFoundPred = true;
                                }
                            } else {
                                if (SearcherGopa.getOPAIdentifier(false).equalsIgnoreCase(Pred) && SearcherFCID.equalsIgnoreCase(GFCID)) {
                                    hasFoundPred = true;
                                }
                            }


                        }
                        if (hasFoundPred && !Temporary.contains(Gopa)) {
                            caninsert = true;
                            break;
                        }

                    }
                    if (caninsert) {
                        if (!Temporary.contains(Gopa)) {
                            Temporary.add(Gopa);

                            neededextraalteration++;
                            this.GenParent.getLog().appendToLog(Logger.INFORMATION, "Chromosome:ChromosomeApplyFeasibility: adding  Feedback " + Gopa.getOPAIdentifier(false) + Gopa.getFCID() + " stillneededalteration" + neededextraalteration);

                        }
                        passedaloopanddidntdoalteration = false;
                    }
                    if (neededextraalteration >= 0) {
                        break;
                    }

                }
                if (passedaloopanddidntdoalteration) {
                    this.GenParent.getLog().appendToLog(Logger.INFORMATION, "Chromosome:ChromosomeApplyFeasibility: quitting loop passed aloop and didnt do alteration, neededextraalteration " + neededextraalteration);

                }

            }



        }

// Arranging ... here i use a temporary Arranger arrayList and i add the temporary list elements one by one after checking if the predecessors are found in the arranger arrayList
        boolean canexit = false;
        int Ubnormal = 0;
        ArrayList<GeneticOPA> Arranger = new ArrayList<GeneticOPA>();
        int originalTempsize = Temporary.size();
        while (!canexit) {
            if (Ubnormal == 1000000) {

                MainForm.CSVier.WriteError("TimeStepper:Start:Ubnormal Finishing for loop ");
                break;
            }
            boolean haspassedwithnoalteration = false;

            for (int j = 0; j < Temporary.size(); j++) {
                GeneticOPA GOpa = Temporary.get(j);
                if (GOpa.IscanStart() && !Arranger.contains(GOpa)) {
                    if (Arranger.add(GOpa)) {
                        Temporary.remove(j);
                    }


                }
            }



            while ((Arranger.size() < originalTempsize)) {
                haspassedwithnoalteration = true;
                for (int j = 0; j < Temporary.size(); j++) {
                    boolean isStartsafterandhavepassedFirstInspection = false;// this one is crititcal since the
                    GeneticOPA TemporaryGopa = Temporary.get(j);
                    if (TemporaryGopa.isStartsafterfeedbackCycle()) {
                        ArrayList<String> Fpreds = TemporaryGopa.getGeneticsFeedbackPredecessors();
                        ArrayList<String> FCids = TemporaryGopa.getGeneticFCIDs();

                        boolean hasfoundall = true;
                        //ArrayList<GeneticOPA> FeedbacksinArranger = getFeedbacksinList(Arranger);
                        for (String Pred : Fpreds) {
                            if (isFeedbackFoundinListbyName(Temporary, Pred, FCids)) {
                                ArrayList<GeneticOPA> allfeedabckVersionsofpred = getallFeedbacksinListbyOrdinaryName(Temporary, Pred, FCids);
                                boolean hasfound = true;
                                for (GeneticOPA Fgopa : allfeedabckVersionsofpred) {
                                    if (!Arranger.contains(Fgopa)) {
                                        hasfound = false;
                                        break;
                                    }

                                }
//                                for (GeneticOPA Fgopa : FeedbacksinArranger) {
//
//                                    String Fgopaname = Fgopa.getOPAIdentifier(true);
//                                    String GFCID = Fgopa.getFCID();
//                                    if (Pred.equalsIgnoreCase(Fgopaname) && FCids.contains(GFCID)) {
//                                        hasfound = true;
//                                    }
//
//                                }
                                if (!hasfound) {
                                    hasfoundall = false;
                                    break;
                                }
                            }
                        }
                        if (hasfoundall) {
                            isStartsafterandhavepassedFirstInspection = true;

                        }


                    }
                    // checking for isfeedbackStarterOPAs they have the  predecessors as ordinary Opas
                    if (TemporaryGopa.isFeddbackStarterOPA()) {
                        ArrayList<String> Gpreds = TemporaryGopa.getPredOPAIDs();
                        boolean hasfoundall = true;
                        for (String Pred : Gpreds) {

                            boolean hasfound = false;
                            for (GeneticOPA EGOPA : Arranger) {
                                String EGOPAName = EGOPA.getOPAIdentifier(false);
                                if (Pred.equalsIgnoreCase(EGOPAName)) {

                                    hasfound = true;
                                }

                            }
                            if (!hasfound) {
                                hasfoundall = false;
                                break;

                            }
                        }
                        if (hasfoundall) {
                            if (!Arranger.contains(TemporaryGopa)) {
                                haspassedwithnoalteration = false;
                                Temporary.remove(j);
                                Arranger.add(TemporaryGopa);
                            }
                        }

                    }

                    if (TemporaryGopa.isfeedback()) {
                        ArrayList<String> Gpreds = TemporaryGopa.getPredOPAIDs();
                        String GFCID = TemporaryGopa.getFCID();
                        boolean hasfoundall = true;
                        for (String Pred : Gpreds) {
                            boolean hasfound = false;
                            for (GeneticOPA EGOPA : Arranger) {
                                String EGOPAName = EGOPA.getOPAIdentifier(false);
                                String EGFCID = EGOPA.getFCID();
                                if (Pred.equalsIgnoreCase(EGOPAName) && GFCID.equalsIgnoreCase(EGFCID)) {

                                    hasfound = true;
                                }

                            }
                            if (!hasfound) {
                                hasfoundall = false;
                                break;

                            }
                        }
                        if (hasfoundall) {
                            if (!Arranger.contains(TemporaryGopa)) {
                                haspassedwithnoalteration = false;
                                Temporary.remove(j);
                                Arranger.add(TemporaryGopa);
                            }
                        }
                        //                   if (Gpred.contains(EGOPAName) && EGFCID.equalsIgnoreCase(GFCID)) {



                    } else {// ordinary GOPA
                        ArrayList<String> Gpreds = TemporaryGopa.getPredOPAIDs();
                        boolean waitsforfeedbackCycle = TemporaryGopa.isStartsafterfeedbackCycle();
                        boolean hasfoundall = true;
                        for (String Pred : Gpreds) {
                            boolean hasfound = false;
                            for (GeneticOPA EGOPA : Arranger) {
                                String EGOPAName = EGOPA.getOPAIdentifier(false);

                                if (Pred.equalsIgnoreCase(EGOPAName)) {

                                    hasfound = true;
                                }

                            }
                            if (!hasfound) {
                                hasfoundall = false;
                                break;

                            }
                        }
                        if (!waitsforfeedbackCycle) {
                            if (hasfoundall) {
                                if (!Arranger.contains(TemporaryGopa)) {
                                    haspassedwithnoalteration = false;
                                    Temporary.remove(j);
                                    Arranger.add(TemporaryGopa);
                                }
                            }
                        } else {

                            if (hasfoundall && isStartsafterandhavepassedFirstInspection) {
                                if (!Arranger.contains(TemporaryGopa)) {
                                    haspassedwithnoalteration = false;
                                    Temporary.remove(j);
                                    Arranger.add(TemporaryGopa);

                                }
                            }

                        }


                    }


                }

            }
            if (checkFeasibility(Arranger, totalnumberofOrdinariesinTemporary)) {
                Temporary.clear();
                Temporary.addAll(Arranger);
                canexit = true;

            }
            Ubnormal++;
        }
        this.Elements.clear();
        Elements.addAll(Temporary);
        System.gc();

        return Temporary;
    }

    public ArrayList<GeneticOPA> getallFeedbacksinListbyOrdinaryName(ArrayList<GeneticOPA> AL, String Name, ArrayList<String> GFCID) {
        ArrayList<GeneticOPA> TempAL = new ArrayList<GeneticOPA>();
        for (GeneticOPA tempGopa : AL) {
            if (tempGopa.isfeedback()) {
                String EGOPAName = tempGopa.getOPAIdentifier(true);
                String EGFCID = tempGopa.getFCID();
                if (EGOPAName.equalsIgnoreCase(Name) && GFCID.contains(EGFCID)) {
                    TempAL.add(tempGopa);
                }
            }
        }
        return TempAL;
    }

    public boolean isFeedbackFoundinListbyName(ArrayList<GeneticOPA> AL, String FName, ArrayList<String> GFCID) {


        boolean hasfound = false;
        for (GeneticOPA tempGopa : AL) {
            if (tempGopa.isfeedback()) {
                String EGOPAName = tempGopa.getOPAIdentifier(true);
                String EGFCID = tempGopa.getFCID();
                if (EGOPAName.equalsIgnoreCase(FName) && GFCID.contains(EGFCID)) {

                    hasfound = true;
                    break;
                }
            }
        }

        return hasfound;

    }

    public boolean isFeedbackFoundinList(ArrayList<GeneticOPA> AL, GeneticOPA Gopa) {
        String GOPAName = Gopa.getOPAIdentifier(false);
        String GFCID = Gopa.getFCID();
        boolean hasfound = false;
        for (GeneticOPA tempGopa : AL) {
            String EGOPAName = tempGopa.getOPAIdentifier(false);
            String EGFCID = tempGopa.getFCID();
            if (EGOPAName.equalsIgnoreCase(GOPAName) && GFCID.equalsIgnoreCase(EGFCID)) {

                hasfound = true;
                break;
            }
        }

        return hasfound;

    }

    public ArrayList<GeneticOPA> getfeedbacksinsourceandnotindest(ArrayList<GeneticOPA> Source, ArrayList<GeneticOPA> Dest) {

        ArrayList<GeneticOPA> AL = new ArrayList<GeneticOPA>();
        for (GeneticOPA Gopa : Source) {
            if (!Dest.contains(Gopa) && Gopa.isfeedback()) {
                AL.add(Gopa);
            }
        }

        return AL;
    }

    public double calculateDurationofstep(ArrayList<GeneticOPA> ExcutionPool) {
        double MinDuration = Double.MAX_VALUE;
        for (GeneticOPA Gopa : ExcutionPool) {
            if (Gopa.getStatus() == GeneticOPA.HASSTARTED) {
                double Dur = (double) Gopa.getCurrentDuration();
                //Log.appendToLog(Logger.INFORMATION, "TimeStepper:solveFight: calculateDurationofstep OPA " + OPA.getOPAIdentifier(false) + " DUr " + Dur + " MD  " + MinDuration);
                if (Dur <= MinDuration) {
                    MinDuration = Dur;
                }

            }
        }
        if (MinDuration == Double.MAX_VALUE) {
            return 0.0;
        }
        return MinDuration;
    }

    public void solveFight(String ResFight, ArrayList<GeneticOPA> AL) {
        String SplitString[] = ResFight.split(",");
        String ResName = SplitString[0];
        double QuantityNeeded = Double.parseDouble(SplitString[1]);
        double QuantityAvailable = Double.parseDouble(SplitString[2]);
        ArrayList<GeneticOPA> Fighters = new ArrayList<GeneticOPA>();

        GeneticOPA MinPA = null;

        for (int i = 3; i < SplitString.length; i++) {
            for (GeneticOPA Opa : AL) {
                if (Opa.getStatus() == GeneticOPA.CANSTART) {
                    if (Opa.getOPAIdentifier(false).equalsIgnoreCase(SplitString[i])) {
                        Fighters.add(Opa);
                        //   Log.appendToLog(Logger.INFORMATION, "TimeStepper:solveFight: add OPA " + SplitString[i] + " to Fighters");
                        break;
                    }
                }
            }
        }
        double Min = 99999.0;
        boolean allEqual = true;
        Double val = 0.0;
        int x = 0;
        while (x != -1) {
            for (GeneticOPA OPa : Fighters) {

                double current = (double) OPa.getResourceNeededQtybyName(ResName);
                if (val == 0.0) {
                    val = current;
                }
                if (!val.equals(current)) {
                    allEqual = false;
                    //  Log.appendToLog(Logger.INFORMATION, "TimeStepper:solveFight: they are not equal " + val + "Cuurent " + current);
                }
                if (current <= Min) {
                    Min = current;
                    MinPA = OPa;
                }
            }

            if (Min <= QuantityAvailable) {
                if (allEqual) {
                    Random generator = new Random();
                    int randomIndex = generator.nextInt(Fighters.size());
                    GeneticOPA opa = Fighters.get(randomIndex);
                    opa.setStatus(OperationProjectActivity.CANSTART);
                    QuantityAvailable -= Min;
                    Fighters.remove(opa);
                    //   Log.appendToLog(Logger.INFORMATION, "TimeStepper:solveFight: all equal and randomizing " + opa.getOPAIdentifier(false) + " can start and use  " + ResName);
                } else {
                    //   Log.appendToLog(Logger.INFORMATION, "TimeStepper:solveFight: Opa " + MinPA.getOPAIdentifier(false) + " can start and use  " + ResName);
                    MinPA.setStatus(OperationProjectActivity.CANSTART);
                    QuantityAvailable -= Min;
                    Fighters.remove(MinPA);
                }
            } else if (Min > QuantityAvailable) {
                x = -1;
                for (GeneticOPA OPa : Fighters) {
                    OPa.setStatus(OperationProjectActivity.ISWAITINGFORRESOURCES);
                    //   Log.appendToLog(Logger.INFORMATION, "TimeStepper:solveFight: Opa " + OPa.getOPAIdentifier(false) + " is waiting for  " + ResName);
                }
            }
        }
    }

    public boolean isanyOPAwaitingforResource(String str, ArrayList<GeneticOPA> ExcutionPool) {
        String SplitString[] = str.split(",");
        for (int i = 3; i < SplitString.length; i++) {
            for (GeneticOPA Opa : ExcutionPool) {
                if (Opa.getOPAIdentifier(false).equalsIgnoreCase(SplitString[i]) && Opa.getStatus() == GeneticOPA.ISWAITINGFORRESOURCES) {
                    return false;

                }
            }
        }
        return true;
    }

    public boolean DoesStringhasStartedOPA(String str, ArrayList<GeneticOPA> ExcutionPool) {
        String SplitString[] = str.split(",");
        for (int i = 3; i < SplitString.length; i++) {
            for (GeneticOPA Opa : ExcutionPool) {
                if (Opa.getOPAIdentifier(false).equalsIgnoreCase(SplitString[i]) && Opa.getStatus() == GeneticOPA.HASSTARTED) {
                    return false;

                }
            }
        }
        return true;
    }

    public void RunLMandNotRandom() {

        ArrayList<GeneticOPA> ExcutionPool = Elements;
        for (GeneticOPA GOPA : ExcutionPool) {
            GOPA.ResetGOPA();
        }

        ArrayList<GeneticOPA> WorkingPool = new ArrayList<GeneticOPA>();
        ArrayList<GeneticOPA> FinishedPool = new ArrayList<GeneticOPA>();
        // will set all the activities that can start to start the project
        for (GeneticOPA GOPA : ExcutionPool) {
            if (GOPA.IscanStart()) {
                GOPA.setStatus(GeneticOPA.CANSTART);
            }

        }
        boolean AllhaveFinished = false;
        int Ubnormal = 0;
        while (!AllhaveFinished) {
            if (Ubnormal == 100000) {
                AllhaveFinished = true;
                this.GenParent.getLog().appendToLog(Logger.INFORMATION, "Chromosome:RunLMandNotRandom:Ubnormal");
                MainForm.CSVier.WriteError("Chromosome:RunLMandNotRandom:Ubnormal Finishing for loop");
                continue;
            }

            for (GeneticOPA GOPA : ExcutionPool) {
                if (GOPA.getStatus() == GeneticOPA.CANSTART) {
                    this.GenParent.getLog().appendToLog(Logger.INFORMATION, "Chromosome:RunLMandNotRandom: setting GOPA " + GOPA.getOPAIdentifier(false) + " to HasStarted");

                    GOPA.setStatus(GeneticOPA.HASSTARTED);
                }
            }
            ArrayList<GeneticOPA> TempPool = new ArrayList<GeneticOPA>();
            for (GeneticOPA GOPA : ExcutionPool) {
                if (GOPA.getStatus() == GeneticOPA.HASSTARTED) {
                    if (!FinishedPool.contains(GOPA)) {

                        TempPool.add(GOPA);

                    }

                    GOPA.setStatus(GeneticOPA.HASFINISHED);
                }
            }
            // this will arrange the addition to the Finished Pool
            Collections.shuffle(TempPool);

            FinishedPool.addAll(TempPool);
            //FinishedPool.add(GOPA);
            for (GeneticOPA GOPA : ExcutionPool) {
                if (GOPA.getStatus() != GeneticOPA.HASSTARTED && GOPA.getStatus() != GeneticOPA.HASFINISHED) {
                    this.GenParent.getLog().appendToLog(Logger.INFORMATION, "Chromosome:RunLMandNotRandom: GOPA : ExcutionPool =" + GOPA.getOPAIdentifier(false));

                    if (arePredecessorsFinished(GOPA, ExcutionPool)) {

                        GOPA.setStatus(GeneticOPA.CANSTART);
                    }

                }
            }

            // Milestone check if all Tasks are FinishedPool
            boolean AllhaveFinishedtemp = true;
            for (GeneticOPA Gopa : ExcutionPool) {
                if (Gopa.getStatus() != GeneticOPA.HASFINISHED) {
                    AllhaveFinishedtemp = false;
                    break;
                }
            }
            AllhaveFinished = AllhaveFinishedtemp;
            Ubnormal++;
        }

        Elements = FinishedPool;
        String s = "FinishedPool Elements are ";
        for (GeneticOPA Gopa : Elements) {
            s += Gopa.getOPAIdentifier(false);

        }
        this.GenParent.getLog().appendToLog(Logger.INFORMATION, "Chromosome:RunLMandNotRandom: FinishedPool Elements are =" + s);

    }

    public boolean arePredecessorsFinished(GeneticOPA Gopa, ArrayList<GeneticOPA> ExcutionPool) {
// check if 
        boolean GopasHasFinished = true;
        if (Gopa.isStartsafterfeedbackCycle()) {
            ArrayList<GeneticOPA> FeedbackElements = getFeedbacksinChromosome();
            ArrayList<GeneticOPA> neededFeedbackElements = new ArrayList<GeneticOPA>();
            ArrayList<String> Fpreds = Gopa.getGeneticsFeedbackPredecessors();
            ArrayList<String> FCids = Gopa.getGeneticFCIDs();

            for (GeneticOPA Fgopa : FeedbackElements) {
                String Fgopaname = Fgopa.getOPAIdentifier(true);
                String GFCID = Fgopa.getFCID();
                if (Fpreds.contains(Fgopaname) && FCids.contains(GFCID)) {
                    neededFeedbackElements.add(Fgopa);
                }
            }
            for (GeneticOPA Pred : neededFeedbackElements) {
                if (Pred.getStatus() != GeneticOPA.HASFINISHED) {
                    return false;

                }

            }
        }
        if (Gopa.isFeddbackStarterOPA()) {
            ArrayList<String> Gpred = Gopa.getPredOPAIDs();
            for (GeneticOPA EGOPA : ExcutionPool) {
                String EGOPAName = EGOPA.getOPAIdentifier(false);
                if (Gpred.contains(EGOPAName)) {
                    if (EGOPA.getStatus() != GeneticOPA.HASFINISHED) {
                        return false;
                    }

                }
            }
        }
        if (Gopa.isfeedback()) {
            ArrayList<String> Gpred = Gopa.getPredOPAIDs();
            String GFCID = Gopa.getFCID();
            for (GeneticOPA EGOPA : ExcutionPool) {
                String EGOPAName = EGOPA.getOPAIdentifier(false);
                String EGFCID = EGOPA.getFCID();
                if (Gpred.contains(EGOPAName) && EGFCID.equalsIgnoreCase(GFCID)) {
                    if (EGOPA.getStatus() != GeneticOPA.HASFINISHED) {
                        return false;
                    }

                }
            }

        } else {
            ArrayList<String> Gpred = Gopa.getPredOPAIDs();
            for (GeneticOPA EGOPA : ExcutionPool) {
                String EGOPAName = EGOPA.getOPAIdentifier(false);
                if (Gpred.contains(EGOPAName)) {
                    if (EGOPA.getStatus() != GeneticOPA.HASFINISHED) {
                        return false;
                    }

                }
            }

        }


        return GopasHasFinished;
    }

    public void RunEMandNotRandom() {

        ArrayList<GeneticOPA> ExcutionPool = Elements;
        ArrayList<GeneticOPA> WorkingPool = new ArrayList<GeneticOPA>();
        ArrayList<GeneticOPA> Finished = new ArrayList<GeneticOPA>();




    }

    public ArrayList<Double> getGopaProbsforcrossover() {
        ArrayList<Double> Aldoubles = new ArrayList<Double>();
        for (int i = 0; i <= Elements.size(); i++) {

            Aldoubles.add(i, (double) GeneticsMain.GeneticsGenerateNumberPercentage());

        }
        return Aldoubles;
    }

    @Override
    public String toString() {
        String s = "";//" the Chromosome has the following Elements";
        for (GeneticOPA Gopa : Elements) {

            s += Gopa.toString(true) + ";";

        }
        return s;

    }

    /**
     * @return the Elements
     */
    public ArrayList<GeneticOPA> getElements() {
        return Elements;
    }

    /**
     * @param Elements the Elements to set
     */
    public void setElements(ArrayList<GeneticOPA> Elements) {
        this.setElements(Elements);
    }

    public final void setDuration() {

        if (!checkFeasibility(Elements, getCountofOrdinaryGOpasinList(Elements))) {
            this.Duration = Double.POSITIVE_INFINITY;
            this.Fitness = Double.valueOf(DFormat.format(1 / Duration));
            this.GenParent.getLog().appendToLog(Logger.INFORMATION, "Chromosome:getDuration: the Gopa returning Positive Infinity  " + getFitness());
            return;
        }
//        this.GenParent.getLog().appendToLog(Logger.INFORMATION, "Chromosome:getDuration: they are feasable  ");
//        // todo insert the running for the project with Resources
        this.Duration = RunforDuration();
        this.Fitness = Double.valueOf(DFormat.format(1 / Duration));
        this.GenParent.getLog().appendToLog(Logger.INFORMATION, "Chromosome:getDuration: the Gopa returning Duration of  " + Duration);

    }

    /**
     * @return the Duration
     */
    public double getDuration() {

        return Duration;
    }

    public ArrayList<GeneticOPA> getOrdinaryGOpasinList(ArrayList<GeneticOPA> List) {
        ArrayList<GeneticOPA> Al = new ArrayList<GeneticOPA>();

        for (GeneticOPA Gopa : List) {
            if (!Gopa.isfeedback()) {
                Al.add(Gopa);
            }

        }


        return Al;


    }

    public int getCountofOrdinaryGOpasinList(ArrayList<GeneticOPA> List) {
        int i = 0;
        for (GeneticOPA Gopa : List) {
            if (!Gopa.isfeedback()) {
                i++;
            }

        }


        return i;


    }

    public boolean checkFeasibility(ArrayList<GeneticOPA> AL, int CountofOrdinaryGopas) {
        if (AL.isEmpty()) {
            return false;
        }

        // check for multiple occurences
        for (GeneticOPA Gopa : AL) {

            int Occurences = Collections.frequency(AL, Gopa);
            if (Occurences > 1) {
                return false;// mutation was ruining this 
            }

        }
//        // check if all ordinary Gopas are found
        int countofOrdinariesinlist = getCountofOrdinaryGOpasinList(AL);
        if (countofOrdinariesinlist != CountofOrdinaryGopas) {
            return false;
        }

        for (GeneticOPA Gopa : AL) {
            int index = AL.indexOf(Gopa);
            if (Gopa.isStartsafterfeedbackCycle()) {
                if (Gopa.isStartsafterfeedbackCycle()) {

                    ArrayList<String> Fpreds = Gopa.getGeneticsFeedbackPredecessors();
                    ArrayList<String> FCids = Gopa.getGeneticFCIDs();

                    for (GeneticOPA Fgopa : AL) {
                        String Fgopaname = Fgopa.getOPAIdentifier(true);
                        String GFCID = Fgopa.getFCID();
                        if (Fpreds.contains(Fgopaname) && FCids.contains(GFCID)) {
                            if (AL.indexOf(Fgopa) > index) {
                                this.GenParent.getLog().appendToLog(Logger.INFORMATION, "Chromosome:checkFeasibility:isStartsafterfeedbackCycle returniing False ");
                                return false;
                            }
                        }


                    }
                }
            }
            if (Gopa.isFeddbackStarterOPA()) {
                ArrayList<String> Gpred = Gopa.getPredOPAIDs();
                for (GeneticOPA EGOPA : AL) {
                    String EGOPAName = EGOPA.getOPAIdentifier(false);
                    int PreIndex = AL.indexOf(EGOPAName);
                    if (Gpred.contains(EGOPAName) && PreIndex > index) {
                        this.GenParent.getLog().appendToLog(Logger.INFORMATION, "Chromosome:checkFeasibility:isFeddbackStarterOPA returniing False ");

                        return false;

                    }
                }
            }
            if (Gopa.isfeedback()) {
                ArrayList<String> Gpred = Gopa.getPredOPAIDs();
                String GFCID = Gopa.getFCID();
                for (GeneticOPA EGOPA : AL) {
                    String EGOPAName = EGOPA.getOPAIdentifier(false);
                    String EGFCID = EGOPA.getFCID();
                    int Preindex = AL.indexOf(EGOPA);
                    if (Gpred.contains(EGOPAName) && EGFCID.equalsIgnoreCase(GFCID)) {
                        if (Preindex > index) {
                            this.GenParent.getLog().appendToLog(Logger.INFORMATION, "Chromosome:checkFeasibility:isfeedback returniing False ");

                            return false;
                        }

                    }
                }

            } else {
                ArrayList<String> Gpred = Gopa.getPredOPAIDs();
                for (GeneticOPA EGOPA : AL) {
                    String EGOPAName = EGOPA.getOPAIdentifier(false);
                    int Preindex = AL.indexOf(EGOPA);
                    if (Gpred.contains(EGOPAName)) {
                        if (Preindex > index) {
                            this.GenParent.getLog().appendToLog(Logger.INFORMATION, "Chromosome:checkFeasibility:Ordinary returniing False ");

                            return false;
                        }

                    }
                }

            }

        }



        return true;
    }

    public ArrayList<GeneticOPA> getOrdinaryGOPAsinChromosome() {
        ArrayList<GeneticOPA> Al = new ArrayList<GeneticOPA>();
        for (GeneticOPA Gopa : Elements) {
            if (!Gopa.isfeedback()) {
                Al.add(Gopa);
            }

        }
        return Al;
    }

    public ArrayList<GeneticOPA> getFeedbacksinList(ArrayList<GeneticOPA> List) {
        ArrayList<GeneticOPA> Al = new ArrayList<GeneticOPA>();
        for (GeneticOPA Gopa : List) {
            if (Gopa.isfeedback()) {
                Al.add(Gopa);
            }

        }
        return Al;
    }

    public int getCountofFeedbacksinList(ArrayList<GeneticOPA> List) {
        int Count = 0;
        for (GeneticOPA Gopa : List) {
            if (Gopa.isfeedback()) {
                Count++;
            }

        }
        return Count;
    }
//
//    public ArrayList<GeneticOPA> getOrdinaryGOPAsinChromosome() {
//        ArrayList<GeneticOPA> Al = new ArrayList<GeneticOPA>();
//        for (GeneticOPA Gopa : Elements) {
//            if (!Gopa.isfeedback()) {
//                Al.add(Gopa);
//            }
//
//        }
//        return Al;
//    }

    public ArrayList<GeneticOPA> getFeedbacksinChromosome() {
        ArrayList<GeneticOPA> Al = new ArrayList<GeneticOPA>();
        for (GeneticOPA Gopa : Elements) {
            if (Gopa.isfeedback()) {
                Al.add(Gopa);
            }

        }
        return Al;
    }

    /**
     * @return the Fitness
     */
    public Double getFitness() {

        return Fitness;
    }

    /**
     * @param Fitness the Fitness to set
     */
    public void setFitness(Double Fitness) {
        this.Fitness = Fitness;
    }

    /**
     * @return the GenParent
     */
    public Generation getGenParent() {
        return GenParent;
    }

    /**
     * @param GenParent the GenParent to set
     */
    public void setGenParent(Generation GenParent) {
        this.GenParent = GenParent;
    }

    /**
     * @return the GenProbability
     */
    public Double getGenProbability() {
        return GenProbability;
    }

    /**
     * @param GenProbability the GenProbability to set
     */
    public void setGenProbability(Double GenProbability) {
        this.GenProbability = GenProbability;
    }

    public int getSizeofElemnts() {

        return this.Elements.size();
    }

    /**
     * @return the CHES
     */
    public ChromosomeExcutionStats getCHES() {
        return CHES;
    }

    /**
     * @param CHES the CHES to set
     */
    public void setCHES(ChromosomeExcutionStats CHES) {
        this.CHES = CHES;
    }

    /**
     * @return the HasbeenMutated
     */
    public boolean isHasbeenMutated() {
        return HasbeenMutated;
    }

    /**
     * @param HasbeenMutated the HasbeenMutated to set
     */
    public void setHasbeenMutated(boolean HasbeenMutated) {
        this.HasbeenMutated = HasbeenMutated;
    }
}
