package org.landy.business.validation.detail.common;

import org.apache.commons.lang.StringUtils;
import org.landy.business.domain.detail.RequestDetail;
import org.landy.business.domain.file.RequestFile;
import org.landy.business.validation.Validator;
import org.landy.business.validation.ValidatorConstants;
import org.landy.constants.Constants;
import org.landy.exception.BusinessValidationException;
import org.springframework.stereotype.Component;

/**
 * @author landyl
 * @create 2:57 PM 05/09/2018
 */
@Component(ValidatorConstants.BEAN_NAME_CUSTOMER_IS_ACTIVE)
public class IsActiveValidator implements Validator<RequestDetail, RequestFile> {

    @Override
    public boolean doValidate(RequestDetail detail, RequestFile file) throws BusinessValidationException {
        if (StringUtils.isNotEmpty(detail.getIsActive())
                && !"0".equals(detail.getIsActive())
                && !"1".equals(detail.getIsActive())) {
            String result = "An invalid Is Active setting was provided. Accepted Value(s): 0, 1 (0 = No; 1 = Yes).";
            detail.bindValidationResult(Constants.INVALID_IS_ACTIVE,result);
            return false;
        }
        return true;
    }
}
