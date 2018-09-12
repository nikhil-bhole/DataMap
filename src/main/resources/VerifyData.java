package dataVerification;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import database.DatabaseConnector;

public class VerifyData {
	
	static final String OUTPUT_FILE_PATH = "/home/nikhil/output.txt";
	static final String DUMMY_FILE_PATH = "/home/nikhil/dummy.txt";
	static List<String> UNMATCHED_REGISTRATION_NUMBERS = new ArrayList<String>();
	
	public static  boolean isMatch(List<String> actual, List<String> expected){
		return actual.equals(expected);
	}
	
	public static boolean isNotNullNotEmptyNotWhiteSpace(final String string){
		return string != null && !string.isEmpty() && !string.trim().isEmpty();
	}
	
	public static void log(String message) throws IOException { 
	     PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_FILE_PATH, true), true);
	     out.write(message);
	     out.close();
	}
	 
	public static void readAndReplaceList(List<String> list1){ 	
		String oldStr="";
	    for (int i = 0; i < list1.size(); i++) {
	        if (list1.get(i).contains(".")) {
	            oldStr = list1.get(i).replace(".", "-");
	            list1.set(i, oldStr);
	        }
	        else if(list1.get(i).contains("ASF") || list1.get(i).contains("asf")) {
	            oldStr = list1.get(i).replace("ASF", "Ars-Sulph-F");
	            list1.set(i, oldStr);
	        }
	        else if(list1.get(i).contains("SL")|| list1.get(i).contains("Phytum") || list1.get(i).contains("sl") || list1.get(i).contains("S-L-")) {
	            oldStr = list1.get(i).replace("SL", "Sac-Lac");
	            list1.set(i, oldStr);
	        }
	        else if(list1.get(i).contains("acid-nit")) {
	            oldStr = list1.get(i).replace("acid-nit", "Nitric-Acid");
	            list1.set(i, oldStr);
	        }
	        else if(list1.get(i).contains("petr") || list1.get(i).contains("Pettr") || list1.get(i).contains("Petr?") ) {
	            oldStr = list1.get(i).replace("petr", "Petroleum");
	            list1.set(i, oldStr);
	        }
	        else if(list1.get(i).contains("ant-t")) {
	            oldStr = list1.get(i).replace("ant-t", "Antim-Tart");
	            list1.set(i, oldStr);
	        }
	        else if(list1.get(i).contains("ant-c")) {
	            oldStr = list1.get(i).replace("ant-c", "Ant-Crud");
	            list1.set(i, oldStr);
	        }
	    }
	}

	public static boolean areSimilar(String s1, String s2){
		if (StringSimilarity.similarity(s1, s2) > 0.60){
			return true;
		}
		return false;
	}
	
	public static List<String> skipList() throws FileNotFoundException{
		Scanner s = new Scanner(new File("/home/nikhil/ignore_reg_nos.txt"));
		ArrayList<String> list = new ArrayList<String>();
		while (s.hasNext()){
		    list.add(s.next());
		}
		s.close();
		return list;
	}
	
	public static void verifyData(String reg_no) throws IOException{
		
		String query_actual_dates = "SELECT VISITDATE FROM `FOLLOW_RegNo_1_To_4139` WHERE  `REG_NO` = '" + reg_no + "'   ORDER BY STR_TO_DATE(`VISITDATE`, '%d/%m/%Y') ASC";
		String query_expected_dates = "SELECT DATE_FORMAT(`Visit_Date`,'%d/%m/%Y') FROM `PatientVisit` WHERE `PatientRegistrationNo` = '" + reg_no + "'  ORDER BY `Visit_Date` ASC";
		
		String query_actual_remedy = "SELECT `REMEDY` FROM `FOLLOW_RegNo_1_To_4139` WHERE  `REG_NO` = '" + reg_no + "'   ORDER BY STR_TO_DATE(`VISITDATE`, '%d/%m/%Y') ASC";
		String query_expected_remedy = "SELECT visitId, RemedyName FROM PrescriptionRemedyMaster INNER JOIN PatientVisitPrescriptionDetails ON PrescriptionRemedyMaster.RemedyID = PatientVisitPrescriptionDetails.Remedy WHERE `PatientRegistrationNo` = '" + reg_no + "'  ORDER by VisitId asc";
		
		String query_actual_complaints = "SELECT `COMP` FROM `FOLLOW_RegNo_1_To_4139` WHERE  `REG_NO` = '" + reg_no + "'  ORDER BY STR_TO_DATE(`VISITDATE`, '%d/%m/%Y') ASC";
		String query_expected_complaints = "SELECT `PatientComments` FROM `PatientVisit` WHERE `PatientRegistrationNo` = '" + reg_no + "'  ORDER BY `PatientVisit`.`Visit_Date`  ASC";
		
		String query_actual_repetetion = "SELECT `REPETITION` FROM `FOLLOW_RegNo_1_To_4139` WHERE  `REG_NO`='" + reg_no + "' ORDER BY STR_TO_DATE(`VISITDATE`, '%d/%m/%Y') ASC";
		String query_expected_repetetion = "SELECT visitId,RepetitionName FROM PrescriptionRepetitionMaster INNER JOIN PatientVisitPrescriptionDetails ON PrescriptionRepetitionMaster.RepetitionID = PatientVisitPrescriptionDetails.Repetition WHERE PatientRegistrationNo = '" + reg_no + "' ORDER by VisitId";
		
		String query_actual_potency = "SELECT POTENCY FROM `FOLLOW_RegNo_1_To_4139` WHERE  `REG_NO` = '" + reg_no + "'   ORDER BY STR_TO_DATE(`VISITDATE`, '%d/%m/%Y') ASC";
		String query_expected_potency = "SELECT visitId, PotencyName FROM PrescriptionPotencyMaster INNER JOIN  PatientVisitPrescriptionDetails ON PrescriptionPotencyMaster.PotencyID =  PatientVisitPrescriptionDetails.Potency WHERE PatientRegistrationNo = '" + reg_no + "' ORDER by VisitId;";
		
		String query_actual_days = "SELECT DAYS FROM `FOLLOW_RegNo_1_To_4139` WHERE  `REG_NO` = '" + reg_no + "'   ORDER BY STR_TO_DATE(`VISITDATE`, '%d/%m/%Y') ASC";
		String query_expected_days = "SELECT visitId, PrescriptionDaysMaster.Days FROM PrescriptionDaysMaster INNER JOIN  PatientVisitPrescriptionDetails ON PrescriptionDaysMaster.DaysID =  PatientVisitPrescriptionDetails.Days WHERE PatientRegistrationNo = '" + reg_no + "'  ORDER by VisitId";
		
		
		
		List<String> actualVisitDates = DatabaseConnector.executeSQLQuery_List("QA", query_actual_dates);
		List<String> expectedVisitDates = DatabaseConnector.executeSQLQuery_List("QA", query_expected_dates);
		
		List<String> actualRemedy = DatabaseConnector.executeSQLQuery_List("QA", query_actual_remedy);
		List<String> modifiedRemedy = new ArrayList<>();
		
		List<String> expectedRemedy = DatabaseConnector.executeSQLQuery_List("QA", query_expected_remedy);
		
		List<String> actualComplaints = DatabaseConnector.executeSQLQuery_List("QA", query_actual_complaints);
		List<String> expectedComplaints = DatabaseConnector.executeSQLQuery_List("QA", query_expected_complaints);
		
		List<String> actualPotency = DatabaseConnector.executeSQLQuery_List("QA", query_actual_potency);
		List<String> expectedPotecy = DatabaseConnector.executeSQLQuery_List("QA", query_expected_potency);
		
		List<String> actualRepetetion = DatabaseConnector.executeSQLQuery_List("QA", query_actual_repetetion);
		List<String> expectedRepetetion = DatabaseConnector.executeSQLQuery_List("QA", query_expected_repetetion);
		
		List<String> actualDays = DatabaseConnector.executeSQLQuery_List("QA", query_actual_days);
		List<String> expectedDays = DatabaseConnector.executeSQLQuery_List("QA", query_expected_days);
		
		//Modify remedies
		for (int i = 0; i < actualRemedy.size(); i++) {
			if (actualRemedy.get(i).contains(":")) {
				String[] remedies = actualRemedy.get(i).split(":");
				for (int j = 0; j < remedies.length; j++) {
					if (isNotNullNotEmptyNotWhiteSpace(remedies[j])) {
						if (remedies[j].startsWith(" ")) {
							modifiedRemedy.add(i + 1 + remedies[j].replaceAll("\\s+$", ""));
						} else {
							modifiedRemedy.add(i + 1 + " " + remedies[j].replaceAll("\\s+$", ""));
						}
					}
				}
			} else {
				modifiedRemedy.add(i + 1 + " " + actualRemedy.get(i).replaceAll("\\s+$", ""));
			}
		}
		readAndReplaceList(modifiedRemedy);
		try
		{
			log("\n::Registration Number:: "+ reg_no + "\n");
			System.out.println("::Registration Number:: " + reg_no);
			boolean b = isMatch(actualVisitDates, expectedVisitDates);
			log(" ::Visit dates match:: " + b + "\n" );
			log(" ::Remedy:: \n");
			log(" ::Actual Size:: "+ actualRemedy.size() + "  " + "Modified Size:: "+ modifiedRemedy.size() + "  " + "Expected Size:: "+ expectedRemedy.size() + "\n" );
			
			if (modifiedRemedy.size() == expectedRemedy.size()) {
				for (int i = 0; i < expectedRemedy.size(); i++) {
					if (expectedRemedy.get(i).toLowerCase().contains(modifiedRemedy.get(i).toLowerCase()) || areSimilar(expectedRemedy.get(i), modifiedRemedy.get(i))) {
					} else {
						System.out.println("Mismatch at " + expectedRemedy.get(i) + " and " + modifiedRemedy.get(i));
						log("   ::Remedy Mismatch at visit :: " + expectedRemedy.get(i) + " and " + modifiedRemedy.get(i) + "\n");
					}
				}
			}
			else {
				if (!skipList().contains(reg_no)) {
					log(" ::Remedy Count Mismatch:: \n");
					log(" ::Modieifed Remedy  :: " + modifiedRemedy+ "\n");
					log("\n");
					log(" \n::Expected Remedy:: " + expectedRemedy);
					UNMATCHED_REGISTRATION_NUMBERS.add(reg_no);
				}
			}
			
		    
			boolean c = isMatch(actualComplaints, expectedComplaints);
			log(" \n ::Visit complaints Same:: " + c + "\n" );
			log(" ::Actual Potency count:: " + actualPotency.size() +" "+ " ::expected Potency count: " + expectedPotecy.size() + "\n");
			log(" ::Actual Potency:: " + actualPotency  + "\n" + " ::Expected Potency::" + expectedPotecy + "\n");
			log(" ::Actual Repetetion:: " + actualRepetetion.size() + " " + "::Expected Repetetion: "+ expectedRepetetion.size() + "\n ");
			log(" ::Actual Days:: " + actualDays + "\n" + " ::Expected Days: "+ expectedDays + "\n ");
			log("___________________________________________________________________________________________________________________________________________________________________" + "\n");
			System.out.println("___________________________________________________________________________________________________________________________________________________________________" + "\n");
		}
		catch(Exception e){
			System.out.println("Exception" + e.getMessage());
			log("Exception" + e.getMessage());
		}
	}
	
	static void checkall() throws IOException {
			String query = "SELECT DISTINCT(`PatientRegistrationNo`) FROM PatientVisit LIMIT 4139";
			List<String> registrationNumbers = DatabaseConnector.executeSQLQuery_List("QA", query);
			log("TOTAL NUMBER OF REGISTERED PATIENTS ARE : "+ registrationNumbers.size()+"\n");
			for(int i=0;i<registrationNumbers.size();i++){
				verifyData(registrationNumbers.get(i));
			}
			System.out.println("unmatched count: " + UNMATCHED_REGISTRATION_NUMBERS.size());
			UNMATCHED_REGISTRATION_NUMBERS.forEach(System.out::println);
	}
	
	public static void main(String args[]) throws IOException {
		checkall() ;
	}
	
	private static Stream<String> filterNonSimilar(List<String> a, List<String> b) {
	    return a.stream().filter(ai -> b.stream().noneMatch(bi -> ai.startsWith(bi) || bi.startsWith(ai)));
	}

	public static List<String> nonSimilar(List<String> a, List<String> b) {
	    return Stream.concat(filterNonSimilar(a, b), filterNonSimilar(b, a)).collect(Collectors.toList());
	}
}

