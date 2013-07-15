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
public final class Portfolio {

    /**
     *
     */
    private ArrayList<Project> Projects;
    private ArrayList<Resource> GlobalResources;
    private String ExcelFilePath = "";
    private Logger Logger;
    ExcelUtils Ex;
    MainForm Parent;

    public Portfolio(Logger Log) {
        setLogger(Log);
        Log.appendToLog(3, "Portfolio: Logger set correctly");
        Projects = new ArrayList();
        GlobalResources = new ArrayList();
    }

    public Portfolio(MainForm Frame, Logger Log, String Path) {
        setLogger(Log);
        this.Parent = Frame;
        Log.appendToLog(3, "Portfolio: Object created using the second COnstructor ");
        Log.appendToLog(Logger.INFORMATION, "Portfolio: Logger set correctly");

        setExcelFilePath(Path);
        Ex = new ExcelUtils(Logger, ExcelFilePath);
        //this.Parent.getPortfolioPanel();
        //this.Parent.createSheetRadioButtons(Ex.getSheetnames());
        //this.Parent.fillOutsheetLists(Ex.getSheetnames());
        Projects = new ArrayList();

    }

    /**
     * This function will load the Global Resources in to the portfolioArrayList
     *
     * @param Sheetname transfered from the MainForm resourceSheetComboList
     * @param FirstCell transfered from the MainForm ComboList
     * @param secondCell transfered from the MainForm ComboList
     * @param hasheader if the
     *
     */
    public boolean loadGlobalResouces(String Sheetname, String FirstCell, String secondCell, boolean hasheader) {
        this.GlobalResources = Ex.getResoucesFromExcel(this, Sheetname, FirstCell, secondCell, hasheader);
        if (!this.GlobalResources.isEmpty()) {
            for (Resource r : this.GlobalResources) {

                Logger.appendToLog(Logger.HAS_TO_SHOW, r.toString());
            }
            return true;
        }
        return false;

    }

    /**
     * This function will load a project and add it to the Arraylist
     *
     * @param PC which is a project capsule that has all the entry fields in it
     * @param hasheader which would direct the function to skip the first row
     *
     */
    public void loadProject(ProjectCapsule PC, boolean hasheader) {
        Logger.appendToLog(Logger.INFORMATION, "Portfolio : loadProject : the function has been called ");
        Project pr = new Project();
        pr.setPortfolioParent(this);
        pr.setLog(Logger);
        pr = (Ex.getProjectFromExcel(pr, PC, hasheader));
        //adjusting RefIds to PA indetifiers 
        for (ProjectActivity PA : pr.getPAs()) {

            PA.convertPredessorIdstoPAIdentifiers();
            PA.convertReworkerstoPAIdentifiers();
            PA.convertProbabilityofbeingworkedinFeedbacktoPAIdentifiers();
            PA.convertReworkProbabilitytoPAIdentifiers();
        }
        this.Projects.add(pr);
        Logger.appendToLog(Logger.HAS_TO_SHOW, "Portfolio : loadProject : Project has been Created " + pr.toString());
        int i = 1;
        for (Project p : this.Projects) {
            Logger.appendToLog(Logger.IMPORTANT, "Portfolio : loadProject : Element " + i + " in ProjectArrayList " + p.toString());
            i++;
        }
    }

    public Resource getResourcebyName(String name) {
        Resource Ret = null;
        for (Resource Rs : this.getGlobalResources()) {

            if (Rs.getResourceName().equalsIgnoreCase(name)) {
                Ret = Rs;
                break;
            }

        }

        return Ret;
    }

    public ArrayList<String> getSheetnames() {

        return Ex.getSheetnames();

    }

    /**
     * @return the ExcelFilePath
     */
    public String getExcelFilePath() {
        return ExcelFilePath;
    }

    /**
     * @param ExcelFilePath the ExcelFilePath to set
     */
    public void setExcelFilePath(String ExcelFilePath) {
        this.ExcelFilePath = ExcelFilePath;
    }

    /**
     * @return the Projects
     */
    public ArrayList<Project> getProjects() {
        return Projects;
    }

    /**
     * @param Projects the Projects to set
     */
    public void setProjects(ArrayList<Project> Projects) {
        this.Projects = Projects;
    }

    /**
     * @return the Logger
     */
    public Logger getLogger() {
        return Logger;
    }

    /**
     * @param Logger the Logger to set
     */
    public final void setLogger(Logger Logger) {
        this.Logger = Logger;
    }

    /**
     * @return the GlobalResources
     */
    public ArrayList<Resource> getGlobalResources() {
        return GlobalResources;
    }

    /**
     * @param GlobalResources the GlobalResources to set
     */
    public void setGlobalResources(ArrayList<Resource> GlobalResources) {
        this.GlobalResources = GlobalResources;
    }
}
