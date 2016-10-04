package ru.trett.cis.interfaces;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface XLSBuilder {
    void downloadXLSFile(HttpServletResponse response, List<List<String>> data);
}
