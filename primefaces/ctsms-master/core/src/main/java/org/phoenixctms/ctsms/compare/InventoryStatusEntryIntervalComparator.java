package org.phoenixctms.ctsms.compare;

import java.util.Date;

import org.phoenixctms.ctsms.vo.InventoryStatusEntryOutVO;

public class InventoryStatusEntryIntervalComparator extends IntervalComparatorBase<InventoryStatusEntryOutVO> {

	public InventoryStatusEntryIntervalComparator(boolean desc) {
		super(desc);
	}

	@Override
	protected Date getStart(InventoryStatusEntryOutVO item) {
		return item.getStart();
	}

	@Override
	protected Date getStop(InventoryStatusEntryOutVO item) {
		return item.getStop();
	}
}
