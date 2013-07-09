/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package projectschedulingproblem;

/**
 *
 * @author saeed
 */
public class GeneticThread implements Runnable{
    GeneticPool GP;
    GeneticsVariablesCapsule GVC;
    GeneticsMain GM;
    public GeneticThread(GeneticPool GP,GeneticsVariablesCapsule GVC,GeneticsMain GM){
        super();
        this.GM=GM;
        this.GVC=GVC;
        this.GP=GP;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    
    }
    
    @Override
    public void run(){
        
      Generation gen = this.GM.CreateFirstGeneration(GP, GVC);

            this.GM.addGeneration(gen);
            //End of MileStone create the first generation 
            // MileStone create the other generations 
            Generation nextGen = null;

            for (int i = 1; i < GVC.getNumberofGenerations(); i++) {
                nextGen = gen.CrossOVer();


                 this.GM.addGeneration(nextGen);

                gen = nextGen;
                nextGen = null;
                System.gc();
            }  
        
    }
}
