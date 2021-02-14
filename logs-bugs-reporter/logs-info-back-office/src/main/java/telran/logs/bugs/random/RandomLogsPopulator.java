package telran.logs.bugs.random;

import java.util.*;

import javax.annotation.PostConstruct;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import telran.logs.bugs.dto.LogDto;
import telran.logs.bugs.mongo.doc.LogDoc;
import telran.logs.bugs.repo.LogRepository;


@Component
public class RandomLogsPopulator {
	static Logger LOG = LoggerFactory.getLogger(RandomLogsPopulator.class);
	@Value("${app-population-enable:false}")
	boolean flPopulation;
	@Value("${app-number-logs:0}")
	int nLogs;
	@Autowired
RandomLogs randomLogs;
	@Autowired
	LogRepository logRepository;
	@PostConstruct
	void populatingDb() {
		if(flPopulation) {
			LOG.info("population started...");
			ArrayList<LogDoc> logs = getRandomLogs();
			logRepository.saveAll(logs).buffer().blockFirst();
			LOG.info("saved {} documents", logs.size());
		}
	}
	private ArrayList<LogDoc> getRandomLogs() {
		ArrayList<LogDoc> res = new ArrayList<>();
		for(int i = 0; i < nLogs; i++) {
			res.add(new LogDoc(randomLogs.createRandomLog()));
		}
		return res;
	}
}
