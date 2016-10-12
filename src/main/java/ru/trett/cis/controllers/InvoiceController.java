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

package ru.trett.cis.controllers;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import ru.trett.cis.interfaces.InventoryService;
import ru.trett.cis.models.Asset;
import ru.trett.cis.models.Invoice;

import javax.inject.Inject;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/invoice")
public class InvoiceController {

    private final InventoryService inventoryService;

    private final MessageSource messageSource;

    private final Locale locale = LocaleContextHolder.getLocale();

    @Inject
    public InvoiceController(InventoryService inventoryService, MessageSource messageSource) {
        this.inventoryService = inventoryService;
        this.messageSource = messageSource;
    }

    @RequestMapping(value = "{id}")
    public ModelAndView list(@PathVariable("id") String id, ModelAndView mv) {
        Invoice invoice = inventoryService.findById(Invoice.class, Long.parseLong(id));
        List<Asset> assets = inventoryService.getAssetListForInvoice(invoice);
        mv.addObject("assets", assets);
        mv.addObject("invoice", invoice);
        mv.addObject("issuer", invoice.getIssuer());
        mv.setViewName("invoice/index");
        return mv;
    }

    @RequestMapping(value = "/list")
    public String list(Model model) {
        model.addAttribute("object", "Invoice");
        return "invoice/table";
    }


    @RequestMapping(value = "/{id}/delete", method = RequestMethod.GET)
    public String delete(@PathVariable("id") String invoiceId, Model model) {
        Invoice invoice = inventoryService.findById(Invoice.class, Long.parseLong(invoiceId));
        if (invoice.getStatus() == Invoice.Status.PUBLISHED) {
            model.addAttribute("message", messageSource.getMessage("must.not.be.published", null, locale));
            return "error";
        }
        inventoryService.deleteInvoice(invoice);
        return "invoice/table";
    }

}
