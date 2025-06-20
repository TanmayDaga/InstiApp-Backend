package in.ac.iitj.instiapp.Tests.EntityTestData.Scheduling.Buses;

import in.ac.iitj.instiapp.database.entities.Scheduling.Buses.BusSchedule;

public enum BusScheduleData {
    BUS_SCHEDULE1("B1"),
    BUS_SCHEDULE2("B2"),
    BUS_SCHEDULE3("B3"),
    BUS_SCHEDULE4("B4"),
    ;


    public final String busname;

    BusScheduleData(String busname) {
        this.busname = busname;
    }

    public BusSchedule toEntity() {
        return new BusSchedule(busname);
    }
}

