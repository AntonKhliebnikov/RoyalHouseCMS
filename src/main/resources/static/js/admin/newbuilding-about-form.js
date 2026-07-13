(function () {
    'use strict';

    document.addEventListener('DOMContentLoaded', function () {
        var inputConfigs = [
            {
                inputId: 'slide1ImageInput',
                wrapperId: 'slide1PreviewWrapper',
                imageId: 'slide1PreviewImage',
                placeholderId: 'slide1PlaceholderText'
            },
            {
                inputId: 'slide2ImageInput',
                wrapperId: 'slide2PreviewWrapper',
                imageId: 'slide2PreviewImage',
                placeholderId: 'slide2PlaceholderText'
            },
            {
                inputId: 'slide3ImageInput',
                wrapperId: 'slide3PreviewWrapper',
                imageId: 'slide3PreviewImage',
                placeholderId: 'slide3PlaceholderText'
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

            var initialLabel = input.dataset.initialLabel || 'Файл не выбран';

            if (input.files && input.files.length > 0) {
                label.textContent = input.files[0].name;
            } else {
                label.textContent = initialLabel;
            }
        }

        inputConfigs.forEach(function (config) {
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
        });
    });
})();
