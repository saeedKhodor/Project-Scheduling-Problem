/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package projectschedulingproblem;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;


/**
 *
 * @author saeed
 */
public class RecordScriptCapsule {
    private String ScriptName="";
    private String FilePath="";
    private int CountofProjects=0;
    private ArrayList<ProjectCapsule> PCs=new ArrayList<ProjectCapsule>() ;
    private ArrayList<ResourceLoaderCapsule> RLCs = new ArrayList<ResourceLoaderCapsule>();

    public RecordScriptCapsule() {
        
        
        
    }
        public RecordScriptCapsule(String Filename) {
        
        this.ReadScriptFile(Filename);
        
    }

    /**
     * @return the ScriptName
     */
    public String getScriptName() {
        return ScriptName;
    }

    /**
     * @param ScriptName the ScriptName to set
     */
    public void setScriptName(String ScriptName) {
        this.ScriptName = ScriptName;
    }

    /**
     * @return the FilePath
     */
    public String getFilePath() {
        return FilePath;
    }

    /**
     * @param FilePath the FilePath to set
     */
    public void setFilePath(String FilePath) {
        this.FilePath = FilePath;
    }

    /**
     * @return the CountofProjects
     */
    public int getCountofProjects() {
        return CountofProjects;
    }

    /**
     * @param CountofProjects the CountofProjects to set
     */
    public void setCountofProjects(int CountofProjects) {
        this.CountofProjects = CountofProjects;
    }

    /**
     * @return the PCs
     */
    public ArrayList<ProjectCapsule> getPCs() {
        return PCs;
    }

    /**
     * @param PCs the PCs to set
     */
    public void setPCs(ArrayList<ProjectCapsule> PCs) {
        this.PCs = PCs;
    }

    /**
     * @return the RLCs
     */
    public ArrayList<ResourceLoaderCapsule> getRLCs() {
        return RLCs;
    }

    /**
     * @param RLCs the RLCs to set
     */
    public void setRLCs(ArrayList<ResourceLoaderCapsule> RLCs) {
        this.RLCs = RLCs;
    }
    public RecordScriptCapsule ReadScriptFile(String Filename){
        
        try {
            int ProjectCount=0;
            String Currentheader="Global";
            ProjectCapsule PC=null;
            ResourceLoaderCapsule Rlc=null;
            
            BufferedReader reader = new BufferedReader(new FileReader(Filename));
            String line = null;
            while ((line = reader.readLine()) != null) {
                String[] Tokens = line.split("=");
                
                                
                if (Tokens.length == 2 ) {
                    if (Currentheader.equals("Global")) {
                      if (Tokens[0].equals("FilePath")) {
                            this.FilePath = Tokens[1];
                        } else if (Tokens[0].equals("ProjectCount")) {
                            ProjectCount = Integer.parseInt(Tokens[1]);
                        }
                    }else if (Currentheader.equals("Res")){
                        
                        if (Tokens[0].equals("Resourcesheetname")) {
                            Rlc.setResourceSheetname( Tokens[1]);
                        } else if (Tokens[0].equals("ResourceStartCell")) {
                             Rlc.setResourceStartCell( Tokens[1]);
                        } else if (Tokens[0].equals("ResourceEndCell")) {
                             Rlc.setResourceEndCell( Tokens[1]);
                        }else if (Tokens[0].equals("HasHeader")) {
                             Rlc.setHasHeader( Boolean.parseBoolean(Tokens[1]));
                        }
                        if(!this.RLCs.contains(Rlc)){
                            this.RLCs.add(Rlc);
                        }
                                                    
                    }else if (Currentheader.equals("Projects")){
                         if (Tokens[0].equals("ActivitiesSheetname")) {
                            PC.setActivitiesSheetname(Tokens[1]);
                             
                        } else if (Tokens[0].equals("ActivitiesStartCell")) {
                           PC.setActivitiesStartCell(Tokens[1]);
                        } else if (Tokens[0].equals("ActivitiesEndCell")) {
                           PC.setActivitiesEndCell(Tokens[1]);
                           // end of Activities
                        }else if (Tokens[0].equals("DSMSheetname") ) {
                            PC.setDSMSheetname(Tokens[1]);
                        }else if (Tokens[0].equals("DSMStartCell") ) {
                            PC.setDSMStartCell(Tokens[1]);
                        }else if (Tokens[0].equals("DSMEndCell") ) {
                            PC.setDSMEndCell(Tokens[1]);// end of DSM Loading 
                        }else if (Tokens[0].equals("ReworkSheetname") ) {
                            PC.setReworkSheetname(Tokens[1]);
                        }else if (Tokens[0].equals("ReworkStartCell") ) {
                            PC.setReworkStartCell(Tokens[1]);
                        }else if (Tokens[0].equals("ReworkEndCell") ) {
                            PC.setReworkEndCell(Tokens[1]);
                        }                      
                        
                    }
                }else{
                    if(Tokens[0].trim().equals("*Resources*".trim())){
                        
                        Currentheader="Res";
                        Rlc=new ResourceLoaderCapsule();
                    }else if(Tokens[0].equals("*Projects*".trim())){
                        
                        Currentheader="Projects";
                        
                    }else if(Tokens[0].equals("<".trim())){
                        PC=new ProjectCapsule();
                    }else if(Tokens[0].equals(">".trim())){
                        this.PCs.add(PC);
                        PC=null;
                    } else if(Tokens[0].equals("*End*".trim())){
                        break;
                    }
                    
                }
            }

        } catch (Exception e) {
           MainForm.CSVier.WriteError( e.getMessage());
        }

        return this;
    }
    @Override
    public String toString(){
        
        String S="";
        
        S+="FilePath="+this.FilePath+"\n";
        S+="ProjectCount="+this.PCs.size()+"\n";
        S+="*Resources*\n";
        for(ResourceLoaderCapsule Rlc : RLCs ){
            S+="Resourcesheetname="+Rlc.getResourceSheetname()+"\n";
            S+="ResourceStartCell="+Rlc.getResourceStartCell()+"\n";
            S+="ResourceEndCell="+Rlc.getResourceEndCell()+"\n";
            S+="HasHeader="+Rlc.isHasHeader()+"\n";

        }
         S+="*Projects*\n";
          for(ProjectCapsule PC : PCs ){
              S+="<"+"\n";
            S+="ActivitiesSheetname="+PC.getActivitiesSheetname()+"\n";
            S+="ActivitiesStartCell="+PC.getActivitiesStartCell()+"\n";
            S+="ActivitiesEndCell="+PC.getActivitiesEndCell()+"\n";
            S+="DSMSheetname="+PC.getDSMSheetname()+"\n";
            S+="DSMStartCell="+PC.getDSMStartCell()+"\n";
            S+="DSMEndCell="+PC.getDSMEndCell()+"\n";
            S+="ReworkSheetname="+PC.getReworkSheetname()+"\n";
            S+="ReworkStartCell="+PC.getReworkStartCell()+"\n";
            S+="ReworkEndCell="+PC.getReworkEndCell()+"\n";
            S+=">"+"\n";
                      
        }
         S+="*End*";
         
        return S;
    }
    
    
}
