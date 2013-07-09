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
public class ExhaustiveCollector {

    private Logger Log;
    private Portfolio Pfolio;
    ArrayList<OperationProject> OPP;
    private ArrayList<OperationProjectActivity> ExhaustivePool;

    public ExhaustiveCollector(Logger Log, Portfolio Pf) {
        this.Log = Log;
        this.Pfolio = Pf;
        ExhaustivePool=new ArrayList<OperationProjectActivity>();
    }

    public void processPortfolio() {
        OPP = new ArrayList<OperationProject>();

        for (Project P : getPfolio().getProjects()) {
            OPP.add(new OperationProject(P, Log));
            Log.appendToLog(Logger.INFORMATION, "ExhaustiveCollector:processPortfolio: Operation Project has been Create for Project" + P.toString());
        }

        for (OperationProject OP : OPP) {
            
            FeedbackManager FM=new FeedbackManager(OP, Log);
            ArrayList<OperationProjectActivity>Pool=OP.getPool();
            for(int i=0;i<Pool.size();i++){
                
                OperationProjectActivity OPA=Pool.get(i);
                FM.processOPA(OPA, true);
            
            }
            ExhaustivePool.addAll(OP.getPool());
        }

        
       
        for(OperationProjectActivity OPA :getExhaustivePool()){
            Log.appendToLog(Logger.INFORMATION, "ExhaustiveCollector:processPortfolio: Exhuastive Pool elements are " +OPA.getOPAIdentifier(false));
            
            
        }
    }

    /**
     * @return the ExhaustivePool
     */
    public ArrayList<OperationProjectActivity> getExhaustivePool() {
        return ExhaustivePool;
    }

    /**
     * @param ExhaustivePool the ExhaustivePool to set
     */
    public void setExhaustivePool(ArrayList<OperationProjectActivity> ExhaustivePool) {
        this.ExhaustivePool = ExhaustivePool;
    }

    /**
     * @return the Pfolio
     */
    public Portfolio getPfolio() {
        return Pfolio;
    }

    /**
     * @param Pfolio the Pfolio to set
     */
    public void setPfolio(Portfolio Pfolio) {
        this.Pfolio = Pfolio;
    }
}
