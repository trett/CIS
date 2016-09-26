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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import ru.trett.cis.config.JPAConfig;
import ru.trett.cis.interfaces.PDFBuilder;
import ru.trett.cis.models.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Collections;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {JPAConfig.class})
@ActiveProfiles("test")
@WebAppConfiguration
public class PDFBuilderTest extends Assert {

    @Autowired
    PDFBuilder pdfBuilder;

    @Test
    public void createPDF() throws Exception {
        Employee employee = new Employee();
        employee.setFirstName("Иван");
        employee.setLastName("Иванов");
        pdfBuilder.setEmployee(employee);
        User issuer = new User();
        issuer.setFirstName("Петр");
        issuer.setLastName("Козлов");
        pdfBuilder.setIssuer(issuer);
        Asset asset = new Asset();
        DeviceModel dm = new DeviceModel();
        DeviceBrand db = new DeviceBrand();
        DeviceType dt = new DeviceType();
        db.setBrand("HP");
        dt.setType("Laptop");
        dm.setModel("840 G1");
        dm.setDeviceBrand(db);
        dm.setDeviceType(dt);
        asset.setDeviceModel(dm);
        asset.setSerialNumber("32179879");
        asset.setInventoryNumber("A000402");
        pdfBuilder.setAssets(Collections.singletonList(asset));
        pdfBuilder.setDate(LocalDate.now());
        String file = pdfBuilder.createPDF();
        System.out.println("file: " + file);
        assertTrue(Files.exists(Paths.get(file)));
    }

}