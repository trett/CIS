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

package ru.trett.cis.validators;


import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import ru.trett.cis.interfaces.InventoryService;
import ru.trett.cis.models.CostCenter;
import ru.trett.cis.models.Employee;

import javax.inject.Inject;

@Component
public class EmployeeValidator implements Validator {

    private final InventoryService inventoryService;

    @Inject
    public EmployeeValidator(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    public boolean supports(Class<?> aClass) {
        return Employee.class.isAssignableFrom(aClass);
    }

    public void validate(Object o, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "costCenter.number", "not.empty");
        if (!errors.hasErrors()) {
            Employee employee = (Employee) o;
            CostCenter costCenter = inventoryService.getCostCenterByNumber(employee.getCostCenter().getNumber());
            if (costCenter == null)
                errors.rejectValue("costCenter.number", "unknown.costCenter.number");
        }
    }
}
