/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
 * ===================================================================================
 * This Acumos software file is distributed by AT&T and Tech Mahindra
 * under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * This file is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ===============LICENSE_END=========================================================
 */

package org.acumos.cds.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Factors code out of search implementations
 */
public abstract class AbstractSearchServiceImpl {

	/**
	 * Populates a criteria object for Hibernate.
	 * 
	 * @param criteria
	 *            Criteria object to extend
	 * @param queryParameters
	 *            Map of field name - field value pairs. Value may be a scalar or
	 *            array. An "equals" criterion is created for scalar values. An "in"
	 *            criterion is created for array values.
	 * @param isOr
	 *            If true, treat the query as a disjunction; else as a conjunction.
	 */
	protected void buildCriteria(Criteria criteria, Map<String, ? extends Object> queryParameters, boolean isOr) {
		Junction junction = isOr ? Restrictions.disjunction() : Restrictions.conjunction();
		criteria.add(junction);
		for (Map.Entry<String, ? extends Object> entry : queryParameters.entrySet()) {
			Criterion criterion = null;
			if (entry.getValue().getClass().isArray()) {
				Object[] array = (Object[]) entry.getValue();
				criterion = Restrictions.in(entry.getKey(), array);
			} else {
				criterion = Restrictions.eq(entry.getKey(), entry.getValue());
			}
			junction.add(criterion);
		}
	}

	/**
	 * Builds a disjunction ("OR") criterion to check exact match of any value in
	 * the list, with special handling for null.
	 * 
	 * @param fieldName
	 *            POJO field name
	 * @param values
	 *            Set of values; null is permitted
	 * @return Criterion
	 */
	protected Criterion buildEqualsListCriterion(String fieldName, Object[] values) {
		Junction junction = Restrictions.disjunction();
		for (Object v : values) {
			if (v == null)
				junction.add(Restrictions.isNull(fieldName));
			else
				junction.add(Restrictions.eq(fieldName, v));
		}
		return junction;
	}

	/**
	 * Builds a disjunction ("OR") criterion to check approximate match of any value
	 * in the list; null is not permitted.
	 * 
	 * @param fieldName
	 *            POJO field name
	 * @param values
	 *            String values; null is forbidden
	 * @return Criterion
	 */
	protected Criterion buildLikeListCriterion(String fieldName, String[] values) {
		Junction junction = Restrictions.disjunction();
		for (String v : values) {
			if (v == null)
				throw new IllegalArgumentException("Null not permitted in value list");
			else
				junction.add(Restrictions.like(fieldName, '%' + v + '%'));
		}
		return junction;
	}

	/**
	 * Executes the query to count total rows, and if nonzero, executes the query
	 * again to get one page of results. Must count so the paged result can report
	 * the total number of pages available.
	 * 
	 * @param criteria
	 *            Query criteria
	 * @param pageable
	 *            Page size and sort criteria
	 * @return
	 */
	protected List<?> getPageOfResults(Criteria criteria, Pageable pageable) {

		// Count the total rows
		criteria.setProjection(Projections.rowCount());
		Long count = (Long) criteria.uniqueResult();
		if (count == 0)
			return new ArrayList<Object>();

		// Reset the criteria, add pagination and sort
		criteria.setProjection(null);
		criteria.setResultTransformer(Criteria.ROOT_ENTITY);
		criteria.setFirstResult(pageable.getOffset());
		criteria.setMaxResults(pageable.getPageSize());
		if (pageable.getSort() != null) {
			Iterator<Sort.Order> orderIter = pageable.getSort().iterator();
			while (orderIter.hasNext()) {
				Sort.Order sortOrder = orderIter.next();
				Order order;
				if (sortOrder.isAscending())
					order = Order.asc(sortOrder.getProperty());
				else
					order = Order.desc(sortOrder.getProperty());
				criteria.addOrder(order);
			}
		}
		return criteria.list();
	}

	/**
	 * Adds page-request criteria to the criteria.
	 * 
	 * @param criteria
	 *            Criteria
	 * @param pageable
	 *            Pageable
	 */
	protected void applyPageableCriteria(Criteria criteria, Pageable pageable) {
		criteria.setFirstResult(pageable.getOffset());
		criteria.setMaxResults(pageable.getPageSize());
		if (pageable.getSort() != null) {
			Iterator<Sort.Order> orderIter = pageable.getSort().iterator();
			while (orderIter.hasNext()) {
				Sort.Order sortOrder = orderIter.next();
				Order order;
				if (sortOrder.isAscending())
					order = Order.asc(sortOrder.getProperty());
				else
					order = Order.desc(sortOrder.getProperty());
				criteria.addOrder(order);
			}
		}
	}

}
