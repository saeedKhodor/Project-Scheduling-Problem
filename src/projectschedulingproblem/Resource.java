/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package projectschedulingproblem;

/**
 *
 * @author saeed
 */
public class Resource {
    private String ResourceName;
    private float  ResourceQty;
    private Logger Log;
    private float ActivityNeededQty;
    private Portfolio PortfolioParent;
    
    
     public Resource(Logger log){
        this.Log=log;

         getLog().appendToLog(Logger.INFORMATION,"Resource: Constructor 1:Resource Created with only a logger" ); 
    }
    
    public Resource(Portfolio pflio,Logger log,String name,float Qty){
        this.Log=log;
        this.ResourceName=name;
        this.ResourceQty=Qty;
         getLog().appendToLog(Logger.INFORMATION,"Resource: Constructor 2:Resource Created with Name  = "+ResourceName+ "and QTY = "+ResourceQty ); 
    }

    /**
     * @return the ResourceName
     */
    public String getResourceName() {
        return ResourceName;
    }

    /**
     * @param ResourceName the ResourceName to set
     */
    public void setResourceName(String ResourceName) {
        this.ResourceName = ResourceName;
    }

    /**
     * @return the ResourceQty
     */
    public float getResourceQty() {
        return ResourceQty;
    }

    /**
     * @param ResourceQty the ResourceQty to set
     */
    public void setResourceQty(float ResourceQty) {
        this.ResourceQty = ResourceQty;
    }

    /**
     * @return the Log
     */
    public Logger getLog() {
        return Log;
    }

    /**
     * @param Log the Log to set
     */
    public void setLog(Logger Log) {
        this.Log = Log;
    }
    
    @Override
    public String toString(){
       return "ResrouceName = "+this.getResourceName()+ "| Resource QTY ="+this.getResourceQty() +"| Parent portfolio =" + PortfolioParent.toString() ; 
        
       }

    /**
     * @return the ActivityNeededQty
     */
    public float getActivityNeededQty() {
        return ActivityNeededQty;
    }

    /**
     * @param ActivityNeededQty the ActivityNeededQty to set
     */
    public void setActivityNeededQty(float ActivityNeededQty) {
        this.ActivityNeededQty = ActivityNeededQty;
    }

    /**
     * @return the PortfolioParent
     */
    public Portfolio getPortfolioParent() {
        return PortfolioParent;
    }

    /**
     * @param PortfolioParent the PortfolioParent to set
     */
    public void setPortfolioParent(Portfolio PortfolioParent) {
        this.PortfolioParent = PortfolioParent;
    }
    
}
