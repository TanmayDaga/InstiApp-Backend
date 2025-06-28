package in.ac.iitj.instiapp.controllers;

import in.ac.iitj.instiapp.config.JwtProvider;
import in.ac.iitj.instiapp.database.entities.User.Organisation.OrganisationType;
import in.ac.iitj.instiapp.payload.User.Organisation.OrganisationBaseDto;
import in.ac.iitj.instiapp.payload.User.Organisation.OrganisationDetailedDto;
import in.ac.iitj.instiapp.payload.common.ApiResponse;
import in.ac.iitj.instiapp.services.OrganisationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organisations")
public class OrganisationController {    private final OrganisationService organisationService;
    private final JwtProvider jwtProvider;

    @Autowired
    public OrganisationController(OrganisationService organisationService, JwtProvider jwtProvider) {
        this.organisationService = organisationService;
        this.jwtProvider = jwtProvider;
    }

    /*--------------------------------------------------------ORGANISATION TYPES---------------------------------------------------*/

    @GetMapping("/types")
    public ResponseEntity<ApiResponse<List<String>>> getAllOrganisationTypes(Pageable pageable) {
        try {
            List<String> types = organisationService.getAllOrganisationTypes(pageable);
            return ResponseEntity.ok(
                    new ApiResponse<>(
                            HttpStatus.OK.value(),
                            null,
                            "Organisation types fetched successfully",
                            types,
                            null
                    )
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "TYPES_FETCH_ERROR",
                            "Error fetching organisation types: " + e.getMessage(),
                            null,
                            null
                    ));
        }
    }

    @PostMapping("/types")
    public ResponseEntity<ApiResponse<Void>> saveOrganisationType(@RequestBody String typeName) {
        try {
            OrganisationType organisationType = new OrganisationType();
            organisationType.setName(typeName);
            organisationService.saveOrganisationType(organisationType);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(
                            HttpStatus.CREATED.value(),
                            null,
                            "Organisation type saved successfully",
                            null,
                            null
                    ));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse<>(
                            HttpStatus.CONFLICT.value(),
                            "TYPE_CONFLICT",
                            e.getMessage(),
                            null,
                            null
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            HttpStatus.BAD_REQUEST.value(),
                            "TYPE_SAVE_ERROR",
                            "Error saving organisation type: " + e.getMessage(),
                            null,
                            null
                    ));
        }
    }

    @PutMapping("/types")
    public ResponseEntity<ApiResponse<Void>> updateOrganisationType(@RequestParam String oldTypeName, @RequestParam String newTypeName) {
        try {
            organisationService.updateOrganisationType(oldTypeName, newTypeName);
            return ResponseEntity.ok(
                    new ApiResponse<>(
                            HttpStatus.OK.value(),
                            null,
                            "Organisation type updated successfully",
                            null,
                            null
                    )
            );
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse<>(
                            HttpStatus.CONFLICT.value(),
                            "TYPE_CONFLICT",
                            e.getMessage(),
                            null,
                            null
                    ));
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(
                            HttpStatus.NOT_FOUND.value(),
                            "TYPE_NOT_FOUND",
                            e.getMessage(),
                            null,
                            null
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            HttpStatus.BAD_REQUEST.value(),
                            "TYPE_UPDATE_ERROR",
                            "Error updating organisation type: " + e.getMessage(),
                            null,
                            null
                    ));
        }
    }

    @DeleteMapping("/types")
    public ResponseEntity<ApiResponse<Void>> deleteOrganisationType(@RequestBody String typeName) {
        try {
            organisationService.deleteOrganisationType(typeName);
            return ResponseEntity.ok(
                    new ApiResponse<>(
                            HttpStatus.OK.value(),
                            null,
                            "Organisation type deleted successfully",
                            null,
                            null
                    )
            );
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(
                            HttpStatus.NOT_FOUND.value(),
                            "TYPE_NOT_FOUND",
                            e.getMessage(),
                            null,
                            null
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            HttpStatus.BAD_REQUEST.value(),
                            "TYPE_DELETE_ERROR",
                            "Error deleting organisation type: " + e.getMessage(),
                            null,
                            null
                    ));
        }
    }

    /*--------------------------------------------------------ORGANISATIONS---------------------------------------------------*/

    @PostMapping("/")
    public ResponseEntity<ApiResponse<Void>> saveOrganisation(@RequestHeader("Authorization") String jwt, @Valid @RequestBody OrganisationBaseDto organisationBaseDto) {
        try {
            String username = jwtProvider.getUsernameFromToken(jwt);
            organisationService.saveOrganisation(organisationBaseDto, username);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(
                            HttpStatus.CREATED.value(),
                            null,
                            "Organisation saved successfully",
                            null,
                            null
                    ));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse<>(
                            HttpStatus.CONFLICT.value(),
                            "ORGANISATION_CONFLICT",
                            e.getMessage(),
                            null,
                            null
                    ));
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(
                            HttpStatus.NOT_FOUND.value(),
                            "USER_NOT_FOUND",
                            e.getMessage(),
                            null,
                            null
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            HttpStatus.BAD_REQUEST.value(),
                            "ORGANISATION_SAVE_ERROR",
                            "Error saving organisation: " + e.getMessage(),
                            null,
                            null
                    ));
        }
    }

    @GetMapping("/{username}")
    public ResponseEntity<ApiResponse<OrganisationBaseDto>> getOrganisation(@PathVariable String username) {
        try {
            OrganisationBaseDto organisation = organisationService.getOrganisation(username);
            return ResponseEntity.ok(
                    new ApiResponse<>(
                            HttpStatus.OK.value(),
                            null,
                            "Organisation fetched successfully",
                            organisation,
                            null
                    )
            );
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(
                            HttpStatus.NOT_FOUND.value(),
                            "ORGANISATION_NOT_FOUND",
                            e.getMessage(),
                            null,
                            null
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "ORGANISATION_FETCH_ERROR",
                            "Error fetching organisation: " + e.getMessage(),
                            null,
                            null
                    ));
        }
    }

    @GetMapping("/detailed/{username}")
    public ResponseEntity<ApiResponse<OrganisationDetailedDto>> getOrganisationDetailed(@PathVariable String username) {
        try {
            OrganisationDetailedDto organisation = organisationService.getOrganisationDetailed(username);
            return ResponseEntity.ok(
                    new ApiResponse<>(
                            HttpStatus.OK.value(),
                            null,
                            "Organisation details fetched successfully",
                            organisation,
                            null
                    )
            );
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(
                            HttpStatus.NOT_FOUND.value(),
                            "ORGANISATION_NOT_FOUND",
                            e.getMessage(),
                            null,
                            null
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "ORGANISATION_FETCH_ERROR",
                            "Error fetching organisation details: " + e.getMessage(),
                            null,
                            null
                    ));
        }
    }

    @GetMapping("/by-type")
    public ResponseEntity<ApiResponse<List<OrganisationBaseDto>>> getOrganisationsByType(
            @RequestParam String typeName,
            Pageable pageable) {
        try {
            List<OrganisationBaseDto> organisations = organisationService.getOrganisationByType(typeName, pageable);
            return ResponseEntity.ok(
                    new ApiResponse<>(
                            HttpStatus.OK.value(),
                            null,
                            "Organisations fetched successfully",
                            organisations,
                            null
                    )
            );
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(
                            HttpStatus.NOT_FOUND.value(),
                            "TYPE_NOT_FOUND",
                            e.getMessage(),
                            null,
                            null
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "ORGANISATIONS_FETCH_ERROR",
                            "Error fetching organisations: " + e.getMessage(),
                            null,
                            null
                    ));
        }
    }

    @PutMapping("/{username}")
    public ResponseEntity<ApiResponse<Void>> updateOrganisation(@RequestHeader("Authorization") String jwt, @PathVariable String username, @Valid @RequestBody OrganisationBaseDto organisationBaseDto) {
        String currentUser = jwtProvider.getUsernameFromToken(jwt);
        
        // Check if current user is authorized to update this organisation
        if (!currentUser.equals(username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(
                            HttpStatus.FORBIDDEN.value(),
                            "FORBIDDEN",
                            "You are not authorized to update this organisation",
                            null,
                            null
                    ));
        }        try {
            organisationService.updateOrganisation(organisationBaseDto, username);
            return ResponseEntity.ok(
                    new ApiResponse<>(
                            HttpStatus.OK.value(),
                            null,
                            "Organisation updated successfully",
                            null,
                            null
                    )
            );
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(
                            HttpStatus.NOT_FOUND.value(),
                            "ORGANISATION_NOT_FOUND",
                            e.getMessage(),
                            null,
                            null
                    ));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse<>(
                            HttpStatus.CONFLICT.value(),
                            "ORGANISATION_CONFLICT",
                            e.getMessage(),
                            null,
                            null
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            HttpStatus.BAD_REQUEST.value(),
                            "ORGANISATION_UPDATE_ERROR",
                            "Error updating organisation: " + e.getMessage(),
                            null,
                            null
                    ));
        }
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<ApiResponse<Void>> deleteOrganisation(@RequestHeader("Authorization") String jwt, @PathVariable String username) {
        String currentUser = jwtProvider.getUsernameFromToken(jwt);
        
        // Check if current user is authorized to delete this organisation
        if (!currentUser.equals(username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(
                            HttpStatus.FORBIDDEN.value(),
                            "FORBIDDEN",
                            "You are not authorized to delete this organisation",
                            null,
                            null
                    ));
        }

        try {
            organisationService.deleteOrganisation(username);
            return ResponseEntity.ok(
                    new ApiResponse<>(
                            HttpStatus.OK.value(),
                            null,
                            "Organisation deleted successfully",
                            null,
                            null
                    )
            );
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(
                            HttpStatus.NOT_FOUND.value(),
                            "ORGANISATION_NOT_FOUND",
                            e.getMessage(),
                            null,
                            null
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            HttpStatus.BAD_REQUEST.value(),
                            "ORGANISATION_DELETE_ERROR",
                            "Error deleting organisation: " + e.getMessage(),
                            null,
                            null
                    ));
        }
    }
}
