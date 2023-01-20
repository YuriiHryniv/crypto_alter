package com.negeso.crypto.data.service.impl;

import com.negeso.crypto.data.entity.Code;
import com.negeso.crypto.data.repository.CodeRepository;
import org.springframework.stereotype.Service;

@Service
public class CodeService {
    private final CodeRepository codeRepository;

    public CodeService(CodeRepository codeRepository) {
        this.codeRepository = codeRepository;
    }

    public Code save(String codeName) {
        Code code = new Code();
        code.setName(codeName);
        return codeRepository.save(code);
    }

    public Code get(String codeName) {
        return codeRepository.getCodeByName(codeName).orElseGet(() -> save(codeName));
    }
}
