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
@Table(name = "employee")
public class Employee extends BaseEntity {

    @OneToMany(mappedBy = "employee")
    @JsonIgnore
    private Set<EmployeeAsset> employeeAssetSet;

    @Field
    @NotEmpty
    @Column(name = "first_name")
    private String firstName;

    @Field
    @NotEmpty
    @Column(name = "last_name")
    private String lastName;

    @Field
    @Column(name = "middle_name")
    private String middleName;

    @Field
    @NotEmpty
    @Column(name = "position")
    private String position;

    @IndexedEmbedded
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "cost_center_id")
    private CostCenter costCenter;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public CostCenter getCostCenter() {
        return costCenter;
    }

    public void setCostCenter(CostCenter costCenter) {
        this.costCenter = costCenter;
    }

    public Set<EmployeeAsset> getEmployeeAssetSet() {
        return employeeAssetSet;
    }

    public void setEmployeeAssetSet(Set<EmployeeAsset> employeeAssetSet) {
        this.employeeAssetSet = employeeAssetSet;
    }

}
