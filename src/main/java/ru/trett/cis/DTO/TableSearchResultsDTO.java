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

package ru.trett.cis.DTO;

import ru.trett.cis.models.BaseEntity;

import java.util.List;

/**
 * DTO for DataTables plugin
 *
 * @param <T> must be extends BaseEntity
 */
public class TableSearchResultsDTO<T extends BaseEntity> {

    private Number draw;

    private Number recordsTotal;

    private Number recordsFiltered;

    private List<T> data;

    public Number getDraw() {
        return draw;
    }

    public void setDraw(Number draw) {
        this.draw = draw;
    }

    public Number getRecordsTotal() {
        return recordsTotal;
    }

    public void setRecordsTotal(Number recordsTotal) {
        this.recordsTotal = recordsTotal;
    }

    public Number getRecordsFiltered() {
        return recordsFiltered;
    }

    public void setRecordsFiltered(Number recordsFiltered) {
        this.recordsFiltered = recordsFiltered;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

}
