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

import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.validation.ValidationContext;
import org.springframework.stereotype.Component;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;
import ru.trett.cis.interfaces.InventoryService;
import ru.trett.cis.models.DeviceType;

import javax.inject.Inject;

@Component
public class DeviceTypeValidator {

    private final InventoryService inventoryService;

    @Inject
    public DeviceTypeValidator(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    public void validateType(DeviceType deviceType, ValidationContext context) {
        RequestContext requestContext = RequestContextHolder.getRequestContext();
        Boolean exists =
                Boolean
                        .valueOf(requestContext.getExternalContext()
                                .getRequestParameterMap()
                                .get("exists"));

        if (inventoryService.getDeviceTypeByName(deviceType.getType()) != null && !exists)
            context
                    .getMessageContext()
                    .addMessage(
                            new MessageBuilder()
                                    .error()
                                    .source("type")
                                    .code("not.unique")
                                    .defaultText("Already present")
                                    .build());
    }
}
