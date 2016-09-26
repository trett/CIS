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
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.trett.cis.interfaces.AssetDAO;
import ru.trett.cis.models.Asset;
import ru.trett.cis.utils.ResultMapper;

import javax.inject.Inject;
import java.util.List;

@Repository
public class AssetDAOImpl implements AssetDAO {

    private final SessionFactory sessionFactory;

    @Inject
    public AssetDAOImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Asset> assetListByIds(List<Long> ids) {
        Query query = sessionFactory.getCurrentSession().createQuery("FROM Asset item WHERE item.id IN (:ids)");
        query.setParameterList("ids", ids);
        return query.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Asset> getAssetsBySerialNumber(String serialNumber) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Asset.class);
        return criteria.add(Restrictions.eq("serialNumber", serialNumber).ignoreCase()).list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Asset> getAssetByInventoryNumber(String inventoryNumber) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Asset.class);
        return criteria.add(Restrictions.eq("inventoryNumber", inventoryNumber).ignoreCase()).list();
    }

    @Override
    @Transactional(readOnly = true)
    public Number getDeviceCountById(Long deviceModelId) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Asset.class);
        return (Number) criteria
                .add(Restrictions.eq("deviceModel.id", deviceModelId))
                .setProjection(Projections.rowCount())
                .uniqueResult();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ResultMapper> deviceModelGroupAndCount() {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Asset.class);
        return criteria
                .createAlias("deviceModel", "dm")
                .setProjection(Projections.projectionList()
                        .add(Projections.groupProperty("deviceModel.id"))
                        .add(Projections.rowCount(), "data")
                        .add(Projections.property("dm.model"), "name")
                )
                .setResultTransformer(Transformers.aliasToBean(ResultMapper.class))
                .setCacheable(true)
                .list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ResultMapper> assetGroupByStatusAndCount() {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Asset.class);
        return criteria.setProjection(Projections.projectionList()
                .add(
                        Projections
                                .sqlGroupProjection(
                                        "status as st",
                                        "st",
                                        new String[]{"st"},
                                        new Type[]{StandardBasicTypes.STRING}), "name")
                .add(Projections.rowCount(), "data"))
                .setResultTransformer(Transformers.aliasToBean(ResultMapper.class))
                .list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Asset> getAssetsWithDeviceModelId(Long id) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Asset.class);
        return criteria.add(Restrictions.eq("deviceModel.id", id)).list();
    }

}
