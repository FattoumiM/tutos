// Generated by: hibernate/SpringHibernateDaoImpl.vsl in andromda-spring-cartridge.
// license-header java merge-point
/**
 * This is only generated once! It will never be overwritten.
 * You can (and have to!) safely modify it by hand.
 */
package org.phoenixctms.ctsms.domain;

import org.phoenixctms.ctsms.vo.TrialStatusActionVO;

/**
 * @see TrialStatusAction
 */
public class TrialStatusActionDaoImpl
		extends TrialStatusActionDaoBase {

	/**
	 * Retrieves the entity object that is associated with the specified value object
	 * from the object store. If no such entity object exists in the object store,
	 * a new, blank entity is created
	 */
	private TrialStatusAction loadTrialStatusActionFromTrialStatusActionVO(TrialStatusActionVO trialStatusActionVO) {
		TrialStatusAction trialStatusAction = null;
		Long id = trialStatusActionVO.getId();
		if (id != null) {
			trialStatusAction = this.load(id);
		}
		if (trialStatusAction == null) {
			trialStatusAction = TrialStatusAction.Factory.newInstance();
		}
		return trialStatusAction;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public TrialStatusActionVO toTrialStatusActionVO(final TrialStatusAction entity) {
		return super.toTrialStatusActionVO(entity);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void toTrialStatusActionVO(
			TrialStatusAction source,
			TrialStatusActionVO target) {
		super.toTrialStatusActionVO(source, target);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public TrialStatusAction trialStatusActionVOToEntity(TrialStatusActionVO trialStatusActionVO) {
		TrialStatusAction entity = this.loadTrialStatusActionFromTrialStatusActionVO(trialStatusActionVO);
		this.trialStatusActionVOToEntity(trialStatusActionVO, entity, true);
		return entity;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void trialStatusActionVOToEntity(
			TrialStatusActionVO source,
			TrialStatusAction target,
			boolean copyIfNull) {
		super.trialStatusActionVOToEntity(source, target, copyIfNull);
	}
}