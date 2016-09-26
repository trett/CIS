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

import com.itextpdf.text.DocumentException;
import ru.trett.cis.exceptions.ApplicationException;
import ru.trett.cis.models.Asset;
import ru.trett.cis.models.Employee;
import ru.trett.cis.models.User;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface PDFBuilder {

    void setEmployee(Employee employeeName);

    void setIssuer(User issuer);

    void setDate(LocalDate date);

    String createPDF() throws IOException, DocumentException, ApplicationException;

    void setAssets(List<Asset> assets);

}
