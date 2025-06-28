package in.ac.iitj.instiapp.payload.User.Organisation;

import com.fasterxml.jackson.annotation.JsonProperty;
import in.ac.iitj.instiapp.payload.User.UserBaseDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;

/**
 * DTO for {@link in.ac.iitj.instiapp.database.entities.User.Organisation.Organisation}
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrganisationBaseDto implements Serializable {    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    UserBaseDto user;
    String parentOrganisationUserUserName;
    
    @NotNull(message = "Organisation type name is required")
    String typeName;
    
    @NotNull(message = "Description is required")
    @Size(min = 1, max = 1000, message = "Description must be between 1 and 1000 characters")
    String Description;
    
    String Website;

    public OrganisationBaseDto(String userName, String parentOrganisationUserUserName, String typeName, String Description, String Website){
        this.user = new UserBaseDto(userName);
        this.parentOrganisationUserUserName = parentOrganisationUserUserName;
        this.typeName = typeName;
        this.Description = Description;
        this.Website = Website;
    }


    public OrganisationBaseDto(String username){
        this.user = new UserBaseDto(username);
        this.typeName = null;
        this.Description = null;
        this.Website = null;
        this.parentOrganisationUserUserName = null;
    }



}