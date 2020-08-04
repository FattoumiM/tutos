package org.phoenixctms.ctsms.pdf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.phoenixctms.ctsms.enumeration.Color;
import org.phoenixctms.ctsms.pdf.InquiriesPDFBlock.BlockType;
import org.phoenixctms.ctsms.util.CommonUtil;
import org.phoenixctms.ctsms.util.CoreUtil;
import org.phoenixctms.ctsms.util.L10nUtil;
import org.phoenixctms.ctsms.util.L10nUtil.Locales;
import org.phoenixctms.ctsms.util.Settings;
import org.phoenixctms.ctsms.util.Settings.Bundle;
import org.phoenixctms.ctsms.vo.InputFieldImageVO;
import org.phoenixctms.ctsms.vo.InputFieldOutVO;
import org.phoenixctms.ctsms.vo.InquiriesPDFVO;
import org.phoenixctms.ctsms.vo.InquiryValueOutVO;
import org.phoenixctms.ctsms.vo.ProbandOutVO;
import org.phoenixctms.ctsms.vo.TrialOutVO;

public class InquiriesPDFPainter extends PDFPainterBase implements PDFOutput {

	protected int blockIndex;
	protected ArrayList<InquiriesPDFBlock> blocks;
	protected InquiriesPDFBlockCursor cursor;
	protected HashMap<Long, HashMap<Long, PDFJpeg>> images;
	protected InquiriesPDFVO pdfVO;
	protected Collection<ProbandOutVO> probandVOs;
	protected HashMap<Long, Collection<TrialOutVO>> trialVOMap;
	protected HashMap<Long, HashMap<Long, Collection<InquiryValueOutVO>>> valueVOMap;
	protected HashMap<Long, InputFieldImageVO> imageVOMap;
	protected boolean blank;
	protected float pageWidth;
	protected float pageHeight;
	protected PDFont fontA;
	protected PDFont fontB;
	protected PDFont fontC;
	protected PDFont fontD;
	protected PDFJpeg checkboxCheckedImage;
	protected PDFJpeg checkboxUncheckedImage;
	protected PDFJpeg radioOnImage;
	protected PDFJpeg radioOffImage;
	protected PDFJpeg selectboxCheckedImage;
	protected PDFJpeg selectboxUncheckedImage;
	protected PDFJpeg checkboxCheckedPresetImage;
	protected PDFJpeg radioOnPresetImage;
	protected PDFJpeg selectboxCheckedPresetImage;
	protected final static PDRectangle DEFAULT_PAGE_SIZE = PDPage.PAGE_SIZE_A4;
	protected static final String INQUIRIES_PDF_FILENAME_PREFIX = "inquiries_";

	public InquiriesPDFPainter() {
		super();
		blocks = new ArrayList<InquiriesPDFBlock>();
		images = new HashMap<Long, HashMap<Long, PDFJpeg>>();
		pdfVO = new InquiriesPDFVO();
		cursor = new InquiriesPDFBlockCursor(this);
		setDrawPageNumbers(Settings.getBoolean(InquiriesPDFSettingCodes.SHOW_PAGE_NUMBERS, Bundle.INQUIRIES_PDF, InquiriesPDFDefaultSettings.SHOW_PAGE_NUMBERS));
	}

	protected void drawBlock(PDPageContentStream contentStream, InquiriesPDFBlock block) throws Exception {
		if (BlockType.NEW_PROBAND_TRIAL.equals(block.getType())) {
			cursor.setCategoryY(cursor.getBlockY());
		} else if (BlockType.NEW_CATEGORY.equals(block.getType())) {
			cursor.setCategoryY(cursor.getBlockY());
		}
		cursor.setBlocks(block);
		cursor.setBlockY(cursor.getBlockY() - block.renderBlock(contentStream, cursor));
	}

	@Override
	public void drawNextBlock(PDPageContentStream contentStream) throws Exception {
		drawBlock(contentStream, blocks.get(blockIndex));
		blockIndex++;
	}

	@Override
	public void drawPage(PDPageContentStream contentStream) throws Exception {
		if (cursor.getProbandTrialBlock() != null) {
			(new InquiriesPDFBlock(cursor.getProbandTrialBlock(), BlockType.PAGE_TITLE, true)).renderBlock(contentStream, cursor);
		}
	}

	@Override
	public void drawPageBreakNewPage(PDPageContentStream contentStream) throws Exception {
		InquiriesPDFBlock block = blocks.get(blockIndex);
		if (BlockType.INPUT_FIELD.equals(block.getType())
				|| BlockType.NEW_CATEGORY.equals(block.getType())) {
			drawBlock(contentStream, new InquiriesPDFBlock(cursor.getProbandTrialBlock(), true));
		}
		if (BlockType.INPUT_FIELD.equals(block.getType())) {
			drawBlock(contentStream, new InquiriesPDFBlock(cursor.getCategoryBlock(), true));
		}
	}

	@Override
	public void drawPageBreakOldPage(PDPageContentStream contentStream) throws Exception {
		InquiriesPDFBlock block = blocks.get(blockIndex - 1);
		if (BlockType.INPUT_FIELD.equals(block.getType())) {
			drawBlock(contentStream, new InquiriesPDFBlock(BlockType.END_OF_CATEGORY, true));
		}
	}

	@Override
	protected void drawPageNumber(PDFImprinter writer, PDPage page, int pageNumber, int totalPages) throws IOException {
		PDPageContentStream contentStream = writer.openContentStream(page);
		PDFUtil.renderTextLine(
				contentStream,
				fontA,
				PDFUtil.FontSize.TINY,
				Settings.getColor(InquiriesPDFSettingCodes.TEXT_COLOR, Bundle.INQUIRIES_PDF, InquiriesPDFDefaultSettings.TEXT_COLOR),
				L10nUtil.getInquiriesPDFLabel(Locales.INQUIRIES_PDF, EcrfPDFLabelCodes.PAGE_NUMBER, "", pageNumber, totalPages),
				Settings.getFloat(InquiriesPDFSettingCodes.PAGE_LEFT_MARGIN, Bundle.INQUIRIES_PDF, InquiriesPDFDefaultSettings.PAGE_LEFT_MARGIN)
						+ (pageWidth - Settings.getFloat(InquiriesPDFSettingCodes.PAGE_LEFT_MARGIN, Bundle.INQUIRIES_PDF, InquiriesPDFDefaultSettings.PAGE_LEFT_MARGIN) - Settings
								.getFloat(
										InquiriesPDFSettingCodes.PAGE_RIGHT_MARGIN, Bundle.INQUIRIES_PDF, InquiriesPDFDefaultSettings.PAGE_RIGHT_MARGIN))
								/ 2.0f,
				Settings.getFloat(InquiriesPDFSettingCodes.PAGE_LOWER_MARGIN, Bundle.INQUIRIES_PDF, InquiriesPDFDefaultSettings.PAGE_LOWER_MARGIN),
				PDFUtil.Alignment.BOTTOM_CENTER);
		writer.closeContentStream();
	}

	public PDFJpeg getCheckboxCheckedImage() {
		return checkboxCheckedImage;
	}

	public PDFJpeg getCheckboxCheckedPresetImage() {
		return checkboxCheckedPresetImage;
	}

	public PDFJpeg getCheckboxUncheckedImage() {
		return checkboxUncheckedImage;
	}

	@Override
	public PDRectangle getDefaultPageSize() {
		return DEFAULT_PAGE_SIZE;
	}

	public PDFont getFontA() {
		return fontA;
	}

	public PDFont getFontB() {
		return fontB;
	}

	public PDFont getFontC() {
		return fontC;
	}

	public PDFont getFontD() {
		return fontD;
	}

	@Override
	public PageOrientation getPageOrientation() {
		if (Settings.getBoolean(InquiriesPDFSettingCodes.LANDSCAPE, Bundle.INQUIRIES_PDF, InquiriesPDFDefaultSettings.LANDSCAPE)) {
			return PageOrientation.LANDSCAPE;
		} else {
			return PageOrientation.PORTRAIT;
		}
	}

	public InquiriesPDFVO getPdfVO() {
		return pdfVO;
	}

	public PDFJpeg getRadioOffImage() {
		return radioOffImage;
	}

	public PDFJpeg getRadioOnImage() {
		return radioOnImage;
	}

	public PDFJpeg getRadioOnPresetImage() {
		return radioOnPresetImage;
	}

	public PDFJpeg getSelectboxCheckedImage() {
		return selectboxCheckedImage;
	}

	public PDFJpeg getSelectboxCheckedPresetImage() {
		return selectboxCheckedPresetImage;
	}

	public PDFJpeg getSelectboxUncheckedImage() {
		return selectboxUncheckedImage;
	}

	protected PDFJpeg getSketchImage(InquiryValueOutVO value) {
		InputFieldOutVO field = value.getInquiry().getField();
		HashMap<Long, PDFJpeg> sketchImages = images.get(field.getId());
		if (sketchImages != null) {
			return sketchImages.get(value.getId());
		}
		return null;
	}

	@Override
	public String getTemplateFileName() throws Exception {
		String key = L10nUtil.getDepartmentL10nKey(InquiriesPDFSettingCodes.TEMPLATE_FILE_NAME, cursor.getTrial());
		if (Settings.containsKey(key, Bundle.INQUIRIES_PDF)) {
			return Settings.getPDFTemplateFilename(key, Bundle.INQUIRIES_PDF, null);
		}
		return Settings.getPDFTemplateFilename(InquiriesPDFSettingCodes.TEMPLATE_FILE_NAME, Bundle.INQUIRIES_PDF, null);
	}

	@Override
	public boolean hasNextBlock() {
		return blockIndex < blocks.size();
	}

	@Override
	public void loadFonts(PDDocument doc) throws Exception {
		fontA = PDFUtil.loadFont(Settings.getPDFFontName(InquiriesPDFSettingCodes.FONT_A, Bundle.INQUIRIES_PDF, null), doc, DEFAULT_BASE_FONT);
		fontB = PDFUtil.loadFont(Settings.getPDFFontName(InquiriesPDFSettingCodes.FONT_B, Bundle.INQUIRIES_PDF, null), doc, DEFAULT_BASE_FONT);
		fontC = PDFUtil.loadFont(Settings.getPDFFontName(InquiriesPDFSettingCodes.FONT_C, Bundle.INQUIRIES_PDF, null), doc, DEFAULT_BASE_FONT);
		fontD = PDFUtil.loadFont(Settings.getPDFFontName(InquiriesPDFSettingCodes.FONT_D, Bundle.INQUIRIES_PDF, null), doc, DEFAULT_BASE_FONT);
	}

	@Override
	public void loadImages(PDDocument doc) throws Exception {
		int quality = Settings.getInt(InquiriesPDFSettingCodes.SELECTION_ITEM_IMAGE_QUALITY, Bundle.INQUIRIES_PDF, InquiriesPDFDefaultSettings.SELECTION_ITEM_IMAGE_QUALITY);
		int dpi = Settings.getInt(InquiriesPDFSettingCodes.SELECTION_ITEM_IMAGE_DPI, Bundle.INQUIRIES_PDF, InquiriesPDFDefaultSettings.SELECTION_ITEM_IMAGE_DPI);
		Color bgColor = Settings.getColor(InquiriesPDFSettingCodes.SELECTION_ITEM_IMAGE_BG_COLOR, Bundle.INQUIRIES_PDF, InquiriesPDFDefaultSettings.SELECTION_ITEM_IMAGE_BG_COLOR);
		int selectionItemImageWidth = Settings.getInt(InquiriesPDFSettingCodes.SELECTION_ITEM_IMAGE_WIDTH, Bundle.INQUIRIES_PDF,
				InquiriesPDFDefaultSettings.SELECTION_ITEM_IMAGE_WIDTH);
		int selectionItemImageHeight = Settings.getInt(InquiriesPDFSettingCodes.SELECTION_ITEM_IMAGE_HEIGHT, Bundle.INQUIRIES_PDF,
				InquiriesPDFDefaultSettings.SELECTION_ITEM_IMAGE_HEIGHT);
		checkboxCheckedImage = PDFJpeg.prepareScaledImage(doc,
				PDFUtil.loadImage(Settings.getImageFilename(InquiriesPDFSettingCodes.CHECKBOX_CHECKED_IMAGE_FILE_NAME, Bundle.INQUIRIES_PDF, null)),
				selectionItemImageWidth, selectionItemImageHeight, quality, dpi, bgColor);
		checkboxCheckedPresetImage = PDFJpeg.prepareScaledImage(doc,
				PDFUtil.loadImage(Settings.getImageFilename(InquiriesPDFSettingCodes.CHECKBOX_CHECKED_PRESET_IMAGE_FILE_NAME, Bundle.INQUIRIES_PDF, null)),
				selectionItemImageWidth, selectionItemImageHeight, quality, dpi, bgColor);
		checkboxUncheckedImage = PDFJpeg.prepareScaledImage(doc,
				PDFUtil.loadImage(Settings.getImageFilename(InquiriesPDFSettingCodes.CHECKBOX_UNCHECKED_IMAGE_FILE_NAME, Bundle.INQUIRIES_PDF, null)),
				selectionItemImageWidth, selectionItemImageHeight, quality, dpi, bgColor);
		radioOnImage = PDFJpeg.prepareScaledImage(doc, PDFUtil.loadImage(Settings.getImageFilename(InquiriesPDFSettingCodes.RADIO_ON_IMAGE_FILE_NAME, Bundle.INQUIRIES_PDF, null)),
				selectionItemImageWidth, selectionItemImageHeight, quality, dpi, bgColor);
		radioOnPresetImage = PDFJpeg.prepareScaledImage(doc,
				PDFUtil.loadImage(Settings.getImageFilename(InquiriesPDFSettingCodes.RADIO_ON_PRESET_IMAGE_FILE_NAME, Bundle.INQUIRIES_PDF, null)),
				selectionItemImageWidth, selectionItemImageHeight, quality, dpi, bgColor);
		radioOffImage = PDFJpeg.prepareScaledImage(doc,
				PDFUtil.loadImage(Settings.getImageFilename(InquiriesPDFSettingCodes.RADIO_OFF_IMAGE_FILE_NAME, Bundle.INQUIRIES_PDF, null)),
				selectionItemImageWidth, selectionItemImageHeight, quality, dpi, bgColor);
		selectboxCheckedImage = PDFJpeg.prepareScaledImage(doc,
				PDFUtil.loadImage(Settings.getImageFilename(InquiriesPDFSettingCodes.SELECTBOX_CHECKED_IMAGE_FILE_NAME, Bundle.INQUIRIES_PDF, null)),
				selectionItemImageWidth, selectionItemImageHeight, quality, dpi, bgColor);
		selectboxCheckedPresetImage = PDFJpeg.prepareScaledImage(doc,
				PDFUtil.loadImage(Settings.getImageFilename(InquiriesPDFSettingCodes.SELECTBOX_CHECKED_PRESET_IMAGE_FILE_NAME, Bundle.INQUIRIES_PDF, null)),
				selectionItemImageWidth, selectionItemImageHeight, quality, dpi, bgColor);
		selectboxUncheckedImage = PDFJpeg.prepareScaledImage(doc,
				PDFUtil.loadImage(Settings.getImageFilename(InquiriesPDFSettingCodes.SELECTBOX_UNCHECKED_IMAGE_FILE_NAME, Bundle.INQUIRIES_PDF, null)),
				selectionItemImageWidth, selectionItemImageHeight, quality, dpi, bgColor);
		if (valueVOMap != null && imageVOMap != null
				&& Settings.getBoolean(InquiriesPDFSettingCodes.RENDER_SKETCH_IMAGES, Bundle.INQUIRIES_PDF, InquiriesPDFDefaultSettings.RENDER_SKETCH_IMAGES)) {
			Iterator<HashMap<Long, Collection<InquiryValueOutVO>>> probandMapIt = valueVOMap.values().iterator();
			while (probandMapIt.hasNext()) {
				Iterator<Collection<InquiryValueOutVO>> trialMapIt = probandMapIt.next().values().iterator();
				while (trialMapIt.hasNext()) {
					Iterator<InquiryValueOutVO> valuesIt = trialMapIt.next().iterator();
					while (valuesIt.hasNext()) {
						putSketchImage(valuesIt.next(), doc);
					}
				}
			}
		}
	}

	@Override
	public boolean nextBlockFitsOnPage() throws Exception {
		InquiriesPDFBlock block = blocks.get(blockIndex);
		if (blockIndex > 0 && BlockType.NEW_PROBAND_TRIAL.equals(block.getType())) {
			return false;
		} else {
			float height = block.getHeight(cursor);
			if (BlockType.NEW_CATEGORY.equals(block.getType())) {
				height += blocks.get(blockIndex + 1).getHeight(cursor);
			} else if (BlockType.END_OF_CATEGORY.equals(block.getType())) {
				return true;
			}
			return (cursor.getBlockY() - height) > Settings.getFloat(InquiriesPDFSettingCodes.BLOCKS_LOWER_MARGIN, Bundle.INQUIRIES_PDF,
					InquiriesPDFDefaultSettings.BLOCKS_LOWER_MARGIN);
		}
	}

	@Override
	public void populateBlocks() {
		blocks.clear();
		if (probandVOs != null && trialVOMap != null && valueVOMap != null) {
			Iterator<ProbandOutVO> probandIt = probandVOs.iterator();
			while (probandIt.hasNext()) {
				ProbandOutVO probandVO = probandIt.next();
				Collection<TrialOutVO> trialVOs = trialVOMap.get(probandVO == null ? null : probandVO.getId());
				if (trialVOs != null) {
					Iterator<TrialOutVO> trialIt = trialVOs.iterator();
					while (trialIt.hasNext()) {
						TrialOutVO trialVO = trialIt.next();
						blocks.add(new InquiriesPDFBlock(probandVO, trialVO, now, blank));
						HashMap<Long, Collection<InquiryValueOutVO>> inquiryValueVOMap = valueVOMap.get(probandVO == null ? null : probandVO.getId());
						if (inquiryValueVOMap != null) {
							Collection<InquiryValueOutVO> valueVOs = inquiryValueVOMap.get(trialVO.getId());
							if (valueVOs != null && valueVOs.size() > 0) {
								boolean first = true;
								String previousCategory = null;
								Iterator<InquiryValueOutVO> valueIt = valueVOs.iterator();
								while (valueIt.hasNext()) {
									InquiryValueOutVO valueVO = valueIt.next();
									String category = valueVO.getInquiry().getCategory();
									if (first) {
										blocks.add(new InquiriesPDFBlock(category));
										first = false;
									} else {
										if (previousCategory == null ? category == null : previousCategory.equals(category)) {
										} else {
											blocks.add(new InquiriesPDFBlock(BlockType.END_OF_CATEGORY, false));
											blocks.add(new InquiriesPDFBlock(category));
										}
									}
									blocks.add(new InquiriesPDFBlock(valueVO, getSketchImage(valueVO), blank));
									previousCategory = category;
								}
								blocks.add(new InquiriesPDFBlock(BlockType.END_OF_CATEGORY, false));
							}
						}
					}
				}
			}
		}
	}

	protected boolean putSketchImage(InquiryValueOutVO value, PDDocument doc) throws Exception {
		InputFieldOutVO field = value.getInquiry().getField();
		InputFieldImageVO inputFieldImage = imageVOMap.get(field.getId());
		if (inputFieldImage != null) {
			HashMap<Long, PDFJpeg> sketchImages;
			if (images.containsKey(field.getId())) {
				sketchImages = images.get(field.getId());
			} else {
				sketchImages = new HashMap<Long, PDFJpeg>();
				images.put(field.getId(), sketchImages);
			}
			if (!sketchImages.containsKey(value.getId())) {
				int quality = Settings.getInt(InquiriesPDFSettingCodes.SKETCH_IMAGE_QUALITY, Bundle.INQUIRIES_PDF, InquiriesPDFDefaultSettings.SKETCH_IMAGE_QUALITY);
				int dpi = Settings.getInt(InquiriesPDFSettingCodes.SKETCH_IMAGE_DPI, Bundle.INQUIRIES_PDF, InquiriesPDFDefaultSettings.SKETCH_IMAGE_DPI);
				Color bgColor = Settings.getColor(InquiriesPDFSettingCodes.SKETCH_IMAGE_BG_COLOR, Bundle.INQUIRIES_PDF, InquiriesPDFDefaultSettings.SKETCH_IMAGE_BG_COLOR);
				sketchImages.put(value.getId(),
						PDFJpeg.prepareSketchImage(
								doc,
								field,
								inputFieldImage,
								value.getInkValues(),
								Settings.getBoolean(InquiriesPDFSettingCodes.SHOW_SKETCH_REGIONS, Bundle.INQUIRIES_PDF, InquiriesPDFDefaultSettings.SHOW_SKETCH_REGIONS),
								!blank,
								cursor.getBlockIndentedWidth(false),
								quality, dpi, bgColor));
				return true;
			}
		}
		return false;
	}

	@Override
	public void reset() {
		super.reset();
		blockIndex = 0;
		pageWidth = DEFAULT_PAGE_SIZE.getWidth();
		pageHeight = DEFAULT_PAGE_SIZE.getHeight();
		cursor.setBlockY(pageHeight - Settings.getFloat(InquiriesPDFSettingCodes.BLOCKS_UPPER_MARGIN, Bundle.INQUIRIES_PDF, InquiriesPDFDefaultSettings.BLOCKS_UPPER_MARGIN));
		cursor.setBlockX(Settings.getFloat(InquiriesPDFSettingCodes.BLOCKS_LEFT_MARGIN, Bundle.INQUIRIES_PDF, InquiriesPDFDefaultSettings.BLOCKS_LEFT_MARGIN));
		cursor.setBlockWidth(pageWidth - Settings.getFloat(InquiriesPDFSettingCodes.BLOCKS_RIGHT_MARGIN, Bundle.INQUIRIES_PDF, InquiriesPDFDefaultSettings.BLOCKS_RIGHT_MARGIN)
				- Settings.getFloat(InquiriesPDFSettingCodes.BLOCKS_LEFT_MARGIN, Bundle.INQUIRIES_PDF, InquiriesPDFDefaultSettings.BLOCKS_LEFT_MARGIN));
		cursor.clearBlocks();
		cursor.setProband(null);
		cursor.setTrial(null);
		fontA = null;
		fontB = null;
		fontC = null;
		fontD = null;
		checkboxCheckedImage = null;
		checkboxCheckedPresetImage = null;
		checkboxUncheckedImage = null;
		radioOnImage = null;
		radioOnPresetImage = null;
		radioOffImage = null;
		selectboxCheckedImage = null;
		selectboxCheckedPresetImage = null;
		selectboxUncheckedImage = null;
		images.clear();
		updateInquiriesPDFVO();
	}

	@Override
	public boolean save(ByteArrayOutputStream pdfStream) throws Exception {
		byte[] documentData = pdfStream.toByteArray();
		pdfVO.setMd5(CommonUtil.getHex(MessageDigest.getInstance("MD5").digest(documentData)));
		pdfVO.setSize(documentData.length);
		pdfVO.setDocumentDatas(documentData);
		return true;
	}

	public void setBlank(boolean blank) {
		this.blank = blank;
	}

	public void setImageVOMap(HashMap<Long, InputFieldImageVO> imageVOMap) {
		this.imageVOMap = imageVOMap;
	}

	@Override
	public void setPageHeight(float pageHeight) {
		this.pageHeight = pageHeight;
		cursor.setBlockY(pageHeight - Settings.getFloat(InquiriesPDFSettingCodes.BLOCKS_UPPER_MARGIN, Bundle.INQUIRIES_PDF, InquiriesPDFDefaultSettings.BLOCKS_UPPER_MARGIN));
		cursor.setCategoryY(cursor.getBlockY());
	}

	@Override
	public void setPageWidth(float pageWidth) {
		this.pageWidth = pageWidth;
		cursor.setBlockWidth(pageWidth - Settings.getFloat(InquiriesPDFSettingCodes.BLOCKS_RIGHT_MARGIN, Bundle.INQUIRIES_PDF, InquiriesPDFDefaultSettings.BLOCKS_RIGHT_MARGIN)
				- Settings.getFloat(InquiriesPDFSettingCodes.BLOCKS_LEFT_MARGIN, Bundle.INQUIRIES_PDF, InquiriesPDFDefaultSettings.BLOCKS_LEFT_MARGIN));
	}

	public void setProbandVOs(Collection<ProbandOutVO> probandVOs) {
		this.probandVOs = probandVOs;
	}

	public void setTrialVOMap(HashMap<Long, Collection<TrialOutVO>> trialVOMap) {
		this.trialVOMap = trialVOMap;
	}

	public void setValueVOMap(HashMap<Long, HashMap<Long, Collection<InquiryValueOutVO>>> valueVOMap) {
		this.valueVOMap = valueVOMap;
	}

	@Override
	public void startNewPage() {
		super.startNewPage(!hasNextBlock() || BlockType.NEW_PROBAND_TRIAL.equals(blocks.get(blockIndex).getType()));
		cursor.setBlockY(pageHeight - Settings.getFloat(InquiriesPDFSettingCodes.BLOCKS_UPPER_MARGIN, Bundle.INQUIRIES_PDF, InquiriesPDFDefaultSettings.BLOCKS_UPPER_MARGIN));
		cursor.setBlockX(Settings.getFloat(InquiriesPDFSettingCodes.BLOCKS_LEFT_MARGIN, Bundle.INQUIRIES_PDF, InquiriesPDFDefaultSettings.BLOCKS_LEFT_MARGIN));
		cursor.setBlockWidth(pageWidth - Settings.getFloat(InquiriesPDFSettingCodes.BLOCKS_RIGHT_MARGIN, Bundle.INQUIRIES_PDF, InquiriesPDFDefaultSettings.BLOCKS_RIGHT_MARGIN)
				- Settings.getFloat(InquiriesPDFSettingCodes.BLOCKS_LEFT_MARGIN, Bundle.INQUIRIES_PDF, InquiriesPDFDefaultSettings.BLOCKS_LEFT_MARGIN));
		cursor.setCategoryY(cursor.getBlockY());
	}

	@Override
	public void updateCursor() {
		InquiriesPDFBlock block = blocks.get(blockIndex);
		if (BlockType.NEW_PROBAND_TRIAL.equals(block.getType())) {
			cursor.setProband(block.getProband());
			cursor.setTrial(block.getTrial());
		}
	}

	protected void updateInquiriesPDFVO() {
		pdfVO.setContentTimestamp(now);
		pdfVO.setContentType(CoreUtil.getPDFMimeType());
		pdfVO.getProbands().clear();
		pdfVO.getTrials().clear();
		if (probandVOs != null && trialVOMap != null) {
			Iterator<ProbandOutVO> probandIt = probandVOs.iterator();
			while (probandIt.hasNext()) {
				ProbandOutVO probandVO = probandIt.next();
				Collection<TrialOutVO> trialVOs = trialVOMap.get(probandVO == null ? null : probandVO.getId());
				if (trialVOs != null) {
					Iterator<TrialOutVO> trialIt = trialVOs.iterator();
					while (trialIt.hasNext()) {
						TrialOutVO trialVO = trialIt.next();
						pdfVO.getProbands().add(probandVO);
						pdfVO.getTrials().add(trialVO);
					}
				}
			}
		}
		StringBuilder fileName = new StringBuilder(INQUIRIES_PDF_FILENAME_PREFIX);
		if (probandVOs != null && probandVOs.size() == 1) {
			ProbandOutVO probandVO = probandVOs.iterator().next();
			if (probandVO != null) {
				fileName.append(probandVO.getId());
				fileName.append("_");
			}
			Collection<TrialOutVO> trialVOs;
			if (trialVOMap != null && (trialVOs = trialVOMap.get(probandVO == null ? null : probandVO.getId())) != null && trialVOs.size() == 1) {
				fileName.append(trialVOs.iterator().next().getId());
				fileName.append("_");
			}
			if (blank) {
				fileName.append("blank");
				fileName.append("_");
			}
		}
		fileName.append(CommonUtil.formatDate(now, CommonUtil.DIGITS_ONLY_DATETIME_PATTERN));
		fileName.append(".");
		fileName.append(CoreUtil.PDF_FILENAME_EXTENSION);
		pdfVO.setFileName(fileName.toString());
	}
}
