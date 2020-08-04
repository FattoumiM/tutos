// Generated by: hibernate/SpringHibernateDaoImpl.vsl in andromda-spring-cartridge.
// license-header java merge-point
/**
 * This is only generated once! It will never be overwritten.
 * You can (and have to!) safely modify it by hand.
 */
package org.phoenixctms.ctsms.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.phoenixctms.ctsms.enumeration.PaymentMethod;
import org.phoenixctms.ctsms.query.CategoryCriterion;
import org.phoenixctms.ctsms.query.CategoryCriterion.EmptyPrefixModes;
import org.phoenixctms.ctsms.query.CriteriaUtil;
import org.phoenixctms.ctsms.query.SubCriteriaMap;
import org.phoenixctms.ctsms.security.CipherText;
import org.phoenixctms.ctsms.security.CryptoUtil;
import org.phoenixctms.ctsms.security.reencrypt.FieldReEncrypter;
import org.phoenixctms.ctsms.security.reencrypt.ReEncrypter;
import org.phoenixctms.ctsms.util.CoreUtil;
import org.phoenixctms.ctsms.util.DefaultSettings;
import org.phoenixctms.ctsms.util.L10nUtil;
import org.phoenixctms.ctsms.util.L10nUtil.Locales;
import org.phoenixctms.ctsms.util.ServiceUtil;
import org.phoenixctms.ctsms.util.SettingCodes;
import org.phoenixctms.ctsms.util.Settings;
import org.phoenixctms.ctsms.util.Settings.Bundle;
import org.phoenixctms.ctsms.vo.BankAccountOutVO;
import org.phoenixctms.ctsms.vo.MoneyTransferInVO;
import org.phoenixctms.ctsms.vo.MoneyTransferOutVO;
import org.phoenixctms.ctsms.vo.PSFVO;
import org.phoenixctms.ctsms.vo.ProbandOutVO;
import org.phoenixctms.ctsms.vo.TrialOutVO;
import org.phoenixctms.ctsms.vo.UserOutVO;

/**
 * @see MoneyTransfer
 */
public class MoneyTransferDaoImpl
		extends MoneyTransferDaoBase {

	private final static Collection<ReEncrypter<MoneyTransfer>> RE_ENCRYPTERS = new ArrayList<ReEncrypter<MoneyTransfer>>();
	static {
		RE_ENCRYPTERS.add(new FieldReEncrypter<MoneyTransfer>() {

			@Override
			protected byte[] getIv(MoneyTransfer item) {
				return item.getCommentIv();
			}

			@Override
			protected byte[] getEncrypted(MoneyTransfer item) {
				return item.getEncryptedComment();
			}

			@Override
			protected void setIv(MoneyTransfer item, byte[] iv) {
				item.setCommentIv(iv);
			}

			@Override
			protected void setEncrypted(MoneyTransfer item, byte[] cipherText) {
				item.setEncryptedComment(cipherText);
			}

			@Override
			protected void setHash(MoneyTransfer item, byte[] hash) {
				item.setCommentHash(hash);
			}
		});
	}

	@Override
	protected Collection<ReEncrypter<MoneyTransfer>> getReEncrypters() {
		return RE_ENCRYPTERS;
	}

	private static void applyCostTypeCriterions(org.hibernate.Criteria moneyTransferCriteria,
			Long probandId, Long trialId) {
		if (trialId != null) {
			moneyTransferCriteria.add(Restrictions.eq("trial.id", trialId.longValue()));
		}
		if (probandId != null) {
			moneyTransferCriteria.add(Restrictions.eq("proband.id", probandId.longValue()));
		}
	}

	private static void applyCostTypeCriterions(org.hibernate.Criteria moneyTransferCriteria,
			Long trialDepartmentId, Long trialId, Long probandDepartmentId,
			Long probandId) {
		Criteria trialCriteria = null;
		if (trialDepartmentId != null) {
			trialCriteria = moneyTransferCriteria.createCriteria("trial", CriteriaSpecification.LEFT_JOIN);
		} else if (trialId != null) {
			trialCriteria = moneyTransferCriteria.createCriteria("trial", CriteriaSpecification.INNER_JOIN);
		}
		Criteria probandCriteria = null;
		if (probandDepartmentId != null) {
			probandCriteria = moneyTransferCriteria.createCriteria("proband", CriteriaSpecification.LEFT_JOIN);
		} else if (probandId != null) {
			probandCriteria = moneyTransferCriteria.createCriteria("proband", CriteriaSpecification.INNER_JOIN);
		}
		if (probandDepartmentId != null || probandId != null) {
			if (probandDepartmentId != null) {
				probandCriteria.add(Restrictions.or(Restrictions.isNull("moneyTransfer.proband"), Restrictions.eq("department.id", probandDepartmentId.longValue())));
			}
			if (probandId != null) {
				probandCriteria.add(Restrictions.idEq(probandId.longValue()));
			}
		}
		if (trialDepartmentId != null || trialId != null) {
			if (trialDepartmentId != null) {
				trialCriteria.add(Restrictions.or(Restrictions.isNull("moneyTransfer.trial"), Restrictions.eq("department.id", trialDepartmentId.longValue())));
			}
			if (trialId != null) {
				trialCriteria.add(Restrictions.idEq(trialId.longValue()));
			}
		}
	}

	private org.hibernate.Criteria createMoneyTransferCriteria(String alias) {
		org.hibernate.Criteria moneyTransferCriteria;
		if (alias != null && alias.length() > 0) {
			moneyTransferCriteria = this.getSession().createCriteria(MoneyTransfer.class, alias);
		} else {
			moneyTransferCriteria = this.getSession().createCriteria(MoneyTransfer.class);
		}
		return moneyTransferCriteria;
	}

	@Override
	protected Collection<MoneyTransfer> handleFindByProbandNoTrialMethodCostTypePaid(Long probandId, PaymentMethod method, String costType, Boolean paid) throws Exception {
		org.hibernate.Criteria moneyTransferCriteria = createMoneyTransferCriteria("moneyTransfer");
		if (method != null) {
			moneyTransferCriteria.add(Restrictions.eq("method", method));
		}
		if (paid != null) {
			moneyTransferCriteria.add(Restrictions.eq("paid", paid.booleanValue()));
		}
		moneyTransferCriteria.add(Restrictions.isNull("trial.id"));
		moneyTransferCriteria.add(Restrictions.eq("proband.id", probandId.longValue()));
		CategoryCriterion.apply(moneyTransferCriteria, new CategoryCriterion(costType, "costType", MatchMode.EXACT, EmptyPrefixModes.ALL_ROWS));
		return moneyTransferCriteria.list();
	}

	@Override
	protected Collection<MoneyTransfer> handleFindByProbandTrialMethodCostTypePaidPerson(Long trialDepartmentId, Long trialId, Long probandDepartmentId, Long probandId,
			PaymentMethod method, String costType, Boolean paid, Boolean person, PSFVO psf) throws Exception {
		org.hibernate.Criteria moneyTransferCriteria = createMoneyTransferCriteria("moneyTransfer");
		if (method != null) {
			moneyTransferCriteria.add(Restrictions.eq("method", method));
		}
		if (paid != null) {
			moneyTransferCriteria.add(Restrictions.eq("paid", paid.booleanValue()));
		}
		SubCriteriaMap criteriaMap = new SubCriteriaMap(MoneyTransfer.class, moneyTransferCriteria);
		Criteria trialCriteria = null;
		if (trialDepartmentId != null) {
			trialCriteria = criteriaMap.createCriteria("trial", CriteriaSpecification.LEFT_JOIN);
		} else if (trialId != null) {
			trialCriteria = criteriaMap.createCriteria("trial", CriteriaSpecification.INNER_JOIN);
		}
		if (probandDepartmentId != null || probandId != null || person != null) {
			Criteria probandCriteria = criteriaMap.createCriteria("proband", CriteriaSpecification.INNER_JOIN);
			if (probandDepartmentId != null) {
				probandCriteria.add(Restrictions.eq("department.id", probandDepartmentId.longValue()));
			}
			if (probandId != null) {
				probandCriteria.add(Restrictions.idEq(probandId.longValue()));
			}
			if (person != null) {
				probandCriteria.add(Restrictions.eq("person", person.booleanValue()));
			}
		}
		if (trialDepartmentId != null || trialId != null) {
			if (trialDepartmentId != null) {
				trialCriteria.add(Restrictions.or(Restrictions.isNull("moneyTransfer.trial"), Restrictions.eq("department.id", trialDepartmentId.longValue())));
			}
			if (trialId != null) {
				trialCriteria.add(Restrictions.idEq(trialId.longValue()));
			}
		}
		CategoryCriterion.apply(moneyTransferCriteria, new CategoryCriterion(costType, "costType", MatchMode.EXACT, EmptyPrefixModes.ALL_ROWS));
		CriteriaUtil.applyPSFVO(criteriaMap, psf);
		return moneyTransferCriteria.list();
	}

	@Override
	protected Collection<String> handleFindCostTypes(Long trialDepartmentId, Long trialId, Long probandDepartmentId,
			Long probandId, String costTypePrefix, Integer limit) throws Exception {
		org.hibernate.Criteria moneyTransferCriteria = createMoneyTransferCriteria("moneyTransfer");
		applyCostTypeCriterions(moneyTransferCriteria, trialDepartmentId, trialId, probandDepartmentId, probandId);
		CategoryCriterion.apply(moneyTransferCriteria, new CategoryCriterion(costTypePrefix, "costType", MatchMode.START));
		moneyTransferCriteria.addOrder(Order.asc("costType"));
		moneyTransferCriteria.setProjection(Projections.distinct(Projections.property("costType")));
		CriteriaUtil.applyLimit(limit, Settings.getIntNullable(SettingCodes.MONEY_TRANSFER_COST_TYPE_AUTOCOMPLETE_DEFAULT_RESULT_LIMIT, Bundle.SETTINGS,
				DefaultSettings.MONEY_TRANSFER_COST_TYPE_AUTOCOMPLETE_DEFAULT_RESULT_LIMIT), moneyTransferCriteria);
		return moneyTransferCriteria.list();
	}

	@Override
	protected long handleGetCostTypeCount(Long probandId, Long trialId) throws Exception {
		org.hibernate.Criteria moneyTransferCriteria = createMoneyTransferCriteria(null);
		applyCostTypeCriterions(moneyTransferCriteria, probandId, trialId);
		// return (Long) moneyTransferCriteria.setProjection(Projections.countDistinct("costType")).uniqueResult();
		// postgres: count(distinct f1) yields the number of distinct ***non-null*** values of f1
		moneyTransferCriteria.setProjection(Projections.distinct(Projections.property("costType")));
		return moneyTransferCriteria.list().size();
	}

	@Override
	protected long handleGetCostTypeCount(Long probandId, Long trialId, String costType) throws Exception {
		org.hibernate.Criteria moneyTransferCriteria = createMoneyTransferCriteria(null);
		applyCostTypeCriterions(moneyTransferCriteria, probandId, trialId);
		CategoryCriterion.apply(moneyTransferCriteria, new CategoryCriterion(costType, "costType", MatchMode.EXACT, EmptyPrefixModes.EMPTY_ROWS));
		// return (Long) moneyTransferCriteria.setProjection(Projections.countDistinct("costType")).uniqueResult();
		// postgres: count(distinct f1) yields the number of distinct ***non-null*** values of f1
		moneyTransferCriteria.setProjection(Projections.distinct(Projections.property("costType")));
		return moneyTransferCriteria.list().size();
	}

	@Override
	protected Collection<String> handleGetCostTypes(Long trialDepartmentId, Long trialId, Long probandDepartmentId,
			Long probandId, PaymentMethod method)
			throws Exception {
		org.hibernate.Criteria moneyTransferCriteria = createMoneyTransferCriteria("moneyTransfer");
		if (method != null) {
			moneyTransferCriteria.add(Restrictions.eq("method", method));
		}
		applyCostTypeCriterions(moneyTransferCriteria, trialDepartmentId, trialId, probandDepartmentId, probandId);
		moneyTransferCriteria.setProjection(Projections.distinct(Projections.property("costType")));
		List<String> result = moneyTransferCriteria.list();
		Collections.sort(result, ServiceUtil.MONEY_TRANSFER_COST_TYPE_COMPARATOR); // match TreeMaps!
		return result;
	}

	@Override
	protected Collection<String> handleGetCostTypesNoTrial(Long probandId, PaymentMethod method)
			throws Exception {
		org.hibernate.Criteria moneyTransferCriteria = createMoneyTransferCriteria("moneyTransfer");
		if (method != null) {
			moneyTransferCriteria.add(Restrictions.eq("method", method));
		}
		moneyTransferCriteria.add(Restrictions.isNull("trial.id"));
		moneyTransferCriteria.add(Restrictions.eq("proband.id", probandId.longValue()));
		moneyTransferCriteria.setProjection(Projections.distinct(Projections.property("costType")));
		List<String> result = moneyTransferCriteria.list();
		Collections.sort(result, ServiceUtil.MONEY_TRANSFER_COST_TYPE_COMPARATOR); // match TreeMaps!
		return result;
	}

	@Override
	protected long handleGetCount(Long trialId, Long probandId, Long bankAccountId, PaymentMethod method, String costType, Boolean paid) throws Exception {
		org.hibernate.Criteria moneyTransferCriteria = createMoneyTransferCriteria("moneyTransfer");
		if (trialId != null) {
			moneyTransferCriteria.add(Restrictions.eq("trial.id", trialId.longValue()));
		}
		if (probandId != null) {
			moneyTransferCriteria.add(Restrictions.eq("proband.id", probandId.longValue()));
		}
		if (bankAccountId != null) {
			moneyTransferCriteria.add(Restrictions.eq("bankAccount.id", bankAccountId.longValue()));
		}
		if (method != null) {
			moneyTransferCriteria.add(Restrictions.eq("method", method));
		}
		if (paid != null) {
			moneyTransferCriteria.add(Restrictions.eq("paid", paid.booleanValue()));
		}
		CategoryCriterion.apply(moneyTransferCriteria, new CategoryCriterion(costType, "costType", MatchMode.EXACT, EmptyPrefixModes.ALL_ROWS));
		return (Long) moneyTransferCriteria.setProjection(Projections.rowCount()).uniqueResult();
	}

	/**
	 * Retrieves the entity object that is associated with the specified value object
	 * from the object store. If no such entity object exists in the object store,
	 * a new, blank entity is created
	 */
	private MoneyTransfer loadMoneyTransferFromMoneyTransferInVO(MoneyTransferInVO moneyTransferInVO) {
		MoneyTransfer moneyTransfer = null;
		Long id = moneyTransferInVO.getId();
		if (id != null) {
			moneyTransfer = this.load(id);
		}
		if (moneyTransfer == null) {
			moneyTransfer = MoneyTransfer.Factory.newInstance();
		}
		return moneyTransfer;
	}

	/**
	 * Retrieves the entity object that is associated with the specified value object
	 * from the object store. If no such entity object exists in the object store,
	 * a new, blank entity is created
	 */
	private MoneyTransfer loadMoneyTransferFromMoneyTransferOutVO(MoneyTransferOutVO moneyTransferOutVO) {
		MoneyTransfer moneyTransfer = this.load(moneyTransferOutVO.getId());
		if (moneyTransfer == null) {
			moneyTransfer = MoneyTransfer.Factory.newInstance();
		}
		return moneyTransfer;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public MoneyTransfer moneyTransferInVOToEntity(MoneyTransferInVO moneyTransferInVO) {
		MoneyTransfer entity = this.loadMoneyTransferFromMoneyTransferInVO(moneyTransferInVO);
		this.moneyTransferInVOToEntity(moneyTransferInVO, entity, true);
		return entity;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void moneyTransferInVOToEntity(
			MoneyTransferInVO source,
			MoneyTransfer target,
			boolean copyIfNull) {
		super.moneyTransferInVOToEntity(source, target, copyIfNull);
		Long bankAccountId = source.getBankAccountId();
		Long probandId = source.getProbandId();
		Long trialId = source.getTrialId();
		if (bankAccountId != null) {
			BankAccount bankAccount = this.getBankAccountDao().load(bankAccountId);
			target.setBankAccount(bankAccount);
			bankAccount.addMoneyTransfers(target);
		} else if (copyIfNull) {
			BankAccount bankAccount = target.getBankAccount();
			target.setBankAccount(null);
			if (bankAccount != null) {
				bankAccount.removeMoneyTransfers(target);
			}
		}
		if (probandId != null) {
			Proband proband = this.getProbandDao().load(probandId);
			target.setProband(proband);
			proband.addMoneyTransfers(target);
		} else if (copyIfNull) {
			Proband proband = target.getProband();
			target.setProband(null);
			if (proband != null) {
				proband.removeMoneyTransfers(target);
			}
		}
		if (trialId != null) {
			Trial trial = this.getTrialDao().load(trialId);
			target.setTrial(trial);
			trial.addPayoffs(target);
		} else if (copyIfNull) {
			Trial trial = target.getTrial();
			target.setTrial(null);
			if (trial != null) {
				trial.removePayoffs(target);
			}
		}
		try {
			if (copyIfNull || source.getComment() != null) {
				CipherText cipherText = CryptoUtil.encryptValue(source.getComment());
				target.setCommentIv(cipherText.getIv());
				target.setEncryptedComment(cipherText.getCipherText());
				target.setCommentHash(CryptoUtil.hashForSearch(source.getComment()));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public MoneyTransfer moneyTransferOutVOToEntity(MoneyTransferOutVO moneyTransferOutVO) {
		MoneyTransfer entity = this.loadMoneyTransferFromMoneyTransferOutVO(moneyTransferOutVO);
		this.moneyTransferOutVOToEntity(moneyTransferOutVO, entity, true);
		return entity;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void moneyTransferOutVOToEntity(
			MoneyTransferOutVO source,
			MoneyTransfer target,
			boolean copyIfNull) {
		super.moneyTransferOutVOToEntity(source, target, copyIfNull);
		BankAccountOutVO bankAccountVO = source.getBankAccount();
		ProbandOutVO probandVO = source.getProband();
		TrialOutVO trialVO = source.getTrial();
		UserOutVO modifiedUserVO = source.getModifiedUser();
		if (bankAccountVO != null) {
			BankAccount bankAccount = this.getBankAccountDao().bankAccountOutVOToEntity(bankAccountVO);
			target.setBankAccount(bankAccount);
			bankAccount.addMoneyTransfers(target);
		} else if (copyIfNull) {
			BankAccount bankAccount = target.getBankAccount();
			target.setBankAccount(null);
			if (bankAccount != null) {
				bankAccount.removeMoneyTransfers(target);
			}
		}
		if (probandVO != null) {
			Proband proband = this.getProbandDao().probandOutVOToEntity(probandVO);
			target.setProband(proband);
			proband.addMoneyTransfers(target);
		} else if (copyIfNull) {
			Proband proband = target.getProband();
			target.setProband(null);
			if (proband != null) {
				proband.removeMoneyTransfers(target);
			}
		}
		if (trialVO != null) {
			Trial trial = this.getTrialDao().trialOutVOToEntity(trialVO);
			target.setTrial(trial);
			trial.addPayoffs(target);
		} else if (copyIfNull) {
			Trial trial = target.getTrial();
			target.setTrial(null);
			if (trial != null) {
				trial.removePayoffs(target);
			}
		}
		if (modifiedUserVO != null) {
			target.setModifiedUser(this.getUserDao().userOutVOToEntity(modifiedUserVO));
		} else if (copyIfNull) {
			target.setModifiedUser(null);
		}
		try {
			if (copyIfNull || source.getComment() != null) {
				CipherText cipherText = CryptoUtil.encryptValue(source.getComment());
				target.setCommentIv(cipherText.getIv());
				target.setEncryptedComment(cipherText.getCipherText());
				target.setCommentHash(CryptoUtil.hashForSearch(source.getComment()));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public MoneyTransferInVO toMoneyTransferInVO(final MoneyTransfer entity) {
		return super.toMoneyTransferInVO(entity);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void toMoneyTransferInVO(
			MoneyTransfer source,
			MoneyTransferInVO target) {
		super.toMoneyTransferInVO(source, target);
		BankAccount bankAccount = source.getBankAccount();
		Proband proband = source.getProband();
		Trial trial = source.getTrial();
		if (bankAccount != null) {
			target.setBankAccountId(bankAccount.getId());
		}
		if (proband != null) {
			target.setProbandId(proband.getId());
		}
		if (trial != null) {
			target.setTrialId(trial.getId());
		}
		try {
			target.setComment((String) CryptoUtil.decryptValue(source.getCommentIv(), source.getEncryptedComment()));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public MoneyTransferOutVO toMoneyTransferOutVO(final MoneyTransfer entity) {
		return super.toMoneyTransferOutVO(entity);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void toMoneyTransferOutVO(
			MoneyTransfer source,
			MoneyTransferOutVO target) {
		super.toMoneyTransferOutVO(source, target);
		BankAccount bankAccount = source.getBankAccount();
		Proband proband = source.getProband();
		Trial trial = source.getTrial();
		User modifiedUser = source.getModifiedUser();
		if (bankAccount != null) {
			target.setBankAccount(this.getBankAccountDao().toBankAccountOutVO(bankAccount));
		}
		if (proband != null) {
			target.setProband(this.getProbandDao().toProbandOutVO(proband));
		}
		if (trial != null) {
			target.setTrial(this.getTrialDao().toTrialOutVO(trial));
		}
		if (modifiedUser != null) {
			target.setModifiedUser(this.getUserDao().toUserOutVO(modifiedUser));
		}
		target.setMethod(L10nUtil.createPaymentMethodVO(Locales.USER, source.getMethod()));
		try {
			if (!CoreUtil.isPassDecryption()) {
				throw new Exception();
			}
			target.setComment((String) CryptoUtil.decryptValue(source.getCommentIv(), source.getEncryptedComment()));
			target.setDecrypted(true);
		} catch (Exception e) {
			target.setComment(null);
			target.setDecrypted(false);
		}
	}
}