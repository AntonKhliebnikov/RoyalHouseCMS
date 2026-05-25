document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('secondary-market-form');
    const container = document.getElementById('secondary-market-slides-container');
    const addButton = document.getElementById('add-secondary-market-slide-btn');
    const template = document.getElementById('secondary-market-slide-template');
    const emptyState = document.getElementById('secondary-market-slides-empty-state');

    if (!form || !container || !template) {
        return;
    }

    function syncEmptyState() {
        const visibleRows = container.querySelectorAll('.secondary-market-slide-row:not(.d-none)');

        if (emptyState) {
            emptyState.style.display = visibleRows.length === 0 ? '' : 'none';
        }
    }

    function hasPersistedId(row) {
        const idInput = row.querySelector('[data-field="id"]');
        return idInput && idInput.value && idInput.value.trim() !== '';
    }

    function wasPersistedAsActive(row) {
        return row.dataset.initialActive === 'true';
    }

    function canShowDeleteButton(row, isActive) {
        if (isActive) {
            return false;
        }

        if (!hasPersistedId(row)) {
            return true;
        }

        return !wasPersistedAsActive(row);
    }

    function updateRowVisualState(row) {
        const isActiveInput = row.querySelector('[data-field="isActive"]');
        const badge = row.querySelector('.slide-status-badge');
        const toggleBtn = row.querySelector('.slide-toggle-btn');
        const toggleIcon = toggleBtn ? toggleBtn.querySelector('i') : null;
        const deleteBtn = row.querySelector('.slide-delete-btn');

        if (!isActiveInput) {
            return;
        }

        const isActive = isActiveInput.value === 'true';

        if (badge) {
            badge.classList.remove('badge-success', 'badge-secondary');
            badge.classList.add(isActive ? 'badge-success' : 'badge-secondary');
            badge.textContent = isActive ? 'Активен' : 'Отключен';
        }

        if (toggleIcon) {
            toggleIcon.classList.remove('fa-plus', 'fa-minus');
            toggleIcon.classList.add(isActive ? 'fa-minus' : 'fa-plus');
        }

        if (deleteBtn) {
            deleteBtn.style.display = canShowDeleteButton(row, isActive) ? '' : 'none';
        }
    }

    function renumberRows() {
        const rows = container.querySelectorAll('.secondary-market-slide-row');

        rows.forEach(function (row, index) {
            const idInput = row.querySelector('[data-field="id"]');
            const imagePathInput = row.querySelector('[data-field="imagePath"]');
            const sortOrderInput = row.querySelector('[data-field="sortOrder"]');
            const isActiveInput = row.querySelector('[data-field="isActive"]');
            const markedForDeleteInput = row.querySelector('[data-field="markedForDelete"]');
            const imageInput = row.querySelector('[data-field="image"]');
            const textInput = row.querySelector('[data-field="text"]');
            const linkUrlInput = row.querySelector('[data-field="linkUrl"]');
            const slideNumber = row.querySelector('.slide-number');

            if (slideNumber) {
                slideNumber.textContent = index + 1;
            }

            if (idInput) {
                idInput.name = 'slides[' + index + '].id';
                idInput.id = 'slides' + index + '.id';
            }

            if (imagePathInput) {
                imagePathInput.name = 'slides[' + index + '].imagePath';
                imagePathInput.id = 'slides' + index + '.imagePath';
            }

            if (sortOrderInput) {
                sortOrderInput.name = 'slides[' + index + '].sortOrder';
                sortOrderInput.id = 'slides' + index + '.sortOrder';
                sortOrderInput.value = index;
            }

            if (isActiveInput) {
                isActiveInput.name = 'slides[' + index + '].isActive';
                isActiveInput.id = 'slides' + index + '.isActive';
            }

            if (markedForDeleteInput) {
                markedForDeleteInput.name = 'slides[' + index + '].markedForDelete';
                markedForDeleteInput.id = 'slides' + index + '.markedForDelete';
            }

            if (imageInput) {
                imageInput.name = 'slides[' + index + '].image';
                imageInput.id = 'slides' + index + '_image';

                const customFile = imageInput.closest('.custom-file');
                const label = customFile ? customFile.querySelector('.custom-file-label') : null;

                if (label) {
                    label.setAttribute('for', imageInput.id);
                }
            }

            if (textInput) {
                textInput.name = 'slides[' + index + '].text';
                textInput.id = 'slides' + index + '.text';
            }

            if (linkUrlInput) {
                linkUrlInput.name = 'slides[' + index + '].linkUrl';
                linkUrlInput.id = 'slides' + index + '.linkUrl';
            }
        });
    }

    function addSlideRow() {
        const wrapper = document.createElement('div');
        wrapper.innerHTML = template.innerHTML.trim();

        const newRow = wrapper.firstElementChild;
        container.appendChild(newRow);

        renumberRows();
        updateRowVisualState(newRow);
        syncEmptyState();
    }

    function updateCustomFileLabel(input) {
        const customFile = input.closest('.custom-file');

        if (!customFile) {
            return;
        }

        const label = customFile.querySelector('.custom-file-label');

        if (!label) {
            return;
        }

        label.textContent = input.files && input.files.length > 0
            ? input.files[0].name
            : 'Выберите файл';
    }

    function updateImagePreview(input) {
        const file = input.files && input.files[0];

        if (!file) {
            return;
        }

        const row = input.closest('.secondary-market-slide-row');
        const preview = row.querySelector('.slide-preview-image');
        const placeholder = row.querySelector('.slide-preview-placeholder');

        if (!preview) {
            return;
        }

        const reader = new FileReader();

        reader.onload = function (event) {
            preview.src = event.target.result;
            preview.classList.remove('d-none');

            if (placeholder) {
                placeholder.classList.add('d-none');
            }
        };

        reader.readAsDataURL(file);
    }

    if (addButton) {
        addButton.addEventListener('click', function () {
            addSlideRow();
        });
    }

    container.addEventListener('click', function (event) {
        const toggleBtn = event.target.closest('.slide-toggle-btn');

        if (toggleBtn) {
            const row = toggleBtn.closest('.secondary-market-slide-row');
            const isActiveInput = row.querySelector('[data-field="isActive"]');

            if (!isActiveInput) {
                return;
            }

            const isActive = isActiveInput.value === 'true';
            isActiveInput.value = isActive ? 'false' : 'true';

            updateRowVisualState(row);
            return;
        }

        const deleteBtn = event.target.closest('.slide-delete-btn');

        if (deleteBtn) {
            const row = deleteBtn.closest('.secondary-market-slide-row');
            const isActiveInput = row.querySelector('[data-field="isActive"]');
            const markedForDeleteInput = row.querySelector('[data-field="markedForDelete"]');

            const isActive = isActiveInput && isActiveInput.value === 'true';

            if (isActive) {
                return;
            }

            if (hasPersistedId(row) && wasPersistedAsActive(row)) {
                return;
            }

            if (!hasPersistedId(row)) {
                row.remove();
            } else {
                markedForDeleteInput.value = 'true';
                row.classList.add('d-none');
            }

            renumberRows();
            syncEmptyState();
        }
    });

    container.addEventListener('change', function (event) {
        const imageInput = event.target.closest('[data-field="image"]');

        if (imageInput) {
            updateCustomFileLabel(imageInput);
            updateImagePreview(imageInput);
        }
    });

    const existingRows = container.querySelectorAll('.secondary-market-slide-row');

    existingRows.forEach(function (row) {
        updateRowVisualState(row);
    });

    renumberRows();
    syncEmptyState();

    form.addEventListener('submit', function () {
        renumberRows();
    });
});