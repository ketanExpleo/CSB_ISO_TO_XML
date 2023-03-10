//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.0
// See https://eclipse-ee4j.github.io/jaxb-ri
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2022.07.10 at 04:24:59 PM IST
//


package com.fss.aeps.jaxb;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for amountSplitConstant.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="amountSplitConstant"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="PURCHASE"/&gt;
 *     &lt;enumeration value="CASHBACK"/&gt;
 *     &lt;enumeration value="PARAMOUNT"/&gt;
 *     &lt;enumeration value="GST"/&gt;
 *     &lt;enumeration value="CGST"/&gt;
 *     &lt;enumeration value="SGST"/&gt;
 *     &lt;enumeration value="IGST"/&gt;
 *     &lt;enumeration value="CESS"/&gt;
 *     &lt;enumeration value="GSTINCENTIVE"/&gt;
 *     &lt;enumeration value="GSTPCT"/&gt;
 *     &lt;enumeration value="TIPS"/&gt;
 *     &lt;enumeration value="CONFEE"/&gt;
 *     &lt;enumeration value="DISCPCT"/&gt;
 *     &lt;enumeration value="CONPCT"/&gt;
 *     &lt;enumeration value="DISCOUNT"/&gt;
 *     &lt;enumeration value="baseAmount"/&gt;
 *     &lt;enumeration value="baseCurr"/&gt;
 *     &lt;enumeration value="FX"/&gt;
 *     &lt;enumeration value="Mkup"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 *
 */
@XmlType(name = "amountSplitConstant")
@XmlEnum
public enum AmountSplitConstant {

    PURCHASE("PURCHASE"),
    CASHBACK("CASHBACK"),
    PARAMOUNT("PARAMOUNT"),
    GST("GST"),
    CGST("CGST"),
    SGST("SGST"),
    IGST("IGST"),
    CESS("CESS"),
    GSTINCENTIVE("GSTINCENTIVE"),
    GSTPCT("GSTPCT"),
    TIPS("TIPS"),
    CONFEE("CONFEE"),
    DISCPCT("DISCPCT"),
    CONPCT("CONPCT"),
    DISCOUNT("DISCOUNT"),
    @XmlEnumValue("baseAmount")
    BASE_AMOUNT("baseAmount"),
    @XmlEnumValue("baseCurr")
    BASE_CURR("baseCurr"),
    FX("FX"),
    @XmlEnumValue("Mkup")
    MKUP("Mkup");
    private final String value;

    AmountSplitConstant(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AmountSplitConstant fromValue(String v) {
        for (AmountSplitConstant c: AmountSplitConstant.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
