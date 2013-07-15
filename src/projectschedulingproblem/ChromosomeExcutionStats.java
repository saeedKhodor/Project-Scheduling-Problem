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
public class ChromosomeExcutionStats {

    private ArrayList<ProjectExcutionStats> ProejctsStats;
    private ArrayList<OperationProject> OppsAdded;
    private Chromosome CHrParent;

    public ChromosomeExcutionStats(Chromosome Chr) {
        this.CHrParent=Chr;
         OppsAdded=new ArrayList<OperationProject>();
        ProejctsStats = new ArrayList<ProjectExcutionStats>();
    }

    /**
     * @return the ProejctsStats
     */
    public ArrayList<ProjectExcutionStats> getProejctsStats() {
        return ProejctsStats;
    }

    /**
     * @param ProejctsStats the ProejctsStats to set
     */
    public void setProejctsStats(ArrayList<ProjectExcutionStats> ProejctsStats) {
        this.ProejctsStats = ProejctsStats;
    }

    /**
     * @return the CHrParent
     */
    public Chromosome getCHrParent() {
        return CHrParent;
    }

    /**
     * @param CHrParent the CHrParent to set
     */
    public void setCHrParent(Chromosome CHrParent) {
        this.CHrParent = CHrParent;
    }

    /**
     * @return the OppsAdded
     */
    public ArrayList<OperationProject> getOppsAdded() {
        return OppsAdded;
    }

    /**
     * @param OppsAdded the OppsAdded to set
     */
    public void setOppsAdded(ArrayList<OperationProject> OppsAdded) {
        this.OppsAdded = OppsAdded;
    }
    public void addProject(OperationProject OP){
        if(!this.OppsAdded.contains(OP)){
             this.getOppsAdded().add(OP);
             this.ProejctsStats.add(new ProjectExcutionStats(OP));
        }
       
    }
    public ProjectExcutionStats getPESbyOPP(OperationProject OP){
        for(ProjectExcutionStats PES:this.ProejctsStats){
            if(PES.ParentOP.equals(OP)){
                return PES;
            }
        }
        return null;
    }
    public String getOPPNamescommadelimeted(){
        String s="";
        for(int i=0;i<OppsAdded.size();i++){
            s+=OppsAdded.get(i).getProjectName()+",";
            
        }
        if(OppsAdded.isEmpty()){
            return "Nothing";
        }
        return s;
    }
    public void checkGOPA(GeneticOPA Gopa,float TimeConsumed){
        OperationProject OPP=Gopa.getOperationProjectParentofGOPA();
        ProjectExcutionStats PES=getPESbyOPP(OPP);
        
        if(PES.getPESStatus()==ProjectExcutionStats.UNTOUCHED){
            PES.setProjectStartTime(TimeConsumed);
            PES.setPESStatus(ProjectExcutionStats.STARTED);
        }else if(PES.getPESStatus()==ProjectExcutionStats.STARTED){
            ArrayList<String> EndingGopas=Gopa.getOperationProjectParentofGOPA().getEndingActivityOPANames();
            boolean allhasfinished=true;
            for(String EndingGopaname:EndingGopas){
                GeneticOPA EndingGopa=this.CHrParent.getGOPAbyNAme(EndingGopaname);
                
                if(EndingGopa.getStatus()!=GeneticOPA.HASFINISHED){
                    allhasfinished=false;
                    break;
                }
            }
            if(allhasfinished){
            PES.setProjectEndTime(TimeConsumed);
            PES.setPESStatus(ProjectExcutionStats.FINISHED);
            }

        }else if(PES.getPESStatus()==ProjectExcutionStats.FINISHED){
            // do nothing
        }
        
        
        
    }
 public String getDelayDtring(String Projectnames) {
           if(ProejctsStats.isEmpty()){
            return "NA";
        }
        String S="";
     String[]explode=Projectnames.split(",");
      for (int i = 0; i < explode.length; i++) {

         for (ProjectExcutionStats PES : this.ProejctsStats) {
             if(PES.ParentOP.getProjectName().equals(explode[i])){
                 S += PES.getDelay() + ",";
             }
             

         }
     }
        return S;
    }
    @Override
    public String toString() {
           if(ProejctsStats.isEmpty()){
            return "NA";
        }
        String S="Chromosome ExcutionStats ";
     
            
        for(ProjectExcutionStats PES:this.ProejctsStats){
            S+=PES.toString()+ "||";
            
        }
        return S;
    }
    
}
