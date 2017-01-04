package zNotes.use.log4j;

import junit.framework.TestCase;
import basic.zBasic.ExceptionZZZ;
import basic.zNotes.kernel.NotesContextProviderZZZ;
import basic.zNotes.use.log4j.NotesReportContextProviderZZZ;
import basic.zNotes.use.log4j.NotesReportLogZZZ;
import basic.zKernel.KernelZZZ;
import custom.zNotes.kernel.KernelNotesZZZ;

public class NotesReportLogZZZTest extends TestCase{
//	+++ Test setup
	private static boolean doCleanup = true;		//default = true      false -> kein Aufr�umen um tearDown().
	
	//Kernel und Log-Objekt
	private KernelZZZ objKernel = null;
	private NotesContextProviderZZZ objContextNotes = null;
	private KernelNotesZZZ objKernelNotes = null;
	private NotesReportContextProviderZZZ objContextNotesReport= null;
	
	
	//Das zu testende Objekt
	//Merke: ReportLogZZZ hat nur statische Methoden, die  man von aussen testen kann
	
 
	
	protected void setUp(){
		try {		
//			Kernel + Log - Object dem TestFixture hinzuf�gen. Siehe test.zzzKernel.KernelZZZTest
			this.objKernel = new KernelZZZ("TEST", "01", "", "ZKernelConfigNotesReportLog_test.ini",(String)null);
			this.objContextNotes = new NotesContextProviderZZZ(objKernel, this.getClass().getName(), this.getClass().getName());
			this.objKernelNotes = new KernelNotesZZZ(objContextNotes, "JAZTest", "01", null);
			this.objContextNotesReport = new NotesReportContextProviderZZZ(objContextNotes, objKernelNotes);
			
			NotesReportLogZZZ.loadKernelContext(objContextNotesReport, true);  //Mit dem true bewirkt man, dass das file immer neu aus dem ConfigurationsPattern erzeugt wird.
		
		}catch(ExceptionZZZ ez){
			fail("Method throws an exception." + ez.getMessageLast());
		}
	
	}//END setup
	
	public void tearDown() throws Exception {
		if(doCleanup){
			cleanUp();
		}
	}
	
	/**************************************************************************/
	/**** diese Aufr�um-Methode muss mit Leben gef�llt werden *****************/
	/**************************************************************************/
	private void cleanUp() {
		//do your cleanup Work
		//this.objContextTest.recycle();
		
		
		//Merke: Es wird bei Erzeugung des DJAgentContext immer ein Noesdocument k�nstlich erzeugt. 
		//           Dies kann man ggf. hier l�schen.
		/*
		if (nlDoc != null) {
			try {
				nlDoc.remove(true);
			} catch (NotesException e) {
				e.printStackTrace();
			}
		}
		*/
	}
	
	
	//###################################################
	//Die Tests
/* weil es keinen public Konstruktor gibt, auskommentiert
	public void testContructor(){
		System.out.println(ReflectionZZZ.getMethodCurrentName()+"#Start");
	
	}//END testConstructor
	*/
	
	public void testLogLevel(){
		try{
			//Den Log Level aus dem Konfigurationsfile auslesen
			int itemp = NotesReportLogZZZ.readLogLevel();
			
			NotesReportLogZZZ.setLogLevel(itemp);
			int itemp2 = NotesReportLogZZZ.getLogLevel();
			assertEquals(itemp2, itemp);	
		}catch(ExceptionZZZ ez){
			fail("Method throws an exception." + ez.getMessageLast());
		}
	}
	
	public void testWriting(){
		try{
			NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "Das ist ein Test");
			
			NotesReportLogZZZ.write(NotesReportLogZZZ.DEBUG, "Zweiter Eintrag -TEST");
			
			NotesReportLogZZZ.endit(true);
		}catch(ExceptionZZZ ez){
			fail("Method throws an exception." + ez.getMessageLast());
		}
	}
	
}//END class

