package basic.zNotes.use.log4j;

import lotus.domino.Database;
import lotus.domino.NotesException;
import basic.zKernel.KernelZZZ;
import custom.zNotes.kernel.KernelNotesLogZZZ;
import custom.zNotes.kernel.KernelNotesZZZ;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.log.KernelReportContextProviderZZZ;
import basic.zNotes.kernel.IKernelNotesZZZ;
import basic.zNotes.kernel.NotesContextProviderZZZ;

/**Klasse erweitert den KernelReportContextProviderZZZ um die Konfigurationen, die für das Schreiben in NotesDatenbanken notwendig sind.
 * Die für alle Notes-Contexte notwendigen Informationen werden aus dem NotesContextProvider geholt.
 * @author lindhaueradmin
 *
 */
public class NotesReportContextProviderZZZ extends KernelReportContextProviderZZZ implements IKernelNotesZZZ{
	private NotesContextProviderZZZ objContextNotes;
	private KernelNotesZZZ objKernelNotes;
	
	private String sLog4jPathTemp=null; //Der Pfad der "cachenden" Log4j - Dateien. Wert für: //für "log4j.appender.NOTESLOGGER.File=@@FILENAMENOTES@@\n"
	private String sLog4jFileTemp=null;
	//private String sLog4jLevel=null;     //Der Wert für @@LEVEL@@ in: "log4j.rootLogger=@@LEVEL@@, ROLL, NOTESLOGGER\n"
	//private String sLog4jDBApplicationLog=null; // für log4j.appender.NOTESLOGGER.notesDb=@@DBApplicationLog@@\n"
	//private String sLog4jServer=null; //                für log4j.appender.NOTESLOGGER.dominoServer=@@DBServer@@n"
    private String sFlagReplication=null; //            für log4j.appender.NOTESLOGGER.flagReplication=@@FlagReplication@@
	
	public NotesReportContextProviderZZZ(NotesContextProviderZZZ objContextNotes, KernelNotesZZZ objKernelNotes) throws ExceptionZZZ {
		
		//Merke: objContextNotes.getKernelObjekt() liefert noch kein NotesKernelObjekt, sondern ein normeles KernelZZZ-Objekt
		super(objContextNotes.getKernelObject(), objContextNotes.getModuleName(), objContextNotes.getAgentName());
		this.objContextNotes = objContextNotes;	
		this.objKernelNotes = objKernelNotes;		
	}
	
//	################################		
	
	
	// FGL Erweiterung: Parmeter aus der ini Auslesen. Merke: Dies sind Parmeter, die zusätzlich zum NotesContextProviderZZZ definiert wurden
//	##################### GETTER /SETTER

		/** Der Dateipfad für die log4jProtokolle, die dann später gesammelt in ein NotesDokument geschrieben werden sollen. Wird als Parameter aud der Ini-Datei ausgelesen
		* @return String
		* lindhaueradmin; 03.11.2006 08:24:14
		 * @throws ExceptionZZZ 
		 */
		public String getLog4jPathTemp() throws ExceptionZZZ {
			if(this.sLog4jPathTemp==null){
				this.sLog4jPathTemp = NotesReportContextProviderZZZ.readLog4jPathTemp(this.getKernelObject(), this.getModuleCalling(), this.getClassCalling());
			}
			return this.sLog4jPathTemp;
		}
		
		public static String readLog4jPathTemp(KernelZZZ objKernel, String sModuleCalling, String sClassCalling) throws ExceptionZZZ{
			if(StringZZZ.isEmpty(sModuleCalling)){
				ExceptionZZZ ez = new ExceptionZZZ("sModuleCalling", iERROR_PARAMETER_MISSING, "NotesReportContextProviderZZZ", ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			if(StringZZZ.isEmpty(sClassCalling)){
				ExceptionZZZ ez = new ExceptionZZZ("sClassCalling", iERROR_PARAMETER_MISSING, "NotesReportContextProviderZZZ", ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			return objKernel.getParameterByProgramAlias(sModuleCalling, sClassCalling, "Log4jPathTemp");
		}
		
		/**Der Dateiname für die log4jProtokolle, die dann später gesammelt in ein NotesDokument geschrieben werden sollen. Wird als Parameter aud der Ini-Datei ausgelesen
		* @return String
		* @throws ExceptionZZZ 
		* 
		* lindhaueradmin; 06.11.2006 07:43:23
		 */
		public String getLog4jFileTemp() throws ExceptionZZZ{
			if(this.sLog4jFileTemp==null){
				this.sLog4jFileTemp = NotesReportContextProviderZZZ.readLog4jFileTemp(this.getKernelObject(), this.getModuleCalling(), this.getClassCalling());
			}
			return this.sLog4jFileTemp;
		}
		
		public static String readLog4jFileTemp(KernelZZZ objKernel, String sModuleCalling, String sClassCalling) throws ExceptionZZZ{
			if(StringZZZ.isEmpty(sModuleCalling)){
				ExceptionZZZ ez = new ExceptionZZZ("sModuleCalling", iERROR_PARAMETER_MISSING, "NotesReportContextProviderZZZ", ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			if(StringZZZ.isEmpty(sClassCalling)){
				ExceptionZZZ ez = new ExceptionZZZ("sClassCalling", iERROR_PARAMETER_MISSING, "NotesReportContextProviderZZZ", ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			return objKernel.getParameterByProgramAlias(sModuleCalling, sClassCalling, "Log4jFileTemp");
		}
		
		
		public String getFlagReplicationString() throws ExceptionZZZ {
			if(this.sFlagReplication==null){
				this.sFlagReplication = NotesReportContextProviderZZZ.readFlagReplicationString(this.getKernelObject(), this.getModuleCalling(), this.getClassCalling());
			}
			return this.sFlagReplication;
		}
		
		public static String readFlagReplicationString(KernelZZZ objKernel, String sModuleCalling, String sClassCalling) throws ExceptionZZZ{
			if(StringZZZ.isEmpty(sModuleCalling)){
				ExceptionZZZ ez = new ExceptionZZZ("sModuleCalling", iERROR_PARAMETER_MISSING, "NotesReportContextProviderZZZ", ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			if(StringZZZ.isEmpty(sClassCalling)){
				ExceptionZZZ ez = new ExceptionZZZ("sClassCalling", iERROR_PARAMETER_MISSING, "NotesReportContextProviderZZZ", ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			return objKernel.getParameterByProgramAlias(sModuleCalling, sClassCalling, "FlagReplication");
		}
		
		
		/** Der Pfad (inclusive Dateiname), der NotesDatenbank. Wird aus der NotesKernel-Konfiguration ermittelt.
		* @return
		* 
		* lindhaueradmin; 06.11.2006 09:08:13
		 * @throws ExceptionZZZ 
		 */
		public String getNotesDBApplicationLogPath() throws ExceptionZZZ {
			String sReturn = null;
			main:{
				try{
				//1. KernelObjekt und daraus den Pfad zur Log-Datenbank
				KernelNotesZZZ objKernelNotes = this.getKernelNotesObject();
				
				Database dbLog = objKernelNotes.getDBLogCurrent();
				sReturn = dbLog.getFilePath();
				}catch(NotesException ne){
					ExceptionZZZ ez = new ExceptionZZZ(ne.text, iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
			}
			return sReturn;
		}

		public String getNotesDBApplicationLogServer() throws ExceptionZZZ {
			String sReturn = null;
			main:{
				try{
				//1. KernelObjekt und daraus den Pfad zur Log-Datenbank
				KernelNotesZZZ objKernelNotes = this.getKernelNotesObject();
				
				Database dbLog = objKernelNotes.getDBLogCurrent();
				sReturn = dbLog.getServer();
				}catch(NotesException ne){
					ExceptionZZZ ez = new ExceptionZZZ(ne.text, iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
			}
			return sReturn;
		}
		


		//####################################################
		//### Getter/Setter aus den Schnittstellen
		public KernelNotesZZZ getKernelNotesObject() {
			return this.objKernelNotes;
		}

		public void setKernelNotesObject(KernelNotesZZZ objKernelNotes) {
			this.objKernelNotes = objKernelNotes;
		}

		public KernelNotesLogZZZ getKernelNotesLogObject() throws ExceptionZZZ {
			return this.objKernelNotes.getKernelNotesLogObject();
		}

		public void setKernelNotesLogObject(KernelNotesLogZZZ objKernelNotesLogIn) {
			this.objKernelNotes.setKernelNotesLogObject(objKernelNotesLogIn);
		}

		
		
}//END class
		
