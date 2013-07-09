/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package projectschedulingproblem;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 *
 * @author saeed
 */
public final class CriticalPathCapsule {

    private ArrayList<CPathOPA> CriticalPathOPAs;
    private float TotalDuration;
    DecimalFormat DFormat = new DecimalFormat(MainForm.PrecFormat);

    public CriticalPathCapsule(ArrayList<CPathOPA> CPOPAs) {
        CriticalPathOPAs = new ArrayList<CPathOPA>();
        processCriticalPathOPAs(CPOPAs);
    }

    public void processCriticalPathOPAs(ArrayList<CPathOPA> CPOPAs) {
        float TotalDur = 0.0f;
        for (CPathOPA CPOpa : CPOPAs) {

            if (CPOpa.ISCritical()) {
                this.getCriticalPathOPAs().add(CPOpa);
                TotalDur += CPOpa.getDuration();
                TotalDur = Float.valueOf(DFormat.format(TotalDur));

            }

        }
       TotalDuration=  Float.valueOf(DFormat.format(TotalDur));
    }

    @Override
    public String toString() {
        String CPath = "";
        for (CPathOPA CPOpa : CriticalPathOPAs) {
            CPath += CPOpa.GetCOPAIDentifier() + "-";
        }
        CPath += "Total Duration of CriticalPath " + TotalDuration;
        return CPath;
    }

    /**
     * @return the CriticalPathOPAs
     */
    public ArrayList<CPathOPA> getCriticalPathOPAs() {
        return CriticalPathOPAs;
    }

    /**
     * @param CriticalPathOPAs the CriticalPathOPAs to set
     */
    public void setCriticalPathOPAs(ArrayList<CPathOPA> CriticalPathOPAs) {
        this.CriticalPathOPAs = CriticalPathOPAs;
    }

    /**
     * @return the TotalDuration
     */
    public float getTotalDuration() {
        return TotalDuration;
    }

    /**
     * @param TotalDuration the TotalDuration to set
     */
    public void setTotalDuration(float TotalDuration) {
        this.TotalDuration = TotalDuration;
    }
}
