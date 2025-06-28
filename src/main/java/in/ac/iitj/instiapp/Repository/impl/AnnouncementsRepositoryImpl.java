package in.ac.iitj.instiapp.Repository.impl;

import in.ac.iitj.instiapp.Repository.AnnouncementsRepository;
import in.ac.iitj.instiapp.database.entities.Announcements;
import in.ac.iitj.instiapp.payload.AnnouncementsDto;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public class AnnouncementsRepositoryImpl implements in.ac.iitj.instiapp.Repository.AnnouncementsRepository {

    private final EntityManager entityManager;

    @Autowired
    public AnnouncementsRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }



    @Override
    public void createAnnouncement(Announcements announcements) {

        entityManager.persist(announcements);
    }    @Override
    public List<AnnouncementsDto> getByGroupIds(List<Long> groupIds) {
        // Since we removed Group support, this method should return all announcements
        // or be modified based on the new requirements
        return entityManager.createQuery("select an from Announcements an", Announcements.class)
                .getResultList()
                .stream()
                .map(an -> new AnnouncementsDto(
                    an.getUser().getUserName(),
                    an.getTitle(),
                    an.getDescription(),
                    an.getDateOfAnnouncement(),
                    an.getMedia() != null ? 
                        an.getMedia().stream().map(m -> m.getPublicId()).collect(java.util.stream.Collectors.toSet()) : 
                        new java.util.HashSet<>(),
                    an.getPublicId()
                ))
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public void updateAnnouncement(String publicId, Announcements announcements) {

    }

    @Override
    public void deleteAnnouncement(String publicId) {

    }

    @Override
    public List<AnnouncementsDto> getAllAnnouncements(Pageable pageable) {
        return List.of();
    }


}
