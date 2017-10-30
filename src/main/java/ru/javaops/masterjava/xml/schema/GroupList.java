
package ru.javaops.masterjava.xml.schema;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for groupList complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType name="groupList">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "groupList", namespace = "http://javaops.ru", propOrder = {
        "value"
})
public class GroupList {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "id", required = true)
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    protected Object id;

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
     * Gets the value of the id property.
     *
     * @return possible object is
     * {@link Object }
     */
    public Object getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     *
     * @param value allowed object is
     *              {@link Object }
     */
    public void setId(Object value) {
        this.id = value;
    }

}
