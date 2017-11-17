package ru.javaops.masterjava.upload;

import one.util.streamex.StreamEx;
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
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class UserProcessor {
    private static final Logger log = LoggerFactory.getLogger(UserProcessor.class);
    private static final int THREAD_COUNT = 4;
    private UserDao userDao = DBIProvider.getDao(UserDao.class);
    private ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);

    public List<FailedEmail> process(final InputStream is, int chunkSize) throws XMLStreamException, JAXBException {
        final StaxStreamProcessor processor = new StaxStreamProcessor(is);
        List<User> chunk = new ArrayList<>(chunkSize);
        List<ChunkFuture> futures = new ArrayList<>();

        while (processor.doUntil(XMLEvent.START_ELEMENT, "User")) {
            final String email = processor.getAttribute("email");
            final UserFlag userFlag = UserFlag.valueOf(processor.getAttribute("flag"));
            final String fullName = processor.getReader().getElementText();
            final User user = new User(fullName, email, userFlag);
            chunk.add(user);
            if (chunk.size() == chunkSize) {
                futures.add(submit(chunk));
                chunk = new ArrayList<>(chunkSize);
            }
        }
        if (!chunk.isEmpty()) {
            futures.add(submit(chunk));
        }

        List<FailedEmail> failed = new ArrayList<>();
        futures.forEach(chunkFuture -> {
            try {
                failed.addAll(StreamEx.of(chunkFuture.future.get()).map(email -> new FailedEmail(email, "Email already present")).toList());
                log.info(chunkFuture.emailRange + " successfully executed!");
            } catch (InterruptedException | ExecutionException e) {
                failed.add(new FailedEmail(chunkFuture.emailRange, e.getMessage()));
                log.error("Error while processing chunk {}, reason: {}", chunkFuture.emailRange, e);
            }
        });
        return failed;
    }

    private ChunkFuture submit(List<User> chunk) {
        return new ChunkFuture(chunk, executorService.submit(() ->
                userDao.batchInsertAndGetConflictEmails(chunk)));
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

    public static class ChunkFuture {
        public String emailRange;
        Future<List<String>> future;

        public ChunkFuture(List<User> chunk, Future<List<String>> future) {
            this.future = future;
            this.emailRange = chunk.get(0).getEmail();
            if (chunk.size() > 1) {
                this.emailRange += '-' + chunk.get(chunk.size() - 1).getEmail();
            }
        }
    }
}
