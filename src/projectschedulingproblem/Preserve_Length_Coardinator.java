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
public class Preserve_Length_Coardinator {
   
    ArrayList<Preserve_LengthCapsule> Capsules;
    ArrayList<Chromosome> Chrms;
    public Preserve_Length_Coardinator(ArrayList<Chromosome> Chrs){
        
        Capsules=new ArrayList<Preserve_LengthCapsule>();
        this.Chrms=Chrs;
        AnalyzeChromosomes();
    }
    private void AnalyzeChromosomes(){
        int i=0;
        for(Chromosome Chr:Chrms){
            
            AddCapsule(i,Chr.getSizeofElemnts() );
            i++;
        }
        int x=0;
    }

    public Chromosome chooseCHR(){// will choose randomly choose the capsule 
        
       Random R= new Random();
       Preserve_LengthCapsule chosenCap=this.Capsules.get(R.nextInt(Capsules.size()));
       return Chrms.get(chosenCap.ChooseCHR());
        
    }
    public boolean AddCapsule(int index,int L){
        boolean Success=true;
        for(Preserve_LengthCapsule Cap :Capsules){
            
            if(Cap.getLength()==L){
                Cap.addIndex(index);
                Success=false;
                
                break;
            }
        }
        if(Success){
            
            this.Capsules.add(new Preserve_LengthCapsule(index,L));
            
        }
            
        return Success;
        
    }
}
