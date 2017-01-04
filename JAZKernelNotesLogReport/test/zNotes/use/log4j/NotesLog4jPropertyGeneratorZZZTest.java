/**
 * 
 */
package zNotes.use.log4j;

import junit.framework.TestCase;
import basic.zBasic.ExceptionZZZ;
import basic.zNotes.kernel.NotesContextProviderZZZ;
import basic.zNotes.use.log4j.NotesLog4jPropertyGeneratorZZZ;
import basic.zNotes.use.log4j.NotesReportContextProviderZZZ;
import basic.zKernel.KernelZZZ;
import custom.zNotes.kernel.KernelNotesZZZ;

/**
 * @author 0823
 *
 */
public class NotesLog4jPropertyGeneratorZZZTest extends TestCase{
	
	//+++ Test setup
	private static boolean doCleanup = true;		//default = true      false -> kein Aufr�umen um tearDown().
	
	//Kernel/NotesKernel und NotesContext
	private KernelZZZ objKernel = null;
	private KernelNotesZZZ objKernelNotes = null;
	private NotesContextProviderZZZ objContextNotes= null;
	private NotesReportContextProviderZZZ objContextNotesReport = null;

	
	//Das zu testende Objekt
	//Die Klasse heat einen private Konstruktor und soll nur static Methoden besitzen private NotesLog4jPropertyGeneratorZZZ objGeneratorTest = null;
	
 
	protected void setUp(){
		try {		
			//Kernel + Log - Object dem TestFixture hinzuf�gen. Siehe test.zzzKernel.KernelZZZTest
			this.objKernel = new KernelZZZ("TEST", "01", "", "ZKernelConfigNotesReportLog_test.ini",(String)null);
			this.objContextNotes = new NotesContextProviderZZZ(objKernel, this.getClass().getName(), this.getClass().getName());
			this.objKernelNotes = new KernelNotesZZZ(objContextNotes, "JAZTest", "01", null);
			this.objContextNotesReport = new NotesReportContextProviderZZZ(objContextNotes, objKernelNotes);
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
	
	public void testReplacePlaceholderAll(){	
		try{
			String sConfig = NotesLog4jPropertyGeneratorZZZ.replacePlaceholderAll(this.objContextNotesReport, NotesLog4jPropertyGeneratorZZZ.getDefaultConfigPatternString());
			System.out.println(sConfig);
		
		}catch(ExceptionZZZ ez){
			fail("Method throws an exception." + ez.getMessageLast());
		}
	}
}//END class