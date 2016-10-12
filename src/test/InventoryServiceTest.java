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

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.validation.ValidationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.webflow.execution.RequestContextHolder;
import org.springframework.webflow.test.MockExternalContext;
import org.springframework.webflow.test.MockParameterMap;
import org.springframework.webflow.test.MockRequestContext;
import org.springframework.webflow.validation.DefaultValidationContext;
import ru.trett.cis.config.JPAConfig;
import ru.trett.cis.config.WebConfig;
import ru.trett.cis.config.WebFlowConfig;
import ru.trett.cis.interfaces.InventoryService;
import ru.trett.cis.models.*;
import ru.trett.cis.validators.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {JPAConfig.class, WebConfig.class, WebFlowConfig.class})
@ActiveProfiles("test")
@WebAppConfiguration
public class InventoryServiceTest extends Assert {

    private static CostCenter cc;

    private static Employee employee;

    private static User user;

    private static boolean initializedFlag = false;

    private MockParameterMap map;

    private ValidationContext validationContext;

    private MockRequestContext requestContext;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private AssetValidator assetValidator;

    @Autowired
    private DeviceModelValidator deviceModelValidator;

    @Autowired
    private DeviceBrandValidator deviceBrandValidator;

    @Autowired
    private DeviceTypeValidator deviceTypeValidator;

    @Autowired
    private EmployeeValidator employeeValidator;

    @BeforeClass
    public static void setUp() throws Exception {
        cc = new CostCenter();
        cc.setNumber("3");
        cc.setDescription("test");
        employee = new Employee();
        employee.setFirstName("Ivan");
        employee.setLastName("Sokolov");
        employee.setMiddleName("Petrovich");
        employee.setCostCenter(cc);
        employee.setPosition("test position");
        user = new User();
        user.setFirstName("Petr");
        user.setLastName("Petrov");
        user.setLoginName("admin");
        user.setPosition("Manger");
    }

    private DeviceModel createModel(String dt, String db, String dm) {
        DeviceType deviceType = new DeviceType(dt);
        DeviceBrand deviceBrand = new DeviceBrand(db);
        return new DeviceModel(deviceType, deviceBrand, dm);
    }

    @Before
    public void init() throws Exception {
        if (!initializedFlag) {
            inventoryService.save(cc);
            inventoryService.save(employee);
            DeviceType type = new DeviceType("Laptop");
            inventoryService.save(type);
            DeviceBrand brand = new DeviceBrand("HP");
            inventoryService.save(brand);
            DeviceModel model = new DeviceModel(type, brand, "folio 1040");
            model.setItemNumber("000111");
            inventoryService.save(model);
            DeviceModel model2 = new DeviceModel(type, brand, "pro 1234");
            model2.setItemNumber("000112");
            inventoryService.save(model2);
            Asset asset = new Asset();
            asset.setDeviceModel(model);
            asset.setSerialNumber("001");
            asset.setInventoryNumber("001");
            Asset asset2 = new Asset();
            asset2.setDeviceModel(model2);
            asset2.setSerialNumber("002");
            asset2.setInventoryNumber("002");
            asset2.setComment("test comment");
            inventoryService.save(user);
            inventoryService.save(employee.getId(), Arrays.asList(asset, asset2), "PUBLISHED", user.getLoginName());
            initializedFlag = true;
        }
        requestContext = new MockRequestContext();
        MessageContext messages = requestContext.getMessageContext();
        messages.clearMessages();
        map = new MockParameterMap();
        MockExternalContext externalContext = new MockExternalContext(map);
        requestContext.setExternalContext(externalContext);
        validationContext = new DefaultValidationContext(requestContext, "start", null);
        RequestContextHolder.setRequestContext(requestContext);
    }

    @Test
    public void CRUD() throws Exception {
        CostCenter cc = new CostCenter();
        cc.setNumber("1");
        inventoryService.save(cc);
        assertEquals(cc, inventoryService.getCostCenterByNumber(cc.getNumber()));
        cc.setNumber("2");
        inventoryService.update(cc);
        assertNotNull(inventoryService.getCostCenterByNumber("2"));
        inventoryService.remove(CostCenter.class, cc.getId());
        assertNull(inventoryService.getCostCenterByNumber("2"));
    }

    @Test
    public void list() throws Exception {
        assertEquals(1, inventoryService.list(Invoice.class).size());
    }

    @Test
    public void getAssetByEmployee() throws Exception {
        assertEquals(2, inventoryService.getAssetsByEmployee(employee).size());
    }

    @Test
    public void getInvoicesForEmployee() throws Exception {
        Set<Invoice> invoices = inventoryService.getInvoicesForEmployee(employee);
        assertEquals(1, invoices.size());
    }

    @Test
    public void getAssetListForInvoice() throws Exception {
        Invoice invoice = inventoryService.list(Invoice.class).get(0);
        assertEquals(2, inventoryService.getAssetListForInvoice(invoice).size());
    }

    @Test
    public void employeeByInvoice() throws Exception {
        Invoice invoice = inventoryService.list(Invoice.class).get(0);
        assertEquals(employee, inventoryService.employeeByInvoice(invoice));
    }

    @Test
    public void getModelByTypeAndBrandAndModel() throws Exception {
        DeviceModel dm = inventoryService.getModelByTypeAndBrandAndModel("Laptop", "HP", "folio 1040");
        assertNotNull(dm);
        assertEquals(1, inventoryService.getDeviceCount(dm).intValue());
    }

    @Test
    public void getBrandsByType() throws Exception {
        assertEquals(1, inventoryService.getBrandsByType("Laptop").size());
    }

    @Test
    public void getDeviceBrandByName() throws Exception {
        assertNotNull(inventoryService.getDeviceBrandByName("HP"));
    }

    @Test
    public void getDeviceTypeByName() throws Exception {
        assertNotNull(inventoryService.getDeviceTypeByName("Laptop"));
    }

    @Test
    public void getModelsByTypeAndBrand() throws Exception {
        assertEquals(2, inventoryService.getModelsByTypeAndBrand("Laptop", "HP").size());
    }

    @Test
    public void getUserByLoginName() throws Exception {
        assertNotNull(inventoryService.getUserByLoginName("admin"));
    }

    @Test
    public void getAssetsBySerialNumber() throws Exception {
        assertNotNull(inventoryService.getAssetsBySerialNumber("001"));
        assertNotNull(inventoryService.getAssetsBySerialNumber("002"));
    }

    @Test
    public void getAssetsByInventoryNumber() throws Exception {
        assertNotNull(inventoryService.getAssetsByInventoryNumber("001"));
        assertNotNull(inventoryService.getAssetsByInventoryNumber("002"));
    }

    @Test
    public void getInvoiceByAsset() throws Exception {
        List<Asset> list = inventoryService.list(Asset.class);
        list.forEach(x -> assertNotNull(inventoryService.getLastInvoiceForAsset(x)));
    }

    @Test
    public void deviceTypeValidator() throws Exception {
        DeviceType dt = new DeviceType("Laptop");
        deviceTypeValidator.validateType(dt, validationContext);
        assertEquals(1, requestContext.getMessageContext().getAllMessages().length);
    }

    @Test
    public void deviceBrandValidator() throws Exception {
        DeviceBrand db = new DeviceBrand("HP");
        deviceBrandValidator.validateBrand(db, validationContext);
        assertEquals(1, requestContext.getMessageContext().getAllMessages().length);
    }

    @Test
    public void deviceModelValidator() throws Exception {
        deviceModelValidator.validateModel(createModel("Laptop", "HP", "folio 1040"), validationContext);
        assertEquals(1, requestContext.getMessageContext().getAllMessages().length);
    }

    @Test
    public void deviceTypeValidatorIgnoreCase() throws Exception {
        DeviceType dt = new DeviceType("LaPtoP");
        deviceTypeValidator.validateType(dt, validationContext);
        assertEquals(1, requestContext.getMessageContext().getAllMessages().length);
    }

    @Test
    public void deviceBrandValidatorIgnoreCase() throws Exception {
        DeviceBrand db = new DeviceBrand("hP");
        deviceBrandValidator.validateBrand(db, validationContext);
        assertEquals(1, requestContext.getMessageContext().getAllMessages().length);
    }

    @Test
    public void deviceModelValidatorIgnoreCase() throws Exception {
        deviceModelValidator.validateModel(createModel("LaPTop", "Hp", "FoLiO 1040"), validationContext);
        assertEquals(1, requestContext.getMessageContext().getAllMessages().length);
    }

    @Test
    public void deviceTypeValidatorExists() throws Exception {
        map.put("exists", "true");
        DeviceType dt = new DeviceType("Laptop");
        deviceTypeValidator.validateType(dt, validationContext);
        assertEquals(0, requestContext.getMessageContext().getAllMessages().length);
    }

    @Test
    public void deviceBrandValidatorExists() throws Exception {
        map.put("exists", "true");
        DeviceBrand db = new DeviceBrand("HP");
        deviceBrandValidator.validateBrand(db, validationContext);
        assertEquals(0, requestContext.getMessageContext().getAllMessages().length);
    }

    @Test
    public void deviceModelValidatorExists() throws Exception {
        map.put("exists", "true");
        deviceModelValidator.validateModel(createModel("Laptop", "HP", "folio 1040"), validationContext);
        assertEquals(0, requestContext.getMessageContext().getAllMessages().length);
    }


    @Test
    public void assetValidatorWrongInventoryNumber() throws Exception {
        Asset asset = new Asset();
        asset.setDeviceModel(createModel("Laptop", "HP", "folio 1040"));
        asset.setSerialNumber("001");
        asset.setInventoryNumber("002");
        Errors errors = new BeanPropertyBindingResult(asset, "");
        assetValidator.validate(asset, errors);
        assertEquals(1, errors.getErrorCount());
    }

    @Test
    public void assetValidatorDraftInvoice() throws Exception {
        Asset asset = inventoryService.getAssetsBySerialNumber("001").get(0);
        Invoice invoice = inventoryService.getLastInvoiceForAsset(asset);
        invoice.setStatus(Invoice.Status.DRAFT);
        inventoryService.save(invoice);
        Errors errors = new BeanPropertyBindingResult(asset, "");
        assetValidator.validate(asset, errors);
        assertEquals(errors.getFieldError().getField(), 1, errors.getErrorCount());
        System.out.println(errors.getFieldError().getField());
    }
}