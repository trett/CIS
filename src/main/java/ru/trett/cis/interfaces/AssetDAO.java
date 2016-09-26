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

package ru.trett.cis.interfaces;

import ru.trett.cis.models.Asset;
import ru.trett.cis.utils.ResultMapper;

import java.util.List;

public interface AssetDAO {

    List<Asset> assetListByIds(List<Long> ids);

    List<Asset> getAssetsBySerialNumber(String serialNumber);

    List<Asset> getAssetByInventoryNumber(String inventoryNumber);

    Number getDeviceCountById(Long deviceModelId);

    List deviceModelGroupAndCount();

    List<ResultMapper> assetGroupByStatusAndCount();

    List<Asset> getAssetsWithDeviceModelId(Long id);
}
