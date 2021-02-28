package telran.logs.bugs.api;

public interface BugsReporterApi {
	 String BUGS_PROGRAMMERS =  "/bugs/programmers";
	String BUGS_OPEN = "/bugs/open"; 
	String BUGS_OPEN_ASSIGN = "/bugs/open/assign" ;
	String BUGS_ASSIGN = "/bugs/assign" ;
	String PROGRAMMER_ID = "programmer_id";
	String BUGS_PROGRAMMERS_COUNT = "/bugs/programmers/count";
	String BUGS_ARTIFACTS = "/bugs/artifacts";
	String BUGS_CLOSE ="/bugs/close";
	String BUGS_UNCLOSED = "/bugs/unclosed";
	String N_DAYS = "n_days";
	String BUGS_PROGRAMMERS_MOST = "/bugs/programmers/most";
	String BUGS_PROGRAMMERS_LEAST = "/bugs/programmers/least";
	String N_PROGRAMMERS = "N_PROGRAMMERS";
	String BUGS_SERIOUSNESS_COUNT = "/bugs/seriousness/count";
	String BUGS_SERIOUSNESS_MOST = "/bugs/seriousness/most";
	String N_TYPES = "n_types";
}
