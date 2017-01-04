package basic.zNotes.use.log4j;

/*
 * Die Sourcecodes, die diesem Buch als Beispiele beiliegen, sind
 * Copyright (c) 2006 - Thomas Ekert. Alle Rechte vorbehalten.
 * 
 * Trotz sorgf�ltiger Kontrolle sind Fehler in Softwareprodukten nie vollst�ndig auszuschlie�en.
 * Die Sourcodes werden in Ihrem Originalzustand ausgeliefert.
 * Anspr�che auf Anpassung, Weiterentwicklung, Fehlerbehebung, Support
 * oder sonstige wie auch immer gearteten Leistungen oder Haftung sind ausgeschlossen.
 * Sie d�rfen kommerziell genutzt, weiterverarbeitet oder weitervertrieben werden.
 * Voraussetzung hierf�r ist, dass f�r jeden beteiligten Entwickler, jeweils mindestens
 * ein Exemplar dieses Buches in seiner aktuellen Version als gekauftes Exemplar vorliegt.
 */
//ESCA*JAVA0267
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import org.apache.log4j.helpers.LogLog;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IConstantZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.log.KernelReportContextProviderZZZ;
import basic.zBasic.util.log.ReportLogCommonZZZ;
import basic.zBasic.util.log.ReportLogZZZ;
import basic.zNotes.basic.GC;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.NotesFactory;
import lotus.domino.RichTextItem;
import lotus.domino.RichTextStyle;
import lotus.domino.Session;

/**Schreibt in die spezifizierte Notesdatenbank den Inhalt eines LogFiles in ein neues eigenes Dokument.
 *  Wird vom NotesReportLogAppenderZZZ aufgerufen.
 *  
 *     !!! Hier wird keine neue Notessession erzeugt, da ich davon ausgehen muss, dass der NotesContextProviderZZZ diese schon erzeugt hat.
 *            Diese Klasse darf deshalb auch nicht NotesThread erweitern.
 *            
 */
public class NotesReportLogWriterZZZ implements IConstantZZZ {
	private static NotesReportLogWriterZZZ objSingletonNRLogWriter= new NotesReportLogWriterZZZ();
	//private KernelContextProviderZZZ objContext = null;
	
	private String sServer;
	private String sDBLogPath;
	private String sUser;
	private String sPwd;
	private String sFlagReplication;
	private String sRollingFileTempPath;
	
	private NotesReportLogWriterZZZ(){		
		//System.out.println("Bin im parameterlosen Konstruktor von meinem NotesReportLogWriterZZZ");
	}
	

	
	/**
	 * Schreibt die mit <b>fileName</b> bezeichnete Datei in ein NotesDocument und
	 * hinterlegt zus�tzlich Informationen �ber diesen Vorgang (Servername, Datum).
	 * FGL: Merke, das wird von einem Rolling File Appender des log4j-Packages verwendet. Darum wird hier kein zu schreibender String direkt �bergeben,
	 *        sondern der Filename des Log-Files.
	 * 
	 * 
	 * @param dServer - Domino Server, auf dem sich die DB befindet, in die geschrieben werden soll.
	 * @param db - Datenbank, in die geschrieben werden soll.
	 * @param server - DIIOP Server, mit dem (optional) die Verbindung aufgebaut werden soll - darf
	 * null sein, dann wird eine lokale Session aufgebaut
	 * @param user - DIIOP User, �ber den die (optionale) IIOP Verbindung aufgebaut wird.
	 * @param pwd - Passwort f�r lokale, bzw. IIOP Session
	 * @param fileName - Dateiname der zu loggenden Datei.
	 * @param replication - Zus�tzlich wird ein Feld F_Replication=<b>replication</b> angelegt.
	 * Dies erleichtert eine Unterscheidung, welche Dokumente der Datenbank repliziert werden sollen.
	 */
public static synchronized void writeToDomino (String sServer, String sDBLogPath, String sUser, String sPwd, String sRollingFileTempPath, String sFlagReplication) {
		//Merke: Dazu muss der Writer mit einem parameterlosen Konstruktor schon erstellt worden sein als Variable.
		objSingletonNRLogWriter.sServer=sServer;
		objSingletonNRLogWriter.sDBLogPath=sDBLogPath;
		objSingletonNRLogWriter.sUser=sUser;
		objSingletonNRLogWriter.sPwd=sPwd;
		objSingletonNRLogWriter.sRollingFileTempPath=sRollingFileTempPath;
		objSingletonNRLogWriter.sFlagReplication=sFlagReplication;
		
		objSingletonNRLogWriter.write();
	}

/** Schreibt am Schluss die letzten Datens�tze der "tempor�ren Datei" nach Notes. R�umt auf, d.h. l�scht die tempr�ren Dateien.
 *   Grund: Es wird am Schluss der "rollOver" Event von log4j wohl nicht mehr aufgerufen.
 *              Ergo fehlen die Eintr�ge, die dann noch in der letzen Datei gemacht wurden.
 *              
 *              DIESE METHODE MACHT EINE EXPLIZITE ZUWEISUNG DER BEN�TIGTEN VARIABLEN. KANN DAHER AUCH AUFGERUFEN WERDEN, WENN DER "ROLLOVER-EVENT" DES APPENDERS NOCH NIE GELAUFEN IST.
 *              
* Merke: Bei static-Objekten wird wohl ein "Finalize" als Destruktor nicht aufgerufen.
*           Darum muss endit(...) explizit aufgerufen werden (was normalerweise �ber den Umweg �ber NotesReportLogZZZ.endit() geschieht.*           
* 
* lindhaueradmin; 12.11.2006 10:08:56
 */
public static synchronized void endit(String sServer, String sDBLogPath, String sUser, String sPwd, String sRollingFileTempPath, String sFlagReplication, boolean bRemoveTempFileAll){	
	//System.out.println("Bin im endit(...) von meinem NotesReportLogWriterZZZ");
	
	objSingletonNRLogWriter.sServer=sServer;
	objSingletonNRLogWriter.sDBLogPath=sDBLogPath;
	objSingletonNRLogWriter.sUser=sUser;
	objSingletonNRLogWriter.sPwd=sPwd;
	objSingletonNRLogWriter.sRollingFileTempPath=sRollingFileTempPath;
	objSingletonNRLogWriter.sFlagReplication=sFlagReplication;
	
	objSingletonNRLogWriter.write();
	
	//da nur das Singleton-objekt den namen der letzten Datei weiss, muss das hier erfolgen. 
	//An dieser Stelle kann aber leider keine Info �ber die maximale Anzahl der tempor�ren Datein vorliegen.
	//Diese steht nur dem NotesReportLogAppenerZZZ-Objekt zur Verf�gung, das aber kein Singleton sein kann, daher nicht static ist und somit hier oder woanders nicht zur Verf�gung steht.	
	//
	if(bRemoveTempFileAll==true){
		for (int i = 999; i >= 1; i--) {
			File file = new File(objSingletonNRLogWriter.sRollingFileTempPath + "." + i);
			if (file.exists()) {
				file.delete();
			}
		}
		File file = new File(objSingletonNRLogWriter.sRollingFileTempPath);
		if(file.exists()){
			file.delete();
			if(file.exists()){
				//Das L�schen schl�gt fehl !!!
				//Warum ???
				//Auf jeden fall alternativ die Werte enfernen.
				try {
					FileWriter objFR = new FileWriter(file);
					objFR.write("");
				} catch (IOException e) {				
					e.printStackTrace();
				}
			}
		}
	}
	
}

	
/** Schreibt am Schluss die letzten Datens�tze der "tempor�ren Datei" nach Notes. R�umt auf, d.h. l�scht die tempr�ren Dateien.
 *   Grund: Es wird am Schluss der "rollOver" Event von log4j wohl nicht mehr aufgerufen.
 *              Ergo fehlen die Eintr�ge, die dann noch in der letzen Datei gemacht wurden.
 *              
* Merke: Bei static-Objekten wird wohl ein "Finalize" als Destruktor nicht aufgerufen.
*           Darum muss endit() explizit aufgerufen werden (was normalerweise �ber den Umweg �ber NotesReportLogZZZ.endit() geschieht.
* 
* Merke: Bei Verwendung dieses ENDIT, ist es zwingend notwendig, dass der NotesAppender schon mal aufgrund des "Rollover-Events" gelaufen ist.
*           Sonst sind die Variablen des Singleton n�mlich nicht initialisiert. 
* lindhaueradmin; 12.11.2006 10:08:56
 */
public static synchronized void endit(boolean bRemoveTempFileAll){
	//System.out.println("Bin im parameterlosen endit() von meinem NotesReportLogWriterZZZ");
	
	//es wird quasi immer die letzte Datei "vergessen", dies also am Schluss nachholen.
	objSingletonNRLogWriter.write();
	
	//da nur das Singleton-objekt den namen der letzten Datei weiss, muss das hier erfolgen. 
	//An dieser Stelle kann aber leider keine Info �ber die maximale Anzahl der tempor�ren Datein vorliegen.
	//Diese steht nur dem NotesReportLogAppenerZZZ-Objekt zur Verf�gung, das aber kein Singleton sein kann, daher nicht static ist und somit hier oder woanders nicht zur Verf�gung steht.	
	//
	if(bRemoveTempFileAll==true){
		for (int i = 999; i >= 1; i--) {
			File file = new File(objSingletonNRLogWriter.sRollingFileTempPath + "." + i);
			if (file.exists()) {
				file.delete();
			}
		}
		File file = new File(objSingletonNRLogWriter.sRollingFileTempPath);
		if(file.exists()){
			file.delete();
			if(file.exists()){
				//Das L�schen schl�gt fehl !!!
				//Warum ???
				//Auf jeden fall alternativ die Werte enfernen.
				try {
					FileWriter objFR = new FileWriter(file);
					objFR.write("");
				} catch (IOException e) {				
					e.printStackTrace();
				}
			}
		}
	}
}//END endit()

	private void write() {
		BufferedReader br = null;
		Session session = null;
		Database db = null;
		Document doc = null;
		RichTextItem rti = null;
		RichTextStyle rts = null;
		try {
			if(StringZZZ.isEmpty(sDBLogPath)){
				ExceptionZZZ ez = new ExceptionZZZ("sDBLogPath", iERROR_PROPERTY_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			//Merke: Hierbei gehe ich davon aus, das zumindest ein statischer Thread durch den "NotesContextProviderZZZ" erzeugt worden ist.
			if(StringZZZ.isEmpty(sPwd) & StringZZZ.isEmpty(sUser) ){					
				session = NotesFactory.createSession();
				db = session.getDatabase("",sDBLogPath);
			}else{
				if(StringZZZ.isEmpty(sServer)){
					//Hierbei gehe ich davon aus, das in der Notes.ini der KeyFileName durch den "NotesContextProviderZZZ" auf den Usernamen ge�ndert worden ist.
					session = NotesFactory.createSession(sPwd);
					db = session.getDatabase("",sDBLogPath);
				}else{
					session = NotesFactory.createSession(sServer, sUser, sPwd);
					if(sServer.equals("-") | sServer.equalsIgnoreCase("local") | sServer.equalsIgnoreCase("unknown")){
						db = session.getDatabase("",sDBLogPath);
					}else{
						db = session.getDatabase(sServer,sDBLogPath);
					}
				}							
			}
						
			doc = db.createDocument();
			doc.replaceItemValue("Form", "FO_LogFileJava");
			
			String sServer = db.getServer();
			if(StringZZZ.isEmpty(sServer)){
				sServer = session.getCommonUserName();
				if(StringZZZ.isEmpty(sServer)) sServer = "local session, no username";
			}
			doc.replaceItemValue("F_Servername",sServer);
			doc.replaceItemValue("F_Replicate", sFlagReplication);
			doc.replaceItemValue("F_NotesLogWriter", "1");
			doc.replaceItemValue("F_Created", session.createDateTime(new Date()));
			br = new BufferedReader (new FileReader (sRollingFileTempPath));
			String tmp = br.readLine();
			rti = doc.createRichTextItem("F_Events");
			rts = session.createRichTextStyle();
			rts.setFont(RichTextStyle.FONT_COURIER);
			rts.setFontSize(8);
			rti.appendStyle(rts);
			boolean errorfound=false;
			while (tmp!=null) {
				if (!errorfound && tmp.indexOf(ReportLogCommonZZZ.logSymbol(ReportLogCommonZZZ.ERROR)) != -1) {
					doc.replaceItemValue("F_Error","1");
					errorfound=true;//spart sp�tere indexOf Evaluierungen...
				}
				rti.appendText(tmp);
				rti.addNewLine(1);
				tmp = br.readLine();
			}
			doc.save (true,false);
		} catch (FileNotFoundException e) {
			System.err.println ("Fatal: " +  e.toString());
		} catch (IOException e) {
			System.err.println ("Fatal: " +  e.toString());
		} catch (NotesException e) {
			System.err.println ("Fatal: " +  e.toString());
		} catch (ExceptionZZZ ez){
			System.err.println("ExceptionZZZ: " + ez.getDetailAllLast());
		}finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				// do nothing
			}
			GC.recycle (rti);
			GC.recycle (doc);
			GC.recycle (db);
			GC.recycle (session);
		}
	}
}

