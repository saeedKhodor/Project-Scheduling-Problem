/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package projectschedulingproblem;

/**
 *
 * @author saeed
 */
public class ExhaustiveMethod {
    private Logger Log;
    private Portfolio Pfolio;
    private ExhaustiveCollector EC;
    
    public ExhaustiveMethod(Logger Log, Portfolio Pf){
        
        this.Log=Log;
        this.Pfolio=Pf;
        EC=new ExhaustiveCollector(this.Log, this.getPfolio());
    }
    public void StartExhaustives(){
              
        getEC().processPortfolio();
    }

    /**
     * @return the EC
     */
    public ExhaustiveCollector getEC() {
        return EC;
    }

    /**
     * @param EC the EC to set
     */
    public void setEC(ExhaustiveCollector EC) {
        this.EC = EC;
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
