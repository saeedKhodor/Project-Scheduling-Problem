/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package projectschedulingproblem;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author saeed
 */
public class GeneticsMain {

    private Logger Log;
    private LogicalMethod LM;
    private ExhaustiveMethod EM;
    boolean withgrouping;
    ArrayList<GeneticPool> GPs;
    private boolean isLM = false;
    private boolean isEM = false;
    private int MethodMode = 0;
    private Portfolio Pfolio;
    public static final int TWOPOINTCROSSOVERMODE = 2;
    public static final int ONEPOINTCROSSOVERMODE = 1;
    public static final int RANDOMFIRSTGEN = 2;
    public static final int NOTRANDOMFIRSTGEN = 1;
    public static final int LMMETHODMODE = 1;
    public static final int EMMETHODMODE = 2;
    private ArrayList<Generation> Generations;
    private ArrayList <String> IDs=new ArrayList<String>();
//    this.OPR = new OperationResources(Pfolio.getGlobalResources(), this.Log);
//        OPR.createOperationReses();

    public GeneticsMain(Logger Log) {
        this.Log = Log;
        GPs = new ArrayList<GeneticPool>();


    }

    public GeneticsMain(Logger Log, LogicalMethod lm, boolean withgrouping) {
        this.Log = Log;
        this.LM = lm;
        this.Pfolio = LM.getPfolio();
        this.withgrouping = withgrouping;
        GPs = new ArrayList<GeneticPool>();
        Generations = new ArrayList<Generation>();
        CreateLMPools();
        isLM = true;
        MethodMode = GeneticsMain.LMMETHODMODE;
    }

    public GeneticsMain(Logger Log, ExhaustiveMethod em) {
        this.Log = Log;
        this.EM = em;
        this.Pfolio = EM.getPfolio();
        GPs = new ArrayList<GeneticPool>();
        Generations = new ArrayList<Generation>();
        createEMPool();
        isEM = true;
        MethodMode = GeneticsMain.EMMETHODMODE;
    }

    public final void createEMPool() {
        GeneticPool GP = new GeneticPool(1, Log);
        GP.getGPool().addAll(this.EM.getEC().getExhaustivePool());
        GP.setGPID("GPID-" + "1");
                IDs.add("GPID-" + "1");
        this.GPs.add(GP);
        this.Log.appendToLog(Logger.INFORMATION, "GeneticsMain : createEMPool : " + GP.toString() + " number of Gps is " + GPs.size());
        for (GeneticPool Gp : GPs) {
            Gp.CreateGOPAPool();
        }
    }

    public synchronized void addGeneration(Generation Gen) {
        Generations.add(Gen);

    }

    public final void CreateLMPools() {

        ArrayList<TimeStepper> TSs = this.LM.getTSs();
        int number=1;
        for (TimeStepper Ts : TSs) {
            if (this.withgrouping) {
                GeneticPool GP = this.getGeneticPoolbySize(Ts.GetAllOPAforGeneticpool().size());
                GP.getTSs().add(Ts);
                GP.setGPID("GPID-" + number);
                IDs.add("GPID-" + number);
                GPs.add(GP);
                this.Log.appendToLog(Logger.INFORMATION, "GeneticsMain : CreateLMPools :Grouping Mode Ts sizes are " + GP.toString() + " number of Gps is " + GPs.size());

            } else {

                GeneticPool GP = new GeneticPool(Ts.GetAllOPAforGeneticpool().size(), this.Log);
                GP.getTSs().add(Ts);
                GPs.add(GP);
                GP.setGPID("GPID-" + number);
                IDs.add("GPID-" + number);
                this.Log.appendToLog(Logger.INFORMATION, "GeneticsMain : CreateLMPools : Normal Mode " + GP.toString() + " number of Gps is " + GPs.size());

            }
            number++;
        }
        for (GeneticPool Gp : GPs) {
            Gp.GroupTSs();
        }

    }

    public Generation CreateFirstGeneration(GeneticPool GP, GeneticsVariablesCapsule GVC) {
        int RandomorNot = GVC.getFirstGenChoosingMode();
        int populationSize = (int) GVC.getPopulationsize();
        Generation Gen = new Generation(this, populationSize, GVC, GP, Log);
        Gen.CreateFirstGenChromosomes(MethodMode, this.withgrouping);
//        if (MethodMode == GeneticsMain.LMMETHODMODE) {
//            if (RandomorNot == GeneticsMain.RANDOMFIRSTGEN) {
//
//                Gen.CreateFirstGenChromosomes(MethodMode, this.withgrouping);
//                //Generations.add(Gen);
//
//                // here create a first random generation
//            } else if (RandomorNot == GeneticsMain.NOTRANDOMFIRSTGEN) {
//                 Gen.CreateFirstGenChromosomes(MethodMode, this.withgrouping);
//                // here create a first not  generation , need to follow normally running scheme 
//            }
//
//
//        } else if (MethodMode == GeneticsMain.EMMETHODMODE) {
//            if (RandomorNot == GeneticsMain.RANDOMFIRSTGEN) {
//
//                Gen.CreateFirstGenChromosomes(MethodMode, this.withgrouping);
//
//                // here create a first random generation you stop choosing when all the primary have been chosen 
//            } else if (RandomorNot == GeneticsMain.NOTRANDOMFIRSTGEN) {
//                // here create a first random generation
//                Gen.CreateFirstGenChromosomes(MethodMode, this.withgrouping);
//            }
//
//        }
        return Gen;
    }

    public static boolean GeneticsFlipaCoin(double d) {
        int Number = (int) (d * 100);

        Random r = new Random();
        int R = r.nextInt(100);
        //Log.appendToLog(Logger.INFORMATION,"OperationProject:FeedbackFlipaCoin: Number"+Number+" and R="+R);
        if (R < Number) {
            return true;
        }

        return false;
    }

    public static float GeneticsGenerateNumberPercentage() {


        Random r = new Random();
        int R = r.nextInt(100);

        return (float) R / 100;
    }

    public static int GeneticsGenerateNumberabovezero(int Number) {


        Random r = new Random();
        int R = r.nextInt(Number);

        return R;
    }

    /**
     *
     */
    public void ClearGps() {

        Generations.clear();
        System.gc();
        
    }
public Generation getHighetRankGenerationbyID(String ID){
    int highest=-1;
    int index=-1;
    for(int i=0;i<Generations.size();i++){
        Generation gen=Generations.get(i);
        if(gen.getGP().getGPID().equals(ID)){
            int GenID=gen.getGenerationID();
            if(GenID>highest){
                highest=GenID;
                index=i;
            }
        }
        
    }
    
    return Generations.get(index);
}
    public void ProcessGps(GeneticsVariablesCapsule GVC) {

        GVC.setMethodMode(this.MethodMode);
        Thread[] GTs = new Thread[GPs.size()];

        try {
            for (int i = 0; i < GPs.size(); i++) {
                GeneticThread GTemp = new GeneticThread(GPs.get(i), GVC, this);
                Thread t = new Thread(GTemp);
                GTs[i] = t;
                t.start();

            }
            for (int i = 0; i < GPs.size(); i++) {
                GTs[i].join();
            }
        } catch (Exception e) {
             Log.appendToLog(Logger.HAS_TO_SHOW, "ExcelUtils getNumberofSheets: Error Exception Caught " +e.getMessage());
        }
//        for (GeneticPool GP : GPs) {
//            // MileStone create the first generation 
//            Generation gen = this.CreateFirstGeneration(GP, GVC);
//
//            Generations.add(gen);
//            //End of MileStone create the first generation 
//            // MileStone create the other generations 
//            Generation nextGen = null;
//
//            for (int i = 1; i < numberofGenerations; i++) {
//                nextGen = gen.CrossOVer();
//
//
//                Generations.add(nextGen);
//
//                gen = nextGen;
//                nextGen = null;
//            }
//            choose the elements give the Chromosome the GP and the method and chromosome will fill itself up
//            generatio is on the level of this function 
//            crossover is on the level of the Generation
//            
//            
//             here we do all the processing  creation of the Generations and Chromoomes 
//             feasibility is on the level of the Chromosome
//             duration is on the level of the Chromosome this means we have to transmit a version of Resources to it ..
//             chosing the elements is on the level of the Chromosome


        // }
//MainForm.CSVier.writeGeneticstoCSV(null,false,"");
          boolean Firsttime1=true;
          String ProjectNames1="";
        for (Generation Gen : Generations) {
            if(Firsttime1){
                ProjectNames1=Gen.GetProjectNamesCommadelimited();
                MainForm.CSVier.writeGeneticstoCSV(Gen,false,ProjectNames1);
                
                Firsttime1=false;
            }
            Gen.setProjectSortingForCSV(ProjectNames1);
            MainForm.CSVier.writeGeneticstoCSV(Gen,false,"Nothing");
            
        }
        
        //MainForm.CSVier. WriteFinalResultsExcel(null,true);
        boolean Firsttime=true;
        String ProjectNames="";
        for(String ID:IDs){
            Generation gen=this.getHighetRankGenerationbyID(ID);
            if(Firsttime){
                ProjectNames=gen.GetProjectNamesCommadelimited();
                  MainForm.CSVier. WriteFinalResultsExcel(gen,false,ProjectNames);
                  Firsttime=false;
            }
                gen.setProjectSortingForCSV(ProjectNames);
                MainForm.CSVier. WriteFinalResultsExcel(gen,false,"Nothing");
           
        }
       
        this.Log.appendToLog(Logger.INFORMATION, "GeneticsMain : ProcessGps : GVC tostring " + GVC.toString());

        this.Log.appendToLog(Logger.HAS_TO_SHOW, "GeneticsMain : ProcessGps : DUMPING MEMORYYYYYGps Process Finished Successfully");
        this.ClearGps();
        

    }

    private GeneticPool getGeneticPoolbySize(int Size) {
        GeneticPool GP = null;
        for (GeneticPool Gp : GPs) {
            if (Gp.getSize() == Size) {

                GP = Gp;
            }
        }
        if (GP == null) {
            GP = new GeneticPool(Size, this.Log);
            GPs.add(GP);
        }
        return GP;

    }

    /**
     * @return the MethodMode
     */
    public int getMethodMode() {
        return MethodMode;
    }

    /**
     * @param MethodMode the MethodMode to set
     */
    public void setMethodMode(int MethodMode) {
        this.MethodMode = MethodMode;
    }

    /**
     * @return the Pfolio
     */
    public Portfolio getPfolio() {
        return Pfolio;
    }
}
