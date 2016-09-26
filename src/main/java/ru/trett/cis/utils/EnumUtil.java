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

package ru.trett.cis.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnumUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnumUtil.class.getName());

    public static <E extends Enum<E>> E lookup(Class<E> e, String id) {
        E result;
        try {
            result = Enum.valueOf(e, id);
        } catch (IllegalArgumentException ex) {
            LOGGER.error(id + " is not valid argument for " + e.getSimpleName(), ex);
            throw new RuntimeException(id + " is not valid argument for " + e.getSimpleName());
        }
        return result;
    }

}
