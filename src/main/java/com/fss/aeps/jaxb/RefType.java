//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.0
// See https://eclipse-ee4j.github.io/jaxb-ri
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2022.07.10 at 04:24:59 PM IST
//


package com.fss.aeps.jaxb;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for refType.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="refType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="PAYER"/&gt;
 *     &lt;enumeration value="PAYEE"/&gt;
 *     &lt;enumeration value="BENEFICIARY"/&gt;
 *     &lt;enumeration value="REMITTER"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 *
 */
@XmlType(name = "refType")
@XmlEnum
public enum RefType {

    PAYER,
    PAYEE,
    BENEFICIARY,
    REMITTER;

    public String value() {
        return name();
    }

    public static RefType fromValue(String v) {
        return valueOf(v);
    }

}
