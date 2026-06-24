package com.royalhouse.cms.admin.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class TransactionalFileCleanupService {
    private final FileStorageService fileStorageService;

    public void deleteAfterCommit(String path) {
        Set<String> paths = normalizePaths(Collections.singleton(path));

        if (paths.isEmpty()) {
            return;
        }

        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            paths.forEach(this::safeDelete);
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                paths.forEach(TransactionalFileCleanupService.this::safeDelete);
            }
        });
    }

    public void deleteAfterCommit(Collection<String> paths) {
        Set<String> normalizedPaths = normalizePaths(paths);

        if (normalizedPaths.isEmpty()) {
            return;
        }

        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            normalizedPaths.forEach(this::safeDelete);
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                normalizedPaths.forEach(TransactionalFileCleanupService.this::safeDelete);
            }
        });
    }

    public void deleteAfterRollback(String path) {
        Set<String> paths = normalizePaths(Collections.singleton(path));

        if (paths.isEmpty()) {
            return;
        }

        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if (status == STATUS_ROLLED_BACK) {
                    paths.forEach(TransactionalFileCleanupService.this::safeDelete);
                }
            }
        });
    }

    public void deleteAfterRollback(Collection<String> paths) {
        Set<String> normalizedPaths = normalizePaths(paths);

        if (normalizedPaths.isEmpty()) {
            return;
        }

        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if (status == STATUS_ROLLED_BACK) {
                    normalizedPaths.forEach(TransactionalFileCleanupService.this::safeDelete);
                }
            }
        });
    }

    private Set<String> normalizePaths(Collection<String> paths) {
        if (paths == null || paths.isEmpty()) {
            return Collections.emptySet();
        }

        return paths.stream()
                .filter(StringUtils::hasText)
                .map(String::trim)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private void safeDelete(String path) {
        try {
            fileStorageService.delete(path);
        } catch (Exception e) {
            log.warn("Не удалось удалить файл: {}", path, e);
        }
    }
}
