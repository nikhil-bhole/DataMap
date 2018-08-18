package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReadFile {
	
	public static InputStream getFile() {
		InputStream input = null;
		String filename = "Patient-Names-Add-Folloup.xlsx";
		input = ReadFile.class.getClassLoader().getResourceAsStream(filename);
		return input;
	}

	public static void execute() {
		DataFormatter dataFormatter = new DataFormatter();
		List<List<String>> test = new ArrayList<List<String>>();
		try {
			XSSFWorkbook workbook = new XSSFWorkbook(getFile());
			for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
				XSSFSheet sheet = workbook.getSheetAt(i);
				Iterator<?> rows = sheet.rowIterator();
				while (rows.hasNext()) {
					XSSFRow row = (XSSFRow) rows.next();
					Iterator<?> cells = row.cellIterator();
					List<String> data = new ArrayList<String>();
					while (cells.hasNext()) {
						XSSFCell cell = (XSSFCell) cells.next();
						data.add(dataFormatter.formatCellValue(cell));
					}
					test.add(data);
				}
				workbook.close();
				test.remove(0);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	public static void main(String args[]) throws IOException {
		getDataOfSpecificColumn();
		
	}

	public static void getDataOfSpecificColumn() throws IOException {
		DataFormatter dataFormatter = new DataFormatter();
		XSSFWorkbook workbook = new XSSFWorkbook(getFile());
		XSSFSheet sheet = workbook.getSheetAt(0);
		List<String> numbers = new ArrayList<String>();
		List<String> notNumbers = new ArrayList<String>();
		List<String> data = new ArrayList<String>();
		for (int rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
			XSSFRow row = sheet.getRow(rowIndex);
			XSSFCell cell = row.getCell(1);
			data.add(dataFormatter.formatCellValue(cell));
		}
		workbook.close();
		data.remove(0);
		
		for(String s : data) {
			if(RegularExpressions.isNumber(s)) {
				numbers.add(s);
			}else {
				notNumbers.add(s);
			}
		}
		System.out.println("\n**Total Entries: " + data.size());
		int fooCount=0;
		for(String foo :data) {
			if(foo.equals("0")) {
				fooCount = fooCount+1;
			}
		}
		System.out.println("\n**Numbers having reg. no 0-> "+ fooCount);
		
		System.out.println("\n**Total Number entries: " + numbers.size());
		
		System.out.println("\n**Total Non-number entries: " + notNumbers.size());
		for(String n:notNumbers) {
			System.out.println(n);
		}
		System.out.println("\n");
		
		
		Map<String, Integer> duplicates = new HashMap<String, Integer>();
		for (String str : data) {
			if (duplicates.containsKey(str)) {
				duplicates.put(str, duplicates.get(str) + 1);
			} else {
				duplicates.put(str, 1);
			}
		}
		int dupCount=0;
		System.out.println("\n**Dups**");
		for (Map.Entry<String, Integer> entry : duplicates.entrySet()) {
			if (entry.getValue() == 2 || entry.getValue() == 3) {
				System.out.println(entry.getKey() + " = " + entry.getValue());
				dupCount = dupCount+1;
			}
		}
		System.out.println("\n**Duplicate Entries: "+ dupCount);
		/*
		 * FIND 
		 */
		/*List<Integer> intList = new ArrayList<Integer>();
		for (String s : data) {
			intList.add(Integer.valueOf(s));
		}
		int head = intList.get(0);
		int tail = intList.get(intList.size() - 1);

		int length = tail - head + 1;
		int[] array = new int[length];

		for (int i : intList) {
			array[i - head] = 1;
		}

		for (int i = 0; i < array.length; i++) {
			if (array[i] == 0) {
				System.out.println(String.format("Missing %d, position %d", i + head, i + 1));
			}
		}*/
	}
		
	public static List<Integer> stringToIntegerList(List<String> list) {
		List<Integer> intList = new ArrayList<Integer>();
		try {
			for (String s : list) {
				intList.add(Integer.valueOf(s));
			}
		} catch (NumberFormatException nume) {

		}
		return intList;
	}

}
