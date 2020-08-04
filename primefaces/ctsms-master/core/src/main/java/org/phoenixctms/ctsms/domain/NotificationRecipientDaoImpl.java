// Generated by: hibernate/SpringHibernateDaoImpl.vsl in andromda-spring-cartridge.
// license-header java merge-point
/**
 * This is only generated once! It will never be overwritten.
 * You can (and have to!) safely modify it by hand.
 */
package org.phoenixctms.ctsms.domain;

import java.util.Collection;

import org.hibernate.criterion.Restrictions;
import org.phoenixctms.ctsms.query.CriteriaUtil;
import org.phoenixctms.ctsms.query.SubCriteriaMap;
import org.phoenixctms.ctsms.util.DefaultSettings;
import org.phoenixctms.ctsms.util.SettingCodes;
import org.phoenixctms.ctsms.util.Settings;
import org.phoenixctms.ctsms.util.Settings.Bundle;
import org.phoenixctms.ctsms.vo.NotificationRecipientVO;
import org.phoenixctms.ctsms.vo.PSFVO;

/**
 * @see NotificationRecipient
 */
public class NotificationRecipientDaoImpl
		extends NotificationRecipientDaoBase {

	@Override
	protected Collection<NotificationRecipient> handleFindPending(Long notificationId, Long departmentId,
			PSFVO psf) throws Exception {
		org.hibernate.Criteria notificationRecipientCriteria = this.getSession().createCriteria(NotificationRecipient.class);
		SubCriteriaMap criteriaMap = new SubCriteriaMap(NotificationRecipient.class, notificationRecipientCriteria);
		if (notificationId != null) {
			notificationRecipientCriteria.add(Restrictions.eq("notification.id", notificationId.longValue()));
		}
		if (departmentId != null) {
			criteriaMap.createCriteria("staff").add(Restrictions.eq("department.id", departmentId.longValue()));
		}
		criteriaMap.createCriteria("notification").add(Restrictions.eq("obsolete", false));
		criteriaMap.createCriteria("notification.type").add(Restrictions.eq("send", true));
		notificationRecipientCriteria.add(Restrictions.eq("sent", false));
		notificationRecipientCriteria.add(Restrictions.eq("dropped", false));
		Long processNotificationsMax = Settings.getLongNullable(SettingCodes.EMAIL_PROCESS_NOTIFICATIONS_MAX, Bundle.SETTINGS, DefaultSettings.EMAIL_PROCESS_NOTIFICATIONS_MAX);
		if (processNotificationsMax != null) {
			notificationRecipientCriteria.add(Restrictions.lt("timesProcessed", processNotificationsMax.longValue()));
		}
		CriteriaUtil.applyPSFVO(criteriaMap, psf);
		return notificationRecipientCriteria.list();
	}

	/**
	 * Retrieves the entity object that is associated with the specified value object
	 * from the object store. If no such entity object exists in the object store,
	 * a new, blank entity is created
	 */
	private NotificationRecipient loadNotificationRecipientFromNotificationRecipientVO(NotificationRecipientVO notificationRecipientVO) {
		throw new UnsupportedOperationException("org.phoenixctms.ctsms.domain.loadNotificationRecipientFromNotificationRecipientVO(NotificationRecipientVO) not yet implemented.");
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public NotificationRecipient notificationRecipientVOToEntity(NotificationRecipientVO notificationRecipientVO) {
		NotificationRecipient entity = this.loadNotificationRecipientFromNotificationRecipientVO(notificationRecipientVO);
		this.notificationRecipientVOToEntity(notificationRecipientVO, entity, true);
		return entity;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void notificationRecipientVOToEntity(
			NotificationRecipientVO source,
			NotificationRecipient target,
			boolean copyIfNull) {
		super.notificationRecipientVOToEntity(source, target, copyIfNull);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public NotificationRecipientVO toNotificationRecipientVO(final NotificationRecipient entity) {
		return super.toNotificationRecipientVO(entity);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void toNotificationRecipientVO(
			NotificationRecipient source,
			NotificationRecipientVO target) {
		super.toNotificationRecipientVO(source, target);
		Staff staff = source.getStaff();
		if (staff != null) {
			target.setStaff(this.getStaffDao().toStaffOutVO(staff));
		}
	}
}