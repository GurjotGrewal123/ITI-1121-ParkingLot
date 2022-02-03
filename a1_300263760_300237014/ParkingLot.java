import java.io.File;
import java.util.Scanner;

/**
 * @author Mehrdad Sabetzadeh, University of Ottawa
 */
public class ParkingLot {
	/**
	 * The delimiter that separates values
	 */
	private static final String SEPARATOR = ",";

	/**
	 * The delimiter that separates the parking lot design section from the parked
	 * car data section
	 */
	private static final String SECTIONER = "###";

	/**
	 * Instance variable for storing the number of rows in a parking lot
	 */
	private int numRows;

	/**
	 * Instance variable for storing the number of spaces per row in a parking lot
	 */
	private int numSpotsPerRow;

	/**
	 * Instance variable (two-dimensional array) for storing the lot design
	 */
	private CarType[][] lotDesign;

	/**
	 * Instance variable (two-dimensional array) for storing occupancy information
	 * for the spots in the lot
	 */
	private Car[][] occupancy;

	/**
	 * Constructs a parking lot by loading a file
	 * 
	 * @param strFilename is the name of the file
	 */
	public ParkingLot(String strFilename) throws Exception {

		if (strFilename == null) {
			System.out.println("File name cannot be null.");
			return;
		}

		// determine numRows and numSpotsPerRow; you can do so by
		// writing your own code or alternatively completing the 
		// private calculateLotDimensions(...) that I have provided
		calculateLotDimensions(strFilename);

		// instantiate the lotDesign and occupancy variables!
		this.lotDesign = new CarType[this.numRows][this.numSpotsPerRow];
		this.occupancy = new Car[this.numRows][this.numSpotsPerRow];

		// populate lotDesign and occupancy; you can do so by
		// writing your own code or alternatively completing the 
		// private populateFromFile(...) that I have provided
		populateFromFile(strFilename);
	}

	/**
	 * Parks a car (c) at a give location (i, j) within the parking lot.
	 * 
	 * @param i is the parking row index
	 * @param j is the index of the spot within row i
	 * @param c is the car to be parked
	 */
	public void park(int i, int j, Car c) {
		if (i < this.numRows && j <this.numSpotsPerRow){
			this.occupancy[i][j] = c;
		}

	}

	/**
	 * Removes the car parked at a given location (i, j) in the parking lot
	 * 
	 * @param i is the parking row index
	 * @param j is the index of the spot within row i
	 * @return the car removed; the method returns null when either i or j are out
	 *         of range, or when there is no car parked at (i, j)
	 */
	public Car remove(int i, int j) {
		if (i > numRows || j > numSpotsPerRow){
			return null;
		} else{
			Car c = this.occupancy[i][j];
			this.occupancy[i][j]= null;
			return c;
		}

	}

	/**
	 * Checks whether a car (which has a certain type) is allowed to park at
	 * location (i, j)
	 * 
	 * @param i is the parking row index
	 * @param j is the index of the spot within row i
	 * @return true if car c can park at (i, j) and false otherwise
	 */
	public boolean canParkAt(int i, int j, Car c) {
		
		if (i < this.numRows || j < this.numSpotsPerRow) {
            if ((lotDesign[i][j] != CarType.NA) && (this.occupancy[i][j] == null) ){
                if (((lotDesign[i][j] == CarType.LARGE) || (lotDesign[i][j] ==CarType.REGULAR) || (lotDesign[i][j] ==CarType.SMALL)) && c.getType() == CarType.SMALL){
                    return true;
                }else if (((lotDesign[i][j] == CarType.LARGE) || (lotDesign[i][j] == CarType.REGULAR)) && c.getType() == CarType.REGULAR) {
                    return true;
                }else if (lotDesign[i][j] ==CarType.LARGE && c.getType() == CarType.LARGE){
                    return true;
                } else if (c.getType() == CarType.ELECTRIC){ 
                    return true;
                }else{
                    return false;
                }
            }else {
                return false;
            }
        }else{
            return false;
        }
	}

	/**
	 * @return the total capacity of the parking lot excluding spots that cannot be
	 *         used for parking (i.e., excluding spots that point to CarType.NA)
	 */
	public int getTotalCapacity() {
		int total = this.numRows*this.numSpotsPerRow; //calculates the total amount of car spots INCLUDING NA
		int i;
		for (i = 0; i < this.numRows; i++ ){
			for (int j = 0; j < this.numSpotsPerRow; j++){
				if (this.lotDesign[i][j] == CarType.NA){
					total--;
				}
			}
		}	//removes all NA
		return total; 

	}

	/**
	 * @return the total occupancy of the parking lot (i.e., the total number of
	 *         cars parked in the lot)
	 */
	public int getTotalOccupancy() {
		int counter = 0;
		
		for (int i=0; i < occupancy.length; i++){
			for (int j = 0; j < occupancy[0].length; j++){
				if (this.occupancy[i][j] != null){
					counter++;
				}
			}
		}
		return counter; 	
	}

	private void calculateLotDimensions(String strFilename) throws Exception {

		Scanner scanner = new Scanner(new File(strFilename));

		while (scanner.hasNext()) {
			String str = scanner.nextLine();
			if (str.equals(SECTIONER)){
				break;
			}
			else if (!str.equals("")){
				String[] currArray = str.split(SEPARATOR);
				this.numSpotsPerRow = currArray.length;
				this.numRows++;
			}

        }

    }

	private void populateFromFile(String strFilename) throws Exception {
		Scanner scanner = new Scanner(new File(strFilename));

		// while loop for reading the lot design
		int currRow, currCol;
		currRow = 0;
		while (scanner.hasNext()) {
			String str = scanner.nextLine();
			if (str.equals(SECTIONER)){
				break;
			}
			else if (!str.equals("")){
				String[] currLotArray = str.split(SEPARATOR);
				for (int i = 0; i < currLotArray.length; i++){
					currCol = i;
					CarType currCarname = Util.getCarTypeByLabel(currLotArray[i].strip());
					this.lotDesign[currRow][currCol] = currCarname;
				}
			currRow++;	
			}

		}

		// while loop for reading occupancy data
		while (scanner.hasNext()) {
			String str = scanner.nextLine();
			if (!str.equals("")){
				String[] currOccArray = str.split(SEPARATOR);
				CarType currCarname = Util.getCarTypeByLabel(currOccArray[2].strip());
				Car carTemp = new Car(currCarname, currOccArray[3].strip());
				if (canParkAt(Integer.parseInt(currOccArray[0].strip()), Integer.parseInt(currOccArray[1].strip()), carTemp)){
					park(Integer.parseInt(currOccArray[0].strip()), Integer.parseInt(currOccArray[1].strip()), carTemp);
				}
				else{
					String currCarType = Util.getLabelByCarType(carTemp.getType());
					System.out.println("Car " + currCarType + "(" + carTemp.getPlateNum() + ") cannot be parked at (" +currOccArray[0].strip()+","+ currOccArray[1].strip()+")");
				}
			}
		}

		scanner.close();
	}

	/**
	 * Produce string representation of the parking lot
	 * 
	 * @return String containing the parking lot information
	 */
	public String toString() {
		// NOTE: The implementation of this method is complete. You do NOT need to
		// change it for the assignment.
		StringBuffer buffer = new StringBuffer();
		buffer.append("==== Lot Design ====").append(System.lineSeparator());

		for (int i = 0; i < lotDesign.length; i++) {
			for (int j = 0; j < lotDesign[0].length; j++) {
				buffer.append((lotDesign[i][j] != null) ? Util.getLabelByCarType(lotDesign[i][j])
						: Util.getLabelByCarType(CarType.NA));
				if (j < numSpotsPerRow - 1) {
					buffer.append(", ");
				}
			}
			buffer.append(System.lineSeparator());
		}

		buffer.append(System.lineSeparator()).append("==== Parking Occupancy ====").append(System.lineSeparator());

		for (int i = 0; i < occupancy.length; i++) {
			for (int j = 0; j < occupancy[0].length; j++) {
				buffer.append(
						"(" + i + ", " + j + "): " + ((occupancy[i][j] != null) ? occupancy[i][j] : "Unoccupied"));
				buffer.append(System.lineSeparator());
			}

		}
		return buffer.toString();
	}

	/**
	 * <b>main</b> of the application. The method first reads from the standard
	 * input the name of the file to process. Next, it creates an instance of
	 * ParkingLot. Finally, it prints to the standard output information about the
	 * instance of the ParkingLot just created.
	 * 
	 * @param args command lines parameters (not used in the body of the method)
	 * @throws Exception
	 */

	public static void main(String args[]) throws Exception {

		StudentInfo.display();

		System.out.print("Please enter the name of the file to process: ");

		Scanner scanner = new Scanner(System.in);

		String strFilename = scanner.nextLine();

		ParkingLot lot = new ParkingLot(strFilename);

		System.out.println("Total number of parkable spots (capacity): " + lot.getTotalCapacity());

		System.out.println("Number of cars currently parked in the lot: " + lot.getTotalOccupancy());

		System.out.print(lot);

	}
}