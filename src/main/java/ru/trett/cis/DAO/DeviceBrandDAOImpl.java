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
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import ru.trett.cis.interfaces.DeviceBrandDAO;
import ru.trett.cis.models.DeviceBrand;

import javax.inject.Inject;

@Repository
public class DeviceBrandDAOImpl implements DeviceBrandDAO {

    private final SessionFactory sessionFactory;

    @Inject
    public DeviceBrandDAOImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public DeviceBrand findByName(String brand) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DeviceBrand.class);
        return (DeviceBrand) criteria.add(Restrictions.eq("brand", brand).ignoreCase()).uniqueResult();
    }
}
