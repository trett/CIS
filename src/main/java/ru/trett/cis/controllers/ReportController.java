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
import ru.trett.cis.models.Asset;
import ru.trett.cis.models.DeviceModel;

import javax.inject.Inject;
import java.util.List;

@Controller
@RequestMapping("/report")
public class ReportController {

    private final InventoryService inventoryService;

    @Inject
    public ReportController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @RequestMapping(value = "devicemodelform")
    public String showForm(Model model, DeviceModel deviceModel) {
        model.addAttribute("deviceModel", deviceModel);
        return "reports/form";
    }


    @RequestMapping(value = "/form", method = RequestMethod.POST)
    public String processForm(@ModelAttribute DeviceModel dm, Model model) {
        DeviceModel deviceModel =
                inventoryService
                        .getModelByTypeAndBrandAndModel(
                                dm.getDeviceType().getType(),
                                dm.getDeviceBrand().getBrand(),
                                dm.getModel()
                        );
        List<Asset> assets = inventoryService.assetsByDeviceModel(deviceModel);
        model.addAttribute("assets", assets);
        return "reports/table";
    }

}
