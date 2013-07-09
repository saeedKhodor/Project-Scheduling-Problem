/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package projectschedulingproblem;

import java.util.ArrayList;
import java.util.logging.Level;

/**
 *
 * @author saeed
 */
public class LogicalMethod {

    private Logger Log;
    private Portfolio Pfolio;
    private ArrayList<TimeStepper> TSs;
     

    public LogicalMethod() {
    }

    public LogicalMethod(Logger log, Portfolio pfolio) {
        TSs = new ArrayList<TimeStepper>();
        this.Log = log;
        this.Pfolio = pfolio;


    }
    // here add we would add a function that gets the value of how manytimes to run the projects in the logical methods in which each TimeStepper function returns a certain sequence of the project run in a logical manner

    public void StartTSs(int numberoftimes) {
       Thread[] Tss = new Thread[numberoftimes];
        try {
            for (int i = 0; i < numberoftimes; i++) {
                TimeStepper TS = new TimeStepper(this.Log, getPfolio());
                this.getTSs().add(TS);
                Thread t = new Thread(TS);
                Tss[i] = t;
                t.start();



            }
            for (int i = 0; i < numberoftimes; i++) {
                Tss[i].join();
            }
        } catch (Exception e) {
             java.util.logging.Logger.getLogger(WritetoCSV.class.getName()).log(Level.SEVERE, null, e);
        }



        for (TimeStepper Ts : this.getTSs()) {
            Ts.generateReportofExcution();
            Ts.GenerateLogFiles();
        }

    }

    /**
     * @return the TSs
     */
    public ArrayList<TimeStepper> getTSs() {
        return TSs;
    }

    /**
     * @return the Pfolio
     */
    public Portfolio getPfolio() {
        return Pfolio;
    }

   
}
