package org.acumos.cds.specification;

import java.util.ArrayList;
import java.util.List;

import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.query.SearchCriterion;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;

/**
 * Supports searches for an MLPSolution by field value.
 * 
 * Inspired by
 * http://www.baeldung.com/rest-api-search-language-spring-data-specifications
 */
public class MLPSolutionSpecificationBuilder {

	private final List<SearchCriterion> criteria;

	public MLPSolutionSpecificationBuilder() {
		criteria = new ArrayList<SearchCriterion>();
	}

	public MLPSolutionSpecificationBuilder with(SearchCriterion criterion) {
		criteria.add(criterion);
		return this;
	}

	/**
	 * Builds a query, honoring the logical predicates in the criteria
	 * 
	 * @return Specification that implements the search criteria
	 */
	public Specification<MLPSolution> build() {
		if (criteria.size() == 0)
			throw new IllegalArgumentException("no criteria");

		// The Specifications class is deprecated in v2.0, but using the recommended
		// Specification#where() yields
		// java.lang.NoSuchMethodError:
		// org.springframework.data.jpa.domain.Specification.where(..)
		Specification<MLPSolution> result = new MLPSolutionSpecification(criteria.get(0));
		for (int i = 1; i < criteria.size(); i++) {
			SearchCriterion sc = criteria.get(i);
			if (sc.isOrPredicate() != null && sc.isOrPredicate())
				result = Specifications.where(result).or(new MLPSolutionSpecification(criteria.get(i)));
			else
				result = Specifications.where(result).and(new MLPSolutionSpecification(criteria.get(i)));
		}

		return result;
	}

}
