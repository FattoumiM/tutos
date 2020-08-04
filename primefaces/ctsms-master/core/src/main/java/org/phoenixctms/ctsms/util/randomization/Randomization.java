package org.phoenixctms.ctsms.util.randomization;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.hibernate.LockMode;
import org.phoenixctms.ctsms.adapt.ProbandListEntryTagRandomizeCollisionFinder;
import org.phoenixctms.ctsms.adapt.ProbandListEntryTagStratificationFieldCollisionFinder;
import org.phoenixctms.ctsms.adapt.StratificationRandomizationListCollisionFinder;
import org.phoenixctms.ctsms.domain.InputField;
import org.phoenixctms.ctsms.domain.InputFieldSelectionSetValue;
import org.phoenixctms.ctsms.domain.InputFieldSelectionSetValueDao;
import org.phoenixctms.ctsms.domain.ProbandGroup;
import org.phoenixctms.ctsms.domain.ProbandGroupDao;
import org.phoenixctms.ctsms.domain.ProbandListEntry;
import org.phoenixctms.ctsms.domain.ProbandListEntryDao;
import org.phoenixctms.ctsms.domain.ProbandListEntryTag;
import org.phoenixctms.ctsms.domain.ProbandListEntryTagDao;
import org.phoenixctms.ctsms.domain.ProbandListEntryTagValue;
import org.phoenixctms.ctsms.domain.ProbandListEntryTagValueDao;
import org.phoenixctms.ctsms.domain.RandomizationListCode;
import org.phoenixctms.ctsms.domain.RandomizationListCodeDao;
import org.phoenixctms.ctsms.domain.StratificationRandomizationList;
import org.phoenixctms.ctsms.domain.StratificationRandomizationListDao;
import org.phoenixctms.ctsms.domain.Trial;
import org.phoenixctms.ctsms.domain.TrialDao;
import org.phoenixctms.ctsms.enumeration.InputFieldType;
import org.phoenixctms.ctsms.enumeration.RandomizationMode;
import org.phoenixctms.ctsms.exception.ServiceException;
import org.phoenixctms.ctsms.util.CheckIDUtil;
import org.phoenixctms.ctsms.util.CommonUtil;
import org.phoenixctms.ctsms.util.CoreUtil;
import org.phoenixctms.ctsms.util.DefaultMessages;
import org.phoenixctms.ctsms.util.DefaultSettings;
import org.phoenixctms.ctsms.util.L10nUtil;
import org.phoenixctms.ctsms.util.L10nUtil.Locales;
import org.phoenixctms.ctsms.util.MessageCodes;
import org.phoenixctms.ctsms.util.ServiceExceptionCodes;
import org.phoenixctms.ctsms.util.ServiceUtil;
import org.phoenixctms.ctsms.util.SettingCodes;
import org.phoenixctms.ctsms.util.Settings;
import org.phoenixctms.ctsms.util.Settings.Bundle;
import org.phoenixctms.ctsms.vo.InputFieldSelectionSetValueInVO;
import org.phoenixctms.ctsms.vo.ProbandGroupInVO;
import org.phoenixctms.ctsms.vo.ProbandListEntryTagInVO;
import org.phoenixctms.ctsms.vo.RandomizationInfoVO;
import org.phoenixctms.ctsms.vo.RandomizationListCodeInVO;
import org.phoenixctms.ctsms.vo.RandomizationListCodeOutVO;
import org.phoenixctms.ctsms.vo.RandomizationListCodeValueVO;
import org.phoenixctms.ctsms.vo.RandomizationListInfoVO;
import org.phoenixctms.ctsms.vo.StratificationRandomizationListInVO;
import org.phoenixctms.ctsms.vo.TrialInVO;

public abstract class Randomization {

	public enum RandomizationType {
		GROUP, TAG_SELECT, TAG_TEXT
	}

	private final static String RANDOMIZATION_SEED_RANDOM_ALGORITHM = CoreUtil.RANDOM_ALGORITHM;
	private final static String RANDOMIZATION_LIST_SEED_RANDOM_ALGORITHM = CoreUtil.RANDOM_ALGORITHM;
	private final static String RANDOMIZATION_BLOCK_LINE_SEPARATOR = "\n";
	public final static String RANDOMIZATION_BLOCK_SPLIT_SEPARATOR = ";";
	private final static String RANDOMIZATION_BLOCK_SPLIT_REGEX_PATTERN = "(\\r\\n)|\\r|\\n|[," + Pattern.quote(RANDOMIZATION_BLOCK_SPLIT_SEPARATOR) + "]";
	private final static Pattern RANDOMIZATION_BLOCK_SPLIT_REGEXP = Pattern.compile(RANDOMIZATION_BLOCK_SPLIT_REGEX_PATTERN);
	private final static boolean TRIM_PROBAND_GROUP_TOKEN = true;
	private final static boolean TRIM_INPUT_FIELD_SELECTION_SET_VALUE_VALUE = true;
	private final static boolean TRIM_INPUT_FIELD_TEXT_VALUE = true;

	public final static void checkInputFieldSelectionSetValueInput(InputFieldSelectionSetValueInVO inputFieldSelectionSetValueIn, ProbandListEntryTagDao probandListEntryTagDao)
			throws ServiceException {
		if (probandListEntryTagDao.getCountByFieldStratificationRandomize(inputFieldSelectionSetValueIn.getFieldId(), null, true) > 0) {
			checkInputFieldSelectionSetValueValue(inputFieldSelectionSetValueIn.getValue());
		}
	}

	private final static void checkInputFieldSelectionSetValueValue(String value) throws ServiceException {
		if (RANDOMIZATION_BLOCK_SPLIT_REGEXP.matcher(value).find()) {
			throw L10nUtil.initServiceException(ServiceExceptionCodes.INVALID_INPUT_FIELD_SELECTION_SET_VALUE_VALUE, value);
		}
		if (TRIM_INPUT_FIELD_SELECTION_SET_VALUE_VALUE && !value.trim().equals(value)) {
			throw L10nUtil.initServiceException(ServiceExceptionCodes.WHITESPACE_INPUT_FIELD_SELECTION_SET_VALUE_VALUE, value);
		}
	}

	public final static void checkProbandGroupInput(Trial trial, ProbandGroupInVO probandGroupIn) throws ServiceException {
		if (probandGroupIn.getRandomize()) {
			checkProbandGroupToken(probandGroupIn.getToken());
		}
	}

	private final static void checkProbandGroupToken(String token) throws ServiceException {
		if (RANDOMIZATION_BLOCK_SPLIT_REGEXP.matcher(token).find()) {
			throw L10nUtil.initServiceException(ServiceExceptionCodes.INVALID_PROBAND_GROUP_TOKEN, token);
		}
		if (TRIM_PROBAND_GROUP_TOKEN && !token.trim().equals(token)) {
			throw L10nUtil.initServiceException(ServiceExceptionCodes.WHITESPACE_PROBAND_GROUP_TOKEN, token);
		}
	}

	public final static void checkProbandListEntryTagInput(Trial trial, InputField field, ProbandListEntryTagInVO listTagIn, TrialDao trialDao, ProbandGroupDao probandGroupDao,
			ProbandListEntryDao probandListEntryDao,
			StratificationRandomizationListDao stratificationRandomizationListDao, ProbandListEntryTagDao probandListEntryTagDao,
			InputFieldSelectionSetValueDao inputFieldSelectionSetValueDao, ProbandListEntryTagValueDao probandListEntryTagValueDao,
			RandomizationListCodeDao randomizationListCodeDao)
			throws ServiceException {
		if (listTagIn.isStratification()) {
			if (listTagIn.isRandomize()) {
				throw L10nUtil.initServiceException(ServiceExceptionCodes.PROBAND_LIST_ENTRY_TAG_STRATIFIKATION_AND_RANDOMIZE);
			}
			if (!ServiceUtil.isInputFieldTypeSelectOne(field.getFieldType())) {
				throw L10nUtil.initServiceException(ServiceExceptionCodes.PROBAND_LIST_ENTRY_TAG_STRATIFICATION_FIELD_NOT_SELECT_ONE);
			}
			if ((new ProbandListEntryTagStratificationFieldCollisionFinder(trialDao, probandListEntryTagDao)).collides(listTagIn)) {
				throw L10nUtil.initServiceException(ServiceExceptionCodes.PROBAND_LIST_ENTRY_TAG_STRATIFICATION_FIELD_NOT_UNIQUE);
			}
		} else if (listTagIn.isRandomize()) {
			if (trial.getRandomization() != null) {
				getInstance(trial.getRandomization(), trialDao, probandGroupDao, probandListEntryDao, stratificationRandomizationListDao, probandListEntryTagDao,
						inputFieldSelectionSetValueDao, probandListEntryTagValueDao, randomizationListCodeDao).checkProbandListEntryTagField(trial, field);
			}
			if ((new ProbandListEntryTagRandomizeCollisionFinder(trialDao, probandListEntryTagDao)).collides(listTagIn)) {
				throw L10nUtil.initServiceException(ServiceExceptionCodes.PROBAND_LIST_ENTRY_TAG_RANDOMIZE_NOT_UNIQUE);
			}
			Iterator<InputFieldSelectionSetValue> it = field.getSelectionSetValues().iterator();
			while (it.hasNext()) {
				checkInputFieldSelectionSetValueValue(it.next().getValue());
			}
		}
	}

	public final static ArrayList<RandomizationListCodeInVO> checkStratificationRandomizationListInput(Trial trial, StratificationRandomizationListInVO randomizationListIn,
			Collection<RandomizationListCodeInVO> codes,
			boolean checkCollision,
			TrialDao trialDao, ProbandGroupDao probandGroupDao, ProbandListEntryDao probandListEntryDao,
			StratificationRandomizationListDao stratificationRandomizationListDao, ProbandListEntryTagDao probandListEntryTagDao,
			InputFieldSelectionSetValueDao inputFieldSelectionSetValueDao, ProbandListEntryTagValueDao probandListEntryTagValueDao,
			RandomizationListCodeDao randomizationListCodeDao) throws ServiceException {
		Iterator<ProbandListEntryTag> it = probandListEntryTagDao.findByTrialFieldStratificationRandomize(randomizationListIn.getTrialId(), null, true, null).iterator();
		HashMap<Long, InputField> tagFieldMap = new HashMap<Long, InputField>();
		while (it.hasNext()) {
			InputField field = it.next().getField();
			tagFieldMap.put(field.getId(), field);
		}
		if (tagFieldMap.size() == 0) {
			throw L10nUtil.initServiceException(ServiceExceptionCodes.NO_PROBAND_LIST_ENTRY_TAG_STRATIFICATION_FIELDS);
		}
		HashSet<Long> selectionSetValueFieldIds = new HashSet<Long>(randomizationListIn.getSelectionSetValueIds().size());
		Iterator<Long> selectionSetValueIdsIt = randomizationListIn.getSelectionSetValueIds().iterator();
		while (selectionSetValueIdsIt.hasNext()) {
			InputFieldSelectionSetValue selectionSetValue = CheckIDUtil.checkInputFieldSelectionSetValueId(selectionSetValueIdsIt.next(), inputFieldSelectionSetValueDao);
			InputField field = selectionSetValue.getField();
			if (!selectionSetValueFieldIds.add(field.getId())) {
				throw L10nUtil.initServiceException(ServiceExceptionCodes.AMBIGOUS_SELECTION_SET_VALUE_FOR_STRATIFICATION_FIELD, selectionSetValue.getNameL10nKey(),
						field.getNameL10nKey());
			}
			if (tagFieldMap.remove(field.getId()) == null) {
				throw L10nUtil.initServiceException(ServiceExceptionCodes.SELECTION_SET_VALUE_FIELD_NOT_A_STRATIFICATION_FIELD, selectionSetValue.getNameL10nKey(),
						field.getNameL10nKey());
			}
		}
		if (tagFieldMap.size() > 0) {
			Iterator<InputField> missingFieldsIt = tagFieldMap.values().iterator();
			StringBuilder sb = new StringBuilder();
			while (missingFieldsIt.hasNext()) {
				if (sb.length() > 0) {
					sb.append(", ");
				}
				sb.append(missingFieldsIt.next().getNameL10nKey());
			}
			throw L10nUtil.initServiceException(ServiceExceptionCodes.MISSING_STRATIFICATION_FIELD_SELECTION_SET_VALUES, sb.toString());
		}
		if (checkCollision && (new StratificationRandomizationListCollisionFinder(trialDao, stratificationRandomizationListDao)).collides(randomizationListIn)) {
			throw L10nUtil.initServiceException(ServiceExceptionCodes.STRATIFICATION_FIELD_SELECTION_SET_VALUES_NOT_UNIQUE);
		}
		if (trial.getRandomization() != null) {
			Randomization randomization = getInstance(trial.getRandomization(), trialDao, probandGroupDao, probandListEntryDao, stratificationRandomizationListDao,
					probandListEntryTagDao,
					inputFieldSelectionSetValueDao, probandListEntryTagValueDao, randomizationListCodeDao);
			randomization.checkStratificationRandomizationListRandomizationListInput(trial, randomizationListIn);
			return randomization.checkRandomizationListCodesInput(randomizationListIn.getRandomizationList(), codes);
		}
		return null;
	}

	public final static ArrayList<RandomizationListCodeInVO> checkTrialInput(Trial trial, TrialInVO trialIn, Collection<RandomizationListCodeInVO> codes, TrialDao trialDao,
			ProbandGroupDao probandGroupDao, ProbandListEntryDao probandListEntryDao,
			StratificationRandomizationListDao stratificationRandomizationListDao, ProbandListEntryTagDao probandListEntryTagDao,
			InputFieldSelectionSetValueDao inputFieldSelectionSetValueDao, ProbandListEntryTagValueDao probandListEntryTagValueDao,
			RandomizationListCodeDao randomizationListCodeDao) throws ServiceException {
		if (trialIn.getRandomization() == null) {
			if (trialIn.getRandomizationList() != null) {
				throw L10nUtil.initServiceException(ServiceExceptionCodes.TRIAL_RANDOMIZATION_LIST_NOT_NULL);
			}
		} else {
			Randomization randomization = getInstance(trialIn.getRandomization(), trialDao, probandGroupDao, probandListEntryDao, stratificationRandomizationListDao,
					probandListEntryTagDao,
					inputFieldSelectionSetValueDao, probandListEntryTagValueDao, randomizationListCodeDao);
			randomization.checkTrialRandomizationInput(trial, trialIn);
			return randomization.checkRandomizationListCodesInput(trialIn.getRandomizationList(), codes);
		}
		return null;
	}

	protected final static HashMap<Long, InputFieldSelectionSetValue> getInputFieldSelectionSetValueIdMap(Collection<InputFieldSelectionSetValue> inputFieldSelectionSetValues) {
		HashMap<Long, InputFieldSelectionSetValue> result = new HashMap<Long, InputFieldSelectionSetValue>();
		Iterator<InputFieldSelectionSetValue> it = inputFieldSelectionSetValues.iterator();
		while (it.hasNext()) {
			InputFieldSelectionSetValue inputFieldSelectionSetValue = it.next();
			result.put(inputFieldSelectionSetValue.getId(), inputFieldSelectionSetValue);
		}
		return result;
	}

	protected final static HashMap<String, InputFieldSelectionSetValue> getInputFieldSelectionSetValueValueMap(
			Collection<InputFieldSelectionSetValue> inputFieldSelectionSetValues) {
		HashMap<String, InputFieldSelectionSetValue> result = new HashMap<String, InputFieldSelectionSetValue>();
		Iterator<InputFieldSelectionSetValue> it = inputFieldSelectionSetValues.iterator();
		while (it.hasNext()) {
			InputFieldSelectionSetValue inputFieldSelectionSetValue = it.next();
			result.put(inputFieldSelectionSetValue.getValue(), inputFieldSelectionSetValue);
		}
		return result;
	}

	public final static Randomization getInstance(RandomizationMode mode, TrialDao trialDao, ProbandGroupDao probandGroupDao, ProbandListEntryDao probandListEntryDao,
			StratificationRandomizationListDao stratificationRandomizationListDao, ProbandListEntryTagDao probandListEntryTagDao,
			InputFieldSelectionSetValueDao inputFieldSelectionSetValueDao, ProbandListEntryTagValueDao probandListEntryTagValueDao,
			RandomizationListCodeDao randomizationListCodeDao) {
		switch (mode) {
			case GROUP_COIN:
				return new GroupCoinRandomization(trialDao, probandGroupDao, probandListEntryDao, stratificationRandomizationListDao, probandListEntryTagDao,
						inputFieldSelectionSetValueDao, probandListEntryTagValueDao, randomizationListCodeDao);
			case GROUP_ADAPTIVE:
				return new GroupAdaptiveRandomization(trialDao, probandGroupDao, probandListEntryDao, stratificationRandomizationListDao, probandListEntryTagDao,
						inputFieldSelectionSetValueDao, probandListEntryTagValueDao, randomizationListCodeDao);
			case GROUP_LIST:
				return new GroupListRandomization(trialDao, probandGroupDao, probandListEntryDao, stratificationRandomizationListDao, probandListEntryTagDao,
						inputFieldSelectionSetValueDao, probandListEntryTagValueDao, randomizationListCodeDao);
			case GROUP_STRATIFIED:
				return new GroupStratifiedRandomization(trialDao, probandGroupDao, probandListEntryDao, stratificationRandomizationListDao, probandListEntryTagDao,
						inputFieldSelectionSetValueDao, probandListEntryTagValueDao, randomizationListCodeDao);
			case TAG_COIN:
				return new TagCoinRandomization(trialDao, probandGroupDao, probandListEntryDao, stratificationRandomizationListDao, probandListEntryTagDao,
						inputFieldSelectionSetValueDao, probandListEntryTagValueDao, randomizationListCodeDao);
			case TAG_ADAPTIVE:
				return new TagAdaptiveRandomization(trialDao, probandGroupDao, probandListEntryDao, stratificationRandomizationListDao, probandListEntryTagDao,
						inputFieldSelectionSetValueDao, probandListEntryTagValueDao, randomizationListCodeDao);
			case TAG_SELECT_LIST:
				return new TagSelectListRandomization(trialDao, probandGroupDao, probandListEntryDao, stratificationRandomizationListDao, probandListEntryTagDao,
						inputFieldSelectionSetValueDao, probandListEntryTagValueDao, randomizationListCodeDao);
			case TAG_SELECT_STRATIFIED:
				return new TagSelectStratifiedRandomization(trialDao, probandGroupDao, probandListEntryDao, stratificationRandomizationListDao, probandListEntryTagDao,
						inputFieldSelectionSetValueDao, probandListEntryTagValueDao, randomizationListCodeDao);
			case TAG_TEXT_LIST:
				return new TagTextListRandomization(trialDao, probandGroupDao, probandListEntryDao, stratificationRandomizationListDao, probandListEntryTagDao,
						inputFieldSelectionSetValueDao, probandListEntryTagValueDao, randomizationListCodeDao);
			case TAG_TEXT_STRATIFIED:
				return new TagTextStratifiedRandomization(trialDao, probandGroupDao, probandListEntryDao, stratificationRandomizationListDao, probandListEntryTagDao,
						inputFieldSelectionSetValueDao, probandListEntryTagValueDao, randomizationListCodeDao);
			default:
				throw new IllegalArgumentException(L10nUtil.getMessage(MessageCodes.UNSUPPORTED_RANDOMIZATION_MODE, DefaultMessages.UNSUPPORTED_RANDOMIZATION_MODE, mode));
		}
	}

	protected final static HashMap<Long, ProbandGroup> getProbandGroupIdMap(Collection<ProbandGroup> probandGroups) {
		HashMap<Long, ProbandGroup> result = new HashMap<Long, ProbandGroup>();
		Iterator<ProbandGroup> it = probandGroups.iterator();
		while (it.hasNext()) {
			ProbandGroup probandGroup = it.next();
			result.put(probandGroup.getId(), probandGroup);
		}
		return result;
	}

	protected final static HashMap<String, ProbandGroup> getProbandGroupTokenMap(Collection<ProbandGroup> probandGroups) {
		HashMap<String, ProbandGroup> result = new HashMap<String, ProbandGroup>();
		Iterator<ProbandGroup> it = probandGroups.iterator();
		while (it.hasNext()) {
			ProbandGroup probandGroup = it.next();
			result.put(probandGroup.getToken(), probandGroup);
		}
		return result;
	}

	protected final static void splitInputFieldSelectionSetValueValues(String randomizationBlock, HashMap<String, InputFieldSelectionSetValue> inputFieldSelectionSetValueMap,
			ArrayList<String> values)
			throws ServiceException {
		if (!CommonUtil.isEmptyString(randomizationBlock)) {
			String[] block = RANDOMIZATION_BLOCK_SPLIT_REGEXP.split(randomizationBlock, -1);
			for (int i = 0; i < block.length; i++) {
				String value = (TRIM_INPUT_FIELD_SELECTION_SET_VALUE_VALUE ? block[i].trim() : block[i]);
				if (!CommonUtil.isEmptyString(value)) {
					if (inputFieldSelectionSetValueMap.containsKey(value)) {
						if (values != null) {
							values.add(value);
						}
					} else {
						throw L10nUtil.initServiceException(ServiceExceptionCodes.UNKNOWN_INPUT_FIELD_SELECTION_SET_VALUE_VALUE, value);
					}
				}
			}
		}
	}

	protected final static void splitInputFieldTextValues(String randomizationBlock, Collection<String> values)
			throws ServiceException {
		if (!CommonUtil.isEmptyString(randomizationBlock)) {
			String[] block = RANDOMIZATION_BLOCK_SPLIT_REGEXP.split(randomizationBlock, -1);
			for (int i = 0; i < block.length; i++) {
				String value = (TRIM_INPUT_FIELD_TEXT_VALUE ? block[i].trim() : block[i]);
				if (!CommonUtil.isEmptyString(value)) {
					if (values != null) {
						values.add(value);
					}
				}
			}
		}
	}

	protected ArrayList<RandomizationListCodeInVO> checkRandomizationListCodesInput(
			String randomizationList, Collection<RandomizationListCodeInVO> codes) throws ServiceException {
		if (codes != null) {
			throw L10nUtil.initServiceException(ServiceExceptionCodes.RANDOMIZATION_LIST_CODES_NOT_SUPPORTED,
					L10nUtil.getRandomizationModeName(Locales.USER, getRandomizationMode().name()));
		}
		return null;
	}

	protected final static ArrayList<RandomizationListCodeInVO> sanitizeRandomizationListCodesInput(String randomizationBlock, Collection<RandomizationListCodeInVO> codes)
			throws ServiceException {
		if (codes != null) {
			LinkedHashSet<String> textValues = new LinkedHashSet<String>();
			splitInputFieldTextValues(randomizationBlock, textValues);
			LinkedHashMap<String, RandomizationListCodeInVO> codeMap = new LinkedHashMap<String, RandomizationListCodeInVO>(codes.size());
			Iterator<RandomizationListCodeInVO> codesIt = codes.iterator();
			while (codesIt.hasNext()) {
				RandomizationListCodeInVO code = codesIt.next();
				String value = code.getCode();
				if (CommonUtil.isEmptyString(value)) {
					throw L10nUtil.initServiceException(ServiceExceptionCodes.EMPTY_RANDOMIZATION_CODE_VALUE);
				} else {
					value = (TRIM_INPUT_FIELD_TEXT_VALUE ? value.trim() : value);
					if (codeMap.containsKey(value)) {
						throw L10nUtil.initServiceException(ServiceExceptionCodes.DUPLICATE_RANDOMIZATION_CODE_VALUE, value);
					} else if (textValues.remove(value)) {
						if (TRIM_INPUT_FIELD_TEXT_VALUE) {
							code = new RandomizationListCodeInVO(code);
							code.setCode(value);
						}
						codeMap.put(value, code);
					} else {
						throw L10nUtil.initServiceException(ServiceExceptionCodes.UNKNOWN_RANDOMIZATION_CODE_VALUE, value);
					}
				}
			}
			if (textValues.size() > 0) {
				Iterator<String> it = textValues.iterator();
				StringBuilder sb = new StringBuilder();
				while (it.hasNext()) {
					if (sb.length() > 0) {
						sb.append(", ");
					}
					sb.append(it.next());
				}
				throw L10nUtil.initServiceException(ServiceExceptionCodes.MISSING_RANDOMIZATION_CODE_VALUES, sb.toString());
			} else {
				return new ArrayList<RandomizationListCodeInVO>(codeMap.values());
			}
		}
		return null;
	}

	protected final static void splitProbandGroupTokens(String randomizationBlock, HashMap<String, ProbandGroup> probandGroupMap, ArrayList<String> tokens)
			throws ServiceException {
		if (!CommonUtil.isEmptyString(randomizationBlock)) {
			String[] block = RANDOMIZATION_BLOCK_SPLIT_REGEXP.split(randomizationBlock, -1);
			for (int i = 0; i < block.length; i++) {
				String token = (TRIM_PROBAND_GROUP_TOKEN ? block[i].trim() : block[i]);
				if (!CommonUtil.isEmptyString(token)) {
					if (probandGroupMap.containsKey(token)) {
						if (tokens != null) {
							tokens.add(token);
						}
					} else {
						throw L10nUtil.initServiceException(ServiceExceptionCodes.UNKNOWN_PROBAND_GROUP_TOKEN, token);
					}
				}
			}
		}
	}

	protected TrialDao trialDao;
	protected ProbandGroupDao probandGroupDao;
	protected ProbandListEntryDao probandListEntryDao;
	protected StratificationRandomizationListDao stratificationRandomizationListDao;
	protected ProbandListEntryTagDao probandListEntryTagDao;
	protected InputFieldSelectionSetValueDao inputFieldSelectionSetValueDao;
	protected ProbandListEntryTagValueDao probandListEntryTagValueDao;
	protected RandomizationListCodeDao randomizationListCodeDao;
	private Random random;
	private boolean locked;
	protected RandomizationInfoVO randomizationInfo;
	private RandomizationListInfoVO randomizationListInfo;

	protected Randomization(TrialDao trialDao, ProbandGroupDao probandGroupDao, ProbandListEntryDao probandListEntryDao,
			StratificationRandomizationListDao stratificationRandomizationListDao, ProbandListEntryTagDao probandListEntryTagDao,
			InputFieldSelectionSetValueDao inputFieldSelectionSetValueDao, ProbandListEntryTagValueDao probandListEntryTagValueDao,
			RandomizationListCodeDao randomizationListCodeDao) {
		this.trialDao = trialDao;
		this.probandGroupDao = probandGroupDao;
		this.probandListEntryDao = probandListEntryDao;
		this.stratificationRandomizationListDao = stratificationRandomizationListDao;
		this.probandListEntryTagDao = probandListEntryTagDao;
		this.inputFieldSelectionSetValueDao = inputFieldSelectionSetValueDao;
		this.probandListEntryTagValueDao = probandListEntryTagValueDao;
		this.randomizationListCodeDao = randomizationListCodeDao;
		locked = false;
	}

	protected void checkProbandListEntryTagField(Trial trial, InputField field) throws ServiceException {
		if (!(ServiceUtil.isInputFieldTypeSelectOne(field.getFieldType()) || InputFieldType.SINGLE_LINE_TEXT.equals(field.getFieldType()))) {
			throw L10nUtil.initServiceException(ServiceExceptionCodes.PROBAND_LIST_ENTRY_TAG_RANDOMIZE_FIELD_NOT_SELECT_ONE_OR_SINGLE_LINE_TEXT);
		}
	}

	public ProbandListEntryTag checkRandomizeProbandListEntryTag(Trial trial) throws ServiceException {
		ProbandListEntryTag randomizationTag;
		try {
			randomizationTag = probandListEntryTagDao.findByTrialFieldStratificationRandomize(trial.getId(), null, null, true).iterator().next();
		} catch (NoSuchElementException e) {
			throw L10nUtil.initServiceException(ServiceExceptionCodes.NO_RANDOMIZE_PROBAND_LIST_ENTRY_TAG);
		}
		checkProbandListEntryTagField(trial, randomizationTag.getField());
		return randomizationTag;
	}

	protected final ProbandListEntryTagValue getRandomizationTagValue(ProbandListEntry listEntry) throws ServiceException {
		ProbandListEntryTag tag = checkRandomizeProbandListEntryTag(listEntry.getTrial());
		try {
			return probandListEntryTagValueDao.findByListEntryListEntryTag(listEntry.getId(), tag.getId()).iterator().next();
		} catch (NoSuchElementException e) {
			throw L10nUtil.initServiceException(ServiceExceptionCodes.RANDOMIZATION_PROBAND_LIST_ENTRY_TAG_VALUE_REQUIRED,
					probandListEntryTagDao.toProbandListEntryTagOutVO(tag).getUniqueName());
		}
	}

	protected void checkStratificationRandomizationListRandomizationListInput(Trial trial, StratificationRandomizationListInVO randomizationListIn)
			throws ServiceException {
	}

	protected void checkTrialRandomizationInput(Trial trial, TrialInVO trialIn) throws ServiceException {
	}

	public final String generateRandomizationList(Trial trial, int n) throws Exception {
		randomizationListInfo = new RandomizationListInfoVO();
		TreeSet<String> items = getRandomizationListItems(trial);
		randomizationListInfo.setItems(new ArrayList<String>(items));
		if (n > 0) {
			randomizationListInfo.setN(n);
			StringBuilder sb = new StringBuilder();
			int blocks = (int) Math.ceil(((double) n) / ((double) items.size()));
			int count = 0;
			long seed = SecureRandom.getInstance(RANDOMIZATION_LIST_SEED_RANDOM_ALGORITHM).nextLong();
			Random random = new Random(seed); // reproducable
			randomizationListInfo.setPrngClass(CoreUtil.getPrngClassDescription(random));
			randomizationListInfo.setSeed(seed);
			for (int i = 0; i < blocks; i++) {
				ArrayList<String> permutation = new ArrayList<String>(items);
				Collections.shuffle(permutation, random);
				boolean newLine = true;
				Iterator<String> itemIt = permutation.iterator();
				while (itemIt.hasNext() && count < n) {
					if (sb.length() > 0) {
						if (newLine) {
							sb.append(RANDOMIZATION_BLOCK_LINE_SEPARATOR);
						} else {
							sb.append(RANDOMIZATION_BLOCK_SPLIT_SEPARATOR);
						}
					}
					newLine = false;
					String item = itemIt.next();
					sb.append(item);
					count++;
					randomizationListInfo.getRandomizationLists().add(item);
				}
			}
			return sb.toString();
		} else {
			throw L10nUtil.initServiceException(ServiceExceptionCodes.POPULATION_LESS_THAN_ONE);
		}
	}

	protected final Random getRandom(Trial trial) throws NoSuchAlgorithmException, ClassNotFoundException, IOException {
		if (random == null) {
			byte[] randomSerialized = trial.getRandom();
			if (randomSerialized != null) {
				random = (Random) CoreUtil.deserialize(randomSerialized);
			} else {
				long seed = SecureRandom.getInstance(RANDOMIZATION_SEED_RANDOM_ALGORITHM).nextLong();
				random = new Random(seed); // reproducable (securerandom cannot be initialized with a dedicated seed)
				randomizationInfo.setPrngClass(CoreUtil.getPrngClassDescription(random));
				randomizationInfo.setSeed(seed);
			}
		}
		return random;
	}

	protected final Collection<ProbandGroup> getRandomizationGroups(Trial trial) throws ServiceException {
		return probandGroupDao.findByTrialRandomize(trial.getId(), true);
	}

	public final RandomizationInfoVO getRandomizationInfoVO() {
		return randomizationInfo;
	}

	protected final Collection<InputFieldSelectionSetValue> getRandomizationInputFieldSelectionSetValues(Trial trial) {
		try {
			return probandListEntryTagDao.findByTrialFieldStratificationRandomize(trial.getId(), null, null, true).iterator().next().getField().getSelectionSetValues();
		} catch (NoSuchElementException e) {
			return new ArrayList<InputFieldSelectionSetValue>();
		}
	}

	protected final TreeSet<String> getRandomizationListGroups(Trial trial) throws Exception {
		TreeSet<String> tokens = new TreeSet<String>();
		Iterator<ProbandGroup> it = getRandomizationGroups(trial).iterator();
		while (it.hasNext()) {
			String token = it.next().getToken();
			checkProbandGroupToken(token);
			tokens.add(token);
		}
		if (tokens.size() == 0) {
			throw L10nUtil.initServiceException(ServiceExceptionCodes.NO_PROBAND_GROUPS);
		}
		return tokens;
	}

	public final RandomizationListInfoVO getRandomizationListInfoVO() {
		return randomizationListInfo;
	}

	protected final TreeSet<String> getRandomizationListInputFieldSelectionSetValueValues(Trial trial) throws Exception {
		TreeSet<String> values = new TreeSet<String>();
		Iterator<InputFieldSelectionSetValue> it = getRandomizationInputFieldSelectionSetValues(trial).iterator();
		while (it.hasNext()) {
			String value = it.next().getValue();
			checkInputFieldSelectionSetValueValue(value);
			values.add(value);
		}
		if (values.size() == 0) {
			throw L10nUtil.initServiceException(ServiceExceptionCodes.NO_INPUT_FIELD_SELECTION_SET_VALUES);
		}
		return values;
	}

	protected TreeSet<String> getRandomizationListItems(Trial trial) throws Exception {
		throw L10nUtil.initServiceException(ServiceExceptionCodes.RANDOMIZATION_LISTS_NOT_SUPPORTED,
				L10nUtil.getRandomizationModeName(Locales.USER, getRandomizationMode().name()));
	}

	protected abstract RandomizationMode getRandomizationMode();

	public final InputFieldSelectionSetValue getRandomizedInputFieldSelectionSetValue(Trial trial, ProbandListEntry exclude) throws Exception {
		if (!locked) {
			trialDao.lock(trial, LockMode.PESSIMISTIC_WRITE);
			locked = true;
		}
		randomizationInfo = new RandomizationInfoVO();
		randomizationInfo.setRandomization(L10nUtil.createRandomizationModeVO(Locales.JOURNAL, getRandomizationMode()));
		InputFieldSelectionSetValue value = randomizeInputFieldSelectionSetValue(trial, exclude);
		if (value == null) {
			throw L10nUtil.initServiceException(ServiceExceptionCodes.NO_INPUT_FIELD_SELECTION_SET_VALUE_RANDOMIZED);
		} else {
			randomizationInfo.setRandomized(value.getValue());
		}
		return value;
	}

	public final String getRandomizedInputFieldTextValue(Trial trial, ProbandListEntry exclude) throws Exception {
		if (!locked) {
			trialDao.lock(trial, LockMode.PESSIMISTIC_WRITE);
			locked = true;
		}
		randomizationInfo = new RandomizationInfoVO();
		randomizationInfo.setRandomization(L10nUtil.createRandomizationModeVO(Locales.JOURNAL, getRandomizationMode()));
		String value = randomizeInputFieldTextValue(trial, exclude);
		if (value == null) {
			throw L10nUtil.initServiceException(ServiceExceptionCodes.NO_INPUT_FIELD_TEXT_VALUE_RANDOMIZED);
		} else {
			randomizationInfo.setRandomized(value);
		}
		return value;
	}

	public final ProbandGroup getRandomizedProbandGroup(Trial trial, ProbandListEntry exclude) throws Exception {
		if (!locked) {
			trialDao.lock(trial, LockMode.PESSIMISTIC_WRITE);
			locked = true;
		}
		randomizationInfo = new RandomizationInfoVO();
		randomizationInfo.setRandomization(L10nUtil.createRandomizationModeVO(Locales.JOURNAL, getRandomizationMode()));
		ProbandGroup group = randomizeProbandGroup(trial, exclude);
		if (group == null) {
			throw L10nUtil.initServiceException(ServiceExceptionCodes.NO_PROBAND_GROUP_RANDOMIZED);
		} else {
			randomizationInfo.setRandomized(group.getToken());
		}
		return group;
	}

	public RandomizationListCode getRandomizationListCode(ProbandListEntry listEntry) throws ServiceException {
		throw L10nUtil.initServiceException(ServiceExceptionCodes.RANDOMIZATION_LIST_CODES_NOT_SUPPORTED,
				L10nUtil.getRandomizationModeName(Locales.USER, getRandomizationMode().name()));
	}

	protected final StratificationRandomizationList getStratificationRandomizationList(Trial trial, ProbandListEntry exclude) throws ServiceException {
		if (exclude != null) {
			Iterator<ProbandListEntryTag> tagIt = probandListEntryTagDao.findByTrialFieldStratificationRandomize(trial.getId(), null, true, null).iterator();
			HashSet<Long> selectionSetValueIds = new HashSet<Long>();
			StringBuilder sb = new StringBuilder();
			while (tagIt.hasNext()) {
				ProbandListEntryTag tag = tagIt.next();
				InputFieldSelectionSetValue selectionSetValue;
				try {
					selectionSetValue = probandListEntryTagValueDao.findByListEntryListEntryTag(exclude.getId(), tag.getId()).iterator().next().getValue().getSelectionValues()
							.iterator().next();
				} catch (NoSuchElementException e) {
					throw L10nUtil.initServiceException(ServiceExceptionCodes.STRATIFICATION_PROBAND_LIST_ENTRY_TAG_VALUE_REQUIRED,
							probandListEntryTagDao.toProbandListEntryTagOutVO(tag).getUniqueName());
				}
				selectionSetValueIds.add(selectionSetValue.getId());
				if (sb.length() > 0) {
					sb.append(", ");
				}
				sb.append(selectionSetValue.getNameL10nKey());
			}
			StratificationRandomizationList result;
			try {
				result = stratificationRandomizationListDao.findByTrialTagValues(trial.getId(), selectionSetValueIds).iterator().next();
			} catch (NoSuchElementException e) {
				throw L10nUtil.initServiceException(ServiceExceptionCodes.MISSING_STRATIFICATION_RANDOMIZATION_LIST, sb.toString());
			}
			return result;
		} else {
			throw L10nUtil.initServiceException(ServiceExceptionCodes.RANDOMIZATION_PROBAND_LIST_ENTRY_REQUIRED);
		}
	}

	public abstract RandomizationType getType();

	protected final void initGroupsInfo(Collection<ProbandGroup> probandGroups) {
		randomizationInfo.setSizes(new HashMap<String, Long>());
		Iterator<ProbandGroup> it = probandGroups.iterator();
		while (it.hasNext()) {
			randomizationInfo.getSizes().put(it.next().getToken(), null);
		}
	}

	protected final void initStratificationValuesInfo(StratificationRandomizationList randomizationList) {
		Iterator<InputFieldSelectionSetValue> it = randomizationList.getSelectionSetValues().iterator();
		while (it.hasNext()) {
			randomizationInfo.getStratificationValues().add(it.next().getValue());
		}
	}

	protected final void initValuesInfo(Collection<InputFieldSelectionSetValue> values) {
		randomizationInfo.setSizes(new HashMap<String, Long>());
		Iterator<InputFieldSelectionSetValue> it = values.iterator();
		while (it.hasNext()) {
			randomizationInfo.getSizes().put(it.next().getValue(), null);
		}
	}

	protected InputFieldSelectionSetValue randomizeInputFieldSelectionSetValue(Trial trial, ProbandListEntry exclude) throws Exception {
		throw L10nUtil.initServiceException(ServiceExceptionCodes.INPUT_FIELD_SELECTION_SET_VALUE_RANDOMIZATION_NOT_SUPPORTED,
				L10nUtil.getRandomizationModeName(Locales.USER, getRandomizationMode().name()));
	}

	protected String randomizeInputFieldTextValue(Trial trial, ProbandListEntry exclude) throws Exception {
		throw L10nUtil.initServiceException(ServiceExceptionCodes.INPUT_FIELD_TEXT_VALUE_RANDOMIZATION_NOT_SUPPORTED,
				L10nUtil.getRandomizationModeName(Locales.USER, getRandomizationMode().name()));
	}

	protected ProbandGroup randomizeProbandGroup(Trial trial, ProbandListEntry exclude) throws Exception {
		throw L10nUtil.initServiceException(ServiceExceptionCodes.PROBAND_GROUP_RANDOMIZATION_NOT_SUPPORTED,
				L10nUtil.getRandomizationModeName(Locales.USER, getRandomizationMode().name()));
	}

	protected final void saveRandom(Trial trial) throws IOException {
		trial.setRandom(CoreUtil.serialize(random));
	}

	public final static void obfuscateRandomizationListCodeValues(RandomizationListCodeOutVO code) {
		if (!code.isBroken() || Settings.getBoolean(SettingCodes.OBFUSCATE_BROKEN_RANDOMIZATION_CODES, Bundle.SETTINGS, DefaultSettings.OBFUSCATE_BROKEN_RANDOMIZATION_CODES)) {
			Iterator<RandomizationListCodeValueVO> valuesIt = code.getValues().iterator();
			while (valuesIt.hasNext()) {
				RandomizationListCodeValueVO value = valuesIt.next();
				if (value.isBlinded()) {
					value.setValue(CoreUtil.OBFUSCATED_STRING);
				}
			}
		}
	}
}
