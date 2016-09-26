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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import ru.trett.cis.interfaces.InventoryService;
import ru.trett.cis.models.Asset;
import ru.trett.cis.models.Employee;

import javax.inject.Inject;

@Controller
@RequestMapping("/asset")
public class AssetController {

    private final InventoryService inventoryService;

    @Inject
    public AssetController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @RequestMapping(value = "/list")
    public String list(Model model) {
        model.addAttribute("object", new Asset());
        return "asset/table";
    }

    @RequestMapping(value = "/{id}")
    public ModelAndView showForm(@PathVariable("id") String id, ModelAndView mv) {
        mv.addObject("asset", inventoryService.findById(Asset.class, Long.parseLong(id)));
        mv.addObject("statuses", Asset.Status.values());
        mv.setViewName("asset/form");
        return mv;
    }

    @RequestMapping(value = "/tostock/{employeeId}")
    public String moveAssetsToStock(@PathVariable("employeeId") String employeeId) {
        Employee employee = inventoryService.findById(Employee.class, Long.parseLong(employeeId));
        inventoryService.changeStatusToAllAssetsForEmployee(employee, Asset.Status.IN_STOCK);
        return "redirect:/home";
    }

}
