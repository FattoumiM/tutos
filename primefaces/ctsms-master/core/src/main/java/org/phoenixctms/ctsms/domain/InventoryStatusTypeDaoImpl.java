// Generated by: hibernate/SpringHibernateDaoImpl.vsl in andromda-spring-cartridge.
// license-header java merge-point
/**
 * This is only generated once! It will never be overwritten.
 * You can (and have to!) safely modify it by hand.
 */
package org.phoenixctms.ctsms.domain;

import java.util.Collection;

import org.phoenixctms.ctsms.query.CriteriaUtil;
import org.phoenixctms.ctsms.util.L10nUtil;
import org.phoenixctms.ctsms.util.L10nUtil.Locales;
import org.phoenixctms.ctsms.vo.InventoryStatusTypeVO;

/**
 * @see InventoryStatusType
 */
public class InventoryStatusTypeDaoImpl
		extends InventoryStatusTypeDaoBase {

	@Override
	protected Collection<InventoryStatusType> handleFindByVisibleId(
			Boolean visible, Long typeId) throws Exception {
		org.hibernate.Criteria statusTypeCriteria = this.getSession().createCriteria(InventoryStatusType.class);
		statusTypeCriteria.setCacheable(true);
		CriteriaUtil.applyVisibleIdCriterion("visible", statusTypeCriteria, visible, typeId);
		return statusTypeCriteria.list();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InventoryStatusType inventoryStatusTypeVOToEntity(InventoryStatusTypeVO inventoryStatusTypeVO) {
		InventoryStatusType entity = this.loadInventoryStatusTypeFromInventoryStatusTypeVO(inventoryStatusTypeVO);
		this.inventoryStatusTypeVOToEntity(inventoryStatusTypeVO, entity, true);
		return entity;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void inventoryStatusTypeVOToEntity(
			InventoryStatusTypeVO source,
			InventoryStatusType target,
			boolean copyIfNull) {
		super.inventoryStatusTypeVOToEntity(source, target, copyIfNull);
	}

	/**
	 * Retrieves the entity object that is associated with the specified value object
	 * from the object store. If no such entity object exists in the object store,
	 * a new, blank entity is created
	 */
	private InventoryStatusType loadInventoryStatusTypeFromInventoryStatusTypeVO(InventoryStatusTypeVO inventoryStatusTypeVO) {
		InventoryStatusType inventoryStatusType = null;
		Long id = inventoryStatusTypeVO.getId();
		if (id != null) {
			inventoryStatusType = this.load(id);
		}
		if (inventoryStatusType == null) {
			inventoryStatusType = InventoryStatusType.Factory.newInstance();
		}
		return inventoryStatusType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InventoryStatusTypeVO toInventoryStatusTypeVO(final InventoryStatusType entity) {
		return super.toInventoryStatusTypeVO(entity);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void toInventoryStatusTypeVO(
			InventoryStatusType source,
			InventoryStatusTypeVO target) {
		super.toInventoryStatusTypeVO(source, target);
		target.setName(L10nUtil.getInventoryStatusTypeName(Locales.USER, source.getNameL10nKey()));
	}
}