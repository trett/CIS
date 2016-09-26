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

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.trett.cis.DTO.ChartDataDTO;
import ru.trett.cis.DTO.ResponseDTO;
import ru.trett.cis.exceptions.ApplicationException;
import ru.trett.cis.interfaces.InventoryService;
import ru.trett.cis.models.Asset;
import ru.trett.cis.models.Invoice;
import ru.trett.cis.utils.ResultMapper;

import javax.inject.Inject;
import java.util.Locale;

@RestController
@RequestMapping("api/asset")
public class AssetAPI {

    private final InventoryService inventoryService;
    private final MessageSource messageSource;

    @Inject
    public AssetAPI(InventoryService inventoryService, MessageSource messageSource) {
        this.inventoryService = inventoryService;
        this.messageSource = messageSource;
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResponseDTO setStatus(@RequestParam("id") String id,
                                 @RequestParam("status") String status,
                                 @RequestParam("comment") String comment) throws ApplicationException {
        ResponseDTO responseDTO = new ResponseDTO();
        Asset asset = inventoryService.findById(Asset.class, Long.parseLong(id));
        Invoice invoice = inventoryService.getLastInvoiceForAsset(asset);
        if (invoice == null)
            throw new ApplicationException("Invoice date error");
        Locale locale = LocaleContextHolder.getLocale();
        if (invoice.getStatus() != Invoice.Status.PUBLISHED) {
            responseDTO.setMessage(messageSource.getMessage("asset.block", null, locale), "WARNING");
            return responseDTO;
        }
        inventoryService.updateAsset(asset, status, comment);
        responseDTO.setMessage(messageSource.getMessage("success", null, locale), "SUCCESS");
        return responseDTO;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping("/chartdata")
    public ChartDataDTO<ResultMapper> chartData() {
        ChartDataDTO<ResultMapper> wrapper = new ChartDataDTO<>();
        wrapper.setData(inventoryService.pieChartData());
        return wrapper;
    }

}
