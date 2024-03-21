package com.adobe.acrobat.demo;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "fields")
public class CheckSheet {
    private TestStatus no1;
    private TestStatus no2;
    private TestStatus no3;
    private TestStatus no4;
    private TestStatus no5;
    private TestStatus no6;
    private TestStatus no7;
    private TestStatus no8;
    private TestStatus no9;
    private TestStatus no10;
    private String name;
    private String date;
}
