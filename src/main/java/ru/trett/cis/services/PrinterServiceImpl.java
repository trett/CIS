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
import org.springframework.stereotype.Service;
import ru.trett.cis.controllers.BaseController;
import ru.trett.cis.exceptions.ApplicationException;
import ru.trett.cis.interfaces.PrinterService;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaSizeName;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@Service("printerService")
public class PrinterServiceImpl implements PrinterService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrinterServiceImpl.class.getName());

    @Override
    public String print(String file) throws ApplicationException, FileNotFoundException, PrintException {
        DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
        aset.add(MediaSizeName.ISO_A4);
        PrintService printServices = PrintServiceLookup.lookupDefaultPrintService();
        if (printServices == null)
            throw new ApplicationException("Default printer not found");
        LOGGER.info("Default printer is " + printServices.getName());
        DocPrintJob printJob = printServices.createPrintJob();
        FileInputStream fis = new FileInputStream(file);
        Doc doc = new SimpleDoc(fis, flavor, null);
        printJob.print(doc, aset);
        return printServices.getName();
    }

}
