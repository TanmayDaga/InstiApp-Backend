package in.ac.iitj.instiapp.Repository.impl;

import in.ac.iitj.instiapp.Repository.BusRepository;
import in.ac.iitj.instiapp.database.entities.Scheduling.Buses.*;
import in.ac.iitj.instiapp.mappers.Scheduling.Buses.BusRunDtoMapper;
import in.ac.iitj.instiapp.payload.Scheduling.Buses.BusOverrideDto;
import in.ac.iitj.instiapp.payload.Scheduling.Buses.BusRunDto;
import in.ac.iitj.instiapp.payload.Scheduling.Buses.BusScheduleDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class BusRepositoryImpl implements BusRepository {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(BusRepositoryImpl.class);
    private final JdbcTemplate jdbcTemplate;
    private final EntityManager entityManager;
    private final BusRunDtoMapper busRunDtoMapper;

    public BusRepositoryImpl(JdbcTemplate jdbcTemplate, EntityManager entityManager, BusRunDtoMapper busRunDtoMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityManager = entityManager;
        this.busRunDtoMapper = busRunDtoMapper;
    }

    // ------------------- Bus Location Operations -------------------

    @Override
    @Transactional
    public void saveBusLocation(String name) {
        if (isBusLocationExists(name) != -1L) {
            throw new DataIntegrityViolationException("Bus location already exists");
        }
        jdbcTemplate.update("insert into bus_location (name) values(?)", name);
        entityManager.flush();
    }

    @Override
    public List<String> getListOfBusLocations(Pageable pageable) {
        Query query = entityManager.createQuery("select name from BusLocation t", String.class);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        return query.getResultList();
    }

    @Override
    public Long isBusLocationExists(String name) {
        try {
            return jdbcTemplate.queryForObject("SELECT id FROM bus_location WHERE name = ?", Long.class, name);
        } catch (DataAccessException ignored) {
            return -1L;
        }
    }

    @Override
    public void updateBusLocation(String oldName, String newName) {
        if (isBusLocationExists(oldName).equals(-1L)) {
            throw new EmptyResultDataAccessException("Bus location does not exist with name " + oldName, 1);
        }
        if (isBusLocationExists(newName) != -1L) {
            throw new DataIntegrityViolationException("Bus location already exists with name " + newName);
        }
        jdbcTemplate.update("update bus_location set name=? where name=?", newName, oldName);
    }

    @Override
    public void deleteBusLocation(String name) {
        if (isBusLocationExists(name).equals(-1L)) {
            throw new EmptyResultDataAccessException("Bus location does not exist", 1);
        }
        jdbcTemplate.update("delete from bus_location where name=?", name);
    }

    @Override
    public BusLocation getLocationById(Long Id){

        return entityManager.createQuery(
                "select new in.ac.iitj.instiapp.database.entities.Scheduling.Buses.BusLocation" +
                "(bu.id, bu.name) from BusLocation bu where bu.id = :id", BusLocation.class)
                .setParameter("id", Id)
                .getSingleResult();
    }

    // ------------------- BusSchedule Operations -------------------
    @Override
    @Transactional
    public void saveBusSchedule(String busNumber) {
        if (existsBusSchedule(busNumber) != -1L) {
            throw new DataIntegrityViolationException("Bus Number already exists with name " + busNumber);
        }
        entityManager.persist(new BusSchedule(busNumber));
    }

    @Override
    public BusScheduleDto getBusSchedule(String busNumber) {
        if (existsBusSchedule(busNumber) != -1L) {
            List<BusRun> busRuns = getBusRunsByBusNumber(busNumber);
            Set<BusRunDto> busRunDtos = busRuns.stream()
                    .map(busRunDtoMapper::toDto)
                    .collect(Collectors.toSet());
            // Return an empty schedule or fetch runs by route if needed
            return new BusScheduleDto(busNumber, busRunDtos);
        }
        throw new EmptyResultDataAccessException("Bus Number" + busNumber + "Does not exists", 1);
    }

    @Override
    public BusSchedule getBusScheduleByBusNumber(String busNumber) {
        try {
            return entityManager.createQuery(
                            "SELECT b FROM BusSchedule b WHERE b.busNumber = :busNumber", BusSchedule.class)
                    .setParameter("busNumber", busNumber)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<String> getBusNumbers(Pageable pageable) {
        return entityManager.createQuery("select bs.busNumber from BusSchedule bs", String.class)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
    }

    @Override
    public Long existsBusSchedule(String busNumber) {
        try {
            return jdbcTemplate.queryForObject("select id from bus_schedule where bus_number = ?", Long.class, busNumber);
        } catch (DataAccessException e) {
            log.error(e.getMessage());
            return -1L;
        }
    }

    @Override
    public void updateBusSchedule(String oldBusNumber, String newBusNumber) {
        if (!existsBusSchedule(newBusNumber).equals(-1L)) {
            throw new DataIntegrityViolationException("Bus with number" + newBusNumber + " already exists");
        }
        if (existsBusSchedule(oldBusNumber).equals(-1L)) {
            throw new EmptyResultDataAccessException("Bus with number" + oldBusNumber + " not found", 1);
        }
        jdbcTemplate.update("update bus_schedule set bus_number=? where bus_number=?", newBusNumber, oldBusNumber);
    }

    @Override
    @Transactional
    public void deleteBusSchedule(String busNumber) {
        if (existsBusSchedule(busNumber).equals(-1L)) {
            throw new EmptyResultDataAccessException("Bus schedule with bus_number " + busNumber + " not found", 1);
        }
        entityManager.createQuery("DELETE FROM BusSchedule bs WHERE bs.busNumber = :busNumber")
                .setParameter("busNumber", busNumber)
                .executeUpdate();
        log.info("Successfully deleted BusSchedule for busNumber: {}", busNumber);
    }

    // ------------------- BusRun with Route Operations -------------------
    @Override
    @Transactional
    public void saveBusRunWithRoute(BusRun busRun) {
        entityManager.persist(busRun);
    }

    @Override
    public Long isBusRouteExists(String name) {
        try {
            return jdbcTemplate.queryForObject("SELECT id FROM bus_route WHERE route_name = ?", Long.class, name);
        } catch (DataAccessException ignored) {
            return -1L;
        }
    }

    @Override
    public List<BusRun> getBusRunsForRoute(String busNumber, Long routeId) {
        return entityManager.createQuery("SELECT br FROM BusRun br WHERE br.busSchedule.busNumber = :busNumber AND br.route.routeId = :routeId", BusRun.class)
                .setParameter("busNumber", busNumber)
                .setParameter("routeId", routeId)
                .getResultList();
    }

    @Override
    public List<BusRun> getBusRunsByBusNumber(String busNumber) {
        return entityManager.createQuery(
                        "SELECT br FROM BusRun br WHERE br.busSchedule.busNumber = :busNumber", BusRun.class)
                .setParameter("busNumber", busNumber)
                .getResultList();
    }

    // ------------------- BusRoute and RouteStop Operations -------------------
    @Override
    @Transactional
    public void saveBusRoute(BusRoute route) {
        entityManager.persist(route);
        entityManager.flush();
        entityManager.refresh(route);
    }

    @Override
    public BusRoute getBusRouteByRouteId(Long routeId) {
        return entityManager.createQuery("SELECT r FROM BusRoute r LEFT JOIN FETCH r.stops WHERE r.id = :routeId", BusRoute.class)
                .setParameter("routeId", routeId)
                .getSingleResult();
    }

    @Override
    public List<BusRoute> getAllBusRoutes() {
        return entityManager.createQuery("SELECT r FROM BusRoute r", BusRoute.class).getResultList();
    }

    @Override
    @Transactional
    public void saveRouteStop(RouteStop stop) {
        BusLocation busLocation = getLocationById(stop.getLocation().getId());
        stop.setLocation(busLocation);

        BusRoute busRoute = getBusRouteByRouteId(stop.getRoute().getId());
        stop.setRoute(busRoute);
        entityManager.persist(stop);
    }

    @Override
    public List<RouteStop> getRouteStopsByRouteId(Long routeId) {
        return entityManager.createQuery("SELECT s FROM RouteStop s WHERE s.route.routeId = :routeId ORDER BY s.stopOrder ASC", RouteStop.class)
                .setParameter("routeId", routeId)
                .getResultList();
    }

    @Override
    public BusRoute findBusRouteByRouteName(String routeName) {
        try {
            return entityManager.createQuery(
                            "SELECT r FROM BusRoute r LEFT JOIN FETCH r.stops WHERE r.routeName = :routeName", BusRoute.class)
                    .setParameter("routeName", routeName)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public String findBusFromBusRoute(String routeName){
        try{
            return entityManager.createQuery(
                            "SELECT b.busNumber " +
                                    "FROM BusSchedule b " +
                                    "JOIN BusRoute r ON b.id = r.id " +
                                    "WHERE r.routeName = :routeName",
                            String.class
                    )
                    .setParameter("routeName", routeName)
                    .getSingleResult();
        }
        catch (NoResultException e){
            return null;
        }

    }

    @Override
    public BusRun getBusRunByBusAndRoute( BusRunDto busRunDto){
        String jpql = """
        SELECT br
        FROM BusRun br
        JOIN br.busSchedule bs
        JOIN br.route r
        WHERE bs.busNumber = :busNumber AND r.routeName = :routeName
    """;
        try {
            return entityManager.createQuery(jpql, BusRun.class)
                    .setParameter("busNumber", busRunDto.getBusNumber())
                    .setParameter("routeName", busRunDto.getRoute().getRouteName())
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

    }
    @Override
    public RouteStop getRouteStopByRouteIdAndLocationId(Long routeId, Long locationId) {
        String jpql = """
        SELECT rs FROM RouteStop rs 
        WHERE rs.route.id = :routeId AND rs.location.id = :locationId
    """;
        try {
            return entityManager.createQuery(jpql, RouteStop.class)
                    .setParameter("routeId", routeId)
                    .setParameter("locationId", locationId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }


    // ------------------- BusOverride Operations (if still needed) -------------------
    @Override
    public void saveBusOverride(String busNumber, BusOverride busOverride) {
        // Implement as needed for new structure
        entityManager.persist(busOverride);
    }

    @Override
    public boolean existsBusOverrideByPublicId(String publicId) {
        // Implement as needed for new structure
        return false;
    }

    @Override
    public List<BusOverrideDto> getBusOverrideForYearAndMonth(int year, int month) {
        // Implement as needed for new structure
        return null;
    }

    @Override
    public void updateBusOverride(String publicId, BusOverride newBusOverride) {
        // Implement as needed for new structure
    }

    @Override
    public void deleteBusOverride(List<String> busOverrideIds) {
        // Implement as needed for new structure
    }



    // Remove all legacy BusSnippet, fromLocation, toLocation, and point-to-point logic.
}
