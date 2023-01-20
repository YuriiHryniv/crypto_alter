package com.negeso.crypto.data.service;

import com.negeso.crypto.data.entity.Transaction;
import com.negeso.crypto.data.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {
    private final TransactionRepository repository;

    @Autowired
    public TransactionService(TransactionRepository repository) {
        this.repository = repository;
    }

    public List<Transaction> getTestTransactions() {
        return repository.getTestTransactions();
    }
}
