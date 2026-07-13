(function () {
    'use strict';

    document.addEventListener('DOMContentLoaded', function () {
        var form = document.getElementById('infrastructure-form');
        var container = document.getElementById('infrastructure-infographics-container');
        var template = document.getElementById('infrastructure-infographic-item-template');

        var slideConfigs = [
            {
                inputId: 'infrastructureSlide1ImageInput',
                wrapperId: 'infrastructureSlide1PreviewWrapper',
                imageId: 'infrastructureSlide1PreviewImage',
                placeholderId: 'infrastructureSlide1PlaceholderText'
            },
            {
                inputId: 'infrastructureSlide2ImageInput',
                wrapperId: 'infrastructureSlide2PreviewWrapper',
                imageId: 'infrastructureSlide2PreviewImage',
                placeholderId: 'infrastructureSlide2PlaceholderText'
            },
            {
                inputId: 'infrastructureSlide3ImageInput',
                wrapperId: 'infrastructureSlide3PreviewWrapper',
                imageId: 'infrastructureSlide3PreviewImage',
                placeholderId: 'infrastructureSlide3PlaceholderText'
            }
        ];

        function updateCustomFileLabel(input) {
            if (!input) {
                return;
            }

            var customFile = input.closest('.custom-file');
            if (!customFile) {
                return;
            }

            var label = customFile.querySelector('.custom-file-label');
            if (!label) {
                return;
            }

            var initialLabel = input.dataset.initialLabel || 'Выбрать файл...';

            if (input.files && input.files.length > 0) {
                label.textContent = input.files[0].name;
            } else {
                label.textContent = initialLabel;
            }
        }

        function initSlidePreview(config) {
            var input = document.getElementById(config.inputId);
            if (!input) {
                return;
            }

            updateCustomFileLabel(input);

            input.addEventListener('change', function () {
                updateCustomFileLabel(input);

                var file = input.files && input.files[0];
                if (!file) {
                    return;
                }

                var wrapper = document.getElementById(config.wrapperId);
                if (!wrapper) {
                    return;
                }

                var previewUrl = URL.createObjectURL(file);

                var placeholder = document.getElementById(config.placeholderId);
                if (placeholder) {
                    placeholder.remove();
                }

                var image = document.getElementById(config.imageId);
                if (!image) {
                    image = document.createElement('img');
                    image.id = config.imageId;
                    image.className = 'about-slide-preview-image';
                    image.alt = 'Слайд';
                    wrapper.appendChild(image);
                }

                image.src = previewUrl;
                wrapper.classList.remove('about-slide-placeholder');
            });
        }

        function renumberItems() {
            var items = container.querySelectorAll('.js-infrastructure-infographic-item');

            items.forEach(function (item, index) {
                var currentImagePathInput = item.querySelector('[data-field="currentImagePath"]');
                var sortOrderInput = item.querySelector('[data-field="sortOrder"]');
                var descriptionInput = item.querySelector('[data-field="description"]');
                var imageInput = item.querySelector('[data-field="image"]');
                var imageLabel = imageInput
                    ? imageInput.closest('.custom-file').querySelector('.custom-file-label')
                    : null;

                if (currentImagePathInput) {
                    currentImagePathInput.name = 'infrastructureInfographics[' + index + '].currentImagePath';
                    currentImagePathInput.id = 'infrastructureInfographics' + index + '.currentImagePath';
                }

                if (sortOrderInput) {
                    sortOrderInput.name = 'infrastructureInfographics[' + index + '].sortOrder';
                    sortOrderInput.id = 'infrastructureInfographics' + index + '.sortOrder';
                    sortOrderInput.value = index + 1;
                }

                if (descriptionInput) {
                    descriptionInput.name = 'infrastructureInfographics[' + index + '].description';
                    descriptionInput.id = 'infrastructureInfographics' + index + '.description';
                }

                if (imageInput) {
                    var imageId = 'infrastructureInfographics' + index + '_image';
                    imageInput.name = 'infrastructureInfographics[' + index + '].image';
                    imageInput.id = imageId;

                    if (imageLabel) {
                        imageLabel.setAttribute('for', imageId);
                    }

                    updateCustomFileLabel(imageInput);
                }
            });
        }

        function syncActionButtons() {
            var items = container.querySelectorAll('.js-infrastructure-infographic-item');

            items.forEach(function (item, index) {
                var button = item.querySelector('.js-infrastructure-infographic-action');
                var icon = button ? button.querySelector('i') : null;

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
            var wrapper = document.createElement('div');
            wrapper.innerHTML = template.innerHTML.trim();

            var newItem = wrapper.firstElementChild;
            container.appendChild(newItem);

            renumberItems();
            syncActionButtons();
        }

        function ensureAtLeastOneItem() {
            var items = container.querySelectorAll('.js-infrastructure-infographic-item');

            if (items.length === 0) {
                addItem();
            } else {
                renumberItems();
                syncActionButtons();
            }
        }

        slideConfigs.forEach(initSlidePreview);

        if (container && form && template) {
            ensureAtLeastOneItem();

            container.addEventListener('click', function (event) {
                var actionButton = event.target.closest('.js-infrastructure-infographic-action');
                if (!actionButton) {
                    return;
                }

                var action = actionButton.dataset.action;

                if (action === 'add') {
                    addItem();
                    return;
                }

                if (action === 'remove') {
                    var item = actionButton.closest('.js-infrastructure-infographic-item');
                    if (item) {
                        item.remove();
                        renumberItems();
                        syncActionButtons();
                    }
                }
            });

            container.addEventListener('change', function (event) {
                var fileInput = event.target.closest('[data-field="image"]');
                if (!fileInput) {
                    return;
                }

                updateCustomFileLabel(fileInput);
            });

            form.addEventListener('submit', function () {
                renumberItems();
            });
        }
    });
})();
