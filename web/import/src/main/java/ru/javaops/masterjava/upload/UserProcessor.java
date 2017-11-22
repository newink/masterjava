package ru.javaops.masterjava.upload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.UserDao;
import ru.javaops.masterjava.persist.model.User;
import ru.javaops.masterjava.persist.model.UserFlag;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class UserProcessor {
    private static final Logger log = LoggerFactory.getLogger(UserProcessor.class);
    private static final int THREAD_COUNT = 4;

    private static final UserDao userDao = DBIProvider.getDao(UserDao.class);
    private ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);

    public List<FailedEmail> process(final InputStream is, int chunkSize) throws XMLStreamException, JAXBException {
        final StaxStreamProcessor processor = new StaxStreamProcessor(is);
        List<User> chunk = new ArrayList<>(chunkSize);
        Map<String, Future<List<String>>> chunkFutures = new LinkedHashMap<>();

        while (processor.doUntil(XMLEvent.START_ELEMENT, "User")) {
            final String email = processor.getAttribute("email");
            final UserFlag userFlag = UserFlag.valueOf(processor.getAttribute("flag"));
            final String fullName = processor.getReader().getElementText();
            final User user = new User(fullName, email, userFlag);
            chunk.add(user);
            if (chunk.size() == chunkSize) {
                addChunkFutures(chunkFutures, chunk);
            }
        }
        if (!chunk.isEmpty()) {
            addChunkFutures(chunkFutures, chunk);
        }

        List<String> alreadyPresents = new ArrayList<>();
        List<FailedEmail> failedEmails = new ArrayList<>();
        chunkFutures.forEach((emailRange, listFuture) -> {
            try {
                List<String> failedEmailsStrings = listFuture.get();
                log.info("{} successfully executed with already present: {}", emailRange, failedEmailsStrings);
                alreadyPresents.addAll(failedEmailsStrings);
            } catch (InterruptedException | ExecutionException e) {
                failedEmails.add(new FailedEmail(emailRange, e.getMessage()));
                log.error("Error while processing chunk {}, reason: {}", emailRange, e);
            }
        });
        if (!alreadyPresents.isEmpty()) {
            failedEmails.add(new FailedEmail(alreadyPresents.toString(), "already presents"));
        }
        return failedEmails;
    }

    private void addChunkFutures(Map<String, Future<List<String>>> chunkFutures, List<User> chunk) {
        String key = String.format("[%s-%s]", chunk.get(0).getEmail(), chunk.get(chunk.size() - 1).getEmail());
        Future<List<String>> future = executorService.submit(() -> userDao.batchInsertAndGetConflictEmails(chunk));
        chunkFutures.put(key, future);
        log.info("Submited chunk: {}", key);
    }


    public static class FailedEmail {
        public String emailOrRange;
        public String reason;

        public FailedEmail(String emailOrRange, String reason) {
            this.emailOrRange = emailOrRange;
            this.reason = reason;
        }

        @Override
        public String toString() {
            return emailOrRange + " - " + reason;
        }
    }
}
