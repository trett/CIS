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

package ru.trett.cis.rest;

import com.itextpdf.text.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.trett.cis.exceptions.ApplicationException;
import ru.trett.cis.interfaces.InventoryService;
import ru.trett.cis.interfaces.PDFBuilder;
import ru.trett.cis.interfaces.PrinterService;
import ru.trett.cis.models.Asset;
import ru.trett.cis.models.Employee;
import ru.trett.cis.models.Invoice;
import ru.trett.cis.models.User;
import ru.trett.cis.utils.ResultMapper;
import ru.trett.cis.DTO.ChartDataDTO;
import ru.trett.cis.DTO.ResponseDTO;

import javax.inject.Inject;
import javax.print.PrintException;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/invoice")
public class InvoiceAPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceAPI.class.getName());

    private final InventoryService inventoryService;

    private final PrinterService printerService;

    private final PDFBuilder pdfBuilder;

    private final MessageSource messageSource;

    private final Locale locale = LocaleContextHolder.getLocale();

    @Inject
    public InvoiceAPI(InventoryService inventoryService, PrinterService printerService,
                      PDFBuilder pdfBuilder, MessageSource messageSource) {
        this.inventoryService = inventoryService;
        this.printerService = printerService;
        this.pdfBuilder = pdfBuilder;
        this.messageSource = messageSource;
    }

    @RequestMapping(value = "/print", method = RequestMethod.POST)
    public ResponseDTO print(@RequestParam("employee") String employeeId,
                             @RequestParam("issuer") String issuerId,
                             @RequestParam("invoice") String invoiceId) {
        Employee employee = inventoryService.findById(Employee.class, Long.parseLong(employeeId));
        User issuer = inventoryService.findById(User.class, Long.parseLong(issuerId));
        Invoice invoice = inventoryService.findById(Invoice.class, Long.parseLong(invoiceId));
        List<Asset> assets = inventoryService.getAssetListForInvoice(invoice);
        ResponseDTO response = new ResponseDTO();
        pdfBuilder.setEmployee(employee);
        pdfBuilder.setIssuer(issuer);
        pdfBuilder.setAssets(assets);
        pdfBuilder.setDate(invoice.getDate().toLocalDateTime().toLocalDate());
        LOGGER.info("pdf building....");
        try {
            String fileName = pdfBuilder.createPDF();
            String printerName = printerService.print(fileName);
            response.setMessage("Job was sending to " + printerName, "SUCCESS");
            LOGGER.info("printing... " + fileName);
        } catch (IOException | DocumentException | PrintException | ApplicationException e) {
            LOGGER.error(e.getMessage());
            response.setMessage(e.getMessage(), "DANGER");
        }
        return response;
    }

    @RequestMapping(value = "/publish", method = RequestMethod.POST)
    public ResponseDTO publish(@RequestParam("invoiceId") String invoiceId) throws ApplicationException {
        ResponseDTO responseDTO = new ResponseDTO();
        inventoryService.publishInvoice(invoiceId);
        responseDTO.setMessage(messageSource.getMessage("published", null, locale), "SUCCESS");
        responseDTO.setCustomData(Invoice.Status.PUBLISHED.name());
        return responseDTO;
    }

    @RequestMapping(value = "/chartdata")
    public ChartDataDTO<ResultMapper> weekChartData() {
        ChartDataDTO<ResultMapper> wrapper = new ChartDataDTO<>();
        wrapper.setName("2 weeks statistics");
        wrapper.setData(inventoryService.twoWeeksChartData());
        return wrapper;
    }

}
