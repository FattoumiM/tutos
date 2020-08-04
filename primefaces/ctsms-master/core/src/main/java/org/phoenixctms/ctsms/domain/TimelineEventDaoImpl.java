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
import java.util.Date;
import java.util.HashMap;

import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.phoenixctms.ctsms.query.CategoryCriterion;
import org.phoenixctms.ctsms.query.CriteriaUtil;
import org.phoenixctms.ctsms.query.SubCriteriaMap;
import org.phoenixctms.ctsms.util.DefaultSettings;
import org.phoenixctms.ctsms.util.SettingCodes;
import org.phoenixctms.ctsms.util.Settings;
import org.phoenixctms.ctsms.util.Settings.Bundle;
import org.phoenixctms.ctsms.vo.EventImportanceVO;
import org.phoenixctms.ctsms.vo.PSFVO;
import org.phoenixctms.ctsms.vo.TimelineEventInVO;
import org.phoenixctms.ctsms.vo.TimelineEventOutVO;
import org.phoenixctms.ctsms.vo.TimelineEventTypeVO;
import org.phoenixctms.ctsms.vo.TrialOutVO;
import org.phoenixctms.ctsms.vo.UserOutVO;
import org.phoenixctms.ctsms.vo.VariablePeriodVO;
import org.phoenixctms.ctsms.vocycle.TimelineEventReflexionGraph;

/**
 * @see TimelineEvent
 */
public class TimelineEventDaoImpl
		extends TimelineEventDaoBase {

	private org.hibernate.Criteria createTimelineEventCriteria() {
		org.hibernate.Criteria timelineEventCriteria = this.getSession().createCriteria(TimelineEvent.class);
		return timelineEventCriteria;
	}

	@Override
	protected Collection<TimelineEvent> handleFindByTrial(
			Long trialId, PSFVO psf) throws Exception {
		Criteria timelineEventCriteria = createTimelineEventCriteria();
		SubCriteriaMap criteriaMap = new SubCriteriaMap(TimelineEvent.class, timelineEventCriteria);
		if (trialId != null) {
			timelineEventCriteria.add(Restrictions.eq("trial.id", trialId.longValue()));
		}
		return CriteriaUtil.listDistinctRootPSFVO(criteriaMap, psf, this); // support filter by children
	}

	@Override
	protected Collection<TimelineEvent> handleFindTimelineEvents(Long trialId, String titleInfix, Integer limit)
			throws Exception {
		Criteria timelineEventCriteria = createTimelineEventCriteria();
		if (trialId != null) {
			timelineEventCriteria.add(Restrictions.eq("trial.id", trialId.longValue()));
		}
		CategoryCriterion.apply(timelineEventCriteria, new CategoryCriterion(titleInfix, "title", MatchMode.ANYWHERE));
		timelineEventCriteria.addOrder(Order.asc("title"));
		timelineEventCriteria.addOrder(Order.asc("start"));
		CriteriaUtil.applyLimit(limit, Settings.getIntNullable(SettingCodes.TIMELINE_EVENT_AUTOCOMPLETE_DEFAULT_RESULT_LIMIT, Bundle.SETTINGS,
				DefaultSettings.TIMELINE_EVENT_AUTOCOMPLETE_DEFAULT_RESULT_LIMIT), timelineEventCriteria);
		return timelineEventCriteria.list();
	}

	@Override
	protected Collection<TimelineEvent> handleFindByTrialDepartmentMemberInterval(
			Long trialId, Long departmentId, Long teamMemberStaffId, Boolean notify, Boolean ignoreTimelineEvents, Timestamp from, Timestamp to)
			throws Exception {
		Criteria timelineEventCriteria = createTimelineEventCriteria();
		CriteriaUtil.applyStopOptionalIntervalCriterion(timelineEventCriteria, from, to, null, true);
		boolean distinctRoot = false;
		if (trialId != null || departmentId != null || teamMemberStaffId != null || ignoreTimelineEvents != null) {
			Criteria trialCriteria = timelineEventCriteria.createCriteria("trial", CriteriaSpecification.INNER_JOIN);
			if (trialId != null) {
				trialCriteria.add(Restrictions.idEq(trialId.longValue()));
			}
			if (departmentId != null) {
				trialCriteria.add(Restrictions.eq("department.id", departmentId.longValue()));
			}
			if (ignoreTimelineEvents != null) {
				trialCriteria.createCriteria("status", CriteriaSpecification.INNER_JOIN).add(Restrictions.eq("ignoreTimelineEvents", ignoreTimelineEvents.booleanValue()));
			}
			if (teamMemberStaffId != null) {
				Criteria membersCriteria = trialCriteria.createCriteria("members", CriteriaSpecification.INNER_JOIN);
				if (notify != null) {
					membersCriteria.add(Restrictions.eq("notifyTimelineEvent", notify.booleanValue()));
				}
				membersCriteria.add(Restrictions.eq("staff.id", teamMemberStaffId.longValue()));
				distinctRoot = true;
			}
		}
		timelineEventCriteria.add(Restrictions.eq("dismissed", false)); // performance only...
		if (distinctRoot) {
			return CriteriaUtil.listEvents(CriteriaUtil.listDistinctRoot(timelineEventCriteria, this), from, to, notify);
		} else {
			return CriteriaUtil.listEvents(timelineEventCriteria, from, to, notify);
		}
	}

	@Override
	protected Collection<TimelineEvent> handleFindByTrialDepartmentStatusTypeShowInterval(
			Long trialId, Long departmentId, Long statusId, Long typeId,
			Boolean show, Timestamp from, Timestamp to) throws Exception {
		Criteria timelineEventCriteria = createTimelineEventCriteria();
		CriteriaUtil.applyStopOptionalIntervalCriterion(timelineEventCriteria, from, to, null, true);
		if (trialId != null || departmentId != null || statusId != null) {
			Criteria trialCriteria = timelineEventCriteria.createCriteria("trial", CriteriaSpecification.INNER_JOIN);
			if (trialId != null) {
				trialCriteria.add(Restrictions.idEq(trialId.longValue()));
			}
			if (departmentId != null) {
				trialCriteria.add(Restrictions.eq("department.id", departmentId.longValue()));
			}
			if (statusId != null) {
				trialCriteria.add(Restrictions.eq("status.id", statusId.longValue()));
			}
		}
		if (typeId != null) {
			timelineEventCriteria.add(Restrictions.eq("type.id", typeId.longValue()));
		}
		if (show != null) {
			timelineEventCriteria.add(Restrictions.eq("show", show.booleanValue()));
		}
		return timelineEventCriteria.list();
	}

	@Override
	protected Collection<TimelineEvent> handleFindTimelineSchedule(Date today,
			Long trialId, Long departmentId, Long teamMemberStaffId, Boolean notify, Boolean ignoreTimelineEvents, boolean includeAlreadyPassed, PSFVO psf)
			throws Exception {
		Criteria timelineEventCriteria = createTimelineEventCriteria();
		SubCriteriaMap criteriaMap = new SubCriteriaMap(TimelineEvent.class, timelineEventCriteria);
		if (trialId != null) {
			timelineEventCriteria.add(Restrictions.eq("trial.id", trialId.longValue()));
		}
		if (departmentId != null) {
			criteriaMap.createCriteria("trial").add(Restrictions.eq("department.id", departmentId.longValue()));
		}
		if (ignoreTimelineEvents != null) {
			criteriaMap.createCriteria("trial.status").add(Restrictions.eq("ignoreTimelineEvents", ignoreTimelineEvents.booleanValue()));
		}
		if (teamMemberStaffId != null) {
			criteriaMap.createCriteria("trial.members").add(Restrictions.eq("staff.id", teamMemberStaffId.longValue())); // unique staff!
			if (notify != null) {
				criteriaMap.createCriteria("trial.members").add(Restrictions.eq("notifyTimelineEvent", notify.booleanValue()));
			}
		}
		timelineEventCriteria.add(Restrictions.eq("dismissed", false)); // performance only...
		if (psf != null) {
			PSFVO sorterFilter = new PSFVO();
			sorterFilter.setFilters(psf.getFilters());
			sorterFilter.setSortField(psf.getSortField());
			sorterFilter.setSortOrder(psf.getSortOrder());
			CriteriaUtil.applyPSFVO(criteriaMap, sorterFilter); // staff is not unique in team members
		} else {
			timelineEventCriteria.setResultTransformer(CriteriaSpecification.ROOT_ENTITY);
		}
		ArrayList<TimelineEvent> resultSet = CriteriaUtil.listReminders(timelineEventCriteria, today, notify, includeAlreadyPassed, null, null);
		return CriteriaUtil.applyPVO(resultSet, psf, true); // remove dupes for teamMemberStaffId != null, but also support filter by children
	}

	@Override
	protected long handleGetCount(
			Long trialId) throws Exception {
		Criteria timelineEventCriteria = createTimelineEventCriteria();
		if (trialId != null) {
			timelineEventCriteria.add(Restrictions.eq("trial.id", trialId.longValue()));
		}
		return (Long) timelineEventCriteria.setProjection(Projections.rowCount()).uniqueResult();
	}

	/**
	 * Retrieves the entity object that is associated with the specified value object
	 * from the object store. If no such entity object exists in the object store,
	 * a new, blank entity is created
	 */
	private TimelineEvent loadTimelineEventFromTimelineEventInVO(TimelineEventInVO timelineEventInVO) {
		TimelineEvent timelineEvent = null;
		Long id = timelineEventInVO.getId();
		if (id != null) {
			timelineEvent = this.load(id);
		}
		if (timelineEvent == null) {
			timelineEvent = TimelineEvent.Factory.newInstance();
		}
		return timelineEvent;
	}

	/**
	 * Retrieves the entity object that is associated with the specified value object
	 * from the object store. If no such entity object exists in the object store,
	 * a new, blank entity is created
	 */
	private TimelineEvent loadTimelineEventFromTimelineEventOutVO(TimelineEventOutVO timelineEventOutVO) {
		throw new UnsupportedOperationException("out value object to recursive entity not supported");
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public TimelineEvent timelineEventInVOToEntity(TimelineEventInVO timelineEventInVO) {
		TimelineEvent entity = this.loadTimelineEventFromTimelineEventInVO(timelineEventInVO);
		this.timelineEventInVOToEntity(timelineEventInVO, entity, true);
		return entity;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void timelineEventInVOToEntity(
			TimelineEventInVO source,
			TimelineEvent target,
			boolean copyIfNull) {
		super.timelineEventInVOToEntity(source, target, copyIfNull);
		Long typeId = source.getTypeId();
		Long parentId = source.getParentId();
		Long trialId = source.getTrialId();
		if (typeId != null) {
			target.setType(this.getTimelineEventTypeDao().load(typeId));
		} else if (copyIfNull) {
			target.setType(null);
		}
		if (trialId != null) {
			Trial trial = this.getTrialDao().load(trialId);
			target.setTrial(trial);
			trial.addEvents(target);
		} else if (copyIfNull) {
			Trial trial = target.getTrial();
			target.setTrial(null);
			if (trial != null) {
				trial.removeEvents(target);
			}
		}
		if (parentId != null) {
			if (target.getParent() != null) {
				target.getParent().removeChildren(target);
			}
			TimelineEvent parent = this.load(parentId);
			target.setParent(parent);
			parent.addChildren(target);
		} else if (copyIfNull) {
			TimelineEvent parent = target.getParent();
			target.setParent(null);
			if (parent != null) {
				parent.removeChildren(target);
			}
		}
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public TimelineEvent timelineEventOutVOToEntity(TimelineEventOutVO timelineEventOutVO) {
		TimelineEvent entity = this.loadTimelineEventFromTimelineEventOutVO(timelineEventOutVO);
		this.timelineEventOutVOToEntity(timelineEventOutVO, entity, true);
		return entity;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void timelineEventOutVOToEntity(
			TimelineEventOutVO source,
			TimelineEvent target,
			boolean copyIfNull) {
		super.timelineEventOutVOToEntity(source, target, copyIfNull);
		TimelineEventTypeVO typeVO = source.getType();
		TrialOutVO trialVO = source.getTrial();
		TimelineEventOutVO parentVO = source.getParent();
		UserOutVO modifiedUserVO = source.getModifiedUser();
		VariablePeriodVO reminderPeriodVO = source.getReminderPeriod();
		EventImportanceVO importanceVO = source.getImportance();
		if (typeVO != null) {
			target.setType(this.getTimelineEventTypeDao().timelineEventTypeVOToEntity(typeVO));
		} else if (copyIfNull) {
			target.setType(null);
		}
		if (trialVO != null) {
			Trial trial = this.getTrialDao().trialOutVOToEntity(trialVO);
			target.setTrial(trial);
			trial.addEvents(target);
		} else if (copyIfNull) {
			Trial trial = target.getTrial();
			target.setTrial(null);
			if (trial != null) {
				trial.removeEvents(target);
			}
		}
		if (parentVO != null) {
			if (target.getParent() != null) {
				target.getParent().removeChildren(target);
			}
			TimelineEvent parent = this.timelineEventOutVOToEntity(parentVO);
			target.setParent(parent);
			parent.addChildren(target);
		} else if (copyIfNull) {
			TimelineEvent parent = target.getParent();
			target.setParent(null);
			if (parent != null) {
				parent.removeChildren(target);
			}
		}
		if (modifiedUserVO != null) {
			target.setModifiedUser(this.getUserDao().userOutVOToEntity(modifiedUserVO));
		} else if (copyIfNull) {
			target.setModifiedUser(null);
		}
		if (reminderPeriodVO != null) {
			target.setReminderPeriod(reminderPeriodVO.getPeriod());
		} else if (copyIfNull) {
			target.setReminderPeriod(null);
		}
		if (importanceVO != null) {
			target.setImportance(importanceVO.getImportance());
		} else if (copyIfNull) {
			target.setImportance(null);
		}
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public TimelineEventInVO toTimelineEventInVO(final TimelineEvent entity) {
		return super.toTimelineEventInVO(entity);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void toTimelineEventInVO(
			TimelineEvent source,
			TimelineEventInVO target) {
		super.toTimelineEventInVO(source, target);
		TimelineEventType type = source.getType();
		Trial trial = source.getTrial();
		TimelineEvent parent = source.getParent();
		if (type != null) {
			target.setTypeId(type.getId());
		}
		if (parent != null) {
			target.setParentId(parent.getId());
		}
		if (trial != null) {
			target.setTrialId(trial.getId());
		}
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public TimelineEventOutVO toTimelineEventOutVO(final TimelineEvent entity) {
		return super.toTimelineEventOutVO(entity);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void toTimelineEventOutVO(
			TimelineEvent source,
			TimelineEventOutVO target) {
		(new TimelineEventReflexionGraph(this, this.getTimelineEventTypeDao(), this.getTrialDao(), this.getUserDao())).toVOHelper(source, target,
				new HashMap<Class, HashMap<Long, Object>>());
	}

	@Override
	public void toTimelineEventOutVO(
			TimelineEvent source,
			TimelineEventOutVO target, Integer... maxInstances) {
		(new TimelineEventReflexionGraph(this, this.getTimelineEventTypeDao(), this.getTrialDao(), this.getUserDao(), maxInstances)).toVOHelper(source,
				target, new HashMap<Class, HashMap<Long, Object>>());
	}

	@Override
	protected long handleGetChildrenCount(Long timelineEventId) throws Exception {
		org.hibernate.Criteria timelineEventCriteria = createTimelineEventCriteria();
		timelineEventCriteria.add(Restrictions.eq("parent.id", timelineEventId.longValue()));
		return (Long) timelineEventCriteria.setProjection(Projections.rowCount()).uniqueResult();
	}
}