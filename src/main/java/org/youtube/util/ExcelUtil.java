package org.youtube.util;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.jdbi.v3.core.Jdbi;
import org.youtube.GGAccount;
import org.youtube.configuration.CircuitBreakerConfiguration;
import org.youtube.entities.YoutubeAccount;
import org.youtube.storage.YoutubeDatabases;
import org.youtube.storage.FaultTolerantDatabase;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.youtube.util.Constants.*;
import static org.youtube.util.Constants.DB_NAME;

public class ExcelUtil {

    public static List<GGAccount> getAccounts() {

        List<GGAccount> accounts = new ArrayList<>();

        String path = System.getProperty("user.dir");
        // obtaining input bytes from a file
        try {
            // cho nay se sua lai thanh thao tac voi DB
            ClassLoader loader = ExcelUtil.class.getClassLoader();
            FileInputStream fis = new FileInputStream(new File(path + "/src/main/resources/emails.xls"));
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

    public static List<YoutubeAccount> readAccountsFromTxtFile() {
        List<YoutubeAccount> accounts = new ArrayList<>();
        String path = System.getProperty("user.dir");

        try {
            File file = new File(path + "/src/main/resources/100_mails.txt");
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = br.readLine();

            while (line != null) {
                String[] accText = line.split("\t");
                YoutubeAccount account = new YoutubeAccount();
                account.setEmail(accText[0]);
                account.setPassword(accText[1]);
                account.setBackupEmail(accText[2]);
                accounts.add(account);
                line = br.readLine();
            }
        } catch (IOException e) {
            LogUtil.severe(e.getMessage());
        }
        return accounts;
    }

    public static void main(String[] args) {
        Jdbi jdbi = Jdbi.create(BASE_URL, USER_NAME, PASSWORD);
        FaultTolerantDatabase accDatabase = new FaultTolerantDatabase(DB_NAME, jdbi, new CircuitBreakerConfiguration());
        YoutubeDatabases youtubeDatabases = new YoutubeDatabases(accDatabase);

        List<YoutubeAccount> youtubeAccounts = readAccountsFromTxtFile();
        youtubeDatabases.bulkInsertAccounts(youtubeAccounts);

        System.out.println("Insert success");
    }
}
