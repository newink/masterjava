package ru.javaops.masterjava.upload;

import ru.javaops.masterjava.model.User;
import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class UserProcessor {
    private JaxbParser parser = new JaxbParser(User.class);  // model.User

    public List<User> process(final InputStream is) throws XMLStreamException, JAXBException {
        final StaxStreamProcessor processor = new StaxStreamProcessor(is);
        List<User> users = new ArrayList<>();

        while (processor.doUntil(XMLEvent.START_ELEMENT, "User")) {
            User user = parser.unmarshal(processor.getReader(), User.class);
            users.add(user);
        }
        return users;
    }
}
