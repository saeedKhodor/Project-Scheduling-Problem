/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package projectschedulingproblem;

import java.util.ArrayList;

/**
 *
 * @author saeed
 */
public class Generation {

    private ArrayList<Chromosome> Chromosomes;
    private int poulationsize;
    private GeneticPool GP;
    private GeneticsVariablesCapsule GVC;
    private Logger Log;
    private int GenerationID = 0;
    private GeneticsMain GMParent;

    public Generation(GeneticsVariablesCapsule gvc, Logger Log) {
        this.Chromosomes = new ArrayList<Chromosome>();
        poulationsize = 0;
        this.GVC = gvc;
        this.Log = Log;
    }

    public Generation(GeneticsMain GM, int PopSize, GeneticsVariablesCapsule gvc, Logger Log) {
        this.Chromosomes = new ArrayList<Chromosome>();
        this.poulationsize = PopSize;
        this.GVC = gvc;
        this.Log = Log;
        this.GMParent = GM;

    }

    public Generation(GeneticsMain GM, int PopSize, GeneticsVariablesCapsule gvc, GeneticPool GP, Logger Log) {
        //Generation(gvc,Log);
        this.Chromosomes = new ArrayList<Chromosome>();
        this.poulationsize = PopSize;
        this.GP = GP;
        this.GVC = gvc;
        this.Log = Log;
        this.GMParent = GM;
    }

    public void CreateFirstGenChromosomes(int MethodMode, boolean withgrouping) {


        for (int i = 0; i < poulationsize; i++) {
            Chromosome CHR = new Chromosome(this, withgrouping);
            this.Chromosomes.add(CHR);
            CHR.CreateFirstGenerationGeneticOPAs(MethodMode);
        }

//     try {
//            for (int i = 0; i < poulationsize; i++) {
//            
//                Thread t = new Thread(new FirstgenerationThread(CHR,MethodMode));
//                Tss[i] = t;
//                t.start();
//
//            }
//            for (int i = 0; i < poulationsize; i++) {
//                Tss[i].join();
//            }
//        } catch (Exception e) {
//            
//        }

        this.GenerationID = 1;
    }

    /**
     * @return the Chromosomes
     */
    public ArrayList<Chromosome> getChromosomes() {
        return Chromosomes;
    }

    /**
     * @param Chromosomes the Chromosomes to set
     */
    public void setChromosomes(ArrayList<Chromosome> Chromosomes) {
        this.Chromosomes = Chromosomes;
    }

    /**
     * @return the GP
     */
    public GeneticPool getGP() {
        return GP;
    }

    /**
     * @param GP the GP to set
     */
    public void setGP(GeneticPool GP) {
        this.GP = GP;
    }

    /**
     * @return the GVC
     */
    public GeneticsVariablesCapsule getGVC() {
        return GVC;
    }

    /**
     * @param GVC the GVC to set
     */
    public void setGVC(GeneticsVariablesCapsule GVC) {
        this.GVC = GVC;
    }

    /**
     * @return the Log
     */
    public Logger getLog() {
        return Log;
    }

    public String GenerateCSVStringforgenerationcsv() {
        String S = "";//ID,Chromosome ,Duration,Fitness,ProjectRunningStats,Mutated" + '\n';
        for (Chromosome CHR : Chromosomes) {

            S += this.GP.getGPID() + "," + this.getGenerationID() + "," + CHR.toString() + "," + CHR.getDuration() + "," + CHR.getFitness() + "," + CHR.isHasbeenMutated() + "," + CHR.getCHES().toString() + "\n";

        }

        return S;

    }

    public String GenerateCSVStringForFinalResultCSV() {
        String S = "";//ID,Chromosome ,Duration,Fitness,ProjectRunningStats,Mutated" + '\n';

        Chromosome CHR = getHighestDurationChr();
        if (CHR != null) {
            S += this.GP.getGPID() + "," + this.getGenerationID() + "," + CHR.toString() + "," + CHR.getDuration() + "," + CHR.getFitness() + "," + CHR.isHasbeenMutated() + "," + CHR.getCHES().toString() + "\n";

        } else {
            S += this.GP.getGPID() + "," + this.getGenerationID() + "There is no fit chromosome to display ";

        }



        return S;

    }

    public Chromosome getHighestDurationChr() {
        double lowestDuration = 999999999.0;
        int index = -1;
        for (int i = 0; i < Chromosomes.size(); i++) {
            Chromosome chr = Chromosomes.get(i);
            if (chr.getDuration() < lowestDuration) {
                lowestDuration = chr.getDuration();
                index = i;

            }

        }
if(index==-1){
   return null;
}
        return Chromosomes.get(index);
    }

    public void ApplyFeasibility() {
        for (Chromosome CHR : Chromosomes) {

            Log.appendToLog(Logger.INFORMATION, "generation:ApplyFeasibility : CHR before Feasibility" + CHR.toString());
            CHR.ChromosomeApplyFeasibility();
            Log.appendToLog(Logger.INFORMATION, "generation:CrossOVer : CHR After Feasibility" + CHR.toString());
        }

    }

    public Generation CrossOVer() {
//        int CrossOverMode = this.GVC.getCrossoverMode();
//        float mutationfactor = this.GVC.getMutationFactor();
//        float CrossOverFactor = this.GVC.getCrossOverFactor();


        Generation nextGen = new Generation(this.getGMParent(), this.poulationsize, this.GVC, this.Log);
        nextGen.setGenerationID(this.GenerationID + 1);
        nextGen.setGP(this.GP);
        // Milestone assign probabilities two methods... first check if all the Fitnesses are equal...assign equal percentage 
        float Fitness;
        boolean foundthatallFitnessareequal = true;
        if (Chromosomes.size() > 0) {
            Fitness = Chromosomes.get(0).getFitness().floatValue();
        } else {
            Log.appendToLog(Logger.INFORMATION, "generation:CrossOVer : Errroooor  chormosomes are equal to zero");

            return null;

        }
        for (Chromosome Chr : Chromosomes) {

            float tempFitness = Chr.getFitness().floatValue();
            int x = Float.compare(Fitness, tempFitness);
            if (x != 0) {
                foundthatallFitnessareequal = false;
                break;
            }
        }
        if (foundthatallFitnessareequal) {
            float GenProbability = (float) 1 / this.poulationsize;
            int count = 0;
            for (Chromosome Chr : Chromosomes) {

                // assign equal probabilities
                count++;
                Chr.setGenProbability((double) GenProbability);
                Log.appendToLog(Logger.INFORMATION, "generation:CrossOVer : foundthatallFitnessareequal Chr " + count + " GenProbability= " + Chr.getGenProbability());

            }
        } else {
            float totalFitness = 0.0f;
            int count = 0;
            for (Chromosome Chr : Chromosomes) {
                float Ftness = Chr.getFitness().floatValue();
                totalFitness += Ftness;
            }
            for (Chromosome Chr : Chromosomes) {
                count++;
                float Ftness = Chr.getFitness().floatValue();
                float GenProb = Ftness / totalFitness;
                Chr.setGenProbability((double) GenProb);
                Log.appendToLog(Logger.INFORMATION, "generation:CrossOVer :!!!!!!foundthatallFitnessareequal Chr " + count + " GenProbability= " + Chr.getGenProbability());
            }
        }
        //******   end of Milestone assigning probability 
        // Milestone choose the chromosome based on Gen Probability  // has to be a while loop since there is a possibity that the random number generated is not greater than any number 

        if (GVC.isBypassfittest()) {

            Chromosome CHR = getbestFittnessCHR();
            if (CHR != null) {
                nextGen.getChromosomes().add(CHR);
            } else {
                Log.appendToLog(Logger.HAS_TO_SHOW, "Error generation:CrossOVer:Bypassfittest: couldnt find aby CHR ");

            }


        }

        while (nextGen.getChromosomes().size() < this.poulationsize) {
            Integer Chr1index = -1;
            Chromosome CHr1 = null;
            Chromosome CHr2 = null;
            int Chrsneeded = 2;
            int ChrFound = 0;

            if (nextGen.getChromosomes().size() == this.poulationsize - 1) {
                Log.appendToLog(Logger.INFORMATION, "generation:CrossOVer : Reached population size-1 setting Status =1  " + Chr1index);
                Chrsneeded = 1;
            }
            while (ChrFound < Chrsneeded) {
                float RandomNumber = GeneticsMain.GeneticsGenerateNumberPercentage();

                for (int i = 0; i < Chromosomes.size(); i++) {
                    if (ChrFound == 1 && Chr1index == i) {
                        Log.appendToLog(Logger.INFORMATION, "generation:CrossOVer : skipping Status =2 and Chr1index is  " + Chr1index);
                        continue;
                    }
                    float genProb = Chromosomes.get(i).getGenProbability().floatValue();
                    int chosen;

                    if (genProb <= RandomNumber) {
                        if (foundthatallFitnessareequal) {
                            if (Chromosomes.size() <= 0) {
                                chosen = i;
                            } else {
                                chosen = i + GeneticsMain.GeneticsGenerateNumberabovezero((Chromosomes.size() - 1) - i);
                            }
                        } else {
                            chosen = i;
                        }
                        if (ChrFound == 1) {

                            CHr2 = Chromosomes.get(chosen);
                            ChrFound += 1;
                            break;
                        }

                        if (ChrFound == 0) {
                            Chr1index = chosen;
                            CHr1 = Chromosomes.get(chosen);
                            ChrFound += 1;
                            break;
                        }


                    }

                }
            }
            if (Chrsneeded == 2) {

                nextGen.getChromosomes().addAll(CrossOverTwoChrs(nextGen, CHr1, CHr2, this.GVC.getCrossoverMode()));

            } else if (Chrsneeded == 1) {
                Chromosome newCHr1 = new Chromosome(nextGen, CHr1);
                newCHr1.Mutate();
                if (GVC.isFeasilbiltyON()) {
                    newCHr1.ChromosomeApplyFeasibility();
                }



                newCHr1.setDuration();

                nextGen.getChromosomes().add(newCHr1);

            }


        }

        return nextGen;
    }

    private Chromosome getbestFittnessCHR() {

        float MAxFittnes = -1.0f;
        int index = -1;
        for (int i = 0; i < Chromosomes.size(); i++) {
            float tempFit = Chromosomes.get(i).getFitness().floatValue();
            int x = Float.compare(MAxFittnes, tempFit);
            if (x <= 0) {
                MAxFittnes = tempFit;
                index = i;
            }
//         int x1=Float.compare(1.0f, 1.0f);//0
//        int x2=Float.compare(2.0f, 1.0f);//1
//         int x3=Float.compare(1.0f, 2.0f);//-1
//         Log.appendToLog(Logger.INFORMATION, "generation:CrossOverTwoChrs : Original Chromosome CHr1 " );

        }
        if (MAxFittnes < 0) {
            return null;
        }
        return Chromosomes.get(index);
    }

    private ArrayList<Chromosome> CrossOverTwoChrs(Generation nextGen, Chromosome Chr1, Chromosome Chr2, int Crossovermode) {
        ArrayList<Chromosome> Al = new ArrayList<Chromosome>();
        float CrossOverFactor = this.GVC.getCrossOverFactor();

        if (Crossovermode == GeneticsMain.ONEPOINTCROSSOVERMODE) {

            int crossoverindex = -1;
            while (crossoverindex == -1) {
                ArrayList<Double> GopaProbs = Chr1.getGopaProbsforcrossover();
                for (int i = 0; i < GopaProbs.size(); i++) {
                    if (GopaProbs.get(i) < CrossOverFactor) {
                        crossoverindex = i;
                        break;
                    }
                }
            }
            Chromosome newChr1 = new Chromosome(nextGen, false);
            Chromosome newChr2 = new Chromosome(nextGen, false);
            Log.appendToLog(Logger.INFORMATION, "generation:CrossOverTwoChrs : Original Chromosome CHr1 " + Chr1.toString());
            Log.appendToLog(Logger.INFORMATION, "generation:CrossOverTwoChrs : Original Chromosome CHr2 " + Chr2.toString());

            int greaterindex = (Chr1.getSizeofElemnts() >= Chr2.getSizeofElemnts()) ? Chr1.getSizeofElemnts() - 1 : Chr2.getSizeofElemnts() - 1;
            int index = 0;
            while (index <= greaterindex) {

                if (index <= crossoverindex) {
                    if (index < Chr1.getSizeofElemnts()) {
                        newChr1.getElements().add(Chr1.getElements().get(index));
                    }
                    if (index < Chr2.getSizeofElemnts()) {
                        newChr2.getElements().add(Chr2.getElements().get(index));
                    }
                } else {
                    if (index < Chr1.getSizeofElemnts()) {
                        newChr2.getElements().add(Chr1.getElements().get(index));
                    }
                    if (index < Chr2.getSizeofElemnts()) {
                        newChr1.getElements().add(Chr2.getElements().get(index));
                    }


                }
                index++;
            }
            newChr1.Mutate();
            newChr2.Mutate();
            if (GVC.isFeasilbiltyON()) {

                newChr1.ChromosomeApplyFeasibility();
                newChr2.ChromosomeApplyFeasibility();
                /// here apply feasilbility to rearrange the tasks
            }
            newChr1.setDuration();
            newChr2.setDuration();
            if (GVC.getMethodMode() == GeneticsMain.EMMETHODMODE) {
                // new GA for EM 
                float NewCHR2Fit = newChr2.getFitness().floatValue();
                float NewCHR1Fit = newChr1.getFitness().floatValue();
                float CHR2Fit = newChr2.getFitness().floatValue();
                float CHR1Fit = newChr1.getFitness().floatValue();

                int NewCHR2size = newChr2.getSizeofElemnts();
                int NewCHR1size = newChr1.getSizeofElemnts();
                int CHR2size = newChr2.getSizeofElemnts();
                int CHR1Fitsize = newChr1.getSizeofElemnts();

                if (NewCHR2Fit >= CHR1Fit) {
                    Al.add(newChr2);


                } else {
                    Al.add(Chr1);
                }

                if (NewCHR1Fit >= CHR2Fit) {

                    Al.add(newChr1);
                } else {
                    Al.add(Chr2);
                }

            } else {
                Al.add(newChr1);
                Al.add(newChr2);
            }

            Log.appendToLog(Logger.INFORMATION, "generation:CrossOverTwoChrs : Original Chromosome new CHr1 " + newChr1.toString());
            Log.appendToLog(Logger.INFORMATION, "generation:CrossOverTwoChrs : Original Chromosome  new CHr2 " + newChr2.toString());
        } else {

            boolean foundpoints = false;

            int crossoverindex = -1;
            int crossoverindex2 = -1;
            while (!foundpoints) {
                ArrayList<Double> GopaProbs = Chr1.getGopaProbsforcrossover();
                for (int i = 0; i < GopaProbs.size(); i++) {

                    if (GopaProbs.get(i) < CrossOverFactor) {
                        if (crossoverindex == -1) {
                            crossoverindex = i;
                        } else {
                            if (crossoverindex != i) {
                                crossoverindex2 = i;
                            }

                        }



                    }
                }
                if (crossoverindex != -1 && crossoverindex2 != -1) {
                    foundpoints = true;
                }
            }
            Chromosome newChr1 = new Chromosome(nextGen, false);
            Chromosome newChr2 = new Chromosome(nextGen, false);
            Log.appendToLog(Logger.INFORMATION, "generation:CrossOverTwoChrs : Original Chromosome CHr1 " + Chr1.toString());
            Log.appendToLog(Logger.INFORMATION, "generation:CrossOverTwoChrs : Original Chromosome CHr2 " + Chr2.toString());


            int greatersize = (Chr1.getSizeofElemnts() >= Chr2.getSizeofElemnts()) ? Chr1.getSizeofElemnts() - 1 : Chr2.getSizeofElemnts() - 1;
            int greaterindex = (crossoverindex >= crossoverindex2) ? crossoverindex : crossoverindex2;
            int Smallerindex = (crossoverindex <= crossoverindex2) ? crossoverindex : crossoverindex2;
            int index = 0;
            while (index < greatersize) {

                if (index > Smallerindex && index < greaterindex) {
                    if (index < Chr1.getSizeofElemnts()) {
                        newChr1.getElements().add(Chr2.getElements().get(index));
                    }
                    if (index < Chr2.getSizeofElemnts()) {
                        newChr2.getElements().add(Chr1.getElements().get(index));
                    }
                } else {
                    if (index < Chr1.getSizeofElemnts()) {
                        newChr1.getElements().add(Chr1.getElements().get(index));
                    }
                    if (index < Chr2.getSizeofElemnts()) {
                        newChr2.getElements().add(Chr2.getElements().get(index));
                    }


                }
                index++;
            }
            newChr1.Mutate();
            newChr2.Mutate();
            if (GVC.isFeasilbiltyON()) {

                newChr1.ChromosomeApplyFeasibility();
                newChr2.ChromosomeApplyFeasibility();
                /// here apply feasilbility to rearrange the tasks
            }
            newChr1.setDuration();
            newChr2.setDuration();
            if (GVC.getMethodMode() == GeneticsMain.EMMETHODMODE) {
                // new GA for EM 
                float NewCHR2Fit = newChr2.getFitness().floatValue();
                float NewCHR1Fit = newChr1.getFitness().floatValue();
                float CHR2Fit = newChr2.getFitness().floatValue();
                float CHR1Fit = newChr1.getFitness().floatValue();



                if (NewCHR2Fit >= CHR1Fit) {
                    Al.add(newChr2);


                } else {
                    Al.add(Chr1);
                }

                if (NewCHR1Fit >= CHR2Fit) {

                    Al.add(newChr1);
                } else {
                    Al.add(Chr2);
                }
            } else {
                Al.add(newChr1);
                Al.add(newChr2);
            }



            Log.appendToLog(Logger.INFORMATION, "generation:CrossOverTwoChrs : Original Chromosome new CHr1 " + newChr1.toString());
            Log.appendToLog(Logger.INFORMATION, "generation:CrossOverTwoChrs : Original Chromosome  new CHr2 " + newChr2.toString());


        }

        return Al;

    }

    /**
     * @return the GenerationID
     */
    public int getGenerationID() {
        return GenerationID;
    }

    /**
     * @param GenerationID the GenerationID to set
     */
    public void setGenerationID(int GenerationID) {
        this.GenerationID = GenerationID;
    }

    /**
     * @return the GMParent
     */
    public GeneticsMain getGMParent() {
        return GMParent;
    }

    /**
     * @param GMParent the GMParent to set
     */
    public void setGMParent(GeneticsMain GMParent) {
        this.GMParent = GMParent;
    }
}
