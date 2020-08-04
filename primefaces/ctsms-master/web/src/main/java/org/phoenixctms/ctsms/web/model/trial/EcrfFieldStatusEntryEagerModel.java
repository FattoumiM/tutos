package org.phoenixctms.ctsms.web.model.trial;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.phoenixctms.ctsms.exception.AuthenticationException;
import org.phoenixctms.ctsms.exception.AuthorisationException;
import org.phoenixctms.ctsms.exception.ServiceException;
import org.phoenixctms.ctsms.vo.ECRFFieldStatusEntryOutVO;
import org.phoenixctms.ctsms.vo.PSFVO;
import org.phoenixctms.ctsms.web.model.EagerDataModelBase;
import org.phoenixctms.ctsms.web.util.WebUtil;

public class EcrfFieldStatusEntryEagerModel extends EagerDataModelBase<ECRFFieldStatusEntryOutVO> {

	private final static PSFVO INITIAL_PSF = new PSFVO();
	static {
		INITIAL_PSF.setSortField(WebUtil.ECRF_FIELD_STATUS_ENTRY_ID_PSF_PROPERTY_NAME);
		INITIAL_PSF.setSortOrder(false);
	}

	public static EcrfFieldStatusEntryEagerModel getCachedFieldStatusEntryModel(ECRFFieldStatusEntryOutVO status,
			HashMap<Long, EcrfFieldStatusEntryEagerModel> fieldStatusEntryModelCache) {
		EcrfFieldStatusEntryEagerModel model;
		if (status != null && fieldStatusEntryModelCache != null) {
			long statusId = status.getId();
			if (fieldStatusEntryModelCache.containsKey(statusId)) {
				model = fieldStatusEntryModelCache.get(statusId);
			} else {
				model = new EcrfFieldStatusEntryEagerModel();
				model.setStatus(status);
				fieldStatusEntryModelCache.put(statusId, model);
			}
		} else {
			model = new EcrfFieldStatusEntryEagerModel();
		}
		return model;
	}

	private ECRFFieldStatusEntryOutVO status;

	public EcrfFieldStatusEntryEagerModel() {
		super();
		resetRows();
	}

	@Override
	protected Collection<ECRFFieldStatusEntryOutVO> getEagerResult(PSFVO psf) {
		if (status != null) {
			try {
				return WebUtil
						.getServiceLocator()
						.getTrialService()
						.getEcrfFieldStatusEntryList(WebUtil.getAuthentication(), status.getStatus().getQueue(), status.getListEntry().getId(), status.getEcrfField().getId(),
								status.getIndex(), false, false, new PSFVO(INITIAL_PSF));
			} catch (ServiceException | AuthorisationException | IllegalArgumentException e) {
			} catch (AuthenticationException e) {
				WebUtil.publishException(e);
			}
		}
		return new ArrayList<ECRFFieldStatusEntryOutVO>();
	}

	@Override
	protected ECRFFieldStatusEntryOutVO getRowElement(Long id) {
		return WebUtil.getEcrfFieldStatusEntry(id);
	}

	public ECRFFieldStatusEntryOutVO getStatus() {
		return status;
	}

	public void setStatus(ECRFFieldStatusEntryOutVO status) {
		this.status = status;
	}
}
