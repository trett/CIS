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

package ru.trett.cis.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.trett.cis.DTO.TableSearchResultsDTO;
import ru.trett.cis.exceptions.ApplicationException;
import ru.trett.cis.exceptions.BindingException;
import ru.trett.cis.interfaces.*;
import ru.trett.cis.models.*;
import ru.trett.cis.utils.EnumUtil;
import ru.trett.cis.utils.ResultMapper;

import javax.inject.Inject;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service("inventoryService")
public class InventoryServiceImpl implements InventoryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryServiceImpl.class.getName());

    private EmployeeAssetDAO employeeAssetDAO;
    private DeviceTypeDAO deviceTypeDAO;
    private AssetDAO assetDAO;
    private DeviceModelDAO deviceModelDAO;
    private DeviceBrandDAO deviceBrandDAO;
    private CostCenterDAO costCenterDAO;
    private CommonDAO commonDAO;
    private UserDAO userDAO;
    private InvoiceDAO invoiceDAO;

    @Inject
    public InventoryServiceImpl(
            EmployeeAssetDAO employeeAssetDAO,
            DeviceTypeDAO deviceTypeDAO,
            AssetDAO assetDAO,
            DeviceModelDAO deviceModelDAO,
            DeviceBrandDAO deviceBrandDAO,
            CostCenterDAO costCenterDAO,
            CommonDAO commonDAO,
            UserDAO userDAO,
            InvoiceDAO invoiceDAO) {
        this.employeeAssetDAO = employeeAssetDAO;
        this.deviceTypeDAO = deviceTypeDAO;
        this.assetDAO = assetDAO;
        this.deviceModelDAO = deviceModelDAO;
        this.deviceBrandDAO = deviceBrandDAO;
        this.costCenterDAO = costCenterDAO;
        this.commonDAO = commonDAO;
        this.userDAO = userDAO;
        this.invoiceDAO = invoiceDAO;
        LOGGER.info("Service initialization");
    }

    @Override
    @Transactional
    public void buildIndices() {
        commonDAO.buildIndices();
        LOGGER.info("Indices was rebuilt");
    }

    @Override
    @Transactional(readOnly = true)
    public <T extends BaseEntity> T findById(Class<T> aClass, Long id) {
        return commonDAO.findById(aClass, id);
    }

    @Override
    @Transactional
    public <T extends BaseEntity> void save(T t) {
        commonDAO.save(t);
    }

    @Override
    @Transactional
    public <T extends BaseEntity> void update(T t) {
        commonDAO.update(t);
    }

    @Override
    @Transactional
    public <T extends BaseEntity> void remove(Class<T> tClass, Long id) {
        commonDAO.remove(tClass, id);
    }

    @Override
    @Transactional(readOnly = true)
    public <T extends BaseEntity> List<T> list(Class<T> tClass) {
        return commonDAO.list(tClass);
    }

    @Override
    @Transactional(readOnly = true)
    public <T extends BaseEntity> List<T> list(Class<T> tClass, Integer first, Integer length) {
        return commonDAO.list(tClass, first, length);
    }

    @Override
    @Transactional(readOnly = true)
    public <T extends BaseEntity> Number rowCount(Class<T> tClass) {
        return commonDAO.rowCount(tClass);
    }

    @Override
    @Transactional
    public Long save(final Long employeeId, final List<Asset> assetList, String status, String loginName)
            throws BindingException {
        Timestamp date = new Timestamp(new Date().getTime());
        Employee employee = findById(Employee.class, employeeId);
        User issuer = userDAO.findByLoginName(loginName);

        Invoice invoice = new Invoice(date,
                EnumUtil.lookup(Invoice.Status.class, status),
                issuer,
                employee);
        save(invoice);

        for (Asset asset : assetList) {
            DeviceModel deviceModel = getModelByTypeAndBrandAndModel(
                    asset.getDeviceModel().getDeviceType().getType(),
                    asset.getDeviceModel().getDeviceBrand().getBrand(),
                    asset.getDeviceModel().getModel());
            if (deviceModel == null)
                throw new BindingException("Device Model is unknown.");

            // check if asset exists
            if (!asset.getSerialNumber().isEmpty()) {
                List<Asset> assets = assetDAO.getAssetsBySerialNumber(asset.getSerialNumber().toUpperCase());
                if (!assets.isEmpty()) {
                    for (Asset a : assets) {
                        if (a.getDeviceModel().equals(deviceModel))
                            asset = a;
                    }
                }
            }
            asset.setDeviceModel(deviceModel);
            asset.setSerialNumber(asset.getSerialNumber().toUpperCase());
            asset.setInventoryNumber(asset.getInventoryNumber().toUpperCase());
            asset.setEmployee(employee);
            switch (invoice.getStatus()) {
                case PUBLISHED:
                    asset.setStatus(Asset.Status.ACTIVE);
                    break;
                case DRAFT:
                    asset.setStatus(Asset.Status.IN_STOCK);
                    break;
            }
            save(asset);

            EmployeeAsset employeeAsset = new EmployeeAsset(employee, asset, invoice);
            save(employeeAsset);

            Tracking tracking = new Tracking(issuer, date, asset, "Was created and added to Database");
            save(tracking);
        }

        return invoice.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public DeviceModel getModelByTypeAndBrandAndModel(String type, String brand, String model) {
        return deviceModelDAO.findByTypeAndBrandAndModel(type, brand, model);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Invoice> getInvoicesForEmployee(Employee employee) {
        List<Invoice> invoices = list(Invoice.class);
        return invoices.stream().filter(x -> x.getEmployee().equals(employee)).collect(Collectors.toSet());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Asset> getAssetListForInvoice(Invoice invoice) {
        List<EmployeeAsset> employeeAssets = employeeAssetDAO.listByInvoiceId(invoice.getId());
        List<Long> ids = new ArrayList<>();
        employeeAssets.forEach(x -> ids.add(x.getAsset().getId()));
        return assetDAO.assetListByIds(ids);
    }

    @Override
    @Transactional(readOnly = true)
    public <T extends BaseEntity> TableSearchResultsDTO<T> searchTable(
            Class<T> tClass,
            String[] fields,
            String key,
            Integer first,
            Integer length) {
        return commonDAO.searchTable(tClass, fields, key, first, length);
    }

    @Override
    @Transactional(readOnly = true)
    public DeviceBrand getDeviceBrandByName(String brand) {
        return deviceBrandDAO.findByName(brand);
    }

    @Override
    @Transactional(readOnly = true)
    public DeviceType getDeviceTypeByName(String name) {
        return deviceTypeDAO.findByType(name);
    }

    @Override
    @Transactional(readOnly = true)
    public CostCenter getCostCenterByNumber(String number) {
        return costCenterDAO.findByNumber(number);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getBrandsByType(String deviceType) {
        return deviceModelDAO.findBrandByType(deviceType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeviceModel> getModelsByTypeAndBrand(String deviceType, String deviceBrand) {
        return deviceModelDAO.findModelsByTypeAndBrand(deviceType, deviceBrand);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserByLoginName(String loginName) {
        return userDAO.findByLoginName(loginName);
    }

    @Override
    @Transactional
    public void save(String deviceType, String deviceBrand, String deviceModel, String itemNumber) {
        DeviceType type = getDeviceTypeByName(deviceType);
        if (type == null) {
            type = new DeviceType(deviceType);
            save(type);
        }

        DeviceBrand brand = getDeviceBrandByName(deviceBrand);
        if (brand == null) {
            brand = new DeviceBrand(deviceBrand);
            save(brand);
        }

        DeviceModel model = new DeviceModel(type, brand, deviceModel);

        if (itemNumber != null)
            model.setItemNumber(itemNumber);
        save(model);
    }

    @Override
    @Transactional(readOnly = true)
    public Employee employeeByInvoice(Invoice invoice) {
        return employeeAssetDAO.findByInvoice(invoice.getId()).getEmployee();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Asset> getAssetsBySerialNumber(String serialNumber) {
        return assetDAO.getAssetsBySerialNumber(serialNumber.toUpperCase());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Asset> getAssetsByInventoryNumber(String inventoryNumber) {
        return assetDAO.getAssetByInventoryNumber(inventoryNumber.toUpperCase());
    }

    @Override
    @Transactional(readOnly = true)
    public Invoice getLastInvoiceForAsset(Asset asset) {
        return getInvoicesByAsset(asset)
                .stream()
                .max((d1, d2) -> d1.getDate().compareTo(d2.getDate()))
                .orElseGet(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Invoice> getInvoicesByAsset(Asset asset) {
        return asset
                .getEmployeeAssetSet()
                .stream()
                .map(EmployeeAsset::getInvoice)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void publishInvoice(String invoiceId) throws ApplicationException {
        try {
            Invoice invoice = findById(Invoice.class, Long.parseLong(invoiceId));
            if (invoice.getStatus() == Invoice.Status.PUBLISHED)
                return;
            List<Asset> assets = getAssetListForInvoice(invoice);
            assets.forEach(x -> x.setStatus(Asset.Status.ACTIVE));
            invoice.setStatus(Invoice.Status.PUBLISHED);
            update(invoice);
        } catch (Exception e) {
            throw new ApplicationException(e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Number getDeviceCount(DeviceModel deviceModel) {
        return assetDAO.getDeviceCountById(deviceModel.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Asset> getAssetsByEmployee(Employee employee) {
        List<Asset> assets = list(Asset.class);
        return assets
                .stream()
                .filter(
                        x -> x.getEmployee().equals(employee)
                                && x.getStatus() != Asset.Status.IN_STOCK
                                && x.getStatus() != Asset.Status.RETIRED
                )
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteInvoice(Invoice invoice) {
        List<Asset> assets = getAssetListForInvoice(invoice);
        for (Asset asset : assets) {
            if (asset.getStatus() != Asset.Status.IN_STOCK)
                throw new RuntimeException("Asset with id " + asset.getId() +
                        " in " + asset.getStatus().name() + " status");
            remove(Asset.class, asset.getId());
        }
        remove(Invoice.class, invoice.getId());
    }

    @Override
    @Transactional
    public void updateAsset(Asset asset, String status, String comment) {
        asset.setStatus(EnumUtil.lookup(Asset.Status.class, status));
        asset.setComment(comment);
        save(asset);
        User issuer = userDAO.findByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
        Tracking tracking = new Tracking(
                issuer,
                new Timestamp(new Date().getTime()),
                asset,
                "Changed status to " + asset.getStatus()
        );
        save(tracking);
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<ResultMapper> pieChartData() {
        return assetDAO.deviceModelGroupAndCount();
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<ResultMapper> twoWeeksChartData() {
        return invoiceDAO.recordsPerDay();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResultMapper> assetGroupByStatus() {
        return assetDAO.assetGroupByStatusAndCount();
    }

    @Override
    @Transactional(readOnly = true)
    public void changeStatusToAllAssetsForEmployee(Employee employee, Asset.Status status) {
        User issuer = userDAO.findByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
        getAssetsByEmployee(employee)
                .forEach(x -> {
                    if (x.getStatus() != Asset.Status.ACTIVE)
                        throw new RuntimeException("Asset with id " + x.getId() + " not ACTIVE");
                    x.setStatus(status);
                    Tracking tracking = new Tracking(
                            issuer,
                            new Timestamp(new Date().getTime()),
                            x,
                            "Changed status to " + status
                    );
                    save(x);
                    save(tracking);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<Asset> assetsByDeviceModel(DeviceModel deviceModel) {
        return assetDAO.getAssetsWithDeviceModelId(deviceModel.getId());
    }

}
