package in.ac.iitj.instiapp.controllers;

import in.ac.iitj.instiapp.database.entities.Scheduling.Buses.BusLocation;
import in.ac.iitj.instiapp.database.entities.Scheduling.Buses.BusOverride;
import in.ac.iitj.instiapp.database.entities.Scheduling.Buses.BusRun;
import in.ac.iitj.instiapp.payload.Scheduling.Buses.BusScheduleDto;
import in.ac.iitj.instiapp.services.BusService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import in.ac.iitj.instiapp.payload.Scheduling.Buses.BusOverrideDto;
import java.util.Objects;

import java.util.*;

@RestController
@RequestMapping("/api")
public class BusController {

    private final BusService busService;
    private final ValidationUtil validationUtil;

    @Autowired
    public BusController(BusService busService, ValidationUtil validationUtil) {
        this.busService = busService;
        this.validationUtil = validationUtil;
    }

/*--------------------------------------------------BUS LOCATION------------------------------------------------------*/
    @PostMapping("/bus-location")
    public ResponseEntity<String> saveBusLocation(@Valid @RequestBody String name) {
        busService.saveBusLocation(name);
        return ResponseEntity.status(HttpStatus.CREATED).body("BusLocation saved successfully");
    }

    @GetMapping("/bus-locations")
    public List<String> getBusLocations(Pageable pageable){
        return busService.getBusLocations(pageable);
    }

    @PutMapping("/bus-location")
    public ResponseEntity<String> updateBusLocation(@Valid @RequestParam String oldName, @Valid @RequestParam String newName) {
        Long oldNameExists = busService.isBusLocationExist(oldName);
        Long newNameExists = busService.isBusLocationExist(newName);

        if (oldNameExists == -1L) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: Old bus location does not exist.");
        }

        if (newNameExists != -1L) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: New bus location name already exists.");
        }

        busService.updateBusLocation(oldName, newName);
        return ResponseEntity.ok("Bus location updated successfully.");
    }

    @DeleteMapping("/bus-location")
    public ResponseEntity<String> deleteBusLocation(@Valid @RequestParam String name) {
        Long oldNameExists = busService.isBusLocationExist(name);
        if(oldNameExists == -1L) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: Bus location does not exist.");
        }
        busService.deleteBusLocation(name);
        return ResponseEntity.ok("Bus location deleted successfully.");
    }

/*------------------------------------------------------BUS SCHEDULE--------------------------------------------------*/
    @PostMapping("/bus-schedule")
    public ResponseEntity<String> saveBusSchedule(@Valid @RequestParam String busNumber) {
        busService.saveBusSchedule(busNumber);
        return ResponseEntity.status(HttpStatus.CREATED).body("BusSchedule saved successfully");
    }

    @GetMapping("/bus-schedule") // Returns a Bus Schedule Dto
    public ResponseEntity<?> getBusSchedule(@Valid @RequestParam String busNumber) {
        Long busNumberExists = busService.existsBusSchedule(busNumber);

        if (busNumberExists == -1L) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The bus number is not found");
        }

        return ResponseEntity.ok(busService.getBusSchedule(busNumber));
    }
    @GetMapping("/bus-numbers")
    public List<String> getBusNumbers(Pageable pageable) {
        return busService.getBusNumbers(pageable);
    }



    @PutMapping("/bus-schedule")
    public ResponseEntity<String> updateBusSchedule(@Valid @RequestParam String oldBusNumber, @Valid @RequestParam String newBusNumber) {
        Long oldBusExists = busService.existsBusSchedule(oldBusNumber);
        Long newBusExists = busService.existsBusSchedule(newBusNumber);

        if (oldBusExists == -1L) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: Old bus number does not exist.");
        }

        if (newBusExists != -1L) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: New bus number already exists.");
        }

        busService.updateBusSchedule(oldBusNumber, newBusNumber);
        return ResponseEntity.ok("Bus schedule updated successfully.");
    }

    @DeleteMapping("/bus-schedule")
    public ResponseEntity<String> deleteBusSchedule(@Valid @RequestParam String busNumber) {
        Long busExists = busService.existsBusSchedule(busNumber);

        if (busExists == -1L) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: Bus schedule does not exist.");
        }

        busService.deleteBusSchedule(busNumber);
        return ResponseEntity.ok("Bus schedule deleted successfully.");
    }
    /*------------------------------------------------------------BUS RUN-------------------------------------------------*/
    @PostMapping("/bus-run")
    public ResponseEntity<String> saveBusRun(@Valid @RequestBody BusRun busRun, @Valid @RequestParam String busNumber) {
        busService.saveBusRun(busRun, busNumber);
        return ResponseEntity.status(HttpStatus.CREATED).body("Bus run saved successfully.");
    }

    @PutMapping("/bus-run")
    public ResponseEntity<String> updateBusScheduleRun(@Valid @RequestParam String publicId, @Valid @RequestBody BusRun newBusRun) {
        if (!busService.existsBusRunByPublicId(publicId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: Bus run does not exist.");
        }

        busService.updateBusScheduleRun(publicId, newBusRun);
        return ResponseEntity.ok("Bus run updated successfully.");
    }

    @DeleteMapping("/bus-runs")
    public ResponseEntity<String> deleteBusRuns(@Valid @RequestBody List<String> busRunPublicIds) {
        busService.deleteBusRuns(busRunPublicIds);
        return ResponseEntity.ok("Bus runs deleted successfully.");
    }

/*------------------------------------------------------BUS OVERRIDE--------------------------------------------------*/
    @PostMapping("/bus-override")
    public ResponseEntity<String> saveBusOverride(@Valid @RequestParam String busNumber, @Valid @RequestBody BusOverride busOverride) {
        busService.saveBusOverride(busNumber, busOverride);
        return ResponseEntity.status(HttpStatus.CREATED).body("Bus override saved successfully.");
    }

    @GetMapping("bus-override")
    public ResponseEntity<List<BusOverrideDto>> getBusOverrideForYearAndMonth(@Valid @RequestParam int year, @Valid @RequestParam int month) {
        return ResponseEntity.ok(busService.getBusOverrideForYearAndMonth(year, month));
    }

    @PutMapping("/bus-override")
    public ResponseEntity<String> updateBusOverride(@Valid @RequestParam String publicId, @Valid @RequestBody BusOverride newBusOverride) {
        if (!busService.existsBusOverrideByPublicId(publicId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: Bus override does not exist.");
        }

        busService.updateBusOverride(publicId, newBusOverride);
        return ResponseEntity.ok("Bus override updated successfully.");
    }

    @DeleteMapping("/bus-override")
    public ResponseEntity<String> deleteBusOverride(@Valid @RequestBody List<String> busOverrideIds) {
        busService.deleteBusOverride(busOverrideIds);
        return ResponseEntity.ok("Bus overrides deleted successfully.");
    }

}












