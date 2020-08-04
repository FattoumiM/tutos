// Generated by: hibernate/SpringHibernateDaoImpl.vsl in andromda-spring-cartridge.
// license-header java merge-point
/**
 * This is only generated once! It will never be overwritten.
 * You can (and have to!) safely modify it by hand.
 */
package org.phoenixctms.ctsms.domain;

import java.util.Collection;

import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import org.phoenixctms.ctsms.query.CategoryCriterion;
import org.phoenixctms.ctsms.query.CriteriaUtil;
import org.phoenixctms.ctsms.util.DefaultSettings;
import org.phoenixctms.ctsms.util.SettingCodes;
import org.phoenixctms.ctsms.util.Settings;
import org.phoenixctms.ctsms.util.Settings.Bundle;

/**
 * @see org.phoenixctms.ctsms.domain.Title
 */
public class TitleDaoImpl
		extends TitleDaoBase {

	@Override
	protected Collection<String> handleFindTitles(String titlePrefix, Integer limit)
			throws Exception {
		org.hibernate.Criteria titleCriteria = this.getSession().createCriteria(Title.class);
		titleCriteria.setCacheable(true);
		CategoryCriterion.apply(titleCriteria, new CategoryCriterion(titlePrefix, "title", MatchMode.START));
		titleCriteria.add(Restrictions.not(Restrictions.or(Restrictions.eq("title", ""), Restrictions.isNull("title"))));
		titleCriteria.addOrder(Order.asc("title"));
		titleCriteria.setProjection(Projections.distinct(Projections.property("title")));
		CriteriaUtil.applyLimit(limit,
				Settings.getIntNullable(SettingCodes.TITLE_AUTOCOMPLETE_DEFAULT_RESULT_LIMIT, Bundle.SETTINGS, DefaultSettings.TITLE_AUTOCOMPLETE_DEFAULT_RESULT_LIMIT),
				titleCriteria);
		return titleCriteria.list();
	}
}