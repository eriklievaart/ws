package com.eriklievaart.ws.repo.sax;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.eriklievaart.ws.toolkit.io.StreamUtils;

public class SaxSupport {

	public static void parse(File file, DefaultHandler handler) throws IOException {
		try {
			getParser().parse(file, handler);

		} catch (SAXException | ParserConfigurationException e) {
			throw new IOException(e);
		}
	}

	public static void parse(InputStream is, DefaultHandler handler) throws IOException {
		try {
			getParser().parse(is, handler);

		} catch (SAXException | ParserConfigurationException e) {
			throw new IOException(e);
		}
	}

	public static void parse(String xml, DefaultHandler handler) throws IOException {
		parse(StreamUtils.toInputStream(xml), handler);
	}

	public static void parse(String xml, SaxPartialHandler handler) throws IOException {
		parse(StreamUtils.toInputStream(xml), handler);
	}

	public static void parse(InputStream is, SaxPartialHandler handler) throws IOException {
		parse(is, new SaxHandlerSplitter(handler));
	}

	private static SAXParser getParser() throws SAXException, ParserConfigurationException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
		return factory.newSAXParser();
	}
}
