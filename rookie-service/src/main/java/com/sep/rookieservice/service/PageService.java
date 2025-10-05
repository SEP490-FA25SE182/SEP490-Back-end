package com.sep.rookieservice.service;

import com.sep.rookieservice.dto.PageRequestDTO;
import com.sep.rookieservice.dto.PageResponseDTO;
import com.sep.rookieservice.enums.IsActived;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PageService {

    PageResponseDTO create(PageRequestDTO dto);

    PageResponseDTO getById(String id);

    PageResponseDTO update(String id, PageRequestDTO dto);

    void softDelete(String id);

    Page<PageResponseDTO> search(String q, String chapterId, IsActived isActived, Pageable pageable);
}
