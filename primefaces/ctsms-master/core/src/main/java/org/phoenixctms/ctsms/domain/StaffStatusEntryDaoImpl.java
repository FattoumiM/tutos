// Generated by: hibernate/SpringHibernateDaoImpl.vsl in andromda-spring-cartridge.
// license-header java merge-point
/**
 * This is only generated once! It will never be overwritten.
 * You can (and have to!) safely modify it by hand.
 */
package org.phoenixctms.ctsms.domain;

import java.sql.Timestamp;
import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.phoenixctms.ctsms.query.CriteriaUtil;
import org.phoenixctms.ctsms.query.SubCriteriaMap;
import org.phoenixctms.ctsms.vo.PSFVO;
import org.phoenixctms.ctsms.vo.StaffOutVO;
import org.phoenixctms.ctsms.vo.StaffStatusEntryInVO;
import org.phoenixctms.ctsms.vo.StaffStatusEntryOutVO;
import org.phoenixctms.ctsms.vo.StaffStatusTypeVO;
import org.phoenixctms.ctsms.vo.UserOutVO;

/**
 * @see StaffStatusEntry
 */
public class StaffStatusEntryDaoImpl
		extends StaffStatusEntryDaoBase {

	private org.hibernate.Criteria createStatusEntryCriteria() {
		org.hibernate.Criteria staffStatusEntryCriteria = this.getSession().createCriteria(StaffStatusEntry.class);
		return staffStatusEntryCriteria;
	}

	@Override
	protected Collection<StaffStatusEntry> handleFindByDepartmentCategoryInterval(
			Long departmentId, Long staffCategoryId, Timestamp from,
			Timestamp to, Boolean staffActive, Boolean allocatable, Boolean hideAvailability)
			throws Exception {
		Criteria statusEntryCriteria = createStatusEntryCriteria();
		CriteriaUtil.applyStopOpenIntervalCriterion(statusEntryCriteria, from, to, null);
		if (staffActive != null || hideAvailability != null) {
			Criteria typeCriteria = statusEntryCriteria.createCriteria("type", CriteriaSpecification.INNER_JOIN);
			if (staffActive != null) {
				typeCriteria.add(Restrictions.eq("staffActive", staffActive.booleanValue()));
			}
			if (hideAvailability != null) {
				typeCriteria.add(Restrictions.eq("hideAvailability", hideAvailability.booleanValue()));
			}
		}
		if (departmentId != null || staffCategoryId != null || allocatable != null) {
			Criteria staffCriteria = statusEntryCriteria.createCriteria("staff", CriteriaSpecification.INNER_JOIN);
			if (departmentId != null) {
				staffCriteria.add(Restrictions.eq("department.id", departmentId.longValue()));
			}
			if (staffCategoryId != null) {
				staffCriteria.add(Restrictions.eq("category.id", staffCategoryId.longValue()));
			}
			if (allocatable != null) {
				staffCriteria.add(Restrictions.eq("allocatable", allocatable.booleanValue()));
			}
		}
		return statusEntryCriteria.list();
	}

	@Override
	protected Collection<StaffStatusEntry> handleFindByStaff(Long staffId,
			PSFVO psf) throws Exception {
		Criteria statusEntryCriteria = createStatusEntryCriteria();
		SubCriteriaMap criteriaMap = new SubCriteriaMap(StaffStatusEntry.class, statusEntryCriteria);
		if (staffId != null) {
			statusEntryCriteria.add(Restrictions.eq("staff.id", staffId.longValue()));
		}
		CriteriaUtil.applyPSFVO(criteriaMap, psf);
		return statusEntryCriteria.list();
	}

	@Override
	protected Collection<StaffStatusEntry> handleFindByStaffInterval(
			Long staffId, Timestamp from, Timestamp to, Boolean staffActive, Boolean allocatable, Boolean hideAvailability)
			throws Exception {
		Criteria statusEntryCriteria = createStatusEntryCriteria();
		CriteriaUtil.applyStopOpenIntervalCriterion(statusEntryCriteria, from, to, null);
		if (staffActive != null || hideAvailability != null) {
			Criteria statusTypeCriteria = statusEntryCriteria.createCriteria("type", CriteriaSpecification.INNER_JOIN);
			if (staffActive != null) {
				statusTypeCriteria.add(Restrictions.eq("staffActive", staffActive.booleanValue()));
			}
			if (hideAvailability != null) {
				statusTypeCriteria.add(Restrictions.eq("hideAvailability", hideAvailability.booleanValue()));
			}
		}
		if (staffId != null) {
			statusEntryCriteria.add(Restrictions.eq("staff.id", staffId.longValue()));
		}
		if (allocatable != null) {
			statusEntryCriteria.createCriteria("staff", CriteriaSpecification.INNER_JOIN).add(Restrictions.eq("allocatable", allocatable.booleanValue()));
		}
		return statusEntryCriteria.list();
	}

	@Override
	protected Collection<StaffStatusEntry> handleFindStaffStatus(Timestamp now,
			Long staffId, Long departmentId, Long staffCategoryId,
			Boolean staffActive, Boolean hideAvailability, PSFVO psf) throws Exception {
		Criteria statusEntryCriteria = createStatusEntryCriteria();
		SubCriteriaMap criteriaMap = new SubCriteriaMap(StaffStatusEntry.class, statusEntryCriteria);
		if (staffId != null) {
			statusEntryCriteria.add(Restrictions.eq("staff.id", staffId.longValue()));
		}
		if (departmentId != null) {
			criteriaMap.createCriteria("staff").add(Restrictions.eq("department.id", departmentId.longValue()));
		}
		if (staffCategoryId != null) {
			criteriaMap.createCriteria("staff").add(Restrictions.eq("category.id", staffCategoryId.longValue()));
		}
		if (staffActive != null) {
			criteriaMap.createCriteria("type").add(Restrictions.eq("staffActive", staffActive.booleanValue()));
		}
		if (hideAvailability != null) {
			criteriaMap.createCriteria("type").add(Restrictions.eq("hideAvailability", hideAvailability.booleanValue()));
		}
		CriteriaUtil.applyCurrentStatusCriterion(statusEntryCriteria, now, null);
		CriteriaUtil.applyPSFVO(criteriaMap, psf);
		return statusEntryCriteria.list();
	}

	@Override
	protected long handleGetCount(Long staffId) throws Exception {
		Criteria statusEntryCriteria = createStatusEntryCriteria();
		if (staffId != null) {
			statusEntryCriteria.add(Restrictions.eq("staff.id", staffId.longValue()));
		}
		return (Long) statusEntryCriteria.setProjection(Projections.rowCount()).uniqueResult();
	}

	/**
	 * Retrieves the entity object that is associated with the specified value object
	 * from the object store. If no such entity object exists in the object store,
	 * a new, blank entity is created
	 */
	private StaffStatusEntry loadStaffStatusEntryFromStaffStatusEntryInVO(StaffStatusEntryInVO staffStatusEntryInVO) {
		StaffStatusEntry staffStatusEntry = null;
		Long id = staffStatusEntryInVO.getId();
		if (id != null) {
			staffStatusEntry = this.load(id);
		}
		if (staffStatusEntry == null) {
			staffStatusEntry = StaffStatusEntry.Factory.newInstance();
		}
		return staffStatusEntry;
	}

	/**
	 * Retrieves the entity object that is associated with the specified value object
	 * from the object store. If no such entity object exists in the object store,
	 * a new, blank entity is created
	 */
	private StaffStatusEntry loadStaffStatusEntryFromStaffStatusEntryOutVO(StaffStatusEntryOutVO staffStatusEntryOutVO) {
		StaffStatusEntry staffStatusEntry = this.load(staffStatusEntryOutVO.getId());
		if (staffStatusEntry == null) {
			staffStatusEntry = StaffStatusEntry.Factory.newInstance();
		}
		return staffStatusEntry;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public StaffStatusEntry staffStatusEntryInVOToEntity(StaffStatusEntryInVO staffStatusEntryInVO) {
		StaffStatusEntry entity = this.loadStaffStatusEntryFromStaffStatusEntryInVO(staffStatusEntryInVO);
		this.staffStatusEntryInVOToEntity(staffStatusEntryInVO, entity, true);
		return entity;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void staffStatusEntryInVOToEntity(
			StaffStatusEntryInVO source,
			StaffStatusEntry target,
			boolean copyIfNull) {
		super.staffStatusEntryInVOToEntity(source, target, copyIfNull);
		Long typeId = source.getTypeId();
		Long staffId = source.getStaffId();
		if (typeId != null) {
			target.setType(this.getStaffStatusTypeDao().load(typeId));
		} else if (copyIfNull) {
			target.setType(null);
		}
		if (staffId != null) {
			Staff staff = this.getStaffDao().load(staffId);
			target.setStaff(staff);
			staff.addStatusEntries(target);
		} else if (copyIfNull) {
			Staff staff = target.getStaff();
			target.setStaff(null);
			if (staff != null) {
				staff.removeStatusEntries(target);
			}
		}
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public StaffStatusEntry staffStatusEntryOutVOToEntity(StaffStatusEntryOutVO staffStatusEntryOutVO) {
		StaffStatusEntry entity = this.loadStaffStatusEntryFromStaffStatusEntryOutVO(staffStatusEntryOutVO);
		this.staffStatusEntryOutVOToEntity(staffStatusEntryOutVO, entity, true);
		return entity;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void staffStatusEntryOutVOToEntity(
			StaffStatusEntryOutVO source,
			StaffStatusEntry target,
			boolean copyIfNull) {
		super.staffStatusEntryOutVOToEntity(source, target, copyIfNull);
		StaffStatusTypeVO typeVO = source.getType();
		StaffOutVO staffVO = source.getStaff();
		UserOutVO modifiedUserVO = source.getModifiedUser();
		if (typeVO != null) {
			target.setType(this.getStaffStatusTypeDao().staffStatusTypeVOToEntity(typeVO));
		} else if (copyIfNull) {
			target.setType(null);
		}
		if (staffVO != null) {
			Staff staff = this.getStaffDao().staffOutVOToEntity(staffVO);
			target.setStaff(staff);
			staff.addStatusEntries(target);
		} else if (copyIfNull) {
			Staff staff = target.getStaff();
			target.setStaff(null);
			if (staff != null) {
				staff.removeStatusEntries(target);
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
	public StaffStatusEntryInVO toStaffStatusEntryInVO(final StaffStatusEntry entity) {
		return super.toStaffStatusEntryInVO(entity);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void toStaffStatusEntryInVO(
			StaffStatusEntry source,
			StaffStatusEntryInVO target) {
		super.toStaffStatusEntryInVO(source, target);
		StaffStatusType type = source.getType();
		Staff staff = source.getStaff();
		if (type != null) {
			target.setTypeId(type.getId());
		}
		if (staff != null) {
			target.setStaffId(staff.getId());
		}
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public StaffStatusEntryOutVO toStaffStatusEntryOutVO(final StaffStatusEntry entity) {
		return super.toStaffStatusEntryOutVO(entity);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void toStaffStatusEntryOutVO(
			StaffStatusEntry source,
			StaffStatusEntryOutVO target) {
		super.toStaffStatusEntryOutVO(source, target);
		StaffStatusType type = source.getType();
		Staff staff = source.getStaff();
		User modifiedUser = source.getModifiedUser();
		if (type != null) {
			target.setType(this.getStaffStatusTypeDao().toStaffStatusTypeVO(type));
		}
		if (staff != null) {
			target.setStaff(this.getStaffDao().toStaffOutVO(staff));
		}
		if (modifiedUser != null) {
			target.setModifiedUser(this.getUserDao().toUserOutVO(modifiedUser));
		}
	}
}