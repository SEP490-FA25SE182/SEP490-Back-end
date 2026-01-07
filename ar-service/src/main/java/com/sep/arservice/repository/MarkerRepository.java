package com.sep.arservice.repository;

import com.sep.arservice.enums.IsActived;
import com.sep.arservice.model.Marker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface MarkerRepository extends JpaRepository<Marker, String> {
    boolean existsByMarkerCode(String markerCode);

    boolean existsByMarkerCodeIgnoreCaseAndIsActived(String markerCode, IsActived isActived);

    List<Marker> findAllByIsActived(IsActived isActived);

    Page<Marker> findByMarkerIdInAndIsActived(Collection<String> markerIds, IsActived isActived, Pageable pageable);

    // lookup marker by fiducial (unique per book)
    Optional<Marker> findByBookIdAndTagFamilyAndTagIdAndIsActived(
            String bookId, String tagFamily, Integer tagId, IsActived isActived
    );

    // lấy tagId lớn nhất hiện tại để cấp tagId tiếp theo
    Optional<Marker> findTopByBookIdAndTagFamilyAndIsActivedOrderByTagIdDesc(
            String bookId, String tagFamily, IsActived isActived
    );

    // list active markers for a book (manifest)
    List<Marker> findAllByBookIdAndIsActived(String bookId, IsActived isActived);

}

