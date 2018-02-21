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
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionFOM;
import org.acumos.cds.util.EELFLoggerDelegate;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Hibernate-assisted methods to search solutions.
 */
@Service("solutionSearchService")
@Transactional
public class SolutionSearchServiceImpl extends AbstractSearchServiceImpl implements SolutionSearchService {

	private final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(SolutionSearchServiceImpl.class);

	@Autowired
	private SessionFactory sessionFactory;

	private final String revAlias = "revs";
	private final String artAlias = "arts";
	private final String ownerAlias = "ownr";
	private final String tagAlias = "tag";

	@SuppressWarnings("unchecked")
	@Override
	public Page<MLPSolution> findSolutions(Map<String, ? extends Object> queryParameters, boolean isOr,
			Pageable pageable) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(MLPSolution.class);
		super.buildCriteria(criteria, queryParameters, isOr);

		// Count the total rows
		criteria.setProjection(Projections.rowCount());
		Long count = (Long) criteria.uniqueResult();
		if (count == 0)
			return new PageImpl<>(new ArrayList<>(), pageable, count);

		// Reset the count criteria; add pagination and sort
		criteria.setProjection(null);
		criteria.setResultTransformer(Criteria.ROOT_ENTITY);
		super.applyPageableCriteria(criteria, pageable);

		// Get a page of results and send it back with the total available
		List<MLPSolution> items = criteria.list();
		logger.debug(EELFLoggerDelegate.debugLogger, "findSolutions: result size={}", items.size());
		return new PageImpl<>(items, pageable, count);
	}

	/**
	 * This implementation is awkward for several reasons:
	 * <UL>
	 * <LI>the need to use LIKE queries on certain fields
	 * <LI>the need to search tags, which are not attributes on the entity itself
	 * but instead are implemented via a mapping table</LI>
	 * </UL>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Page<MLPSolution> findPortalSolutions(String[] nameKeywords, String[] descKeywords, boolean active,
			String[] ownerIds, String[] modelTypeCode, String[] accessTypeCode, String[] validationStatusCode,
			String[] tags, Pageable pageable) {

		Criteria solCriteria = sessionFactory.getCurrentSession().createCriteria(MLPSolutionFOM.class);

		// Aliases for subclasses
		solCriteria.createAlias("revisions", revAlias);
		solCriteria.createAlias(revAlias + ".artifacts", artAlias);
		solCriteria.createAlias("owner", ownerAlias);
		solCriteria.createAlias("tags", tagAlias);

		// Attributes on the solution
		solCriteria.add(Restrictions.eq("active", active));
		if (nameKeywords != null && nameKeywords.length > 0)
			solCriteria.add(buildLikeListCriterion("name", nameKeywords));
		if (descKeywords != null && descKeywords.length > 0)
			solCriteria.add(buildLikeListCriterion("description", descKeywords));
		if (modelTypeCode != null && modelTypeCode.length > 0)
			solCriteria.add(Restrictions.in("modelTypeCode", modelTypeCode));
		// Attributes on related types
		if (ownerIds != null && ownerIds.length > 0)
			solCriteria.add(Restrictions.in(ownerAlias + ".userId", ownerIds));
		if (accessTypeCode != null && accessTypeCode.length > 0)
			solCriteria.add(Restrictions.in(revAlias + ".accessTypeCode", accessTypeCode));
		if (validationStatusCode != null && validationStatusCode.length > 0)
			solCriteria.add(Restrictions.in(revAlias + ".validationStatusCode", validationStatusCode));
		if (tags != null && tags.length > 0)
			solCriteria.add(Restrictions.in(tagAlias + ".tag", tags));

		// Count the total rows
		solCriteria.setProjection(Projections.rowCount());
		Long count = (Long) solCriteria.uniqueResult();
		if (count == 0)
			return new PageImpl<>(new ArrayList<>(), pageable, count);

		// Reset the count criteria; add pagination and sort
		solCriteria.setProjection(null);
		solCriteria.setResultTransformer(Criteria.ROOT_ENTITY);
		super.applyPageableCriteria(solCriteria, pageable);

		// Get a page of results
		List<MLPSolutionFOM> items = solCriteria.list();
		if (items.isEmpty())
			throw new RuntimeException("findPortalSolutions: unexpected empty result");

		List<MLPSolution> solutions = new ArrayList<>();
		for (Object item : items) {
			if (item instanceof MLPSolutionFOM)
				solutions.add(((MLPSolutionFOM) item).toMLPSolution());
			else
				logger.error(EELFLoggerDelegate.errorLogger, "Unexpected type: {} ", item.getClass().getName());
		}
		logger.debug(EELFLoggerDelegate.debugLogger, "findPortalSolutions: result size={}", solutions.size());
		return new PageImpl<>(solutions, pageable, count);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Page<MLPSolution> findSolutionsByModifiedDate(boolean active, String[] accessTypeCode,
			String[] validationStatusCode, Date date, Pageable pageable) {

		Criteria solCriteria = sessionFactory.getCurrentSession().createCriteria(MLPSolutionFOM.class);
		solCriteria.createAlias("revisions", revAlias);
		solCriteria.createAlias(revAlias + ".artifacts", artAlias);

		solCriteria.add(Restrictions.eq("active", active));
		if (accessTypeCode != null && accessTypeCode.length > 0)
			solCriteria.add(Restrictions.in(revAlias + ".accessTypeCode", accessTypeCode));
		if (validationStatusCode != null && validationStatusCode.length > 0)
			solCriteria.add(Restrictions.in(revAlias + ".validationStatusCode", validationStatusCode));

		// Construct a disjunction to find any updated item;
		// unfortunately this requires hardcoded field names
		Criterion solModified = Restrictions.ge("modified", date);
		Criterion revModified = Restrictions.ge(revAlias + ".modified", date);
		Criterion artModified = Restrictions.ge(artAlias + ".modified", date);
		Disjunction itemModifiedAfter = Restrictions.disjunction();
		itemModifiedAfter.add(solModified);
		itemModifiedAfter.add(revModified);
		itemModifiedAfter.add(artModified);
		solCriteria.add(itemModifiedAfter);

		// Count the total rows
		solCriteria.setProjection(Projections.rowCount());
		Long count = (Long) solCriteria.uniqueResult();
		if (count == 0)
			return new PageImpl<>(new ArrayList<>(), pageable, count);

		// Remove the count projections
		solCriteria.setProjection(null);
		// Want unique set; cross product yields multiple rows with same solution
		solCriteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		// Add pagination and sort
		super.applyPageableCriteria(solCriteria, pageable);

		// Get a page of results
		List items = solCriteria.list();
		if (items.isEmpty())
			throw new RuntimeException("findSolutionsByModifiedDate: unexpected empty result");

		List<MLPSolution> solutions = new ArrayList<>();
		for (Object item : items) {
			if (item instanceof MLPSolutionFOM)
				solutions.add(((MLPSolutionFOM) item).toMLPSolution());
			else
				logger.error(EELFLoggerDelegate.errorLogger, "Unexpected type: {} ", item.getClass().getName());
		}
		logger.debug(EELFLoggerDelegate.debugLogger, "findSolutionsByModifiedDate: result size={}", solutions.size());
		return new PageImpl<>(solutions, pageable, count);
	}

}
