package org.phoenixctms.ctsms.web.model.proband;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.faces.model.SelectItem;

import org.phoenixctms.ctsms.exception.AuthenticationException;
import org.phoenixctms.ctsms.exception.AuthorisationException;
import org.phoenixctms.ctsms.exception.ServiceException;
import org.phoenixctms.ctsms.util.CommonUtil;
import org.phoenixctms.ctsms.vo.PSFVO;
import org.phoenixctms.ctsms.vo.ProbandStatusEntryOutVO;
import org.phoenixctms.ctsms.vo.StaffStatusEntryOutVO;
import org.phoenixctms.ctsms.vo.VisitScheduleItemOutVO;
import org.phoenixctms.ctsms.web.model.EagerDataModelBase;
import org.phoenixctms.ctsms.web.util.WebUtil;

public class CollidingVisitScheduleItemEagerModel extends EagerDataModelBase<VisitScheduleItemOutVO> {

	public static CollidingVisitScheduleItemEagerModel getCachedCollidingVisitScheduleItemModel(ProbandStatusEntryOutVO statusEntry, boolean allProbandGroups,
			HashMap<Long, CollidingVisitScheduleItemEagerModel> collidingVisitScheduleItemModelCache) {
		CollidingVisitScheduleItemEagerModel model;
		if (statusEntry != null && collidingVisitScheduleItemModelCache != null) {
			long probandStatusEntryId = statusEntry.getId();
			if (collidingVisitScheduleItemModelCache.containsKey(probandStatusEntryId)) {
				model = collidingVisitScheduleItemModelCache.get(probandStatusEntryId);
			} else {
				model = new CollidingVisitScheduleItemEagerModel();
				model.setProbandStatusEntryId(probandStatusEntryId);
				model.setAllProbandGroups(allProbandGroups);
				collidingVisitScheduleItemModelCache.put(probandStatusEntryId, model);
			}
		} else {
			model = new CollidingVisitScheduleItemEagerModel();
			model.setAllProbandGroups(allProbandGroups);
			model.initSets();
		}
		return model;
	}

	public static CollidingVisitScheduleItemEagerModel getCachedCollidingVisitScheduleItemModel(StaffStatusEntryOutVO statusEntry,
			HashMap<Long, CollidingVisitScheduleItemEagerModel> collidingVisitScheduleItemModelCache) {
		CollidingVisitScheduleItemEagerModel model;
		if (statusEntry != null && collidingVisitScheduleItemModelCache != null) {
			long staffStatusEntryId = statusEntry.getId();
			if (collidingVisitScheduleItemModelCache.containsKey(staffStatusEntryId)) {
				model = collidingVisitScheduleItemModelCache.get(staffStatusEntryId);
			} else {
				model = new CollidingVisitScheduleItemEagerModel();
				model.setStaffStatusEntryId(staffStatusEntryId);
				collidingVisitScheduleItemModelCache.put(staffStatusEntryId, model);
			}
		} else {
			model = new CollidingVisitScheduleItemEagerModel();
		}
		return model;
	}

	private Long probandStatusEntryId;
	private Long staffStatusEntryId;
	private ArrayList<SelectItem> filterTrials;
	private boolean allProbandGroups;

	public CollidingVisitScheduleItemEagerModel() {
		super();
		resetRows();
	}

	@Override
	protected Collection<VisitScheduleItemOutVO> getEagerResult(PSFVO psf) {
		if (probandStatusEntryId != null) {
			try {
				return WebUtil.getServiceLocator().getProbandService().getCollidingVisitScheduleItems(WebUtil.getAuthentication(), probandStatusEntryId, allProbandGroups);
			} catch (ServiceException | AuthorisationException | IllegalArgumentException e) {
			} catch (AuthenticationException e) {
				WebUtil.publishException(e);
			}
		} else if (staffStatusEntryId != null) {
			try {
				return WebUtil.getServiceLocator().getStaffService().getCollidingVisitScheduleItems(WebUtil.getAuthentication(), staffStatusEntryId, null);
			} catch (ServiceException | AuthorisationException | IllegalArgumentException e) {
			} catch (AuthenticationException e) {
				WebUtil.publishException(e);
			}
		}
		return new ArrayList<VisitScheduleItemOutVO>();
	}

	public ArrayList<SelectItem> getFilterTrials() {
		return filterTrials;
	}

	public Long getProbandStatusEntryId() {
		return probandStatusEntryId;
	}

	@Override
	protected VisitScheduleItemOutVO getRowElement(Long id) {
		return WebUtil.getVisitScheduleItem(id);
	}

	public Long getStaffStatusEntryId() {
		return staffStatusEntryId;
	}

	private void initSets() {
		ProbandStatusEntryOutVO probandStatusEntryVO = WebUtil.getProbandStatusEntry(probandStatusEntryId);
		if (probandStatusEntryVO != null) {
			filterTrials = WebUtil.getParticipationTrials(probandStatusEntryVO.getProband().getId());
		} else {
			filterTrials = new ArrayList<SelectItem>();
		}
		filterTrials.add(0, new SelectItem(CommonUtil.NO_SELECTION_VALUE, ""));
	}

	public boolean isAllProbandGroups() {
		return allProbandGroups;
	}

	public void setAllProbandGroups(boolean allProbandGroups) {
		this.allProbandGroups = allProbandGroups;
	}

	public void setProbandStatusEntryId(Long probandStatusEntryId) {
		this.probandStatusEntryId = probandStatusEntryId;
		initSets();
	}

	public void setStaffStatusEntryId(Long staffStatusEntryId) {
		this.staffStatusEntryId = staffStatusEntryId;
		filterTrials = null;
	}
}
