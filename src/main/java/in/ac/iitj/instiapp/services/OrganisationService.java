package in.ac.iitj.instiapp.services;

import in.ac.iitj.instiapp.database.entities.User.Organisation.OrganisationType;
import in.ac.iitj.instiapp.payload.User.Organisation.OrganisationBaseDto;
import in.ac.iitj.instiapp.payload.User.Organisation.OrganisationDetailedDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface OrganisationService {

    /*--------------------------------------------------------ORGANISATION TYPES---------------------------------------------------*/
    
    /**
     * Saves a new organisation type
     * @param organisationType The organisation type to save
     * @throws org.springframework.dao.DataIntegrityViolationException if OrganisationType with same name exists
     */
    void saveOrganisationType(OrganisationType organisationType);

    /**
     * Gets all organisation types with pagination
     * @param pageable Pagination parameters
     * @return List of organisation type names
     */
    List<String> getAllOrganisationTypes(Pageable pageable);

    /**
     * Checks if organisation type exists
     * @param name The organisation type name to check
     * @return The organisation type ID if exists, -1 otherwise
     */
    Long existsOrganisationType(String name);

    /**
     * Updates an organisation type name
     * @param oldName The current name
     * @param newName The new name
     * @throws org.springframework.dao.EmptyResultDataAccessException if oldName doesn't exist
     * @throws org.springframework.dao.DataIntegrityViolationException if newName already exists
     */
    void updateOrganisationType(String oldName, String newName);

    /**
     * Deletes an organisation type
     * @param name The organisation type name to delete
     * @throws org.springframework.dao.EmptyResultDataAccessException if the organisationType doesn't exist
     */
    void deleteOrganisationType(String name);

    /*--------------------------------------------------------ORGANISATIONS---------------------------------------------------*/

    /**
     * Saves a new organisation
     * @param organisationBaseDto The organisation data to save
     * @param username The username of the user creating the organisation
     * @throws org.springframework.dao.DataIntegrityViolationException if username with organisation already exists
     */
    void saveOrganisation(OrganisationBaseDto organisationBaseDto, String username);

    /**
     * Gets organisation by username
     * @param username The organisation's username
     * @return OrganisationBaseDto with basic organisation information
     * @throws org.springframework.dao.EmptyResultDataAccessException if username doesn't exist
     */
    OrganisationBaseDto getOrganisation(String username);

    /**
     * Gets organisations by type with pagination
     * @param organisationTypeName The organisation type name
     * @param pageable Pagination parameters
     * @return List of organisations of the specified type
     */
    List<OrganisationBaseDto> getOrganisationByType(String organisationTypeName, Pageable pageable);

    /**
     * Gets detailed organisation information
     * @param username The organisation's username
     * @return OrganisationDetailedDto with complete organisation information
     * @throws org.springframework.dao.EmptyResultDataAccessException if username doesn't exist
     */
    OrganisationDetailedDto getOrganisationDetailed(String username);

    /**
     * Checks if organisation exists
     * @param username The organisation's username
     * @return The organisation ID if exists, -1 otherwise
     */
    Long existOrganisation(String username);

    /**
     * Updates an existing organisation
     * @param organisationBaseDto The updated organisation data
     * @param username The organisation's username
     * @return List of old media public IDs that were replaced (for cleanup)
     * @throws org.springframework.dao.EmptyResultDataAccessException if organisation doesn't exist
     */
    Optional<List<String>> updateOrganisation(OrganisationBaseDto organisationBaseDto, String username);

    /**
     * Deletes an organisation
     * @param username The organisation's username
     * @return Success message
     * @throws org.springframework.dao.EmptyResultDataAccessException if organisation doesn't exist
     */
    String deleteOrganisation(String username);
}
