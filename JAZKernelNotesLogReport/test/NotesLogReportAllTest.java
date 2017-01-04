import junit.framework.Test;
import junit.framework.TestSuite;

import zNotes.use.log4j.NotesLog4jPropertyGeneratorZZZTest;
import zNotes.use.log4j.NotesReportContextProviderZZZTest;
import zNotes.use.log4j.NotesReportLogZZZTest;



public class NotesLogReportAllTest {
	public static Test suite(){
		TestSuite objReturn = new TestSuite();
		//Merke: Die Tests bilden in ihrer Reihenfolge in etwa die Hierarchie im Framework ab. 
		//            Dies beim Einf�gen weiterer Tests bitte beachten.         
		 
		objReturn.addTestSuite(NotesReportContextProviderZZZTest.class);
		objReturn.addTestSuite(NotesLog4jPropertyGeneratorZZZTest.class);
		objReturn.addTestSuite(NotesReportLogZZZTest.class);
		
		return objReturn;
	}
	
	/**
	 * Hiermit eine Swing-Gui starten.
	 * Das ist bei eclipse aber nicht notwendig, au�er man will alle hier eingebundenen Tests durchführen.
	 * @param args
	 */
	public static void main(String[] args) {
		//Ab Eclipse 4.4 ist junit.swingui sogar nicht mehr Bestandteil des Bundles
		//also auch nicht mehr unter der Eclipse Variablen JUNIT_HOME/junit.jar zu finden
		//junit.swingui.TestRunner.run(NotesLogReportAllTest.class);
	}

}
