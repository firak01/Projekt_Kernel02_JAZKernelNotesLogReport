package basic.zNotes.use.log4j;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Vector;

import lotus.domino.Database;
import lotus.domino.NotesException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import custom.zNotes.kernel.KernelNotesZZZ;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IConstantZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.file.FileEasyZZZ;
import basic.zBasic.util.log.IReportLogConstantZZZ;
import basic.zBasic.util.log.KernelReportContextProviderZZZ;
import basic.zBasic.util.log.ReportLogCommonZZZ;
import basic.zNotes.kernel.NotesContextProviderZZZ;

/**Diese Klasse schreibt in den NotesReportLogAppender.
 * D.h. sie sorgt auch dafür, dass die log4j-Konfigurationsdatei entsprechend angepasst wird.
 * 
 * Dadurch entspricht sie nahezu der ReportLogZZZ-Klasse.
 * Vererbung ist allerdings schwierig, weil beide Klassen Singleton und private Konstruktor realisieren.
 * @author lindhaueradmin
 *
 */
public class NotesReportLogZZZ implements IConstantZZZ, IReportLogConstantZZZ{
	private static NotesReportLogZZZ objSingletonNRLog = new NotesReportLogZZZ();
	
	private  Logger objLogger = null;
	private String sBasePath = null;
	private String configFilename=null;
	
	//FGL Erweiterungen / Änderungen
	private NotesReportContextProviderZZZ objContext;
	
	int currentLogLevel = NOT_LOADED;
	private static boolean showTrace = true;
	
	/**
	 * @see write (int, String, boolean)
	 * gibt einen Trace nur dann aus, wenn für die Klasse ein Trace zugelassen ist.
	 * @param logLevel
	 * @param msg
	 */
	public static void write (int logLevel, String msg) {
		write (logLevel, msg, !showTrace );
	}
	
	/**Schreibt eine Stringnachricht ins Log, wenn das in der Konfiguration eingestellte Loglevel es zuläßt. 
	 * @param logLevel - Level auf dem gelogged werden soll
	 * @param msg - Log Nachricht
	 * @param kurz - falls kurz==true, wird kein Trace (Anzeige der aufrufenden Klasse)
	 * ausgegeben.
	 */	
	public static void write(int logLevel, String msg, boolean kurz)
	{
		if (logLevel <= NotesReportLogZZZ.objSingletonNRLog.currentLogLevel) {
			/* mehrere sequentielle Aufrufe von System.out.print{,ln} resultieren
			 * in jeweils einer neuen Zeile.
			 */
			 int t = ReflectCodeZZZ.callStackSize() - INITIAL_STACK_SIZE;
			 if (t < 1) { t = 0; }
			 
			 Level objLevel = ReportLogCommonZZZ.internalToLog4jLevel(logLevel);			 
			 if (kurz) {
			 	objSingletonNRLog.objLogger.log (objLevel, msg);			 	
			 } else {	
				 String sInfo = ReportLogCommonZZZ.logSymbol (logLevel) +" " +  StringZZZ.repeat(". ", t) + ReflectCodeZZZ.lastCaller("ReportLogZZZ") + ": " + msg;
				 objSingletonNRLog.objLogger.log (objLevel, sInfo);
			 }
		}
	}

	/**
 	 * Schreibt eine Stringnachricht ins Log, wenn das im Profildokument
	 * eingestellte Loglevel es zuläßt. 
	 * @see write (int, String)
	 * @param logLevel
	 * @param msg
	 */
	public static void writeString(int logLevel, String msg)
	{
		write(logLevel, msg);
	}

	/**
	 * Schreibt eine Schlüssel-Wert-Liste ins Log, wenn das im Profildokument
	 * eingestellte Loglevel es zuläßt.
	 * @param logLevel
	 * @param keys
	 * @param values
	 */
	public static void writePairs(int logLevel, Vector keys, Vector values)
	{
		StringWriter sw = new StringWriter();
		PrintWriter ps = new PrintWriter(sw);
		ps.println("" + keys.size() + " Schlüssel und " + values.size() + " Werte:");
		for (int i = 0; i < keys.size() || i < values.size(); i++) {
			ps.print((i < keys.size())? keys.elementAt(i): "(null)");
			ps.print("=");
			ps.println((i < values.size())? values.elementAt(i): "(null)");
		}
		write(logLevel, sw.toString());
	}

	/**
 	 * Schreibt eine Exception mit StackTrace ins Log, wenn das im Profildokument
	 * eingestellte Loglevel es zuläßt.
	 * @param logLevel
	 * @param e
	 */
	public static void writeException(int logLevel, Throwable e)
	{
		if (e == null) {
			write (logLevel, "unknown exeption");
		}
		else {
			StringWriter sw = new StringWriter();
			
			//Merke: Hier wurde ein Spezialfall der "NotesException" entfernt
			e.printStackTrace(new PrintWriter(sw));
			try {
				nicewrite(logLevel, "\n" + sw.toString());
			} catch (IOException ex) {
				System.err.println("FATAL: ReportLogZZZ.writeException: " + ex);
			}
		}
	}



	/* setzt lange strings so um, dass write eine saubere ausgabe auf die (Domino) Konsole macht */
	private static void nicewrite (int logLevel, String message) throws IOException {
		String sLine = "";			
		StringReader reader= new StringReader (message);
		for (int s =  reader.read();s>0; s =  reader.read())
			{
				if (s > 0 && s != CARRIAGE_RETURN && s!= LINE_FEED) {
					if (s!=0) { sLine += (char) s; }
				} else {
					if (sLine != null && !sLine.equals( "")) { write (logLevel,sLine, true); }
					sLine = "";
				}
			}
	}

	/**
	 * Schreibt eine Exception mit Loglevel <code>ERROR</code> ins Log,
	 * wenn das im Profildokument eingestellte Loglevel es zuläßt. 
	 * @param e
	 */
	public static void writeException(Throwable e)
	{
		writeException(ERROR, e);
	}

	/**
	 * Schreibt eine Aufzählung ins Log, wenn das im Profildokument
	 * eingestellte Loglevel es zuläßt.
	 * @param logLevel
	 * @param e - auszugebende Enumeration
	 */
	public static void writeEnumeration(int logLevel, Enumeration e)
	{
		if (e == null) {
			write(logLevel, "writeEnumeration: null");
			return;
		}
		write(logLevel, e.getClass().getName());
		for (int i = 0; e.hasMoreElements(); i++) {
			write(logLevel, "" + i + ": " + e.nextElement());
		}
	}
	
	/**
	 * Schreibt einen Vector ins Log
	 * @param logLevel
	 * @param v
	 */
	public static void writeVector(int logLevel, Vector v)
	{
		if (v == null) {
			write(logLevel, "writeVector: null");
			return;
		}
		write(logLevel, v.getClass().getName());
		for (int i = 0, k = v.size(); i<k; i++) {
			try {
				write(logLevel, "" + i + ": " + v.elementAt(i).toString());
			} //TSA-Java0166
				catch (Exception e) {
				write (logLevel, "" + i + ": Exception " + e.toString());
			}
		}
	}
	
	/**
	 * Ändert das Loglevel.
	 * @param newLogLevel
	 */
	public static void setLogLevel(int newLogLevel) {
		objSingletonNRLog.currentLogLevel = newLogLevel;
	}
	/**
	 * Gibt das Loglevel aus.
	 * @return
	 */
	public static int getLogLevel() {
		return objSingletonNRLog.currentLogLevel;
	}
	
	/**
	 * Der basePath der Log Klasse ist per Default das Verzeichnis <<user.dir>>/java
	 * Es kann mit setBasePath verändert werden.
	 * In diesem Pfad werden logs und die log4j.conf gespeichert.
	 * @param path
	 */
	public static final void setBasePath (String path) {
		objSingletonNRLog.sBasePath=path;
	}

	/**
	 * @return
	 */
	public static String getBasePath() {
		return objSingletonNRLog.sBasePath;
	} 
	
	/**
	 * legt fest, ob bei der Ausgabe ein Stacktrace angegeben werden soll.
	 * @param newTraceStatus - true oder false
	 */
	public static void setTraceStatus (boolean newTraceStatus) {
		showTrace = newTraceStatus;
	}
	
	/** 
	 * zeigt an, ob zur Zeit ein Stacktrace beim Loggen ausgegeben wird oder nicht
	 * @return
	 */
	public static boolean getTraceStatus () {
		return showTrace;
	}
	
	/**
	 * Schreibt <i>msg</i> mit dem Level ERROR in die Logdatei.
	 * @param msg
	 */
	public static void error(String msg) {
		write(ERROR, msg); 
	}
	
	/**
	 * Schreibt <i>msg</i> mit dem Level WARN in die Logdatei.
	 * @param msg
	 */
	public static void warn(String msg) {
		write(WARN, msg);
	}
	
	/**
	 * Schreibt <i>msg</i> mit dem Level INFO in die Logdatei.
	 * @param msg
	 */
	public static void info(String msg) {
		write(INFO, msg);
	}
	
	/**
	 * Schreibt <i>msg</i> mit dem Level DEBUG in die Logdatei.
	 * @param msg
	 */
	public static void debug(String msg) {
		write(DEBUG, msg);
	}
	
	private NotesReportLogZZZ () {
		//System.out.println("Im private Konstruktor von NotesReportLogZZZ");
		//!!! FGL: Es muss der Methodenaufruf "loadKernelContext(..)" ausgefürht werden.		
	}
	
	
	//####################################################
	//### FGL Erweiterungen / Änderungen
	/** Liest das Konfigurierte LogLeEvel aus der Konfigurationsdatei aus.
	 *   Transformiert den konfigurierten String in einen integer-wert, 
	 *   wie er dann in .setLogLevel(int) übergeben werden kann.
	* @return int
	* 
	* lindhaueradmin; 22.10.2006 17:23:35
	 * @throws ExceptionZZZ 
	 */
	public static int readLogLevel() throws ExceptionZZZ{
		int iReturn = 0;
		main:{
			String stemp = objSingletonNRLog.objContext.getLog4jLevel();
			if(StringZZZ.isEmpty(stemp)) break main;
			
			iReturn = ReportLogCommonZZZ.configToReportLogConstantLevel(stemp);
		}//End main
		return iReturn;
	}
	
	/** FGL Erweiterung/Änderung: Lies den Namen des verwendeten Logs aus der Kernel-ProgramKonfiguration aus.
	 * Merke: Dieser Name wird zur statischen Erzeugung der log4j Logger-Objekts verwendet.
	* @return String
	* 
	* lindhaueradmin; 24.10.2006 09:54:49
	 * @throws ExceptionZZZ 
	 */
	public static String readLogName() throws ExceptionZZZ{
		return objSingletonNRLog.objContext.getLog4jName();
	}
	
	public static String readLog4jPathConfig() throws ExceptionZZZ{
		return objSingletonNRLog.objContext.getLog4jPathConfig();
	}
	
	public static String readLog4jFileConfig() throws ExceptionZZZ{
		return objSingletonNRLog.objContext.getLog4jFileConfig();
	}
	
	
	/** FGL Änderung/Erweiterung: Neben dem Setzen des KernelContext-Objekts in das objSingletonNRLog,
	 * werden auch die grundlegenden Informationen aus der Kernel-Config-Ini-Datei ausgelesen und gesetzt.
	* @return boolean
	* @param objContext
	* 
	* lindhaueradmin; 24.10.2006 10:08:09
	 * @throws ExceptionZZZ 
	 */
	public static boolean loadKernelContext(NotesReportContextProviderZZZ objContext, boolean bRecreateFileConfig) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			if(objContext==null){
				ExceptionZZZ ez = new ExceptionZZZ("NotesReportContextProviderZZZ", iERROR_PARAMETER_MISSING, "NotesReportLogZZZ", ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			NotesReportLogZZZ.setNotesReportContext(objContext);
			
			String stemp  = NotesReportLogZZZ.readLogName();
			objSingletonNRLog.objLogger = Logger.getLogger(stemp);
			
			int itemp = NotesReportLogZZZ.readLogLevel();
			objSingletonNRLog.currentLogLevel = itemp;
			
			stemp = NotesReportLogZZZ.readLog4jPathConfig();
			if(StringZZZ.isEmpty(stemp)) stemp = ".";
			objSingletonNRLog.sBasePath = stemp;
			
			stemp = NotesReportLogZZZ.readLog4jFileConfig();
			objSingletonNRLog.configFilename = stemp;
			
			//nun setupLog4j(sFile); ersetzen
			if (FileEasyZZZ.exists(objSingletonNRLog.sBasePath  + "\\" + objSingletonNRLog.configFilename) && bRecreateFileConfig == false) {
				System.out.println (ReflectCodeZZZ.getMethodCurrentName() + "#Loading Log4j Config from file: "+ objSingletonNRLog.sBasePath + "\\" +objSingletonNRLog.configFilename);				
			} else {
				System.out.println ("Generating NEW Log4j Default Config in file: "+ objSingletonNRLog.sBasePath  + "\\" +objSingletonNRLog.configFilename);
				boolean btemp = NotesLog4jPropertyGeneratorZZZ.createFile(objContext);	
				if(btemp==false){
					ExceptionZZZ ez = new ExceptionZZZ("unable to create log4j-properties file", iERROR_RUNTIME, objSingletonNRLog, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
			}
			stemp = objSingletonNRLog.sBasePath  + "\\" + objSingletonNRLog.configFilename;
			PropertyConfigurator.configure (stemp);
						
			bReturn = true;
		}//END main
		return bReturn;
	}
	
	/** Schreibt am Schluss die letzten Datensätze der "temporären Datei" nach Notes. Räumt auf, d.h. löscht die temprären Dateien.
	 *   Grund: Es wird am Schluss der "rollOver" Event von log4j wohl nicht mehr aufgerufen.
	 *              Ergo fehlen die Einträge, die dann noch in der letzen Datei gemacht wurden.
	 *              
	 *    Merke: Welche Datei die zuletzt geschriebene war, "weiss nur das NotesReportLogWriterZZZ-Singleton-Objekt, es sei denn dieses ist noch nie aufgerufen worden, weil der Rollover-Event nicht stattgefunden hat.
	 *    			 DIESE METHODE MACHT DAHER EINE EXPLIZITE ZUWEISUNG DER BENÖTIGTEN VARIABLEN. KANN DAHER AUCH AUFGERUFEN WERDEN, WENN DER "ROLLOVER-EVENT" DES APPENDERS NOCH NIE GELAUFEN IST.
 *                        
	* lindhaueradmin; 12.11.2006 10:08:56
	 * @throws ExceptionZZZ 
	 */
	public static void endit(boolean bRemoveAllTempFiles) throws ExceptionZZZ{
		//Merke: Das sind die gleichen Parameter wie im NotesReportContextProviderZZZ
		//           und wie im NotesLog4jPropertyGeneratorZZZ
		
		//Merke: Diese Parameter müssen extra ermittelt werden, weil es sein kann, dass der Appender noch nicht gelaufen ist, z.B. wg. zu wenigen Datensätzen, die Protokolliert wurden, wurde der Rollover-Event nicht gestartet.
		
		try{
		    if (objSingletonNRLog.objContext == null){
		    	System.out.println("Kein Contextobjekt vorhanden");
		    	ExceptionZZZ ez = new ExceptionZZZ("Kein Contextobject vorhanden", iERROR_PARAMETER_MISSING, "NotesReportLogZZZ", ReflectCodeZZZ.getMethodCurrentName());
		    	throw ez;
		    }
		    
			
			//aus dem NotesReport Context Provider
			String sClassCalling = objSingletonNRLog.objContext.getClassCalling();
			String sModuleCalling = objSingletonNRLog.objContext.getModuleCalling();
			
			String stemp1 = NotesReportContextProviderZZZ.readLog4jPathTemp(objSingletonNRLog.objContext.getKernelObject(), sModuleCalling, sClassCalling);
			String stemp2 = NotesReportContextProviderZZZ.readLog4jFileTemp(objSingletonNRLog.objContext.getKernelObject(), sModuleCalling, sClassCalling);
			String sRollingFileTempPath = stemp1 + "\\" + stemp2;
		
			String sFlagReplication = NotesReportContextProviderZZZ.readFlagReplicationString(objSingletonNRLog.objContext.getKernelObject(), sModuleCalling, sClassCalling);
				
			// aus dem NotesKernel
			KernelNotesZZZ objKernelNotes = objSingletonNRLog.objContext.getKernelNotesObject();
			Database dbLog = objKernelNotes.getDBLogCurrent();
			String sServer = dbLog.getServer();
			String sDBLogPath = dbLog.getFilePath();
		
			//Aus dem NotesContextProviders (hier über den NotesKernel zu beziehen)
			NotesContextProviderZZZ objContextNotes = objKernelNotes.getNotesContextProvider();
			String sUser = objContextNotes.getUsername();
			String sPwd = objContextNotes.getPassword();
		
			//Merke: Hier müssen alle Werte übergeben werden, falls der "Rolling Appender"-Mechanismus, z.B. aufgrund zu wenigen Protokollierungen noch nicht angesprungen ist.
			NotesReportLogWriterZZZ.endit(sServer, sDBLogPath, sUser, sPwd, sRollingFileTempPath, sFlagReplication, bRemoveAllTempFiles);
		
		}catch(NotesException ne){
			System.out.println("A NotesException happend: " + ne.text);
			ExceptionZZZ ez = new ExceptionZZZ(ne.text, iERROR_RUNTIME, "NotesReportLogZZZ", ReflectCodeZZZ.getMethodCurrentName());
			throw ez;
		}
	}

	//### Getter / Setter
	public static void setNotesReportContext(NotesReportContextProviderZZZ objContext) {
		objSingletonNRLog.objContext = objContext;
	}
	public KernelReportContextProviderZZZ getKernelContext(){
		return this.objContext;
	}
}
