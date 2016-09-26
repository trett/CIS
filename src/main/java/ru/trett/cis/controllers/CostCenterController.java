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
import ru.trett.cis.models.CostCenter;

import javax.inject.Inject;
import javax.validation.Valid;

@Controller
@SessionAttributes("costCenter")
@RequestMapping("/costcenter")
public class CostCenterController {

    private final InventoryService inventoryService;

    @Inject
    public CostCenterController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @RequestMapping(value = "/list")
    public String list(Model model) {
        model.addAttribute("object", new CostCenter());
        return "costcenter/table";
    }

    @RequestMapping
    public String showForm(Model model) {
        model.addAttribute("costCenter", new CostCenter());
        return "costcenter/form";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String processForm(@ModelAttribute @Valid CostCenter costCenter, BindingResult result, SessionStatus status) {
        if (result.hasErrors()) {
            return "costcenter/form";
        }
        inventoryService.save(costCenter);
        status.setComplete();
        return "redirect:/costcenter/list";
    }

    @RequestMapping(value = "/{id}")
    public ModelAndView showUpdateForm(@PathVariable("id") String id, ModelAndView mv) {
        mv.addObject("costCenter", inventoryService.findById(CostCenter.class, Long.parseLong(id)));
        mv.setViewName("costcenter/form");
        return mv;
    }

    @RequestMapping(value = "/{id}/delete")
    public String delete(@PathVariable("id") long id) {
        inventoryService.remove(CostCenter.class, id);
        return "redirect:/costcenter/list";
    }

}
