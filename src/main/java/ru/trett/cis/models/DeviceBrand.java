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

package ru.trett.cis.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;

@Entity
@Indexed
@Table(name = "device_brand")
public class DeviceBrand extends BaseEntity {

    @Field
    @NotEmpty
    @Column(name = "brand")
    private String brand;

    @ContainedIn
    @OneToMany(mappedBy = "deviceBrand")
    private Set<DeviceModel> deviceModels;

    public DeviceBrand() {}

    public DeviceBrand(String brand) {
        this.brand = brand;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    @JsonIgnore
    public Set<DeviceModel> getDeviceModels() {
        return deviceModels;
    }

    public void setDeviceModels(Set<DeviceModel> deviceModels) {
        this.deviceModels = deviceModels;
    }
}
