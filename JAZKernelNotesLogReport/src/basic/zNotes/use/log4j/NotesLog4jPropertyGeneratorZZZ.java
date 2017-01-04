package basic.zNotes.use.log4j;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import custom.zNotes.kernel.KernelNotesZZZ;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.file.FileEasyZZZ;
import basic.zBasic.util.log.KernelReportContextProviderZZZ;
import basic.zBasic.util.log.Log4jPropertyGeneratorZZZ;
import basic.zNotes.kernel.NotesContextProviderZZZ;

public class NotesLog4jPropertyGeneratorZZZ  extends Log4jPropertyGeneratorZZZ{
	private NotesLog4jPropertyGeneratorZZZ(){
		//private Konstruktor zum Verbergen. === > nut static Methoden einsetzen
		//aber: Von einer Klasse mit private Construktor kann nicht geerbt werden.
	}
	
	/**Default Konfiguration für log4j .
     * Alternativ ist eine Konfiguration über ein Pattern-File möglich.
	 * @return String
	 *
	 * javadoc created by: 0823, 06.11.2006 - 13:53:29
	 */
	public static String getDefaultConfigPatternString(){
		String sReturn = "#\n"
			 + "# Automatisch durch Log4jPropertyGeneratorZZZ aus Log4jPropertyGeneratorZZZ.defaultProperies generiertes property file für log4j.\n"
			 + "# Alternativ ist eine Konfiguration über ein Pattern-File möglich.\n"
			 + "# Merke: In den 'Pattern' für die Konfiguration sind immer Platzhalter mit @@xyz@@ vorgesehen,\n"
			 + "#             die Werte für diese Platzhalter werden in der (private) Methode .replacePlaceHolderAll(..) der Log4jPropertyGenaratorZZZ - Klasse ersetzt.\n"
			 + "#\n"		 
			 + "\n"
			 + "\n# Merke: Das Konzept sieht vor, dass die Daten erst per ROLL-Appender in Files geloggt werden. (Der NOTESLOGGERAPPENDER wird zwar gleichzeitig ausgeführt)."
			 + "\n#           Der NOTESAPPENDER fügt dann alle x-Mal die Inhalte der Dateien dem Notesdokument hinzu."
			 + "\nlog4j.rootLogger=@@LEVEL@@, NOTESLOGGER"	
			 + "\n"
			 + "\n# Merke: Ggf. kann auch der STDOUTLOGGER verwendet werden, der hier als Consolen-Appender definiert ist."
			 + "\n# log4j.rootLogger=@@LEVEL@@, STDOUTLOGGER, ROLL, NOTESLOGGER"
			 + "\n"
			 + "#################################################################\n"
			 + "### Definition for Stdout logger\n"
			 + "#################################################################\n"
			 + "\n"
			 + "log4j.appender.STDOUTLOGGER=org.apache.log4j.ConsoleAppender\n"
			 + "log4j.appender.STDOUTLOGGER.layout=org.apache.log4j.PatternLayout\n"
			 + "\n"
			 + "# Pattern to output the caller's file name and line number.\n"
			 + "log4j.appender.STDOUTLOGGER.layout.ConversionPattern=%d [%t] %-5p %c - %m%n\n"
			 + "\n"
			 + "\n"
			 + "#################################################################\n"
			 + "### Definition for Notes logger - writes to notes db\n"
			 + "#################################################################\n"
			 + "\n"
			 + "log4j.appender.NOTESLOGGER=basic.zNotes.log4j.NotesReportLogZZZ\n"
			 + "log4j.appender.NOTESLOGGER.layout=org.apache.log4j.PatternLayout\n"
			 + "\n"
			 + "# Pattern to output the caller's file name and line number.\n"
			 + "log4j.appender.NOTESLOGGER.layout.ConversionPattern=%d [%t] %-5p %c - %m%n\n"
			 + "\n"
			 + "log4j.appender.NOTESLOGGER.File=@@FILENAMETEMP@@\n"
			 + "\n"
			 + "#Max Size of temporary files. Dont choose to large value, because these files will be converted to notes documents.\n"
			 + "log4j.appender.NOTESLOGGER.maxFileSize=1KB"
			 + "\n# Keep 10 backup files\n"
			 + "log4j.appender.NOTESLOGGER.maxBackupIndex=10"
			 + "\n"
			 + "\n# Name of target Database\n"
			 + "log4j.appender.NOTESLOGGER.dbLog=@@DBApplicationLog@@"
			 + "\n"
			 + "# Domino Server Name (e.g. ServerName/Organisation)\n"
			 + "log4j.appender.NOTESLOGGER.NotesServer=@@DBServer@@"
			 + "\n"
			 + "# Password for local/domino or IIOP Session - can be left empty on servers\n"
			 + "log4j.appender.NOTESLOGGER.NotesPassword=@@Password@@"
			 + "\n"
			 + "#User\n"
			 + "log4j.appender.NOTESLOGGER.NotesUser=@@User@@"
			 + "\n"
			 + "# Enable a Replication Flag in the generated NotesLog-Documents\n"
			 + "log4j.appender.NOTESLOGGER.flagReplication=@@FlagReplication@@"
			 + "\n"
			 + "#################################################################\n"
			 + "### Definition for Rolling File Appender logger\n"
			 + "#################################################################\n"
			 + "\n"
			 + "log4j.appender.ROLL=org.apache.log4j.RollingFileAppender\n"
			 + "log4j.appender.ROLL.File=c:\\\\temp\\\\log.txt\n"
			 + "\n"
			 + "log4j.appender.ROLL.maxFileSize=1MB\n"
			 + "# Keep 10 backup files\n"
			 + "log4j.appender.ROLL.maxBackupIndex=10\n"
			 + "\n"
			 + "log4j.appender.ROLL.layout=org.apache.log4j.PatternLayout\n"
			 + "log4j.appender.ROLL.layout.ConversionPattern=%d [%t] %-5p %c - %m%n\n"
			 + "\n";
		return sReturn;
	}
	
	
	
	public static boolean createFile(NotesReportContextProviderZZZ objContext) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			try{ 					
			//Erstelle ggf. das Verzeichnis
			String sDir = objContext.getLog4jPathConfig();
			if(StringZZZ.isEmpty(sDir)){
				sDir = ".";
			}else{
				FileEasyZZZ.makeDirectory(sDir);
			}
		
						
			//Filewriter für die Datei
			String sFile = objContext.getLog4jFileConfig();
			if(StringZZZ.isEmpty(sFile)){
				ExceptionZZZ ez = new ExceptionZZZ("Log4jFile not configured as a property in the configuration.", iERROR_CONFIGURATION_MISSING, null, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			FileWriter fw = new FileWriter(sDir + "\\" + sFile);
			
			
			//+++ Den zu schreibenden Inhalt
			//Hole ggf. ein Pattern aus der Configuration
			String sFilePattern = objContext.getLog4jFilePattern();
			String sDirPattern = null;
			String sContentPattern = "";
			if(!StringZZZ.isEmpty(sFilePattern)){
				//Es wird sonst die default Konfiguration genommen
				//ExceptionZZZ ez = new ExceptionZZZ("Log4jPatternFile not configured as a property in the configuration.", iERROR_CONFIGURATION_MISSING, null, ReflectionZZZ.getMethodCurrentName());
				//throw ez;
				
				sDirPattern = objContext.getLog4jPathPattern();		
				if(StringZZZ.isEmpty(sDirPattern)){
					sDirPattern = ".";
				}
				
				
				String stemp = new String(sDirPattern + "\\" + sFilePattern);
				FileReader objFR = new FileReader(stemp);
				BufferedReader objBFR= new BufferedReader(objFR);
				
				String sLine = null;
				sLine = objBFR.readLine();	
				if(sLine!=null) sContentPattern = sLine;
				sLine = objBFR.readLine();
				while(sLine!=null){
					//Zeile anhängen
					sContentPattern = sContentPattern + "\n" + sLine;
					sLine = objBFR.readLine();
				}
			}else{ 
				sContentPattern = getDefaultConfigPatternString();
			}
			
			//Die Platzhalter aus dem "Pattern" ersetzen
			String sContent = replacePlaceholderAll (objContext, sContentPattern); 
			fw.write(sContent);
			fw.flush(); 
			fw.close();
			 //TSA-JAVA0174 
			fw = null;
			
			bReturn = true;
			}catch(IOException e){
				ExceptionZZZ ez = new ExceptionZZZ(e.getMessage(), iERROR_RUNTIME, null, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
		}//END main:
		return bReturn;
	}
	
	/**Platzhalter ersetzen
	 * @param input
	 * @return
	 * @throws ExceptionZZZ 
	 */

	public static String replacePlaceholderAll(NotesReportContextProviderZZZ objContext , String sInput) throws ExceptionZZZ {
		/*Das sind die Variablen, die im Appeder definiert wurden.
		 *Ihr Wert wird aus der Properties Datei ausgelesen: 
		 * 	protected String server = "";
		 * protected String dbLog = "";
		 * 	protected String password="";
		 * protected String user="";
		 * protected String flagReplication="YES";
		 */
		
		String sReturn = sInput;
		main:{
		//1. Die Platzhalter ersetzen, die in der Elternklasse definiert sind
	    //Log4jLevel .....
		sReturn = Log4jPropertyGeneratorZZZ.replacePlaceholderAll((KernelReportContextProviderZZZ) objContext, sReturn);
		
		//##########################################################
		//2. Platzhalter für diesen speziellen appender
		//Temporäres file, in das der "Rolling File Appender" schreibt (Merke: Der Notes Appender erweitert den Rolling File Appender)
		String sBasePath = objContext.getLog4jPathTemp();	
		String stemp = StringZZZ.replace(sBasePath, "\\", "\\\\");  //In der Konfigurationsdatei müssen alle Pfade mit doppeltem Backslash stehen.
		sBasePath = stemp;
		
		String sFileName= objContext.getLog4jFileTemp();
		stemp = StringZZZ.replace(sFileName, "\\", "\\\\");  //In der Konfigurationsdatei müssen alle Pfade mit doppeltem Backslash stehen.
		sFileName = stemp;
		sReturn = StringZZZ.replace(sReturn, "@@FILENAMETEMP@@", sBasePath + "\\\\" + sFileName);  //!!!! Wie auch in der Konfiguration, so muss hier ein doppelter Backslash rein !!!
		
		//Ermittlung der Notesdatenbank, die fürs Logging verwendet werden soll
		//für:  + "log4j.appender.NOTESLOGGER.notesDb=@@DBApplicationLog@@\n"
		String sFilePathDBApplicationLog = objContext.getNotesDBApplicationLogPath();
		stemp = StringZZZ.replace(sFilePathDBApplicationLog, "\\", "\\\\");  //In der Konfigurationsdatei müssen alle Pfade mit doppeltem Backslash stehen.
		sReturn = StringZZZ.replace(sReturn, "@@DBApplicationLog@@", stemp);
		
		
		//Ermittlung des Servers, auf dem die Datenbank liegt
		//für:  + "#log4j.appender.NOTESLOGGER.dominoServer=@@DBServer@@n"
		String sServerDBApplicationLog = objContext.getNotesDBApplicationLogServer();
		sReturn = StringZZZ.replace(sReturn, "@@DBServer@@", sServerDBApplicationLog);
		
		//Ermittlung, ob das Replikationsflag in den erstellten Dokumenten aktiviert werden soll
		String sFlagReplication = objContext.getFlagReplicationString();
		sReturn = StringZZZ.replace(sReturn, "@@FlagReplication@@", sFlagReplication);
		
		
		//##########################################################
		//3. Platzhalter, die im NotesKernel definiert sind
		KernelNotesZZZ objKernelNotes = objContext.getKernelNotesObject();
		NotesContextProviderZZZ objContextKernelNotes = objKernelNotes.getNotesContextProvider();
		
		String sUser = objContextKernelNotes.getUsername();
		sReturn = StringZZZ.replace(sReturn, "@@User@@", sUser);
		
		String sPassword = objContextKernelNotes.getPassword();
		sReturn = StringZZZ.replace(sReturn, "@@Password@@", sPassword);
		
		}//END main:
		return sReturn;
	}
}//END class
