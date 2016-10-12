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
import ru.trett.cis.interfaces.InventoryService;
import ru.trett.cis.models.Tracking;

import javax.inject.Inject;
import java.util.List;

@Controller
@RequestMapping("/tracking")
public class TrackingController {

    private final InventoryService inventoryService;

    @Inject
    public TrackingController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @RequestMapping("/list")
    public String list(Model model) {
        List<Tracking> trackingList = inventoryService.list(Tracking.class);
        model.addAttribute("list", trackingList);
        model.addAttribute("object", "Tracking");
        return "tracking/table";
    }

    @RequestMapping("/{id}")
    public String details(@PathVariable("id") String id, Model model) {
        Tracking tracking = inventoryService.findById(Tracking.class, Long.parseLong(id));
        model.addAttribute("tracking", tracking);
        return "tracking/form";
    }

}
