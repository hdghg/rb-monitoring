package com.guthub.hdghg.rbmonitoring.service;

import com.guthub.hdghg.rbmonitoring.model.RbEntry;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class HtmlParser {
    public List<RbEntry> parse(InputStream is) throws IOException {
        Document doc = Jsoup.parse(is, "windows-1251", "http://example.com");
        Elements select = doc.getElementsByClass("content").select("tr");
        List<RbEntry> result = new ArrayList<>();
        for (Element element : select.subList(2, select.size())) {
            RbEntry rbEntry = new RbEntry();
            rbEntry.setLevel(Integer.parseInt(element.child(0).ownText()));
            rbEntry.setName(element.child(1).ownText());
            rbEntry.setAlive("Живой".equals(element.child(2).child(0).ownText()));
            result.add(rbEntry);
        }
        return result;
    }

}
