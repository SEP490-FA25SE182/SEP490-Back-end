package com.sep.aiservice.service;

import com.sep.aiservice.dto.OnlinePlagiarismRequestDTO;
import com.sep.aiservice.dto.OnlinePlagiarismResultDTO;

public interface OnlinePlagiarismService {
    OnlinePlagiarismResultDTO scan(OnlinePlagiarismRequestDTO request);
}
