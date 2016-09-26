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

package ru.trett.cis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.jmx.export.annotation.AnnotationJmxAttributeSource;
import org.springframework.jmx.export.assembler.MetadataMBeanInfoAssembler;
import org.springframework.jmx.export.naming.MetadataNamingStrategy;
import ru.trett.cis.interfaces.IDBServiceBean;
import ru.trett.cis.utils.DBServiceBean;
import ru.trett.cis.utils.JsonDateSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class Beans {

    @SuppressWarnings("unchecked")
    @Bean
    public MBeanExporter exporter() {
        MBeanExporter exporter = new MBeanExporter();
        exporter.setAllowEagerInit(true);
        Map<String, Object> beans = new HashMap() {{
            put("cis:name=DBServiceBean", database());
        }};
        exporter.setBeans(beans);
        return exporter;
    }

    @Bean(name = "dbservice")
    public IDBServiceBean database() {
        return new DBServiceBean();
    }

    @Bean
    public AnnotationJmxAttributeSource attributeSource() {
        return new AnnotationJmxAttributeSource();
    }

    @Bean
    public MetadataMBeanInfoAssembler assembler() {
        MetadataMBeanInfoAssembler assembler = new MetadataMBeanInfoAssembler();
        assembler.setAttributeSource(attributeSource());
        return assembler;
    }

    @Bean
    public MetadataNamingStrategy strategy() {
        MetadataNamingStrategy strategy = new MetadataNamingStrategy();
        strategy.setAttributeSource(attributeSource());
        return strategy;
    }

    @Bean
    public JsonDateSerializer dateSerializer() {
        return new JsonDateSerializer();
    }

}
