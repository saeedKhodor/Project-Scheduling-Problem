/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package projectschedulingproblem;

import java.util.HashMap;

/**
 *this will encapsulate resource
 * @author saeed
 */
public class OperationRes {
    private Resource OriginalResource;
    private float OriginalQuantity;
    private float QuantityUsed;
    //create a log of HAshmap that will hold a log for each resources name and the quantity used of this resource
    private HashMap<String,Double> ResourceLog;
    private String OPResName ="";
    
    public OperationRes(Resource OR){
        this.OriginalResource=OR;
        ResourceLog =new HashMap<String, Double>();
        this.OPResName=OriginalResource.getResourceName();
        QuantityUsed=0.0f;
        OriginalQuantity=OriginalResource.getResourceQty();
        
    }
    public void addLOckStringtoResourcesLog(String str,Double QTY){
        // the str starts with L for lock and R for release
        str="L-"+str;
        this.ResourceLog.put(str, QTY);
        
    }
    public void addReleaseStringtoResourcesLog(String str,Double Qty){
           str="R-"+str;
        this.ResourceLog.put(str, Qty);
        
    }
    public float getAvailableQuantity(){
        
        return OriginalQuantity-QuantityUsed;
    }
    /**
     * @return the OriginalResource
     */
    public Resource getOriginalResource() {
        return OriginalResource;
    }

    /**
     * @param OriginalResource the OriginalResource to set
     */
    public void setOriginalResource(Resource OriginalResource) {
        this.OriginalResource = OriginalResource;
    }

    /**
     * @return the OPResName
     */
    public String getOPResName() {
        return OPResName;
    }

    /**
     * @param OPResName the OPResName to set
     */
    public void setOPResName(String OPResName) {
        this.OPResName = OPResName;
    }

    /**
     * @return the OriginalQuantity
     */
    public float getOriginalQuantity() {
        return OriginalQuantity;
    }

    /**
     * @param OriginalQuantity the OriginalQuantity to set
     */
    public void setOriginalQuantity(float OriginalQuantity) {
        this.OriginalQuantity = OriginalQuantity;
    }

    /**
     * @return the QuantityUsed
     */
    public float getQuantityUsed() {
       
        return QuantityUsed;
    }

    /**
     * @param QuantityUsed the QuantityUsed to set
     */
    public void setQuantityUsed(float QuantityUsed) {
        this.QuantityUsed = QuantityUsed;
    }

    /**
     * @return the ResourceLog
     */
    public HashMap<String,Double> getResourceLog() {
        return ResourceLog;
    }

    /**
     * @param ResourceLog the ResourceLog to set
     */
    public void setResourceLog(HashMap<String,Double> ResourceLog) {
        this.ResourceLog = ResourceLog;
    }
    
    
}
