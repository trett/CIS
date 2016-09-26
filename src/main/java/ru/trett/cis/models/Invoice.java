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
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import ru.trett.cis.utils.JsonDateSerializer;
import ru.trett.cis.utils.StringBridgeImpl;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;

@Entity
@Indexed
@Table(name = "invoice")
public class Invoice extends BaseEntity {

    @Field
    @FieldBridge(impl = StringBridgeImpl.class)
    @Column(name = "date")
    private Timestamp date;

    @OneToMany(mappedBy = "invoice")
    private Set<EmployeeAsset> employeeAssets;

    @Field
    @FieldBridge(impl = StringBridgeImpl.class)
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @IndexedEmbedded
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "issuer_id")
    private User issuer;

    @OneToOne
    @IndexedEmbedded
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @JsonIgnore
    public Set<EmployeeAsset> getEmployeeAssets() {
        return employeeAssets;
    }

    public void setEmployeeAssets(Set<EmployeeAsset> employeeAssets) {
        this.employeeAssets = employeeAssets;
    }

    @JsonSerialize(using = JsonDateSerializer.class)
    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public User getIssuer() {
        return issuer;
    }

    public void setIssuer(User issuer) {
        this.issuer = issuer;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public enum Status {

        DRAFT, PUBLISHED

    }

}
