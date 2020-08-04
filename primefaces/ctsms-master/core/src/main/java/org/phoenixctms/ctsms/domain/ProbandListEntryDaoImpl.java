// Generated by: hibernate/SpringHibernateDaoImpl.vsl in andromda-spring-cartridge.
// license-header java merge-point
/**
 * This is only generated once! It will never be overwritten.
 * You can (and have to!) safely modify it by hand.
 */
package org.phoenixctms.ctsms.domain;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.phoenixctms.ctsms.query.CriteriaUtil;
import org.phoenixctms.ctsms.query.SubCriteriaMap;
import org.phoenixctms.ctsms.vo.PSFVO;
import org.phoenixctms.ctsms.vo.ProbandGroupOutVO;
import org.phoenixctms.ctsms.vo.ProbandListEntryInVO;
import org.phoenixctms.ctsms.vo.ProbandListEntryOutVO;
import org.phoenixctms.ctsms.vo.ProbandOutVO;
import org.phoenixctms.ctsms.vo.TrialOutVO;
import org.phoenixctms.ctsms.vo.UserOutVO;
import org.phoenixctms.ctsms.vocycle.ProbandListEntryGraph;

/**
 * @see ProbandListEntry
 */
public class ProbandListEntryDaoImpl
		extends ProbandListEntryDaoBase {

	private static Criteria applyStratificationTagValuesCriterions(org.hibernate.Criteria listEntryCriteria, Set<Long> selectionSetValueIds) {
		org.hibernate.Criteria tagValuesCriteria = listEntryCriteria.createCriteria("tagValues", CriteriaSpecification.INNER_JOIN);
		tagValuesCriteria.createCriteria("tag", CriteriaSpecification.INNER_JOIN).add(Restrictions.eq("stratification", true));
		org.hibernate.Criteria selectionValuesCriteria = tagValuesCriteria.createCriteria("value", CriteriaSpecification.INNER_JOIN).createCriteria("selectionValues",
				CriteriaSpecification.INNER_JOIN);
		selectionValuesCriteria.add(Restrictions.in("id", selectionSetValueIds));
		ProjectionList proj = Projections.projectionList();
		proj.add(Projections.id());
		proj.add(Projections.sqlGroupProjection(
				"count(*) as selectionValuesCount",
				"{alias}.id having count(*) = " + selectionSetValueIds.size(),
				new String[] { "selectionValuesCount" },
				new org.hibernate.type.Type[] { Hibernate.LONG }));
		listEntryCriteria.setProjection(proj);
		return listEntryCriteria;
	}

	private org.hibernate.Criteria createListEntryCriteria() {
		org.hibernate.Criteria listEntryCriteria = this.getSession().createCriteria(ProbandListEntry.class);
		return listEntryCriteria;
	}

	@Override
	protected Collection<ProbandListEntry> handleFindByTrialGroupProbandCountPerson(
			Long trialId, Long probandGroupId, Long probandId, boolean total,
			Boolean person, PSFVO psf) throws Exception {
		org.hibernate.Criteria listEntryCriteria = createListEntryCriteria();
		SubCriteriaMap criteriaMap = new SubCriteriaMap(ProbandListEntry.class, listEntryCriteria);
		if (trialId != null) {
			listEntryCriteria.add(Restrictions.eq("trial.id", trialId.longValue()));
		}
		if (probandGroupId != null) {
			listEntryCriteria.add(Restrictions.eq("group.id", probandGroupId.longValue()));
		}
		if (probandId != null || person != null) {
			Criteria probandCriteria = criteriaMap.createCriteria("proband", CriteriaSpecification.INNER_JOIN);
			if (probandId != null) {
				probandCriteria.add(Restrictions.idEq(probandId.longValue()));
			}
			if (person != null) {
				probandCriteria.add(Restrictions.eq("person", person.booleanValue()));
			}
		}
		if (!total) {
			criteriaMap.createCriteria("lastStatus.status", CriteriaSpecification.INNER_JOIN).add(Restrictions.eq("count", true));
		}
		CriteriaUtil.applyPSFVO(criteriaMap, psf);
		return listEntryCriteria.list();
	}

	@Override
	protected Collection<ProbandListEntry> handleFindByTrialPosition(
			Long trialId, Long position) throws Exception {
		org.hibernate.Criteria listEntryCriteria = createListEntryCriteria();
		if (trialId != null) {
			listEntryCriteria.add(Restrictions.eq("trial.id", trialId.longValue()));
		}
		if (position != null) {
			listEntryCriteria.add(Restrictions.eq("position", position.longValue()));
		}
		return listEntryCriteria.list();
	}

	@Override
	protected ProbandListEntry handleFindByTrialProband(
			Long trialId, Long probandId) throws Exception {
		org.hibernate.Criteria listEntryCriteria = createListEntryCriteria();
		listEntryCriteria.add(Restrictions.eq("trial.id", trialId.longValue()));
		listEntryCriteria.add(Restrictions.eq("proband.id", probandId.longValue()));
		listEntryCriteria.setMaxResults(1);
		return (ProbandListEntry) listEntryCriteria.uniqueResult();
	}

	@Override
	protected Collection<ProbandListEntry> handleFindByTrialProbandDepartment(
			Long trialId, Long probandDepartmentId) throws Exception {
		org.hibernate.Criteria listEntryCriteria = createListEntryCriteria();
		if (trialId != null) {
			listEntryCriteria.add(Restrictions.eq("trial.id", trialId.longValue()));
		}
		if (probandDepartmentId != null) {
			listEntryCriteria.createCriteria("proband", CriteriaSpecification.INNER_JOIN).add(Restrictions.eq("department.id", probandDepartmentId.longValue()));
		}
		return listEntryCriteria.list();
	}

	@Override
	protected Collection<ProbandListEntry> handleFindByTrialProbandSorted(Long trialId, Long probandId)
			throws Exception {
		org.hibernate.Criteria listEntryCriteria = createListEntryCriteria();
		if (trialId != null) {
			listEntryCriteria.add(Restrictions.eq("trial.id", trialId.longValue()));
		}
		if (probandId != null) {
			listEntryCriteria.add(Restrictions.eq("proband.id", probandId.longValue()));
		}
		listEntryCriteria.addOrder(Order.asc("trial"));
		listEntryCriteria.addOrder(Order.asc("position"));
		return listEntryCriteria.list();
	}

	@Override
	protected Long handleFindMaxPosition(Long trialId) throws Exception {
		org.hibernate.Criteria listEntryCriteria = createListEntryCriteria();
		if (trialId != null) {
			listEntryCriteria.add(Restrictions.eq("trial.id", trialId.longValue()));
		}
		listEntryCriteria.setProjection(Projections.max("position"));
		return (Long) listEntryCriteria.uniqueResult();
	}

	@Override
	protected Collection<ProbandListEntry> handleGetProbandList(
			Long trialId, org.phoenixctms.ctsms.enumeration.ProbandListStatusLogLevel logLevel, boolean last)
			throws Exception {
		// http://stackoverflow.com/questions/1648426/hibernate-detached-queries-as-a-part-of-the-criteria-query
		// https://forum.hibernate.org/viewtopic.php?p=2317841#2317841
		org.hibernate.Criteria listEntryCriteria = createListEntryCriteria();
		boolean distinctRoot = false;
		if (trialId != null) {
			listEntryCriteria.add(Restrictions.eq("trial.id", trialId.longValue()));
		}
		if (logLevel != null) {
			org.hibernate.Criteria statusEntryCriteria;
			if (last) {
				statusEntryCriteria = listEntryCriteria.createCriteria("statusEntries", "probandListStatusEntry0", CriteriaSpecification.INNER_JOIN);
			} else {
				statusEntryCriteria = listEntryCriteria.createCriteria("statusEntries", CriteriaSpecification.INNER_JOIN);
			}
			org.hibernate.Criteria statusTypeCriteria = statusEntryCriteria.createCriteria("status", CriteriaSpecification.INNER_JOIN);
			org.hibernate.Criteria logLevelCriteria = statusTypeCriteria.createCriteria("logLevels", CriteriaSpecification.INNER_JOIN);
			logLevelCriteria.add(Restrictions.eq("logLevel", logLevel));
			if (last) {
				DetachedCriteria subQuery = DetachedCriteria.forClass(ProbandListStatusEntryImpl.class, "probandListStatusEntry1"); // IMPL!!!!
				subQuery.add(Restrictions.eqProperty("probandListStatusEntry1.listEntry", "probandListStatusEntry0.listEntry"));
				subQuery.setProjection(Projections.max("id"));
				statusEntryCriteria.add(Subqueries.propertyEq("id", subQuery));
			} else {
				distinctRoot = true;
			}
		}
		listEntryCriteria.addOrder(Order.asc("trial"));
		listEntryCriteria.addOrder(Order.asc("position"));
		if (distinctRoot) {
			return CriteriaUtil.listDistinctRoot(listEntryCriteria, this, "trial.id", "position");
		} else {
			return listEntryCriteria.list();
		}
	}

	@Override
	protected long handleGetTrialGroupProbandCount(
			Long trialId, Long probandGroupId, Long probandId, boolean total) throws Exception {
		org.hibernate.Criteria listEntryCriteria = createListEntryCriteria();
		if (trialId != null) {
			listEntryCriteria.add(Restrictions.eq("trial.id", trialId.longValue()));
		}
		if (probandGroupId != null) {
			listEntryCriteria.add(Restrictions.eq("group.id", probandGroupId.longValue()));
		}
		if (probandId != null) {
			listEntryCriteria.add(Restrictions.eq("proband.id", probandId.longValue()));
		}
		if (!total) {
			listEntryCriteria.createCriteria("lastStatus", CriteriaSpecification.INNER_JOIN).createCriteria("status", CriteriaSpecification.INNER_JOIN)
					.add(Restrictions.eq("count", true));
		}
		return (Long) listEntryCriteria.setProjection(Projections.rowCount()).uniqueResult();
	}

	@Override
	protected long handleGetTrialGroupStratificationTagValuesCount(
			Long trialId, Long probandGroupId, Set<Long> selectionSetValueIds, Long excludeId) throws Exception {
		org.hibernate.Criteria listEntryCriteria = createListEntryCriteria();
		if (trialId != null) {
			listEntryCriteria.add(Restrictions.eq("trial.id", trialId.longValue()));
		}
		if (probandGroupId != null) {
			listEntryCriteria.add(Restrictions.eq("group.id", probandGroupId.longValue()));
		} else {
			listEntryCriteria.add(Restrictions.isNull("group.id"));
		}
		if (excludeId != null) {
			listEntryCriteria.add(Restrictions.ne("id", excludeId.longValue()));
		}
		if (selectionSetValueIds != null && selectionSetValueIds.size() > 0) {
			applyStratificationTagValuesCriterions(listEntryCriteria, selectionSetValueIds);
			Iterator it = listEntryCriteria.list().iterator();
			long result = 0l;
			while (it.hasNext()) {
				it.next();
				result++;
			}
			return result;
		} else {
			return (Long) listEntryCriteria.setProjection(Projections.rowCount()).uniqueResult();
		}
	}

	@Override
	protected ProbandListEntry handleGetByRandomizationListCode(
			RandomizationListCode code) throws Exception {
		org.hibernate.Criteria listEntryCriteria = createListEntryCriteria();
		StratificationRandomizationList randomizationList = code.getRandomizationList();
		Trial trial = null;
		HashSet<Long> selectionSetValueIds = null;
		if (randomizationList != null) {
			trial = randomizationList.getTrial();
			selectionSetValueIds = new HashSet<Long>();
			Iterator<InputFieldSelectionSetValue> selectionSetValuesIt = randomizationList.getSelectionSetValues().iterator();
			while (selectionSetValuesIt.hasNext()) {
				selectionSetValueIds.add(selectionSetValuesIt.next().getId());
			}
		} else {
			trial = code.getTrial();
		}
		listEntryCriteria.add(Restrictions.eq("trial.id", trial.getId().longValue()));
		org.hibernate.Criteria tagValuesCriteria = listEntryCriteria.createCriteria("tagValues", CriteriaSpecification.INNER_JOIN);
		tagValuesCriteria.createCriteria("tag", CriteriaSpecification.INNER_JOIN).add(Restrictions.eq("randomize", true));
		tagValuesCriteria.createCriteria("value", CriteriaSpecification.INNER_JOIN).add(Restrictions.eq("stringValue", code.getCode()));
		Iterator listEntryIt = listEntryCriteria.list().iterator();
		while (listEntryIt.hasNext()) {
			ProbandListEntry listEntry = (ProbandListEntry) listEntryIt.next();
			if (selectionSetValueIds != null) {
				if (applyStratificationTagValuesCriterions(createListEntryCriteria().add(Restrictions.idEq(listEntry.getId().longValue())), selectionSetValueIds).list().iterator()
						.hasNext()) {
					return listEntry;
				}
			} else {
				return listEntry;
			}
		}
		return null;
	}

	@Override
	protected long handleGetTrialRandomizeSelectStratificationTagValuesCount(
			Long trialId, Long randomizeSelectionSetValueId, Set<Long> selectionSetValueIds, Long excludeId) throws Exception {
		org.hibernate.Criteria listEntryCriteria = createListEntryCriteria();
		if (trialId != null) {
			listEntryCriteria.add(Restrictions.eq("trial.id", trialId.longValue()));
		}
		if (randomizeSelectionSetValueId != null) {
			org.hibernate.Criteria tagValuesCriteria = listEntryCriteria.createCriteria("tagValues", CriteriaSpecification.INNER_JOIN);
			tagValuesCriteria.createCriteria("tag", CriteriaSpecification.INNER_JOIN).add(Restrictions.eq("randomize", true));
			tagValuesCriteria.createCriteria("value", CriteriaSpecification.INNER_JOIN).createCriteria("selectionValues", CriteriaSpecification.INNER_JOIN)
					.add(Restrictions.idEq(randomizeSelectionSetValueId.longValue()));
		} else {
			org.hibernate.Criteria tagValuesCriteria = listEntryCriteria.createCriteria("tagValues", CriteriaSpecification.LEFT_JOIN);
			tagValuesCriteria.createCriteria("tag", CriteriaSpecification.LEFT_JOIN).add(Restrictions.or(Restrictions.isNull("randomize"), Restrictions.eq("randomize", true)));
			tagValuesCriteria.createCriteria("value", CriteriaSpecification.LEFT_JOIN).add(Restrictions.isEmpty("selectionValues"));
		}
		if (excludeId != null) {
			listEntryCriteria.add(Restrictions.ne("id", excludeId.longValue()));
		}
		if (selectionSetValueIds != null && selectionSetValueIds.size() > 0) {
			ProjectionList projectionList = Projections.projectionList().add(Projections.id());
			listEntryCriteria.setProjection(Projections.distinct(projectionList));
			Iterator it = listEntryCriteria.list().iterator();
			long result = 0l;
			while (it.hasNext()) {
				if (applyStratificationTagValuesCriterions(createListEntryCriteria().add(Restrictions.idEq(it.next())), selectionSetValueIds).list().iterator()
						.hasNext()) {
					result++;
				}
			}
			return result;
		} else {
			return (Long) listEntryCriteria.setProjection(Projections.countDistinct("id")).uniqueResult();
		}
	}

	@Override
	protected long handleGetTrialRandomizeTextStratificationTagValuesCount(
			Long trialId, Boolean randomizeTextValueEmpty, Set<Long> selectionSetValueIds, Long excludeId) throws Exception {
		org.hibernate.Criteria listEntryCriteria = createListEntryCriteria();
		if (trialId != null) {
			listEntryCriteria.add(Restrictions.eq("trial.id", trialId.longValue()));
		}
		if (randomizeTextValueEmpty != null) {
			if (!randomizeTextValueEmpty) {
				org.hibernate.Criteria tagValuesCriteria = listEntryCriteria.createCriteria("tagValues", CriteriaSpecification.INNER_JOIN);
				tagValuesCriteria.createCriteria("tag", CriteriaSpecification.INNER_JOIN).add(Restrictions.eq("randomize", true));
				tagValuesCriteria.createCriteria("value", CriteriaSpecification.INNER_JOIN)
						.add(Restrictions.not(Restrictions.or(Restrictions.isNull("stringValue"), Restrictions.eq("stringValue", ""))));
			} else {
				org.hibernate.Criteria tagValuesCriteria = listEntryCriteria.createCriteria("tagValues", CriteriaSpecification.LEFT_JOIN);
				tagValuesCriteria.createCriteria("tag", CriteriaSpecification.LEFT_JOIN).add(Restrictions.or(Restrictions.isNull("randomize"), Restrictions.eq("randomize", true)));
				tagValuesCriteria.createCriteria("value", CriteriaSpecification.LEFT_JOIN)
						.add(Restrictions.or(Restrictions.isNull("stringValue"), Restrictions.eq("stringValue", "")));
			}
		}
		if (excludeId != null) {
			listEntryCriteria.add(Restrictions.ne("id", excludeId.longValue()));
		}
		if (selectionSetValueIds != null && selectionSetValueIds.size() > 0) {
			ProjectionList projectionList = Projections.projectionList().add(Projections.id());
			listEntryCriteria.setProjection(Projections.distinct(projectionList));
			Iterator it = listEntryCriteria.list().iterator();
			long result = 0l;
			while (it.hasNext()) {
				if (applyStratificationTagValuesCriterions(createListEntryCriteria().add(Restrictions.idEq(it.next())), selectionSetValueIds).list().iterator()
						.hasNext()) {
					result++;
				}
			}
			return result;
		} else {
			return (Long) listEntryCriteria.setProjection(Projections.countDistinct("id")).uniqueResult();
		}
	}

	/**
	 * Retrieves the entity object that is associated with the specified value object
	 * from the object store. If no such entity object exists in the object store,
	 * a new, blank entity is created
	 */
	private ProbandListEntry loadProbandListEntryFromProbandListEntryInVO(ProbandListEntryInVO probandListEntryInVO) {
		ProbandListEntry probandListEntry = null;
		Long id = probandListEntryInVO.getId();
		if (id != null) {
			probandListEntry = this.load(id);
		}
		if (probandListEntry == null) {
			probandListEntry = ProbandListEntry.Factory.newInstance();
		}
		return probandListEntry;
	}

	/**
	 * Retrieves the entity object that is associated with the specified value object
	 * from the object store. If no such entity object exists in the object store,
	 * a new, blank entity is created
	 */
	private ProbandListEntry loadProbandListEntryFromProbandListEntryOutVO(ProbandListEntryOutVO probandListEntryOutVO) {
		throw new UnsupportedOperationException("out value object to recursive entity not supported");
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public ProbandListEntry probandListEntryInVOToEntity(ProbandListEntryInVO probandListEntryInVO) {
		ProbandListEntry entity = this.loadProbandListEntryFromProbandListEntryInVO(probandListEntryInVO);
		this.probandListEntryInVOToEntity(probandListEntryInVO, entity, true);
		return entity;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void probandListEntryInVOToEntity(
			ProbandListEntryInVO source,
			ProbandListEntry target,
			boolean copyIfNull) {
		super.probandListEntryInVOToEntity(source, target, copyIfNull);
		Long trialId = source.getTrialId();
		Long probandId = source.getProbandId();
		Long groupId = source.getGroupId();
		if (trialId != null) {
			Trial trial = this.getTrialDao().load(trialId);
			target.setTrial(trial);
			trial.addProbandListEntries(target);
		} else if (copyIfNull) {
			Trial trial = target.getTrial();
			target.setTrial(null);
			if (trial != null) {
				trial.removeProbandListEntries(target);
			}
		}
		if (probandId != null) {
			Proband proband = this.getProbandDao().load(probandId);
			target.setProband(proband);
			proband.addTrialParticipations(target);
		} else if (copyIfNull) {
			Proband proband = target.getProband();
			target.setProband(null);
			if (proband != null) {
				proband.removeTrialParticipations(target);
			}
		}
		if (groupId != null) {
			ProbandGroup group = this.getProbandGroupDao().load(groupId);
			target.setGroup(group);
			group.addProbandListEntries(target);
		} else if (copyIfNull) {
			ProbandGroup group = target.getGroup();
			target.setGroup(null);
			if (group != null) {
				group.removeProbandListEntries(target);
			}
		}
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public ProbandListEntry probandListEntryOutVOToEntity(ProbandListEntryOutVO probandListEntryOutVO) {
		ProbandListEntry entity = this.loadProbandListEntryFromProbandListEntryOutVO(probandListEntryOutVO);
		this.probandListEntryOutVOToEntity(probandListEntryOutVO, entity, true);
		return entity;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void probandListEntryOutVOToEntity(
			ProbandListEntryOutVO source,
			ProbandListEntry target,
			boolean copyIfNull) {
		super.probandListEntryOutVOToEntity(source, target, copyIfNull);
		TrialOutVO trialVO = source.getTrial();
		ProbandOutVO probandVO = source.getProband();
		ProbandGroupOutVO groupVO = source.getGroup();
		UserOutVO modifiedUserVO = source.getModifiedUser();
		if (trialVO != null) {
			Trial trial = this.getTrialDao().trialOutVOToEntity(trialVO);
			target.setTrial(trial);
			trial.addProbandListEntries(target);
		} else if (copyIfNull) {
			Trial trial = target.getTrial();
			target.setTrial(null);
			if (trial != null) {
				trial.removeProbandListEntries(target);
			}
		}
		if (probandVO != null) {
			Proband proband = this.getProbandDao().probandOutVOToEntity(probandVO);
			target.setProband(proband);
			proband.addTrialParticipations(target);
		} else if (copyIfNull) {
			Proband proband = target.getProband();
			target.setProband(null);
			if (proband != null) {
				proband.removeTrialParticipations(target);
			}
		}
		if (groupVO != null) {
			ProbandGroup group = this.getProbandGroupDao().probandGroupOutVOToEntity(groupVO);
			target.setGroup(group);
			group.addProbandListEntries(target);
		} else if (copyIfNull) {
			ProbandGroup group = target.getGroup();
			target.setGroup(null);
			if (group != null) {
				group.removeProbandListEntries(target);
			}
		}
		if (modifiedUserVO != null) {
			target.setModifiedUser(this.getUserDao().userOutVOToEntity(modifiedUserVO));
		} else if (copyIfNull) {
			target.setModifiedUser(null);
		}
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public ProbandListEntryInVO toProbandListEntryInVO(final ProbandListEntry entity) {
		return super.toProbandListEntryInVO(entity);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void toProbandListEntryInVO(
			ProbandListEntry source,
			ProbandListEntryInVO target) {
		super.toProbandListEntryInVO(source, target);
		Trial trial = source.getTrial();
		Proband proband = source.getProband();
		ProbandGroup group = source.getGroup();
		if (trial != null) {
			target.setTrialId(trial.getId());
		}
		if (proband != null) {
			target.setProbandId(proband.getId());
		}
		if (group != null) {
			target.setGroupId(group.getId());
		}
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public ProbandListEntryOutVO toProbandListEntryOutVO(final ProbandListEntry entity) {
		return super.toProbandListEntryOutVO(entity);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void toProbandListEntryOutVO(
			ProbandListEntry source,
			ProbandListEntryOutVO target) {
		(new ProbandListEntryGraph(this, this.getProbandListStatusEntryDao(), this.getTrialDao(), this.getProbandDao(), this.getProbandGroupDao(), this.getUserDao())).toVOHelper(
				source, target, new HashMap<Class, HashMap<Long, Object>>());
	}

	@Override
	public void toProbandListEntryOutVO(
			ProbandListEntry source,
			ProbandListEntryOutVO target, HashMap<Class, HashMap<Long, Object>> voMap) {
		(new ProbandListEntryGraph(this, this.getProbandListStatusEntryDao(), this.getTrialDao(), this.getProbandDao(), this.getProbandGroupDao(), this.getUserDao())).toVOHelper(
				source, target, voMap);
	}
}