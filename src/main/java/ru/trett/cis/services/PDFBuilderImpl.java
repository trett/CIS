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

import javax.inject.Inject;
import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

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
            String regularFontPath = servletContext.getRealPath("WEB-INF/resources/fonts/OpenSans-Regular.ttf");
            if (regularFontPath == null)
                throw new ApplicationException("Regular Font file was not found");
            BaseFont bfr = BaseFont.createFont(regularFontPath, BaseFont.IDENTITY_H, true);
            String boldFontPath = servletContext.getRealPath("WEB-INF/resources/fonts/OpenSans-Bold.ttf");
            if (boldFontPath == null)
                throw new ApplicationException("Bold Font file was not found");
            BaseFont bfb = BaseFont.createFont(boldFontPath, BaseFont.IDENTITY_H, true);
            regularFont = new Font(bfr);
            boldFont = new Font(bfb);
        } catch (Exception e) {
            e.printStackTrace();
        }
        File file = File.createTempFile("order", ".pdf");
        Document doc = new Document(PageSize.A4);
        Chunk glue = new Chunk(new VerticalPositionMark());
        PdfWriter.getInstance(doc, new FileOutputStream(file));
        doc.open();
        doc.newPage();
        //title
        Paragraph p = new Paragraph("Акт приема-передачи", boldFont);
        p.setAlignment(Element.ALIGN_CENTER);
        doc.add(p);
        doc.add(Chunk.NEWLINE);
        //body
        p = new Paragraph(String.format(
                "Настоящий документ подтверждает, что оборудование, готовое к работе, получает сотрудник %s %s.",
                employee.getFirstName(), employee.getLastName()),
                regularFont
        );
        doc.add(p);
        p = new Paragraph(
                "Данное оборудование закрепляется за сотрудником, и с момента выдачи он несет за него ответственность.",
                regularFont
        );
        doc.add(p);
        doc.add(Chunk.NEWLINE);
        p = new Paragraph("Сотрудник обязуется:", boldFont);
        doc.add(p);
        com.itextpdf.text.List list = new com.itextpdf.text.List(com.itextpdf.text.List.ORDERED);
        list.add(new ListItem(new Chunk("бережно обращаться с выданным ему оборудованием," +
                " эксплуатировать его только по назначению",
                regularFont)));
        list.add(new ListItem(new Chunk("в случае поломки, выхода оборудования из строя или наличия каких-либо неполадок" +
                " в его работе необходимо немедленно сообщить об этом в департамент ИТ",
                regularFont)));
        doc.add(list);

        //table
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        doc.add(Chunk.NEWLINE);
        table.addCell(getCell("Оборудование", PdfPCell.ALIGN_CENTER));
        table.addCell(getCell("Серийный номер", PdfPCell.ALIGN_CENTER));
        table.addCell(getCell("Инвентарный номер", PdfPCell.ALIGN_CENTER));
        for (Asset asset : assets) {
            table.addCell(getCell(String.format("%s %s %s",
                    asset.getDeviceModel().getDeviceType().getType(),
                    asset.getDeviceModel().getDeviceBrand().getBrand(),
                    asset.getDeviceModel().getModel()), PdfPCell.ALIGN_CENTER));
            table.addCell(getCell(asset.getSerialNumber(), PdfPCell.ALIGN_CENTER));
            table.addCell(getCell(asset.getInventoryNumber(), PdfPCell.ALIGN_CENTER));
        }
        table.getRows();
        doc.add(table);
        doc.add(Chunk.NEWLINE);
        doc.add(Chunk.NEWLINE);
        //footer
        doc.add(Chunk.NEWLINE);
        doc.add(Chunk.NEWLINE);
        p = new Paragraph("Подпись сотрудника департамента ИТ", regularFont);
        p.add(glue);
        p.add(String.format("_______________   %s %s", issuer.getFirstName(), issuer.getLastName()));
        doc.add(p);
        doc.add(Chunk.NEWLINE);
        doc.add(Chunk.NEWLINE);
        p = new Paragraph("Подпись сотрудника, получившего оборудование", regularFont);
        p.add(glue);
        p.add(String.format("_______________   %s %s", employee.getFirstName(), employee.getLastName()));
        doc.add(p);
        //date
        doc.add(Chunk.NEWLINE);
        doc.add(Chunk.NEWLINE);
        p = new Paragraph("г. Калуга", regularFont);
        p.add(new Chunk(glue));
        p.add(String.format(" %s г.", date.format(dateFormat)));
        doc.add(p);
        doc.close();
        return file.getPath();
    }

    private PdfPCell getCell(String text, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, regularFont));
        cell.setPadding(10);
        cell.setHorizontalAlignment(alignment);
        return cell;
    }

}
