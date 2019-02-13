package org.acumos.cds.migrate.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class CarouselSlide {

	public String name;
	public Boolean graphicImgEnabled;
	public String headline;
	public String infoImageAling; // sic
	public Integer number;
	public Boolean slideEnabled;
	public String supportingContent;
	public String textAling; // sic

	// For images in CMS
	public String bgImageUrl;
	// For images in CMS
	public String infoImageUrl;
	// For images in CDS
	public Integer uniqueKey;
	// For images in CDS
	public Boolean hasInfographic;
	// For images in CDS
	public String bgImgKey;
	// For images in CDS
	public String infoImgKey;

}
