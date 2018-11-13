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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.acumos.cds.domain.MLPArtifact;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Hibernate-assisted methods to search Artifact information.
 */
@Service("artifactSearchService")
@Transactional(readOnly = true)
public class ArtifactSearchServiceImpl extends AbstractSearchServiceImpl implements ArtifactSearchService {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	private EntityManager entityManager;

	@SuppressWarnings("unchecked")
	@Override
	public Page<MLPArtifact> findArtifacts(Map<String, ? extends Object> queryParameters, boolean isOr,
			Pageable pageable) {

		try (Session session = getSessionFactory().openSession()) {
			Criteria criteria = session.createCriteria(MLPArtifact.class);
			super.buildCriteria(criteria, queryParameters, isOr);

			// Count the total rows
			criteria.setProjection(Projections.rowCount());
			Long count = (Long) criteria.uniqueResult();
			if (count == 0)
				return new PageImpl<>(new ArrayList<>(), pageable, count);

			// Reset the count criteria; add pagination and sort
			criteria.setProjection(null);
			criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
			super.applyPageableCriteria(criteria, pageable);

			// Get a page of results and send it back with the total available
			List<MLPArtifact> items = criteria.list();
			logger.info("getArtifacts: result size={}", items.size());
			return new PageImpl<>(items, pageable, count);
		}
	}

	private Predicate createArtPredicate(Root<MLPArtifact> fromArt,String artifactTypeCode, String name, String uri, String version,
			String userId, boolean isOr) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		List<Predicate> predicates = new ArrayList<Predicate>();
		if (artifactTypeCode != null && !artifactTypeCode.isEmpty()) 
			predicates.add(cb.equal(cb.lower(fromArt.<String>get("artifactTypeCode")), artifactTypeCode));
		if (name != null && !name.isEmpty()) 
			predicates.add(cb.equal(cb.lower(fromArt.<String>get("name")), name));
		if (uri != null && !uri.isEmpty()) 
			predicates.add(cb.equal(cb.lower(fromArt.<String>get("uri")), uri));
		if (version != null && !version.isEmpty()) 
			predicates.add(cb.equal(cb.lower(fromArt.<String>get("version")), version));
		if (userId != null && !userId.isEmpty()) 
			predicates.add(cb.equal(cb.lower(fromArt.<String>get("userId")), userId));
		if (predicates.isEmpty())
			throw new IllegalArgumentException("Missing query values, must have at least one non-null");
		Predicate[] predArray = new Predicate[predicates.size()];
		predicates.toArray(predArray);
		return isOr? cb.or(predArray) : cb.and(predArray);
	}
	
	@Override
	public Page<MLPArtifact> findArtifacts(String artifactTypeCode, String name, String uri, String version,
			String userId, boolean isOr, Pageable pageable) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		
		// Count rows available
		CriteriaQuery<Long> countQueryDef = cb.createQuery(Long.class);
		Root<MLPArtifact> countFromArt = countQueryDef.from(MLPArtifact.class);
		countQueryDef.select(cb.count(countFromArt));
		countQueryDef.where(createArtPredicate(countFromArt, artifactTypeCode, name, uri, version, userId, isOr));
		TypedQuery<Long> countQuery = entityManager.createQuery(countQueryDef);
		Long count = countQuery.getSingleResult();
		logger.debug("findArtifacts: count {}", count);

		// Get one page of rows
		CriteriaQuery<MLPArtifact> artQueryDef = cb.createQuery(MLPArtifact.class);
		Root<MLPArtifact> fromArt = artQueryDef.from(MLPArtifact.class);
		artQueryDef.select(fromArt);
		artQueryDef.where(createArtPredicate(fromArt, artifactTypeCode, name, uri, version, userId, isOr));

		Sort sort = pageable.getSort();
		if (sort != null && !sort.isEmpty()) {
			List<javax.persistence.criteria.Order> jpaOrderList = new ArrayList<>();
			Iterator<org.springframework.data.domain.Sort.Order> sprOrderIter = sort.iterator();
			while (sprOrderIter.hasNext()) {
				org.springframework.data.domain.Sort.Order sprOrder = sprOrderIter.next();
				if (sprOrder.isAscending())
					jpaOrderList.add(cb.asc(fromArt.get(sprOrder.getProperty())));
				else
					jpaOrderList.add(cb.desc(fromArt.get(sprOrder.getProperty())));
			}
			artQueryDef.orderBy(jpaOrderList);
		}

		TypedQuery<MLPArtifact> artQuery = entityManager.createQuery(artQueryDef);
		artQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
		artQuery.setMaxResults(pageable.getPageSize());
		List<MLPArtifact> queryResult = artQuery.getResultList();
		logger.debug("findArtifacts: result size {}", queryResult.size());

		return null;
	}

}
