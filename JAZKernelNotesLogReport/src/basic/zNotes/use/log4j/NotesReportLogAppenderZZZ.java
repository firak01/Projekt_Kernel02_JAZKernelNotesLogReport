package basic.zNotes.use.log4j;

/*
 * Die Sourcecodes, die diesem Buch als Beispiele beiliegen, sind
 * Copyright (c) 2006 - Thomas Ekert. Alle Rechte vorbehalten.
 * 
 * Trotz sorgfältiger Kontrolle sind Fehler in Softwareprodukten nie vollständig auszuschließen.
 * Die Sourcodes werden in Ihrem Originalzustand ausgeliefert.
 * Ansprüche auf Anpassung, Weiterentwicklung, Fehlerbehebung, Support
 * oder sonstige wie auch immer gearteten Leistungen oder Haftung sind ausgeschlossen.
 * Sie dürfen kommerziell genutzt, weiterverarbeitet oder weitervertrieben werden.
 * Voraussetzung hierfür ist, dass für jeden beteiligten Entwickler, jeweils mindestens
 * ein Exemplar dieses Buches in seiner aktuellen Version als gekauftes Exemplar vorliegt.
 */
/*
 * Logger Appender für log4j
 * Ist wie der standard RollOverFileAppender aufgebaut und erweitert diesen
 * Das jeweils aktuellste File wird, wenn es nachrückt, in die Datenbank, die durch die Konfiguration bestimmt wird geschoben.
 * Beispiel:
 * 
#################################################################
### Definition for Notes logger - writes to notes db
#################################################################

log4j.rootLogger=ALL, NOTESLOGGER

log4j.appender.NOTESLOGGER=djbuch.kapitel_19.NotesLogAppender
log4j.appender.NOTESLOGGER.layout=org.apache.log4j.PatternLayout

# Pattern to output the caller's file name and line number.
log4j.appender.NOTESLOGGER.layout.ConversionPattern=%d [%t] %-5p %c - %m%n

log4j.appender.NOTESLOGGER.File=C:/Lotus/Domino/java/pwgbnotes.log

#Max Size of temporary files. Dont choose to large value, because these files will be converted to notes documents.
log4j.appender.NOTESLOGGER.MaxFileSize=100KB
# Keep 10 backup files
log4j.appender.NOTESLOGGER.MaxBackupIndex=10

# Name of target Database
log4j.appender.NOTESLOGGER.notesDb=djbuch/djbuch.nsf

# Domino Server Name (z.B. ServerName/Organisation)
log4j.appender.NOTESLOGGER.dominoServer=Java/DJBUCH

# Password for local or IIOP Session - can be left empty on server
#log4j.appender.NOTESLOGGER.password=secret

#
# Optional - Fill iiop Variables to establishe IIOP Connection instead of local connection
# When using this: Be carefull to be shure, that the iiopServer Domain is the same Server as dominoServer
# Otherwise you will get security exceptions
#
# IIOP User
#log4j.appender.NOTESLOGGER.iiopUser=user

# Domino IIOP Server Domainname (z.B. my.Server.com)
#log4j.appender.NOTESLOGGER.iiopServer=127.0.0.1
 */
import java.io.File;
import java.io.IOException;

import org.apache.log4j.helpers.CountingQuietWriter;
import org.apache.log4j.helpers.LogLog;

/**
 * @author Thomas Ekert
 *
 */
public class NotesReportLogAppenderZZZ extends org.apache.log4j.RollingFileAppender {
	public NotesReportLogAppenderZZZ(){
		//System.out.println("Bin im Konstruktor von meinem Appender");
	}
	public void rollOver() {
		//System.out.println("Bin im rollOver");
		
	
		LogLog.debug("rolling over count=" + ((CountingQuietWriter) qw).getCount());
		LogLog.debug("maxBackupIndex=" + maxBackupIndex);
		if (maxBackupIndex >= 0) {
			File file = new File(fileName + '.' + maxBackupIndex);
			if (file.exists()) {
				file.delete();
			}
			File target = null;
			for (int i = maxBackupIndex - 1; i >= 1; i--) {
				file = new File(fileName + "." + i);
				if (file.exists()) {
					target = new File(fileName + '.' + (i + 1));
					LogLog.debug("Renaming file " + file + " to " + target);
					file.renameTo(target);
				}
			}

			target = new File(fileName + "." + 1);
			closeFile();
			file = new File(fileName);
			NotesReportLogWriterZZZ.writeToDomino(NotesServer, NotesDBLog, NotesUser, NotesPassword, file.getAbsolutePath(), flagReplication);
			LogLog.debug("Renaming file " + file + " to " + target);
			file.renameTo(target);
		} try {
			setFile(fileName, false, bufferedIO, bufferSize);
		} catch (IOException e) {
			LogLog.error("setFile(" + fileName + ", false) call failed.", e);
		}
	}//END rollover()
	
	public static final int NUM_32K = 32768;
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//Merke: Diese Variablen sind über die Konfigurationsdatei von log4j zu erreichen
	//protected String dominoServer = "unknown";
	//protected String notesDb = "unknown";
	//protected String iiopServer = null;
	//protected String password = null;
	//protected String iiopUser = null;
	//protected String replication = "YES";

	 
	//FGL Überarbeitete Variablen, diese müssen auch in der Konfigurationsdatei als Properties der AppenderKlasse auftauchen.
	//Merke: Die Variable filename kommt schon aus log4j.RollingFileAppender, von der NotesReportLogZZZ erbt.
	//Merke: Die set-MEthoden sind notwendig, damit log4j auf die Properties zugreifen kann
	protected String NotesServer = "";
	protected String NotesDBLog = "";
	protected String NotesPassword="";
	protected String NotesUser="";
	protected String flagReplication="YES";
	
	
	//!!! DIESE set-Methoden sind wichtig, damit Log4j die Properties aus der Konfiguration lesen kann
	public void setNotesDBLog (String dbName) {
		NotesDBLog = dbName;
	}
	
	public void setflagReplication (String repl) {
		flagReplication = repl;
	}
	
	public void setNotesServer (String server) {
		NotesServer = server;
	}
	
	public void setNotesUser (String user) {
		NotesUser = user;
	}
	
	public void setNotesPassword (String pwd) {
		NotesPassword = pwd;
	}
}//END class

