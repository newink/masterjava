package ru.javaops.masterjava.upload;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.GroupDao;
import ru.javaops.masterjava.persist.dao.ProjectDao;
import ru.javaops.masterjava.persist.model.Group;
import ru.javaops.masterjava.persist.model.Project;
import ru.javaops.masterjava.persist.model.type.GroupType;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.stream.XMLStreamException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class ProjectsGroupsProcessor {
    private final GroupDao groupDao = DBIProvider.getDao(GroupDao.class);
    private final ProjectDao projectDao = DBIProvider.getDao(ProjectDao.class);

    public Map<String, Group> process(StaxStreamProcessor processor) throws XMLStreamException {
        Map<String, Group> groupMap = groupDao.getAsMap();
        Map<String, Project> projectMap = projectDao.getAsMap();

        List<Group> newGroups = new ArrayList<>();

        while (processor.startElement("Project", "Projects")) {
            val projectName = processor.getAttribute("name");
            val description = processor.getElementValue("description");
            if (!projectMap.containsKey(projectName)) {
                Project project = new Project(projectName, description);
                projectDao.insert(project);
                projectMap.put(projectName, project);
            }

            val projectId = projectMap.get("name").getId();

            while (processor.startElement("Group", "Project")) {
                val groupName = processor.getAttribute("groupName");
                val type = GroupType.valueOf(processor.getAttribute("type"));

                if (!groupMap.containsKey(groupName)) {
                    newGroups.add(new Group(groupName, type, projectId));
                }
            }
            log.info("Insert batch " + newGroups);
            groupDao.insertBatch(newGroups);
        }


        return groupDao.getAsMap();
    }
}
