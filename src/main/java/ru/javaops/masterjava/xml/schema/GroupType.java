
package ru.javaops.masterjava.xml.schema;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for groupType complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType name="groupType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute name="project" use="required" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
 *       &lt;attribute name="type" use="required" type="{http://javaops.ru}statusType" />
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "groupType", namespace = "http://javaops.ru", propOrder = {
        "value"
})
public class GroupType {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "project", required = true)
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    protected Object project;
    @XmlAttribute(name = "type", required = true)
    protected StatusType type;
    @XmlAttribute(name = "id", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;

    /**
     * Gets the value of the value property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the project property.
     *
     * @return possible object is
     * {@link Object }
     */
    public Object getProject() {
        return project;
    }

    /**
     * Sets the value of the project property.
     *
     * @param value allowed object is
     *              {@link Object }
     */
    public void setProject(Object value) {
        this.project = value;
    }

    /**
     * Gets the value of the type property.
     *
     * @return possible object is
     * {@link StatusType }
     */
    public StatusType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     *
     * @param value allowed object is
     *              {@link StatusType }
     */
    public void setType(StatusType value) {
        this.type = value;
    }

    /**
     * Gets the value of the id property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setId(String value) {
        this.id = value;
    }

}
