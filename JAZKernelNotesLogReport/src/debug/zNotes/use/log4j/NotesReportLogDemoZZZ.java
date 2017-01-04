package debug.zNotes.use.log4j;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.util.log.ReportLogCommonZZZ;
import basic.zNotes.kernel.NotesContextProviderZZZ;
import basic.zNotes.use.log4j.NotesReportContextProviderZZZ;
import basic.zNotes.use.log4j.NotesReportLogZZZ;
import basic.zKernel.KernelZZZ;
import custom.zNotes.kernel.KernelNotesZZZ;

public class NotesReportLogDemoZZZ {

	/** TODO What the method does.
	 * @param args
	 * 
	 * lindhaueradmin; 09.11.2006 09:17:30
	 */
	public static void main(String[] args) {
		KernelZZZ objKernel;
		try {
			objKernel = new KernelZZZ("TEST", "01", "", "ZKernelConfigNotesReportLog_test.ini",(String)null);		
			NotesContextProviderZZZ objContextNotes = new NotesContextProviderZZZ(objKernel, "test.zNotes.log4j.NotesReportLogZZZTest", "test.zNotes.log4j.NotesReportLogZZZTest");
			KernelNotesZZZ objKernelNotes = new KernelNotesZZZ(objContextNotes, "JAZTest", "01", null);
			NotesReportContextProviderZZZ objContextNotesReport = new NotesReportContextProviderZZZ(objContextNotes, objKernelNotes);
			
			NotesReportLogZZZ.loadKernelContext(objContextNotesReport, true);  //Mit dem true bewirkt man, dass das file immer neu aus dem ConfigurationsPattern erzeugt wird.
			//System.out.println(NotesReportLogZZZ.getBasePath());
			NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "Das ist ein Test");			
			for(int icount = 1; icount <= 100; icount++){
				NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, icount + " Eintrag -TEST");
			}
			
			NotesReportLogZZZ.endit(true);  //Das true bewirkt, dass die letzten Datensätze eingelesen und die temporären Dateien entfernt werden.
			
		} catch (ExceptionZZZ e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}//End main()

}
