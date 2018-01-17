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

import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Restrictions;

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
	 *            Map of field name - field value pairs; ignored if null or empty.
	 *            Value may be a scalar or array. An "equals" criterion is created
	 *            for scalar values. An "in" criterion is created for array values.
	 * @param isOr
	 *            If true, treat the query as a disjunction; else as a conjunction.
	 */
	protected void buildCriteria(Criteria criteria, Map<String, ? extends Object> queryParameters, boolean isOr) {
		if (queryParameters != null && queryParameters.size() > 0) {
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
	}
}
