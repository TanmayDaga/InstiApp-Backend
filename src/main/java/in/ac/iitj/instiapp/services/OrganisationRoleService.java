package in.ac.iitj.instiapp.services;

import in.ac.iitj.instiapp.payload.User.Organisation.OrganisationRoleDto;
import in.ac.iitj.instiapp.payload.User.UserBaseDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface OrganisationRoleService {

    /**
     * Saves a new organisation role
     * @param organisationRoleDto The organisation role data
     * @param organisationUsername The organisation's username
     * @throws org.springframework.dao.EmptyResultDataAccessException if organisation doesn't exist
     */
    void saveOrganisationRole(OrganisationRoleDto organisationRoleDto, String organisationUsername);

    /**
     * Gets all organisation roles for a specific organisation
     * @param organisationUsername The organisation's username
     * @param pageable Pagination parameters
     * @return List of organisation roles
     */
    List<OrganisationRoleDto> getOrganisationRoles(String organisationUsername, Pageable pageable);

    /**
     * Checks if organisation role exists
     * @param organisationUsername The organisation's username
     * @param roleName The role name
     * @return The role ID if exists, -1 otherwise
     */
    Long existOrganisationRole(String organisationUsername, String roleName);

    /**
     * Updates an organisation role
     * @param organisationUsername The organisation's username
     * @param oldRoleName The current role name
     * @param newOrganisationRoleDto The updated role data
     * @throws org.springframework.dao.EmptyResultDataAccessException if role doesn't exist
     * @throws org.springframework.dao.DataIntegrityViolationException if new role already exists
     */
    void updateOrganisationRole(String organisationUsername, String oldRoleName, OrganisationRoleDto newOrganisationRoleDto);

    /**
     * Deletes an organisation role
     * @param organisationUsername The organisation's username
     * @param roleName The role name to delete
     * @param deletedRoleName The role name to reassign existing users to
     * @throws org.springframework.dao.EmptyResultDataAccessException if role doesn't exist
     */
    void deleteOrganisationRole(String organisationUsername, String roleName, String deletedRoleName);

    /**
     * Adds a user to an organisation role
     * @param organisationUsername The organisation's username
     * @param roleName The role name
     * @param userUsername The user's username
     * @throws org.springframework.dao.DataIntegrityViolationException if user already exists in organisation
     * @throws org.springframework.dao.EmptyResultDataAccessException if organisation or role doesn't exist
     */
    void addUserToOrganisationRole(String organisationUsername, String roleName, String userUsername);

    /**
     * Gets all users with their roles for a specific organisation
     * @param organisationUsername The organisation's username
     * @param pageable Pagination parameters
     * @return Map of users and their roles
     */
    List<Map<UserBaseDto, OrganisationRoleDto>> getAllOrganisationRoles(String organisationUsername, Pageable pageable);

    /**
     * Removes a user from an organisation role
     * @param organisationUsername The organisation's username
     * @param roleName The role name
     * @param userUsername The user's username
     * @throws org.springframework.dao.EmptyResultDataAccessException if organisation, role, or user doesn't exist
     */
    void removeUserFromOrganisationRole(String organisationUsername, String roleName, String userUsername);
}
