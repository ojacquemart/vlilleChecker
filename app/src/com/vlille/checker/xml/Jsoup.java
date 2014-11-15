package com.vlille.checker.xml;

import com.vlille.checker.Application;

import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

import java.io.IOException;
import java.io.InputStream;

public class Jsoup {

    public static final String HTTP = "http";
    public static final String UTF_16 = "UTF-16";

    public static Document getDocument(String path) {
        try {
            if (path.startsWith(HTTP)) {
                return getDocumentFromUrl(path);
            }

            return getDocumentFromFile(path);
        } catch (IOException e) {
            throw new IllegalStateException("Error while parsing file: " + path, e);
        }
    }


    public static Document getDocumentFromUrl(String path) throws IOException {
        return org.jsoup.Jsoup.connect(path).parser(Parser.xmlParser()).get();
    }

    private static Document getDocumentFromFile(String path) throws IOException {
        InputStream open = Application.getContext().getAssets().open(path);

        return org.jsoup.Jsoup.parse(open, UTF_16, "", Parser.xmlParser());
    }

}
