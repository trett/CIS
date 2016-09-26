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
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.util.Set;

@Entity
@Indexed
@Table(name = "device_model")
public class DeviceModel extends BaseEntity {

    @Field
    @NotEmpty
    @Column(name = "model")
    private String model;

    @OneToMany(mappedBy = "deviceModel")
    private Set<Asset> assets;

    @Field
    @Column(name = "item_number")
    private String itemNumber;

    @IndexedEmbedded
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "device_brand_id")
    private DeviceBrand deviceBrand;

    @IndexedEmbedded
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "device_type_id")
    private DeviceType deviceType;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getItemNumber() {
        return itemNumber;
    }

    public void setItemNumber(String itemNumber) {
        this.itemNumber = itemNumber;
    }

    @JsonIgnore
    public Set<Asset> getAssets() {
        return assets;
    }

    public void setAssets(Set<Asset> assets) {
        this.assets = assets;
    }

    public DeviceBrand getDeviceBrand() {
        return deviceBrand;
    }

    public void setDeviceBrand(DeviceBrand deviceBrand) {
        this.deviceBrand = deviceBrand;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }
}
