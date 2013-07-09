    /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package projectschedulingproblem;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author saeed
 */
public class ReworkRecordCapsule {

    private String Startopa;
    private String Endopa;
    private int LevelReached = 0;
    private HashMap<String, Integer> OpaRecords;
    private ArrayList<OperationProjectActivity> Interval;
    private Integer MAxSOPARwrk=0;
    private boolean RRCfinished=false;
    private FeedbackManager FM;
    public ReworkRecordCapsule(FeedbackManager FM,String Sopa, String Eopa) {
        this.Startopa = Sopa;
        this.Endopa = Eopa;
        OpaRecords = new HashMap<String, Integer>();
        this.FM=FM;

    }
    public boolean isthisRRCfinished(){
        return RRCfinished;

    }
    public void analyzeTempOPAs(ArrayList<OperationProjectActivity> AL) {
//        Log.appendToLog(Logger.INFORMATION,"FeedbackCycle:analyzeTempOPAs: Function has been called with AL size="+AL.size());
        ArrayList<String> AlOPAStrings = new ArrayList<String>();

        for (OperationProjectActivity OPA : AL) {
            AlOPAStrings.add(OPA.getOPAIdentifier(false));
                if(OPA.getOPAIdentifier(false).equalsIgnoreCase(this.Startopa)){
                
                       this.MAxSOPARwrk=(int)OPA.getmaxnumberofreworks();
                       
                }
            //        Log.appendToLog(Logger.INFORMATION,"FeedbackCycle:analyzeTempOPAs: OPA in tempOPA "+OPA.getOPAIdentifier(false)+ OPA.getOriginalPA().getProbabilityofbeingworkedinFeedback().size());
        }
        for (int i = 0; i < AL.size(); i++) {
            //for(OperationProjectActivity OPA:AL){
            OperationProjectActivity OPA = AL.get(i);
            boolean Remove = true;
            ArrayList<String> Pred = OPA.getOriginalPA().getPredecesorsIDsPAIdentfiers();
            for (String s : AlOPAStrings) {
                for (String s1 : Pred) {
                    //                          Log.appendToLog(Logger.INFORMATION,"FeedbackCycle:analyzeTempOPAs: Checking OPA "+OPA.getOPAIdentifier(false)+" & "+s);
                    if (s.equalsIgnoreCase(s1)) {

                        Remove = false;
                    }
                }
            } 
            // removing the OPA that can start in parallel with the startOPA
            if (Remove && !OPA.getOPAIdentifier(false).equalsIgnoreCase(this.Startopa)) {
                if (AL.remove(OPA)) {
                    AlOPAStrings.remove(OPA.getOPAIdentifier(true));
                }

            }
            // Log.appendToLog(Logger.INFORMATION,"FeedbackCycle:analyzeTempOPAs: OPA in tempOPA"+OPA.getOPAIdentifier(false)+ OPA.getOriginalPA().getProbabilityofbeingworkedinFeedback().size());
        }
        
        // added on 5/6/2013 after removing OPA based on teh predecessor ( the ones that can start with StartOPA we need to remove the others that depend on it 
        boolean canRemove=true;
        while (canRemove){
            canRemove=false;
            for (int i = 0; i < AL.size(); i++) {
            
                OperationProjectActivity OPA = AL.get(i);
                String OpAName = OPA.getOPAIdentifier(true);
                if (!OpAName.equalsIgnoreCase(this.Startopa)) {
                    ArrayList<String> Pred = OPA.getOriginalPA().getPredecesorsIDsPAIdentfiers();

                    for (String s1 : Pred) {
                       if (!AlOPAStrings.contains(s1)) {
                            if (AL.remove(OPA)) {
                                AlOPAStrings.remove(OPA.getOPAIdentifier(true));
                                canRemove = true;
                                
                            }
                        }

                    }


                }
            }

        }
        
        for(OperationProjectActivity pa:AL){
            this.OpaRecords.put(pa.getOPAIdentifier(true), 0);
            
        }
        this.setInterval(AL);
    }
    public int addonetoOPArecord(OperationProjectActivity OPA,int v){
        String OPAName=OPA.getOPAIdentifier(true);
        if(this.OpaRecords.containsKey(OPAName)){
            
            Integer val=this.OpaRecords.get(OPAName);
            
            this.OpaRecords.remove(OPAName);
            this.OpaRecords.put(OPAName,val+v);
            this.FM.getLog().appendToLog(Logger.INFORMATION,"ReworkRecordCapsule:addonetoOPArecord:value  "+val+ " adding ne to OPA "+ OPA.getOPAIdentifier(false)+this.OpaRecords.get(OPAName));
        
            return val+v;
        }
        //appendToLog(Logger.INFORMATION,"FeedbackCycle:analyzeTempOPAs: OPA in tempOPA"+OPA.getOPAIdentifier(false)+ OPA.getOriginalPA().getProbabilityofbeingworkedinFeedback().size());
        
        
        return -1;
    }
    public int addOnetoLevelReached(){
        Integer lr=getLevelReached();
        lr+=1;
        setLevelReached(lr );
        if(lr>=MAxSOPARwrk){
            this.RRCfinished=true;
            
        }
        return getLevelReached();
    }
    public int getOPARecord(OperationProjectActivity OPA, boolean withaddition){// the with addtion is important since it will allow multiple calls for the getOPA record
        
        if(this.OpaRecords.containsKey(OPA.getOPAIdentifier(true))){
            return OpaRecords.get(OPA.getOPAIdentifier(true));
        }else{
            if(withaddition){
              return addonetoOPArecord(OPA,0);  
            }
            return -1;
            
        }
        

    }
    public String getFCID(boolean withLevel){
        if(withLevel){
            return Startopa+"|"+Endopa+"|"+getLevelReached();
        }else{
           return Startopa+"|"+Endopa;
        }
    }

    /**
     * @return the LevelReached
     */
    public int getLevelReached() {
        return LevelReached;
    }

    /**
     * @param LevelReached the LevelReached to set
     */
    public void setLevelReached(int LevelReached) {
        this.LevelReached = LevelReached;
    }

    /**
     * @return the Interval
     */
    
    public ArrayList<OperationProjectActivity> getInterval() {

//        for (int i = 0; i < Interval.size(); i++) {
//            OperationProjectActivity OPA = Interval.get(i);
//            Double MaxNum = OPA.getmaxnumberofreworks();
//            Double Num = new Double(this.FM.getnumberofReworksforPA(OPA, this.getFCID(false)));
//
//            if (Num >=MaxNum ) {
//                ArrayList<String> Pred = OPA.getOriginalPA().getPredecesorsIDsPAIdentfiers();
//                for (int j = 0; j < Interval.size(); j++) {
//                    String s = Interval.get(j).getOPAIdentifier(true);
//                    for (String s1 : Pred) {
//                        if (s.equalsIgnoreCase(s1)) {
//                           
//                            Interval.remove(j);
//
//                        }
//                    }
//                }
//            }
//
//        }
        return Interval;
    }

    /**
     * @param Interval the Interval to set
     */
    public void setInterval(ArrayList<OperationProjectActivity> Interval) {
        this.Interval = Interval;
    }
}
