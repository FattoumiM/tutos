// Generated by: hibernate/SpringHibernateDaoImpl.vsl in andromda-spring-cartridge.
// license-header java merge-point
/**
 * This is only generated once! It will never be overwritten.
 * You can (and have to!) safely modify it by hand.
 */
package org.phoenixctms.ctsms.domain;

import java.util.Collection;

import org.hibernate.criterion.CriteriaSpecification;
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
 * @see org.phoenixctms.ctsms.domain.InputFieldValue
 */
public class InputFieldValueDaoImpl
		extends InputFieldValueDaoBase {

	private org.hibernate.Criteria createInputFieldValueCriteria() {
		org.hibernate.Criteria inputFieldValueCriteria = this.getSession().createCriteria(InputFieldValue.class);
		return inputFieldValueCriteria;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	protected Collection<String> handleFindTextValues(String textValueInfix, Integer limit) {
		org.hibernate.Criteria inputFieldValueCriteria = createInputFieldValueCriteria();
		CategoryCriterion.apply(inputFieldValueCriteria, new CategoryCriterion(textValueInfix, "truncatedStringValue", MatchMode.ANYWHERE));
		inputFieldValueCriteria.addOrder(Order.asc("truncatedStringValue"));
		inputFieldValueCriteria.setProjection(Projections.distinct(Projections.property("truncatedStringValue")));
		CriteriaUtil.applyLimit(limit, Settings.getIntNullable(SettingCodes.INPUT_FIELD_TEXT_VALUE_AUTOCOMPLETE_DEFAULT_RESULT_LIMIT, Bundle.SETTINGS,
				DefaultSettings.INPUT_FIELD_TEXT_VALUE_AUTOCOMPLETE_DEFAULT_RESULT_LIMIT), inputFieldValueCriteria);
		return inputFieldValueCriteria.list();
	}

	@Override
	protected long handleGetCount(Long selectionSetValueId) {
		org.hibernate.Criteria inputFieldValueCriteria = createInputFieldValueCriteria();
		if (selectionSetValueId != null) {
			org.hibernate.Criteria selectionSetValueCriteria = inputFieldValueCriteria.createCriteria("selectionValues", CriteriaSpecification.INNER_JOIN);
			selectionSetValueCriteria.add(Restrictions.idEq(selectionSetValueId));
		} else {
			inputFieldValueCriteria.add(Restrictions.not(Restrictions.isEmpty("selectionValues")));
		}
		return (Long) inputFieldValueCriteria.setProjection(Projections.rowCount()).uniqueResult();
	}
}