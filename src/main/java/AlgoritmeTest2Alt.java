import java.io.IOException;
import java.sql.SQLException;

import com.rabbitmq.client.Channel;

public class AlgoritmeTest2Alt  {
	 
	public static void main(String[] args) throws SQLException {

	}
	
	public static void controlFridges(Channel channel) throws SQLException, IOException, InterruptedException, java.lang.ClassNotFoundException {
		Data[] List = mySQLtest.getLastTemp(); //Hent data.
		int n = List.length;
		boolean running = true;
		int offset = 0, count = 0;
		int maxTndt = (int) Math.floor((double) (n) * 0.5);
		int sum = 0;

		while(running){
			//Slukker for alle k�leskabe under 2 grader og t�nder alle k�leskabe over 5
			//update(List); //EK Comment: getLastTemp funktionen gør dette.
			List = mySQLtest.getLastTemp();
			n = List.length;
			for(int i = 0; i<n; i++){ //for alle med temp h�jere end 5 grader
				if(List[i].getTemp() < 5){ 
					break;
				}
				if(List[i].getState() == false){
					List[i].changeState(channel, true);
					offset++;
				}
			}

			for(int i = n - 1; i <= 0; i--) { //for alle med temp lavere end 2 grader
				if(List[i].getTemp() > 2){
					break;
				}
				if(List[i].getState() == true) {
					List[i].changeState(channel, false);
					count++;
				}
			}
			
			for(int i = offset; i < n - count ; i++) { //Tjekker alle k�leskabe mellem 5 og 2 grader
				if(i < (maxTndt)){
					List[i].changeState(channel, true);
					}
				} else {
					List[i].changeState(channel, false);
				}
				sum += List[i].getTemp();
			}
			sum = sum/(n-(offset+count));  //Ændrer antallet af tændte køleskabe afhængigt af middeltemperaturen
			if(sum > 4) {
				maxTndt += 1;
			}else if(sum < 3) {
				maxTndt -= 1;
			}

			offset = 0; count = 0;
			Thread.sleep(180000); //vent 3 minutter)
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
