package io.petprojects.bookshelfs.service;

import io.petprojects.bookshelfs.entity.ReaderEntity;
import io.petprojects.bookshelfs.exception.BookshelfsException;
import io.petprojects.bookshelfs.exception.ErrorType;
import io.petprojects.bookshelfs.model.request.ReaderUpdateRequest;
import io.petprojects.bookshelfs.model.response.ReaderInfoResponse;
import io.petprojects.bookshelfs.model.response.ReaderListResponse;
import io.petprojects.bookshelfs.repository.ReaderRepository;
import io.petprojects.bookshelfs.service.mapper.ReaderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReaderService {

    private final PasswordEncoder passwordEncoder;
    private final BookshelfService bookshelfService;
    private final ReaderRepository readerRepository;
    private final ReaderMapper readerMapper;

    public List<ReaderListResponse> findAll() {
        return readerMapper.toListResponse(readerRepository.findAll());
    }

    public ReaderInfoResponse findById(Long readerId) {
        ReaderEntity reader = readerRepository.findById(readerId)
                .orElseThrow(() -> new BookshelfsException(ErrorType.NOT_FOUND,
                        "Пользователь не найден с id: " + readerId));
        return readerMapper.toInfoResponse(reader, bookshelfService.findAll(readerId));
    }

    public String updateById(Long id, ReaderUpdateRequest newReader) {
        ReaderEntity readerEntity = readerRepository.findById(id).orElseThrow(() -> new BookshelfsException(ErrorType.NOT_FOUND,
                "Пользователь не найден с id: " + id));
        if (readerRepository.existsByUsername(newReader.getUsername())){
            throw new BookshelfsException(ErrorType.CLIENT_ERROR, "Логин уже занят");
        }
        if (!Objects.equals(newReader.getPassword(), newReader.getPasswordRepeated())){
            throw new BookshelfsException(ErrorType.CLIENT_ERROR, "Пароли должны совпадать!");
        }
        readerEntity.setUsername(newReader.getUsername());
        readerEntity.setPassword(passwordEncoder.encode(newReader.getPasswordRepeated()));
        readerEntity.setPublicName(newReader.getPublicName());
        readerRepository.save(readerEntity);
        return "Данные успешно изменены";
    }
}