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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import ru.trett.cis.interfaces.InventoryService;
import ru.trett.cis.models.DeviceBrand;
import ru.trett.cis.models.DeviceModel;
import ru.trett.cis.models.DeviceType;
import ru.trett.cis.validators.DeviceModelValidator;

import javax.inject.Inject;
import javax.validation.Valid;

@Controller
@SessionAttributes("deviceModel")
@RequestMapping("/devicemodel")
public class DeviceModelController {

    private final InventoryService inventoryService;

    private final DeviceModelValidator deviceModelValidator;

    @Inject
    public DeviceModelController(DeviceModelValidator deviceModelValidator, InventoryService inventoryService) {
        this.deviceModelValidator = deviceModelValidator;
        this.inventoryService = inventoryService;
    }

    @RequestMapping(value = "/list")
    public String list(Model model) {
        model.addAttribute("object", "DeviceModel");
        return "devicemodel/table";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String processForm(@ModelAttribute @Valid DeviceModel deviceModel,
                              BindingResult result, SessionStatus status) {
        deviceModelValidator.validate(deviceModel, result);
        if (result.hasErrors())
            return "devicemodel/form";
        DeviceBrand deviceBrand = inventoryService.getDeviceBrandByName(deviceModel.getDeviceBrand().getBrand());
        DeviceType deviceType = inventoryService.getDeviceTypeByName(deviceModel.getDeviceType().getType());
        deviceModel.setDeviceBrand(deviceBrand);
        deviceModel.setDeviceType(deviceType);
        inventoryService.update(deviceModel);
        status.setComplete();
        return "redirect:/devicemodel/list";
    }

    @RequestMapping(value = "/{id}")
    public ModelAndView showUpdateForm(@PathVariable("id") String id, ModelAndView mv) {
        DeviceModel deviceModel = inventoryService.findById(DeviceModel.class, Long.parseLong(id));
        mv.addObject("deviceModel", deviceModel);
        mv.addObject("quantity", inventoryService.getDeviceCount(deviceModel));
        mv.setViewName("devicemodel/form");
        return mv;
    }

    @RequestMapping(value = "/{id}/delete")
    public String delete(@PathVariable("id") long id) {
        inventoryService.remove(DeviceModel.class, id);
        return "redirect:/devicemodel/list";
    }

}
