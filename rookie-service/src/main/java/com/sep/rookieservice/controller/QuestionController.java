package com.sep.rookieservice.controller;
import com.sep.rookieservice.dto.QuestionRequestDTO;
import com.sep.rookieservice.dto.QuestionResponseDTO;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/rookie/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService svc;

    /**
     * Search & pagination endpoint.
     * - page: 0-based index
     * - size: page size
     * - sort: e.g. sort=createdAt,desc
     */
    @GetMapping
    public Page<QuestionResponseDTO> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) List<String> sort,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String quizId,
            @RequestParam(required = false) IsActived isActived
    ) {
        Sort sortObj = Sort.unsorted();
        if (sort != null && !sort.isEmpty()) {
            for (String s : sort) {
                String[] parts = s.split(",");
                String prop = parts[0].trim();
                Sort.Direction dir = (parts.length > 1) ? Sort.Direction.fromString(parts[1].trim()) : Sort.Direction.ASC;
                sortObj = sortObj.and(Sort.by(dir, prop));
            }
        }

        Pageable pageable = PageRequest.of(page, size, sortObj);
        return svc.search(q, quizId, isActived, pageable);
    }

    @GetMapping("/{id}")
    public QuestionResponseDTO getById(@PathVariable String id) {
        return svc.getById(id);
    }

    @PostMapping
    public QuestionResponseDTO create(@RequestBody QuestionRequestDTO dto) {
        return svc.create(dto);
    }

    @PutMapping("/{id}")
    public QuestionResponseDTO update(@PathVariable String id, @RequestBody QuestionRequestDTO dto) {
        return svc.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        svc.delete(id);
    }
}