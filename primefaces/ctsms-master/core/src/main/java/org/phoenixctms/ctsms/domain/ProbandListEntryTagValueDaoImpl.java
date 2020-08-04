// Generated by: hibernate/SpringHibernateDaoImpl.vsl in andromda-spring-cartridge.
// license-header java merge-point
/**
 * This is only generated once! It will never be overwritten.
 * You can (and have to!) safely modify it by hand.
 */
package org.phoenixctms.ctsms.domain;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.phoenixctms.ctsms.compare.VOIDComparator;
import org.phoenixctms.ctsms.enumeration.InputFieldType;
import org.phoenixctms.ctsms.query.CriteriaUtil;
import org.phoenixctms.ctsms.query.SubCriteriaMap;
import org.phoenixctms.ctsms.util.CommonUtil;
import org.phoenixctms.ctsms.util.CoreUtil;
import org.phoenixctms.ctsms.util.DefaultSettings;
import org.phoenixctms.ctsms.util.L10nUtil;
import org.phoenixctms.ctsms.util.L10nUtil.Locales;
import org.phoenixctms.ctsms.util.ServiceUtil;
import org.phoenixctms.ctsms.util.SettingCodes;
import org.phoenixctms.ctsms.util.Settings;
import org.phoenixctms.ctsms.util.Settings.Bundle;
import org.phoenixctms.ctsms.vo.InputFieldSelectionSetValueJsonVO;
import org.phoenixctms.ctsms.vo.InputFieldSelectionSetValueOutVO;
import org.phoenixctms.ctsms.vo.PSFVO;
import org.phoenixctms.ctsms.vo.ProbandListEntryOutVO;
import org.phoenixctms.ctsms.vo.ProbandListEntryTagOutVO;
import org.phoenixctms.ctsms.vo.ProbandListEntryTagValueInVO;
import org.phoenixctms.ctsms.vo.ProbandListEntryTagValueJsonVO;
import org.phoenixctms.ctsms.vo.ProbandListEntryTagValueOutVO;
import org.phoenixctms.ctsms.vo.UserOutVO;

/**
 * @see ProbandListEntryTagValue
 */
public class ProbandListEntryTagValueDaoImpl
		extends ProbandListEntryTagValueDaoBase {

	private final static VOIDComparator SELECTION_SET_VALUE_OUT_VO_ID_COMPARATOR = new VOIDComparator<InputFieldSelectionSetValueOutVO>(false);
	private final static VOIDComparator SELECTION_SET_VALUE_JSON_VO_ID_COMPARATOR = new VOIDComparator<InputFieldSelectionSetValueJsonVO>(false);

	private static void applySortOrders(org.hibernate.Criteria listEntryTagCriteria) {
		if (listEntryTagCriteria != null) {
			listEntryTagCriteria.addOrder(Order.asc("trial"));
			listEntryTagCriteria.addOrder(Order.asc("position"));
		}
	}

	private org.hibernate.Criteria createListEntryTagValueCriteria() {
		org.hibernate.Criteria listEntryTagValueCriteria = this.getSession().createCriteria(ProbandListEntryTagValue.class);
		return listEntryTagValueCriteria;
	}

	private org.hibernate.Criteria createProbandListEntryTagCriteria(Long probandListEntryId) {
		ProbandListEntry listEntry = this.getProbandListEntryDao().load(probandListEntryId);
		org.hibernate.Criteria listEntryTagCriteria = this.getSession().createCriteria(ProbandListEntryTag.class,
				ServiceUtil.PROBAND_LIST_ENTRY_TAG_VALUE_DAO_PROBAND_LIST_ENTRY_TAG_ALIAS);
		listEntryTagCriteria.add(Restrictions.eq("trial.id", listEntry.getTrial().getId().longValue()));
		org.hibernate.Criteria listEntryTagValueCriteria = listEntryTagCriteria.createCriteria("tagValues",
				ServiceUtil.PROBAND_LIST_ENTRY_TAG_VALUE_DAO_PROBAND_LIST_ENTRY_TAG_VALUE_ALIAS,
				CriteriaSpecification.LEFT_JOIN,
				Restrictions.eq(ServiceUtil.PROBAND_LIST_ENTRY_TAG_VALUE_DAO_PROBAND_LIST_ENTRY_TAG_VALUE_ALIAS + ".listEntry.id", probandListEntryId.longValue()));
		return listEntryTagCriteria;
	}

	@Override
	protected Collection<ProbandListEntryTagValue> handleFindByListEntryField(
			Long probandListEntryId, Long inputFieldId)
			throws Exception {
		org.hibernate.Criteria listEntryTagValueCriteria = createListEntryTagValueCriteria();
		if (probandListEntryId != null) {
			listEntryTagValueCriteria.add(Restrictions.eq("listEntry.id", probandListEntryId.longValue()));
		}
		if (inputFieldId != null) {
			listEntryTagValueCriteria.createCriteria("tag", CriteriaSpecification.INNER_JOIN).add(Restrictions.eq("field.id", inputFieldId.longValue()));
		}
		return listEntryTagValueCriteria.list();
	}

	@Override
	protected Collection<Map> handleFindByListEntryJs(Long probandListEntryId, boolean sort, Boolean js, PSFVO psf) throws Exception {
		org.hibernate.Criteria listEntryTagCriteria = createProbandListEntryTagCriteria(probandListEntryId);
		if (js != null) {
			if (js) {
				listEntryTagCriteria.add(Restrictions.and(Restrictions.ne("jsVariableName", ""), Restrictions.isNotNull("jsVariableName")));
			} else {
				listEntryTagCriteria.add(Restrictions.or(Restrictions.eq("jsVariableName", ""), Restrictions.isNull("jsVariableName")));
			}
		}
		if (psf != null) {
			SubCriteriaMap criteriaMap = new SubCriteriaMap(ProbandListEntryTag.class, listEntryTagCriteria);
			// clear sort and filters?
			CriteriaUtil.applyPSFVO(criteriaMap, psf);
		}
		if (sort) {
			applySortOrders(listEntryTagCriteria);
		}
		listEntryTagCriteria.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP);
		return listEntryTagCriteria.list();
	}

	@Override
	protected Collection<ProbandListEntryTagValue> handleFindByListEntryListEntryTag(
			Long probandListEntryId, Long probandListEntryTagId)
			throws Exception {
		org.hibernate.Criteria listEntryTagValueCriteria = createListEntryTagValueCriteria();
		if (probandListEntryId != null) {
			listEntryTagValueCriteria.add(Restrictions.eq("listEntry.id", probandListEntryId.longValue()));
		}
		if (probandListEntryTagId != null) {
			listEntryTagValueCriteria.add(Restrictions.eq("tag.id", probandListEntryTagId.longValue()));
		}
		return listEntryTagValueCriteria.list();
	}

	@Override
	protected long handleGetCount(
			Long probandListEntryId, Long probandListEntryTagId)
			throws Exception {
		org.hibernate.Criteria listEntryTagValueCriteria = createListEntryTagValueCriteria();
		if (probandListEntryId != null) {
			listEntryTagValueCriteria.add(Restrictions.eq("listEntry.id", probandListEntryId.longValue()));
		}
		if (probandListEntryTagId != null) {
			listEntryTagValueCriteria.add(Restrictions.eq("tag.id", probandListEntryTagId.longValue()));
		}
		return (Long) listEntryTagValueCriteria.setProjection(Projections.rowCount()).uniqueResult();
	}

	@Override
	protected long handleGetCountByField(Long inputFieldId) throws Exception {
		org.hibernate.Criteria listEntryTagValueCriteria = createListEntryTagValueCriteria();
		if (inputFieldId != null) {
			org.hibernate.Criteria tagCriteria = listEntryTagValueCriteria.createCriteria("tag", CriteriaSpecification.INNER_JOIN);
			tagCriteria.add(Restrictions.eq("field.id", inputFieldId.longValue()));
		}
		return (Long) listEntryTagValueCriteria.setProjection(Projections.rowCount()).uniqueResult();
	}

	/**
	 * Retrieves the entity object that is associated with the specified value object
	 * from the object store. If no such entity object exists in the object store,
	 * a new, blank entity is created
	 */
	private ProbandListEntryTagValue loadProbandListEntryTagValueFromProbandListEntryTagValueInVO(ProbandListEntryTagValueInVO probandListEntryTagValueInVO) {
		ProbandListEntryTagValue probandListEntryTagValue = null;
		Long id = probandListEntryTagValueInVO.getId();
		if (id != null) {
			probandListEntryTagValue = this.load(id);
		}
		if (probandListEntryTagValue == null) {
			probandListEntryTagValue = ProbandListEntryTagValue.Factory.newInstance();
		}
		return probandListEntryTagValue;
	}

	/**
	 * Retrieves the entity object that is associated with the specified value object
	 * from the object store. If no such entity object exists in the object store,
	 * a new, blank entity is created
	 */
	private ProbandListEntryTagValue loadProbandListEntryTagValueFromProbandListEntryTagValueJsonVO(ProbandListEntryTagValueJsonVO probandListEntryTagValueJsonVO) {
		throw new UnsupportedOperationException(
				"org.phoenixctms.ctsms.domain.loadProbandListEntryTagValueFromProbandListEntryTagValueJsonVO(ProbandListEntryTagValueJsonVO) not yet implemented.");
	}

	/**
	 * Retrieves the entity object that is associated with the specified value object
	 * from the object store. If no such entity object exists in the object store,
	 * a new, blank entity is created
	 */
	private ProbandListEntryTagValue loadProbandListEntryTagValueFromProbandListEntryTagValueOutVO(ProbandListEntryTagValueOutVO probandListEntryTagValueOutVO) {
		ProbandListEntryTagValue probandListEntryTagValue = this.load(probandListEntryTagValueOutVO.getId());
		if (probandListEntryTagValue == null) {
			probandListEntryTagValue = ProbandListEntryTagValue.Factory.newInstance();
		}
		return probandListEntryTagValue;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public ProbandListEntryTagValue probandListEntryTagValueInVOToEntity(ProbandListEntryTagValueInVO probandListEntryTagValueInVO) {
		ProbandListEntryTagValue entity = this.loadProbandListEntryTagValueFromProbandListEntryTagValueInVO(probandListEntryTagValueInVO);
		this.probandListEntryTagValueInVOToEntity(probandListEntryTagValueInVO, entity, true);
		return entity;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void probandListEntryTagValueInVOToEntity(
			ProbandListEntryTagValueInVO source,
			ProbandListEntryTagValue target,
			boolean copyIfNull) {
		super.probandListEntryTagValueInVOToEntity(source, target, copyIfNull);
		Long listEntryId = source.getListEntryId();
		Long tagId = source.getTagId();
		if (listEntryId != null) {
			ProbandListEntry listEntry = this.getProbandListEntryDao().load(listEntryId);
			target.setListEntry(listEntry);
			listEntry.addTagValues(target);
		} else if (copyIfNull) {
			ProbandListEntry listEntry = target.getListEntry();
			target.setListEntry(null);
			if (listEntry != null) {
				listEntry.removeTagValues(target);
			}
		}
		if (tagId != null) {
			ProbandListEntryTag tag = this.getProbandListEntryTagDao().load(tagId);
			target.setTag(tag);
			tag.addTagValues(target);
		} else if (copyIfNull) {
			ProbandListEntryTag tag = target.getTag();
			target.setTag(null);
			if (tag != null) {
				tag.removeTagValues(target);
			}
		}
		InputFieldValue value = target.getValue();
		if (value == null) {
			value = InputFieldValue.Factory.newInstance();
			target.setValue(value);
		}
		if (copyIfNull || source.getTextValue() != null) {
			value.setStringValue(source.getTextValue());
			value.setTruncatedStringValue(CommonUtil.truncateStringValue(source.getTextValue(), Settings.getIntNullable(SettingCodes.INPUT_FIELD_TRUNCATED_STRING_VALUE_MAX_LENGTH,
					Bundle.SETTINGS, DefaultSettings.INPUT_FIELD_TRUNCATED_STRING_VALUE_MAX_LENGTH)));
		}
		value.setBooleanValue(source.getBooleanValue());
		if (copyIfNull || source.getLongValue() != null) {
			value.setLongValue(source.getLongValue());
		}
		if (copyIfNull || source.getFloatValue() != null) {
			value.setFloatValue(source.getFloatValue());
		}
		if (copyIfNull || source.getDateValue() != null) {
			value.setDateValue(CoreUtil.forceDate(source.getDateValue()));
		}
		if (copyIfNull || source.getTimestampValue() != null) {
			value.setTimestampValue((source.getTimestampValue() == null ? null : new Timestamp(source.getTimestampValue().getTime())));
		}
		if (copyIfNull || source.getTimeValue() != null) {
			value.setTimeValue(CoreUtil.forceDate(source.getTimeValue()));
		}
		if (copyIfNull || source.getInkValues() != null) {
			value.setInkValue(source.getInkValues());
		}
		Collection selectionValueIds;
		if ((selectionValueIds = source.getSelectionValueIds()).size() > 0 || copyIfNull) {
			value.setSelectionValues(toInputFieldSelectionSetValueSet(selectionValueIds));
		}
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public ProbandListEntryTagValue probandListEntryTagValueJsonVOToEntity(ProbandListEntryTagValueJsonVO probandListEntryTagValueJsonVO) {
		ProbandListEntryTagValue entity = this.loadProbandListEntryTagValueFromProbandListEntryTagValueJsonVO(probandListEntryTagValueJsonVO);
		this.probandListEntryTagValueJsonVOToEntity(probandListEntryTagValueJsonVO, entity, true);
		return entity;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void probandListEntryTagValueJsonVOToEntity(
			ProbandListEntryTagValueJsonVO source,
			ProbandListEntryTagValue target,
			boolean copyIfNull) {
		super.probandListEntryTagValueJsonVOToEntity(source, target, copyIfNull);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public ProbandListEntryTagValue probandListEntryTagValueOutVOToEntity(ProbandListEntryTagValueOutVO probandListEntryTagValueOutVO) {
		ProbandListEntryTagValue entity = this.loadProbandListEntryTagValueFromProbandListEntryTagValueOutVO(probandListEntryTagValueOutVO);
		this.probandListEntryTagValueOutVOToEntity(probandListEntryTagValueOutVO, entity, true);
		return entity;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void probandListEntryTagValueOutVOToEntity(
			ProbandListEntryTagValueOutVO source,
			ProbandListEntryTagValue target,
			boolean copyIfNull) {
		super.probandListEntryTagValueOutVOToEntity(source, target, copyIfNull);
		ProbandListEntryOutVO listEntryVO = source.getListEntry();
		ProbandListEntryTagOutVO tagVO = source.getTag();
		UserOutVO modifiedUserVO = source.getModifiedUser();
		if (listEntryVO != null) {
			ProbandListEntry listEntry = this.getProbandListEntryDao().probandListEntryOutVOToEntity(listEntryVO);
			target.setListEntry(listEntry);
			listEntry.addTagValues(target);
		} else if (copyIfNull) {
			ProbandListEntry listEntry = target.getListEntry();
			target.setListEntry(null);
			if (listEntry != null) {
				listEntry.removeTagValues(target);
			}
		}
		if (tagVO != null) {
			ProbandListEntryTag tag = this.getProbandListEntryTagDao().probandListEntryTagOutVOToEntity(tagVO);
			target.setTag(tag);
			tag.addTagValues(target);
		} else if (copyIfNull) {
			ProbandListEntryTag tag = target.getTag();
			target.setTag(null);
			if (tag != null) {
				tag.removeTagValues(target);
			}
		}
		if (modifiedUserVO != null) {
			target.setModifiedUser(this.getUserDao().userOutVOToEntity(modifiedUserVO));
		} else if (copyIfNull) {
			target.setModifiedUser(null);
		}
		InputFieldValue value = target.getValue();
		if (value == null) {
			value = InputFieldValue.Factory.newInstance();
			target.setValue(value);
		}
		if (copyIfNull || source.getTextValue() != null) {
			value.setStringValue(source.getTextValue());
			value.setTruncatedStringValue(CommonUtil.truncateStringValue(source.getTextValue(), Settings.getIntNullable(SettingCodes.INPUT_FIELD_TRUNCATED_STRING_VALUE_MAX_LENGTH,
					Bundle.SETTINGS, DefaultSettings.INPUT_FIELD_TRUNCATED_STRING_VALUE_MAX_LENGTH)));
		}
		value.setBooleanValue(source.getBooleanValue());
		if (copyIfNull || source.getLongValue() != null) {
			value.setLongValue(source.getLongValue());
		}
		if (copyIfNull || source.getFloatValue() != null) {
			value.setFloatValue(source.getFloatValue());
		}
		if (copyIfNull || source.getDateValue() != null) {
			value.setDateValue(CoreUtil.forceDate(source.getDateValue()));
		}
		if (copyIfNull || source.getTimestampValue() != null) {
			value.setTimestampValue((source.getTimestampValue() == null ? null : new Timestamp(source.getTimestampValue().getTime())));
		}
		if (copyIfNull || source.getTimeValue() != null) {
			value.setTimeValue(CoreUtil.forceDate(source.getTimeValue()));
		}
		if (copyIfNull || source.getInkValues() != null) {
			value.setInkValue(source.getInkValues());
		}
		Collection selectionValues = source.getSelectionValues();
		if (selectionValues.size() > 0) {
			selectionValues = new ArrayList(selectionValues); //prevent changing VO
			this.getInputFieldSelectionSetValueDao().inputFieldSelectionSetValueOutVOToEntityCollection(selectionValues);
			value.setSelectionValues(selectionValues); // hashset-exception!!!
		} else if (copyIfNull) {
			value.getSelectionValues().clear();
		}
	}

	private ArrayList<InputFieldSelectionSetValueJsonVO> toInputFieldSelectionSetValueJsonVOCollection(Collection<InputFieldSelectionSetValue> selectionValues) { // lazyload
		InputFieldSelectionSetValueDao inputFieldSelectionSetValueDao = this.getInputFieldSelectionSetValueDao();
		ArrayList<InputFieldSelectionSetValueJsonVO> result = new ArrayList<InputFieldSelectionSetValueJsonVO>(selectionValues.size());
		Iterator<InputFieldSelectionSetValue> it = selectionValues.iterator();
		while (it.hasNext()) {
			result.add(inputFieldSelectionSetValueDao.toInputFieldSelectionSetValueJsonVO(it.next()));
		}
		Collections.sort(result, SELECTION_SET_VALUE_JSON_VO_ID_COMPARATOR);
		return result;
	}

	private ArrayList<InputFieldSelectionSetValueOutVO> toInputFieldSelectionSetValueOutVOCollection(Collection<InputFieldSelectionSetValue> selectionValues) { // lazyload
		InputFieldSelectionSetValueDao inputFieldSelectionSetValueDao = this.getInputFieldSelectionSetValueDao();
		ArrayList<InputFieldSelectionSetValueOutVO> result = new ArrayList<InputFieldSelectionSetValueOutVO>(selectionValues.size());
		Iterator<InputFieldSelectionSetValue> it = selectionValues.iterator();
		while (it.hasNext()) {
			result.add(inputFieldSelectionSetValueDao.toInputFieldSelectionSetValueOutVO(it.next()));
		}
		Collections.sort(result, SELECTION_SET_VALUE_OUT_VO_ID_COMPARATOR);
		return result;
	}

	// Error performing 'TrialService.setProbandListEntryTagValues(Set<ProbandListEntryTagValueInVO> probandListEntryTagValuesIns)' --> java.lang.ClassCastException:
	// java.util.ArrayList cannot be cast to java.util.Set
	private HashSet<InputFieldSelectionSetValue> toInputFieldSelectionSetValueSet(Collection<Long> selectionValueIds) { // lazyload persistentset prevention
		InputFieldSelectionSetValueDao inputFieldSelectionSetValueDao = this.getInputFieldSelectionSetValueDao();
		HashSet<InputFieldSelectionSetValue> result = new HashSet<InputFieldSelectionSetValue>(selectionValueIds.size());
		Iterator<Long> it = selectionValueIds.iterator();
		while (it.hasNext()) {
			result.add(inputFieldSelectionSetValueDao.load(it.next()));
		}
		return result;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public ProbandListEntryTagValueInVO toProbandListEntryTagValueInVO(final ProbandListEntryTagValue entity) {
		return super.toProbandListEntryTagValueInVO(entity);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void toProbandListEntryTagValueInVO(
			ProbandListEntryTagValue source,
			ProbandListEntryTagValueInVO target) {
		super.toProbandListEntryTagValueInVO(source, target);
		ProbandListEntry listEntry = source.getListEntry();
		ProbandListEntryTag tag = source.getTag();
		if (listEntry != null) {
			target.setListEntryId(listEntry.getId());
		}
		if (tag != null) {
			target.setTagId(tag.getId());
		}
		InputFieldValue value = source.getValue();
		if (value != null) {
			target.setTextValue(value.getStringValue());
			target.setBooleanValue((value.getBooleanValue() == null ? false : value.getBooleanValue().booleanValue()));
			target.setLongValue(value.getLongValue());
			target.setFloatValue(value.getFloatValue());
			target.setDateValue(CoreUtil.forceDate(value.getDateValue()));
			target.setTimestampValue(value.getTimestampValue());
			target.setTimeValue(CoreUtil.forceDate(value.getTimeValue()));
			target.setInkValues(value.getInkValue());
			target.setSelectionValueIds(ServiceUtil.toInputFieldSelectionSetValueIdCollection(value.getSelectionValues()));
		}
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public ProbandListEntryTagValueJsonVO toProbandListEntryTagValueJsonVO(final ProbandListEntryTagValue entity) {
		return super.toProbandListEntryTagValueJsonVO(entity);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void toProbandListEntryTagValueJsonVO(
			ProbandListEntryTagValue source,
			ProbandListEntryTagValueJsonVO target) {
		super.toProbandListEntryTagValueJsonVO(source, target);
		ProbandListEntryTag tag = source.getTag();
		if (tag != null) {
			target.setTagId(tag.getId());
			target.setPosition(tag.getPosition());
			target.setJsVariableName(tag.getJsVariableName());
			target.setJsValueExpression(tag.getJsValueExpression());
			target.setJsOutputExpression(tag.getJsOutputExpression());
			target.setDisabled(tag.isDisabled());
			InputField inputField = tag.getField();
			if (inputField != null) {
				target.setInputFieldId(inputField.getId());
				target.setInputFieldType(inputField.getFieldType());
				target.setUserTimeZone(inputField.isUserTimeZone());
				if (inputField.isLocalized()) {
					target.setInputFieldName(L10nUtil.getInputFieldName(Locales.USER, inputField.getNameL10nKey()));
				} else {
					target.setInputFieldName(inputField.getNameL10nKey());
				}
				if (ServiceUtil.isLoadSelectionSet(inputField.getFieldType())) {
					target.setInputFieldSelectionSetValues(toInputFieldSelectionSetValueJsonVOCollection(inputField.getSelectionSetValues()));
				}
			}
		}
		InputFieldValue value = source.getValue();
		if (value != null) {
			target.setTextValue(value.getStringValue());
			target.setBooleanValue((value.getBooleanValue() == null ? false : value.getBooleanValue().booleanValue()));
			target.setLongValue(value.getLongValue());
			target.setFloatValue(value.getFloatValue());
			target.setDateValue(CoreUtil.forceDate(value.getDateValue()));
			target.setTimestampValue(value.getTimestampValue());
			target.setTimeValue(CoreUtil.forceDate(value.getTimeValue()));
			if (InputFieldType.SKETCH.equals(target.getInputFieldType())) {
				target.setInkValues(value.getInkValue());
			} else {
				target.setInkValues(null);
			}
			target.setSelectionValueIds(ServiceUtil.toInputFieldSelectionSetValueIdCollection(value.getSelectionValues()));
		}
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public ProbandListEntryTagValueOutVO toProbandListEntryTagValueOutVO(final ProbandListEntryTagValue entity) {
		return super.toProbandListEntryTagValueOutVO(entity);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void toProbandListEntryTagValueOutVO(
			ProbandListEntryTagValue source,
			ProbandListEntryTagValueOutVO target) {
		super.toProbandListEntryTagValueOutVO(source, target);
		ProbandListEntry listEntry = source.getListEntry();
		ProbandListEntryTag tag = source.getTag();
		User modifiedUser = source.getModifiedUser();
		if (listEntry != null) {
			target.setListEntry(this.getProbandListEntryDao().toProbandListEntryOutVO(listEntry));
		}
		if (tag != null) {
			target.setTag(this.getProbandListEntryTagDao().toProbandListEntryTagOutVO(tag));
		}
		if (modifiedUser != null) {
			target.setModifiedUser(this.getUserDao().toUserOutVO(modifiedUser));
		}
		InputFieldValue value = source.getValue();
		if (value != null) {
			target.setTextValue(value.getStringValue());
			target.setBooleanValue((value.getBooleanValue() == null ? false : value.getBooleanValue().booleanValue()));
			target.setLongValue(value.getLongValue());
			target.setFloatValue(value.getFloatValue());
			target.setDateValue(CoreUtil.forceDate(value.getDateValue()));
			target.setTimestampValue(value.getTimestampValue());
			target.setTimeValue(CoreUtil.forceDate(value.getTimeValue()));
			if (ServiceUtil.isInputFieldType(target.getTag(), InputFieldType.SKETCH)) {
				target.setInkValues(value.getInkValue());
			} else {
				target.setInkValues(null);
			}
			target.setSelectionValues(toInputFieldSelectionSetValueOutVOCollection(value.getSelectionValues()));
		}
	}
}