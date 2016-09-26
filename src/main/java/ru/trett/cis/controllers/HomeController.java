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

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import ru.trett.cis.interfaces.InventoryService;
import ru.trett.cis.models.Tracking;
import ru.trett.cis.models.User;

import javax.inject.Inject;
import javax.validation.Valid;

@Controller
@RequestMapping({"/", "/home"})
@SessionAttributes("user")
public class HomeController {

    private final InventoryService inventoryService;

    @Inject
    public HomeController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @RequestMapping
    public String mainPage(Model model) {
        model.addAttribute("track", inventoryService.list(Tracking.class, 1, 10));
        model.addAttribute("resultMapperList", inventoryService.assetGroupByStatus());
        return "home/index";
    }

    @RequestMapping("/profile")
    public String profile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = inventoryService.getUserByLoginName(auth.getName());
        if (user == null) {
            user = new User();
            user.setLoginName(auth.getName());
        }
        model.addAttribute("user", user);
        return "home/profile";
    }

    @RequestMapping(value = "/profile", method = RequestMethod.POST)
    public String save(@Valid @ModelAttribute User user, BindingResult result, SessionStatus session) {
        if (result.hasErrors())
            return "home/profile";
        inventoryService.save(user);
        session.setComplete();
        return "redirect:/home/profile";
    }

    @RequestMapping(value = "profile/delete")
    public String deleteProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = inventoryService.getUserByLoginName(auth.getName());
        inventoryService.remove(User.class, user.getId());
        return "redirect:/logout";
    }

}
