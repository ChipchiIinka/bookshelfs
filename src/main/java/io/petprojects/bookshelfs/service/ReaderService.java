package io.petprojects.bookshelfs.service;

import io.petprojects.bookshelfs.exception.BookshelfsException;
import io.petprojects.bookshelfs.exception.ErrorType;
import io.petprojects.bookshelfs.model.ReaderResponse;
import io.petprojects.bookshelfs.repository.ReaderRepository;
import io.petprojects.bookshelfs.service.mapper.ReaderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReaderService {

    private final ReaderRepository readerRepository;
    private final ReaderMapper readerMapper;

    public List<ReaderResponse> findAllReaders() {
        return readerMapper.toResponseList(readerRepository.findAll());
    }

    public ReaderResponse findReaderById(Long id) {
        return readerMapper.toResponse(readerRepository.findById(id)
                .orElseThrow(() -> new BookshelfsException(ErrorType.NOT_FOUND,
                        "No reader found with id: " + id)));
    }
}
