package ar.edu.itba.paw.models;

public class DoctorShift {
    private final long id;
    private final long doctorId;
    private final WeekdayEnum weekday;
    private final String address;
    private final int amount;
    private final String range;

    public DoctorShift(long id, long doctorId, WeekdayEnum weekday, String address, int amount, String range){
        this.id = id;
        this.doctorId = doctorId;
        this.weekday = weekday;
        this.address = address;
        this.amount = amount;
        this.range = range;
    }

    public long getId(){
        return id;
    }

    public long getDoctorId(){
        return doctorId;
    }

    public WeekdayEnum getWeekday(){
        return weekday;
    }

    public String getAddress(){
        return address;
    }

    public int getAmount(){
        return amount;
    }

    public String getRange(){
        return range;
    }
}
