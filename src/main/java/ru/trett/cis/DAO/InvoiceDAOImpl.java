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
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import org.springframework.stereotype.Repository;
import ru.trett.cis.interfaces.InvoiceDAO;
import ru.trett.cis.models.Invoice;
import ru.trett.cis.utils.ResultMapper;

import javax.inject.Inject;
import java.util.List;

@Repository
public class InvoiceDAOImpl implements InvoiceDAO {

    private final SessionFactory sessionFactory;

    @Inject
    public InvoiceDAOImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ResultMapper> recordsPerDay() {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Invoice.class);
        return criteria
                .setProjection(
                        Projections
                                .projectionList()
                                .add(Projections
                                        .sqlGroupProjection(
                                                "date(date) as creationDate",
                                                "creationDate",
                                                new String[]{"creationDate"},
                                                new Type[]{StandardBasicTypes.STRING}), "name")
                                .add(Projections.rowCount(), "data"))
                .setResultTransformer(Transformers.aliasToBean(ResultMapper.class))
                .setCacheable(true)
                .list();
    }
}
