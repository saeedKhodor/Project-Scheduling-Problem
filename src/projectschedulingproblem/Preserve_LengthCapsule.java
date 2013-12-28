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
public class Preserve_LengthCapsule {
    private int Length;// the length of the chromosome 
    private int count;// how many times the chromosome has been chosen 
    private ArrayList<Integer> Indexes;
    public Preserve_LengthCapsule(int index,int L){
        Length=L;
        count=0;
        Indexes=new ArrayList<Integer>(); 
        Indexes.add(index);
    }
    
    public boolean addIndex(int index){
        
        if(!Indexes.contains(index)){
            getIndexes().add(index);
            return true;
        }
  
        return false;
    }

    /**
     * @return the Length
     */
    public int getLength() {
        return Length;
    }

    /**
     * @param Length the Length to set
     */
    public void setLength(int Length) {
        this.Length = Length;
    }

    /**
     * @return the count
     */
    public int getCount() {
        return count;
    }

    /**
     * @param count the count to set
     */
    public void setCount() {
        this.count++;
    }

    /**
     * @return the Indexes
     */
    public ArrayList<Integer> getIndexes() {
        return Indexes;
    }
   public int ChooseCHR(){
       
       
       Random R= new Random();
       int Chosen=R.nextInt(this.Indexes.size());
       count++;
       return Indexes.get(Chosen);
   }
}
