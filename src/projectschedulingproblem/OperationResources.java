/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package projectschedulingproblem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *this Object will control the resources for an instance that is run 
 * @author saeed
 */
public class OperationResources {
    ArrayList<Resource> OriginalRes;
    ArrayList<OperationRes> OperationalResList;
   final static int QUANTITYRESEREVED=1;// this is returned by LOCK resource function with confirmation that it is reserved
   final static int QUANTITYEXCEEDSAVAILABLEAMOUNT=2;//this is return when the LOCK Resource fuincton has failed to reserve the amount of needed resources and the OPA has to wait.
    final static int QUANTITYEXCEEDSORIGINALAMOUNT=3;//this would mean that there is an error in the eacell and the resource usage is not well setup
    final static int QUANTITYRELEASED=4;
    Logger Log;
    //HashMap<String, Quantity>
    public OperationResources(ArrayList <Resource> Res,Logger Log){
        this.OriginalRes=Res;
        this.Log=Log;
        OperationalResList=new ArrayList<OperationRes>();
      
    }
    public void createOperationReses(){
        
        for(Resource Res :OriginalRes){
            
         OperationalResList.add(new OperationRes(Res));
         
        }
    }
     public int LockResourceOPA(OperationProjectActivity OPA, int Numberofsteps){
       HashMap <String,Double> HM=OPA.getOriginalPA().getNeededRes();
                  Set<Map.Entry<String, Double>> set = HM.entrySet();
                for (Map.Entry<String, Double> Res : set) {
                   String ResName= Res.getKey();
                   Double QNeeded=Res.getValue();
                    OperationRes OPR= this.getOperationResourcebyName(ResName);
                     float QAvailable=OPR.getAvailableQuantity();
                     float UsedQ=OPR.getQuantityUsed();
                     UsedQ+=QNeeded;
                     
                     OPR.setQuantityUsed(UsedQ);
                Log.appendToLog(Logger.INFORMATION, "OperationResources: LockResourceOPA: "+OPA.getOPAIdentifier(false)+" Locking Resource "+ResName + " with Qty needed"+"QNeededhas available "+OPR.getAvailableQuantity());
                String LLog=OPA.getOPAIdentifier(false)+",Step "+Numberofsteps ;
                OPR.addReleaseStringtoResourcesLog(LLog, QNeeded);
                }

        return 0;
    } 
     
          public int LockResourceOPA(GeneticOPA OPA){
       HashMap <String,Double> HM=OPA.getOriginalOPA().getOriginalPA().getNeededRes();
                  Set<Map.Entry<String, Double>> set = HM.entrySet();
                for (Map.Entry<String, Double> Res : set) {
                   String ResName= Res.getKey();
                   Double QNeeded=Res.getValue();
                    OperationRes OPR= this.getOperationResourcebyName(ResName);
                     float QAvailable=OPR.getAvailableQuantity();
                     float UsedQ=OPR.getQuantityUsed();
                     UsedQ+=QNeeded;
                     
                     OPR.setQuantityUsed(UsedQ);
                Log.appendToLog(Logger.INFORMATION, "OperationResources: LockResourceOPA: "+OPA.getOPAIdentifier(false)+" Locking Resource "+ResName + " with Qty needed"+"QNeededhas available "+OPR.getAvailableQuantity());

                }

        return 0;
    } 
     
     // this function will check if the OPA is able to reseerve its quantity without any fight
      public boolean canOPALockAllItsResources(OperationProjectActivity OPA){
          boolean itcan=true;
          HashMap <String,Double> HM=OPA.getOriginalPA().getNeededRes();
                  Set<Map.Entry<String, Double>> set = HM.entrySet();
                for (Map.Entry<String, Double> Res : set) {
                   String ResName= Res.getKey();
                   Double QNeeded=Res.getValue();
                    OperationRes OPR= this.getOperationResourcebyName(ResName);
                     float QAvailable=OPR.getAvailableQuantity();
                     float UsedQ=OPR.getQuantityUsed();
                     UsedQ+=QNeeded;
                     
                    if(QNeeded>QAvailable){
                       itcan=false;
                       break;
                    }
          
           
                }
                return itcan;
       }
     
         public boolean canOPALockAllItsResources(GeneticOPA OPA){
             if(OPA.getStatus()!=GeneticOPA.CANSTART){
                 return false;
             }
          boolean itcan=true;
          HashMap <String,Double> HM=OPA.getOriginalOPA().getOriginalPA().getNeededRes();
                  Set<Map.Entry<String, Double>> set = HM.entrySet();
                for (Map.Entry<String, Double> Res : set) {
                   String ResName= Res.getKey();
                   Double QNeeded=Res.getValue();
                    OperationRes OPR= this.getOperationResourcebyName(ResName);
                     float QAvailable=OPR.getAvailableQuantity();
                     float UsedQ=OPR.getQuantityUsed();
                     UsedQ+=QNeeded;
                     
                    if(QNeeded>QAvailable){
                       itcan=false;
                       break;
                    }
          
           
                }
                return itcan;
       }
      public int ReleaseResourceOPA(OperationProjectActivity OPA, int Numberofsteps){
                HashMap <String,Double> HM=OPA.getOriginalPA().getNeededRes();
                  Set<Map.Entry<String, Double>> set = HM.entrySet();
                  
                for (Map.Entry<String, Double> Res : set) {
                   String ResName= Res.getKey();
                   Double QNeeded=Res.getValue();
                    OperationRes OPR= this.getOperationResourcebyName(ResName);
                     float QAvailable=OPR.getAvailableQuantity();
                     float UsedQ=OPR.getQuantityUsed();
                     UsedQ-=QNeeded;
                     OPR.setQuantityUsed(UsedQ);
                Log.appendToLog(Logger.INFORMATION, "OperationResources: ReleaseResourceOPA: "+OPA.getOPAIdentifier(false)+" Releasing Resource "+ResName + " with Qty needed"+"QNeededhas available "+OPR.getAvailableQuantity());
                String RLog=OPA.getOPAIdentifier(false)+",Step "+Numberofsteps ;
                OPR.addReleaseStringtoResourcesLog(RLog, QNeeded);
                
                }
           return QUANTITYRELEASED;

    } 
      
       public int ReleaseResourceOPA(GeneticOPA Gopa){
                HashMap <String,Double> HM=Gopa.getOriginalOPA().getOriginalPA().getNeededRes();
                  Set<Map.Entry<String, Double>> set = HM.entrySet();
                  
                for (Map.Entry<String, Double> Res : set) {
                   String ResName= Res.getKey();
                   Double QNeeded=Res.getValue();
                    OperationRes OPR= this.getOperationResourcebyName(ResName);
                     float QAvailable=OPR.getAvailableQuantity();
                     float UsedQ=OPR.getQuantityUsed();
                     UsedQ-=QNeeded;
                     OPR.setQuantityUsed(UsedQ);
                Log.appendToLog(Logger.INFORMATION, "OperationResources: ReleaseResourceOPA: "+Gopa.getOPAIdentifier(false)+" Releasing Resource "+ResName + " with Qty needed"+"QNeededhas available "+OPR.getAvailableQuantity());
            
                
                }
           return QUANTITYRELEASED;

    } 
    public int LockResource(String ResName,Double Qty){
       OperationRes OPR= this.getOperationResourcebyName(ResName);
       if(Qty>OPR.getOriginalQuantity()){
           return QUANTITYEXCEEDSORIGINALAMOUNT;
           
       }
       if(Qty<=OPR.getAvailableQuantity()){
         
         float UsedQ=OPR.getQuantityUsed();
         UsedQ+=Qty;
         
           OPR.setQuantityUsed(UsedQ);
           Log.appendToLog(Logger.INFORMATION, "OperationResources: LockResources: Locking Resource "+ResName + " with Qty "+UsedQ);
           return QUANTITYRESEREVED;
       }
       else if (Qty>OPR.getAvailableQuantity()){
           return QUANTITYEXCEEDSAVAILABLEAMOUNT;
       }
        
        return 0;
    } 
    // be called when it finishes
    public int ReleaseResource(String ResName,float Qty){
       OperationRes OPR= this.getOperationResourcebyName(ResName);

       float UsedQ=OPR.getQuantityUsed();
       UsedQ-=Qty;
           
           OPR.setQuantityUsed(UsedQ); 
           Log.appendToLog(Logger.INFORMATION, "OperationResources: LockResources: Realease Resource "+ResName + " with Qty "+UsedQ +" Param QTY"+Qty);
           return QUANTITYRELEASED;

    } 
    
    public OperationRes getOperationResourcebyName (String name){
        OperationRes Ret=null;
        for ( OperationRes Rs : this.OperationalResList){

            if(Rs.getOPResName().equalsIgnoreCase(name)){
                Ret=Rs;
                break;
            }

        }
    
    return Ret;
}
}
