package in.ac.iitj.instiapp.services.impl;

import in.ac.iitj.instiapp.Repository.User.Organisation.OrganisationRepository;
import in.ac.iitj.instiapp.Repository.User.Organisation.OrganisationRoleRepository;
import in.ac.iitj.instiapp.Repository.UserRepository;
import in.ac.iitj.instiapp.database.entities.User.Organisation.Organisation;
import in.ac.iitj.instiapp.database.entities.User.Organisation.OrganisationRole;
import in.ac.iitj.instiapp.payload.User.Organisation.OrganisationRoleDto;
import in.ac.iitj.instiapp.payload.User.UserBaseDto;
import in.ac.iitj.instiapp.services.OrganisationRoleService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class OrganisationRoleServiceImpl implements OrganisationRoleService {

    private final OrganisationRoleRepository organisationRoleRepository;
    private final OrganisationRepository organisationRepository;
    private final UserRepository userRepository;
    private final EntityManager entityManager;

    @Autowired
    public OrganisationRoleServiceImpl(OrganisationRoleRepository organisationRoleRepository,
                                     OrganisationRepository organisationRepository,
                                     UserRepository userRepository,
                                     EntityManager entityManager) {
        this.organisationRoleRepository = organisationRoleRepository;
        this.organisationRepository = organisationRepository;
        this.userRepository = userRepository;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void saveOrganisationRole(OrganisationRoleDto organisationRoleDto, String organisationUsername) {
        // Verify organisation exists
        Long organisationId = organisationRepository.existOrganisation(organisationUsername);
        if (organisationId == -1L) {
            throw new EmptyResultDataAccessException("Organisation not found: " + organisationUsername, 1);
        }

        // Convert DTO to entity
        OrganisationRole organisationRole = convertDtoToEntity(organisationRoleDto, organisationId);
        
        organisationRoleRepository.saveOrganisationRole(organisationRole);
    }

    @Override
    public List<OrganisationRoleDto> getOrganisationRoles(String organisationUsername, Pageable pageable) {
        // Verify organisation exists
        Long organisationId = organisationRepository.existOrganisation(organisationUsername);
        if (organisationId == -1L) {
            throw new EmptyResultDataAccessException("Organisation not found: " + organisationUsername, 1);
        }

        return organisationRoleRepository.getOrganisationRoles(organisationUsername, pageable);
    }

    @Override
    public Long existOrganisationRole(String organisationUsername, String roleName) {
        return organisationRoleRepository.existOrganisationRole(organisationUsername, roleName);
    }

    @Override
    @Transactional
    public void updateOrganisationRole(String organisationUsername, String oldRoleName, OrganisationRoleDto newOrganisationRoleDto) {
        // Verify organisation exists
        Long organisationId = organisationRepository.existOrganisation(organisationUsername);
        if (organisationId == -1L) {
            throw new EmptyResultDataAccessException("Organisation not found: " + organisationUsername, 1);
        }

        // Create old role entity for identification
        OrganisationRole oldRole = new OrganisationRole();
        oldRole.setOrganisation(entityManager.getReference(Organisation.class, organisationId));
        oldRole.setRoleName(oldRoleName);

        // Convert new DTO to entity
        OrganisationRole newRole = convertDtoToEntity(newOrganisationRoleDto, organisationId);

        organisationRoleRepository.updateOrganisationRole(oldRole, newRole);
    }

    @Override
    @Transactional
    public void deleteOrganisationRole(String organisationUsername, String roleName, String deletedRoleName) {
        organisationRoleRepository.deleteOrganisationRole(roleName, organisationUsername, deletedRoleName);
    }

    @Override
    @Transactional
    public void addUserToOrganisationRole(String organisationUsername, String roleName, String userUsername) {
        // Get user ID
        Long userId = userRepository.getUserIdFromUsername(userUsername);
        if (userId == null) {
            throw new EmptyResultDataAccessException("User not found: " + userUsername, 1);
        }

        organisationRoleRepository.insertIntoOrganisationRole(organisationUsername, roleName, userId);
    }

    @Override
    public List<Map<UserBaseDto, OrganisationRoleDto>> getAllOrganisationRoles(String organisationUsername, Pageable pageable) {
        // Verify organisation exists
        Long organisationId = organisationRepository.existOrganisation(organisationUsername);
        if (organisationId == -1L) {
            throw new EmptyResultDataAccessException("Organisation not found: " + organisationUsername, 1);
        }

        return organisationRoleRepository.getAllOrganisationRoles(organisationUsername, pageable);
    }

    @Override
    @Transactional
    public void removeUserFromOrganisationRole(String organisationUsername, String roleName, String userUsername) {
        // Get organisation and user IDs
        Long organisationId = organisationRepository.existOrganisation(organisationUsername);
        if (organisationId == -1L) {
            throw new EmptyResultDataAccessException("Organisation not found: " + organisationUsername, 1);
        }

        Long userId = userRepository.getUserIdFromUsername(userUsername);
        if (userId == null) {
            throw new EmptyResultDataAccessException("User not found: " + userUsername, 1);
        }

        organisationRoleRepository.removePersonFromOrganisationRole(organisationId, roleName, userId);
    }

    /*--------------------------------------------------------HELPER METHODS---------------------------------------------------*/

    /**
     * Converts OrganisationRoleDto to OrganisationRole entity
     */
    private OrganisationRole convertDtoToEntity(OrganisationRoleDto dto, Long organisationId) {
        OrganisationRole role = new OrganisationRole();
        role.setOrganisation(entityManager.getReference(Organisation.class, organisationId));
        role.setRoleName(dto.getRoleName());
        role.setPermission(dto.getPermission());
        return role;
    }
}
