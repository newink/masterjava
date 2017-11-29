package ru.javaops.masterjava.upload;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.CityDao;
import ru.javaops.masterjava.persist.model.City;
import ru.javaops.masterjava.xml.schema.CityType;
import ru.javaops.masterjava.xml.schema.ObjectFactory;
import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CityProcessor {
    private static final JaxbParser jaxbParser = new JaxbParser(ObjectFactory.class);
    private static CityDao cityDao = DBIProvider.getDao(CityDao.class);
    private static CityCache cityCache = CityCache.getInstance();

    public List<String> process(InputStream is) throws XMLStreamException, JAXBException {
        val unmarshaller = jaxbParser.createUnmarshaller();
        val processor = new StaxStreamProcessor(is);

        List<String> failedCitiesNames = new ArrayList<>();

        while (processor.doUntil(XMLEvent.START_ELEMENT, "City")) {
            CityType city = unmarshaller.unmarshal(processor.getReader(), CityType.class);
            City possibleInserted = cityDao.insert(new City(city.getId(), city.getValue()));
            if (possibleInserted.getId() == 0) {
                failedCitiesNames.add(possibleInserted.getName());
            } else {
                cityCache.put(possibleInserted.getMnemonic(), possibleInserted);
            }
        }
        return failedCitiesNames;
    }
}
