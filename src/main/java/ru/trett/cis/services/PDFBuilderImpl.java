/*
 *     CIS - cool inventory system
 *
 *     Copyright © 2016 Roman Tretyakov <roman@trett.ru>
 *
 *     ********************************************************************
 *
 *     CIS is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Foobar is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with CIS.  If not, see <http://www.gnu.org/licenses/>.
 */

package ru.trett.cis.services;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import org.springframework.stereotype.Service;
import ru.trett.cis.exceptions.ApplicationException;
import ru.trett.cis.interfaces.PDFBuilder;
import ru.trett.cis.models.Asset;
import ru.trett.cis.models.Employee;
import ru.trett.cis.models.User;
import ru.trett.cis.utils.TemplateParser;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service("pdfBuilder")
public class PDFBuilderImpl implements PDFBuilder {

    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd MMM yyyy")
            .withLocale(Locale.forLanguageTag("ru-RU"));

    private Font regularFont;

    private Font boldFont;

    private Employee employee;

    private User issuer;

    private List<Asset> assets;

    private ServletContext servletContext;

    private LocalDate date;

    @Inject
    public PDFBuilderImpl(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    @Override
    public void setIssuer(User issuer) {
        this.issuer = issuer;
    }

    @Override
    public void setAssets(List<Asset> assets) {
        this.assets = assets;
    }

    @Override
    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String createPDF() throws IOException, DocumentException, ApplicationException {
        try {
            String templatePath = servletContext.getRealPath("WEB-INF/resources/template-settings.xml");
            Map<String, List<String>> data = TemplateParser.parse(new File(templatePath));
            String regularFontPath = servletContext.getRealPath("WEB-INF/resources/fonts/OpenSans-Regular.ttf");
            if (regularFontPath == null)
                throw new ApplicationException("Regular Font file was not found");
            BaseFont bfr = BaseFont.createFont(regularFontPath, BaseFont.IDENTITY_H, true);
            String boldFontPath = servletContext.getRealPath("WEB-INF/resources/fonts/OpenSans-Bold.ttf");
            if (boldFontPath == null)
                throw new ApplicationException("Bold Font file was not found");
            BaseFont bfb = BaseFont.createFont(boldFontPath, BaseFont.IDENTITY_H, true);
            regularFont = new Font(bfr);
            regularFont.setSize(10);
            boldFont = new Font(bfb);
            boldFont.setSize(10);

            File file = File.createTempFile("order", ".pdf");
            Document doc = new Document(PageSize.A4);
            Chunk glue = new Chunk(new VerticalPositionMark());
            PdfWriter.getInstance(doc, new FileOutputStream(file));
            doc.open();
            doc.newPage();

            //title
            Paragraph p = new Paragraph(data.get("header").get(0), boldFont);
            p.setAlignment(Element.ALIGN_CENTER);
            doc.add(p);
            doc.add(Chunk.NEWLINE);

            //body
            for (String text : data.get("text")) {
                p = new Paragraph(text, regularFont);
                doc.add(p);
            }

            //table
            doc.add(Chunk.NEWLINE);
            List<String> cols = data.get("cols");
            PdfPTable table = new PdfPTable(cols.size());
            table.setWidthPercentage(100);

            cols.forEach(x -> table.addCell(getCell(x, PdfPCell.ALIGN_CENTER, boldFont)));

            for (Asset asset : assets) {
                table.addCell(getCell(String.format("%s %s %s",
                        asset.getDeviceModel().getDeviceType().getType(),
                        asset.getDeviceModel().getDeviceBrand().getBrand(),
                        asset.getDeviceModel().getModel()), PdfPCell.ALIGN_CENTER, regularFont));
                table.addCell(getCell(asset.getSerialNumber(), PdfPCell.ALIGN_CENTER, regularFont));
                table.addCell(getCell(asset.getInventoryNumber(), PdfPCell.ALIGN_CENTER, regularFont));
            }

            table.getRows();
            doc.add(table);
            doc.add(Chunk.NEWLINE);
            doc.add(Chunk.NEWLINE);

            //signers
            Phrase phrase = new Phrase(new Chunk(data.get("signers").get(0) + " ", regularFont));
            phrase.add(Chunk.NEWLINE);
            phrase.add(new Chunk(issuer.getFirstName() + " " + issuer.getLastName() + "  \\__________________",
                    regularFont));
            phrase.add(Chunk.NEWLINE);
            phrase.add(Chunk.NEWLINE);
            phrase.add(new Chunk(data.get("signers").get(1) + " ", regularFont));
            phrase.add(Chunk.NEWLINE);
            phrase.add(new Chunk(employee.getFirstName() + " " + employee.getLastName() + "  \\__________________",
                    regularFont));
            doc.add(phrase);

            //date
            doc.add(Chunk.NEWLINE);
            doc.add(Chunk.NEWLINE);
            p = new Paragraph(data.get("place").get(0), regularFont);
            p.add(new Chunk(glue));
            p.add(date.format(dateFormat));
            doc.add(p);
            doc.close();
            return file.getPath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private PdfPCell getCell(String text, int alignment, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        cell.setHorizontalAlignment(alignment);
        return cell;
    }

}
