import java.time.LocalDate;

public class User implements Comparable<User> {
	
	private String name;
	private int timesTable;
	private int bestScore;
	private int timeTaken;
	private LocalDate dayDone;


	public User(String name, int timesTable, int bestScore, int timeTaken, LocalDate dayDone) {
		this.name = name;
		this.timesTable = timesTable;
		this.bestScore = bestScore;
		this.timeTaken = timeTaken;
		this.dayDone = dayDone;
	}

	public String getName() {
		return name;
	}

	public void setname(String name) {
		this.name = name;
	}

	public int getTimesTable() {
		return timesTable;
	}

	public void setTimesTable(int timesTable) {
		this.timesTable = timesTable;
	}

	public int getBestScore() {
		return bestScore;
	}

	public void setBestScore(int bestScore) {
		this.bestScore = bestScore;
	}

	public int getTimeTaken() {
		return timeTaken;
	}

	public void setTimeTaken(int timeTaken) {
		this.timeTaken = timeTaken;
	}
	
	public LocalDate getDayDone() {
		return dayDone;
	}

	public void setDayDone(LocalDate dayDone) {
		this.dayDone = dayDone;
	}
	
	@Override
	public boolean equals(Object otherObj) {
		if (otherObj instanceof User) {
			User otherUser = (User) otherObj;
			
			String otherName = otherUser.name;
			int otherTimesTable = otherUser.timesTable;
			int otherBestScore = otherUser.bestScore;
			int otherTimeTaken = otherUser.timeTaken;
			
			if (name.equalsIgnoreCase(otherName) &&
				timesTable == otherTimesTable &&
				bestScore == otherBestScore &&
				timeTaken == otherTimeTaken) {
				return true;
			} else {
				return false;
			}
			
		} else {
			return false;
		}
	}

	@Override
	public int compareTo(User other) {
		if (name.compareToIgnoreCase(other.name) != 0) {
			// the two users have different names - so order based name
			return name.compareToIgnoreCase(other.name);
		} else if (Integer.compare(timesTable, other.timesTable) != 0) {
			// the users have the same name and different times table - so order based on times table
			return Integer.compare(timesTable, other.timesTable);
		} else if (Integer.compare(bestScore, other.bestScore) != 0){
			// the users have the same name and same times table - so order based on best score (highest to lowest!!!!)
			return Integer.compare(other.bestScore, bestScore);
		} else {
			// the users have the same name and same times table and same best score - so order on time
			return Integer.compare(timeTaken, other.timeTaken);
		}
	}
	
	@Override
	public String toString() {
		String s = name + ", with Times Table: " + timesTable;
		s += "\thas a Best Score of: " + bestScore;
		s += "\tin a time of: " + timeTaken + " seconds";
		s += " on " + dayDone;
		return s;
	}
	

}
