/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package projectschedulingproblem;



import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JTextArea;

/**
 *
 * @author saeed
 */

public class Logger {
 public static final int HAS_TO_SHOW = -1;   
 public static final int IMPORTANT = 1;
 public static final int PROCESSING= 2;
 public static final int INFORMATION = 3;
 
 private JTextArea LoggerTextArea;
 private int Verbose=0;//highest verbose is 3 meaning that 3 is will be the verbised 



public Logger(JTextArea LTA){
    LoggerTextArea=LTA;
    this.appendToLog(Logger.INFORMATION, "Logger : Constructor1 Logger has been initialized");
}

public void appendToLog(int verbose, String Str ){
  
   int OrigVerbose=this.getVerbose();
   int Verbse=OrigVerbose;
    if(MainForm.DeactivateTA){
      Verbse=0;
   }
    if(verbose<=Verbse){
       DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
       Date date = new Date();  
    String newSTR= dateFormat.format(date).toString()+": " +Str +"\n";
    
    this.LoggerTextArea.append(newSTR);
    }
}

    /**
     * @return the Verbose
     */
    public int getVerbose() {
        return Verbose;
    }

    /**
     * @param Verbose the Verbose to set
     */
    public void setVerbose(int Verbose) {
        
        this.Verbose = Verbose;
        this.appendToLog(Logger.INFORMATION, "Logger : SetVerbose Verbose is set to " + Verbose);
    }
}
