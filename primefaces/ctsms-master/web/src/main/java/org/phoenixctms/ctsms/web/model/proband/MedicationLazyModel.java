package org.phoenixctms.ctsms.web.model.proband;

import java.util.ArrayList;
import java.util.Collection;

import org.phoenixctms.ctsms.exception.AuthenticationException;
import org.phoenixctms.ctsms.exception.AuthorisationException;
import org.phoenixctms.ctsms.exception.ServiceException;
import org.phoenixctms.ctsms.vo.MedicationOutVO;
import org.phoenixctms.ctsms.vo.PSFVO;
import org.phoenixctms.ctsms.web.model.LazyDataModelBase;
import org.phoenixctms.ctsms.web.util.WebUtil;

public class MedicationLazyModel extends LazyDataModelBase<MedicationOutVO> {

	private Long probandId;

	@Override
	protected Collection<MedicationOutVO> getLazyResult(PSFVO psf) {
		if (probandId != null) {
			try {
				return WebUtil.getServiceLocator().getProbandService().getMedicationList(WebUtil.getAuthentication(), probandId, psf);
			} catch (ServiceException|AuthorisationException|IllegalArgumentException e) {
			} catch (AuthenticationException e) {
				WebUtil.publishException(e);
			}
		}
		return new ArrayList<MedicationOutVO>();
	}

	public Long getProbandId() {
		return probandId;
	}

	@Override
	protected MedicationOutVO getRowElement(Long id) {
		try {
			return WebUtil.getServiceLocator().getProbandService().getMedication(WebUtil.getAuthentication(), id);
		} catch (ServiceException|AuthorisationException|IllegalArgumentException e) {
		} catch (AuthenticationException e) {
			WebUtil.publishException(e);
		}
		return null;
	}

	public void setProbandId(Long probandId) {
		this.probandId = probandId;
	}
}
