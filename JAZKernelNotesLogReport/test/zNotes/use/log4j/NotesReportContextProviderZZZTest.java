package zNotes.use.log4j;

import junit.framework.TestCase;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.util.log.KernelReportContextProviderZZZ;
import basic.zBasic.util.log.ReportLogZZZ;
import basic.zNotes.kernel.NotesContextProviderZZZ;
import basic.zNotes.use.log4j.NotesReportContextProviderZZZ;
import basic.zNotes.use.log4j.NotesReportLogAppenderZZZ;
import basic.zKernel.KernelZZZ;
import custom.zNotes.kernel.KernelNotesZZZ;

public class NotesReportContextProviderZZZTest extends TestCase{
	
	//+++ Test setup
	private static boolean doCleanup = true;		//default = true      false -> kein Aufr�umen um tearDown().
	
	//Kernel/NotesKernel und NotesContext
	private KernelZZZ objKernel = null;
	private KernelNotesZZZ objKernelNotes = null;
	private NotesContextProviderZZZ objContextNotes= null;
	
	//Das zu testende Objekt
	private NotesReportContextProviderZZZ objContextReportTest = null;
	
 
	protected void setUp(){
		try {		
			//Kernel + Log - Object dem TestFixture hinzuf�gen. Siehe test.zzzKernel.KernelZZZTest
			this.objKernel = new KernelZZZ("TEST", "01", "", "ZKernelConfigNotesReportLog_test.ini",(String)null);
			this.objContextNotes = new NotesContextProviderZZZ(objKernel, this.getClass().getName(), this.getClass().getName());
			this.objKernelNotes = new KernelNotesZZZ(objContextNotes, "JAZTest", "01", null);
			this.objContextReportTest = new NotesReportContextProviderZZZ(objContextNotes, objKernelNotes);
			//Das ist dann wohl sp�ter, bei Verwendung der anderen NotesReport...-Klassen  zu tun:   NotesReportLogZZZ.loadKernelContext(objContext, true);  //Mit dem true bewirkt man, dass das file immer neu aus dem ConfigurationsPattern erzeugt wird.
		
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
	
	public void testReadParameterAllFromContextProvider(){	
		try{
			//Parameter, die f�r jeden NotesContextProvider gef�llt sein m�ssen
			String sServer = objContextNotes.getServerCalling();
			assertNotNull(sServer); 
		
			String sDB = objContextNotes.getDBCallingPath();
			assertNotNull(sDB);
			
			String sUser = objContextNotes.getUsername();
			assertNotNull(sUser);
			
			String sPW = objContextNotes.getPassword();
			assertNotNull(sPW); //Merke: Falls null ausgelesen wird, so wird das intern in einen Leerstring ge�ndert
			
			
			//Parameter nur f�r log4j
			String sPathTemp = objContextReportTest.getLog4jPathTemp();
			assertNotNull(sPathTemp); //Merke: Das ist der Dateipfad f�r die temp�ren log-files
			
			String sFileTemp = objContextReportTest.getLog4jFileTemp();
			assertNotNull(sFileTemp);
			assertFalse(sFileTemp.equals(""));
			
			//Auslesen des Pfads zur NotesLog-Datenbank, wie sie im Notes-Kernel Konfiguriert ist.
			String sNotesDBLogPath = objContextReportTest.getNotesDBApplicationLogPath();
			assertNotNull(sNotesDBLogPath);
			assertFalse(sNotesDBLogPath.equals(""));
			
			
			/* wird noch nicht ben�tigt
			String sServerIIOP = objContextReportTest.getServerIIOP();
			if (sServerIIOP!= null){
				assertFalse(sServerIIOP.equals(""));
			}else{			
			}
			
			String sReplication = objContextReportTest.getReplication();
			if(sReplication!=null){
				assertFalse(sReplication.equals(""));
			}else{			
			}		
			*/
		
		}catch(ExceptionZZZ ez){
			fail("Method throws an exception." + ez.getMessageLast());
		}
	}
}//END class
