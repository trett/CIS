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
import ru.trett.cis.models.DeviceType;

import javax.inject.Inject;
import javax.validation.Valid;

@Controller
@SessionAttributes("deviceType")
@RequestMapping("/devicetype")
public class DeviceTypeController {

    private final InventoryService inventoryService;

    @Inject
    public DeviceTypeController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @RequestMapping(value = "list")
    public String list(Model model) {
        model.addAttribute("object", new DeviceType());
        return "devicetype/table";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String processForm(@ModelAttribute @Valid DeviceType deviceType,
                              BindingResult result, SessionStatus status) {
        if (result.hasErrors())
            return "devicetype/form";
        inventoryService.update(deviceType);
        status.setComplete();
        return "redirect:/devicetype/list";
    }

    @RequestMapping(value = "/{id}")
    public ModelAndView showUpdateForm(@PathVariable("id") String id, ModelAndView mv) {
        mv.addObject("deviceType", inventoryService.findById(DeviceType.class, Long.parseLong(id)));
        mv.setViewName("devicetype/form");
        return mv;
    }

    @RequestMapping(value = "/{id}/delete")
    public String delete(@PathVariable("id") long id) {
        inventoryService.remove(DeviceType.class, id);
        return "redirect:/devicetype/list";
    }

}
