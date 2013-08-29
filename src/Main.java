

public class Main {

	public static void main(String[] args) {
		
		DatabaseDriver databaseDriver = new DatabaseDriver();
		databaseDriver.loadData();
		databaseDriver.findShortestRoute();
		System.out.println(databaseDriver);
	}
	
	
}
