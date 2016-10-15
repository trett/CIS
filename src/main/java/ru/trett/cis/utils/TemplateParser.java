package ru.trett.cis.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class TemplateParser {

    public static Map<String, List<String>> parse(File file)
            throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(file);
        Map<String, List<String>> result = new HashMap<>();
        result.put("header", Collections
                .singletonList(document.getElementsByTagName("header").item(0).getTextContent()));
        NodeList nParagraph = document.getElementsByTagName("paragraph");
        List<String> text = new ArrayList<>();
        for (int i = 0; i < nParagraph.getLength(); ++i) {
            text.add(nParagraph.item(i).getTextContent());
        }
        result.put("text", text);
        Node nTable = document.getElementsByTagName("table").item(0);
        Element table = (Element) nTable;
        NodeList tCols = table.getElementsByTagName("col");
        List<String> cols = new ArrayList<>();
        for (int i = 0; i < tCols.getLength(); i++) {
            cols.add(tCols.item(i).getTextContent());
        }
        result.put("cols", cols);
        Node nSigners = document.getElementsByTagName("signers").item(0);
        NodeList nSigner = ((Element) nSigners).getElementsByTagName("signer");
        List<String> signers = new ArrayList<>();
        for (int i = 0; i < nSigner.getLength(); ++i) {
            signers.add(nSigner.item(i).getTextContent());
        }
        result.put("signers", signers);
        result.put("place", Collections
                .singletonList(document.getElementsByTagName("place").item(0).getTextContent()));
        return result;
    }
}
