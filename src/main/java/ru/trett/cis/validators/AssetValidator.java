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
import ru.trett.cis.models.Asset;
import ru.trett.cis.models.DeviceModel;
import ru.trett.cis.models.Invoice;

import javax.inject.Inject;
import java.util.List;

@Component
public class AssetValidator implements Validator {

    private InventoryService inventoryService;

    @Inject
    public AssetValidator(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return Asset.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        ValidationUtils.rejectIfEmpty(errors, "deviceModel.deviceType.type", "not.empty");
        ValidationUtils.rejectIfEmpty(errors, "deviceModel.deviceBrand.brand", "not.empty");
        ValidationUtils.rejectIfEmpty(errors, "deviceModel.model", "not.empty");

        Asset obj = (Asset) o;
        //if serial and a inventory number are not present do not check
        if (obj.getSerialNumber().length() == 0 && obj.getInventoryNumber().length() == 0)
            return;

        List<Asset> assets = inventoryService.getAssetsBySerialNumber(obj.getSerialNumber().toUpperCase());

        // if assets exists
        for (Asset asset : assets) {
            if (asset != null) {

                DeviceModel model =
                        inventoryService
                                .getModelByTypeAndBrandAndModel(
                                        obj.getDeviceModel().getDeviceType().getType(),
                                        obj.getDeviceModel().getDeviceBrand().getBrand(),
                                        obj.getDeviceModel().getModel());

                //return if device model are not the same
                if (!asset.getDeviceModel().equals(model))
                    break;

                //if asset with given serial number is present but with another inventory number
                if (asset.getInventoryNumber().length() > 0
                        && !asset.getInventoryNumber().equals(obj.getInventoryNumber().toUpperCase())) {
                    errors.rejectValue("inventoryNumber", "asset.wrong.inventory");
                    break;
                }

                Invoice invoice = inventoryService.getLastInvoiceForAsset(asset);

                if (invoice == null)
                    throw new RuntimeException("Invoice date error");

                if (invoice.getStatus() == Invoice.Status.DRAFT) {
                    errors.rejectValue("serialNumber", "asset.block");
                    break;
                }
            }
        }
    }

}
