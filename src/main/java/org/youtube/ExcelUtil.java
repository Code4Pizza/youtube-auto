package org.youtube;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ExcelUtil {

	public static List<GGAccount> getAccounts() {

		List<GGAccount> accounts = new ArrayList<>();

		String path = System.getProperty("user.dir");
		// obtaining input bytes from a file
		try {
			// cho nay se sua lai thanh thao tac voi DB
			ClassLoader loader = ExcelUtil.class.getClassLoader();
			FileInputStream fis = new FileInputStream(new File(path +"/src/main/resources/emails.xls"));
			HSSFWorkbook wb = new HSSFWorkbook(fis);
			// creating a Sheet object to retrieve the object
			HSSFSheet sheet = wb.getSheetAt(0);
			// evaluating cell type
			FormulaEvaluator formulaEvaluator = wb.getCreationHelper().createFormulaEvaluator();
			for (int j = 1; j <= sheet.getLastRowNum(); j++) {
				Row row = sheet.getRow(j);
				if (row.getCell((row.getLastCellNum() - 1)).getNumericCellValue() == 0) {
					continue;
				}
				GGAccount account = new GGAccount();
				for (int i = 0; i < row.getLastCellNum() - 1; i++) {
					Cell cell = row.getCell(i);
					System.out.print(cell.getStringCellValue() + "\t\t");
					if (i == 0) {
						account.setEmail(cell.getStringCellValue());
					} else if (i == 1) {
						account.setPassword(cell.getStringCellValue());
					} else {
						account.setBackup(cell.getStringCellValue());
					}
				}
				accounts.add(account);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return accounts;
	}
}
