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
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;
import ru.trett.cis.interfaces.InventoryService;
import ru.trett.cis.models.DeviceModel;

import javax.inject.Inject;

@Component
public class DeviceModelValidator implements Validator {

    private final InventoryService inventoryService;

    @Inject
    public DeviceModelValidator(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    public boolean supports(Class<?> aClass) {
        return DeviceModel.class.isAssignableFrom(aClass);
    }

    public void validate(Object o, Errors errors) {
        ValidationUtils.rejectIfEmpty(errors, "deviceBrand.brand", "not.empty");
        ValidationUtils.rejectIfEmpty(errors, "deviceType.type", "not.empty");
    }

    public void validateModel(DeviceModel deviceModel, ValidationContext context) {
        RequestContext requestContext = RequestContextHolder.getRequestContext();
        Boolean exists =
                Boolean
                        .valueOf(requestContext.getExternalContext()
                                .getRequestParameterMap()
                                .get("exists"));

        DeviceModel dm =
                inventoryService
                        .getModelByTypeAndBrandAndModel(
                                deviceModel.getDeviceType().getType(),
                                deviceModel.getDeviceBrand().getBrand(),
                                deviceModel.getModel());

        if (dm != null && !exists)
            context
                    .getMessageContext()
                    .addMessage(
                            new MessageBuilder()
                                    .error()
                                    .source("model")
                                    .code("not.unique")
                                    .defaultText("Already present")
                                    .build());
    }
}
