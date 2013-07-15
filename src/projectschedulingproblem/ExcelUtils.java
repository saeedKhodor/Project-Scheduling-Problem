/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package projectschedulingproblem;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellReference;
/**
 *
 * @author saeed
 */
public  class ExcelUtils {
    private Logger Log;
    private String filename;
    private int NumberofSheets=0;
    private ArrayList <String> Sheetnames;
      
   public ExcelUtils(Logger Log){
       this.Log=Log;
      Log.appendToLog(3, "ExcelUtil: Has been initialized using the first constructor");
   }
    public ExcelUtils(Logger Log,String filename){
       this.Log=Log;
       Log.appendToLog(Logger.INFORMATION, "ExcelUtil: Contstructor2 Has been initialized using the second constructor");
       this.filename=filename;
       Log.appendToLog(Logger.INFORMATION, "ExcelUtil: Contstructor2 the filename is "+ filename);
       this.NumberofSheets=getNumberofSheets(filename);
       Log.appendToLog(Logger.PROCESSING, "ExcelUtil: Contstructor2 NumberofSheets set to "+ NumberofSheets);
       this.Sheetnames= getNamesofSheets(filename);
       Log.appendToLog(Logger.PROCESSING, "ExcelUtil: Contstructor2 Sheetnames set");
     if(Logger.INFORMATION<= Log.getVerbose()){
       for (String s : Sheetnames){
         Log.appendToLog(Logger.HAS_TO_SHOW, "ExcelUtil: Contstructor2 the Sheetname is " + "i= "+ s);  
           
       }
     }
//    System.out.println();
//    Sheet sheet = workbook.getSheetAt(0);
//    System.out.println("Number Of Rows:" + sheet.getLastRowNum());
//
//    Row row = sheet.getRow(0);
//    System.out.println("Cell Value:" + row.getCell(0).getStringCellValue());
   }
    /**
 * gets the number of Sheets in the Excel File.
 *
 * @param filename  the path of the filename
 * 
 */
    public int getNumberofSheets(String filename){ 
      if(filename.equalsIgnoreCase("null")){
          filename=this.getFilename();
      }
        try{
                Workbook wb = this.getWb(filename);
                    //WorkbookFactory.create(inp);
                getLog().appendToLog(Logger.INFORMATION,"ExcelUtils: getNumberofSheets : the Number of Sheets is "+ wb.getNumberOfSheets());
                      
            return wb.getNumberOfSheets();
           
      

         }catch(Exception E){
            getLog().appendToLog(Logger.HAS_TO_SHOW, "ExcelUtils getNumberofSheets: Error Exception Caught " +E.getMessage());
            return 0;
      }
       
    }

public ArrayList getNamesofSheets(String filename){ 
   ArrayList SheetnamesAl=new ArrayList(); 
    if(filename.equalsIgnoreCase("null")){
          filename=this.getFilename();
      }
        try{
//move this part to excelutils get file excel file
       
            Workbook wb = this.getWb(filename);
                getLog().appendToLog(Logger.PROCESSING,"ExcelUtils getNamesofSheets: created Workbook from file :"+filename);
            
            for(int i=0;i<this.getNumberofSheets();i++){
                
                SheetnamesAl.add(wb.getSheetName(i));
          }      
                          
            return SheetnamesAl ;
            
        

         }catch(Exception E){
            getLog().appendToLog(Logger.HAS_TO_SHOW, "ExcelUtils: getNamesofSheets :Error Exception Caught " +E.getMessage());
             return null;
      }
       
       
    }

public ArrayList<Resource> getResoucesFromExcel(Portfolio prtfolio,String Sheetname,String TopLeftCell,String LowerRightCell,boolean SkipTitle){
    
    ArrayList <Resource> AL=new ArrayList(); 
    Workbook wb=this.getWb(this.filename);
   try{
    Sheet Sh=wb.getSheet(Sheetname);
    CellReference StartCell = new CellReference(TopLeftCell);
    CellReference EndCell = new CellReference(LowerRightCell);
      getLog().appendToLog(Logger.INFORMATION,"ExcelUtils: getResoucesFromExcel : getting the dimensions of the Resources ");
    int RowStart=StartCell.getRow();
    int RowEnd=EndCell.getRow();
    int ColStart=StartCell.getCol();
     int ColEnd=EndCell.getCol();
     int Height=RowEnd-RowStart;
     int width=ColEnd-ColStart;
       getLog().appendToLog(Logger.INFORMATION,"ExcelUtils: getResoucesFromExcel : the resource Table dimensions are  [ " + Height+","+width+" ]" );   
     if(SkipTitle){
         RowStart=RowStart+1;   
     }
       for (int rowNum = RowStart; rowNum <= RowEnd; rowNum++) {
       Row r = Sh.getRow(rowNum);
       Resource Res=new Resource(Log);
       Res.setPortfolioParent(prtfolio);
       Cell c = r.getCell(ColStart, Row.RETURN_BLANK_AS_NULL);
       String ResName= ((String) c.getStringCellValue()).trim();
       Res.setResourceName(ResName);
        Cell cq = r.getCell(ColStart+1, Row.RETURN_BLANK_AS_NULL);
        if(cq.getCellType()==Cell.CELL_TYPE_NUMERIC){
          Res.setResourceQty( (float)cq.getNumericCellValue());   
        }
       AL.add(Res);
     }

    return AL;
   
   }  catch(Exception e){
       getLog().appendToLog(Logger.HAS_TO_SHOW,"ExcelUtils: getResoucesFromExcel : the cell is null ");
       return null;
       }
      
}
public Project getProjectFromExcel(Project Pr,ProjectCapsule PC,boolean SkipTitle){// need to add thedsm cell reference , and the reqork impact
   Project pr=Pr;
    Workbook wb=this.getWb(this.filename);
     try{
    getLog().appendToLog(Logger.INFORMATION,"ExcelUtils: getProjectFromExcel : function has started...");
//Load Project Activities
   
    getLog().appendToLog(Logger.HAS_TO_SHOW,"ExcelUtils: getProjectFromExcel : loading Project Activities...");
    
    Sheet Sh=wb.getSheet(PC.getActivitiesSheetname());
     getLog().appendToLog(Logger.INFORMATION,"ExcelUtils: getProjectFromExcel : sheet "+ PC.getActivitiesSheetname() +" has been loaded...");
    CellReference StartCell = new CellReference(PC.getActivitiesStartCell());
    
    int RowStart=StartCell.getRow();
    int ColStart=StartCell.getCol();
     
    
     Cell c = Sh.getRow(RowStart).getCell(ColStart, Row.RETURN_BLANK_AS_NULL);
         getLog().appendToLog(Logger.INFORMATION,"ExcelUtils: getProjectFromExcel : Projectinformation are "+ c.toString());
         if(c != null){
         pr.setProjectName(c.getStringCellValue());
        }
        pr.setPAs(this.getProjectActivitiesFromExcel( Sh,pr,PC.getActivitiesStartCell(),PC.getActivitiesEndCell(),true));
        pr.allActivitieshavebeenloaded();//this will set the Number fo activities
        
        pr=InterpretDSMandRework(pr,PC,true);

  
//     inside this the project activity will be loaded by the below function 
//     moreover the DSM has to be read to fill out the predeseccors and the reqork impact 
//     this will be used by the project to find its activities and add them to Arraylist it has
    
    return pr;
   } catch(Exception Ex){
            getLog().appendToLog(Logger.HAS_TO_SHOW,"ExcelUtils: getProjectFromExcel : Exception has been thrown " + Ex.getMessage());
         return null;
    }
}
public Project InterpretDSMandRework(Project Pr,ProjectCapsule PC,boolean SkipTitle){
    getLog().appendToLog(Logger.INFORMATION,"ExcelUtils: InterpretDSMandRework : Interpretting the DSM and Rework has started ....");   
     Workbook wb=this.getWb(this.filename);
     Project pr=Pr;
     try{
        Sheet ShDSM=wb.getSheet(PC.getDSMSheetname());
         Sheet ShRework=wb.getSheet(PC.getReworkSheetname());
        CellReference DSMStartCell = new CellReference(PC.getDSMStartCell()); 
        CellReference DSMEndCell = new CellReference(PC.getDSMEndCell()); 
        CellReference ReworkStartCell = new CellReference(PC.getReworkStartCell());  
        CellReference ReworkEndCell = new CellReference(PC.getReworkEndCell());
        
        int DSMStartRow=DSMStartCell.getRow();
        int DSMEndRow=DSMEndCell.getRow();
        int DSMRowStartofData=DSMStartRow+1;
        int DSMStartCol=DSMStartCell.getCol();
        int DSMEndCol=DSMEndCell.getCol();
        int DSMColStartofData=DSMStartCol+2;
        getLog().appendToLog(Logger.INFORMATION,"ExcelUtils: InterpretDSMandRework :DSMStartRow "+DSMStartRow+" ' "+" 'DSMEndRow "+DSMEndRow+" 'DSMRowStartofData "+DSMRowStartofData+" 'DSMStartCol "+DSMStartCol+" 'DSMEndCol "+DSMEndCol+" 'DSMColStartofData "+DSMColStartofData);
        int RWStartRow=ReworkStartCell.getRow();
        int RWEndRow=ReworkEndCell.getRow();
        int RWRowStartofData=RWStartRow+1;// the plus 2 is to skip the first two columns since they have the refrence number and the PA Name
        int RWStartCol=ReworkStartCell.getCol();
        int RWEndCol=ReworkEndCell.getCol();
        int RWColStartofData=RWStartCol+2;
        
        
        int DSMDatawidth=DSMEndCol-DSMColStartofData; 
        int DSMDataHeight=DSMEndRow-DSMRowStartofData;
        int RWDatawidth=RWEndCol-RWColStartofData; 
        int RWDataHeight=RWEndRow-RWRowStartofData;
        
        for(int i=DSMRowStartofData;i<=DSMEndRow;i++){
            boolean canstart=true;
       Row r=ShDSM.getRow(i);
       Row RW=ShRework.getRow(RWRowStartofData+(i-DSMRowStartofData));
       Cell ref=r.getCell(DSMStartCol,Row.RETURN_BLANK_AS_NULL);
       
       ProjectActivity PA=Pr.getPAbyRefNum(ref.toString());
       getLog().appendToLog(Logger.PROCESSING,"ExcelUtils: InterpretDSMandRework : Analyzing the Project Activity " +PA.toString()+"i="+i); 
         for(int j=DSMColStartofData;j<i+1;j++){
             Cell ref1=r.getCell(j);
             if( ref1.getCellType()==Cell.CELL_TYPE_NUMERIC){
                 canstart=false;
                getLog().appendToLog(Logger.PROCESSING,"ExcelUtils: InterpretDSMandRework : Predessor for Project  "+PA.getName()+" has been found of Reference "+(j-1));   
                String ID=""+(j-1);
                PA.getPredecesorsIDs().add(ID);
                getLog().appendToLog(Logger.PROCESSING,"ExcelUtils: InterpretDSMandRework : the Predessor has been added to List " +Pr.getPAbyRefNum(ID).getName()+ID);
                PA.getProbabilityofbeingworkedinFeedback().put(ID, Double.parseDouble(ref1.toString()));
                getLog().appendToLog(Logger.PROCESSING,"ExcelUtils: InterpretDSMandRework : Probability of Being worked in feedback is set to " +PA.getProbabilityofbeingworkedinFeedback().get(ID).toString());
                Cell RefRework=RW.getCell(j);
                if( RefRework.getCellType()==Cell.CELL_TYPE_NUMERIC){
                 getLog().appendToLog(Logger.PROCESSING,"ExcelUtils: InterpretDSMandRework : interpreting Rework Table ");   
                String RWID=""+(j-1);
                PA.getReworkImpact().put(RWID, Double.parseDouble(RefRework.toString()));   
                getLog().appendToLog(Logger.PROCESSING,"ExcelUtils: InterpretDSMandRework : Reworked Probability is set" +PA.getReworkImpact().get(RWID).toString());
                }
             } 
        }
        
        PA.setCanStart(canstart);   
        if(canstart){
            getLog().appendToLog(Logger.PROCESSING,"ExcelUtils: InterpretDSMandRework : the PA "+PA.getName() +"Can Start since it has no predecessors" ); 
        }
        
         for(int k=i+2;k<DSMEndCol;k++){
             Cell ref2=r.getCell(k);
            if( ref2.getCellType()==Cell.CELL_TYPE_NUMERIC){
               String ID=""+(k-1); 
               getLog().appendToLog(Logger.PROCESSING,"ExcelUtils: InterpretDSMandRework :  the Reworker ID was added to the HashMAP " +pr.getPAbyRefNum(ID).getName()+ID +" with Probability "+ref2.toString());
                PA.getReworkProbability().put(ID, Double.parseDouble(ref2.toString()));
               
            } 
        //getLog().appendToLog(Logger.INFORMATION,"ExcelUtils: InterpretDSMandRework : moving in the second Diagonal ref2  " +ref2.toString()+"k= "+k); 
      
        }
                  
   //getLog().appendToLog(Logger.INFORMATION,"ExcelUtils: InterpretDSMandRework : getting the dimensions of the Project Activities " +DSMRowStartofData+" "+DSMDataHeight);           
          
        }
        
       return pr;  
     }catch (Exception Ex){
         getLog().appendToLog(Logger.HAS_TO_SHOW,"ExcelUtils: InterpretDSMandRework : Exception has been thrown " + Ex.getMessage());  
         return null;
     }
}
public ArrayList<ProjectActivity> getProjectActivitiesFromExcel( Sheet sh,Project P,String TopLeftCell,String LowerRightCell,boolean SkipTitle){
    // this will be used by the project to find its activities and add them to Arraylist it has
    ArrayList<ProjectActivity> PAs=new ArrayList<ProjectActivity>();
    CellReference StartCell = new CellReference(TopLeftCell);
    CellReference EndCell = new CellReference(LowerRightCell);
      getLog().appendToLog(Logger.INFORMATION,"ExcelUtils: getProjectActivitiesFromExcel : getting the dimensions of the Project Activities ");
    int RowStart=StartCell.getRow();
    int RowEnd=EndCell.getRow();
    int ColStart=StartCell.getCol();
    int ColEnd=EndCell.getCol();
    int Height=RowEnd-RowStart;
    int width=ColEnd-ColStart;
    if(SkipTitle){
         RowStart=RowStart+1;   
     }
    getLog().appendToLog(Logger.INFORMATION,"ExcelUtils: getProjectActivitiesFromExcel : the Project Activities dimensions are  [ " + Height+","+width+" ]" );  
    for (int rowNum = RowStart; rowNum <=RowEnd; rowNum++) {
       Row r = sh.getRow(rowNum);
      // Resource Res=new Resource(Log);
       ProjectActivity PA=new ProjectActivity(P);
       PA.setLog(P.getLog());
    int i=1;
    String ResouceNames="";
    getLog().appendToLog(Logger.INFORMATION,"ExcelUtils: getProjectActivitiesFromExcel : row Number  =" +rowNum);
       for (Cell cell : r) {
           switch(i){
               case 1: {
                   getLog().appendToLog(Logger.INFORMATION,"ExcelUtils: getProjectActivitiesFromExcel : Case 1 Cellnum  =" +1 +" , " +cell.toString());
                   PA.setReferenceNumber(cell.toString());
                 break;
              } 
               case 2: {
                   getLog().appendToLog(Logger.INFORMATION,"ExcelUtils: getProjectActivitiesFromExcel : Case 2 Cellnum =" +i+" , " +cell.toString());
                 PA.setName(cell.getStringCellValue());
                 break;
              }  
               case 3: {
                getLog().appendToLog(Logger.INFORMATION,"ExcelUtils: getProjectActivitiesFromExcel : Case 3 Cellnum =" +i);
                    if(cell.getCellType()==Cell.CELL_TYPE_NUMERIC){
                        PA.setMinimumDuration((float)cell.getNumericCellValue());
                    }else{
                     getLog().appendToLog(Logger.INFORMATION,"ExcelUtils: getProjectActivitiesFromExcel : Case 3 Cellnum =" +i +" , "+cell.getCellType());   
                        
                        
                    }
                 break;
              }    
               case 4: {
                   getLog().appendToLog(Logger.INFORMATION,"ExcelUtils: getProjectActivitiesFromExcel : Case 4 Cellnum  =" +i);
                    if(cell.getCellType()==Cell.CELL_TYPE_NUMERIC){
                        PA.setMostlikelyDuration((float)cell.getNumericCellValue());
                    }
                    break;
              }
               case 5: {
                   getLog().appendToLog(Logger.INFORMATION,"ExcelUtils: getProjectActivitiesFromExcel : Case 5 Cellnum  =" +i);
                    if(cell.getCellType()==Cell.CELL_TYPE_NUMERIC){
                        PA.setMaximumDuration((float)cell.getNumericCellValue());
                    }
                    break;
              }   
               case 6: {
                   getLog().appendToLog(Logger.INFORMATION,"ExcelUtils: getProjectActivitiesFromExcel : Case 6 Cellnum =" +i);
                 ResouceNames=cell.toString();
                 break;
              }   
               case 7: {
                   getLog().appendToLog(Logger.INFORMATION,"ExcelUtils: getProjectActivitiesFromExcel : Case 7 Cellnum =" +i);
                 //PA.setProjectReferenceNumber(cell.getStringCellValue());
                   PA.filloutResourceHashMap(ResouceNames, cell.toString());
                   break;
              }   
               case 8: {
                   getLog().appendToLog(Logger.INFORMATION,"ExcelUtils: getProjectActivitiesFromExcel : Case 8 Cellnum  =" +i);
                    if(cell.getCellType()==Cell.CELL_TYPE_NUMERIC){
                        PA.setMaxiNumofReworks((float)cell.getNumericCellValue());
                    }
                   break;
              }
               case 9: {
                   getLog().appendToLog(Logger.INFORMATION,"ExcelUtils: getProjectActivitiesFromExcel : Case 9 Cellnum  =" +i);
                    if(cell.getCellType()==Cell.CELL_TYPE_NUMERIC){
                        PA.setLearningFactor((float)cell.getNumericCellValue());
                    }
                    break;
              } 
           
           }
        getLog().appendToLog(Logger.INFORMATION,"ExcelUtils: getProjectActivitiesFromExcel : cell outside switch "+  cell.toString());
         i++;   
      }
//PA.setProjectparent(P);
       PAs.add(PA);
        getLog().appendToLog(Logger.IMPORTANT,"ExcelUtils: getProjectActivitiesFromExcel : Project Activity has been Added " + PA.toString() );
     }
    
    return PAs;
}
public Workbook getWb(String Filename){
      try{
    File ex=new File(filename);
        if(!ex.exists()){
                getLog().appendToLog(Logger.HAS_TO_SHOW,"ExcelUtils: getWb : The file doesnt exist");
            return null;
        }else{
            InputStream inp = new FileInputStream(ex);
                getLog().appendToLog(Logger.PROCESSING,"ExcelUtils: getWb : created input Stream for file :"+filename);
            Workbook wb = WorkbookFactory.create(inp);
            return wb;
        }
      }catch(Exception E){
            getLog().appendToLog(Logger.HAS_TO_SHOW, "ExcelUtils:  getWb: Error Exception Caught " +E.getMessage());
             return null;
      }
    
}
    /**
     * @return the Log
     */
    public Logger getLog() {
        return Log;
    }

    /**
     * @return the filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * @return the NumberofSheets
     */
    public int getNumberofSheets() {
        return NumberofSheets;
    }

    /**
     * @return the Sheetnames
     */
    public ArrayList getSheetnames() {
        return Sheetnames;
    }

}
