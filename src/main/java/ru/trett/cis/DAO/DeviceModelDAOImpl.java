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

package ru.trett.cis.DAO;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import ru.trett.cis.interfaces.DeviceModelDAO;
import ru.trett.cis.models.DeviceModel;

import javax.inject.Inject;
import java.util.List;

@Repository
public class DeviceModelDAOImpl implements DeviceModelDAO {

    private final SessionFactory sessionFactory;

    @Inject
    public DeviceModelDAOImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public DeviceModel findByName(String model) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DeviceModel.class);
        return (DeviceModel) criteria.add(Restrictions.eq("model", model).ignoreCase()).uniqueResult();
    }

    @Override
    public DeviceModel findByTypeAndBrandAndModel(String type, String brand, String model) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DeviceModel.class);
        criteria.createAlias("deviceType", "dt");
        criteria.createAlias("deviceBrand", "db");
        return (DeviceModel) criteria
                .add(Restrictions.eq("dt.type", type).ignoreCase())
                .add(Restrictions.eq("db.brand", brand).ignoreCase())
                .add(Restrictions.eq("model", model).ignoreCase())
                .uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<String> findBrandByType(String deviceType) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DeviceModel.class);
        criteria.createAlias("deviceType", "dt");
        criteria.createAlias("deviceBrand", "db");
        criteria.setProjection(Projections.distinct(Projections.property("db.brand")));
        return criteria.add(Restrictions.eq("dt.type", deviceType).ignoreCase()).list();
    }

    @SuppressWarnings("unchecked")
    public List<DeviceModel> findModelsByTypeAndBrand(String deviceType, String deviceBrand) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DeviceModel.class);
        criteria.createAlias("deviceType", "dt");
        criteria.createAlias("deviceBrand", "db");
        return criteria
                .add(Restrictions.eq("dt.type", deviceType).ignoreCase())
                .add(Restrictions.eq("db.brand", deviceBrand).ignoreCase())
                .list();
    }

}
