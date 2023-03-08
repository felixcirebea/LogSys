package ro.siit.logsys;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import ro.siit.logsys.service.implementation.CsvParserImpl;

@SpringBootApplication
public class LogsysApplication {

	public static void main(String[] args) {
		ApplicationContext run = SpringApplication.run(LogsysApplication.class, args);
		CsvParserImpl csvParser = run.getBean(CsvParserImpl.class);
		csvParser.run();
	}

}
