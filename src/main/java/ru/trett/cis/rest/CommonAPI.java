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

package ru.trett.cis.rest;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.trett.cis.DTO.ResponseDTO;
import ru.trett.cis.DTO.TableSearchResultsDTO;
import ru.trett.cis.exceptions.ClassMappingException;
import ru.trett.cis.interfaces.InventoryService;
import ru.trett.cis.models.BaseEntity;
import ru.trett.cis.models.DeviceModel;

import javax.inject.Inject;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/common")
public class CommonAPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonAPI.class.getName());

    private final InventoryService inventoryService;

    private MessageSource messageSource;

    @Inject
    public CommonAPI(InventoryService inventoryService, MessageSource messageSource) {
        this.inventoryService = inventoryService;
        this.messageSource = messageSource;
    }

    private Class getModelClass(String className) throws ClassMappingException {
        Reflections reflections = new Reflections("ru.trett.cis.models");
        Set<Class<? extends BaseEntity>> classes = reflections.getSubTypesOf(BaseEntity.class);
        Set<String> classNames = new LinkedHashSet<>();
        classes.forEach(x -> classNames.add(x.getSimpleName()));
        if (!classNames.contains(className))
            throw new ClassMappingException("Class is not Model.");
        try {
            return Class.forName("ru.trett.cis.models." + className);
        } catch (ClassNotFoundException e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/table", method = RequestMethod.GET)
    public <T extends BaseEntity> TableSearchResultsDTO tableData
            (@RequestParam("draw") String draw,
             @RequestParam("start") String start,
             @RequestParam("length") String length,
             @RequestParam("obj") String obj,
             @RequestParam("fields[]") String[] fields,
             @RequestParam("search[value]") String key) throws ClassMappingException {
        Class tClass = getModelClass(obj);
        Number rowCount = inventoryService.rowCount(tClass);
        TableSearchResultsDTO<T> tableSearchResultsDTO = new TableSearchResultsDTO<>();
        tableSearchResultsDTO.setDraw(Integer.parseInt(draw));
        Integer firstRow = Integer.parseInt(start);
        Integer lengthRow = Integer.parseInt(length);
        if (key.isEmpty()) {
            tableSearchResultsDTO.setRecordsTotal(rowCount);
            tableSearchResultsDTO.setRecordsFiltered(rowCount);
            tableSearchResultsDTO.setData(inventoryService.list(tClass, firstRow, lengthRow));
        } else {
            tableSearchResultsDTO = inventoryService.searchTable(tClass, fields, key, firstRow, lengthRow);
        }
        return tableSearchResultsDTO;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<? extends BaseEntity> order(@RequestParam("obj") String obj) {
        try {
            Class tClass = getModelClass(obj);
            return inventoryService.list(tClass);
        } catch (ClassMappingException e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    @RequestMapping(value = "/brandbytype", method = RequestMethod.GET)
    public List<String> brandsByType(@RequestParam("devicetype") String deviceType) {
        return inventoryService.getBrandsByType(deviceType);
    }

    @RequestMapping(value = "/modelsbytypeandbrand", method = RequestMethod.GET)
    public List<DeviceModel> modelsByTypeAndBrand(
            @RequestParam("devicetype") String deviceType,
            @RequestParam("devicebrand") String deviceBrand) {
        return inventoryService.getModelsByTypeAndBrand(deviceType, deviceBrand);
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/delete/{obj}/{id}", method = RequestMethod.DELETE)
    public <T extends BaseEntity> ResponseDTO delete(@PathVariable("obj") String obj, @PathVariable("id") String id)
            throws ClassMappingException {
        ResponseDTO responseDTO = new ResponseDTO();
        Class<T> tClass = getModelClass(obj);
        inventoryService.remove(tClass, Long.parseLong(id));
        responseDTO.setMessage(messageSource.getMessage(
                "success",
                null,
                LocaleContextHolder.getLocale()), "SUCCESS"
        );

        return responseDTO;
    }

}

