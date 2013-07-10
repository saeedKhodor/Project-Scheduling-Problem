/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package projectschedulingproblem;

/**
 *
 * @author saeed
 */
public class ResourceLoaderCapsule {
    private String ResourceSheetname;
    private String ResourceStartCell;
    private String ResourceEndCell;
    private boolean hasHeader;

    /**
     * @return the ResourceSheetname
     */
    public String getResourceSheetname() {
        return ResourceSheetname;
    }

    /**
     * @param ResourceSheetname the ResourceSheetname to set
     */
    public void setResourceSheetname(String ResourceSheetname) {
        this.ResourceSheetname = ResourceSheetname;
    }

    /**
     * @return the ResourceStartCell
     */
    public String getResourceStartCell() {
        return ResourceStartCell;
    }

    /**
     * @param ResourceStartCell the ResourceStartCell to set
     */
    public void setResourceStartCell(String ResourceStartCell) {
        this.ResourceStartCell = ResourceStartCell;
    }

    /**
     * @return the ResourceEndCell
     */
    public String getResourceEndCell() {
        return ResourceEndCell;
    }

    /**
     * @param ResourceEndCell the ResourceEndCell to set
     */
    public void setResourceEndCell(String ResourceEndCell) {
        this.ResourceEndCell = ResourceEndCell;
    }

    /**
     * @return the hasHeader
     */
    public boolean isHasHeader() {
        return hasHeader;
    }

    /**
     * @param hasHeader the hasHeader to set
     */
    public void setHasHeader(boolean hasHeader) {
        this.hasHeader = hasHeader;
    }

    
    
}
