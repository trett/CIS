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

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.trett.cis.interfaces.InventoryService;
import ru.trett.cis.interfaces.XLSBuilder;
import ru.trett.cis.models.Asset;
import ru.trett.cis.models.DeviceModel;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/report")
public class ReportController {

    private final InventoryService inventoryService;

    private final XLSBuilder xlsBuilder;

    private List<List<String>> data;

    @Inject
    public ReportController(InventoryService inventoryService, XLSBuilder xlsBuilder) {
        this.inventoryService = inventoryService;
        this.xlsBuilder = xlsBuilder;
    }

    @RequestMapping(value = "/devicemodelform")
    public String showModelForm(Model model, DeviceModel deviceModel) {
        model.addAttribute("deviceModel", deviceModel);
        return "reports/form";
    }


    @RequestMapping(value = "/form", method = RequestMethod.POST)
    public String processModelForm(@ModelAttribute DeviceModel dm, Model model) {
        DeviceModel deviceModel =
                inventoryService
                        .getModelByTypeAndBrandAndModel(
                                dm.getDeviceType().getType(),
                                dm.getDeviceBrand().getBrand(),
                                dm.getModel()
                        );
        List<Asset> assets = inventoryService.assetsByDeviceModel(deviceModel);
        data = new ArrayList<>();
        for (Asset asset : assets) {
            List<String> col = new ArrayList<>();
            col.add(asset.getEmployee().getFirstName());
            col.add(asset.getEmployee().getLastName());
            col.add(asset.getDeviceModel().getDeviceType().getType());
            col.add(asset.getDeviceModel().getDeviceBrand().getBrand());
            col.add(asset.getDeviceModel().getModel());
            col.add(asset.getStatus().name());
            data.add(col);
        }
        model.addAttribute("data", data);
        return "reports/table";
    }

    @RequestMapping(value = "/xls")
    public void downloadXLS(HttpServletResponse response) {
        xlsBuilder.downloadXLSFile(response, data);
    }

}
