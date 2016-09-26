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

import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.stereotype.Repository;
import ru.trett.cis.interfaces.CommonDAO;
import ru.trett.cis.models.BaseEntity;
import ru.trett.cis.DTO.TableSearchResultsDTO;

import javax.inject.Inject;
import java.util.List;

@Repository
public class CommonDAOImpl implements CommonDAO {

    private final SessionFactory sessionFactory;

    @Inject
    public CommonDAOImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void buildIndices() {
        FullTextSession fullTextSession = Search.getFullTextSession(sessionFactory.getCurrentSession());
        try {
            fullTextSession.createIndexer().startAndWait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public <T extends BaseEntity> T findById(Class<T> tClass, Long id) {
        return sessionFactory.getCurrentSession().get(tClass, id);
    }

    @Override
    public <T extends BaseEntity> void save(T t) {
        sessionFactory.getCurrentSession().saveOrUpdate(t);
    }

    @Override
    public <T extends BaseEntity> void remove(Class<T> tClass, Long id) {
        Session session = sessionFactory.getCurrentSession();
        T t = session.load(tClass, id);
        if (t != null)
            session.delete(t);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BaseEntity> List<T> list(Class<T> tClass) {
        return sessionFactory
                .getCurrentSession()
                .createQuery(String.format("from %s", tClass.getCanonicalName()))
                .list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BaseEntity> List<T> list(Class<T> tClass, Integer first, Integer length) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(tClass);
        criteria.addOrder(Order.desc("id"));
        criteria.setFirstResult(first);
        criteria.setMaxResults(length);
        return criteria.list();
    }

    @Override
    public <T extends BaseEntity> Number rowCount(Class<T> tClass) {
        return (Number) sessionFactory.getCurrentSession().
                createCriteria(tClass).
                setProjection(Projections.rowCount()).
                uniqueResult();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BaseEntity> TableSearchResultsDTO<T> searchTable(
            Class<T> tClass,
            String[] fields,
            String key,
            Integer first,
            Integer length
    ) {
        FullTextSession fullTextSession = Search.getFullTextSession(sessionFactory.getCurrentSession());
        Sort sort = new Sort(
                SortField.FIELD_SCORE,
                new SortField("id", SortField.Type.STRING, true));

        QueryBuilder queryBuilder =
                fullTextSession
                        .getSearchFactory()
                        .buildQueryBuilder()
                        .forEntity(tClass)
                        .get();

        Query query =
                queryBuilder
                        .keyword()
                        .wildcard()
                        .onFields(fields)
                        .matching(key.toLowerCase())
                        .createQuery();

        org.hibernate.search.FullTextQuery textQuery = fullTextSession.createFullTextQuery(query, tClass);
        textQuery.setSort(sort);

        TableSearchResultsDTO<T> tableSearchResultsDTO = new TableSearchResultsDTO<>();
        tableSearchResultsDTO.setRecordsTotal(textQuery.list().size());
        tableSearchResultsDTO.setRecordsFiltered(textQuery.list().size());
        textQuery.setFirstResult(first);
        textQuery.setMaxResults(length);
        tableSearchResultsDTO.setData(textQuery.list());
        return tableSearchResultsDTO;
    }

    @Override
    public <T extends BaseEntity> void update(T t) {
        sessionFactory.getCurrentSession().update(t);
    }
}
