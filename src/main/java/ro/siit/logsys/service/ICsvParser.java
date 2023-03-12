package ro.siit.logsys.service;

import ro.siit.logsys.exception.EntryNotFoundException;
import ro.siit.logsys.exception.InputFileException;

public interface ICsvParser {
    void run() throws InputFileException, EntryNotFoundException;
}
