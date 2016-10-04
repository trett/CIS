package ru.trett.cis.services;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.trett.cis.interfaces.XLSBuilder;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Service("xlsBuilder")
public class XLSBuilderImpl implements XLSBuilder {

    private static final Logger LOGGGER = LoggerFactory.getLogger(XLSBuilderImpl.class.getName());

    @Override
    public void downloadXLSFile(HttpServletResponse response, List<List<String>> data) {
        try {
            String fileName = "report.xls";
            ServletOutputStream sos = response.getOutputStream();
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet();
            int i = 0;
            int j = 0;
            for (List<String> rows : data) {
                HSSFRow hssfRow = sheet.createRow(i);
                i++;
                for (String row : rows) {
                    HSSFCell cell = hssfRow.createCell(j);
                    cell.setCellValue(row);
                    j++;
                }
                j = 0;
            }
            LOGGGER.info("Report was created and write in " + fileName);
            response.setHeader("Content-Disposition", "inline; filename=" + fileName);
            response.setContentType("application/vnd.ms-excel");
            workbook.write(sos);
            sos.flush();
        } catch (Exception e) {
            throw new RuntimeException("Unable to write report to the output stream.");
        }
    }
}
