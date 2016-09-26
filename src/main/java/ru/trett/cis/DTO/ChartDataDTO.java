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

import java.util.List;

/**
 * DTO used to delivery data to HighCharts
 *
 * @param <ResultMapper>
 */
public class ChartDataDTO<ResultMapper> {

    private String name;

    private Boolean colorByPoint = true;

    private List<ResultMapper> data;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getColorByPoint() {
        return colorByPoint;
    }

    public void setColorByPoint(Boolean colorByPoint) {
        this.colorByPoint = colorByPoint;
    }

    public List<ResultMapper> getData() {
        return data;
    }

    public void setData(List<ResultMapper> data) {
        this.data = data;
    }
}
