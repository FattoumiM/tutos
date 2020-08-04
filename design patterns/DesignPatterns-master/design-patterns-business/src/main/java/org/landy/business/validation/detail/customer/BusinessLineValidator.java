package org.landy.business.validation.detail.customer;

import org.landy.business.domain.detail.CustomerRequestDetail;
import org.landy.business.domain.file.CustomerRequestFile;
import org.landy.business.validation.Validator;
import org.landy.business.validation.ValidatorChain;
import org.landy.business.validation.ValidatorConstants;
import org.landy.constants.Constants;
import org.landy.exception.BusinessValidationException;
import org.springframework.stereotype.Component;

/**
 * @author landyl
 * @create 2:57 PM 05/09/2018
 */
@Component(ValidatorConstants.BEAN_NAME_CUSTOMER_BUSINESS_LINE)
public class BusinessLineValidator implements Validator<CustomerRequestDetail, CustomerRequestFile> {

    public String doValidate(CustomerRequestDetail detail, CustomerRequestFile file, ValidatorChain chain) throws BusinessValidationException {
        String result = validateBusinessLineLogic(detail);

        if(!Constants.VALID.equals(result)){
            return result;
        }

        return chain.doValidate(detail,file);
    }

    private String validateBusinessLineLogic(CustomerRequestDetail detail) {
        if(detail.getBusinessLine() == null || detail.getBusinessLine().trim().isEmpty()){
            return "BusinessLine is required!";
        }

        String businessLineTrim = detail.getBusinessLine().trim();
        if(businessLineTrim.length() !=1 || "012".indexOf(businessLineTrim) == -1){
            return "Invalid Business Line! An invalid Business Line setting was provided. Accepted Value(s): 0, 1, 2 (0 = Individual, 1 = Small Medium Group, 2 = Medicare).";
        }

        return Constants.VALID;
    }

}
