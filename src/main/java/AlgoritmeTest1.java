import java.sql.SQLException;

public class AlgoritmeTest1  {
	 
	public static void main(String[] args) throws SQLException {

	}
	
	public void controlfridges() {
		Data[] List = mySQLtest.getLastTemp(); //Hent data.
		int n = List.length;
		boolean running = true;
		int offset = 0, count = 0;
		int maxTndt = (int) Math.floor((double) (n) * 0.6);

		while(running){
			//Slukker for alle k�leskabe under 2 grader og t�nder alle k�leskabe over 5
			//update(List); //EK Comment: getLastTemp funktionen gør dette.
			for(int i = 0; i<n; i++){ //for alle med temp h�jere end th
				if(List[i].getTemp() < List[i].getTempHigh()){
					break;
				}
				if(List[i].getState() == false){
					List[i].setState(true); 
					offset++;
				}
			}
			
			count = offset;
			for(int i = n - 1; i <= 0; i--) { //for alle med temp lavere end tl
				if(List[i].getTemp() > List[i].getTempLow()){
					break;
				}
				if(List[i].getState() == true) {
					List[i].setState(false);
					count++;
				}
			}
			
			for(int i = offset; i < n - count ; i++) { //Tjekker alle k�leskabe mellem th og tl
				if(i < (maxTndt - offset)){
					List[i].setState(true); 
				} else {
					List[i].setState(false);
				}
			}
		}
	}
	
	/*
	public static void update(Data[] array){ 
		int n = array.length;
		for(int i = 0; i < n ; i++) { 
			//array[i].setTemp(tilsvarende data fra database);
			//array[i].setTempLow(tilsvarende data fra database);
			//array[i].setTempHigh(tilsvarende data fra database);
			//array[i].setON(tilsvarende data fra database);
		}
	}*/
}
