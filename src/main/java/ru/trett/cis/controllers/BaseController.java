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

package ru.trett.cis.controllers;


import org.omg.CORBA.portable.ApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.trett.cis.exceptions.ClassMappingException;
import ru.trett.cis.DTO.ResponseDTO;

@ControllerAdvice
public class BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseController.class.getName());

    private static final String ERROR_TEMPLATE = "error";

    @ExceptionHandler(value = Exception.class)
    public String errorHandler(Model model, Exception e) {
        LOGGER.error(e.getMessage());
        model.addAttribute("message", "Critical error. " + e.getMessage());
        return ERROR_TEMPLATE;
    }

    @ExceptionHandler(value = {ApplicationException.class, ClassMappingException.class})
    @ResponseBody
    public ResponseDTO applicationExceptionHandler(ApplicationException e) {
        ResponseDTO responseDTO = new ResponseDTO();
        LOGGER.error(e.getMessage());
        responseDTO.setMessage(e.getMessage(), "DANGER");
        return responseDTO;
    }

    @ExceptionHandler(value = DataIntegrityViolationException.class)
    public String conflictHandler(Model model) {
        model.addAttribute("level", "warning");
        model.addAttribute("message", "This data in use.");
        return ERROR_TEMPLATE;
    }

}
