/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package projectschedulingproblem;

/**
 *
 * @author saeed
 */
public class GeneticsVariablesCapsule {
        private float CrossOverFactor;
        private float MutationFactor;
        private float Populationsize;
        private float NumberofGenerations;   
        private boolean FeasilbiltyON;
        private int CrossoverMode;
        private int FirstGenChoosingMode;
        private int MethodMode;
        private boolean bypassfittest;
        private boolean tournament;
        
    public GeneticsVariablesCapsule(float CrossOverFactor,float MutationFactor,float Populationsize,float NumberofGenerations,boolean FeasilbiltyON,int CrossoverMode,int FirstGenChoosingMode,boolean bypassfittest,boolean tournamentstyle){
        
        this.CrossOverFactor=CrossOverFactor;
        this.MutationFactor=MutationFactor;
        this.NumberofGenerations=NumberofGenerations;
        this.FeasilbiltyON=FeasilbiltyON;
        this.Populationsize=Populationsize;
        this.CrossoverMode=CrossoverMode;
        this.FirstGenChoosingMode=FirstGenChoosingMode;
        this.bypassfittest=bypassfittest;
        this.tournament=tournamentstyle;
    }
    @Override
    public String toString(){
        return "COF="+this.CrossOverFactor+" MUT "+MutationFactor+" Populationsize  "+Populationsize+ " #of Generations "+NumberofGenerations+ " Feasibility " + FeasilbiltyON +" CrossoverMode "+CrossoverMode+"BypassFittest "+bypassfittest+" tournamentstyle "+tournament;

}
    /**
     * @return the CrossOverFactor
     */
    public float getCrossOverFactor() {
        return CrossOverFactor;
    }

    /**
     * @param CrossOverFactor the CrossOverFactor to set
     */
    public void setCrossOverFactor(float CrossOverFactor) {
        this.CrossOverFactor = CrossOverFactor;
    }

    /**
     * @return the MutationFactor
     */
    public float getMutationFactor() {
        return MutationFactor;
    }

    /**
     * @param MutationFactor the MutationFactor to set
     */
    public void setMutationFactor(float MutationFactor) {
        this.MutationFactor = MutationFactor;
    }

    /**
     * @return the Populationsize
     */
    public float getPopulationsize() {
        return Populationsize;
    }

    /**
     * @param Populationsize the Populationsize to set
     */
    public void setPopulationsize(float Populationsize) {
        this.Populationsize = Populationsize;
    }

    /**
     * @return the NumberofGenerations
     */
    public float getNumberofGenerations() {
        return NumberofGenerations;
    }

    /**
     * @param NumberofGenerations the NumberofGenerations to set
     */
    public void setNumberofGenerations(float NumberofGenerations) {
        this.NumberofGenerations = NumberofGenerations;
    }

    /**
     * @return the FeasilbiltyON
     */
    public boolean isFeasilbiltyON() {
        return FeasilbiltyON;
    }

    /**
     * @param FeasilbiltyON the FeasilbiltyON to set
     */
    public void setFeasilbiltyON(boolean FeasilbiltyON) {
        this.FeasilbiltyON = FeasilbiltyON;
    }

    /**
     * @return the CrossoverMode
     */
    public int getCrossoverMode() {
        return CrossoverMode;
    }

    /**
     * @param CrossoverMode the CrossoverMode to set
     */
    public void setCrossoverMode(int CrossoverMode) {
        this.CrossoverMode = CrossoverMode;
    }

    /**
     * @return the FirstGenChoosingMode
     */
    public int getFirstGenChoosingMode() {
        return FirstGenChoosingMode;
    }

    /**
     * @param FirstGenChoosingMode the FirstGenChoosingMode to set
     */
    public void setFirstGenChoosingMode(int FirstGenChoosingMode) {
        this.FirstGenChoosingMode = FirstGenChoosingMode;
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
     * @return the bypassfittest
     */
    public boolean isBypassfittest() {
        return bypassfittest;
    }

    /**
     * @param bypassfittest the bypassfittest to set
     */
    public void setBypassfittest(boolean bypassfittest) {
        this.bypassfittest = bypassfittest;
    }

    /**
     * @return the tournament
     */
    public boolean isTournament() {
        return tournament;
    }

    /**
     * @param tournament the tournament to set
     */
    public void setTournament(boolean tournament) {
        this.tournament = tournament;
    }
}
