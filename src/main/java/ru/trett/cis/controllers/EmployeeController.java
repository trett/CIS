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
import ru.trett.cis.models.Asset;
import ru.trett.cis.models.CostCenter;
import ru.trett.cis.models.Employee;
import ru.trett.cis.models.Invoice;
import ru.trett.cis.validators.EmployeeValidator;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@Controller
@SessionAttributes("employee")
@RequestMapping("/employee")
public class EmployeeController {

    private final InventoryService inventoryService;

    private final EmployeeValidator employeeValidator;

    @Inject
    public EmployeeController(EmployeeValidator employeeValidator, InventoryService inventoryService) {
        this.employeeValidator = employeeValidator;
        this.inventoryService = inventoryService;
    }

    @RequestMapping(value = "/list")
    public String employeeList(Model model) {
        model.addAttribute("object", "Employee");
        return "employee/table";
    }

    @RequestMapping
    public String showForm(Model model) {
        model.addAttribute("employee", new Employee());
        return "employee/form";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String processForm(@ModelAttribute @Valid Employee employee,
                              BindingResult result, SessionStatus status) {
        employeeValidator.validate(employee, result);
        if (result.hasErrors())
            return "employee/form";
        CostCenter costCenter = inventoryService.getCostCenterByNumber(employee.getCostCenter().getNumber());
        employee.setCostCenter(costCenter);
        inventoryService.save(employee);
        status.setComplete();
        return "redirect:/employee/list";
    }

    @RequestMapping(value = "/{id}")
    public ModelAndView showUpdateForm(@PathVariable("id") String id, ModelAndView mv) {
        mv.addObject("employee", inventoryService.findById(Employee.class, Long.parseLong(id)));
        mv.setViewName("employee/form");
        return mv;
    }

    @RequestMapping(value = "/{id}/delete")
    public String delete(@PathVariable("id") long id) {
        inventoryService.remove(Employee.class, id);
        return "redirect:/employee/list";
    }

    @RequestMapping(value = "/search")
    public String searchEmployee() {
        return "employee/search";
    }

    @RequestMapping(value = "/{id}/assets")
    public String showAssets(@PathVariable("id") String employeeId, Model model) {
        Employee employee = inventoryService.findById(Employee.class, Long.parseLong(employeeId));
        List<Asset> assets = inventoryService.getAssetsByEmployee(employee);
        Set<Invoice> invoices = inventoryService.getInvoicesForEmployee(employee);
        model.addAttribute("assets", assets);
        model.addAttribute("invoices", invoices);
        model.addAttribute("employee", employee);
        return "employee/assets";
    }

}
