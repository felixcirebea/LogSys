package ro.siit.logsys;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import ro.siit.logsys.exception.InputFileException;
import ro.siit.logsys.service.implementation.CsvParserImpl;

@SpringBootApplication
@EnableAsync
public class LogsysApplication {

	public static void main(String[] args) throws InputFileException {
		ApplicationContext run = SpringApplication.run(LogsysApplication.class, args);
		CsvParserImpl csvParser = run.getBean(CsvParserImpl.class);
		csvParser.run();
	}
}
