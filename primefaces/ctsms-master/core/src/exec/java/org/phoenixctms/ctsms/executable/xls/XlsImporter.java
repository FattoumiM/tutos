package org.phoenixctms.ctsms.executable.xls;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.phoenixctms.ctsms.Search;
import org.phoenixctms.ctsms.SearchParameter;
import org.phoenixctms.ctsms.domain.AspDao;
import org.phoenixctms.ctsms.domain.AspSubstanceDao;
import org.phoenixctms.ctsms.exception.AuthenticationException;
import org.phoenixctms.ctsms.exception.AuthorisationException;
import org.phoenixctms.ctsms.exception.ServiceException;
import org.phoenixctms.ctsms.service.shared.FileService;
import org.phoenixctms.ctsms.util.ChunkedRemoveAll;
import org.phoenixctms.ctsms.util.CommonUtil;
import org.phoenixctms.ctsms.util.ExecUtil;
import org.phoenixctms.ctsms.util.JobOutput;
import org.phoenixctms.ctsms.vo.AuthenticationVO;
import org.phoenixctms.ctsms.vo.FileStreamOutVO;
import org.springframework.beans.factory.annotation.Autowired;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;

public class XlsImporter {

	private static Search getRevisionSearch(String revision) {
		return new Search(new SearchParameter[] {
				new SearchParameter("revision", revision, SearchParameter.EQUAL_COMPARATOR) });
	}

	@Autowired
	protected SelectionSetValueRowProcessor selectionSetValueRowProcessor;
	@Autowired
	protected InputFieldRowProcessor inputFieldRowProcessor;
	@Autowired
	protected EcrfFieldRowProcessor ecrfFieldRowProcessor;
	@Autowired
	protected EcrfRowProcessor ecrfRowProcessor;
	@Autowired
	protected AspRowProcessor aspRowProcessor;
	@Autowired
	protected RandomizationListCodeRowProcessor randomizationListCodeRowProcessor;
	@Autowired
	protected AspDao aspDao;
	@Autowired
	protected AspSubstanceDao aspSubstanceDao;
	@Autowired
	private FileService fileService;
	private JobOutput jobOutput;

	public XlsImporter() {
	}

	private XlsImporterContext createContext(RowProcessor processor, String fileName, boolean mandatory) {
		XlsImporterContext context = new XlsImporterContext(this, fileName);
		setContext(processor, context, mandatory);
		return context;
	}

	public EcrfFieldRowProcessor getEcrfFieldRowProcessor() {
		return ecrfFieldRowProcessor;
	}

	public SelectionSetValueRowProcessor getSelectionSetValueRowProcessor() {
		return selectionSetValueRowProcessor;
	}

	public long loadAsps(String fileName, boolean removeAllBeforeInsert, String revision) throws Throwable {
		XlsImporterContext context = createContext(aspRowProcessor, fileName, true);
		if (CommonUtil.isEmptyString(revision)) {
			revision = ExecUtil.removeExtension((new File(fileName)).getName());
			jobOutput.println("no asp revision specified, using " + revision);
		} else {
			jobOutput.println("using asp revision " + revision);
		}
		aspRowProcessor.setRevision(revision);
		long medicationCount = aspDao.getMedicationCount(revision);
		if (medicationCount > 0) {
			throw new IllegalArgumentException("asps of revision " + revision + " are used by " + medicationCount + " medications");
		}
		medicationCount = aspSubstanceDao.getMedicationCount(revision);
		if (medicationCount > 0) {
			throw new IllegalArgumentException("asp substances of revision " + revision + " are used by medications " + medicationCount + " times");
		}
		if (removeAllBeforeInsert) {
			removeAspRecords(revision);
			jobOutput.println("asp revision " + revision + " cleared");
		}
		return readRows(context, aspRowProcessor);
	}

	public long loadRandomizationLists(String fileName, AuthenticationVO auth, Long trialId, boolean purge) throws Throwable {
		XlsImporterContext context = createContext(randomizationListCodeRowProcessor, fileName, true);
		context.setAuth(auth);
		context.setEntityId(trialId);
		randomizationListCodeRowProcessor.setPurge(purge);
		return readRows(context, randomizationListCodeRowProcessor);
	}

	protected long loadEcrfFields(XlsImporterContext context) throws Throwable {
		setContext(ecrfFieldRowProcessor, context, true);
		return readRows(context, ecrfFieldRowProcessor);
	}

	public long loadEcrfs(String fileName, AuthenticationVO auth, Long trialId) throws Throwable {
		XlsImporterContext context = createContext(ecrfRowProcessor, fileName, true);
		context.setAuth(auth);
		context.setEntityId(trialId);
		return readRows(context, ecrfRowProcessor);
	}

	public long loadInputFields(String fileName, AuthenticationVO auth) throws Throwable {
		XlsImporterContext context = createContext(inputFieldRowProcessor, fileName, true);
		context.setAuth(auth);
		return readRows(context, inputFieldRowProcessor);
	}

	protected long loadInputFields(XlsImporterContext context) throws Throwable {
		setContext(inputFieldRowProcessor, context, false);
		return readRows(context, inputFieldRowProcessor);
	}

	protected long loadSelectionSetValues(XlsImporterContext context) throws Throwable {
		setContext(selectionSetValueRowProcessor, context, context.isMandatory(inputFieldRowProcessor));
		return readRows(context, selectionSetValueRowProcessor);
	}

	private InputStream getInputStream(String fileName, AuthenticationVO auth, FileService fileService, JobOutput jobOutput) throws AuthenticationException,
			AuthorisationException, ServiceException, FileNotFoundException {
		if (CommonUtil.isEmptyString(fileName)) {
			jobOutput.println("reading from job");
			return new ByteArrayInputStream(jobOutput.getJobFile().getDatas());
		} else {
			try {
				long fileId = Long.parseLong(fileName);
				FileStreamOutVO file = fileService.getFileStream(auth, fileId);
				jobOutput.println("reading from file ID " + fileName + " (" + file.getFileName() + ")");
				return file.getStream();
			} catch (NumberFormatException e) {
				jobOutput.println("reading from file " + fileName);
				return new FileInputStream(fileName);
			}
		}
	}

	private long readRows(XlsImporterContext context, RowProcessor processor) throws Throwable {
		processor.init();
		long rowCount = 0l;
		long lineNumber = 1l;
		Workbook workbook = null;
		try {
			InputStream inputStream = getInputStream(context.getFileName(), context.getAuth(), fileService, jobOutput);
			WorkbookSettings workbookSettings = processor.getWorkbookSettings();
			if (workbookSettings != null) {
				workbook = Workbook.getWorkbook(inputStream, workbookSettings);
			} else {
				workbook = Workbook.getWorkbook(inputStream);
			}
			Sheet sheet;
			String sheetName = processor.getSheetName();
			if (sheetName != null) {
				sheet = workbook.getSheet(sheetName);
			} else {
				try {
					sheet = workbook.getSheet(processor.getSheetNum());
				} catch (IndexOutOfBoundsException e) {
					sheet = null;
				}
			}
			if (sheet != null) {
				sheetName = sheet.getName();
				if (!CommonUtil.isEmptyString(sheetName)) {
					jobOutput.println("processing sheet '" + sheetName + "'");
				}
				int sheetRowCount = sheet.getRows();
				if (sheetRowCount > 0) {
					Cell[] row = sheet.getRow(0);
					if (!processor.processHeaderRow(row)) {
						rowCount += processor.processRow(row, lineNumber);
					}
					lineNumber++;
					for (int i = 1; i < sheetRowCount; i++) {
						row = sheet.getRow(i);
						rowCount += processor.processRow(row, lineNumber);
						lineNumber++;
					}
				}
			} else {
				if (context.isMandatory(processor)) {
					throw new IllegalArgumentException("spreadsheet not found");
				} else {
					jobOutput.println("spreadsheet not found, skipping");
				}
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("line " + lineNumber + ": error reading line", e);
		} finally {
			if (workbook != null) {
				workbook.close();
			}
		}
		processor.postProcess();
		jobOutput.println(rowCount + " rows processed");
		return rowCount;
	}

	private void removeAspRecords(String revision) throws Exception {
		ChunkedRemoveAll<AspDao> aspRemover = new ChunkedRemoveAll<AspDao>(
				aspDao, getRevisionSearch(revision)) {

			@Override
			protected Collection<Object> convertPage(Collection<Object> page) {
				return new HashSet(page);
			}

			@Override
			protected Method getRemoveMethod(Class dao) throws NoSuchMethodException, SecurityException {
				return dao.getMethod("removeTxn", Long.class);
			}

			@Override
			protected Method getRemovePageMethod(Class dao) throws NoSuchMethodException, SecurityException {
				return dao.getMethod("removeAllTxn", Set.class);
			}

			@Override
			protected boolean removePageDone(int pageSize, Object removePageResult) {
				jobOutput.println(pageSize + " asp records removed");
				return true;
			}
		};
		aspRemover.remove();
	}

	private void setContext(RowProcessor processor, XlsImporterContext context, boolean mandatory) {
		context.setMandatory(processor, mandatory);
		processor.setContext(context);
		processor.setJobOutput(jobOutput);
	}

	public void setJobOutput(JobOutput jobOutput) {
		this.jobOutput = jobOutput;
	}
}
