(function () {
    'use strict';

    document.addEventListener('DOMContentLoaded', function () {
        var input = document.getElementById('panoramaImageInput');
        var wrapper = document.getElementById('panoramaPreviewWrapper');

        function updateCustomFileLabel(inputElement) {
            if (!inputElement) {
                return;
            }

            var customFile = inputElement.closest('.custom-file');
            if (!customFile) {
                return;
            }

            var label = customFile.querySelector('.custom-file-label');
            if (!label) {
                return;
            }

            var initialLabel = inputElement.dataset.initialLabel || 'Файл не выбран';

            if (inputElement.files && inputElement.files.length > 0) {
                label.textContent = inputElement.files[0].name;
            } else {
                label.textContent = initialLabel;
            }
        }

        if (!input || !wrapper) {
            return;
        }

        updateCustomFileLabel(input);

        input.addEventListener('change', function () {
            updateCustomFileLabel(input);

            var file = input.files && input.files[0];
            if (!file) {
                return;
            }

            var previewUrl = URL.createObjectURL(file);

            var placeholderText = document.getElementById('panoramaPlaceholderText');
            if (placeholderText) {
                placeholderText.remove();
            }

            var image = document.getElementById('panoramaPreviewImage');
            if (!image) {
                image = document.createElement('img');
                image.id = 'panoramaPreviewImage';
                image.className = 'banner-preview-image';
                image.alt = 'Панорама';
                wrapper.appendChild(image);
            }

            image.src = previewUrl;
            wrapper.classList.remove('banner-placeholder');
        });
    });
})();
