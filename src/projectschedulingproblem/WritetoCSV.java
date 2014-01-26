/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package projectschedulingproblem;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;

/**
 *
 * @author saeed
 */
public class WritetoCSV {

    private String CurrentDirectory = "";
    private Logger log;
    private boolean insertedheaders = false;

    public WritetoCSV(Logger Log) {
        try {
            String workingDir = System.getProperty("user.dir");
            String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime());

            CurrentDirectory = workingDir + "\\" + timeStamp;
            this.log = Log;
            log.appendToLog(Logger.INFORMATION, "WritetoCSV:Constructor: Current Directory " + CurrentDirectory);
            File Dir = new File(getCurrentDirectory());

            if (!Dir.exists()) {
                boolean result = Dir.mkdir();
                if (result) {
                    log.appendToLog(Logger.INFORMATION, "WritetoCSV:Constructor: Dir Doesnt Exist ... created Directory  " + CurrentDirectory);
                } else {

                    log.appendToLog(Logger.INFORMATION, "WritetoCSV:Constructor: Dir Doesnt Exist ... but i dont have Previlage to do it   " + CurrentDirectory);
                }
            }
            writeGeneticstoCSV(null, true, "Nothing");
            WriteFinalResultsExcel(null, true, "Nothing");
            WriteFinalExcution(null, true);

        } catch (Exception e) {
        }

    }

    public final synchronized void writeGeneticstoCSV(Generation Gen, boolean justHeaders, String Projectnames) {

        FileWriter writer;

        try {


            writer = new FileWriter(this.CurrentDirectory + "\\" + "Generations" + ".csv", true);

            if (justHeaders) {
                writer.append("ProjectID,GenID,Chromosome,Duration,Fitness,Mutated,ProjectRunningStats");

            } else {
                if (!Projectnames.equals("Nothing")) {// to be able add the colomns of project names as the headers for the Delay time 
                    writer.write("," + Projectnames + "\n");
                } else {
                    writer.append(Gen.GenerateCSVStringforgenerationcsv());
                }


//                if(Gen==null){// to be able add the colomns of project names as the headers for the Delay time 
//                     writer.write(","+Projectnames+"\n");
//                }else{
//                  writer.append(Gen.GenerateCSVStringforgenerationcsv());     
//                }
            }

            writer.flush();
            writer.close();
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(WritetoCSV.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void WriteRcstofile(RecordScriptCapsule RSC) {

        FileWriter writer;

        try {


            writer = new FileWriter(this.CurrentDirectory + "\\" + "Script.txt", true);

            writer.append(RSC.toString());


            writer.flush();
            writer.close();
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(WritetoCSV.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

    public final synchronized void WriteError(String S) {

        FileWriter writer;

        try {


            writer = new FileWriter(this.CurrentDirectory + "\\" + "Error" + ".txt", true);
            writer.append(S + '\n');


            writer.flush();
            writer.close();
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(WritetoCSV.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void WriteLoadingProjecttoTxt(OperationProject Op) {

        FileWriter writer;
        String Fname = Op.getProjectName() + "Loading";

        try {
            writer = new FileWriter(this.CurrentDirectory + "\\" + Fname + ".txt", true);
            writer.append("Project Name=" + Fname);
            writer.append('\n');

//            writer.append(Op.toString());
//            writer.append('\n');
//            for (ProjectActivity Opa : Op.getOriginalProject().getPAs()) {
//                writer.append(Opa.toString());
//                writer.append('\n');
//
//            }

            writer.flush();
            writer.close();
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(WritetoCSV.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public final synchronized void WriteFinalResultsExcel(Generation gen, boolean justHeaders, String ProjectNames) {
        FileWriter writer;
        try {
            writer = new FileWriter(this.CurrentDirectory + "\\" + "FinalResults.csv", true);
//            writer.append("Project Name=" + Fname);
//            writer.append('\n');
            if (justHeaders) {
                writer.append("ProjectID,GenID,Chromosome,Duration,Fitness,Mutated,ProjectRunningStats");

            } else {
                if (!ProjectNames.equals("Nothing")) {// to be able add the colomns of project names as the headers for the Delay time 
                    writer.write("," + ProjectNames );
                    writer.write("R3,R5"+ '\n');
                } else {
                    writer.append(gen.GenerateCSVStringForFinalResultCSV());
                }
                

            }

            writer.flush();
            writer.close();
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(WritetoCSV.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public final synchronized void WriteFinalExcution(String S, boolean justHeaders) {
        FileWriter writer;
        try {
            writer = new FileWriter(this.CurrentDirectory + "\\" + "Timings.csv", true);
//            writer.append("Project Name=" + Fname);
//            writer.append('\n');
            if (justHeaders) {
                writer.append("Function,Excution Time" + '\n');
            } else {
                writer.append(S + '\n');

            }

            writer.flush();
            writer.close();
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(WritetoCSV.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void WriteExcutionProjecttoTxt(OperationProject Op) {

        FileWriter writer;
        String Fname = Op.getProjectName();
        CriticalPathCapsule CPX = Op.getCPCapsule();
        try {
            writer = new FileWriter(this.CurrentDirectory + "\\" + Fname + "Excution" + ".txt", true);
            writer.append("Project Name=" + Fname);
            writer.append('\n');
            writer.append(CPX.toString());
            writer.append('\n');
            writer.append(Op.getPES().toString());


//            writer.append(Op.toString());
//            writer.append('\n');
//            for (ProjectActivity Opa : Op.getOriginalProject().getPAs()) {
//                writer.append(Opa.toString());
//                writer.append('\n');
//
//            }

            writer.flush();
            writer.close();
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(WritetoCSV.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void WritetoCSV(String FileName) {

        FileWriter writer;
        // String Fname=Op.getProjectName();
        try {
            writer = new FileWriter(this.CurrentDirectory + "\\" + FileName + ".txt", true);



            writer.append("DisplayName");
            writer.append(',');
            writer.append("Age");
            writer.append('\n');
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(WritetoCSV.class.getName()).log(Level.SEVERE, null, ex);
        }



    }

    /**
     * @return the CurrentDirectory
     */
    public final String getCurrentDirectory() {
        return CurrentDirectory;
    }

    /**
     * @param CurrentDirectory the CurrentDirectory to set
     */
    public void setCurrentDirectory(String CurrentDirectory) {
        this.CurrentDirectory = CurrentDirectory;
    }

    /**
     * @return the log
     */
    public Logger getLog() {
        return log;
    }

    /**
     * @param log the log to set
     */
    public void setLog(Logger log) {
        this.log = log;
    }
}
