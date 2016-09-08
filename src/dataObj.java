
public class dataObj {
	String sun = ("SubjectivizationSubjectivization");
	String mon = ("MonochromaticityMonochromaticity");
	String tue = ("TumultuousnessesTumultuousnesses");
	String wed = ("WeltanschauungenWeltanschauungen");
	String thu = ("ThoughtfulnessesThoughtfulnesses");
	String fri = ("FriendlessnessesFriendlessnesses");
	String sat = ("SalubriousnessesSalubriousnesses");
	String output = "";
		
		public String getMOTD (int input) {
			switch(input){
			  case 1: output = sun; 
			  break;
			  case 2: output = mon; 
			  break;
			  case 3: output = tue; 
			  break;
			  case 4: output = wed; 
			  break;
			  case 5: output = thu; 
			  break;
			  case 6: output = fri; 
			  break;
			  case 7: output = sat; 
			  break;
			  }
			  return output;
		}
}
