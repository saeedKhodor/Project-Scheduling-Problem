/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package projectschedulingproblem;

/**
 *
 * @author saeed
 */
public class FirstgenerationThread extends Thread implements Runnable{
    Chromosome Chr;
    int MethodMode=0;
    public FirstgenerationThread(Chromosome CHr,int MMode){
        Chr=CHr;
        this.MethodMode=MMode;
    }
    @Override
    public void run(){
      this.Chr.CreateFirstGenerationGeneticOPAs(MethodMode);  
        
    }
    
}
