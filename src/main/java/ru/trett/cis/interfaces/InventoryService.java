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

import ru.trett.cis.exceptions.ApplicationException;
import ru.trett.cis.exceptions.BindingException;
import ru.trett.cis.models.*;
import ru.trett.cis.utils.ResultMapper;
import ru.trett.cis.DTO.TableSearchResultsDTO;

import java.util.List;
import java.util.Set;

public interface InventoryService {

    /**
     * Rebuilds Lucene indices
     */
    void buildIndices();

    /**
     * Returns Model object with given ID
     *
     * @param aClass Class object
     * @param id     model ID
     * @param <T>    must be extends BaseEntity
     * @return Model object
     */
    <T extends BaseEntity> T findById(Class<T> aClass, Long id);

    /**
     * Saves Model
     *
     * @param t   Class object
     * @param <T> must be extends BaseEntity
     */
    <T extends BaseEntity> void save(T t);

    /**
     * Updates Model
     *
     * @param t   Class object
     * @param <T> must be extends BaseEntity
     */
    <T extends BaseEntity> void update(T t);

    /**
     * Removes Model with given ID
     *
     * @param tClass Class object
     * @param id     model ID
     * @param <T>    must be extends BaseEntity
     */
    <T extends BaseEntity> void remove(Class<T> tClass, Long id);

    /**
     * Returns list of Models for given class
     *
     * @param tClass Class object
     * @param <T>    must be extends BaseEntity
     * @return list of items
     */
    <T extends BaseEntity> List<T> list(Class<T> tClass);

    /**
     * Returns list of Models for given class with fixed size
     *
     * @param tClass Class object
     * @param first  first element ID
     * @param length size of list
     * @param <T>    must be extends BaseEntity
     * @return list contains Models object
     */
    <T extends BaseEntity> List<T> list(Class<T> tClass, Integer first, Integer length);

    /**
     * Returns count of tables rows for given Model
     *
     * @param tClass Class object
     * @param <T>    must be extends BaseEntity
     * @return rows quantity
     */
    <T extends BaseEntity> Number rowCount(Class<T> tClass);

    /**
     * Returns Object contains result of full text search query of table
     *
     * @param tClass Class object
     * @param fields array contains fields which will be used for search
     * @param key    query word
     * @param first  first element ID
     * @param length length of list
     * @param <T>    must be extends BaseEntity
     * @return wrapper with fixed size list which contains search results for key word
     * @see TableSearchResultsDTO
     */
    <T extends BaseEntity> TableSearchResultsDTO<T> searchTable(
            Class<T> tClass, String[] fields,
            String key, Integer first,
            Integer length
    );

    /**
     * Gets DeviceBrand by name
     *
     * @param brand name
     * @return DeviceBrand with given name
     */
    DeviceBrand getDeviceBrandByName(String brand);

    /**
     * Gets DeviceType by name
     *
     * @param name device type name
     * @return DeviceType with given name
     */
    DeviceType getDeviceTypeByName(String name);

    /**
     * Gets CostCenter by number
     *
     * @param number cost center number
     * @return CostCenter with given name
     */
    CostCenter getCostCenterByNumber(String number);

    /**
     * Creates assets and saves it for employee with given status
     *
     * @param employeeId id for employee
     * @param assetList  assets lists
     * @param status     asset status
     * @param loginName  issuer login name
     * @return Invoice id that will be created during transaction
     * @throws BindingException
     * @see BindingException
     * @see Asset.Status
     */
    Long save(Long employeeId, List<Asset> assetList, String status, String loginName) throws BindingException;

    /**
     * Gets unique DeviceModel by DeviceType name, DeviceBrand name and DeviceModel name
     *
     * @param type  device type
     * @param brand device brand
     * @param model device model
     * @return unique DeviceModel
     */
    DeviceModel getModelByTypeAndBrandAndModel(String type, String brand, String model);

    /**
     * Gets all invoices for Employee
     *
     * @param employee employee
     * @return list of invoices
     */
    Set<Invoice> getInvoicesForEmployee(Employee employee);

    /**
     * Gets all assets for given invoice
     *
     * @param invoice invoice
     * @return list of assets
     */
    List<Asset> getAssetListForInvoice(Invoice invoice);

    /**
     * Gets all possible brands for given DeviceType type
     *
     * @param deviceType device yype
     * @return list of brands
     */
    List<String> getBrandsByType(String deviceType);

    /**
     * Gets device models with given type and brand
     *
     * @param deviceType  device type
     * @param deviceBrand device brand
     * @return list of device models
     */
    List<DeviceModel> getModelsByTypeAndBrand(String deviceType, String deviceBrand);

    /**
     * Get User with given login name
     *
     * @param loginName users login name
     * @return user for login name
     */
    User getUserByLoginName(String loginName);

    /**
     * Saves DeviceModel
     *
     * @param deviceType  device type
     * @param deviceBrand device brand
     * @param deviceModel device model
     * @param itemNumber  unique number for device model
     */
    void save(String deviceType, String deviceBrand, String deviceModel, String itemNumber);

    /**
     * Gets Employee for invoice
     *
     * @param invoice Invoice
     * @return employee
     */
    Employee employeeByInvoice(Invoice invoice);

    /**
     * Gets assets with given serial number
     *
     * @param serialNumber number
     * @return list of assets
     */
    List<Asset> getAssetsBySerialNumber(String serialNumber);

    /**
     * Gets assets with given inventory number
     *
     * @param inventoryNumber number
     * @return list of assets
     */
    List<Asset> getAssetsByInventoryNumber(String inventoryNumber);

    /**
     * Gets invoices that contains given asset
     *
     * @param asset asset
     * @return last of invoices
     */
    Invoice getLastInvoiceForAsset(Asset asset);

    /**
     * Gets invoices list which contains
     *
     * @param asset asset
     * @return invoices list
     */
    List<Invoice> getInvoicesByAsset(Asset asset);

    /**
     * Publishes invoice if current status is DRAFT
     * <p>All elements status will be changed to ACTIVE</p>
     * <p>All assets must have status IN_STOCK</p>
     *
     * @param invoiceId Invoice ID
     * @throws ApplicationException will be thrown if any assets doesn't have IN_STOCK status
     */
    void publishInvoice(String invoiceId) throws ApplicationException;

    /**
     * Returns total number of device models
     *
     * @param deviceModel device model
     * @return total number
     */
    Number getDeviceCount(DeviceModel deviceModel);

    /**
     * Gets assets for employee
     *
     * @param employee employee
     * @return list of assets
     */
    List<Asset> getAssetsByEmployee(Employee employee);

    /**
     * Deletes invoice
     *
     * @param invoice invoice
     */
    void deleteInvoice(Invoice invoice);

    /**
     * Updates assets with new status and comment
     *
     * @param asset   asset
     * @param status  status
     * @param comment comment
     * @see Asset.Status
     */
    void updateAsset(Asset asset, String status, String comment);

    /**
     * Returns data for pie chart
     *
     * @return data
     */
    List pieChartData();

    /**
     * Returns data for two weeks chart Data
     *
     * @return data
     * @see ResultMapper
     */
    List<ResultMapper> twoWeeksChartData();

    /**
     * Returns assets grouped by status
     *
     * @return list of assets
     * @see ResultMapper
     */
    List<ResultMapper> assetGroupByStatus();

    /**
     * Changes all assets for Employee to given status
     *
     * @param employee employee
     * @param status   new status
     * @see Asset.Status
     */
    void changeStatusToAllAssetsForEmployee(Employee employee, Asset.Status status);

    /**
     * Returns assets with given device models
     *
     * @param deviceModel device model
     * @return list of assets
     */
    List<Asset> assetsByDeviceModel(DeviceModel deviceModel);

}
