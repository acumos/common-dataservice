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

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.acumos.cds.domain.MLPTaskStepResult;
import org.acumos.cds.domain.MLPTaskStepResult_;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("stepResultSearchService")
@Transactional(readOnly = true)
public class StepResultSearchServiceImpl extends AbstractSearchServiceImpl implements StepResultSearchService {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	/*
	 * Uses type-safe JPA methods to create a predicate that compares field values
	 * ignoring case.
	 * 
	 * @return Predicate
	 */
	private Predicate createStepResultPredicate(Root<MLPTaskStepResult> from, Long taskId, String name,
			String statusCode, boolean isOr) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		List<Predicate> predicates = new ArrayList<>();
		if (taskId != null)
			predicates.add(cb.equal(from.<Long>get(MLPTaskStepResult_.taskId), taskId));
		if (name != null && !name.isEmpty())
			predicates.add(cb.equal(cb.lower(from.<String>get(MLPTaskStepResult_.name)), name.toLowerCase()));
		if (statusCode != null && !statusCode.isEmpty())
			predicates
					.add(cb.equal(cb.lower(from.<String>get(MLPTaskStepResult_.statusCode)), statusCode.toLowerCase()));
		if (predicates.isEmpty())
			throw new IllegalArgumentException("Missing query values, must have at least one non-null");
		Predicate[] predArray = new Predicate[predicates.size()];
		predicates.toArray(predArray);
		return isOr ? cb.or(predArray) : cb.and(predArray);
	}

	/*
	 * Use JPA in Spring-Boot version 2.1
	 */
	@Override
	public Page<MLPTaskStepResult> findStepResults(Long taskId, String name, String statusCode, boolean isOr,
			Pageable pageable) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();

		// Count rows available
		CriteriaQuery<Long> countQueryDef = cb.createQuery(Long.class);
		countQueryDef.distinct(true);
		Root<MLPTaskStepResult> countFrom = countQueryDef.from(MLPTaskStepResult.class);
		countQueryDef.select(cb.count(countFrom));
		countQueryDef.where(createStepResultPredicate(countFrom, taskId, name, statusCode, isOr));
		TypedQuery<Long> countQuery = entityManager.createQuery(countQueryDef);
		Long count = countQuery.getSingleResult();
		logger.debug("findStepResults: count {}", count);
		if (count == 0)
			return new PageImpl<>(new ArrayList<>(), pageable, count);

		// Get one page of rows
		CriteriaQuery<MLPTaskStepResult> rootQueryDef = cb.createQuery(MLPTaskStepResult.class);
		// Coalesce any duplicates due to joins
		rootQueryDef.distinct(true);
		Root<MLPTaskStepResult> fromRoot = rootQueryDef.from(MLPTaskStepResult.class);
		rootQueryDef.select(fromRoot);
		rootQueryDef.where(createStepResultPredicate(countFrom, taskId, name, statusCode, isOr));
		if (pageable.getSort() != null && !pageable.getSort().isEmpty())
			rootQueryDef.orderBy(buildOrderList(cb, fromRoot, pageable.getSort()));
		TypedQuery<MLPTaskStepResult> itemQuery = entityManager.createQuery(rootQueryDef);
		itemQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
		itemQuery.setMaxResults(pageable.getPageSize());
		List<MLPTaskStepResult> queryResult = itemQuery.getResultList();
		logger.debug("findStepResults: result size {}", queryResult.size());

		return new PageImpl<>(queryResult, pageable, count);
	}

}
