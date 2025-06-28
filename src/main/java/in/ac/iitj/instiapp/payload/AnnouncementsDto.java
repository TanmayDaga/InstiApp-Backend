package in.ac.iitj.instiapp.payload;

import in.ac.iitj.instiapp.payload.Media.MediaBaseDto;
import in.ac.iitj.instiapp.payload.User.UserBaseDto;
import lombok.Value;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * DTO for {@link in.ac.iitj.instiappa.database.entities.Announcements}
 */
@Value
public class AnnouncementsDto implements Serializable {
    UserBaseDto userBaseDto;
    String Title;
    String Description;    Date dateOfAnnouncement;
    Set<MediaBaseDto> media;
    String publicId;
    public AnnouncementsDto(String username, String title, String description, Date dateOfAnnouncement, Set<String> mediaPublicIds, String publicId) {
        this.userBaseDto = new UserBaseDto(username);
        this.Title = title;
        this.Description = description;
        this.dateOfAnnouncement = dateOfAnnouncement;
        this.media = mediaPublicIds.stream().map(MediaBaseDto::new).collect(Collectors.toSet());
        this.publicId = publicId;
    }

}