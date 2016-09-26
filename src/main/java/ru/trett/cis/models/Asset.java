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
import org.hibernate.search.annotations.*;
import ru.trett.cis.utils.StringBridgeImpl;

import javax.persistence.*;
import java.util.Set;

@Entity
@Indexed
@Table(name = "asset")
public class Asset extends BaseEntity {

    @IndexedEmbedded
    @ManyToOne
    @JoinColumn(name = "device_model_id")
    private DeviceModel deviceModel;

    @ContainedIn
    @OneToMany(mappedBy = "asset", fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<EmployeeAsset> employeeAssetSet;

    @ContainedIn
    @OneToMany(mappedBy = "asset")
    @JsonIgnore
    private Set<Tracking> trackingSet;

    @Field
    @FieldBridge(impl = StringBridgeImpl.class)
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Field
    @Column(name = "serial_number")
    private String serialNumber;

    @Field
    @Column(name = "inventory_number")
    private String inventoryNumber;

    @Field
    @Column(name = "comment")
    private String comment;

    @OneToOne
    @IndexedEmbedded
    @JoinColumn(name = "employee_id")
    private Employee employee;

    public DeviceModel getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(DeviceModel deviceModelId) {
        this.deviceModel = deviceModelId;
    }

    public Set<EmployeeAsset> getEmployeeAssetSet() {
        return employeeAssetSet;
    }

    public void setEmployeeAssetSet(Set<EmployeeAsset> employeeAssetSet) {
        this.employeeAssetSet = employeeAssetSet;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getInventoryNumber() {
        return inventoryNumber;
    }

    public void setInventoryNumber(String inventoryNumber) {
        this.inventoryNumber = inventoryNumber;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Set<Tracking> getTrackingSet() {
        return trackingSet;
    }

    public void setTrackingSet(Set<Tracking> trackingSet) {
        this.trackingSet = trackingSet;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public enum Status {

        ACTIVE, IN_STOCK, REPAIR, RETIRED

    }

}
