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

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.trett.cis.interfaces.InventoryService;
import ru.trett.cis.models.Employee;
import ru.trett.cis.DTO.TableSearchResultsDTO;

import javax.inject.Inject;
import java.util.List;

@RestController
@RequestMapping("/api/employee")
public class EmployeeAPI {

    private final InventoryService inventoryService;

    @Inject
    EmployeeAPI(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public List<Employee> search(@RequestParam("name") String name) {

        TableSearchResultsDTO<Employee> tableSearchResultsDTO =
                inventoryService
                        .searchTable(
                                Employee.class,
                                new String[]{"firstName", "lastName"},
                                name,
                                0,
                                Integer.MAX_VALUE);

        return tableSearchResultsDTO.getData();
    }

}
