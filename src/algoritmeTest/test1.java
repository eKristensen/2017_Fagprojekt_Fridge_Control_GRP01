

public class AlgoritmeTest1 {
	 
	public static void main(String[] args) {
		Data[] List; //initialiser list på en eller anden måde.
		int n = List.length; //n er antallet af elementer i filen eller det data set vi importere.
		boolean running = true;
		int offset = 0, count = 0;
		int maxTændt = (int) Math.floor((double) (n) * 0.6);

		while(running){
			//Slukker for alle køleskabe under 2 grader og tænder alle køleskabe over 5
			update(List);
			for(int i = 0; i<n; i++){ //for alle med temp højere end th
				if(List[i].getTemp() < List[i].getTempHigh()){
					break;
				}
				if(List[i].isON() == false){
					List[i].setON(true); 
					offset++;
				}
			}
			
			count = offset;
			for(int i = n - 1; i <= 0; i--) { //for alle med temp lavere end tl
				if(List[i].getTemp() > List[i].getTempLow()){
					break;
				}
				if(List[i].isON() == true) {
					List[i].setON(false);
					count++;
				}
			}
			
			for(int i = offset; i < n - count ; i++) { //Tjekker alle køleskabe mellem th og tl
				if(i < (maxTændt - offset)){
					List[i].setON(true); 
				} else {
					List[i].setON(false);
				}
			}
		}
	}
	
	public static void update(Data[] array){ 
		int n = array.length;
		for(int i = 0; i < n ; i++) { 
			//array[i].setTemp(tilsvarende data fra database);
			//array[i].setTempLow(tilsvarende data fra database);
			//array[i].setTempHigh(tilsvarende data fra database);
			//array[i].setON(tilsvarende data fra database);
		}
	}
}
