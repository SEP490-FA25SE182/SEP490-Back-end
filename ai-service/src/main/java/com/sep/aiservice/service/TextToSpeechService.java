package com.sep.aiservice.service;

import com.sep.aiservice.dto.AudioResponse;
import com.sep.aiservice.dto.TtsGenerateRequest;

public interface TextToSpeechService {
    /**
     * Tạo audio từ văn bản qua Google AI Studio (Gemini TTS),
     * lưu file, lưu Audio, liên kết AIGeneration/AIGenerationTarget,
     * rồi trả về AudioResponse.
     *
     * @param req    tham số sinh TTS
     * @param userId id người dùng gọi (để ghi AIGeneration.userId)
     */
    AudioResponse synthesize(TtsGenerateRequest req, String userId);
}
