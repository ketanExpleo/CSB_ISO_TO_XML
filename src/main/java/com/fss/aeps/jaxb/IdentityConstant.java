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
 * <p>Java class for identityConstant.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="identityConstant"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="PAN"/&gt;
 *     &lt;enumeration value="BANK"/&gt;
 *     &lt;enumeration value="AADHAAR"/&gt;
 *     &lt;enumeration value="ACCOUNT"/&gt;
 *     &lt;enumeration value="GSTIN"/&gt;
 *     &lt;enumeration value="PASSPORT"/&gt;
 *     &lt;enumeration value="VOTERID"/&gt;
 *     &lt;enumeration value="DRIVINGLICENSE"/&gt;
 *     &lt;enumeration value="SHGACC"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 *
 */
@XmlType(name = "identityConstant")
@XmlEnum
public enum IdentityConstant {

    PAN,
    BANK,
    AADHAAR,
    ACCOUNT,
    GSTIN,
    PASSPORT,
    VOTERID,
    DRIVINGLICENSE,
    SHGACC;

    public String value() {
        return name();
    }

    public static IdentityConstant fromValue(String v) {
        return valueOf(v);
    }

}