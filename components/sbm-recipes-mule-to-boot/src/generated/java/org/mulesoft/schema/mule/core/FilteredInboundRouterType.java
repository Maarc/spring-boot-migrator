
package org.mulesoft.schema.mule.core;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.mulesoft.schema.mule.jms.PropertyFilter;


/**
 * <p>Java class for filteredInboundRouterType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="filteredInboundRouterType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.mulesoft.org/schema/mule/core}abstractInboundRouterType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{http://www.mulesoft.org/schema/mule/core}abstract-filter" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;anyAttribute processContents='lax' namespace='##other'/&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "filteredInboundRouterType", propOrder = {
    "abstractFilter"
})
@XmlSeeAlso({
    WireTapRouterType.class,
    BaseAggregatorRouterType.class
})
public class FilteredInboundRouterType
    extends AbstractInboundRouterType
{

    @XmlElementRef(name = "abstract-filter", namespace = "http://www.mulesoft.org/schema/mule/core", type = JAXBElement.class, required = false)
    protected JAXBElement<? extends CommonFilterType> abstractFilter;

    /**
     * Gets the value of the abstractFilter property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link CollectionFilterType }{@code >}
     *     {@link JAXBElement }{@code <}{@link PropertyFilter }{@code >}
     *     {@link JAXBElement }{@code <}{@link ExpressionFilterType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CollectionFilterType }{@code >}
     *     {@link JAXBElement }{@code <}{@link UnitaryFilterType }{@code >}
     *     {@link JAXBElement }{@code <}{@link RefFilterType }{@code >}
     *     {@link JAXBElement }{@code <}{@link WildcardFilterType }{@code >}
     *     {@link JAXBElement }{@code <}{@link RegexFilterType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ScopedPropertyFilterType }{@code >}
     *     {@link JAXBElement }{@code <}{@link TypeFilterType }{@code >}
     *     {@link JAXBElement }{@code <}{@link WildcardFilterType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CustomFilterType }{@code >}
     *     {@link JAXBElement }{@code <}{@link TypeFilterType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CommonFilterType }{@code >}
     *     
     */
    public JAXBElement<? extends CommonFilterType> getAbstractFilter() {
        return abstractFilter;
    }

    /**
     * Sets the value of the abstractFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link CollectionFilterType }{@code >}
     *     {@link JAXBElement }{@code <}{@link PropertyFilter }{@code >}
     *     {@link JAXBElement }{@code <}{@link ExpressionFilterType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CollectionFilterType }{@code >}
     *     {@link JAXBElement }{@code <}{@link UnitaryFilterType }{@code >}
     *     {@link JAXBElement }{@code <}{@link RefFilterType }{@code >}
     *     {@link JAXBElement }{@code <}{@link WildcardFilterType }{@code >}
     *     {@link JAXBElement }{@code <}{@link RegexFilterType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ScopedPropertyFilterType }{@code >}
     *     {@link JAXBElement }{@code <}{@link TypeFilterType }{@code >}
     *     {@link JAXBElement }{@code <}{@link WildcardFilterType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CustomFilterType }{@code >}
     *     {@link JAXBElement }{@code <}{@link TypeFilterType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CommonFilterType }{@code >}
     *     
     */
    public void setAbstractFilter(JAXBElement<? extends CommonFilterType> value) {
        this.abstractFilter = value;
    }

}
