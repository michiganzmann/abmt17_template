package ch.ethz.matsim.courses.abmt17_template.JavaUtil;

public class MyDoubleEntry {

	private Double withPassenger;
    private Double total;

    public MyDoubleEntry(Double withPassenger, Double total) {
        this.withPassenger = withPassenger;
        this.total = total;
    }

    public Double getWithPassenger() {
        return withPassenger;
    }

    public Double getTotal() {
        return total;
    }

    public MyDoubleEntry addTotal(Double value) {
        return new MyDoubleEntry(this.withPassenger, this.total + value);
    }
    
    public MyDoubleEntry addWithPassenger(Double value) {
        return new MyDoubleEntry(this.withPassenger  + value, this.total + value);
    }
    
    @Override
    public String toString() {
    	String output = getWithPassenger() + "," + getTotal();
    	return output;
    }

}
