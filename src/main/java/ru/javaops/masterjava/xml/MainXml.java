package ru.javaops.masterjava.xml;

import com.google.common.io.Resources;
import ru.javaops.masterjava.xml.schema.ObjectFactory;
import ru.javaops.masterjava.xml.schema.Payload;
import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.Schemas;

import javax.xml.bind.JAXBException;
import java.io.IOException;

public class MainXml {
    private static final JaxbParser JAXB_PARSER = new JaxbParser(ObjectFactory.class);

    static {
        JAXB_PARSER.setSchema(Schemas.ofClasspath("payload.xsd"));
    }

    public static void main(String[] args) {
        //JAXB
        Payload payload = null;
        try {
            payload = JAXB_PARSER.unmarshal(
                    Resources.getResource("payload.xml").openStream());
        } catch (JAXBException | IOException e) {
            System.out.println("Error occurred while opening XML file");
            System.exit(-1);
        }
        payload.getUsers().getUser().forEach(user -> System.out.println(user.getEmail() + " " + user.getFullName()));
    }
}
