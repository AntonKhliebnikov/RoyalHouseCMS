(function () {
    'use strict';

    document.addEventListener('DOMContentLoaded', function () {
        const form = document.getElementById('basic-form');
        const container = document.getElementById('basic-infographics-container');
        const template = document.getElementById('infographic-item-template');

        const bannerInput = document.getElementById('bannerImageInput');
        const bannerWrapper = document.getElementById('bannerPreviewWrapper');

        function updateCustomFileLabel(input) {
            if (!input) {
                return;
            }

            const customFile = input.closest('.custom-file');
            if (!customFile) {
                return;
            }

            const label = customFile.querySelector('.custom-file-label');
            if (!label) {
                return;
            }

            const initialLabel = input.dataset.initialLabel || 'Выбрать файл...';

            if (input.files && input.files.length > 0) {
                label.textContent = input.files[0].name;
            } else {
                label.textContent = initialLabel;
            }
        }

        function initBannerPreview() {
            if (!bannerInput || !bannerWrapper) {
                return;
            }

            updateCustomFileLabel(bannerInput);

            bannerInput.addEventListener('change', function () {
                updateCustomFileLabel(bannerInput);

                const file = bannerInput.files && bannerInput.files[0];
                if (!file) {
                    return;
                }

                const previewUrl = URL.createObjectURL(file);

                const placeholderText = document.getElementById('bannerPlaceholderText');
                if (placeholderText) {
                    placeholderText.remove();
                }

                let image = document.getElementById('bannerPreviewImage');
                if (!image) {
                    image = document.createElement('img');
                    image.id = 'bannerPreviewImage';
                    image.className = 'banner-preview-image';
                    image.alt = 'Главный баннер';
                    bannerWrapper.appendChild(image);
                }

                image.src = previewUrl;
                bannerWrapper.classList.remove('banner-placeholder');
            });
        }

        function renumberItems() {
            const items = container.querySelectorAll('.js-infographic-item');

            items.forEach(function (item, index) {
                const currentImagePathInput = item.querySelector('[data-field="currentImagePath"]');
                const sortOrderInput = item.querySelector('[data-field="sortOrder"]');
                const descriptionInput = item.querySelector('[data-field="description"]');
                const imageInput = item.querySelector('[data-field="image"]');
                const imageLabel = imageInput
                    ? imageInput.closest('.custom-file').querySelector('.custom-file-label')
                    : null;

                if (currentImagePathInput) {
                    currentImagePathInput.name = 'basicInfographics[' + index + '].currentImagePath';
                    currentImagePathInput.id = 'basicInfographics' + index + '.currentImagePath';
                }

                if (sortOrderInput) {
                    sortOrderInput.name = 'basicInfographics[' + index + '].sortOrder';
                    sortOrderInput.id = 'basicInfographics' + index + '.sortOrder';
                    sortOrderInput.value = index + 1;
                }

                if (descriptionInput) {
                    descriptionInput.name = 'basicInfographics[' + index + '].description';
                    descriptionInput.id = 'basicInfographics' + index + '.description';
                }

                if (imageInput) {
                    const imageId = 'basicInfographics' + index + '_image';
                    imageInput.name = 'basicInfographics[' + index + '].image';
                    imageInput.id = imageId;

                    if (imageLabel) {
                        imageLabel.setAttribute('for', imageId);
                    }

                    updateCustomFileLabel(imageInput);
                }
            });
        }

        function syncActionButtons() {
            const items = container.querySelectorAll('.js-infographic-item');

            items.forEach(function (item, index) {
                const button = item.querySelector('.js-infographic-action');
                const icon = button ? button.querySelector('i') : null;

                if (!button || !icon) {
                    return;
                }

                button.classList.remove('btn-danger', 'btn-outline-secondary', 'nb-action-add', 'nb-action-remove');
                icon.classList.remove('fa-plus', 'fa-trash');

                if (index === 0) {
                    button.dataset.action = 'add';
                    button.classList.add('btn-outline-secondary', 'nb-action-add');
                    icon.classList.add('fa-plus');
                } else {
                    button.dataset.action = 'remove';
                    button.classList.add('btn-danger', 'nb-action-remove');
                    icon.classList.add('fa-trash');
                }
            });
        }

        function addItem() {
            const wrapper = document.createElement('div');
            wrapper.innerHTML = template.innerHTML.trim();

            const newItem = wrapper.firstElementChild;
            container.appendChild(newItem);

            renumberItems();
            syncActionButtons();
        }

        function ensureAtLeastOneItem() {
            const items = container.querySelectorAll('.js-infographic-item');
            if (items.length === 0) {
                addItem();
            } else {
                renumberItems();
                syncActionButtons();
            }
        }

        if (container && form && template) {
            ensureAtLeastOneItem();

            container.addEventListener('click', function (event) {
                const actionButton = event.target.closest('.js-infographic-action');
                if (!actionButton) {
                    return;
                }

                const action = actionButton.dataset.action;

                if (action === 'add') {
                    addItem();
                    return;
                }

                if (action === 'remove') {
                    const item = actionButton.closest('.js-infographic-item');
                    if (item) {
                        item.remove();
                        renumberItems();
                        syncActionButtons();
                    }
                }
            });

            container.addEventListener('change', function (event) {
                const fileInput = event.target.closest('[data-field="image"]');
                if (!fileInput) {
                    return;
                }

                updateCustomFileLabel(fileInput);
            });

            form.addEventListener('submit', function () {
                renumberItems();
            });
        }

        initBannerPreview();
    });

})();
