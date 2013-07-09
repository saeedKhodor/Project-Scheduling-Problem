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
public class GeneticPool {

    private ArrayList<TimeStepper> TSs;
    private int size;
    private ArrayList<OperationProjectActivity> GPool;// this will be the Geneticpool that the first generation will be chosen from.
    private Logger Log;
    private ArrayList<GeneticOPA> GOPAPool; // this will be used by apply feasibility  to choose from them 
    private String GPID;
            
    public GeneticPool(int Sze, Logger Log) {
        size = Sze;
        TSs = new ArrayList<TimeStepper>();
        GPool = new ArrayList<OperationProjectActivity>();
        GOPAPool = new ArrayList<GeneticOPA>();
        this.Log = Log;

    }

    /**
     * @return the TSs
     */
    public ArrayList<TimeStepper> getTSs() {
        return TSs;
    }

    /**
     * @param TSs the TSs to set
     */
    public void setTSs(ArrayList<TimeStepper> TSs) {
        this.TSs = TSs;
    }

    /**
     * @return the size
     */
    public int getSize() {
        return size;
    }

    public int GroupTSs() {
        ArrayList<OperationProjectActivity> GPooltemp = new ArrayList<OperationProjectActivity>();
        ArrayList<OperationProjectActivity> temp = new ArrayList<OperationProjectActivity>();

        if (TSs.size() > 1) {
            for (TimeStepper Ts : TSs) {
                temp.addAll(Ts.GetAllOPAforGeneticpool());
            }
            for (int i = 0; i < temp.size(); i++) {

                OperationProjectActivity OPA = temp.get(i);

                boolean addOPA = true;
                for (int j = 0; j < GPooltemp.size(); j++) {
                    OperationProjectActivity OPAtemp = GPooltemp.get(j);
                    if (!OPA.isIsfeedback()) {

                        if (OPAtemp.getOPAIdentifier(false).equalsIgnoreCase(OPA.getOPAIdentifier(false))) {
                            addOPA = false;
                            break;
                        }
                    } else {
                        if (OPAtemp.getOPAIdentifierandFCID().equalsIgnoreCase(OPA.getOPAIdentifierandFCID())) {
                            addOPA = false;
                            break;
                        }

                        // here is the case where the OPA is a feedback we check the FCID and the Name
                    }


                }
                if (addOPA) {
                    GPooltemp.add(OPA);

                }



            }
            // here is the case were either there is no grouping or the size is mentioned once
        } else {
            if (TSs.size() > 0) {
                GPooltemp.addAll(TSs.get(0).GetAllOPAforGeneticpool());

            }

        }
        getGPool().addAll(GPooltemp);
        String s = "GeneticPool:GroupTSs: GPool " + this.toString() + " , ";
        for (OperationProjectActivity OPA : getGPool()) {
            if (OPA.isIsfeedback()) {
                s += OPA.getOPAIdentifierandFCID() +OPA.toString()+ ",";
            } else {
                s += OPA.getOPAIdentifier(false) + ",";
            }


        }
        // this will let the 
        CreateGOPAPool();

        Log.appendToLog(Logger.HAS_TO_SHOW, s);
        return 0;
    }

    @Override
    public String toString() {

        return "GP Size " + size + " TSs Array Size " + TSs.size() + " GPOOL Size = " + getGPool().size();

    }

    /**
     * @return the GPool
     */
    public ArrayList<OperationProjectActivity> getGPool() {
        return GPool;
    }

    /**
     * @param GPool the GPool to set
     */
    public void setGPool(ArrayList<OperationProjectActivity> GPool) {
        this.GPool = GPool;
    }

    public void CreateGOPAPool() {
        for (OperationProjectActivity OPA : GPool) {

            getGOPAPool().add(new GeneticOPA(OPA));

        }
    }

    /**
     * @return the GOPAPool
     */
    public ArrayList<GeneticOPA> getGOPAPool() {
        return GOPAPool;
    }

    public ArrayList<GeneticOPA> getOrdinaryGOPAsinGP() {
        ArrayList<GeneticOPA> Al = new ArrayList<GeneticOPA>();
        for (GeneticOPA Gopa : this.getGOPAPool()) {
            if (!Gopa.isfeedback()) {
                Al.add(Gopa);
            }

        }
        return Al;

    }

    public ArrayList<GeneticOPA> getFeedbacksGOPAsinGP() {
        ArrayList<GeneticOPA> Al = new ArrayList<GeneticOPA>();
        for (GeneticOPA Gopa : this.getGOPAPool()) {
            if (Gopa.isfeedback()) {
                Al.add(Gopa);
            }

        }
        return Al;

    }

    /**
     * @return the GPID
     */
    public String getGPID() {
        return GPID;
    }

    /**
     * @param GPID the GPID to set
     */
    public void setGPID(String GPID) {
        this.GPID = GPID;
    }
}
