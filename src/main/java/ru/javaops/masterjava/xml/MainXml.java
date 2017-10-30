package ru.javaops.masterjava.xml;

import com.google.common.io.Resources;
import ru.javaops.masterjava.xml.schema.*;
import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.Schemas;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;

public class MainXml {
    private static final JaxbParser JAXB_PARSER = new JaxbParser(ObjectFactory.class);

    static {
        JAXB_PARSER.setSchema(Schemas.ofClasspath("payload.xsd"));
    }

    public static void main(String[] args) {
        System.out.println("\n----------JAXB----------");
        Payload payload = null;
        try {
            payload = JAXB_PARSER.unmarshal(
                    Resources.getResource("payload.xml").openStream());
        } catch (JAXBException | IOException e) {
            System.out.println("Error occurred while opening XML file");
            System.exit(-1);
        }
        for (User user : payload.getUsers().getUser()) {
            for (GroupList groupList : user.getGroup()) {
                GroupType group = (GroupType) groupList.getId();
                ProjectType projectType = (ProjectType) group.getProject();
                if (projectType.getName().equals(args[0])) {
                    System.out.printf("%s %s - Group: %s , Project: %s\n", user.getEmail(), user.getFullName(), group.getValue(), projectType.getValue());
                }
            }
        }


        System.out.println("\n----------StaX----------");
        try (StaxStreamProcessor processor =
                     new StaxStreamProcessor(Resources.getResource("payload.xml").openStream())) {
            XMLStreamReader reader = processor.getReader();
            while (reader.hasNext()) {
                int event = reader.next();
                if (event == XMLEvent.START_ELEMENT) {
                    if ("User".equals(reader.getLocalName())) {
                        String email = reader.getAttributeValue(null, "email");
                        reader.nextTag();
                        String name = reader.getElementText();
                        System.out.println(email + " " + name);
                    }
                }
            }
        }  catch (IOException | XMLStreamException e) {
            System.out.println("Error occurred while opening XML file: " + e);
            System.exit(-1);
        }

    }
}
