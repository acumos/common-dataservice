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
import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionFOM;
import org.acumos.cds.domain.MLPSolution_;
import org.hibernate.AssertionFailure;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Defines methods to search solutions on user-specified fields and yield
 * paginated results. Most methods treat every field as optional, which is the
 * key difference from the methods in the solution repository class, where the
 * parameters are fixed and required.
 * 
 * <P>
 * These two aspects must be observed to get pagination working as expected:
 * <OL>
 * <LI>For all to-many mappings, force use of separate select instead of left
 * outer join. This is far less efficient due to repeated trips to the database,
 * and becomes impossible if you must check properties on mapped (i.e., not the
 * root) entities.</LI>
 * <LI>Specify an unambiguous ordering. This at least is cheap, just add
 * order-by the ID field.</LI>
 * </OL>
 * I'm not the only one who has fought Hibernate to get paginated search
 * results:
 * 
 * <PRE>
 * https://stackoverflow.com/questions/300491/how-to-get-distinct-results-in-hibernate-with-joins-and-row-based-limiting-pagi
 * https://stackoverflow.com/questions/9418268/hibernate-distinct-results-with-pagination
 * https://stackoverflow.com/questions/11038234/pagination-with-hibernate-criteria-and-distinct-root-entity
 * https://stackoverflow.com/questions/42910271/duplicate-records-with-hibernate-joins-and-pagination
 * </PRE>
 * 
 * Many of the queries here check properties of the solution AND associated
 * entities especially revisions. The queries require an inner join and yield a
 * large cross product that Hibernate will coalesce. Because of the joins it's
 * unsafe to apply limit (pagination) parameters at the database. Therefore the
 * approach taken here is to fetch the full result from the database then
 * reduces the result size in the method, which is inefficient.
 *
 */
@Service("solutionSearchService")
@Transactional(readOnly = true)
public class SolutionSearchServiceImpl extends AbstractSearchServiceImpl implements SolutionSearchService {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final String revAlias = "revs";
	private final String artAlias = "arts";
	private final String ownerAlias = "ownr";
	private final String accAlias = "acc";
	private final String descsAlias = "descs";
	private final String docsAlias = "docs";
	private final String solutionId = "solutionId";
	// Aliases used in subquery for required tags
	private final String solAlias = "sol";
	private final String subqAlias = "subsol";
	private final String tagsFieldAlias = "t";
	private final String tagValueField = tagsFieldAlias + ".tag";

	/*
	 * Uses type-safe JPA methods to create a predicate that compares field values
	 * ignoring case.
	 * 
	 * @return Predicate
	 */
	private Predicate createSolutionPredicate(Root<MLPSolution> from, String name, Boolean active, String userId,
			String sourceId, String modelTypeCode, String toolkitTypeCode, String origin, boolean isOr) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		List<Predicate> predicates = new ArrayList<Predicate>();
		if (name != null && !name.isEmpty())
			predicates.add(cb.equal(cb.lower(from.<String>get("name")), name.toLowerCase()));
		if (active != null) {
			Predicate activePredicate = active ? cb.isTrue(from.<Boolean>get("active"))
					: cb.isFalse(from.<Boolean>get("active"));
			predicates.add(activePredicate);
		}
		if (userId != null && !userId.isEmpty())
			predicates.add(cb.equal(cb.lower(from.<String>get("userId")), userId.toLowerCase()));
		if (sourceId != null && !sourceId.isEmpty())
			predicates.add(cb.equal(cb.lower(from.<String>get("sourceId")), sourceId.toLowerCase()));
		if (modelTypeCode != null && !modelTypeCode.isEmpty())
			predicates.add(cb.equal(cb.lower(from.<String>get("modelTypeCode")), modelTypeCode.toLowerCase()));
		if (toolkitTypeCode != null && !toolkitTypeCode.isEmpty())
			predicates.add(cb.equal(cb.lower(from.<String>get("toolkitTypeCode")), toolkitTypeCode.toLowerCase()));
		if (origin != null && !origin.isEmpty())
			predicates.add(cb.equal(cb.lower(from.<String>get("origin")), origin.toLowerCase()));
		if (predicates.isEmpty())
			throw new IllegalArgumentException("Missing query values, must have at least one non-null");
		Predicate[] predArray = new Predicate[predicates.size()];
		predicates.toArray(predArray);
		return isOr ? cb.or(predArray) : cb.and(predArray);
	}

	/*
	 * This criteria only checks properties of the solution entity, not of any
	 * associated entities, so inner joins and their cross products are avoidable.
	 * Therefore it's safe to use limit criteria in the database, which saves the
	 * effort of computing a big result and discarding all but the desired page.
	 * Unfortunately the solution entity has very few properties that are worth
	 * searching, so this is largely worthless.
	 */
	@Override
	public Page<MLPSolution> findSolutions(String name, Boolean active, String userId, String sourceId,
			String modelTypeCode, String toolkitTypeCode, String origin, boolean isOr, Pageable pageable) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();

		// Count rows available
		CriteriaQuery<Long> countQueryDef = cb.createQuery(Long.class);
		Root<MLPSolution> countFrom = countQueryDef.from(MLPSolution.class);
		countQueryDef.select(cb.count(countFrom));
		countQueryDef.where(createSolutionPredicate(countFrom, name, active, userId, sourceId, modelTypeCode,
				toolkitTypeCode, origin, isOr));
		TypedQuery<Long> countQuery = entityManager.createQuery(countQueryDef);
		Long count = countQuery.getSingleResult();
		logger.debug("findSolutions: count {}", count);
		if (count == 0)
			return new PageImpl<>(new ArrayList<>(), pageable, count);

		// Get one page of rows
		CriteriaQuery<MLPSolution> rootQueryDef = cb.createQuery(MLPSolution.class);
		Root<MLPSolution> fromRoot = rootQueryDef.from(MLPSolution.class);
		rootQueryDef.select(fromRoot);
		rootQueryDef.where(createSolutionPredicate(countFrom, name, active, userId, sourceId, modelTypeCode,
				toolkitTypeCode, origin, isOr));
		if (pageable.getSort() != null && !pageable.getSort().isEmpty())
			rootQueryDef.orderBy(buildOrderList(cb, fromRoot, pageable.getSort()));
		TypedQuery<MLPSolution> itemQuery = entityManager.createQuery(rootQueryDef);
		itemQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
		itemQuery.setMaxResults(pageable.getPageSize());
		List<MLPSolution> queryResult = itemQuery.getResultList();
		// Deal with lazy initialization
		for (MLPSolution s : queryResult) {
			Hibernate.initialize(s.getTags());
		}
		logger.debug("findSolutions: result size {}", queryResult.size());

		return new PageImpl<>(queryResult, pageable, count);
	}

	/**
	 * Runs a query on the SolutionFOM entity, returns a page after converting the
	 * resulting FOM solution objects to plain solution objects.
	 * 
	 * @param criteria
	 *                     Criteria to evaluate
	 * @param pageable
	 *                     Page and sort criteria
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private Page<MLPSolution> runSolutionFomQuery(Criteria criteria, Pageable pageable) {

		// Include user's sort request
		if (pageable.getSort() != null)
			super.applySortCriteria(criteria, pageable);
		// Add order on a unique field. Without this the pagination
		// can yield odd results; e.g., request 10 items but only get 8.
		criteria.addOrder(Order.asc(solutionId));
		// Hibernate should coalesce the results, yielding only solutions
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		// Getting the complete result could be brutally expensive.
		List foms = criteria.list();
		if (foms.isEmpty() || foms.size() < pageable.getOffset())
			return new PageImpl<>(new ArrayList<>(), pageable, 0);

		// Get a page of FOM solutions and convert each to plain solution
		List<MLPSolution> items = new ArrayList<>();
		long lastItemInPage = pageable.getOffset() + pageable.getPageSize();
		long limit = lastItemInPage < foms.size() ? lastItemInPage : foms.size();
		for (long i = pageable.getOffset(); i < limit; ++i) {
			Object o = foms.get((int) i);
			if (o instanceof MLPSolutionFOM) {
				MLPSolution s = ((MLPSolutionFOM) o).toMLPSolution();
				// Deal with lazy initialization
				Hibernate.initialize(s.getTags());
				items.add(s);
			} else {
				throw new AssertionFailure("Unexpected type: " + o.getClass().getName());
			}
		}

		return new PageImpl<>(items, pageable, foms.size());
	}

	/**
	 * Finds solutions in the marketplace.
	 * 
	 * Also see comment above about paginated queries.
	 *
	 * This implementation is made yet more awkward due to the requirement to
	 * perform LIKE queries on certain fields.
	 */
	@Override
	public Page<MLPSolution> findPortalSolutions(String[] nameKeywords, String[] descKeywords, boolean active,
			String[] userIds, String[] modelTypeCode, String[] accessTypeCode, String[] tags, String[] authorKeywords,
			String[] publisherKeywords, Pageable pageable) {

		// build the query using FOM to access child attributes
		try (Session session = getSessionFactory().openSession()) {
			Criteria criteria = session.createCriteria(MLPSolutionFOM.class, solAlias);
			// Attributes on the solution
			criteria.add(Restrictions.eq("active", active));
			if (nameKeywords != null && nameKeywords.length > 0)
				criteria.add(buildLikeListCriterion("name", nameKeywords, true));
			if (modelTypeCode != null && modelTypeCode.length > 0)
				criteria.add(buildEqualsListCriterion("modelTypeCode", modelTypeCode));
			if ((accessTypeCode != null && accessTypeCode.length > 0) //
					|| (descKeywords != null && descKeywords.length > 0)
					|| (authorKeywords != null && authorKeywords.length > 0)
					|| (publisherKeywords != null && publisherKeywords.length > 0)) {
				// revisions are optional, but a solution without them is useless
				criteria.createAlias("revisions", revAlias);
				if (accessTypeCode != null && accessTypeCode.length > 0)
					criteria.add(buildEqualsListCriterion(revAlias + ".accessTypeCode", accessTypeCode));
				if (authorKeywords != null && authorKeywords.length > 0)
					criteria.add(buildLikeListCriterion(revAlias + ".authors", authorKeywords, true));
				if (publisherKeywords != null && publisherKeywords.length > 0)
					criteria.add(buildLikeListCriterion(revAlias + ".publisher", publisherKeywords, true));
				if (descKeywords != null && descKeywords.length > 0) {
					criteria.createAlias(revAlias + ".descriptions", descsAlias,
							org.hibernate.sql.JoinType.LEFT_OUTER_JOIN);
					criteria.add(buildLikeListCriterion(descsAlias + ".description", descKeywords, true));
				}
			}
			if (userIds != null && userIds.length > 0) {
				criteria.createAlias("owner", ownerAlias);
				criteria.add(Restrictions.in(ownerAlias + ".userId", userIds));
			}
			if (tags != null && tags.length > 0) {
				// https://stackoverflow.com/questions/51992269/hibernate-java-criteria-query-for-instances-with-multiple-collection-members-lik
				DetachedCriteria subquery = DetachedCriteria.forClass(MLPSolutionFOM.class, subqAlias)
						.add(Restrictions.eqProperty(subqAlias + ".id", solAlias + ".id")) //
						.createAlias("tags", tagsFieldAlias) //
						.add(Restrictions.in(tagValueField, tags)) //
						.setProjection(Projections.count(tagValueField));
				criteria.add(Subqueries.eq((long) tags.length, subquery));
			}
			Page<MLPSolution> result = runSolutionFomQuery(criteria, pageable);
			logger.info("findPortalSolutions: result size={}", result.getNumberOfElements());
			return result;
		}
	}

	public Page<MLPSolution> findPortalSolutionsJpa(String[] nameKeywords, String[] descKeywords, boolean active,
			String[] userIds, String[] modelTypeCode, String[] accessTypeCode, String[] validationStatusCode,
			String[] tags, String[] authorKeywords, String[] publisherKeywords, Pageable pageable) {

		// build the query using FOM class to access child attributes
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<MLPSolutionFOM> rootQueryDef = cb.createQuery(MLPSolutionFOM.class);
		Root<MLPSolutionFOM> fromRoot = rootQueryDef.from(MLPSolutionFOM.class);
		rootQueryDef.select(fromRoot);

		List<Predicate> predicates = new ArrayList<Predicate>();
		// Active is a required parameter
		predicates.add(active ? cb.isTrue(fromRoot.<Boolean>get(MLPSolution_.active))
				: cb.isFalse(fromRoot.<Boolean>get(MLPSolution_.active)));
		// Remaining parameters can be null or empty
		if (nameKeywords != null && nameKeywords.length > 0) {

		}
		if (modelTypeCode != null && modelTypeCode.length > 0)
			predicates.add(cb.in(fromRoot.<String>get(MLPSolution_.modelTypeCode)).in((Object[]) modelTypeCode));
		Predicate[] predArray = new Predicate[predicates.size()];
		predicates.toArray(predArray);
		rootQueryDef.where(predArray);

		TypedQuery<MLPSolutionFOM> typedQuery = entityManager.createQuery(rootQueryDef);
		List<MLPSolutionFOM> queryResult = typedQuery.getResultList();
		return null;

		/*
		 * if ((accessTypeCode != null && accessTypeCode.length > 0) // || (descKeywords
		 * != null && descKeywords.length > 0) || (authorKeywords != null &&
		 * authorKeywords.length > 0) || (publisherKeywords != null &&
		 * publisherKeywords.length > 0)) { <<<<<<< HEAD // revisions are optional, but
		 * a solution without them is useless criteria.createAlias("revisions",
		 * revAlias); if (accessTypeCode != null && accessTypeCode.length > 0)
		 * criteria.add(buildEqualsListCriterion(revAlias + ".accessTypeCode",
		 * accessTypeCode)); if (authorKeywords != null && authorKeywords.length > 0)
		 * criteria.add(buildLikeListCriterion(revAlias + ".authors", authorKeywords,
		 * true)); if (publisherKeywords != null && publisherKeywords.length > 0)
		 * criteria.add(buildLikeListCriterion(revAlias + ".publisher",
		 * publisherKeywords, true)); ======= // Join the revisions. They are optional,
		 * but a solution is useless without. Join<MLPSolutionFOM,
		 * MLPSolutionRevisionFOM> solRevJoin = fromRoot.join(MLPSolutionFOM_.revisions,
		 * JoinType.LEFT); >>>>>>> Upgrade to spring-boot version 2.1.1 if (descKeywords
		 * != null && descKeywords.length > 0) {
		 * 
		 * } }
		 */
	}

	/**
	 * Finds models for a single user, in support of Portal's My Models page.
	 * 
	 * Also see comment above about paginated queries.
	 *
	 * Baffling problem occurred here:
	 * 
	 * Caused by: java.lang.IllegalArgumentException: Can not set java.lang.String
	 * field org.acumos.cds.domain.MLPUser.userId to java.lang.String
	 * 
	 * Which was due to a missing alias for the owner/userId field; i.e., my error,
	 * not some obscure Hibernate issue.
	 */
	@Override
	public Page<MLPSolution> findUserSolutions(String[] nameKeywords, String[] descKeywords, boolean active,
			String userId, String[] modelTypeCode, String[] accessTypeCode, String[] tags, Pageable pageable) {

		try (Session session = getSessionFactory().openSession()) {
			// build the query using FOM to access child attributes
			Criteria criteria = session.createCriteria(MLPSolutionFOM.class, solAlias);
			// Find user's own models AND others via access map which requires outer join
			criteria.createAlias("owner", ownerAlias);
			Criterion owner = Restrictions.eq(ownerAlias + ".userId", userId);
			criteria.createAlias("accessUsers", accAlias, org.hibernate.sql.JoinType.LEFT_OUTER_JOIN);
			Criterion access = Restrictions.eq(accAlias + ".userId", userId);
			criteria.add(Restrictions.or(owner, access));

			// Attributes on the solution
			criteria.add(Restrictions.eq("active", active));
			if (nameKeywords != null && nameKeywords.length > 0)
				criteria.add(buildLikeListCriterion("name", nameKeywords, false));
			if (modelTypeCode != null && modelTypeCode.length > 0)
				criteria.add(buildEqualsListCriterion("modelTypeCode", modelTypeCode));
			if ((accessTypeCode != null && accessTypeCode.length > 0) //
					|| (descKeywords != null && descKeywords.length > 0)) {
				// revisions are optional, but a solution without them is useless
				criteria.createAlias("revisions", revAlias);
				if (accessTypeCode != null && accessTypeCode.length > 0)
					criteria.add(buildEqualsListCriterion(revAlias + ".accessTypeCode", accessTypeCode));
				if (descKeywords != null && descKeywords.length > 0) {
					criteria.createAlias(revAlias + ".descriptions", descsAlias,
							org.hibernate.sql.JoinType.LEFT_OUTER_JOIN);
					criteria.add(buildLikeListCriterion(descsAlias + ".description", descKeywords, false));
				}
			}
			if (tags != null && tags.length > 0) {
				// https://stackoverflow.com/questions/51992269/hibernate-java-criteria-query-for-instances-with-multiple-collection-members-lik
				DetachedCriteria subquery = DetachedCriteria.forClass(MLPSolutionFOM.class, subqAlias)
						.add(Restrictions.eqProperty(subqAlias + ".id", solAlias + ".id")) //
						.createAlias("tags", tagsFieldAlias) //
						.add(Restrictions.in(tagValueField, tags)) //
						.setProjection(Projections.count(tagValueField));
				criteria.add(Subqueries.eq((long) tags.length, subquery));
			}
			Page<MLPSolution> result = runSolutionFomQuery(criteria, pageable);
			logger.info("findUserSolutions: result size={}", result.getNumberOfElements());
			return result;
		}
	}

	/**
	 * This supports federation, which needs to search for solutions modified after
	 * a point in time.
	 * 
	 * Also see comment above about paginated queries.
	 */
	@Override
	public Page<MLPSolution> findSolutionsByModifiedDate(boolean active, String[] accessTypeCode, Date date,
			Pageable pageable) {

		try (Session session = getSessionFactory().openSession()) {
			// build the query using FOM to access child attributes
			Criteria criteria = session.createCriteria(MLPSolutionFOM.class, solAlias);
			// A solution should ALWAYS have revisions.
			criteria.createAlias("revisions", revAlias);
			// A revision should ALWAYS have artifacts
			criteria.createAlias(revAlias + ".artifacts", artAlias);
			// A revision MAY have descriptions
			criteria.createAlias(revAlias + ".descriptions", descsAlias, org.hibernate.sql.JoinType.LEFT_OUTER_JOIN);
			// A revision MAY have documents
			criteria.createAlias(revAlias + ".documents", docsAlias, org.hibernate.sql.JoinType.LEFT_OUTER_JOIN);
			// Attributes on the solution
			criteria.add(Restrictions.eq("active", active));
			if (accessTypeCode != null && accessTypeCode.length > 0)
				criteria.add(Restrictions.in(revAlias + ".accessTypeCode", accessTypeCode));
			// Construct a disjunction to find any updated item.
			// Unfortunately this requires hard-coded field names
			Criterion solModified = Restrictions.ge("modified", date);
			Criterion revModified = Restrictions.ge(revAlias + ".modified", date);
			Criterion descModified = Restrictions.ge(descsAlias + ".modified", date);
			Criterion docModified = Restrictions.ge(docsAlias + ".modified", date);
			Criterion artModified = Restrictions.ge(artAlias + ".modified", date);
			Disjunction itemModifiedAfter = Restrictions.disjunction();
			itemModifiedAfter.add(solModified);
			itemModifiedAfter.add(revModified);
			itemModifiedAfter.add(descModified);
			itemModifiedAfter.add(docModified);
			itemModifiedAfter.add(artModified);
			criteria.add(itemModifiedAfter);

			Page<MLPSolution> result = runSolutionFomQuery(criteria, pageable);
			logger.info("findSolutionsByModifiedDate: result size={}", result.getNumberOfElements());
			return result;
		}

	}

	/*
	 * Low-rent version of full-text search.
	 */
	@Override
	public Page<MLPSolution> findPortalSolutionsByKw(String[] keywords, boolean active, String[] userIds,
			String[] modelTypeCode, String[] accessTypeCode, String[] tags, Pageable pageable) {

		try (Session session = getSessionFactory().openSession()) {
			// build the query using FOM to access child attributes
			Criteria criteria = session.createCriteria(MLPSolutionFOM.class, solAlias);
			criteria.add(Restrictions.eq("active", active));
			// A solution should ALWAYS have revisions.
			criteria.createAlias("revisions", revAlias);
			// Descriptions are optional, so must use outer join
			if (keywords != null && keywords.length > 0) {
				criteria.createAlias(revAlias + ".descriptions", descsAlias,
						org.hibernate.sql.JoinType.LEFT_OUTER_JOIN);
				Disjunction keywordDisjunction = Restrictions.disjunction();
				keywordDisjunction.add(buildLikeListCriterion("name", keywords, false));
				keywordDisjunction.add(buildLikeListCriterion(descsAlias + ".description", keywords, false));
				keywordDisjunction.add(buildLikeListCriterion(revAlias + ".authors", keywords, false));
				keywordDisjunction.add(buildLikeListCriterion(revAlias + ".publisher", keywords, false));
				// Also match on IDs, but exact only
				keywordDisjunction.add(buildEqualsListCriterion("solutionId", keywords));
				keywordDisjunction.add(buildEqualsListCriterion(revAlias + ".revisionId", keywords));
				criteria.add(keywordDisjunction);
			}
			if (modelTypeCode != null && modelTypeCode.length > 0)
				criteria.add(buildEqualsListCriterion("modelTypeCode", modelTypeCode));
			if (accessTypeCode != null && accessTypeCode.length > 0)
				criteria.add(buildEqualsListCriterion(revAlias + ".accessTypeCode", accessTypeCode));
			if (userIds != null && userIds.length > 0) {
				criteria.createAlias("owner", ownerAlias);
				criteria.add(Restrictions.in(ownerAlias + ".userId", userIds));
			}
			if (tags != null && tags.length > 0) {
				// https://stackoverflow.com/questions/51992269/hibernate-java-criteria-query-for-instances-with-multiple-collection-members-lik
				DetachedCriteria subquery = DetachedCriteria.forClass(MLPSolutionFOM.class, subqAlias)
						.add(Restrictions.eqProperty(subqAlias + ".id", solAlias + ".id")) //
						.createAlias("tags", tagsFieldAlias) //
						.add(Restrictions.in(tagValueField, tags)) //
						.setProjection(Projections.count(tagValueField));
				criteria.add(Subqueries.eq((long) tags.length, subquery));
			}
			Page<MLPSolution> result = runSolutionFomQuery(criteria, pageable);
			logger.info("findPortalSolutionsByKw: result size={}", result.getNumberOfElements());
			return result;
		}
	}

	/*
	 * Provides flexible treatment of tags.
	 */
	@Override
	public Page<MLPSolution> findPortalSolutionsByKwAndTags(String[] keywords, boolean active, String[] userIds,
			String[] modelTypeCode, String[] accessTypeCode, String[] allTags, String[] anyTags, Pageable pageable) {

		try (Session session = getSessionFactory().openSession()) {
			// build the query using FOM to access child attributes
			Criteria criteria = session.createCriteria(MLPSolutionFOM.class, solAlias);
			criteria.add(Restrictions.eq("active", active));
			// A solution should ALWAYS have revisions.
			criteria.createAlias("revisions", revAlias);
			// Descriptions are optional, so must use outer join
			if (keywords != null && keywords.length > 0) {
				criteria.createAlias(revAlias + ".descriptions", descsAlias,
						org.hibernate.sql.JoinType.LEFT_OUTER_JOIN);
				Disjunction keywordDisjunction = Restrictions.disjunction();
				keywordDisjunction.add(buildLikeListCriterion("name", keywords, false));
				keywordDisjunction.add(buildLikeListCriterion(descsAlias + ".description", keywords, false));
				keywordDisjunction.add(buildLikeListCriterion(revAlias + ".authors", keywords, false));
				keywordDisjunction.add(buildLikeListCriterion(revAlias + ".publisher", keywords, false));
				// Also match on IDs, but exact only
				keywordDisjunction.add(buildEqualsListCriterion("solutionId", keywords));
				keywordDisjunction.add(buildEqualsListCriterion(revAlias + ".revisionId", keywords));
				criteria.add(keywordDisjunction);
			}
			if (modelTypeCode != null && modelTypeCode.length > 0)
				criteria.add(buildEqualsListCriterion("modelTypeCode", modelTypeCode));
			if (accessTypeCode != null && accessTypeCode.length > 0)
				criteria.add(buildEqualsListCriterion(revAlias + ".accessTypeCode", accessTypeCode));
			if (userIds != null && userIds.length > 0) {
				criteria.createAlias("owner", ownerAlias);
				criteria.add(Restrictions.in(ownerAlias + ".userId", userIds));
			}
			if (allTags != null && allTags.length > 0) {
				// https://stackoverflow.com/questions/51992269/hibernate-java-criteria-query-for-instances-with-multiple-collection-members-lik
				DetachedCriteria allTagsQuery = DetachedCriteria.forClass(MLPSolutionFOM.class, subqAlias)
						.add(Restrictions.eqProperty(subqAlias + ".id", solAlias + ".id")) //
						.createAlias("tags", tagsFieldAlias) //
						.add(Restrictions.in(tagValueField, allTags)) //
						.setProjection(Projections.count(tagValueField));
				criteria.add(Subqueries.eq((long) allTags.length, allTagsQuery));
			}
			if (anyTags != null && anyTags.length > 0) {
				final String subq2Alias = "subsol2";
				final String tag2Alias = "anytag";
				final String tag2ValueField = tag2Alias + ".tag";
				DetachedCriteria anyTagsQuery = DetachedCriteria.forClass(MLPSolutionFOM.class, subq2Alias)
						.add(Restrictions.eqProperty(subq2Alias + ".id", solAlias + ".id")) //
						.createAlias("tags", tag2Alias) //
						.add(Restrictions.in(tag2ValueField, anyTags)).setProjection(Projections.count(tag2ValueField));
				criteria.add(Subqueries.lt(0L, anyTagsQuery));
			}
			Page<MLPSolution> result = runSolutionFomQuery(criteria, pageable);
			logger.debug("findPortalSolutionsByKwAndTags: result size={}", result.getNumberOfElements());
			return result;
		}
	}

}
