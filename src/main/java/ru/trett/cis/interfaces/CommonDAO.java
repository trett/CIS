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

import ru.trett.cis.models.BaseEntity;
import ru.trett.cis.DTO.TableSearchResultsDTO;

import java.util.List;

public interface CommonDAO {

    <T extends BaseEntity> T findById(Class<T> tClass, Long id);

    <T extends BaseEntity> void save(T t);

    <T extends BaseEntity> void remove(Class<T> tClass, Long id);

    <T extends BaseEntity> List<T> list(Class<T> tClass);

    <T extends BaseEntity> List<T> list(Class<T> tClass, Integer first, Integer length);

    <T extends BaseEntity> Number rowCount(Class<T> tClass);

    <T extends BaseEntity> TableSearchResultsDTO<T> searchTable(Class<T> tClass, String[] fields, String key, Integer first, Integer length);

    <T extends BaseEntity> void update(T t);

    void buildIndices();
}
