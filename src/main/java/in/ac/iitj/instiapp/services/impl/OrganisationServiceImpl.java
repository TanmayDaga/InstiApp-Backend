package in.ac.iitj.instiapp.services.impl;

import in.ac.iitj.instiapp.Repository.User.Organisation.OrganisationRepository;
import in.ac.iitj.instiapp.Repository.UserRepository;
import in.ac.iitj.instiapp.database.entities.User.Organisation.Organisation;
import in.ac.iitj.instiapp.database.entities.User.Organisation.OrganisationType;
import in.ac.iitj.instiapp.database.entities.User.User;
import in.ac.iitj.instiapp.payload.User.Organisation.OrganisationBaseDto;
import in.ac.iitj.instiapp.payload.User.Organisation.OrganisationDetailedDto;
import in.ac.iitj.instiapp.services.OrganisationService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrganisationServiceImpl implements OrganisationService {    private final OrganisationRepository organisationRepository;
    private final UserRepository userRepository;
    private final EntityManager entityManager;

    @Autowired
    public OrganisationServiceImpl(OrganisationRepository organisationRepository, 
                                 UserRepository userRepository,
                                 EntityManager entityManager) {
        this.organisationRepository = organisationRepository;
        this.userRepository = userRepository;
        this.entityManager = entityManager;
    }

    /*--------------------------------------------------------ORGANISATION TYPES---------------------------------------------------*/

    @Override
    @Transactional
    public void saveOrganisationType(OrganisationType organisationType) {
        organisationRepository.saveOrganisationType(organisationType);
    }

    @Override
    public List<String> getAllOrganisationTypes(Pageable pageable) {
        return organisationRepository.getAllOrganisationTypes(pageable);
    }

    @Override
    public Long existsOrganisationType(String name) {
        return organisationRepository.existsOrganisationType(name);
    }

    @Override
    @Transactional
    public void updateOrganisationType(String oldName, String newName) {
        organisationRepository.updateOrganisationType(oldName, newName);
    }

    @Override
    @Transactional
    public void deleteOrganisationType(String name) {
        organisationRepository.deleteOrganisationType(name);
    }

    /*--------------------------------------------------------ORGANISATIONS---------------------------------------------------*/

    @Override
    @Transactional
    public void saveOrganisation(OrganisationBaseDto organisationBaseDto, String username) {
        Organisation organisation = convertDtoToEntity(organisationBaseDto);
        
        // Set the user for this organisation
        Long userId = userRepository.getUserIdFromUsername(username);
        if (userId == null) {
            throw new EmptyResultDataAccessException("User not found with username: " + username, 1);
        }
        organisation.setUser(entityManager.getReference(User.class, userId));
        
        organisationRepository.save(organisation);
    }

    @Override
    public OrganisationBaseDto getOrganisation(String username) {
        return organisationRepository.getOrganisation(username);
    }

    @Override
    public List<OrganisationBaseDto> getOrganisationByType(String organisationTypeName, Pageable pageable) {
        Long typeId = organisationRepository.existsOrganisationType(organisationTypeName);
        if (typeId == -1L) {
            throw new EmptyResultDataAccessException("Organisation type not found: " + organisationTypeName, 1);
        }
        
        OrganisationType organisationType = new OrganisationType();
        organisationType.setId(typeId);
        organisationType.setName(organisationTypeName);
        
        return organisationRepository.getOrganisationByType(organisationType, pageable);
    }

    @Override
    public OrganisationDetailedDto getOrganisationDetailed(String username) {
        return organisationRepository.organisationDetailed(username);
    }

    @Override
    public Long existOrganisation(String username) {
        return organisationRepository.existOrganisation(username);
    }

    @Override
    @Transactional
    public Optional<List<String>> updateOrganisation(OrganisationBaseDto organisationBaseDto, String username) {
        // Check if organisation exists
        Long organisationId = organisationRepository.existOrganisation(username);
        if (organisationId == -1L) {
            throw new EmptyResultDataAccessException("Organisation not found with username: " + username, 1);
        }

        // Convert DTO to entity and set ID
        Organisation organisation = convertDtoToEntity(organisationBaseDto);
        organisation.setId(organisationId);

        return organisationRepository.updateOrganisation(organisation);
    }

    @Override
    @Transactional
    public String deleteOrganisation(String username) {
        return organisationRepository.deleteOrganisation(username);
    }

    /*--------------------------------------------------------HELPER METHODS---------------------------------------------------*/

    /**
     * Converts OrganisationBaseDto to Organisation entity
     */
    private Organisation convertDtoToEntity(OrganisationBaseDto dto) {
        Organisation organisation = new Organisation();
        
        organisation.setDescription(dto.getDescription());
        organisation.setWebsite(dto.getWebsite());

        // Set organisation type
        if (dto.getTypeName() != null) {
            Long typeId = organisationRepository.existsOrganisationType(dto.getTypeName());
            if (typeId == -1L) {
                throw new EmptyResultDataAccessException("Organisation type not found: " + dto.getTypeName(), 1);
            }
            organisation.setType(entityManager.getReference(OrganisationType.class, typeId));
        }

        // Set parent organisation if specified
        if (dto.getParentOrganisationUserUserName() != null) {
            Long parentOrgId = organisationRepository.existOrganisation(dto.getParentOrganisationUserUserName());
            if (parentOrgId == -1L) {
                throw new EmptyResultDataAccessException("Parent organisation not found: " + dto.getParentOrganisationUserUserName(), 1);
            }
            organisation.setParentOrganisation(entityManager.getReference(Organisation.class, parentOrgId));
        }

        return organisation;
    }
}
